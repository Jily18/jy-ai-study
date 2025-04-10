package com.jy.study.web.controller.web;

import com.github.pagehelper.PageHelper;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.common.core.page.TableDataInfo;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.domain.StudyArticleCategory;
import com.jy.study.lesson.domain.StudyLesson;
import com.jy.study.lesson.domain.StudyLessonCategory;
import com.jy.study.lesson.service.IStudyArticleCategoryService;
import com.jy.study.lesson.service.IStudyArticleService;
import com.jy.study.lesson.service.IStudyLessonCategoryService;
import com.jy.study.lesson.service.IStudyLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台分类页面Controller
 */
@Controller
@RequestMapping("/web/category")
public class WebCategoryController extends BaseController {

    @Autowired
    private IStudyLessonCategoryService categoryService;

    @Autowired
    private IStudyLessonService lessonService;

    @Autowired
    private IStudyArticleService articleService;

    @Autowired
    private IStudyArticleCategoryService articleCategoryService;

    /**
     * 课程分类页面
     */
    @GetMapping("/lesson")
    public String lessonCategory(ModelMap mmap) {
        // 获取所有正常状态的分类
        StudyLessonCategory query = new StudyLessonCategory();
        query.setStatus("0"); // 正常状态
        List<StudyLessonCategory> categories = categoryService.selectStudyLessonCategoryList(query);
        mmap.put("categories", categories);

        // 获取第一页课程数据
        PageHelper.startPage(1, 12);
        List<StudyLesson> lessons = lessonService.selectStudyLessonList(new StudyLesson());
        TableDataInfo tableData = getDataTable(lessons);
        
        mmap.put("lessons", tableData.getRows());
        // 当前页码固定为1
        mmap.put("pageNum", 1);
        // 计算总页数
        long totalPages = (tableData.getTotal() + 11) / 12; // 12是每页大小，11是(12-1)
        mmap.put("totalPages", totalPages);

        return "/web/lesson-category";
    }

    /**
     * 获取课程列表数据
     */
    @PostMapping("/lessonList")
    @ResponseBody
    public AjaxResult lessonList(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String keyword) {
        
        PageHelper.startPage(pageNum, 12);

        StudyLesson query = new StudyLesson();
        if (categoryId != null && categoryId > 0) {
            query.setCategoryId(categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setTitle(keyword.trim());
        }

        List<StudyLesson> lessons = lessonService.selectStudyLessonList(query);
        TableDataInfo tableData = getDataTable(lessons);

        Map<String, Object> data = new HashMap<>();
        data.put("lessons", tableData.getRows());
        data.put("pageNum", pageNum);
        // 计算总页数
        long totalPages = (tableData.getTotal() + 11) / 12;
        data.put("totalPages", totalPages);

        return AjaxResult.success(data);
    }

    /**
     * 文章分类页面
     */
    @GetMapping("/article")
    public String articleCategory(ModelMap mmap) {
        // 获取所有正常状态的分类
        StudyArticleCategory query = new StudyArticleCategory();
        query.setStatus("0"); // 正常状态
        List<StudyArticleCategory> categories = articleCategoryService.selectStudyArticleCategoryList(query);
        mmap.put("categories", categories);

        // 获取第一页文章数据
        PageHelper.startPage(1, 12);
        List<StudyArticle> articles = articleService.selectArticleList(new StudyArticle());
        TableDataInfo tableData = getDataTable(articles);
        
        mmap.put("articles", tableData.getRows());
        mmap.put("pageNum", 1);
        long totalPages = (tableData.getTotal() + 11) / 12;
        mmap.put("totalPages", totalPages);

        return "web/article-category";
    }

    /**
     * 获取文章列表数据
     */
    @PostMapping("/articleList")
    @ResponseBody
    public AjaxResult articleList(@RequestParam(defaultValue = "1") Integer pageNum,
                      @RequestParam(required = false) Long categoryId,
                      @RequestParam(required = false) String keyword) {
        
        PageHelper.startPage(pageNum, 12);

        StudyArticle query = new StudyArticle();
        if (categoryId != null && categoryId > 0) {
            query.setCategoryId(categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setTitle(keyword.trim());
        }

        List<StudyArticle> articles = articleService.selectArticleList(query);
        TableDataInfo tableData = getDataTable(articles);

        Map<String, Object> data = new HashMap<>();
        data.put("articles", tableData.getRows());
        data.put("pageNum", pageNum);
        long totalPages = (tableData.getTotal() + 11) / 12;
        data.put("totalPages", totalPages);

        return AjaxResult.success(data);
    }
} 