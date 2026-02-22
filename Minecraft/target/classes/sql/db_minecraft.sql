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
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简介',
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


