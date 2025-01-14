package com.jy.study.lesson.service;

import com.jy.study.lesson.domain.StudyLesson;

import java.util.List;


/**
 * 课程Service接口
 * 
 * @author jily
 * @date 2025-01-14
 */
public interface IStudyLessonService 
{
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
     * 批量删除课程
     * 
     * @param lessonIds 需要删除的课程主键集合
     * @return 结果
     */
    public int deleteStudyLessonByLessonIds(String lessonIds);

    /**
     * 删除课程信息
     * 
     * @param lessonId 课程主键
     * @return 结果
     */
    public int deleteStudyLessonByLessonId(Long lessonId);

    /**
     * 更新课程浏览量
     */
    public int updateViewCount(Long lessonId);
}
