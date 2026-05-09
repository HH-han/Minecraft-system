-- 创建积分系统相关表

-- 商品表
CREATE TABLE IF NOT EXISTS `points_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `points_price` int NOT NULL COMMENT '所需积分',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片地址',
  `status` tinyint DEFAULT '1' COMMENT '状态 0:下架 1:上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 兑换订单表
CREATE TABLE IF NOT EXISTS `exchange_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(100) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `points_used` int NOT NULL COMMENT '使用积分',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `address` varchar(500) DEFAULT NULL COMMENT '收货地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `receiver` varchar(50) DEFAULT NULL COMMENT '收货人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分记录表
CREATE TABLE IF NOT EXISTS `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '变动积分',
  `before_points` int NOT NULL COMMENT '变动前积分',
  `after_points` int NOT NULL COMMENT '变动后积分',
  `type` varchar(20) NOT NULL COMMENT '类型',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 为用户表添加积分字段
ALTER TABLE `user` ADD COLUMN `points` int DEFAULT '0' COMMENT '积分' AFTER `experience`;

-- 插入测试数据
INSERT INTO `points_products` (`name`, `description`, `points_price`, `stock`, `image_url`) VALUES
('小米手环', '智能运动手环', 500, 100, 'https://via.placeholder.com/300x200'),
('蓝牙耳机', '无线蓝牙耳机', 800, 50, 'https://via.placeholder.com/300x200'),
('充电宝', '大容量移动电源', 300, 200, 'https://via.placeholder.com/300x200');
