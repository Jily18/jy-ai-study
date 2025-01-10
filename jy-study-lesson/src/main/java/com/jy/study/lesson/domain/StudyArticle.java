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

    private Long lessonId;

    private String status;  // 0正常 1停用

    private Integer sort;

    private String remark;

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

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("articleId", getArticleId())
                .append("title", getTitle())
                .append("content", getContent())
                .append("lessonId", getLessonId())
                .append("status", getStatus())
                .append("sort", getSort())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}