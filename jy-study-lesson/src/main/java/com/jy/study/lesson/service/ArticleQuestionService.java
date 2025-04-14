package com.jy.study.lesson.service;

import com.jy.study.lesson.domain.StudyAiCoze;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.jy.study.common.ai.CozeWorkFlow;
import com.jy.study.common.ai.CozeResponse;

@Service
public class ArticleQuestionService {
    
    @Autowired
    private CozeWorkFlow cozeWorkFlow;
    
    @Autowired
    private IStudyAiCozeService aiCozeService;
    
    @Autowired
    private IStudyArticleService articleService;
    
    /**
     * 处理AI生成的试题内容，去除转义字符
     */
    private String formatQuestionContent(String content) {
        if (content == null) {
            return null;
        }
        return content.replace("\\[", "[")
                     .replace("\\]", "]")
                     .replace("\\.", ".")
                     .replace("\\、", "、")
                     .replace("\\(", "(")
                     .replace("\\)", ")")
                     .replace("\\；", "；")
                     .replace("\\：", "：")
                     .replace("\\，", "，")
                     .replace("\\。", "。")
                     .replace("\\？", "？")
                     .replace("\\！", "！")
                     .replace("\\、", "、");
    }

    public StudyAiCoze generateQuestions(Long articleId, Integer xuanze, Integer tiankong,
                                         Integer panduan, Integer jianda, String content) {
        // 1. 创建AI试题记录
        StudyAiCoze aiCoze = new StudyAiCoze();
        aiCoze.setArticleId(articleId);
        aiCoze.setQuestionNum(xuanze + tiankong + panduan + jianda);
        aiCoze.setStatus("0"); // 0-生成中
        
        // 保存初始记录
        aiCozeService.insertAiCoze(aiCoze);
        
        try {
            // 2. 准备参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("xuanze", xuanze);
            parameters.put("tiankong", tiankong);
            parameters.put("panduan", panduan);
            parameters.put("jianda", jianda);
            parameters.put("File", content);
            
            // 3. 调用AI生成题目
            CozeResponse response = cozeWorkFlow.runWorkflow(parameters);
            
            // 4. 更新AI试题记录
            // 处理content中的转义字符
            String formattedContent = formatQuestionContent(response.getOutput());
            aiCoze.setContent(formattedContent);
            aiCoze.setFileUrl(response.getFileUrl());
            aiCoze.setDebugUrl(response.getDebugUrl());
            aiCoze.setStatus("1"); // 1-已完成
            aiCozeService.updateAiCoze(aiCoze);
            
            // 5. 更新文章的cozeId
            articleService.updateArticleCozeId(articleId, aiCoze.getId());
            
            return aiCoze;
        } catch (Exception e) {
            // 6. 如果生成失败，更新状态
            aiCoze.setStatus("2"); // 2-失败
            aiCoze.setContent("生成失败：" + e.getMessage());
            aiCozeService.updateAiCoze(aiCoze);
            
            throw e;
        }
    }
} 