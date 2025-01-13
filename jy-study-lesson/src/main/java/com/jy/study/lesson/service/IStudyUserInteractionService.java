package com.jy.study.lesson.service;

public interface IStudyUserInteractionService {
    /**
     * 记录浏览
     */
    void recordView(Long userId, String type, Long targetId, String ipAddr);

    /**
     * 点赞
     */
    boolean like(Long userId, String type, Long targetId);

    /**
     * 取消点赞
     */
    boolean unlike(Long userId, String type, Long targetId);

    /**
     * 收藏
     */
    boolean collect(Long userId, String type, Long targetId);

    /**
     * 取消收藏
     */
    boolean uncollect(Long userId, String type, Long targetId);

    /**
     * 检查是否已点赞
     */
    boolean checkLiked(Long userId, String type, Long targetId);

    /**
     * 检查是否已收藏
     */
    boolean checkCollected(Long userId, String type, Long targetId);
} 