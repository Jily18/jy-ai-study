package com.jy.study.lesson.mapper;
import com.jy.study.lesson.domain.StudyArticle;
import java.util.List;

public interface StudyArticleMapper {
    public StudyArticle selectArticleById(Long articleId);

    public List<StudyArticle> selectArticleList(StudyArticle article);

    public int insertArticle(StudyArticle article);

    public int updateArticle(StudyArticle article);

    public int deleteArticleById(Long articleId);

    public int deleteArticleByIds(String[] articleIds);
} 