package com.jy.study.lesson.mapper;

import com.jy.study.lesson.domain.StudyUserLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudyUserLikeMapper {
    public int insertUserLike(StudyUserLike like);
    
    public StudyUserLike selectUserLike(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
    
    public int deleteUserLike(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
    
    public boolean checkLiked(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
}
