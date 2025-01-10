package com.jy.study.web.controller.web;

import com.jy.study.common.core.controller.BaseController;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.service.IStudyArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 前台首页控制器
 */
@Controller
@RequestMapping("/web")
public class WebController extends BaseController {

    @Autowired
    private IStudyArticleService articleService;

    /**
     * 前台首页 - 根路径访问
     */
    @GetMapping("")
    public String root(ModelMap mmap) {
        return index(mmap);
    }

    /**
     * 前台首页 - /web/index路径访问
     */
    @GetMapping("/index")
    public String index(ModelMap mmap) {
        StudyArticle article = new StudyArticle();
        article.setStatus("0"); // 只查询正常状态的文章
        List<StudyArticle> articles = articleService.selectArticleList(article);
        mmap.put("articles", articles);
        return "web/index";
    }
}
