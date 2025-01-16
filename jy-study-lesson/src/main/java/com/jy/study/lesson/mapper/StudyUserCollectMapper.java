package com.jy.study.lesson.mapper;

import com.jy.study.lesson.domain.StudyUserCollect;
import org.apache.ibatis.annotations.Param;

public interface StudyUserCollectMapper {
    public int insertUserCollect(StudyUserCollect collect);
    
    public StudyUserCollect selectUserCollect(Long userId, String type, Long targetId);
    
    public int deleteUserCollect(Long userId, String type, Long targetId);
    
    public boolean checkCollected(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
}
