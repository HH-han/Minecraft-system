-- 卡片表
CREATE TABLE `cards` (
`id` INT NOT NULL AUTO_INCREMENT COMMENT '卡片ID',
`username` VARCHAR(50) NOT NULL COMMENT '用户名',
`user_id` INT COMMENT '关联用户ID（如果有用户表）',
`title` VARCHAR(200) NOT NULL COMMENT '标题',
`content` TEXT NOT NULL COMMENT '内容',
`images` JSON COMMENT '图片URL数组（存储为JSON格式）',
`location` VARCHAR(255) COMMENT '地点',
`tags` VARCHAR(500) COMMENT '标签，多个标签用逗号分隔',
`date` DATE COMMENT '旅行日期',
`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`view_count` INT DEFAULT 0 COMMENT '浏览次数',
`like_count` INT DEFAULT 0 COMMENT '点赞数',
`status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-删除，2-审核中',
PRIMARY KEY (`id`),
INDEX `idx_username` (`username`),
INDEX `idx_location` (`location`),
INDEX `idx_created_at` (`created_at`),
INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卡片表';
-- 卡片标签关联表（更新外键引用）
CREATE TABLE `card_tags` (
`card_id` INT NOT NULL,
`tag_id` INT NOT NULL,
`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`card_id`, `tag_id`),
FOREIGN KEY (`card_id`) REFERENCES `cards` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卡片标签关联表';