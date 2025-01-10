package com.jy.study.web.controller.art;

import com.jy.study.common.annotation.Log;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.common.core.page.TableDataInfo;
import com.jy.study.common.enums.BusinessType;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.service.IStudyArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/article")
public class ArtController extends BaseController {
    
    private String prefix = "article";
    
    @Autowired
    private IStudyArticleService articleService;

    @GetMapping("/list")
    public String list() {
        return prefix + "/art-list";
    }

    @PostMapping("/listData")
    @ResponseBody
    public TableDataInfo listData(StudyArticle article) {
        startPage();
        List<StudyArticle> list = articleService.selectArticleList(article);
        return getDataTable(list);
    }

    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    @Log(title = "文章管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated StudyArticle article) {
        article.setCreateBy(getLoginName());
        return toAjax(articleService.insertArticle(article));
    }

    @GetMapping("/edit/{articleId}")
    public String edit(@PathVariable("articleId") Long articleId, ModelMap mmap) {
        mmap.put("article", articleService.selectArticleById(articleId));
        return prefix + "/edit";
    }

    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated StudyArticle article) {
        article.setUpdateBy(getLoginName());
        return toAjax(articleService.updateArticle(article));
    }

    @Log(title = "文章管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(articleService.deleteArticleByIds(ids));
    }

    @GetMapping("/detail/{articleId}")
    public String detail(@PathVariable("articleId") Long articleId, ModelMap mmap) {
        mmap.put("article", articleService.selectArticleById(articleId));
        return prefix + "/detail";
    }
}
