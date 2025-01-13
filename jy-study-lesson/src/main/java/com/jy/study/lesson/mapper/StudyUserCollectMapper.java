package com.jy.study.lesson.mapper;

import com.jy.study.lesson.domain.StudyUserCollect;

public interface StudyUserCollectMapper {
    public int insertUserCollect(StudyUserCollect collect);
    
    public StudyUserCollect selectUserCollect(Long userId, String type, Long targetId);
    
    public int deleteUserCollect(Long userId, String type, Long targetId);
    
    public boolean checkCollected(Long userId, String type, Long targetId);
} 