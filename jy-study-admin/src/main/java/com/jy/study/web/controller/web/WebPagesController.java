package com.jy.study.web.controller.web;

import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.entity.SysUser;
import com.jy.study.lesson.domain.StudyAiCoze;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.domain.StudyLesson;
import com.jy.study.lesson.service.IStudyAiCozeService;
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
import java.util.Map;

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
    @Autowired
    private IStudyAiCozeService aiCozeService;

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
        // 获取试题预览
        if (article.getCozeId() != null) {
            StudyAiCoze aiCoze = aiCozeService.selectAiCozeById(article.getCozeId());
            if (aiCoze != null) {
                mmap.put("aiCoze", aiCoze);
                // 获取内容预览（前100个字符）
                String previewContent = getPreviewContent(aiCoze.getContent(), 100);
                mmap.put("previewContent", previewContent);
            }
        }

        mmap.put("article", article);
        return "web/article";
    }

    /**
     * 获取内容预览
     */
    private String getPreviewContent(String content, int length) {
        if (content == null) {
            return "";
        }
        // 去除多余的空行
        content = content.replaceAll("\\n+", "\n").trim();
        if (content.length() <= length) {
            return content;
        }
        return content.substring(0, length) + "...";
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


    /**
     * 课程列表页
     */
    @GetMapping("/lessons")
    public String lessons(ModelMap mmap) {
        SysUser user = getSysUser();
        mmap.put("user", user);

        // 获取课程列表
        StudyLesson lesson = new StudyLesson();
        lesson.setStatus("0"); // 只查询正常状态的课程
        List<StudyLesson> lessons = lessonService.selectStudyLessonList(lesson);
        mmap.put("lessons", lessons);

        return "web/lessons";
    }

    /**
     * 文章列表页
     */
    @GetMapping("/articles")
    public String articles(ModelMap mmap) {
        SysUser user = getSysUser();
        mmap.put("user", user);

        // 获取文章列表
        StudyArticle article = new StudyArticle();
        article.setStatus("0"); // 只查询正常状态的文章
        List<StudyArticle> articles = articleService.selectArticleList(article);
        mmap.put("articles", articles);

        return "web/articles";
    }

    /**
     * 试题详情页
     */
    @GetMapping("/questions/{articleId}")
    public String questions(@PathVariable("articleId") Long articleId, ModelMap mmap) {
        // 1. 获取文章信息
        StudyArticle article = articleService.selectArticleById(articleId);
        if (article == null || !"0".equals(article.getStatus())) {
            return "error/404";
        }
        mmap.put("article", article);
        
        // 2. 获取试题信息
        if (article.getCozeId() == null) {
            return "error/404";
        }
        
        StudyAiCoze aiCoze = aiCozeService.selectAiCozeById(article.getCozeId());
        if (aiCoze == null) {
            return "error/404";
        }
        mmap.put("aiCoze", aiCoze);
        
        // 3. 格式化试题内容，将内容按题型分类
        String formattedContent = formatQuestionContent(aiCoze.getContent());
        mmap.put("formattedContent", formattedContent);
        
        return "web/question";
    }

    /**
     * 格式化试题内容，添加HTML样式
     */
    private String formatQuestionContent(String content) {
        if (content == null) {
            return "";
        }
        
        StringBuilder html = new StringBuilder();
        String[] sections = content.split("(?=一、|二、|三、|四、)");
        
        for (String section : sections) {
            if (section.trim().isEmpty()) {
                continue;
            }
            
            // 获取题型标题
            String title = section.substring(0, section.indexOf("、") + 1);
            String questions = section.substring(section.indexOf("、") + 1);
            
            // 创建题型区块
            html.append("<div class=\"question-section\">");
            html.append("<h3 class=\"section-title\">").append(title).append("</h3>");
            
            // 处理每道题目
            String[] questionItems = questions.split("(?=\\d+、)");
            for (String item : questionItems) {
                if (item.trim().isEmpty()) {
                    continue;
                }
                
                html.append("<div class=\"question-item\">");
                
                // 分离题目内容、答案和解析
                String questionText = "";
                String answer = "";
                String explanation = "";
                
                // 处理题目内容
                int answerIndex = item.indexOf("[答案]");
                int explanationIndex = item.indexOf("[解析]");
                
                if (answerIndex != -1) {
                    questionText = item.substring(0, answerIndex).trim();
                    if (explanationIndex != -1) {
                        answer = item.substring(answerIndex + 4, explanationIndex).trim();
                        explanation = item.substring(explanationIndex + 4).trim();
                    } else {
                        answer = item.substring(answerIndex + 4).trim();
                    }
                } else {
                    // 处理选择题格式
                    answerIndex = item.indexOf("[正确答案]");
                    if (answerIndex != -1) {
                        questionText = item.substring(0, answerIndex).trim();
                        if (explanationIndex != -1) {
                            answer = item.substring(answerIndex + 6, explanationIndex).trim();
                            explanation = item.substring(explanationIndex + 4).trim();
                        } else {
                            answer = item.substring(answerIndex + 6).trim();
                        }
                    } else {
                        questionText = item;
                    }
                }
                
                // 添加题目内容（保持换行）
                html.append("<div class=\"question-content\">");
                String[] lines = questionText.split("\n");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        html.append(line.trim()).append("<br>");
                    }
                }
                html.append("</div>");
                
                // 添加答案（如果存在）
                if (!answer.isEmpty()) {
                    html.append("<div class=\"question-answer\">");
                    html.append("<strong>答案：</strong>");
                    String[] answerLines = answer.split("\n");
                    for (String line : answerLines) {
                        if (!line.trim().isEmpty()) {
                            html.append(line.trim()).append("<br>");
                        }
                    }
                    html.append("</div>");
                }
                
                // 添加解析（如果存在）
                if (!explanation.isEmpty()) {
                    html.append("<div class=\"question-explanation\">");
                    html.append("<strong>解析：</strong>");
                    String[] explanationLines = explanation.split("\n");
                    for (String line : explanationLines) {
                        if (!line.trim().isEmpty()) {
                            html.append(line.trim()).append("<br>");
                        }
                    }
                    html.append("</div>");
                }
                
                html.append("</div>");
            }
            
            html.append("</div>");
        }
        
        return html.toString();
    }

} 