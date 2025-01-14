package com.jy.study.lesson.mapper;

import com.jy.study.lesson.domain.StudyLesson;
import java.util.List;

public interface StudyLessonMapper {
    /**
     * 查询课程
     *
     * @param lessonId 课程主键
     * @return 课程
     */
    public StudyLesson selectStudyLessonByLessonId(Long lessonId);

    /**
     * 查询课程列表
     *
     * @param studyLesson 课程
     * @return 课程集合
     */
    public List<StudyLesson> selectStudyLessonList(StudyLesson studyLesson);

    /**
     * 新增课程
     *
     * @param studyLesson 课程
     * @return 结果
     */
    public int insertStudyLesson(StudyLesson studyLesson);

    /**
     * 修改课程
     *
     * @param studyLesson 课程
     * @return 结果
     */
    public int updateStudyLesson(StudyLesson studyLesson);

    /**
     * 删除课程
     *
     * @param lessonId 课程主键
     * @return 结果
     */
    public int deleteStudyLessonByLessonId(Long lessonId);

    /**
     * 批量删除课程
     *
     * @param lessonIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStudyLessonByLessonIds(String[] lessonIds);

    // 计数相关方法
    public int incrementViewCount(Long lessonId);
    public int incrementLikeCount(Long lessonId);
    public int decrementLikeCount(Long lessonId);
    public int incrementCollectCount(Long lessonId);
    public int decrementCollectCount(Long lessonId);

    /**
     * 更新课程浏览量
     */
    public int updateViewCount(StudyLesson lesson);
} 