package com.jy.study.lesson.mapper;

import org.apache.ibatis.annotations.Param;
import com.jy.study.lesson.domain.StudyUserView;

public interface StudyUserViewMapper {
    public int insertUserView(StudyUserView view);
    
    public StudyUserView selectUserView(@Param("userId") Long userId, 
                                      @Param("type") String type, 
                                      @Param("targetId") Long targetId);
    
    public int deleteUserView(Long viewId);
} 