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
    
    public StudyAiCoze generateQuestions(Long articleId, Integer xuanze, Integer tiankong,
                                         Integer panduan, Integer jianda, String content) {
        // 1. 准备参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("xuanze", xuanze);
        parameters.put("tiankong", tiankong);
        parameters.put("panduan", panduan);
        parameters.put("jianda", jianda);
        parameters.put("File", content);
        
        // 2. 调用AI生成题目
        CozeResponse response = cozeWorkFlow.runWorkflow(parameters);
        
        // 3. 保存记录
        StudyAiCoze aiCoze = new StudyAiCoze();
        aiCoze.setArticleId(articleId);
        aiCoze.setQuestionNum(xuanze + tiankong + panduan + jianda);
        aiCoze.setContent(response.getOutput());
        aiCoze.setFileUrl(response.getFileUrl());
        aiCoze.setDebugUrl(response.getDebugUrl());
        aiCoze.setStatus("1"); // 1-已完成
        
        // 保存AI试题记录
        aiCozeService.insertAiCoze(aiCoze);
        
        // 4. 更新文章的cozeId
        articleService.updateArticleCozeId(articleId, aiCoze.getId());
        
        return aiCoze;
    }
} 