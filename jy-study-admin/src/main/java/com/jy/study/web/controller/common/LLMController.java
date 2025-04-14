package com.jy.study.web.controller.common;

import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.jy.study.common.ai.SiliconCloudAI;
import com.jy.study.common.ai.TongYiMultiRound;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.service.IStudyArticleService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.alibaba.dashscope.common.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/llm")
public class LLMController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(LLMController.class);

    @Autowired
    private IStudyArticleService articleService;

    @Autowired
    private SiliconCloudAI siliconCloudAI;

    @Autowired
    private TongYiMultiRound tongYiMultiRound;

    public static Map<Long,List<String>> cacheData = new ConcurrentHashMap<>();

    private Map<String, List<Message>> conversations = new ConcurrentHashMap<>();

    @GetMapping("/analyze/{articleId}")
    @ResponseBody
    public SseEmitter analyzeArticleStream(@PathVariable("articleId") Long articleId) {
        // 设置超时时间为5分钟
        SseEmitter emitter = new SseEmitter(300000L);
        // 在主线程中获取用户信息
        final Long userId = getSysUser().getUserId();

        try {
            // 验证用户是否登录
            if (userId == null) {
                emitter.send(SseEmitter.event().data("请先登录后再使用此功能").build());
                emitter.complete();
                return emitter;
            }
            
            // 获取文章内容
            StudyArticle article = articleService.selectArticleById(articleId);
            if(article == null) {
                emitter.send(SseEmitter.event().data("文章不存在").build());
                emitter.complete();
                return emitter;
            }

            String prompt = "请分析这篇文章的主要内容、写作特点和核心观点，并给出你的评价。";
            String articleContent = "文章标题：" + article.getTitle() + "\n\n" + article.getContent();
            
            // 异步处理AI响应
            new Thread(() -> {
                try {
                    List<String> result = siliconCloudAI.requestSSE("Qwen/Qwen2.5-14B-Instruct", prompt, articleContent);
                    if(result != null) {
                        cacheData.put(articleId, result);
                        for(String msg : result) {
                            if("DONE".equals(msg)) {
                                emitter.complete();
                            } else {
                                emitter.send(msg);
                                Thread.sleep(20);
                            }
                        }
                    } else {
                        emitter.send(SseEmitter.event().data("分析失败，请稍后重试").build());
                        emitter.complete();
                    }
                } catch (Exception e) {
                    try {
                        emitter.send(SseEmitter.event().data("分析过程发生错误").build());
                        emitter.complete();
                    } catch (IOException ex) {
                        log.warn("发送消息失败", ex);
                    }
                }
            }).start();

        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event().data("系统错误").build());
                emitter.complete();
            } catch (IOException ex) {
               log.warn("ai分析文章", ex);
            }
        }
        
        return emitter;
    }

    @GetMapping("/chat/stream")
    public SseEmitter chatStream(String message, String conversationId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时
        
        try {
            // 获取或创建会话历史
            List<Message> messages = conversations.computeIfAbsent(
                conversationId == null ? UUID.randomUUID().toString() : conversationId,
                k -> {
                    List<Message> newMessages = new ArrayList<>();
                    newMessages.add(tongYiMultiRound.createSystemMessage());
                    return newMessages;
                }
            );
            
            // 添加用户消息
            messages.add(tongYiMultiRound.createUserMessage(message));
            
            // 创建生成参数
            GenerationParam param = tongYiMultiRound.createStreamGenerationParam(messages);
            
            // 异步处理流式响应
            new Thread(() -> {
                try {
                    // 使用信号量控制流程
                    Semaphore semaphore = new Semaphore(0);
                    StringBuilder fullContent = new StringBuilder();
                    
                    // 调用流式API
                    tongYiMultiRound.streamCall(param, new ResultCallback<GenerationResult>() {
                        @Override
                        public void onEvent(GenerationResult message) {
                            try {
                                String content = message.getOutput().getChoices().get(0).getMessage().getContent();
                                fullContent.append(content);
                                emitter.send(content);
                            } catch (IOException e) {
                                log.error("发送流式消息失败", e);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            log.error("流式对话出错", e);
                            semaphore.release();
                        }

                        @Override
                        public void onComplete() {
                            try {
                                // 保存助手回复到会话历史
                                messages.add(tongYiMultiRound.createAssistantMessage(fullContent.toString()));
                                emitter.complete();
                            } catch (Exception e) {
                                log.error("完成流式对话失败", e);
                            }
                            semaphore.release();
                        }
                    });
                    
                    // 等待完成
                    semaphore.acquire();
                    
                } catch (Exception e) {
                    log.error("处理流式对话失败", e);
                    try {
                        emitter.send(SseEmitter.event().data("处理失败，请稍后重试").build());
                        emitter.complete();
                    } catch (IOException ex) {
                        log.error("发送错误消息失败", ex);
                    }
                }
            }).start();
            
        } catch (Exception e) {
            log.error("创建流式对话失败", e);
            emitter.complete();
        }
        
        return emitter;
    }

    @GetMapping("/markdownToHtml2")
    @ResponseBody
    public String markdownToHtml2(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }

        Parser parser = Parser.builder().build();
        Node document = parser.parse(content);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    @GetMapping("/markdownToHtml")
    @ResponseBody
    public String markdownToHtml(Long id) {
        List<String> list = cacheData.get(id);
        if(list == null) {
            return "";
        }
        
        StringBuilder markdown = new StringBuilder();
        for(String str : list) {
            if(!"DONE".equals(str)) {
                markdown.append(str);
            }
        }
        cacheData.remove(id);
        
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown.toString());
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    @PostMapping("/chat")
    @ResponseBody
    public AjaxResult chat(@RequestBody Map<String, String> params) {
        String message = params.get("message");
        String conversationId = params.get("conversationId");
        
        try {
            // 获取或创建会话历史
            List<Message> messages = conversations.computeIfAbsent(
                conversationId == null ? UUID.randomUUID().toString() : conversationId,
                k -> {
                    List<Message> newMessages = new ArrayList<>();
                    newMessages.add(tongYiMultiRound.createSystemMessage());
                    return newMessages;
                }
            );
            
            // 添加用户消息
            messages.add(tongYiMultiRound.createUserMessage(message));
                
            // 创建生成参数
            GenerationParam param = tongYiMultiRound.createGenerationParam(messages);
            
            // 调用模型获取响应
            GenerationResult result = tongYiMultiRound.callGenerationWithMessages(param);
            
            if (result == null || result.getOutput() == null || result.getOutput().getChoices() == null 
                || result.getOutput().getChoices().isEmpty()) {
                return AjaxResult.error("AI响应异常");
            }
            
            // 获取助手回复
            Message assistantMessage = result.getOutput().getChoices().get(0).getMessage();
            messages.add(assistantMessage);
            
            // 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("content", assistantMessage.getContent());
            data.put("conversationId", conversationId == null ? UUID.randomUUID().toString() : conversationId);
            
            return AjaxResult.success(data);
            
        } catch (Exception e) {
            log.error("AI对话出错", e);
            return AjaxResult.error("AI对话服务出现错误: " + e.getMessage());
        }
    }

    // 可选：添加清理超时会话的方法
//    @Scheduled(fixedRate = 3600000) // 每小时执行一次
//    public void cleanupOldConversations() {
//        // 清理3小时前的会话
//        long cutoffTime = System.currentTimeMillis() - 3 * 3600000;
//        conversations.entrySet().removeIf(entry ->
//            entry.getValue().get(entry.getValue().size() - 1).getTimestamp() < cutoffTime);
//    }
}
