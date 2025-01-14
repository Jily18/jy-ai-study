package com.jy.study.lesson.domain;

import com.jy.study.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class StudyArticle extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long articleId;

    @NotBlank(message = "文章标题不能为空")
    @Size(min = 0, max = 100, message = "文章标题不能超过100个字符")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String status;  // 0正常 1停用

    private Integer sort;

    private String remark;

    @Size(min = 0, max = 50, message = "文章分类长度不能超过50个字符")
    private String category;
    
    @Size(min = 0, max = 255, message = "文章标签长度不能超过255个字符")
    private String tags;
    
    @Size(min = 0, max = 500, message = "文章摘要长度不能超过500个字符")
    private String summary;
    
    private Long viewCount;
    
    private Long likeCount;
    
    private Long collectCount;
    
    private String top;

    // getter和setter方法
    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Long collectCount) {
        this.collectCount = collectCount;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("articleId", getArticleId())
                .append("title", getTitle())
                .append("content", getContent())
                .append("status", getStatus())
                .append("sort", getSort())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("category", getCategory())
                .append("tags", getTags())
                .append("summary", getSummary())
                .append("viewCount", getViewCount())
                .append("likeCount", getLikeCount())
                .append("collectCount", getCollectCount())
                .append("top", getTop())
                .toString();
    }
}