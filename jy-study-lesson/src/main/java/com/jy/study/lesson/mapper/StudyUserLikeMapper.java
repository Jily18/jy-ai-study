package com.jy.study.lesson.mapper;

import com.jy.study.lesson.domain.StudyUserLike;

public interface StudyUserLikeMapper {
    public int insertUserLike(StudyUserLike like);
    
    public StudyUserLike selectUserLike(Long userId, String type, Long targetId);
    
    public int deleteUserLike(Long userId, String type, Long targetId);
    
    public boolean checkLiked(Long userId, String type, Long targetId);
} 