package com.jy.study.lesson.mapper;

import com.jy.study.lesson.domain.StudyUserCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudyUserCollectMapper {
    public int insertUserCollect(StudyUserCollect collect);
    
    public StudyUserCollect selectUserCollect(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
    
    public int deleteUserCollect(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
    
    public boolean checkCollected(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
}
