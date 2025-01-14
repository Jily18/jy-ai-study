package com.jy.study.lesson.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.jy.study.common.annotation.Excel;
import com.jy.study.common.core.domain.BaseEntity;

/**
 * 课程对象 study_lesson
 *
 * @author jily
 * @date 2025-01-14
 */
public class StudyLesson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程ID */
    private Long lessonId;

    /** 课程标题 */
    @Excel(name = "课程标题")
    private String title;

    /** 课程封面图片 */
    @Excel(name = "课程封面图片")
    private String coverImg;

    /** 课程描述 */
    @Excel(name = "课程描述")
    private String description;

    /** 课程分类 */
    @Excel(name = "课程分类")
    private String category;

    /** 课程标签(逗号分隔) */
    @Excel(name = "课程标签(逗号分隔)")
    private String tags;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sort;

    /** 浏览量 */
    @Excel(name = "浏览量")
    private Long viewCount;

    /** 购买数 */
    @Excel(name = "购买数")
    private Long buyCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 收藏数 */
    @Excel(name = "收藏数")
    private Long collectCount;

    // getter和setter
    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Long buyCount) {
        this.buyCount = buyCount;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("lessonId", getLessonId())
                .append("title", getTitle())
                .append("coverImg", getCoverImg())
                .append("description", getDescription())
                .append("category", getCategory())
                .append("tags", getTags())
                .append("status", getStatus())
                .append("sort", getSort())
                .append("viewCount", getViewCount())
                .append("buyCount", getBuyCount())
                .append("likeCount", getLikeCount())
                .append("collectCount", getCollectCount())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
} 