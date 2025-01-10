--文章表
CREATE TABLE `study_article` (
                                 `article_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
                                 `title` varchar(100) NOT NULL COMMENT '文章标题',
                                 `content` text NOT NULL COMMENT '文章内容',
                                 `lesson_id` bigint(20) DEFAULT NULL COMMENT '所属课程ID',
                                 `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                 `sort` int(4) DEFAULT '0' COMMENT '排序',
                                 `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                                 `category` varchar(50) DEFAULT 'course' COMMENT '文章分类(course-课程文章,knowledge-知识库文章)',
                                 `tags` varchar(255) DEFAULT NULL COMMENT '文章标签(逗号分隔)',
                                 `summary` varchar(500) DEFAULT NULL COMMENT '文章摘要',
                                 `view_count` bigint(20) DEFAULT '0' COMMENT '阅读量',
                                 `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数',
                                 `collect_count` bigint(20) DEFAULT '0' COMMENT '收藏数',
                                 `top` char(1) DEFAULT '0' COMMENT '是否置顶(0-否,1-是)',
                                 PRIMARY KEY (`article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程文章表';