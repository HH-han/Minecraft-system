/*
 Navicat Premium Dump SQL

 Source Server         : localhost-3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : db_minecraft

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 14/02/2026 16:35:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attraction
-- ----------------------------
DROP TABLE IF EXISTS `attraction`;
CREATE TABLE `attraction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景点名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片列表',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `season` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '季节',
  `rating` int NULL DEFAULT 0 COMMENT '评分',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city`(`city` ASC) USING BTREE,
  INDEX `idx_season`(`season` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '景点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品类型 ticket-票务 product-商品',
  `item_id` bigint NOT NULL COMMENT '商品ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品名称',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_item`(`user_id` ASC, `item_type` ASC, `item_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'text' COMMENT '消息类型 text-文本 image-图片 video-视频 audio-音频',
  `is_read` int NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收藏类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区',
  `item_id` bigint NOT NULL COMMENT '收藏对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_item`(`user_id` ASC, `item_type` ASC, `item_id` ASC) USING BTREE,
  INDEX `idx_item_type_id`(`item_type` ASC, `item_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区',
  `item_id` bigint NOT NULL COMMENT '评论对象ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID 0-顶级评论',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片列表',
  `rating` int NULL DEFAULT NULL COMMENT '评分 1-5星',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_item_type_id`(`item_type` ASC, `item_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for community_post
-- ----------------------------
DROP TABLE IF EXISTS `community_post`;
CREATE TABLE `community_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片列表',
  `videos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '视频列表',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `chunk_index` int NULL DEFAULT 0 COMMENT '分片索引',
  `total_chunks` int NULL DEFAULT 1 COMMENT '总分片数',
  `file_md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件MD5',
  `status` int NULL DEFAULT 0 COMMENT '状态 0-上传中 1-上传完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_md5`(`file_md5` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '美食ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '美食名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片列表',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `cuisine_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜系类型',
  `rating` int NULL DEFAULT 0 COMMENT '评分',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city`(`city` ASC) USING BTREE,
  INDEX `idx_cuisine_type`(`cuisine_type` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '美食表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '好友ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `friend_id` bigint NOT NULL COMMENT '好友ID',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NULL DEFAULT 0 COMMENT '状态 0-待确认 1-已添加',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_friend`(`user_id` ASC, `friend_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '好友表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_travel
-- ----------------------------
DROP TABLE IF EXISTS `group_travel`;
CREATE TABLE `group_travel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '跟团游ID',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '团名',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `destination` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目的地',
  `departure_time` datetime NULL DEFAULT NULL COMMENT '出发时间',
  `return_time` datetime NULL DEFAULT NULL COMMENT '返回时间',
  `max_members` int NULL DEFAULT NULL COMMENT '最大人数',
  `current_members` int NULL DEFAULT 0 COMMENT '当前人数',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片列表',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-取消 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_departure_time`(`departure_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '跟团游表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hotel
-- ----------------------------
DROP TABLE IF EXISTS `hotel`;
CREATE TABLE `hotel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '酒店ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '酒店名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片列表',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `star_level` int NULL DEFAULT NULL COMMENT '星级',
  `rating` int NULL DEFAULT 0 COMMENT '评分',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `facilities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设施',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city`(`city` ASC) USING BTREE,
  INDEX `idx_star_level`(`star_level` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '酒店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for like_record
-- ----------------------------
DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '点赞类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区 comment-评论',
  `item_id` bigint NOT NULL COMMENT '点赞对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_item`(`user_id` ASC, `item_type` ASC, `item_id` ASC) USING BTREE,
  INDEX `idx_item_type_id`(`item_type` ASC, `item_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '新闻资讯表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品类型 ticket-票务 product-商品',
  `item_id` bigint NOT NULL COMMENT '商品ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品名称',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单金额',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '订单状态 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-已退款',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for payment
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付流水号',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式 alipay-支付宝 wechat-微信 card-银行卡',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '支付状态 0-待支付 1-支付成功 2-支付失败 3-已取消 4-退款中 5-已退款',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片列表',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `rating` int NULL DEFAULT 0 COMMENT '评分',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city`(`city` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '特产/纪念品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '票务ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型 train-火车 flight-飞机 taxi-出租车',
  `departure_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '出发城市',
  `arrival_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '到达城市',
  `departure_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出发时间',
  `arrival_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '到达时间',
  `carrier` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '承运商',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `seat_class` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '座位等级',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_departure_city`(`departure_city` ASC) USING BTREE,
  INDEX `idx_arrival_city`(`arrival_city` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '票务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for travel_plan
-- ----------------------------
DROP TABLE IF EXISTS `travel_plan`;
CREATE TABLE `travel_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '计划内容',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_start_date`(`start_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '出行计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `occupation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职业',
  `hobbies` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '爱好',
  `bio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人简介',
  `online` int NULL DEFAULT 0 COMMENT '在线状态 0-离线 1-在线',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account`(`account` ASC) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for continents
-- ----------------------------
DROP TABLE IF EXISTS `continents`;
CREATE TABLE `continents`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '大洲ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '大洲名称',
  `chinese_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '面积(万平方公里)',
  `population` int NULL DEFAULT NULL COMMENT '人口(万)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '大洲表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for countries
-- ----------------------------
DROP TABLE IF EXISTS `countries`;
CREATE TABLE `countries`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '国家ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '国家名称',
  `chinese_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国家图片',
  `continent_id` int NOT NULL COMMENT '所属大洲ID',
  `capital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '首都',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '面积(万平方公里)',
  `population` int NULL DEFAULT NULL COMMENT '人口(万)',
  `currency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '货币',
  `language` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '官方语言',
  `timezone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时区',
  `country_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国家代码',
  `phone_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话区号',
  `flag_emoji` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国旗emoji',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '简介',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_continent_id`(`continent_id` ASC) USING BTREE,
  CONSTRAINT `fk_countries_continent` FOREIGN KEY (`continent_id`) REFERENCES `continents` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '国家表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cities
-- ----------------------------
DROP TABLE IF EXISTS `cities`;
CREATE TABLE `cities`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '城市ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市名称',
  `chinese_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市图片',
  `country_id` int NOT NULL COMMENT '所属国家ID',
  `is_capital` tinyint(1) NULL DEFAULT 0 COMMENT '是否是首都',
  `area` decimal(8, 2) NULL DEFAULT NULL COMMENT '面积(平方公里)',
  `population` int NULL DEFAULT NULL COMMENT '人口(万)',
  `timezone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时区',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '城市简介',
  `latitude` decimal(10, 8) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(11, 8) NULL DEFAULT NULL COMMENT '经度',
  `famous_for` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '闻名于世的原因',
  `best_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最佳旅游季节',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_country_id`(`country_id` ASC) USING BTREE,
  CONSTRAINT `fk_cities_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '城市表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attractions
-- ----------------------------
DROP TABLE IF EXISTS `attractions`;
CREATE TABLE `attractions`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景点名称',
  `chinese_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `city_id` int NOT NULL COMMENT '所在城市ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型(古迹/自然/现代建筑等)',
  `rating` decimal(2, 1) NULL DEFAULT NULL COMMENT '评分(1-5)',
  `price_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消费水平(免费/便宜/中等/昂贵)',
  `visiting_hours` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开放时间',
  `best_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最佳游览季节',
  `visit_duration` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '建议游览时长',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '详细描述',
  `history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '历史背景',
  `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '游玩建议',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '具体地址',
  `latitude` decimal(10, 8) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(11, 8) NULL DEFAULT NULL COMMENT '经度',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主图URL',
  `official_website` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '官方网站',
  `year_built` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '建造年份',
  `architect` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '建筑师',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city_id`(`city_id` ASC) USING BTREE,
  CONSTRAINT `fk_attractions_city` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '景点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE COMMENT '标签名称',
  `chinese_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签分类(主题/适合人群/特色等)',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签图标',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签颜色',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attraction_tags
-- ----------------------------
DROP TABLE IF EXISTS `attraction_tags`;
CREATE TABLE `attraction_tags`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '景点-标签关联ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `tag_id` int NOT NULL COMMENT '标签ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_attraction_tag`(`attraction_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_attraction_id`(`attraction_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `fk_attraction_tags_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_attraction_tags_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '景点-标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attraction_images
-- ----------------------------
DROP TABLE IF EXISTS `attraction_images`;
CREATE TABLE `attraction_images`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '缩略图URL',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片描述',
  `photographer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摄影师',
  `license` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '版权信息',
  `is_primary` tinyint(1) NULL DEFAULT 0 COMMENT '是否是主图',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `width` int NULL DEFAULT NULL COMMENT '图片宽度',
  `height` int NULL DEFAULT NULL COMMENT '图片高度',
  `file_size` int NULL DEFAULT NULL COMMENT '文件大小(KB)',
  `upload_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attraction_id`(`attraction_id` ASC) USING BTREE,
  CONSTRAINT `fk_attraction_images_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ticket_prices
-- ----------------------------
DROP TABLE IF EXISTS `ticket_prices`;
CREATE TABLE `ticket_prices`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '门票价格ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `ticket_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '门票类型(成人票/学生票/儿童票/老人票)',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '货币单位',
  `season` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '适用季节(旺季/淡季/全年)',
  `start_date` date NULL DEFAULT NULL COMMENT '有效期开始',
  `end_date` date NULL DEFAULT NULL COMMENT '有效期结束',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '价格说明',
  `purchase_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '购票链接',
  `discount_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优惠信息',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attraction_id`(`attraction_id` ASC) USING BTREE,
  CONSTRAINT `fk_ticket_prices_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT '门票价格表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for transportation
-- ----------------------------
DROP TABLE IF EXISTS `transportation`;
CREATE TABLE `transportation`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '交通方式ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交通类型(公交/地铁/出租车/步行/缆车等)',
  `route_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '路线信息',
  `from_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '起始地点',
  `duration` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所需时间',
  `distance` decimal(8, 2) NULL DEFAULT NULL COMMENT '距离(公里)',
  `cost` decimal(8, 2) NULL DEFAULT NULL COMMENT '费用',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '货币单位',
  `schedule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '运营时间',
  `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '交通提示',
  `is_recommended` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attraction_id`(`attraction_id` ASC) USING BTREE,
  CONSTRAINT `fk_transportation_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT '交通方式表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` decimal(2, 1) NOT NULL COMMENT '评分(1-5)',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评论标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `visit_date` date NULL DEFAULT NULL COMMENT '游玩日期',
  `travel_with` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '和谁一起(独自/伴侣/家庭/朋友)',
  `photos_count` int NULL DEFAULT 0 COMMENT '照片数量',
  `likes_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `helpful_count` int NULL DEFAULT 0 COMMENT '有用数',
  `is_verified` tinyint(1) NULL DEFAULT 0 COMMENT '是否验证过',
  `status` enum('pending', 'approved', 'rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '审核状态',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attraction_rating`(`attraction_id` ASC, `rating` ASC) USING BTREE,
  INDEX `idx_user_created`(`user_id` ASC, `created_at` ASC) USING BTREE,
  CONSTRAINT `fk_reviews_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for review_replies
-- ----------------------------
DROP TABLE IF EXISTS `review_replies`;
CREATE TABLE `review_replies`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论回复ID',
  `review_id` int NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '回复用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回复内容',
  `likes_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_review_id`(`review_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_review_replies_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_replies_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT '评论回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for review_likes
-- ----------------------------
DROP TABLE IF EXISTS `review_likes`;
CREATE TABLE `review_likes`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论点赞ID',
  `review_id` int NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_review_user`(`review_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_review_id`(`review_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_review_likes_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_likes_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT '评论点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for carousels
-- ----------------------------
DROP TABLE IF EXISTS `carousels`;
CREATE TABLE `carousels`  (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL COMMENT '轮播标题',
    `subtitle` VARCHAR(500) COMMENT '副标题/描述',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `mobile_image_url` VARCHAR(500) COMMENT '移动端图片URL(适配手机)',
    `thumbnail_url` VARCHAR(500) COMMENT '缩略图URL',
    
    -- 跳转链接信息
    `link_type` ENUM('none', 'url', 'attraction', 'city', 'country', 'custom_page') 
        DEFAULT 'none' COMMENT '链接类型',
    `link_url` VARCHAR(500) COMMENT '跳转链接URL',
    `link_target` ENUM('_self', '_blank') DEFAULT '_self' COMMENT '链接打开方式',
    `target_id` INT COMMENT '目标ID(当link_type为attraction/city/country时使用)',
    
    -- 按钮文字
    `button_text` VARCHAR(50) COMMENT '按钮文字',
    `button_color` VARCHAR(20) COMMENT '按钮颜色',
    
    -- 文字覆盖层样式
    `text_color` VARCHAR(20) DEFAULT '#FFFFFF' COMMENT '文字颜色',
    `text_shadow` BOOLEAN DEFAULT TRUE COMMENT '文字阴影',
    `overlay_color` VARCHAR(20) COMMENT '遮罩层颜色',
    `overlay_opacity` DECIMAL(2,1) DEFAULT 0.3 COMMENT '遮罩层透明度(0-1)',
    
    -- 展示策略
    `position` VARCHAR(50) COMMENT '展示位置(home/home_top/home_middle/home_bottom/attraction_page等)',
    `device_type` ENUM('all', 'pc', 'mobile') DEFAULT 'all' COMMENT '适配设备',
    `user_type` ENUM('all', 'visitor', 'logged_in', 'vip') DEFAULT 'all' COMMENT '用户类型',
    
    -- 时间控制
    `start_time` DATETIME COMMENT '开始展示时间',
    `end_time` DATETIME COMMENT '结束展示时间',
    `is_always_show` BOOLEAN DEFAULT TRUE COMMENT '是否永久展示',
    
    -- 统计信息
    `click_count` INT DEFAULT 0 COMMENT '点击次数',
    `impression_count` INT DEFAULT 0 COMMENT '展示次数',
    
    -- 排序和状态
    `sort_order` INT DEFAULT 0 COMMENT '排序(数字越小越靠前)',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否删除(软删除)',
    
    -- 备注信息
    `remark` VARCHAR(500) COMMENT '备注',
    `created_by` VARCHAR(50) COMMENT '创建人',
    
    -- 时间戳
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX `idx_position_active` (`position`, `is_active`, `sort_order`),
    INDEX `idx_time_range` (`start_time`, `end_time`),
    INDEX `idx_click_count` (`click_count`)
) COMMENT='轮播图表';

-- ----------------------------
-- Table structure for carousel_groups
-- ----------------------------
DROP TABLE IF EXISTS `carousel_groups`;
CREATE TABLE `carousel_groups`  (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_name` VARCHAR(100) NOT NULL COMMENT '分组名称',
    `group_code` VARCHAR(50) UNIQUE NOT NULL COMMENT '分组代码(用于前端调用)',
    `description` VARCHAR(500) COMMENT '分组描述',
    
    -- 展示配置
    `display_type` ENUM('single', 'multiple', 'fullscreen') DEFAULT 'multiple' COMMENT '展示类型',
    `auto_play` BOOLEAN DEFAULT TRUE COMMENT '是否自动播放',
    `auto_play_interval` INT DEFAULT 5000 COMMENT '自动播放间隔(毫秒)',
    `animation_type` ENUM('slide', 'fade', 'cube', 'coverflow') DEFAULT 'slide' COMMENT '切换动画',
    `show_indicators` BOOLEAN DEFAULT TRUE COMMENT '是否显示指示点',
    `show_arrows` BOOLEAN DEFAULT TRUE COMMENT '是否显示箭头',
    `pause_on_hover` BOOLEAN DEFAULT TRUE COMMENT '鼠标悬停时暂停',
    
    -- 尺寸配置
    `width` INT COMMENT '宽度(像素)',
    `height` INT COMMENT '高度(像素)',
    `aspect_ratio` VARCHAR(20) COMMENT '宽高比(如16:9)',
    
    -- 其他配置
    `max_items` INT DEFAULT 10 COMMENT '最大显示数量',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='轮播分组表';

-- ----------------------------
-- Table structure for carousel_group_items
-- ----------------------------
DROP TABLE IF EXISTS `carousel_group_items`;
CREATE TABLE `carousel_group_items`  (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_id` INT NOT NULL COMMENT '分组ID',
    `carousel_id` INT NOT NULL COMMENT '轮播ID',
    `sort_order` INT DEFAULT 0 COMMENT '在分组内的排序',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '在分组内是否启用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`group_id`) REFERENCES `carousel_groups`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`carousel_id`) REFERENCES `carousels`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_group_carousel` (`group_id`, `carousel_id`)
) COMMENT='轮播分组项关联表';

-- ----------------------------
-- Table structure for carousel_stats
-- ----------------------------
DROP TABLE IF EXISTS `carousel_stats`;
CREATE TABLE `carousel_stats`  (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `carousel_id` INT NOT NULL COMMENT '轮播ID',
    `stat_date` DATE NOT NULL COMMENT '统计日期',
    `click_count` INT DEFAULT 0 COMMENT '当日点击次数',
    `impression_count` INT DEFAULT 0 COMMENT '当日展示次数',
    `unique_click_count` INT DEFAULT 0 COMMENT '独立IP点击次数',
    `unique_impression_count` INT DEFAULT 0 COMMENT '独立IP展示次数',
    
    -- 设备统计
    `pc_click_count` INT DEFAULT 0 COMMENT 'PC端点击',
    `mobile_click_count` INT DEFAULT 0 COMMENT '移动端点击',
    `pc_impression_count` INT DEFAULT 0 COMMENT 'PC端展示',
    `mobile_impression_count` INT DEFAULT 0 COMMENT '移动端展示',
    
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`carousel_id`) REFERENCES `carousels`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_carousel_date` (`carousel_id`, `stat_date`)
) COMMENT='轮播统计表';

SET FOREIGN_KEY_CHECKS = 1;


create table worldcharacteristics
(
    id          int auto_increment
        primary key comment '主键ID，自增',
    name        varchar(255) not null comment '名称（如：长城、埃菲尔铁塔等）',
    country     varchar(100) not null comment '所属国家',
    image       text         null comment '图片URL或Base64数据',
    description text         null comment '简要描述',
    features    text         null comment '特色/特点',
    history     text         null comment '历史背景',
    culture     text         null comment '文化意义',
    tags        text         null comment '标签（多个标签可用逗号分隔）',
    category    varchar(100) null comment '分类（如：建筑、自然景观、文物等）',
    color       varchar(20)  null comment '主题颜色（用于前端展示）'
)
    row_format = DYNAMIC comment = '世界特征信息表（存储各国特色建筑、景观、文化等）';


-- 创建月份表
CREATE TABLE months
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '月份ID，自增',
    month_name  VARCHAR(20) NOT NULL COMMENT '月份名称（如：一月、二月等）',
    month_order TINYINT     NOT NULL COMMENT '月份顺序（1-12）',
    is_active   TINYINT(1) DEFAULT 1 COMMENT '是否启用（0：禁用，1：启用）',
    created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT = '月份配置表';

-- 插入月份数据
INSERT INTO months (month_name, month_order) VALUES
                                                 ('一月', 1),
                                                 ('二月', 2),
                                                 ('三月', 3),
                                                 ('四月', 4),
                                                 ('五月', 5),
                                                 ('六月', 6),
                                                 ('七月', 7),
                                                 ('八月', 8),
                                                 ('九月', 9),
                                                 ('十月', 10),
                                                 ('十一月', 11),
                                                 ('十二月', 12);

-- 创建月份旅行推荐内容表
CREATE TABLE monthly_recommendations
(
    id               INT AUTO_INCREMENT PRIMARY KEY COMMENT '推荐ID，自增',
    month_id         INT          NOT NULL COMMENT '关联月份ID',
    destination_name VARCHAR(255) NOT NULL COMMENT '目的地名称',
    image_url        TEXT COMMENT '图片URL',
    description      TEXT COMMENT '目的地描述',
    tags             JSON COMMENT '标签（JSON数组格式，如：["雪山","滑雪","温泉"]）',
    season_features  VARCHAR(500) COMMENT '当季特色',
    climate_info     VARCHAR(500) COMMENT '气候信息',
    travel_tips      TEXT COMMENT '旅行小贴士',
    recommended_days TINYINT COMMENT '建议游玩天数',
    sort_order       INT    DEFAULT 0 COMMENT '排序顺序（数字越小越靠前）',
    is_recommended   TINYINT(1) DEFAULT 1 COMMENT '是否推荐（0：不推荐，1：推荐）',
    view_count       INT    DEFAULT 0 COMMENT '浏览次数',
    created_at       TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (month_id) REFERENCES months(id) ON DELETE CASCADE
) COMMENT = '月份旅行推荐内容表';

-- 为常用查询字段添加索引
CREATE INDEX idx_month_id ON monthly_recommendations(month_id) COMMENT '月份ID索引';
CREATE INDEX idx_is_recommended ON monthly_recommendations(is_recommended) COMMENT '推荐状态索引';
CREATE INDEX idx_sort_order ON monthly_recommendations(sort_order) COMMENT '排序索引';

-- 插入示例数据（一月推荐）
INSERT INTO monthly_recommendations (
    month_id,
    destination_name,
    image_url,
    description,
    tags,
    season_features,
    climate_info,
    travel_tips,
    recommended_days,
    sort_order
) VALUES
      (1, '哈尔滨', '/images/harbin.jpg', '冰雪之城，冬季旅游胜地',
       JSON_ARRAY('冰雪', '冰雕', '滑雪'),
       '哈尔滨国际冰雪节，欣赏精美冰雕雪雕',
       '气温约-20℃至-10℃，寒冷干燥',
       '注意保暖，穿羽绒服、雪地靴，携带保温杯',
       3, 1),

      (1, '三亚', '/images/sanya.jpg', '避寒胜地，热带海滨风光',
       JSON_ARRAY('海滩', '阳光', '海鲜'),
       '温暖如春，适合避寒度假',
       '气温约20℃-28℃，舒适宜人',
       '防晒必备，可下海游泳',
       4, 2),

      (1, '长白山', '/images/changbaishan.jpg', '冬季滑雪泡温泉的绝佳去处',
       JSON_ARRAY('滑雪', '温泉', '雪景'),
       '林海雪原，雾凇奇观',
       '气温约-25℃至-15℃，山区更冷',
       '滑雪需注意安全，泡温泉要控制时间',
       3, 3),

      (1, '昆明', '/images/kunming.jpg', '春城，四季如春',
       JSON_ARRAY('暖冬', '花卉', '民族风情'),
       '冬季温暖，鲜花盛开',
       '气温约5℃-18℃，早晚温差大',
       '早晚需穿外套，可游览滇池、石林',
       2, 4);

-- 插入示例数据（七月推荐）
INSERT INTO monthly_recommendations (
    month_id,
    destination_name,
    image_url,
    description,
    tags,
    season_features,
    climate_info,
    travel_tips,
    recommended_days,
    sort_order
) VALUES
      (7, '青岛', '/images/qingdao.jpg', '海滨城市，避暑胜地',
       JSON_ARRAY('海滩', '啤酒', '海鲜'),
       '青岛国际啤酒节，海滨浴场开放',
       '气温约22℃-28℃，凉爽海风',
       '品尝海鲜啤酒，注意防晒',
       3, 1),

      (7, '承德避暑山庄', '/images/chengde.jpg', '清代皇家避暑胜地',
       JSON_ARRAY('古迹', '避暑', '园林'),
       '山区凉爽，适合避暑',
       '气温约20℃-28℃，山区凉爽',
       '游览皇家园林，了解清代历史',
       2, 2),

      (7, '九寨沟', '/images/jiuzhaigou.jpg', '童话世界，夏季水量充沛',
       JSON_ARRAY('山水', '瀑布', '原始森林'),
       '水量充沛，瀑布壮观',
       '气温约15℃-25℃，昼夜温差大',
       '带雨具，注意高原反应',
       4, 3),

      (7, '呼伦贝尔', '/images/hulunbeier.jpg', '大草原最美的季节',
       JSON_ARRAY('草原', '骑马', '蒙古包'),
       '草原最美季节，那达慕大会',
       '气温约18℃-28℃，昼夜温差大',
       '体验草原文化，骑马需注意安全',
       4, 4);

-- 创建视图：方便查询月份及其推荐内容
CREATE VIEW view_monthly_recommendations AS
SELECT
    m.id AS month_id,
    m.month_name,
    m.month_order,
    r.id AS recommendation_id,
    r.destination_name,
    r.image_url,
    r.description,
    r.tags,
    r.season_features,
    r.climate_info,
    r.travel_tips,
    r.recommended_days,
    r.sort_order
FROM months m
         LEFT JOIN monthly_recommendations r ON m.id = r.month_id AND r.is_recommended = 1
ORDER BY m.month_order, r.sort_order;

-- 创建存储过程：根据月份获取推荐
DELIMITER $$
CREATE PROCEDURE get_recommendations_by_month(IN p_month_name VARCHAR(20))
BEGIN
SELECT
    r.destination_name AS name,
    r.image_url AS imageUrl,
    r.description,
    r.tags,
    r.season_features AS seasonFeatures,
    r.climate_info AS climateInfo,
    r.travel_tips AS travelTips,
    r.recommended_days AS recommendedDays
FROM monthly_recommendations r
         INNER JOIN months m ON r.month_id = m.id
WHERE m.month_name = p_month_name AND r.is_recommended = 1
ORDER BY r.sort_order;
END$$
DELIMITER ;

-- 创建推荐表
create table recommend
(
    id          int auto_increment
        primary key,
    image       varchar(255)  null,
    name        varchar(255)  not null,
    description varchar(255)  null,
    details     varchar(5000) null
)
    row_format = DYNAMIC;
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (7, 'http://localhost:2025/upload/1d9ebc15-2a48-4814-bedf-838cbd1d4a03.png', '故宫博物院', '明清皇家宫殿，世界文化遗产', '故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。故宫以三大殿为中心，占地面积约72万平方米，建筑面积约15万平方米，有大小宫殿七十多座，房屋九千余间。故宫是世界上现存规模最大、保存最为完整的木质结构古建筑之一，是国家AAAAA级旅游景区，1961年被列为第一批全国重点文物保护单位；1987年被列为世界文化遗产。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (8, 'http://localhost:2025/upload/276c0fd3-7d5e-49b6-8221-7fdea1b07fcd.png', '长城', '世界新七大奇迹之一', '长城又称万里长城，是中国古代的军事防御工事，是一道高大、坚固而且连绵不断的长垣，用以限隔敌骑的行动。长城不是一道单纯孤立的城墙，而是以城墙为主体，同大量的城、障、亭、标相结合的防御体系。长城修筑的历史可上溯到西周时期，发生在首都镐京的著名典故“烽火戏诸侯”就源于此。春秋战国时期列国争霸，互相防守，长城修筑进入第一个高潮，但此时修筑的长度都比较短。秦灭六国统一天下后，秦始皇连接和修缮战国长城，始有万里长城之称。明朝是最后一个大修长城的朝代，今天人们所看到的长城多是此时修筑。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (9, 'http://localhost:2025/upload/ceed866f-7ac0-4788-989c-bf8f2ffbc589.png', '西湖', '人间天堂，杭州明珠', '西湖位于浙江省杭州市西湖区龙井路1号，杭州市区西部，景区总面积49平方千米，汇水面积为21.22平方千米，湖面面积为6.38平方千米。西湖有100多处公园景点，有“西湖十景”、“新西湖十景”、“三评西湖十景”之说，有60多处国家、省、市级重点文物保护单位和20多座博物馆，有断桥、雷峰塔、钱王祠、净慈寺、苏小小墓等景点。2007年，杭州市西湖风景名胜区被评为“国家AAAAA级旅游景区”。2011年6月24日，“杭州西湖文化景观”正式被列入《世界遗产名录》。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (10, 'http://localhost:2025/upload/46a7d0c6-9ce6-4cd1-876d-1c3b853fcf85.png', '兵马俑', '世界第八大奇迹', '兵马俑，即秦始皇兵马俑，亦简称秦兵马俑或秦俑，是古代墓葬雕塑的一个类别。位于今陕西省西安市临潼区秦始皇陵以东1.5千米处的兵马俑坑内。兵马俑是制成兵马（战车、战马、士兵）形状的殉葬品。1961年3月4日，秦始皇陵被国务院公布为第一批全国重点文物保护单位。1974年3月，兵马俑被发现。1987年，秦始皇陵及兵马俑坑被联合国教科文组织批准列入《世界遗产名录》，并被誉为“世界第八大奇迹”，先后有200多位外国元首和政府首脑参观访问，成为中国古代辉煌文明的一张金字名片，被誉为世界十大古墓稀世珍宝之一。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (11, 'http://localhost:2025/upload/4fc497b9-0e0b-439a-bd56-170fb49f2c05.png', '张家界', '奇峰三千，秀水八百', '张家界国家森林公园位于湖南省西北部张家界市境内，是中国第一个国家森林公园。公园自然风光以峰称奇、以谷显幽、以林见秀。其间有奇峰3000多座，这些石峰如人如兽、如器如物，形象逼真，气势壮观。峰间峡谷，溪流潺潺，浓荫蔽日。有“三千奇峰，八百秀水”之美称。公园不仅自然风光壮美绝伦，而且森林植物和野生动物资源极为丰富，森林覆盖率达98%，是一座巨大的生物宝库和天然氧吧，被称为“自然博物馆和天然植物园”。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (12, 'http://localhost:2025/upload/87df6577-08cd-4a3e-82e4-ce8274d6e203.png', '九寨沟', '童话世界，人间仙境', '九寨沟位于四川省阿坝藏族羌族自治州九寨沟县境内，是一条纵深50余千米的山沟谷地，总面积64297公顷，森林覆盖率超过80%。因沟内有树正寨、荷叶寨、则查洼寨等九个藏族村寨坐落在这片高山湖泊群中而得名。九寨沟国家级自然保护区主要保护对象是大熊猫、金丝猴等珍稀动物及其自然生态环境。有74种国家保护珍稀植物，有18种国家保护动物，还有丰富的古生物化石、古冰川地貌。“九寨归来不看水”，是对九寨沟景色真实的诠释。泉、瀑、河、滩108个海子，构成一个个五彩斑斓的瑶池玉盆。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (13, 'http://localhost:2025/upload/2a99bc39-f02b-45cd-8f22-fd9eadca74ec.png', '黄山', '五岳归来不看山，黄山归来不看岳', '黄山位于安徽省南部黄山市境内，有72峰，主峰莲花峰海拔1864米，与光明顶、天都峰并称三大黄山主峰，为36大峰之一。黄山是安徽旅游的标志，是中国十大风景名胜唯一的山岳风光。黄山原名“黟山”，因峰岩青黑，遥望苍黛而名。后因传说轩辕黄帝曾在此炼丹，故改名为“黄山”。黄山代表景观有“四绝三瀑”，四绝：奇松、怪石、云海、温泉；三瀑：人字瀑、百丈泉、九龙瀑。黄山迎客松是安徽人民热情友好的象征，承载着拥抱世界的东方礼仪文化。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (14, 'http://localhost:2025/upload/00f6eccf-1e43-41cd-b410-1ee3fc524678.png', '桂林山水', '桂林山水甲天下', '桂林山水是对桂林旅游资源的总称。国家AAAAA级旅游景区，中国十大风景名胜之一，桂林山水是中国山水的代表，典型的喀斯特地形构成了别具一格的桂林山水，桂林山水所指的范围很广，项目繁多。桂林山水“山青、水秀、洞奇、石美"，包括山、水、喀斯特岩洞、石刻等等，其境内的山水风光举世闻名，千百年来享有“桂林山水甲天下”的美誉。漓江属珠江水系的桂江上游河段，发源于华南第一峰猫儿山，是桂林山水风光的精华。闻名全球的漓江景区是桂林山水的核心，漓江像一条青绸绿带，盘绕在万点峰峦之间。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (15, 'http://localhost:2025/upload/f787f69a-274b-442a-9ed3-9b5030673180.png', '鼓浪屿', '海上花园，钢琴之岛', '鼓浪屿原名“圆沙洲”，别名“圆洲仔”，南宋时期命“五龙屿”，明朝改称“鼓浪屿”。因岛西南方海滩上有一块两米多高、中有洞穴的礁石，每当涨潮水涌，浪击礁石，声似擂鼓，人们称“鼓浪石”，鼓浪屿因此而得名。鼓浪屿风景名胜区获得国家5A级旅游景区、全国重点文物保护单位、中国最美五大城区等荣誉。2017年7月8日，“鼓浪屿：历史国际社区”被列入世界遗产名录，成为中国第52项世界遗产项目。岛上气候宜人，四季如春，无车马喧嚣，有鸟语花香，素有“海上花园”之誉。');
INSERT INTO db_minecraft.recommend (id, image, name, description, details) VALUES (16, 'http://localhost:2025/upload/01130228-67ad-4d32-be47-25e751b8f9ba.png', '亚龙湾', '天下第一湾', '亚龙湾是中华人民共和国海南省三亚市东郊的一处优质热带海滨风景区，距离市中心区约10公里。海湾以中心有野猪岛为中心，南有东洲岛、西洲岛，西面有东排、西排，可开展多种水上运动。亚龙湾为一个月牙湾，拥有7千米长的银白色海滩，沙质相当细腻。而这里的南海没有受到污染，海水洁净透明，远望呈现几种不同的蓝色，而水面下珊瑚种类丰富，可清楚观赏珊瑚，适合多种水面下活动包括潜水等，令海底成为了当地的旅游的核心。岸上林木郁郁葱葱。冬季这里的气温27摄氏度，水温20摄氏度，是一处理想的冬季避寒和休闲度假胜地。号称"东方夏威夷"。');

-- ======================================================
-- 数据库：旅游推荐系统
-- 表名：home_recommendations
-- 描述：首页旅游推荐内容表（单表）
-- 作者：AI 助手
-- 创建时间：2024
-- ======================================================

CREATE TABLE home_recommendations (
    -- 主键与基础信息
                                        id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID，自增长',
                                        uuid CHAR(36) NOT NULL COMMENT '全局唯一标识符，用于对外接口',
                                        name VARCHAR(200) NOT NULL COMMENT '景点/项目名称',
                                        english_name VARCHAR(500) DEFAULT NULL COMMENT '英文名称（如果有）',
                                        alias_name VARCHAR(500) DEFAULT NULL COMMENT '别名/俗称',

    -- 分类与标签
                                        category_id INT UNSIGNED NOT NULL COMMENT '分类ID（如：1-自然风光，2-历史文化，3-主题乐园，4-美食探店，5-休闲度假）',
                                        category_name VARCHAR(100) NOT NULL COMMENT '分类名称（冗余字段，便于查询）',
                                        tags VARCHAR(500) DEFAULT NULL COMMENT '标签，多个用逗号分隔（如：亲子,情侣,网红打卡,拍照圣地）',

    -- 地理位置
                                        country VARCHAR(100) NOT NULL DEFAULT '中国' COMMENT '国家',
                                        province VARCHAR(100) NOT NULL COMMENT '省份/直辖市',
                                        city VARCHAR(100) NOT NULL COMMENT '城市',
                                        district VARCHAR(100) DEFAULT NULL COMMENT '区/县',
                                        address VARCHAR(500) NOT NULL COMMENT '详细地址',
                                        longitude DECIMAL(10, 7) DEFAULT NULL COMMENT '经度（WGS84坐标系）',
                                        latitude DECIMAL(10, 7) DEFAULT NULL COMMENT '纬度（WGS84坐标系）',
                                        google_map_url VARCHAR(1000) DEFAULT NULL COMMENT 'Google地图链接',
                                        baidu_map_url VARCHAR(1000) DEFAULT NULL COMMENT '百度地图链接',
                                        transportation TEXT COMMENT '交通方式描述（如：地铁X号线XX站A口出，步行500米）',

    -- 推荐核心信息
                                        recommendation_reason TEXT NOT NULL COMMENT '推荐理由（首页展示的核心文案）',
                                        highlights TEXT COMMENT '亮点特色（多个亮点可用分号分隔）',
                                        best_travel_time VARCHAR(200) DEFAULT NULL COMMENT '最佳游玩时间（如：春秋两季，9月-11月）',
                                        visit_duration VARCHAR(100) DEFAULT NULL COMMENT '建议游玩时长（如：3-4小时，1天）',

    -- 媒体资源
                                        cover_image_url VARCHAR(1000) NOT NULL COMMENT '封面图片URL（首页列表展示）',
                                        banner_image_url VARCHAR(1000) DEFAULT NULL COMMENT '详情页Banner图URL',
                                        video_url VARCHAR(1000) DEFAULT NULL COMMENT '宣传视频URL',
                                        image_urls TEXT COMMENT '更多图片URL，JSON数组格式存储',

    -- 价格与费用
                                        ticket_price_description VARCHAR(500) DEFAULT NULL COMMENT '门票价格描述（如：成人票100元，学生票50元）',
                                        min_price DECIMAL(10, 2) UNSIGNED DEFAULT NULL COMMENT '最低价格（用于价格筛选）',
                                        max_price DECIMAL(10, 2) UNSIGNED DEFAULT NULL COMMENT '最高价格（用于价格筛选）',
                                        price_currency VARCHAR(10) DEFAULT 'CNY' COMMENT '货币单位（CNY, USD, etc.）',
                                        is_free BOOLEAN DEFAULT FALSE COMMENT '是否免费（TRUE-免费，FALSE-收费）',

    -- 开放时间
                                        opening_hours_weekday VARCHAR(200) DEFAULT NULL COMMENT '工作日开放时间（如：09:00-18:00）',
                                        opening_hours_weekend VARCHAR(200) DEFAULT NULL COMMENT '周末开放时间（如：09:00-20:00）',
                                        opening_hours_description TEXT COMMENT '开放时间详细描述（如：每周一闭馆，节假日正常开放）',

    -- 联系方式
                                        contact_phone VARCHAR(200) DEFAULT NULL COMMENT '联系电话（多个用逗号分隔）',
                                        official_website VARCHAR(500) DEFAULT NULL COMMENT '官方网站',
                                        social_media JSON DEFAULT NULL COMMENT '社交媒体信息（JSON格式，如：{"weibo":"xxx", "wechat":"xxx", "douyin":"xxx"}）',

    -- 统计与评分
                                        view_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览次数',
                                        like_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞/收藏次数',
                                        share_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '分享次数',
                                        comment_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论次数',
                                        average_rating DECIMAL(3, 2) UNSIGNED DEFAULT 0.00 COMMENT '平均评分（满分5分）',
                                        rating_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '评分人数',

    -- 推荐权重与状态
                                        weight INT NOT NULL DEFAULT 0 COMMENT '推荐权重（数字越大，排名越靠前）',
                                        is_hot BOOLEAN DEFAULT FALSE COMMENT '是否热门推荐',
                                        is_new BOOLEAN DEFAULT FALSE COMMENT '是否新晋推荐',
                                        is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
                                        status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架，2-审核中，3-待发布',

    -- 季节/节日关联
                                        season VARCHAR(50) DEFAULT NULL COMMENT '推荐季节（spring, summer, autumn, winter, all）',
                                        festival VARCHAR(100) DEFAULT NULL COMMENT '关联节日（如：春节，国庆，圣诞节）',

    -- 注意事项
                                        tips TEXT COMMENT '游玩小贴士/注意事项',
                                        suitable_crowd VARCHAR(200) DEFAULT NULL COMMENT '适合人群（如：亲子，情侣，朋友，家庭，老人）',
                                        facility_info TEXT COMMENT '设施信息（如：有无停车场，有无母婴室，无障碍设施等）',

    -- SEO相关
                                        seo_title VARCHAR(500) DEFAULT NULL COMMENT 'SEO标题',
                                        seo_keywords VARCHAR(500) DEFAULT NULL COMMENT 'SEO关键词',
                                        seo_description TEXT COMMENT 'SEO描述',

    -- 内容管理
                                        content TEXT COMMENT '详细介绍内容（富文本或Markdown）',
                                        summary VARCHAR(1000) DEFAULT NULL COMMENT '摘要/简介（用于列表页）',
                                        source VARCHAR(200) DEFAULT NULL COMMENT '信息来源（如：小编踩点，用户投稿，官方合作）',
                                        author VARCHAR(100) DEFAULT NULL COMMENT '作者/编辑',

    -- 时间戳
                                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        published_at TIMESTAMP NULL DEFAULT NULL COMMENT '发布时间',
                                        expired_at TIMESTAMP NULL DEFAULT NULL COMMENT '过期时间（NULL表示永不过期）',

    -- 主键约束
                                        PRIMARY KEY (id),

    -- 唯一约束
                                        UNIQUE KEY uk_uuid (uuid),

    -- 索引：用于提升查询效率
                                        INDEX idx_category (category_id, status, weight),
                                        INDEX idx_city (province, city, status),
                                        INDEX idx_hot (is_hot, weight),
                                        INDEX idx_new (is_new, created_at),
                                        INDEX idx_price (min_price, max_price),
                                        INDEX idx_season (season),
                                        INDEX idx_status_published (status, published_at),
                                        INDEX idx_location (longitude, latitude),
                                        FULLTEXT INDEX ft_content (name, summary, content) WITH PARSER ngram  -- 全文索引，用于搜索
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页旅游推荐内容表';

-- ======================================================
-- 插入示例数据
-- ======================================================

INSERT INTO home_recommendations (
    uuid, name, english_name, category_id, category_name, tags,
    province, city, district, address, transportation,
    recommendation_reason, highlights, best_travel_time, visit_duration,
    cover_image_url, image_urls,
    ticket_price_description, min_price, max_price, is_free,
    opening_hours_weekday, opening_hours_weekend, opening_hours_description,
    contact_phone, official_website,
    weight, is_hot, is_new, status, season,
    tips, suitable_crowd, content, summary
) VALUES
      (
          UUID(), '故宫博物院', 'The Palace Museum', 2, '历史文化', '世界遗产,拍照圣地,亲子游,研学',
          '北京市', '北京市', '东城区', '景山前街4号', '地铁1号线天安门东站A口出，步行约10分钟',
          '穿越六百年时光，感受明清两代的皇家气派。故宫不仅是中国古代建筑的精髓，更是中华文明的瑰宝。',
          '三大殿太和殿中和殿保和殿;珍宝馆;钟表馆;故宫角楼;景山俯瞰全景',
          '春秋两季（4月-5月，9月-10月）', '半天至一天',
          'https://example.com/images/gugong_cover.jpg',
          '["https://example.com/images/gugong_1.jpg", "https://example.com/images/gugong_2.jpg"]',
          '旺季60元/人，淡季40元/人，珍宝馆和钟表馆需另购票', 40.00, 60.00, FALSE,
          '08:30-17:00', '08:30-17:00', '每周一闭馆（法定节假日除外）',
          '010-85007421', 'https://www.dpm.org.cn',
          100, TRUE, FALSE, 1, 'autumn',
          '建议提前网上预约购票，携带身份证入场。避开节假日高峰期，可租讲解器或请导游。',
          '历史文化爱好者,亲子家庭,摄影爱好者',
          '故宫博物院建立于1925年，是在明朝、清朝两代皇宫及其收藏的基础上建立起来的中国综合性博物馆。',
          '穿越六百年时光，探寻紫禁城的秘密'
      ),
      (
          UUID(), '成都大熊猫繁育研究基地', 'Chengdu Research Base of Giant Panda Breeding', 1, '自然风光', '熊猫,亲子,动物保护,网红',
          '四川省', '成都市', '成华区', '熊猫大道1375号', '地铁3号线熊猫大道站A口出，转乘景区直通车',
          '近距离观察国宝大熊猫的日常生活，看软萌的熊猫宝宝嬉戏玩耍，是成都必打卡的亲子胜地。',
          '月亮产房看熊猫幼崽;太阳产房;成年熊猫别墅;小熊猫活动区;熊猫博物馆',
          '全年（最佳是春秋季，上午熊猫最活跃）', '3-4小时',
          'https://example.com/images/panda_cover.jpg',
          '["https://example.com/images/panda_1.jpg", "https://example.com/images/panda_2.jpg"]',
          '成人票55元，学生票27元', 27.00, 55.00, FALSE,
          '07:30-18:00', '07:30-18:00', '全年开放',
          '028-83510033', 'http://www.panda.org.cn',
          95, TRUE, TRUE, 1, 'spring',
          '尽量上午8-10点前往，此时熊猫最活跃，是喂食时间。穿舒适的鞋子，园区较大。',
          '亲子家庭,动物爱好者,自然爱好者',
          '成都大熊猫繁育研究基地是一个专门保护和繁育大熊猫、小熊猫等珍稀动物的机构，也是全球最大的大熊猫人工繁育机构。',
          '近距离感受国宝熊猫的萌力暴击'
      );

create table safetytips
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    title       varchar(100)                       not null comment '标题',
    description text                               not null comment '描述内容',
    image_url   varchar(255)                       null comment '图片URL',
    category_id bigint                             null comment '分类ID',
    sort_weight int      default 0                 null comment '排序权重(0-999)',
    status      tinyint  default 1                 null comment '状态(0-禁用,1-启用)',
    creator_id  bigint                             null comment '创建人ID',
    updater_id  bigint                             null comment '更新人ID',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '安全知识提示表' charset = utf8mb4
                             row_format = DYNAMIC;

create index idx_category
    on safetytips (category_id);

create index idx_create_time
    on safetytips (create_time);

create index idx_sort
    on safetytips (sort_weight);

create index idx_status
    on safetytips (status);


INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (31, '旅游前的准备工作', '旅游前要检查身份证、护照等证件，准备好常用药品，了解目的地的天气情况...', 'https://example.com/safety1.jpg', null, 1, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (32, '在景区游玩的安全注意事项', '在景区游玩时要遵守景区规定，不要离开指定路线，注意防滑防摔...', 'https://example.com/safety2.jpg', null, 2, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (33, '食品安全注意事项', '在旅游过程中要注意饮食卫生，避免食用生冷食物，选择正规餐厅...', 'https://example.com/safety3.jpg', null, 3, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (34, '交通安全注意事项', '在旅游过程中要遵守交通规则，选择正规的交通工具，系好安全带...', 'https://example.com/safety4.jpg', null, 4, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (35, '财物安全注意事项', '在旅游过程中要保管好个人财物，避免携带大量现金，注意防盗防骗...', 'https://example.com/safety5.jpg', null, 5, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (36, '应急处理方法', '在旅游过程中如果遇到紧急情况，要保持冷静，及时拨打当地的紧急救援电话...', 'https://example.com/safety6.jpg', null, 6, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (37, '高原旅游注意事项', '在高原地区旅游时要注意预防高原反应，避免剧烈运动，保持充足的睡眠...', 'https://example.com/safety7.jpg', null, 7, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (38, '水上活动安全注意事项', '在进行水上活动时要穿戴好救生设备，听从教练的指导，不要独自下水...', 'https://example.com/safety8.jpg', null, 8, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (39, '登山安全注意事项', '在登山时要选择合适的装备，遵循登山路线，注意天气变化...', 'https://example.com/safety9.jpg', null, 9, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
INSERT INTO db_minecraft.safetytips (id, title, description, image_url, category_id, sort_weight, status, creator_id, updater_id, create_time, update_time) VALUES (40, '夜间出行安全注意事项', '在夜间出行时要选择明亮的路线，避免前往偏僻的地方，注意自身安全...', 'https://example.com/safety10.jpg', null, 10, 1, null, null, '2025-12-21 12:02:13', '2025-12-21 12:02:13');
