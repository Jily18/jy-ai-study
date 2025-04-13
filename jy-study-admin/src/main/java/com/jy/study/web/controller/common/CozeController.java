package com.jy.study.web.controller.common;

import com.jy.study.common.ai.CozeWorkFlow;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.domain.StudyAiCoze;
import com.jy.study.lesson.service.ArticleQuestionService;
import com.jy.study.lesson.service.IStudyArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//在这个controller里面调用coze的接口
@Controller
@RequestMapping("/coze")
public class CozeController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(CozeController.class);
    
    @Autowired
    private ArticleQuestionService articleQuestionService;
    
    @Autowired
    private IStudyArticleService articleService;

    /**
     * 生成试题
     */
    @PostMapping("/generate/questions/{articleId}")
    @ResponseBody
    public AjaxResult generateQuestions(@PathVariable Long articleId,
            Integer xuanze, Integer tiankong, Integer panduan, Integer jianda) {
        try {
            // 1. 获取文章内容
            StudyArticle article = articleService.selectArticleById(articleId);
            if (article == null) {
                return AjaxResult.error("文章不存在");
            }
            
            // 2. 生成题目并保存
            StudyAiCoze result = articleQuestionService.generateQuestions(
                articleId, xuanze, tiankong, panduan, jianda, article.getContent());
            return AjaxResult.success("生成成功", result);
        } catch (Exception e) {
            log.error("生成试题异常", e);
            return AjaxResult.error("生成失败：" + e.getMessage());
        }
    }
}
