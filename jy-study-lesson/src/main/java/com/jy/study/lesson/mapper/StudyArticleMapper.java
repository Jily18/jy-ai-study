package com.jy.study.lesson.mapper;
import com.jy.study.lesson.domain.StudyArticle;
import java.util.List;

public interface StudyArticleMapper {
    public StudyArticle selectArticleById(Long articleId);

    /**
     * 查询文章列表
     * 
     * @param article 文章信息
     * @return 文章集合
     */
    public List<StudyArticle> selectArticleList(StudyArticle article);

    public int insertArticle(StudyArticle article);

    public int updateArticle(StudyArticle article);

    public int deleteArticleById(Long articleId);

    public int deleteArticleByIds(String[] articleIds);

    public int incrementViewCount(Long articleId);

    public int incrementLikeCount(Long articleId);

    public int decrementLikeCount(Long articleId);

    public int incrementCollectCount(Long articleId);

    public int decrementCollectCount(Long articleId);
} 