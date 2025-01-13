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

-- ----------------------------
-- 1、课程表
-- ----------------------------
CREATE TABLE `study_lesson` (
    `lesson_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    `title` varchar(100) NOT NULL COMMENT '课程标题',
    `cover_img` varchar(255) DEFAULT NULL COMMENT '课程封面图片',
    `description` text COMMENT '课程描述',
    `category` varchar(50) DEFAULT NULL COMMENT '课程分类',
    `tags` varchar(255) DEFAULT NULL COMMENT '课程标签(逗号分隔)',
    `teacher_id` bigint(20) DEFAULT NULL COMMENT '讲师ID',
    `price` decimal(10,2) DEFAULT '0.00' COMMENT '课程价格',
    `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `sort` int(4) DEFAULT '0' COMMENT '排序',
    `view_count` bigint(20) DEFAULT '0' COMMENT '浏览量',
    `buy_count` bigint(20) DEFAULT '0' COMMENT '购买数',
    `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数',
    `collect_count` bigint(20) DEFAULT '0' COMMENT '收藏数',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`lesson_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- ----------------------------
-- 2、课程章节表
-- ----------------------------
CREATE TABLE `study_lesson_chapter` (
    `chapter_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `lesson_id` bigint(20) NOT NULL COMMENT '课程ID',
    `title` varchar(100) NOT NULL COMMENT '章节标题',
    `description` varchar(500) DEFAULT NULL COMMENT '章节描述',
    `sort` int(4) DEFAULT '0' COMMENT '排序',
    `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`chapter_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

-- ----------------------------
-- 3、课程资源表(视频、音频、文档等)
-- ----------------------------
CREATE TABLE `study_lesson_resource` (
    `resource_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
    `lesson_id` bigint(20) NOT NULL COMMENT '课程ID',
    `chapter_id` bigint(20) DEFAULT NULL COMMENT '章节ID',
    `title` varchar(100) NOT NULL COMMENT '资源标题',
    `type` varchar(20) NOT NULL COMMENT '资源类型(video-视频,audio-音频,doc-文档)',
    `url` varchar(500) NOT NULL COMMENT '资源URL',
    `duration` int(11) DEFAULT '0' COMMENT '时长(秒)',
    `size` bigint(20) DEFAULT '0' COMMENT '文件大小(字节)',
    `sort` int(4) DEFAULT '0' COMMENT '排序',
    `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程资源表';

-- ----------------------------
-- 4、用户学习记录表
-- ----------------------------
CREATE TABLE `study_user_learn` (
    `learn_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `lesson_id` bigint(20) NOT NULL COMMENT '课程ID',
    `resource_id` bigint(20) NOT NULL COMMENT '资源ID',
    `progress` int(3) DEFAULT '0' COMMENT '学习进度(0-100)',
    `learn_time` int(11) DEFAULT '0' COMMENT '学习时长(秒)',
    `last_learn_time` datetime DEFAULT NULL COMMENT '最后学习时间',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`learn_id`),
    KEY `idx_user_lesson` (`user_id`,`lesson_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户学习记录表';

-- ----------------------------
-- 5、用户收藏表
-- ----------------------------
CREATE TABLE `study_user_collect` (
    `collect_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `type` char(1) NOT NULL COMMENT '收藏类型(1课程 2文章)',
    `target_id` bigint(20) NOT NULL COMMENT '收藏对象ID',
    `create_time` datetime DEFAULT NULL COMMENT '收藏时间',
    PRIMARY KEY (`collect_id`),
    UNIQUE KEY `idx_user_target` (`user_id`,`type`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ----------------------------
-- 6、用户点赞表
-- ----------------------------
CREATE TABLE `study_user_like` (
    `like_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `type` char(1) NOT NULL COMMENT '点赞类型(1课程 2文章)',
    `target_id` bigint(20) NOT NULL COMMENT '点赞对象ID',
    `create_time` datetime DEFAULT NULL COMMENT '点赞时间',
    PRIMARY KEY (`like_id`),
    UNIQUE KEY `idx_user_target` (`user_id`,`type`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞表';

-- ----------------------------
-- 7、AI对话记录表
-- ----------------------------
CREATE TABLE `study_ai_chat` (
    `chat_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对话ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `chat_type` varchar(20) NOT NULL COMMENT '对话类型(digital_human-数字人,ai_model-AI模型)',
    `question` text NOT NULL COMMENT '用户问题',
    `answer` text NOT NULL COMMENT 'AI回答',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`chat_id`),
    KEY `idx_user_time` (`user_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';

-- ----------------------------
-- 8、AI知识库表
-- ----------------------------
CREATE TABLE `study_ai_knowledge` (
    `knowledge_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '知识ID',
    `title` varchar(100) NOT NULL COMMENT '知识标题',
    `content` text NOT NULL COMMENT '知识内容',
    `category` varchar(50) DEFAULT NULL COMMENT '知识分类',
    `tags` varchar(255) DEFAULT NULL COMMENT '知识标签',
    `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`knowledge_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库表';

-- ----------------------------
-- 9、用户学习统计表
-- ----------------------------
CREATE TABLE `study_user_stats` (
    `stats_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `total_learn_time` bigint(20) DEFAULT '0' COMMENT '总学习时长(秒)',
    `total_learn_days` int(11) DEFAULT '0' COMMENT '总学习天数',
    `continuous_learn_days` int(11) DEFAULT '0' COMMENT '连续学习天数',
    `last_learn_date` date DEFAULT NULL COMMENT '最后学习日期',
    `lesson_count` int(11) DEFAULT '0' COMMENT '学习课程数',
    `article_count` int(11) DEFAULT '0' COMMENT '阅读文章数',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`stats_id`),
    UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户学习统计表';

-- ----------------------------
-- 10、系统通知表
-- ----------------------------
CREATE TABLE `study_notification` (
    `notification_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `title` varchar(100) NOT NULL COMMENT '通知标题',
    `content` text NOT NULL COMMENT '通知内容',
    `type` varchar(20) NOT NULL COMMENT '通知类型(system-系统通知,course-课程通知)',
    `status` char(1) DEFAULT '0' COMMENT '状态（0未读 1已读）',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    PRIMARY KEY (`notification_id`),
    KEY `idx_user_status` (`user_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- ----------------------------
-- 用户浏览记录表
-- ----------------------------
CREATE TABLE `study_user_view` (
    `view_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '浏览ID',
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID(未登录为空)',
    `type` char(1) NOT NULL COMMENT '浏览类型(1课程 2文章)',
    `target_id` bigint(20) NOT NULL COMMENT '浏览对象ID',
    `ip_addr` varchar(128) DEFAULT '' COMMENT '访问IP地址',
    `create_time` datetime DEFAULT NULL COMMENT '浏览时间',
    PRIMARY KEY (`view_id`),
    KEY `idx_target` (`type`,`target_id`),
    KEY `idx_user_time` (`user_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览记录表';