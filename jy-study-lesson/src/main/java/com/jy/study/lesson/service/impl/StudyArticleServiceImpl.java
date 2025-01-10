package com.jy.study.lesson.service.impl;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.mapper.StudyArticleMapper;
import com.jy.study.lesson.service.IStudyArticleService;

import com.jy.study.common.core.text.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyArticleServiceImpl implements IStudyArticleService {
    @Autowired
    private StudyArticleMapper articleMapper;

    @Override
    public StudyArticle selectArticleById(Long articleId) {
        return articleMapper.selectArticleById(articleId);
    }

    @Override
    public List<StudyArticle> selectArticleList(StudyArticle article) {
        return articleMapper.selectArticleList(article);
    }

    @Override
    public int insertArticle(StudyArticle article) {
        return articleMapper.insertArticle(article);
    }

    @Override
    public int updateArticle(StudyArticle article) {
        return articleMapper.updateArticle(article);
    }

    @Override
    public int deleteArticleById(Long articleId) {
        return articleMapper.deleteArticleById(articleId);
    }

    @Override
    public int deleteArticleByIds(String ids) {
        return articleMapper.deleteArticleByIds(Convert.toStrArray(ids));
    }
}