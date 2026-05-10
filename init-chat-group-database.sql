-- 群聊系统相关表

-- 群聊表
CREATE TABLE IF NOT EXISTS `chat_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '群ID',
  `name` varchar(100) NOT NULL COMMENT '群名称',
  `description` varchar(500) DEFAULT NULL COMMENT '群描述',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `max_members` int DEFAULT '500' COMMENT '最大成员数',
  `member_count` int DEFAULT '1' COMMENT '当前成员数',
  `avatar` varchar(500) DEFAULT NULL COMMENT '群头像',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊表';

-- 群成员表
CREATE TABLE IF NOT EXISTS `group_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id` bigint NOT NULL COMMENT '群ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) DEFAULT 'MEMBER' COMMENT '角色：OWNER-群主，ADMIN-管理员，MEMBER-成员',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_user` (`group_id`, `user_id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群成员表';

-- 群聊消息表
CREATE TABLE IF NOT EXISTS `group_chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `group_id` bigint NOT NULL COMMENT '群ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_name` varchar(100) DEFAULT NULL COMMENT '发送者名称',
  `sender_avatar` varchar(500) DEFAULT NULL COMMENT '发送者头像',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) DEFAULT 'TEXT' COMMENT '消息类型：TEXT-文本，IMAGE-图片，FILE-文件，VIDEO-视频',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊消息表';

-- 群聊消息已读表（可选，用于记录群消息已读状态）
CREATE TABLE IF NOT EXISTS `group_message_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_user` (`message_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群消息已读表';

-- 如果已存在表，添加缺失字段
ALTER TABLE IF EXISTS `group_chat_message` 
ADD COLUMN IF NOT EXISTS `sender_name` varchar(100) DEFAULT NULL COMMENT '发送者名称',
ADD COLUMN IF NOT EXISTS `sender_avatar` varchar(500) DEFAULT NULL COMMENT '发送者头像';
