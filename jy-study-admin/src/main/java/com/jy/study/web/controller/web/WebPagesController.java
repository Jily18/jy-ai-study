package com.jy.study.web.controller.web;

import com.jy.study.common.core.controller.BaseController;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.domain.StudyLesson;
import com.jy.study.lesson.service.IStudyArticleService;
import com.jy.study.lesson.service.IStudyLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 前台页面功能控制器
 */
@Controller
@RequestMapping("/web")
public class WebPagesController extends BaseController {
    
    @Autowired
    private IStudyArticleService articleService;
    @Autowired
    private IStudyLessonService lessonService;
    /**
     * 文章详情页
     */
    @GetMapping("/article/{articleId}")
    public String article(@PathVariable("articleId") Long articleId, ModelMap mmap) {
        StudyArticle article = articleService.selectArticleById(articleId);
        if (article == null) {
            return "error/404";
        }
        // 只允许查看正常状态的文章
        if (!"0".equals(article.getStatus())) {
            return "error/404";
        }
        mmap.put("article", article);
        return "web/article";
    }

    /**
     * 搜索页面
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, ModelMap mmap) {
        StudyArticle article = new StudyArticle();
        article.setStatus("0");
        if (keyword != null && !keyword.trim().isEmpty()) {
            article.setTitle(keyword);
        }
        List<StudyArticle> articles = articleService.selectArticleList(article);
        mmap.put("articles", articles);
        mmap.put("keyword", keyword);
        return "web/search";
    }

    /**
     * 课程分类页
     */
    @GetMapping("/category")
    public String category(ModelMap mmap) {
        return "web/category";
    }

    /**
     * 精选推荐页
     */
    @GetMapping("/featured")
    public String featured(ModelMap mmap) {
        StudyArticle article = new StudyArticle();
        article.setStatus("0");
        List<StudyArticle> articles = articleService.selectArticleList(article);
        mmap.put("articles", articles);
        return "web/featured";
    }

    /**
     * 课程详情页
     */
    @GetMapping("/lesson/{lessonId}")
    public String lesson(@PathVariable("lessonId") Long lessonId, ModelMap mmap) {
        StudyLesson lesson = lessonService.selectStudyLessonByLessonId(lessonId);
        if (lesson == null || !"0".equals(lesson.getStatus())) {
            return "error/404";
        }
        mmap.put("lesson", lesson);
        
        return "web/lesson";
    }
} 