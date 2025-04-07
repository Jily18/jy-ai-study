package com.jy.study.web.controller.common;

import com.jy.study.common.ai.SiliconCloudAI;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.service.IStudyArticleService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/llm")
public class LLMController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(LLMController.class);

    @Autowired
    private IStudyArticleService articleService;

    @Autowired
    private SiliconCloudAI siliconCloudAI;

    public static Map<Long,List<String>> cacheData = new ConcurrentHashMap<>();

    @GetMapping("/analyze/{articleId}")
    @ResponseBody
    public SseEmitter analyzeArticleStream(@PathVariable("articleId") Long articleId) {
        // 设置超时时间为5分钟
        SseEmitter emitter = new SseEmitter(300000L);
        // 在主线程中获取用户信息
        final Long userId = getSysUser().getUserId();
        
        try{
            // 设置响应头
            emitter.send(SseEmitter.event().data("连接成功").build());
        }catch (Exception e){
             log.error("发送消息失败", e);
        }

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
                                emitter.send(SseEmitter.event().data(msg).build());
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
}
