package com.jy.study.lesson.mapper;

import com.jy.study.lesson.domain.StudyLesson;
import java.util.List;

public interface StudyLessonMapper {
    public StudyLesson selectLessonById(Long lessonId);

    public List<StudyLesson> selectLessonList(StudyLesson lesson);

    public int insertLesson(StudyLesson lesson);

    public int updateLesson(StudyLesson lesson);

    public int deleteLessonById(Long lessonId);

    public int deleteLessonByIds(String[] lessonIds);

    // 计数相关方法
    public int incrementViewCount(Long lessonId);
    public int incrementLikeCount(Long lessonId);
    public int decrementLikeCount(Long lessonId);
    public int incrementCollectCount(Long lessonId);
    public int decrementCollectCount(Long lessonId);
} 