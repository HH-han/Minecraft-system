-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: db_minecraft
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attraction`
--

DROP TABLE IF EXISTS `attraction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attraction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景点名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片列表',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `season` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '季节',
  `rating` int DEFAULT '0' COMMENT '评分',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签',
  `status` int DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_city` (`city`) USING BTREE,
  KEY `idx_season` (`season`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE,
  KEY `idx_like_count` (`like_count`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='景点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attraction`
--

LOCK TABLES `attraction` WRITE;
INSERT INTO `attraction` (`id`, `name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `season`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`, `create_time`, `update_time`) VALUES (4,'故宫博物院','北京','北京','北京市东城区景山前街4号','中国明清两代的皇家宫殿，世界上现存规模最大、保存最为完整的木质结构古建筑之一。','http://localhost:8080/upload/49c46ccc-c693-45cd-aea0-b51172f1e260.png','[\"https://example.com/attraction1_1.jpg\", \"https://example.com/attraction1_2.jpg\"]',60.00,'四季皆宜',5,1200,800,350,'文化遗产,历史,建筑',1,'2026-02-14 17:21:56','2026-03-09 23:18:18'),(5,'长城','北京','北京','北京市怀柔区','中国古代的军事防御工程，是世界文化遗产之一。','http://localhost:8080/upload/a65a8058-e67c-46b6-9627-9b53c8b5a6d5.png','[\"https://example.com/attraction2_1.jpg\", \"https://example.com/attraction2_2.jpg\"]',40.00,'春秋',5,1500,900,420,'文化遗产,历史,自然',1,'2026-02-14 17:21:56','2026-04-05 14:57:39'),(6,'西湖','杭州','浙江','浙江省杭州市西湖区','中国古典园林代表作，世界文化遗产。','http://localhost:8080/upload/d2678cf0-e445-4e56-83e0-6dc774605a9f.png','[\"https://example.com/attraction3_1.jpg\", \"https://example.com/attraction3_2.jpg\"]',0.00,'四季皆宜',5,1800,1200,500,'文化遗产,自然,湖泊',1,'2026-02-14 17:21:56','2026-03-29 17:41:32'),(7,'黄山','黄山','安徽','安徽省黄山市黄山区','以奇松、怪石、云海、温泉、冬雪\"五绝\"著称于世。','https://example.com/attraction4.jpg','[\"https://example.com/attraction4_1.jpg\", \"https://example.com/attraction4_2.jpg\"]',230.00,'春夏秋',5,1600,1000,450,'自然,山岳,云海',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,'九寨沟','阿坝','四川','四川省阿坝藏族羌族自治州九寨沟县','以翠海、叠瀑、彩林、雪峰、藏情、蓝冰\"六绝\"著称。','https://example.com/attraction5.jpg','[\"https://example.com/attraction5_1.jpg\", \"https://example.com/attraction5_2.jpg\"]',220.00,'秋季',5,1400,950,400,'自然,湖泊,瀑布',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,'兵马俑','西安','陕西','陕西省西安市临潼区秦始皇陵以东1.5公里处','世界文化遗产，被誉为\"世界第八大奇迹\"。','https://example.com/attraction6.jpg','[\"https://example.com/attraction6_1.jpg\", \"https://example.com/attraction6_2.jpg\"]',150.00,'四季皆宜',5,1300,850,380,'文化遗产,历史,考古',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'三亚湾','三亚','海南','海南省三亚市三亚湾路','绵长的海岸线，椰梦长廊，是著名的海滨度假区。','http://localhost:8080/upload/b3b2a9cf-3ab6-4fad-84eb-bde83bc31cb3.png','[\"https://example.com/attraction7_1.jpg\", \"https://example.com/attraction7_2.jpg\"]',0.00,'冬季',4,1100,750,320,'海滨,度假,自然',1,'2026-02-14 17:21:56','2026-03-15 15:00:52'),(11,'张家界','张家界','湖南','湖南省张家界市武陵源区','以张家界国家森林公园、天门山等著名景点著称。','https://example.com/attraction8.jpg','[\"https://example.com/attraction8_1.jpg\", \"https://example.com/attraction8_2.jpg\"]',248.00,'春夏秋',5,1550,1050,480,'自然,山岳,森林公园',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,'颐和园','北京','北京','北京市海淀区新建宫门路19号','中国现存规模最大、保存最完整的皇家园林。','https://example.com/attraction9.jpg','[\"https://example.com/attraction9_1.jpg\", \"https://example.com/attraction9_2.jpg\"]',30.00,'四季皆宜',4,900,600,280,'文化遗产,园林,历史',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(13,'鼓浪屿','厦门','福建','福建省厦门市思明区鼓浪屿','有着\"海上花园\"的美誉，是国家5A级旅游景区。','https://example.com/attraction10.jpg','[\"https://example.com/attraction10_1.jpg\", \"https://example.com/attraction10_2.jpg\"]',80.00,'四季皆宜',4,1000,700,300,'海岛,历史,建筑',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `attraction_images`
--

DROP TABLE IF EXISTS `attraction_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attraction_images` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图URL',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片描述',
  `photographer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摄影师',
  `license` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版权信息',
  `is_primary` tinyint(1) DEFAULT '0' COMMENT '是否是主图',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `width` int DEFAULT NULL COMMENT '图片宽度',
  `height` int DEFAULT NULL COMMENT '图片高度',
  `file_size` int DEFAULT NULL COMMENT '文件大小(KB)',
  `upload_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_attraction_id` (`attraction_id`) USING BTREE,
  CONSTRAINT `fk_attraction_images_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attraction_images`
--

LOCK TABLES `attraction_images` WRITE;
INSERT INTO `attraction_images` (`id`, `attraction_id`, `image_url`, `thumbnail_url`, `title`, `description`, `photographer`, `license`, `is_primary`, `sort_order`, `width`, `height`, `file_size`, `upload_time`) VALUES (1,1,'https://example.com/greatwall1.jpg','https://example.com/greatwall1_thumb.jpg','长城远景','中国长城的壮丽远景','Zhang Wei','CC BY-SA 4.0',1,1,1920,1080,2048,'2026-02-14 09:23:52'),(2,1,'https://example.com/greatwall2.jpg','https://example.com/greatwall2_thumb.jpg','长城近景','长城的砖石结构','Li Ming','CC BY-SA 4.0',0,2,1920,1080,1800,'2026-02-14 09:23:52'),(3,2,'https://example.com/forbidden_city1.jpg','https://example.com/forbidden_city1_thumb.jpg','故宫全景','故宫的鸟瞰图','Wang Hua','CC BY-SA 4.0',1,1,1920,1080,2200,'2026-02-14 09:23:52'),(4,2,'https://example.com/forbidden_city2.jpg','https://example.com/forbidden_city2_thumb.jpg','故宫宫殿','故宫的太和殿','Zhao Yang','CC BY-SA 4.0',0,2,1920,1080,1900,'2026-02-14 09:23:52'),(5,3,'https://example.com/eiffel_tower1.jpg','https://example.com/eiffel_tower1_thumb.jpg','埃菲尔铁塔','埃菲尔铁塔的正面照','Jean Dupont','CC BY-SA 4.0',1,1,1920,1080,2100,'2026-02-14 09:23:52'),(6,3,'https://example.com/eiffel_tower2.jpg','https://example.com/eiffel_tower2_thumb.jpg','埃菲尔铁塔夜景','夜晚的埃菲尔铁塔','Pierre Martin','CC BY-SA 4.0',0,2,1920,1080,2300,'2026-02-14 09:23:52'),(7,4,'https://example.com/statue_of_liberty1.jpg','https://example.com/statue_of_liberty1_thumb.jpg','自由女神像','自由女神像的正面照','John Smith','CC BY-SA 4.0',1,1,1920,1080,2000,'2026-02-14 09:23:52'),(8,4,'https://example.com/statue_of_liberty2.jpg','https://example.com/statue_of_liberty2_thumb.jpg','自由女神像远景','自由女神像的远景','Jane Doe','CC BY-SA 4.0',0,2,1920,1080,1850,'2026-02-14 09:23:52'),(9,5,'https://example.com/tokyo_tower1.jpg','https://example.com/tokyo_tower1_thumb.jpg','东京塔','东京塔的正面照','Tanaka Yuki','CC BY-SA 4.0',1,1,1920,1080,1950,'2026-02-14 09:23:52'),(10,5,'https://example.com/tokyo_tower2.jpg','https://example.com/tokyo_tower2_thumb.jpg','东京塔夜景','夜晚的东京塔','Sato Hiroshi','CC BY-SA 4.0',0,2,1920,1080,2150,'2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `attraction_tags`
--

DROP TABLE IF EXISTS `attraction_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attraction_tags` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '景点-标签关联ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `tag_id` int NOT NULL COMMENT '标签ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_attraction_tag` (`attraction_id`,`tag_id`) USING BTREE,
  KEY `idx_attraction_id` (`attraction_id`) USING BTREE,
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  CONSTRAINT `fk_attraction_tags_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_attraction_tags_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='景点-标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attraction_tags`
--

LOCK TABLES `attraction_tags` WRITE;
INSERT INTO `attraction_tags` (`id`, `attraction_id`, `tag_id`, `created_at`) VALUES (1,1,1,'2026-02-14 09:23:52'),(2,1,5,'2026-02-14 09:23:52'),(3,1,8,'2026-02-14 09:23:52'),(4,2,1,'2026-02-14 09:23:52'),(5,2,4,'2026-02-14 09:23:52'),(6,2,6,'2026-02-14 09:23:52'),(7,3,3,'2026-02-14 09:23:52'),(8,3,6,'2026-02-14 09:23:52'),(9,3,8,'2026-02-14 09:23:52'),(10,4,3,'2026-02-14 09:23:52'),(11,4,4,'2026-02-14 09:23:52'),(12,4,6,'2026-02-14 09:23:52'),(13,5,3,'2026-02-14 09:23:52'),(14,5,4,'2026-02-14 09:23:52'),(15,5,9,'2026-02-14 09:23:52'),(16,6,3,'2026-02-14 09:23:52'),(17,6,4,'2026-02-14 09:23:52'),(18,6,8,'2026-02-14 09:23:52'),(19,7,2,'2026-02-14 09:23:52'),(20,7,5,'2026-02-14 09:23:52'),(21,7,7,'2026-02-14 09:23:52'),(22,8,3,'2026-02-14 09:23:52'),(23,8,6,'2026-02-14 09:23:52'),(24,8,8,'2026-02-14 09:23:52'),(25,9,1,'2026-02-14 09:23:52'),(26,9,4,'2026-02-14 09:23:52'),(27,9,9,'2026-02-14 09:23:52'),(28,10,1,'2026-02-14 09:23:52'),(29,10,4,'2026-02-14 09:23:52'),(30,10,6,'2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `attractions`
--

DROP TABLE IF EXISTS `attractions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attractions` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景点名称',
  `chinese_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `city_id` int NOT NULL COMMENT '所在城市ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型(古迹/自然/现代建筑等)',
  `rating` decimal(2,1) DEFAULT NULL COMMENT '评分(1-5)',
  `price_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消费水平(免费/便宜/中等/昂贵)',
  `visiting_hours` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开放时间',
  `best_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最佳游览季节',
  `visit_duration` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '建议游览时长',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '详细描述',
  `history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '历史背景',
  `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '游玩建议',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '具体地址',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '经度',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主图URL',
  `official_website` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '官方网站',
  `year_built` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '建造年份',
  `architect` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '建筑师',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_city_id` (`city_id`) USING BTREE,
  CONSTRAINT `fk_attractions_city` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='景点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attractions`
--

LOCK TABLES `attractions` WRITE;
INSERT INTO `attractions` (`id`, `name`, `chinese_name`, `city_id`, `type`, `rating`, `price_level`, `visiting_hours`, `best_season`, `visit_duration`, `description`, `history`, `tips`, `address`, `latitude`, `longitude`, `image_url`, `official_website`, `year_built`, `architect`, `created_at`, `updated_at`) VALUES (1,'Great Wall of China','中国长城',1,'historical',4.8,'中等','08:00-17:00','春秋','3-4小时','中国长城是世界上最伟大的建筑之一，也是世界文化遗产。','长城始建于春秋战国时期，是中国古代为了防御北方游牧民族入侵而修建的。','建议早上早点去，避开人流高峰；穿舒适的鞋子，因为需要走很多台阶。','北京市怀柔区',40.43190000,116.57040000,'https://example.com/greatwall.jpg','https://www.badaling.gov.cn/','公元前7世纪','秦始皇','2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,'Forbidden City','故宫',1,'historical',4.9,'中等','08:30-17:00','四季皆宜','4-5小时','故宫是中国明清两代的皇家宫殿，是世界上现存规模最大、保存最为完整的木质结构古建筑之一。','故宫始建于明永乐四年（1406年），是中国古代宫廷建筑的精华。','建议预留充足的时间游览；可以租讲解器了解更多历史；注意保护文物，不要触摸展品。','北京市东城区景山前街4号',39.91630000,116.39720000,'https://example.com/forbidden_city.jpg','https://www.dpm.org.cn/','1420年','蒯祥','2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,'Eiffel Tower','埃菲尔铁塔',5,'modern',4.7,'中等','09:00-23:45','春秋','2-3小时','埃菲尔铁塔是法国巴黎的标志性建筑，也是世界著名的建筑之一。','埃菲尔铁塔建于1889年，是为了纪念法国大革命100周年而建造的。','建议提前网上购票，避免排队；黄昏时分登塔可以看到美丽的日落；塔顶风大，注意保暖。','法国巴黎战神广场',48.85840000,2.29450000,'https://example.com/eiffel_tower.jpg','https://www.toureiffel.paris/','1889年','古斯塔夫·埃菲尔','2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,'Statue of Liberty','自由女神像',4,'monument',4.6,'中等','09:00-17:00','春秋','2-3小时','自由女神像是美国的标志性建筑，象征着自由和民主。','自由女神像是法国赠送给美国的礼物，于1886年10月28日落成。','建议提前网上购票，尤其是 crown access；需要乘船前往，注意天气情况；可以参观女神像内部和观景台。','美国纽约自由岛',40.68920000,-74.04450000,'https://example.com/statue_of_liberty.jpg','https://www.nps.gov/stli/index.htm','1886年','弗雷德里克·奥古斯特·巴托尔迪','2026-02-14 09:23:52','2026-02-14 09:23:52'),(5,'Tokyo Tower','东京塔',3,'modern',4.5,'便宜','09:00-22:00','四季皆宜','1-2小时','东京塔是日本东京的标志性建筑，高333米。','东京塔建于1958年，是为了纪念日本战后复兴而建造的。','建议黄昏时分登塔，可以看到东京的日景和夜景；塔顶有咖啡厅，可以休息和观景；可以购买纪念品。','日本东京都港区芝公园',35.65860000,139.74540000,'https://example.com/tokyo_tower.jpg','https://www.tokyotower.co.jp/','1958年','内藤多仲','2026-02-14 09:23:52','2026-02-14 09:23:52'),(6,'Sydney Opera House','悉尼歌剧院',6,'modern',4.8,'中等','09:00-17:00','春秋','1-2小时','悉尼歌剧院是澳大利亚悉尼的标志性建筑，也是世界著名的建筑之一。','悉尼歌剧院建于1973年，是20世纪最具特色的建筑之一。','建议参加导览团，了解建筑历史和内部结构；可以观看演出；周边有很多餐厅和咖啡馆。','澳大利亚悉尼本尼朗角',-33.85680000,151.21530000,'https://example.com/sydney_opera_house.jpg','https://www.sydneyoperahouse.com/','1973年','约恩·乌松','2026-02-14 09:23:52','2026-02-14 09:23:52'),(7,'Table Mountain','桌山',7,'natural',4.9,'中等','08:00-20:00','春秋','3-4小时','桌山是南非开普敦的标志性自然景观，山顶平坦如桌。','桌山是开普敦的象征，也是世界自然遗产。','建议乘坐缆车上下山，节省体力；山顶风大，注意保暖；可以徒步登山，但需要一定的体力和时间。','南非开普敦',-33.96280000,18.40360000,'https://example.com/table_mountain.jpg','https://www.tablemountain.net/',NULL,NULL,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(8,'Christ the Redeemer','基督像',8,'monument',4.7,'便宜','08:00-19:00','冬春','1-2小时','基督像是巴西里约热内卢的标志性建筑，高30米。','基督像建于1931年，是为了纪念巴西独立100周年而建造的。','建议早上或下午前往，避开中午的高温；可以乘坐小火车或徒步登山；山顶风大，注意安全。','巴西里约热内卢科尔科瓦多山',-22.95190000,-43.21050000,'https://example.com/christ_the_redeemer.jpg','https://www.corcovado.com.br/','1931年','保罗·兰多斯基','2026-02-14 09:23:52','2026-02-14 09:23:52'),(9,'Brandenburg Gate','勃兰登堡门',9,'historical',4.6,'免费','全天开放','春秋','0.5-1小时','勃兰登堡门是德国柏林的标志性建筑，也是德国统一的象征。','勃兰登堡门建于1791年，是柏林的象征。','建议黄昏时分前往，灯光效果很美；周边有很多历史景点，可以一并游览；注意个人财物安全。','德国柏林市中心',52.51630000,13.37770000,'https://example.com/brandenburg_gate.jpg','https://www.visitberlin.de/en/brandenburg-gate','1791年','卡尔·戈特哈德·朗汉斯','2026-02-14 09:23:52','2026-02-14 09:23:52'),(10,'Taj Mahal','泰姬陵',10,'historical',4.9,'中等','06:00-19:00','冬','2-3小时','泰姬陵是印度的标志性建筑，也是世界文化遗产。','泰姬陵建于1653年，是莫卧儿皇帝沙贾汗为纪念他的妻子穆姆塔兹·玛哈尔而建造的。','建议早上日出时分前往，可以看到泰姬陵的美丽光影；注意着装得体，尊重当地文化；可以聘请导游了解更多历史。','印度阿格拉',27.17510000,78.04210000,'https://example.com/taj_mahal.jpg','https://www.tajmahal.gov.in/','1653年','乌斯塔德·艾哈迈德·拉合里','2026-02-14 09:23:52','2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `call_records`
--

DROP TABLE IF EXISTS `call_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `call_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `call_id` varchar(64) NOT NULL COMMENT '通话唯一标识',
  `caller_id` bigint NOT NULL COMMENT '发起方用户ID',
  `receiver_id` bigint NOT NULL COMMENT '接收方用户ID',
  `call_type` varchar(10) NOT NULL COMMENT '通话类型: voice/video',
  `status` varchar(20) NOT NULL COMMENT '通话状态',
  `duration` int DEFAULT '0' COMMENT '通话时长(秒)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `call_id` (`call_id`),
  KEY `idx_call_id` (`call_id`),
  KEY `idx_caller_id` (`caller_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通话记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `call_records`
--

LOCK TABLES `call_records` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `carousel_group_items`
--

DROP TABLE IF EXISTS `carousel_group_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carousel_group_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` int NOT NULL COMMENT '分组ID',
  `carousel_id` int NOT NULL COMMENT '轮播ID',
  `sort_order` int DEFAULT '0' COMMENT '在分组内的排序',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '在分组内是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_group_carousel` (`group_id`,`carousel_id`),
  KEY `carousel_id` (`carousel_id`),
  CONSTRAINT `carousel_group_items_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `carousel_groups` (`id`) ON DELETE CASCADE,
  CONSTRAINT `carousel_group_items_ibfk_2` FOREIGN KEY (`carousel_id`) REFERENCES `carousels` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='轮播分组项关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carousel_group_items`
--

LOCK TABLES `carousel_group_items` WRITE;
INSERT INTO `carousel_group_items` (`id`, `group_id`, `carousel_id`, `sort_order`, `is_active`, `created_at`) VALUES (1,1,1,1,1,'2026-02-14 09:30:45'),(2,1,2,2,1,'2026-02-14 09:30:45'),(3,1,3,3,1,'2026-02-14 09:30:45'),(4,2,4,1,1,'2026-02-14 09:30:45'),(5,2,5,2,1,'2026-02-14 09:30:45'),(6,3,6,1,1,'2026-02-14 09:30:45'),(7,3,7,2,1,'2026-02-14 09:30:45'),(8,4,8,1,1,'2026-02-14 09:30:45');
UNLOCK TABLES;

--
-- Table structure for table `carousel_groups`
--

DROP TABLE IF EXISTS `carousel_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carousel_groups` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '分组名称',
  `group_code` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '分组代码(用于前端调用)',
  `description` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分组描述',
  `display_type` enum('single','multiple','fullscreen') COLLATE utf8mb4_bin DEFAULT 'multiple' COMMENT '展示类型',
  `auto_play` tinyint(1) DEFAULT '1' COMMENT '是否自动播放',
  `auto_play_interval` int DEFAULT '5000' COMMENT '自动播放间隔(毫秒)',
  `animation_type` enum('slide','fade','cube','coverflow') COLLATE utf8mb4_bin DEFAULT 'slide' COMMENT '切换动画',
  `show_indicators` tinyint(1) DEFAULT '1' COMMENT '是否显示指示点',
  `show_arrows` tinyint(1) DEFAULT '1' COMMENT '是否显示箭头',
  `pause_on_hover` tinyint(1) DEFAULT '1' COMMENT '鼠标悬停时暂停',
  `width` int DEFAULT NULL COMMENT '宽度(像素)',
  `height` int DEFAULT NULL COMMENT '高度(像素)',
  `aspect_ratio` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '宽高比(如16:9)',
  `max_items` int DEFAULT '10' COMMENT '最大显示数量',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_code` (`group_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='轮播分组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carousel_groups`
--

LOCK TABLES `carousel_groups` WRITE;
INSERT INTO `carousel_groups` (`id`, `group_name`, `group_code`, `description`, `display_type`, `auto_play`, `auto_play_interval`, `animation_type`, `show_indicators`, `show_arrows`, `pause_on_hover`, `width`, `height`, `aspect_ratio`, `max_items`, `is_active`, `sort_order`, `created_at`, `updated_at`) VALUES (1,'首页顶部轮播','home_top','网站首页顶部主轮播','multiple',1,5000,'slide',1,1,1,NULL,600,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(2,'首页中部轮播','home_middle','首页中部推荐轮播','multiple',1,4000,'slide',1,1,1,NULL,400,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(3,'景点详情页轮播','attraction_detail','景点详情页顶部轮播','single',1,3000,'slide',1,1,1,NULL,500,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(4,'APP开屏轮播','app_splash','移动端开屏广告轮播','fullscreen',1,3000,'slide',1,1,1,NULL,800,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45');
UNLOCK TABLES;

--
-- Table structure for table `carousel_stats`
--

DROP TABLE IF EXISTS `carousel_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carousel_stats` (
  `id` int NOT NULL AUTO_INCREMENT,
  `carousel_id` int NOT NULL COMMENT '轮播ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `click_count` int DEFAULT '0' COMMENT '当日点击次数',
  `impression_count` int DEFAULT '0' COMMENT '当日展示次数',
  `unique_click_count` int DEFAULT '0' COMMENT '独立IP点击次数',
  `unique_impression_count` int DEFAULT '0' COMMENT '独立IP展示次数',
  `pc_click_count` int DEFAULT '0' COMMENT 'PC端点击',
  `mobile_click_count` int DEFAULT '0' COMMENT '移动端点击',
  `pc_impression_count` int DEFAULT '0' COMMENT 'PC端展示',
  `mobile_impression_count` int DEFAULT '0' COMMENT '移动端展示',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_carousel_date` (`carousel_id`,`stat_date`),
  CONSTRAINT `carousel_stats_ibfk_1` FOREIGN KEY (`carousel_id`) REFERENCES `carousels` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='轮播统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carousel_stats`
--

LOCK TABLES `carousel_stats` WRITE;
INSERT INTO `carousel_stats` (`id`, `carousel_id`, `stat_date`, `click_count`, `impression_count`, `unique_click_count`, `unique_impression_count`, `pc_click_count`, `mobile_click_count`, `pc_impression_count`, `mobile_impression_count`, `created_at`, `updated_at`) VALUES (1,1,'2025-02-01',1250,15000,0,0,800,450,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(2,1,'2025-02-02',1350,16200,0,0,850,500,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(3,2,'2025-02-01',980,12000,0,0,600,380,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(4,3,'2025-02-01',2100,25000,0,0,1200,900,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45');
UNLOCK TABLES;

--
-- Table structure for table `carousels`
--

DROP TABLE IF EXISTS `carousels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carousels` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '轮播标题',
  `subtitle` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '副标题/描述',
  `image_url` varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '图片URL',
  `mobile_image_url` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '移动端图片URL(适配手机)',
  `thumbnail_url` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '缩略图URL',
  `link_type` enum('none','url','attraction','city','country','custom_page') COLLATE utf8mb4_bin DEFAULT 'none' COMMENT '链接类型',
  `link_url` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '跳转链接URL',
  `link_target` enum('_self','_blank') COLLATE utf8mb4_bin DEFAULT '_self' COMMENT '链接打开方式',
  `target_id` int DEFAULT NULL COMMENT '目标ID(当link_type为attraction/city/country时使用)',
  `button_text` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '按钮文字',
  `button_color` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '按钮颜色',
  `text_color` varchar(20) COLLATE utf8mb4_bin DEFAULT '#FFFFFF' COMMENT '文字颜色',
  `text_shadow` tinyint(1) DEFAULT '1' COMMENT '文字阴影',
  `overlay_color` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '遮罩层颜色',
  `overlay_opacity` decimal(2,1) DEFAULT '0.3' COMMENT '遮罩层透明度(0-1)',
  `position` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '展示位置(home/home_top/home_middle/home_bottom/attraction_page等)',
  `device_type` enum('all','pc','mobile') COLLATE utf8mb4_bin DEFAULT 'all' COMMENT '适配设备',
  `user_type` enum('all','visitor','logged_in','vip') COLLATE utf8mb4_bin DEFAULT 'all' COMMENT '用户类型',
  `start_time` datetime DEFAULT NULL COMMENT '开始展示时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束展示时间',
  `is_always_show` tinyint(1) DEFAULT '1' COMMENT '是否永久展示',
  `click_count` int DEFAULT '0' COMMENT '点击次数',
  `impression_count` int DEFAULT '0' COMMENT '展示次数',
  `sort_order` int DEFAULT '0' COMMENT '排序(数字越小越靠前)',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(软删除)',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `created_by` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_position_active` (`position`,`is_active`,`sort_order`),
  KEY `idx_time_range` (`start_time`,`end_time`),
  KEY `idx_click_count` (`click_count`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carousels`
--

LOCK TABLES `carousels` WRITE;
INSERT INTO `carousels` (`id`, `title`, `subtitle`, `image_url`, `mobile_image_url`, `thumbnail_url`, `link_type`, `link_url`, `link_target`, `target_id`, `button_text`, `button_color`, `text_color`, `text_shadow`, `overlay_color`, `overlay_opacity`, `position`, `device_type`, `user_type`, `start_time`, `end_time`, `is_always_show`, `click_count`, `impression_count`, `sort_order`, `is_active`, `is_deleted`, `remark`, `created_by`, `created_at`, `updated_at`) VALUES (1,'浪漫巴黎之旅','探索埃菲尔铁塔的浪漫与优雅','http://localhost:8080/upload/65dba708-6f3e-4367-9371-3d14f8f2ca0b.png','/images/carousel/paris_mobile.jpg',NULL,'attraction',NULL,'_self',1,'立即预订',NULL,'#FFFFFF',1,NULL,0.3,'home_top','all','all','2025-01-01 00:00:00','2025-12-31 23:59:00',1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:23:56'),(2,'古罗马文明','穿越千年，感受斗兽场的辉煌','http://localhost:8080/upload/f272c7a6-a6a8-4be2-9a2c-75d33652e8f4.png','/images/carousel/rome_mobile.jpg',NULL,'attraction',NULL,'_self',2,'了解更多',NULL,'#FFD700',1,NULL,0.4,'home_top','all','all',NULL,NULL,1,0,0,2,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:21:39'),(3,'纽约不夜城','自由女神像俯瞰曼哈顿','http://localhost:8080/upload/92096a7a-1e61-439d-b083-5c865d3c4f8d.png','/images/carousel/nyc_mobile.jpg',NULL,'attraction',NULL,'_self',3,'查看详情',NULL,'#FFFFFF',1,NULL,0.2,'attraction','all','all',NULL,NULL,1,0,0,3,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:21:39'),(4,'暑期特惠','欧洲五国游限时8折优惠','http://localhost:8080/upload/7b966a24-32cb-410a-9a1d-c2ba1c0cefb1.png','/images/carousel/summer_sale_mobile.jpg',NULL,'attraction','https://example.com/summer-sale','_self',NULL,'立即抢购',NULL,'#FF0000',1,NULL,0.2,'home_top','all','all','2025-06-01 00:00:00','2025-08-31 23:59:59',1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:23:00'),(5,'亲子游推荐','最适合带娃的十大景点','http://localhost:8080/upload/f9d5edf7-5b9e-4fbf-a331-0d14b66ee33d.png','/images/carousel/family_mobile.jpg',NULL,'attraction','/family-travel','_self',NULL,'查看推荐',NULL,'#4CAF50',1,NULL,0.3,'destination','all','all',NULL,NULL,1,0,0,2,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:23:00'),(6,'埃菲尔铁塔全景','360度俯瞰巴黎市区','http://localhost:8080/upload/5b3ee4e2-9a69-4606-84ac-f69960a25c1c.png','/images/attractions/eiffel/detail_1_mobile.jpg',NULL,'attraction',NULL,'_self',1,'查看详情',NULL,'#FFFFFF',1,NULL,0.2,'souvenir','all','all',NULL,NULL,1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(7,'埃菲尔铁塔夜景','璀璨灯光秀每晚上演','http://localhost:8080/upload/9f14736f-71b8-477d-b89f-cecb7251481f.png','/images/attractions/eiffel/detail_2_mobile.jpg',NULL,'attraction',NULL,'_self',1,'查看详情',NULL,'#FFFFFF',1,NULL,0.3,'souvenir','all','all',NULL,NULL,1,0,0,2,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(8,'APP限时福利','新用户注册立减100元','http://localhost:8080/upload/66337df7-e754-4921-89c5-20fb12bf80db.png','/images/app_splash/splash_1_mobile.jpg',NULL,'url','https://example.com/download','_self',NULL,'立即下载',NULL,'#FFFFFF',1,NULL,0.2,'destination','all','all','2025-01-01 00:00:00','2025-03-31 23:59:00',1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(9,'夏日狂欢节','清凉一夏，水上乐园特惠','http://localhost:8080/upload/601c3402-1980-4c6c-bb9b-58e4c9bc70f1.png','https://images.unsplash.com/photo-1518005068251-37900150dfca','https://images.unsplash.com/photo-1477959858617-67f85cf4f1df?w=200','attraction',NULL,'_self',101,'立即预订','#FF6B6B','#FFFFFF',1,'#000000',0.3,'home_top','all','all','2024-07-01 00:00:00','2024-08-31 23:59:00',0,1250,45800,1,1,0,'夏季主题活动','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(10,'浪漫巴黎','埃菲尔铁塔下的爱情故事','https://images.unsplash.com/photo-1502602898657-3e91760cbb34','https://images.unsplash.com/photo-1499856871958-5b9627545d1a','https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=200','country',NULL,'_self',33,'探索巴黎','#9B59B6','#FFFFFF',1,'#2C3E50',0.4,'destination','all','visitor','2024-01-01 00:00:00','2024-12-31 23:59:00',1,3420,89200,2,1,0,'欧洲热门目的地','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(11,'限时特惠','暑期出游，立减500元','http://localhost:8080/upload/ee45b4f2-296a-4dde-81eb-fed535040988.png','https://images.unsplash.com/photo-1508672019048-805c876b267e','https://images.unsplash.com/photo-1503220317375-aaad61436b1b?w=200','url','https://www.example.com/summer-sale','_blank',NULL,'领取优惠','#E74C3C','#FFFFFF',1,'#34495E',0.2,'hotel','all','logged_in','2024-06-01 00:00:00','2024-07-15 23:59:00',1,890,23400,3,1,0,'暑期促销活动','marketing','2026-03-12 13:55:08','2026-03-12 13:55:08'),(12,'日本樱花季','春日赏樱，浪漫之旅','https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e','https://images.unsplash.com/photo-1526481280693-3bfa7568e0f3','https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=200','country',NULL,'_self',45,'查看详情','#FF9F4A','#FFFFFF',1,'#000000',0.3,'community','pc','all','2024-03-01 00:00:00','2024-04-30 23:59:00',1,2100,56700,4,1,0,'春季赏樱专题','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(13,'上海迪士尼','梦幻王国，童心未泯','http://localhost:8080/upload/dd168290-667b-40ee-8dd7-0874be1bf08e.png','https://images.unsplash.com/photo-1559348346-1f1f5c0f1b0b','https://images.unsplash.com/photo-1522804144733-221e3d3a4d8b?w=200','attraction',NULL,'_self',156,'立即购票','#F1C40F','#2C3E50',0,'#FFFFFF',0.2,'hotel','all','vip','2024-01-01 00:00:00','2024-12-31 23:59:00',1,5600,124500,5,1,0,'热门景点','operator','2026-03-12 13:55:08','2026-03-12 13:55:08'),(14,'北京故宫','穿越千年的文化之旅','http://localhost:8080/upload/5c6a9496-84f3-47ee-8ce0-7d24cfeab1d2.png','https://images.unsplash.com/photo-1581790746931-e6b8c3ef7d3c','https://images.unsplash.com/photo-1557872943-16a5ac26437e?w=200','attraction',NULL,'_self',89,'探索历史','#E67E22','#FFFFFF',1,'#2C3E50',0.3,'home_top','all','visitor','2024-02-01 00:00:00','2024-11-30 23:59:00',1,1890,45300,6,1,0,'文化遗产','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(15,'马尔代夫','天堂海岛，蜜月首选','http://localhost:8080/upload/ac98d60e-5c89-4d8a-8165-fcba814c94c8.png','https://images.unsplash.com/photo-1573843981267-be1999ff37cd','https://images.unsplash.com/photo-1514282401047-d79a71a590e8?w=200','country',NULL,'_self',112,'查看套餐','#3498DB','#FFFFFF',1,'#000000',0.4,'food','mobile','logged_in','2024-05-01 00:00:00','2024-10-31 23:59:00',1,3200,67800,7,1,0,'海岛度假','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(16,'桂林山水','甲天下的山水风光','http://localhost:8080/upload/e63a2954-fb15-499b-8f25-a68db3f3703a.png','https://images.unsplash.com/photo-1527090526205-beaac8dc3c62','https://images.unsplash.com/photo-1528297506724-1b832ed7c9e1?w=200','city',NULL,'_self',234,'立即出发','#27AE60','#FFFFFF',1,'#34495E',0.3,'community','pc','all','2024-04-01 00:00:00','2024-09-30 23:59:00',1,980,32100,8,1,0,'自然风光','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(17,'会员专享','VIP专属优惠，立减1000','http://localhost:8080/upload/09479fe2-e04c-4e78-be90-e480d3723193.png','https://images.unsplash.com/photo-1556742502-ec7c0e9f34b4','https://images.unsplash.com/photo-1556742502-ec7c0e9f34b4?w=200','url','https://www.example.com/vip-offer','_blank',NULL,'成为会员','#8E44AD','#FFFFFF',1,'#000000',0.2,'attraction','all','vip','2024-01-01 00:00:00','2024-12-31 23:59:00',1,450,8900,9,1,0,'会员专属活动','marketing','2026-03-12 13:55:08','2026-03-12 13:55:08'),(18,'成都大熊猫','萌宠来袭，国宝之旅','http://localhost:8080/upload/0e845557-a747-4301-b27f-8e172049859f.png','https://images.unsplash.com/photo-1568571428749-26d1a7e9b762','https://images.unsplash.com/photo-1538098848135-8df3c6db1bf1?w=200','attraction',NULL,'_self',178,'预约参观','#2ECC71','#FFFFFF',0,'#27AE60',0.1,'food','all','all','2024-03-01 00:00:00','2024-08-31 23:59:00',1,2300,48900,10,1,0,'亲子游推荐','operator','2026-03-12 13:55:08','2026-03-12 13:55:08'),(19,'纽约时代广场','不夜城的魅力','http://localhost:8080/upload/0dc479bf-9e25-45b8-8315-a997aec910b1.png','https://images.unsplash.com/photo-1534430480872-3498386e7856','https://images.unsplash.com/photo-1485738422979-f5c462d49f74?w=200','city',NULL,'_self',567,'了解更多','#E67E22','#FFFFFF',1,'#000000',0.4,'food','all','visitor','2024-06-01 00:00:00','2024-12-31 23:59:00',1,1500,28700,11,1,0,'城市观光','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(20,'张家界','阿凡达的奇幻世界','http://localhost:8080/upload/79f89234-86d9-4eed-a2bc-d5a404c306e8.png','https://images.unsplash.com/photo-1545558014-8692077e9b5c','https://images.unsplash.com/photo-1519046904884-53103b34b5b7?w=200','attraction',NULL,'_self',201,'探秘仙境','#9B59B6','#FFFFFF',1,'#34495E',0.3,'home_top','all','logged_in','2024-05-15 00:00:00','2024-10-15 23:59:00',0,1750,39600,12,1,0,'自然奇观','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(21,'春节特辑','阖家欢乐，春节出游','http://localhost:8080/upload/e1adbd4f-aa22-4ab6-8100-636266c8b768.png','https://images.unsplash.com/photo-1545569341-97d0d0c2e2b2','https://images.unsplash.com/photo-1545569341-97d0d0c2e2b2?w=200','none',NULL,'_self',NULL,'查看活动','#E74C3C','#FFD700',1,'#2C3E50',0.2,'home_top','pc','all','2024-01-15 00:00:00','2024-02-15 23:59:00',1,670,15200,13,1,0,'节日活动','marketing','2026-03-12 13:55:08','2026-03-12 13:55:08'),(22,'西安兵马俑','世界第八大奇迹','http://localhost:8080/upload/5a8acf03-12a4-43de-8a8b-3e4fecd0f94d.png','https://images.unsplash.com/photo-1621106036928-6ce0b7d2a7d1','https://images.unsplash.com/photo-1575986767340-5d3b52b1a78b?w=200','attraction',NULL,'_self',67,'探索历史','#D35400','#FFFFFF',1,'#000000',0.4,'strategy','mobile','all','2024-02-01 00:00:00','2024-11-30 23:59:00',1,2100,51200,14,1,0,'历史文化','operator','2026-03-12 13:55:08','2026-03-12 13:55:08'),(23,'周末特惠','说走就走的短途旅行','https://images.unsplash.com/photo-1506905925346-21bda4d32df4','https://images.unsplash.com/photo-1530789253388-582c481c54b0','https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=200','url','https://www.example.com/weekend-deals','_blank',NULL,'查看优惠','#3498DB','#FFFFFF',0,'#2980B9',0.1,'strategy','all','visitor','2024-04-01 00:00:00','2024-09-30 23:59:00',1,890,18900,15,1,0,'周末游推荐','admin','2026-03-12 13:55:08','2026-03-12 13:55:08');
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品类型 ticket-票务 product-商品',
  `item_id` bigint NOT NULL COMMENT '商品ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品名称',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_item` (`user_id`,`item_type`,`item_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
INSERT INTO `cart` (`id`, `user_id`, `item_type`, `item_id`, `item_name`, `price`, `quantity`, `create_time`, `update_time`, `image`) VALUES (1,1,'product',1,'北京烤鸭礼盒',198.00,2,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(2,1,'ticket',1,'北京-上海火车票',553.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(3,2,'product',2,'上海雪花膏',68.00,3,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(4,2,'ticket',2,'上海-北京火车票',553.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(5,3,'product',3,'杭州西湖龙井茶叶',298.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(6,3,'ticket',3,'北京-广州机票',1200.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(7,4,'product',4,'四川大熊猫玩偶',88.00,2,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(8,4,'ticket',4,'广州-北京机票',1200.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(9,5,'product',5,'广东广绣',398.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(10,5,'ticket',5,'北京-西安火车票',515.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(15,15,'product',1,'北京烤鸭礼盒',198.00,4,'2026-03-22 12:41:45','2026-03-22 12:41:45','http://localhost:8080/upload/dc857d4f-9991-450e-849a-6d4a43a493c8.png'),(17,15,'product',5,'广东广绣',398.00,1,'2026-03-22 17:32:05','2026-03-22 17:32:05','https://example.com/product5.jpg');
UNLOCK TABLES;

--
-- Table structure for table `chat_group`
--

DROP TABLE IF EXISTS `chat_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_group` (
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群聊表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_group`
--

LOCK TABLES `chat_group` WRITE;
INSERT INTO `chat_group` (`id`, `name`, `description`, `creator_id`, `max_members`, `member_count`, `avatar`, `create_time`, `update_time`) VALUES (3,'开心聊天群','111',15,200,4,'http://localhost:8080/upload/5af0f72d-bad8-4c51-9072-6c0b32def4a4.webp','2026-05-10 11:43:01','2026-05-12 13:56:51'),(4,'大象世界','分享快乐',15,200,4,'http://localhost:8080/upload/876a0388-5ad0-4d1c-b1a0-b8614b5ee56f.png','2026-05-12 14:06:02','2026-05-12 14:06:02');
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'text' COMMENT '消息类型 text-文本 image-图片 video-视频 audio-音频',
  `is_read` int DEFAULT '0' COMMENT '是否已读 0-未读 1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sender_id` (`sender_id`) USING BTREE,
  KEY `idx_receiver_id` (`receiver_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
INSERT INTO `chat_message` (`id`, `sender_id`, `receiver_id`, `content`, `message_type`, `is_read`, `create_time`) VALUES (58,21,15,'😴','emoji',1,'2026-05-12 12:50:47'),(59,22,15,'http://localhost:8080/upload/3988b87e-6425-4419-94ff-cb516ee41beb.webp','emoji',1,'2026-05-12 14:22:17'),(60,22,15,'仔阿康吗','text',1,'2026-05-12 14:22:23'),(61,22,15,'🚀','emoji',1,'2026-05-12 14:23:48'),(62,19,15,'撒旦','text',1,'2026-05-12 17:32:34');
UNLOCK TABLES;

--
-- Table structure for table `cities`
--

DROP TABLE IF EXISTS `cities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cities` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '城市ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市名称',
  `chinese_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `country_id` int NOT NULL COMMENT '所属国家ID',
  `is_capital` tinyint(1) DEFAULT '0' COMMENT '是否是首都',
  `area` decimal(8,2) DEFAULT NULL COMMENT '面积(平方公里)',
  `population` int DEFAULT NULL COMMENT '人口(万)',
  `timezone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时区',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '城市简介',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '经度',
  `famous_for` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '闻名于世的原因',
  `best_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最佳旅游季节',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_country_id` (`country_id`) USING BTREE,
  CONSTRAINT `fk_cities_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='城市表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cities`
--

LOCK TABLES `cities` WRITE;
INSERT INTO `cities` (`id`, `name`, `chinese_name`, `country_id`, `is_capital`, `area`, `population`, `timezone`, `description`, `latitude`, `longitude`, `famous_for`, `best_season`, `created_at`) VALUES (1,'Beijing','北京',1,1,16410.54,21540000,'UTC+8','中国的首都，拥有悠久的历史和丰富的文化遗产。',39.90420000,116.40740000,'故宫、长城、颐和园','春秋','2026-02-14 09:23:52'),(2,'Shanghai','上海',1,0,6340.50,24870000,'UTC+8','中国最大的城市，经济金融中心。',31.23040000,121.47370000,'外滩、东方明珠、豫园','四季皆宜','2026-02-14 09:23:52'),(3,'Tokyo','东京',2,1,2194.07,13960000,'UTC+9','日本的首都，现代化大都市。',35.67620000,139.65030000,'东京塔、明治神宫、涩谷','春秋','2026-02-14 09:23:52'),(4,'New York','纽约',3,0,1214.40,8336817,'UTC-5','美国最大的城市，世界金融中心。',40.71280000,-74.00600000,'自由女神像、时代广场、中央公园','春秋','2026-02-14 09:23:52'),(5,'Paris','巴黎',4,1,105.40,2148000,'UTC+1','法国的首都，浪漫之都。',48.85660000,2.35220000,'埃菲尔铁塔、卢浮宫、巴黎圣母院','春秋','2026-02-14 09:23:52'),(6,'Sydney','悉尼',5,0,12368.19,5312000,'UTC+10','澳大利亚最大的城市。',-33.86880000,151.20930000,'悉尼歌剧院、海港大桥、邦迪海滩','春秋','2026-02-14 09:23:52'),(7,'Cape Town','开普敦',6,0,2499.00,4710000,'UTC+2','南非的立法首都，美丽的海滨城市。',-33.92490000,18.42410000,'桌山、好望角、维多利亚港','春秋','2026-02-14 09:23:52'),(8,'Rio de Janeiro','里约热内卢',7,0,1200.00,6718903,'UTC-3','巴西的前首都，以狂欢节著称。',-22.90680000,-43.17290000,'基督像、科帕卡巴纳海滩、狂欢节','冬春','2026-02-14 09:23:52'),(9,'Berlin','柏林',8,1,891.80,3748000,'UTC+1','德国的首都，历史文化名城。',52.52000000,13.40500000,'勃兰登堡门、柏林墙、博物馆岛','春秋','2026-02-14 09:23:52'),(10,'Mumbai','孟买',9,0,603.40,20411274,'UTC+5:30','印度最大的城市，经济中心。',19.07600000,72.87770000,'泰姬玛哈酒店、贾特拉帕蒂·希瓦吉终点站、海滩','冬','2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `collection`
--

DROP TABLE IF EXISTS `collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收藏类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区',
  `item_id` bigint NOT NULL COMMENT '收藏对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_item` (`user_id`,`item_type`,`item_id`) USING BTREE,
  KEY `idx_item_type_id` (`item_type`,`item_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collection`
--

LOCK TABLES `collection` WRITE;
INSERT INTO `collection` (`id`, `user_id`, `item_type`, `item_id`, `create_time`) VALUES (1,1,'attraction',1,'2026-02-14 17:21:56'),(2,1,'hotel',1,'2026-02-14 17:21:56'),(3,1,'food',1,'2026-02-14 17:21:56'),(4,2,'attraction',2,'2026-02-14 17:21:56'),(5,2,'hotel',2,'2026-02-14 17:21:56'),(6,2,'food',2,'2026-02-14 17:21:56'),(7,3,'attraction',3,'2026-02-14 17:21:56'),(8,3,'hotel',3,'2026-02-14 17:21:56'),(9,3,'food',3,'2026-02-14 17:21:56'),(10,4,'attraction',4,'2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区',
  `item_id` bigint NOT NULL COMMENT '评论对象ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID 0-顶级评论',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片列表',
  `rating` int DEFAULT NULL COMMENT '评分 1-5星',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` int DEFAULT '1' COMMENT '状态 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_item_type_id` (`item_type`,`item_id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
INSERT INTO `comment` (`id`, `user_id`, `item_type`, `item_id`, `parent_id`, `content`, `images`, `rating`, `like_count`, `status`, `create_time`, `update_time`) VALUES (1,1,'attraction',1,0,'故宫真的很震撼，历史感很强，值得一去。','[\"https://example.com/comment1_1.jpg\"]',5,30,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'attraction',1,0,'人太多了，建议早点去，避开高峰期。',NULL,4,15,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'hotel',1,0,'酒店位置很好，服务态度也不错，下次还会选择。',NULL,5,20,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'hotel',1,0,'房间很干净，设施齐全，就是价格有点贵。',NULL,4,10,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'food',1,0,'北京烤鸭真的很正宗，皮脆肉嫩，推荐大家尝试。','[\"https://example.com/comment5_1.jpg\"]',5,25,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'food',1,0,'价格有点贵，但是味道确实不错。',NULL,4,8,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'product',1,0,'包装很好，味道也很正宗，送礼很合适。',NULL,5,15,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,8,'product',1,0,'真空包装的，方便携带，但是没有现做的好吃。',NULL,4,5,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,9,'news',1,0,'五一确实人很多，但是旅游体验还是不错的。',NULL,NULL,12,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,10,'community',1,0,'照片拍得很好，下次有机会也要去北京看看。',NULL,NULL,8,1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `community_post`
--

DROP TABLE IF EXISTS `community_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片列表',
  `videos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '视频列表',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `status` int DEFAULT '1' COMMENT '状态 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='社区帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_post`
--

LOCK TABLES `community_post` WRITE;
INSERT INTO `community_post` (`id`, `user_id`, `title`, `content`, `images`, `videos`, `like_count`, `comment_count`, `collect_count`, `status`, `create_time`, `update_time`) VALUES (1,1,'北京之行归来，分享一些照片','五一假期去了北京，游览了故宫、长城等景点，人真的很多，但是景色很美。分享一些照片给大家。','[\"https://example.com/post1_1.jpg\", \"https://example.com/post1_2.jpg\"]',NULL,250,80,120,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'上海美食推荐','作为一个上海本地人，给大家推荐一些地道的上海美食，包括小笼包、生煎包、白斩鸡等。','[\"https://example.com/post2_1.jpg\", \"https://example.com/post2_2.jpg\"]',NULL,180,60,90,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'杭州西湖一日游攻略','整理了一份西湖一日游的详细攻略，包括最佳游览路线、必看景点、美食推荐等。','[\"https://example.com/post3_1.jpg\", \"https://example.com/post3_2.jpg\"]',NULL,220,70,100,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'成都大熊猫基地游玩体验','去了成都大熊猫基地，看到了很多可爱的大熊猫，分享一些照片和游玩体验。','[\"https://example.com/post4_1.jpg\", \"https://example.com/post4_2.jpg\"]',NULL,300,90,150,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'三亚海滩度假心得','在三亚度过了一个愉快的假期，分享一些海滩度假的心得和照片。','[\"https://example.com/post5_1.jpg\", \"https://example.com/post5_2.jpg\"]',NULL,280,85,130,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'西安兵马俑震撼之旅','参观了秦始皇兵马俑，被其规模和工艺所震撼，分享一些照片和感受。','[\"https://example.com/post6_1.jpg\", \"https://example.com/post6_2.jpg\"]',NULL,200,65,85,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'厦门鼓浪屿的慢生活','在鼓浪屿度过了两天慢生活，感受了岛上的文艺气息，分享一些照片和体验。','[\"https://example.com/post7_1.jpg\", \"https://example.com/post7_2.jpg\"]',NULL,190,62,88,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,8,'张家界天门山玻璃栈道体验','挑战了天门山的玻璃栈道，虽然有点害怕，但是风景真的很美，分享一些照片和体验。','[\"https://example.com/post8_1.jpg\", \"https://example.com/post8_2.jpg\"]',NULL,230,75,110,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,9,'云南大理洱海骑行','在大理洱海骑行，沿途风景如画，分享一些照片和骑行路线推荐。','[\"https://example.com/post9_1.jpg\", \"https://example.com/post9_2.jpg\"]',NULL,210,68,95,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,10,'黄山云海日出绝美景色','在黄山山顶等待日出，看到了壮观的云海日出，分享一些照片和拍摄技巧。','[\"https://example.com/post10_1.jpg\", \"https://example.com/post10_2.jpg\"]',NULL,320,95,160,1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `continents`
--

DROP TABLE IF EXISTS `continents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `continents` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '大洲ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '大洲名称',
  `chinese_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `area` decimal(10,2) DEFAULT NULL COMMENT '面积(万平方公里)',
  `population` int DEFAULT NULL COMMENT '人口(万)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='大洲表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `continents`
--

LOCK TABLES `continents` WRITE;
INSERT INTO `continents` (`id`, `name`, `chinese_name`, `area`, `population`, `description`, `created_at`) VALUES (1,'Asia','亚洲',4457.90,4541668,'亚洲是世界上面积最大、人口最多的大洲，拥有丰富的文化和历史遗产。','2026-02-14 09:23:52'),(2,'Europe','欧洲',1016.00,747636,'欧洲是世界上发达国家最集中的大洲，拥有悠久的历史和灿烂的文化。','2026-02-14 09:23:52'),(3,'North America','北美洲',2470.90,579024,'北美洲包括美国、加拿大等国家，经济发达，科技先进。','2026-02-14 09:23:52'),(4,'South America','南美洲',1784.00,434189,'南美洲拥有丰富的自然资源和多样的生态系统，亚马逊雨林位于此处。','2026-02-14 09:23:52'),(5,'Africa','非洲',3037.00,1340598,'非洲是人类文明的发源地之一，拥有多样的文化和自然景观。','2026-02-14 09:23:52'),(6,'Oceania','大洋洲',867.00,41261,'大洋洲由众多岛屿组成，澳大利亚是其最大的国家。','2026-02-14 09:23:52'),(7,'Antarctica','南极洲',1397.00,0,'南极洲是世界上最寒冷的大洲，也是唯一没有永久居民的大洲。','2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `countries`
--

DROP TABLE IF EXISTS `countries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `countries` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '国家ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '国家名称',
  `chinese_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `continent_id` int NOT NULL COMMENT '所属大洲ID',
  `capital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '首都',
  `area` decimal(10,2) DEFAULT NULL COMMENT '面积(万平方公里)',
  `population` int DEFAULT NULL COMMENT '人口(万)',
  `currency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '货币',
  `language` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '官方语言',
  `timezone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时区',
  `country_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国家代码',
  `phone_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话区号',
  `flag_emoji` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国旗emoji',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '简介',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `image` varchar(155) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市图片',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_continent_id` (`continent_id`) USING BTREE,
  CONSTRAINT `fk_countries_continent` FOREIGN KEY (`continent_id`) REFERENCES `continents` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='国家表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `countries`
--

LOCK TABLES `countries` WRITE;
INSERT INTO `countries` (`id`, `name`, `chinese_name`, `continent_id`, `capital`, `area`, `population`, `currency`, `language`, `timezone`, `country_code`, `phone_code`, `flag_emoji`, `description`, `created_at`, `image`) VALUES (1,'China','中国',1,'北京',960.00,1412600000,'CNY','Chinese','UTC+8','CN','86','🇨🇳','中国是世界上人口最多的国家，拥有悠久的历史和灿烂的文化。','2026-02-14 09:23:52','http://localhost:8080/upload/9cb9edbd-5968-4f9b-a842-24bbd44fcc3e.png'),(2,'Japan','日本',1,'东京',37.80,125836000,'JPY','Japanese','UTC+9','JP','81','🇯🇵','日本是一个岛国，拥有独特的文化和先进的科技。','2026-02-14 09:23:52',NULL),(3,'United States','美国',3,'华盛顿',937.30,331900000,'USD','English','UTC-5 to UTC-10','US','1','🇺🇸','美国是世界上最强大的国家之一，经济和科技发达。','2026-02-14 09:23:52',NULL),(4,'France','法国',2,'巴黎',55.10,67750000,'EUR','French','UTC+1','FR','33','🇫🇷','法国是欧洲的重要国家，以浪漫的文化和美食著称。','2026-02-14 09:23:52',NULL),(5,'Australia','澳大利亚',6,'堪培拉',769.20,25739000,'AUD','English','UTC+8 to UTC+11','AU','61','🇦🇺','澳大利亚是世界上面积第六大的国家，拥有独特的自然景观。','2026-02-14 09:23:52',NULL),(6,'South Africa','南非',5,'比勒陀利亚',122.10,59390000,'ZAR','Afrikaans, English','UTC+2','ZA','27','🇿🇦','南非是非洲最发达的国家之一，拥有丰富的自然资源。','2026-02-14 09:23:52',NULL),(7,'Brazil','巴西',4,'巴西利亚',851.60,213993000,'BRL','Portuguese','UTC-2 to UTC-5','BR','55','🇧🇷','巴西是南美洲最大的国家，拥有世界上最大的热带雨林。','2026-02-14 09:23:52',NULL),(8,'Germany','德国',2,'柏林',35.70,83240000,'EUR','German','UTC+1','DE','49','🇩🇪','德国是欧洲经济最强大的国家之一，以精密制造著称。','2026-02-14 09:23:52',NULL),(9,'India','印度',1,'新德里',328.70,1380004000,'INR','Hindi, English','UTC+5:30','IN','91','🇮🇳','印度是世界上人口第二多的国家，拥有多样的文化。','2026-02-14 09:23:52',NULL),(10,'Canada','加拿大',3,'渥太华',998.50,38246000,'CAD','English, French','UTC-3:30 to UTC-8','CA','1','🇨🇦','加拿大是世界上面积第二大的国家，自然资源丰富。','2026-02-14 09:23:52',NULL);
UNLOCK TABLES;

--
-- Table structure for table `emoji`
--

DROP TABLE IF EXISTS `emoji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emoji` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `emoji_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情编码',
  `emoji_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情名称',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `unicode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Unicode编码',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表情描述',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `emoji_code` (`emoji_code`),
  KEY `idx_category` (`category`),
  KEY `idx_emoji_code` (`emoji_code`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统表情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emoji`
--

LOCK TABLES `emoji` WRITE;
INSERT INTO `emoji` (`id`, `emoji_code`, `emoji_name`, `category`, `unicode`, `description`, `sort_order`, `is_active`, `create_time`, `update_time`) VALUES (1,'smile','笑脸','表情','U+1F604','开心的笑脸',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(2,'laugh','大笑','表情','U+1F602','大笑表情',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(3,'love','爱心眼','表情','U+1F60D','花痴表情',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(4,'sad','难过','表情','U+1F622','难过的表情',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(5,'cry','哭泣','表情','U+1F62D','哭泣表情',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(6,'angry','生气','表情','U+1F620','生气的表情',6,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(7,'surprised','惊讶','表情','U+1F62E','惊讶表情',7,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(8,'shy','害羞','表情','U+1F60A','害羞表情',8,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(9,'thumbs_up','点赞','手势','U+1F44D','竖起大拇指',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(10,'thumbs_down','差评','手势','U+1F44E','向下大拇指',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(11,'ok','OK','手势','U+1F44C','OK手势',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(12,'peace','和平','手势','U+270C','和平手势',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(13,'heart','红心','符号','U+2764','红心',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(14,'star','星星','符号','U+2B50','星星',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(15,'fire','火焰','符号','U+1F525','火焰',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(16,'gift','礼物','物品','U+1F381','礼物盒',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(17,'cake','蛋糕','物品','U+1F382','生日蛋糕',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(18,'coffee','咖啡','物品','U+2615','咖啡杯',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(19,'phone','电话','物品','U+1F4DE','电话',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(20,'mail','邮件','物品','U+2709','邮件',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(21,'sun','太阳','自然','U+2600','太阳',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(22,'cloud','云朵','自然','U+2601','云朵',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(23,'rain','下雨','自然','U+2614','下雨',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(24,'snow','雪花','自然','U+2744','雪花',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(25,'flower','花朵','自然','U+1F338','花朵',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(26,'rocket','火箭','科技','U+1F680','火箭',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(27,'plane','飞机','交通','U+2708','飞机',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(28,'car','汽车','交通','U+1F697','汽车',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(29,'train','火车','交通','U+1F682','火车',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(30,'music','音乐','娱乐','U+1F3B5','音符',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(31,'game','游戏','娱乐','U+1F3AE','游戏手柄',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(32,'movie','电影','娱乐','U+1F3A5','电影放映机',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(33,'book','书本','学习','U+1F4DA','书本',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(34,'graduation','毕业','学习','U+1F393','毕业帽',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(35,'heart_break','心碎','情感','U+1F494','破碎的心',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(36,'kiss','飞吻','情感','U+1F618','飞吻表情',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(37,'wink','眨眼','表情','U+1F609','眨眼表情',9,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(38,'tired','疲惫','表情','U+1F62B','疲惫表情',10,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(39,'sleepy','困倦','表情','U+1F634','困倦表情',11,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(40,'sick','生病','表情','U+1F912','生病表情',12,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(41,'money','金钱','符号','U+1F4B0','钞票',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(42,'gem','钻石','符号','U+1F48E','钻石',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(43,'truck','卡车','交通','U+1F69A','卡车',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(44,'bike','自行车','交通','U+1F6B2','自行车',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(45,'food','食物','物品','U+1F34E','苹果',6,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(46,'pizza','披萨','物品','U+1F355','披萨',7,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(47,'hamburger','汉堡','物品','U+1F354','汉堡包',8,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(48,'beer','啤酒','物品','U+1F37A','啤酒杯',9,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(49,'wine','红酒','物品','U+1F377','红酒杯',10,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(50,'sports','运动','娱乐','U+26BD','足球',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(51,'trophy','奖杯','娱乐','U+1F3C6','奖杯',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(52,'medal','奖牌','娱乐','U+1F3C5','奖牌',6,1,'2026-05-10 17:08:45','2026-05-10 17:08:45');
UNLOCK TABLES;

--
-- Table structure for table `exchange_order`
--

DROP TABLE IF EXISTS `exchange_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exchange_order` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exchange_order`
--

LOCK TABLES `exchange_order` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `file_info`
--

DROP TABLE IF EXISTS `file_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小',
  `chunk_index` int DEFAULT '0' COMMENT '分片索引',
  `total_chunks` int DEFAULT '1' COMMENT '总分片数',
  `file_md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件MD5',
  `status` int DEFAULT '0' COMMENT '状态 0-上传中 1-上传完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_file_md5` (`file_md5`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_info`
--

LOCK TABLES `file_info` WRITE;
INSERT INTO `file_info` (`id`, `file_name`, `original_name`, `file_path`, `file_type`, `file_size`, `chunk_index`, `total_chunks`, `file_md5`, `status`, `create_time`) VALUES (1,'avatar1.jpg','avatar1.jpg','/uploads/avatar1.jpg','image/jpeg',1024000,0,1,'e10adc3949ba59abbe56e057f20f883e',1,'2026-02-14 17:21:56'),(2,'avatar2.jpg','avatar2.jpg','/uploads/avatar2.jpg','image/jpeg',1234567,0,1,'c81e728d9d4c2f636f067f89cc14862c',1,'2026-02-14 17:21:56'),(3,'avatar3.jpg','avatar3.jpg','/uploads/avatar3.jpg','image/jpeg',987654,0,1,'a87ff679a2f3e71d9181a67b7542122c',1,'2026-02-14 17:21:56'),(4,'attraction1.jpg','attraction1.jpg','/uploads/attraction1.jpg','image/jpeg',2048000,0,1,'eccbc87e4b5ce2fe28308fd9f2a7baf3',1,'2026-02-14 17:21:56'),(5,'attraction2.jpg','attraction2.jpg','/uploads/attraction2.jpg','image/jpeg',2560000,0,1,'c4ca4238a0b923820dcc509a6f75849b',1,'2026-02-14 17:21:56'),(6,'hotel1.jpg','hotel1.jpg','/uploads/hotel1.jpg','image/jpeg',1800000,0,1,'c81e728d9d4c2f636f067f89cc14862c',1,'2026-02-14 17:21:56'),(7,'hotel2.jpg','hotel2.jpg','/uploads/hotel2.jpg','image/jpeg',1900000,0,1,'eccbc87e4b5ce2fe28308fd9f2a7baf3',1,'2026-02-14 17:21:56'),(8,'food1.jpg','food1.jpg','/uploads/food1.jpg','image/jpeg',1500000,0,1,'a87ff679a2f3e71d9181a67b7542122c',1,'2026-02-14 17:21:56'),(9,'food2.jpg','food2.jpg','/uploads/food2.jpg','image/jpeg',1600000,0,1,'c4ca4238a0b923820dcc509a6f75849b',1,'2026-02-14 17:21:56'),(10,'product1.jpg','product1.jpg','/uploads/product1.jpg','image/jpeg',1700000,0,1,'c81e728d9d4c2f636f067f89cc14862c',1,'2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `food`
--

DROP TABLE IF EXISTS `food`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '美食ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '美食名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片列表',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `cuisine_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜系类型',
  `rating` int DEFAULT '0' COMMENT '评分',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签',
  `status` int DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `commodity` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_city` (`city`) USING BTREE,
  KEY `idx_cuisine_type` (`cuisine_type`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE,
  KEY `idx_like_count` (`like_count`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='美食表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food`
--

LOCK TABLES `food` WRITE;
INSERT INTO `food` (`id`, `name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `cuisine_type`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`, `create_time`, `update_time`, `commodity`) VALUES (3,'北京烤鸭','北京','北京','北京市东城区前门大街30号','北京传统名菜，外酥里嫩，香气四溢。','http://localhost:8080/upload/ed071f0c-f397-4e79-984e-81b2abc8df9c.png','https://example.com/food1_1.jpg,https://example.com/food1_2.jpg',268.00,'京菜',5,1200,800,400,'传统,烤鸭,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(4,'上海小笼包','上海','上海','上海市黄浦区豫园路85号','上海传统点心，皮薄馅大，汤汁丰富。','http://localhost:8080/upload/396217d8-909d-4f79-9176-38711f8d185d.png','https://example.com/food2_1.jpg,https://example.com/food2_2.jpg',38.00,'沪菜',5,1000,700,350,'传统,点心,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(5,'杭州西湖醋鱼','杭州','浙江','浙江省杭州市西湖区楼外楼','杭州传统名菜，鱼肉鲜嫩，酸甜可口。','http://localhost:8080/upload/e171c78c-dd8b-44b9-88bf-925d3a4836bd.png','https://example.com/food3_1.jpg,https://example.com/food3_2.jpg',88.00,'浙菜',4,800,500,280,'传统,西湖,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(6,'四川火锅','成都','四川','四川省成都市锦江区春熙路','四川特色美食，麻辣鲜香，回味无穷。','http://localhost:8080/upload/fba4e820-9073-49b8-809f-847ade920057.png','https://example.com/food4_1.jpg,https://example.com/food4_2.jpg',128.00,'川菜',5,1500,1000,500,'麻辣,火锅,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(7,'广东早茶','广州','广东','广东省广州市越秀区上下九','广东传统饮食文化，品种丰富，精致美味。','https://example.com/food5.jpg','https://example.com/food5_1.jpg,https://example.com/food5_2.jpg',68.00,'粤菜',5,900,600,320,'传统,点心,早茶',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(8,'云南过桥米线','昆明','云南','云南省昆明市五华区正义路','云南特色美食，汤鲜料足，风味独特。','https://example.com/food6.jpg','https://example.com/food6_1.jpg,https://example.com/food6_2.jpg',45.00,'滇菜',4,700,400,250,'特色,米线,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(9,'西安肉夹馍','西安','陕西','陕西省西安市莲湖区回民街','西安传统小吃，馍香肉嫩，肥而不腻。','https://example.com/food7.jpg','https://example.com/food7_1.jpg,https://example.com/food7_2.jpg',15.00,'陕菜',5,1100,800,380,'传统,小吃,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(10,'湖南臭豆腐','长沙','湖南','湖南省长沙市芙蓉区黄兴南路','湖南特色小吃，闻起来臭，吃起来香。','https://example.com/food8.jpg','https://example.com/food8_1.jpg,https://example.com/food8_2.jpg',10.00,'湘菜',4,600,300,200,'特色,小吃,臭豆腐',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(11,'福建沙县小吃','福州','福建','福建省福州市鼓楼区八一七路','福建传统小吃，品种丰富，价格实惠。','https://example.com/food9.jpg','https://example.com/food9_1.jpg,https://example.com/food9_2.jpg',25.00,'闽菜',4,500,250,180,'小吃,实惠,传统',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(12,'新疆大盘鸡','乌鲁木齐','新疆','新疆维吾尔自治区乌鲁木齐市天山区','新疆特色美食，鸡肉鲜嫩，土豆软糯，辣味十足。','https://example.com/food10.jpg','https://example.com/food10_1.jpg,https://example.com/food10_2.jpg',98.00,'新疆菜',5,850,550,300,'特色,大盘鸡,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0');
UNLOCK TABLES;

--
-- Table structure for table `friend`
--

DROP TABLE IF EXISTS `friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '好友ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `friend_id` bigint NOT NULL COMMENT '好友ID',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `status` int DEFAULT '0' COMMENT '状态 0-待确认 1-已添加',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_friend` (`user_id`,`friend_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='好友表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend`
--

LOCK TABLES `friend` WRITE;
INSERT INTO `friend` (`id`, `user_id`, `friend_id`, `remark`, `status`, `create_time`, `update_time`) VALUES (16,15,19,NULL,1,'2026-05-08 23:35:23','2026-05-08 23:35:23'),(17,19,15,NULL,1,'2026-05-08 23:42:48','2026-05-08 23:42:48'),(19,21,15,NULL,1,'2026-05-09 23:04:52','2026-05-09 23:04:52'),(20,15,21,NULL,1,'2026-05-09 23:06:11','2026-05-09 23:06:11'),(21,22,15,NULL,1,'2026-05-12 12:55:26','2026-05-12 12:55:26'),(22,15,22,'老相好',1,'2026-05-12 12:55:43','2026-05-12 12:55:43');
UNLOCK TABLES;

--
-- Table structure for table `group_chat_message`
--

DROP TABLE IF EXISTS `group_chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_chat_message` (
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群聊消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_chat_message`
--

LOCK TABLES `group_chat_message` WRITE;
INSERT INTO `group_chat_message` (`id`, `group_id`, `sender_id`, `sender_name`, `sender_avatar`, `content`, `message_type`, `create_time`) VALUES (1,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','权威的','text','2026-05-10 12:05:15'),(2,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg',' 达瓦达瓦','text','2026-05-10 12:06:30'),(3,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','哇大王的','text','2026-05-10 12:06:35'),(4,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','http://localhost:8080/upload/45c4666e-37b7-4cf2-963e-bf0d4ece3042.jpeg','emoji','2026-05-10 16:59:30'),(6,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','http://localhost:8080/upload/e67ba6aa-a0ee-4cf8-81f7-6a63693e966e.jpg','image','2026-05-12 12:50:27'),(7,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','🚀','emoji','2026-05-12 13:33:12'),(8,3,22,'yun xiao','http://localhost:8080/upload/39adb7e5-e022-459d-862b-57da1244f8e3.png','😭','emoji','2026-05-12 14:04:54'),(9,4,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','http://localhost:8080/upload/38869291-f574-4d77-936c-081c2129e2a2.png','image','2026-05-12 14:06:12'),(10,3,19,'李小明','http://localhost:8080/upload/d333bdc6-a972-403b-8c2f-1e5da3b73edb.jpeg','📞','emoji','2026-05-13 20:54:42'),(11,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','🍎','emoji','2026-05-13 20:55:01'),(12,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','http://localhost:8080/upload/20c8ba8f-5cc7-47f4-ad7d-002aad3a5ac2.png','image','2026-05-13 20:55:08'),(13,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','http://localhost:8080/upload/3988b87e-6425-4419-94ff-cb516ee41beb.webp','emoji','2026-05-13 20:55:41');
UNLOCK TABLES;

--
-- Table structure for table `group_member`
--

DROP TABLE IF EXISTS `group_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id` bigint NOT NULL COMMENT '群ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) DEFAULT 'MEMBER' COMMENT '角色：OWNER-群主，ADMIN-管理员，MEMBER-成员',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_user` (`group_id`,`user_id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_member`
--

LOCK TABLES `group_member` WRITE;
INSERT INTO `group_member` (`id`, `group_id`, `user_id`, `role`, `join_time`) VALUES (1,1,15,'admin',NULL),(2,2,19,'member',NULL),(3,2,21,'member',NULL),(4,2,15,'admin',NULL),(5,3,19,'member',NULL),(6,3,21,'member',NULL),(7,3,15,'admin',NULL),(8,3,22,'member',NULL),(9,4,19,'member',NULL),(10,4,21,'member',NULL),(11,4,22,'member',NULL),(12,4,15,'admin',NULL);
UNLOCK TABLES;

--
-- Table structure for table `group_message_read`
--

DROP TABLE IF EXISTS `group_message_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_message_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_user` (`message_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群消息已读表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_message_read`
--

LOCK TABLES `group_message_read` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `group_travel`
--

DROP TABLE IF EXISTS `group_travel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_travel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '跟团游ID',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '团名',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `destination` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目的地',
  `departure_time` datetime DEFAULT NULL COMMENT '出发时间',
  `return_time` datetime DEFAULT NULL COMMENT '返回时间',
  `max_members` int DEFAULT NULL COMMENT '最大人数',
  `current_members` int DEFAULT '0' COMMENT '当前人数',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片列表',
  `status` int DEFAULT '1' COMMENT '状态 0-取消 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_creator_id` (`creator_id`) USING BTREE,
  KEY `idx_departure_time` (`departure_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='跟团游表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_travel`
--

LOCK TABLES `group_travel` WRITE;
INSERT INTO `group_travel` (`id`, `creator_id`, `name`, `description`, `destination`, `departure_time`, `return_time`, `max_members`, `current_members`, `price`, `images`, `status`, `create_time`, `update_time`) VALUES (1,1,'北京5日经典游','游览北京主要景点，包括故宫、长城、颐和园等。','北京','2024-05-01 08:00:00','2024-05-05 18:00:00',30,25,2800.00,'http://localhost:8080/upload/ffb22709-362c-44b5-b336-ccc706141a9d.png',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'上海+杭州4日游','游览上海外滩、杭州西湖等著名景点。','上海,杭州','2024-05-02 09:00:00','2024-05-05 17:00:00',25,20,2200.00,'[\"https://example.com/group2_1.jpg\", \"https://example.com/group2_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'成都+九寨沟5日游','游览成都大熊猫基地、九寨沟等景点。','成都,九寨沟','2024-05-03 07:30:00','2024-05-07 19:00:00',20,15,3500.00,'[\"https://example.com/group3_1.jpg\", \"https://example.com/group3_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'三亚3日度假游','享受三亚阳光沙滩，游览天涯海角等景点。','三亚','2024-05-04 10:00:00','2024-05-06 16:00:00',35,30,2900.00,'[\"https://example.com/group4_1.jpg\", \"https://example.com/group4_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'西安4日文化游','游览兵马俑、华清池等历史文化景点。','西安','2024-05-05 08:30:00','2024-05-08 18:30:00',28,22,2500.00,'[\"https://example.com/group5_1.jpg\", \"https://example.com/group5_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'云南大理+丽江6日游','游览大理古城、丽江古城等景点。','大理,丽江','2024-05-06 07:00:00','2024-05-11 20:00:00',22,18,3800.00,'[\"https://example.com/group6_1.jpg\", \"https://example.com/group6_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'厦门+鼓浪屿3日游','游览厦门市区、鼓浪屿等景点。','厦门,鼓浪屿','2024-05-07 09:30:00','2024-05-09 17:30:00',30,26,1900.00,'[\"https://example.com/group7_1.jpg\", \"https://example.com/group7_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,8,'张家界4日游','游览张家界国家森林公园、天门山等景点。','张家界','2024-05-08 08:00:00','2024-05-11 18:00:00',26,20,3200.00,'[\"https://example.com/group8_1.jpg\", \"https://example.com/group8_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,9,'广州+深圳4日游','游览广州塔、深圳世界之窗等景点。','广州,深圳','2024-05-09 09:00:00','2024-05-12 17:00:00',24,19,2100.00,'[\"https://example.com/group9_1.jpg\", \"https://example.com/group9_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,10,'黄山3日游','游览黄山风景区，欣赏奇松怪石云海。','黄山','2024-05-10 07:30:00','2024-05-12 18:30:00',20,16,2800.00,'[\"https://example.com/group10_1.jpg\", \"https://example.com/group10_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `home_recommendations`
--

DROP TABLE IF EXISTS `home_recommendations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_recommendations` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增长',
  `uuid` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全局唯一标识符，用于对外接口',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景点/项目名称',
  `english_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英文名称（如果有）',
  `alias_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '别名/俗称',
  `category_id` int unsigned NOT NULL COMMENT '分类ID（如：1-自然风光，2-历史文化，3-主题乐园，4-美食探店，5-休闲度假）',
  `category_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称（冗余字段，便于查询）',
  `tags` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，多个用逗号分隔（如：亲子,情侣,网红打卡,拍照圣地）',
  `country` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '中国' COMMENT '国家',
  `province` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份/直辖市',
  `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区/县',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度（WGS84坐标系）',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度（WGS84坐标系）',
  `google_map_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Google地图链接',
  `baidu_map_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '百度地图链接',
  `transportation` text COLLATE utf8mb4_unicode_ci COMMENT '交通方式描述（如：地铁X号线XX站A口出，步行500米）',
  `recommendation_reason` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推荐理由（首页展示的核心文案）',
  `highlights` text COLLATE utf8mb4_unicode_ci COMMENT '亮点特色（多个亮点可用分号分隔）',
  `best_travel_time` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最佳游玩时间（如：春秋两季，9月-11月）',
  `visit_duration` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '建议游玩时长（如：3-4小时，1天）',
  `cover_image_url` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图片URL（首页列表展示）',
  `banner_image_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详情页Banner图URL',
  `video_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '宣传视频URL',
  `image_urls` text COLLATE utf8mb4_unicode_ci COMMENT '更多图片URL，JSON数组格式存储',
  `ticket_price_description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '门票价格描述（如：成人票100元，学生票50元）',
  `min_price` decimal(10,2) unsigned DEFAULT NULL COMMENT '最低价格（用于价格筛选）',
  `max_price` decimal(10,2) unsigned DEFAULT NULL COMMENT '最高价格（用于价格筛选）',
  `price_currency` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'CNY' COMMENT '货币单位（CNY, USD, etc.）',
  `is_free` tinyint(1) DEFAULT '0' COMMENT '是否免费（TRUE-免费，FALSE-收费）',
  `opening_hours_weekday` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作日开放时间（如：09:00-18:00）',
  `opening_hours_weekend` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '周末开放时间（如：09:00-20:00）',
  `opening_hours_description` text COLLATE utf8mb4_unicode_ci COMMENT '开放时间详细描述（如：每周一闭馆，节假日正常开放）',
  `contact_phone` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话（多个用逗号分隔）',
  `official_website` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '官方网站',
  `social_media` json DEFAULT NULL COMMENT '社交媒体信息（JSON格式，如：{"weibo":"xxx", "wechat":"xxx", "douyin":"xxx"}）',
  `view_count` int unsigned NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `like_count` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞/收藏次数',
  `share_count` int unsigned NOT NULL DEFAULT '0' COMMENT '分享次数',
  `comment_count` int unsigned NOT NULL DEFAULT '0' COMMENT '评论次数',
  `average_rating` decimal(3,2) unsigned DEFAULT '0.00' COMMENT '平均评分（满分5分）',
  `rating_count` int unsigned NOT NULL DEFAULT '0' COMMENT '评分人数',
  `weight` int NOT NULL DEFAULT '0' COMMENT '推荐权重（数字越大，排名越靠前）',
  `is_hot` tinyint(1) DEFAULT '0' COMMENT '是否热门推荐',
  `is_new` tinyint(1) DEFAULT '0' COMMENT '是否新晋推荐',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架，2-审核中，3-待发布',
  `season` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐季节（spring, summer, autumn, winter, all）',
  `festival` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联节日（如：春节，国庆，圣诞节）',
  `tips` text COLLATE utf8mb4_unicode_ci COMMENT '游玩小贴士/注意事项',
  `suitable_crowd` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适合人群（如：亲子，情侣，朋友，家庭，老人）',
  `facility_info` text COLLATE utf8mb4_unicode_ci COMMENT '设施信息（如：有无停车场，有无母婴室，无障碍设施等）',
  `seo_title` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SEO标题',
  `seo_keywords` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SEO关键词',
  `seo_description` text COLLATE utf8mb4_unicode_ci COMMENT 'SEO描述',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '详细介绍内容（富文本或Markdown）',
  `summary` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要/简介（用于列表页）',
  `source` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '信息来源（如：小编踩点，用户投稿，官方合作）',
  `author` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者/编辑',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `published_at` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `expired_at` timestamp NULL DEFAULT NULL COMMENT '过期时间（NULL表示永不过期）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  KEY `idx_category` (`category_id`,`status`,`weight`),
  KEY `idx_city` (`province`,`city`,`status`),
  KEY `idx_hot` (`is_hot`,`weight`),
  KEY `idx_new` (`is_new`,`created_at`),
  KEY `idx_price` (`min_price`,`max_price`),
  KEY `idx_season` (`season`),
  KEY `idx_status_published` (`status`,`published_at`),
  KEY `idx_location` (`longitude`,`latitude`),
  FULLTEXT KEY `ft_content` (`name`,`summary`,`content`) /*!50100 WITH PARSER `ngram` */ 
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页旅游推荐内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `home_recommendations`
--

LOCK TABLES `home_recommendations` WRITE;
INSERT INTO `home_recommendations` (`id`, `uuid`, `name`, `english_name`, `alias_name`, `category_id`, `category_name`, `tags`, `country`, `province`, `city`, `district`, `address`, `longitude`, `latitude`, `google_map_url`, `baidu_map_url`, `transportation`, `recommendation_reason`, `highlights`, `best_travel_time`, `visit_duration`, `cover_image_url`, `banner_image_url`, `video_url`, `image_urls`, `ticket_price_description`, `min_price`, `max_price`, `price_currency`, `is_free`, `opening_hours_weekday`, `opening_hours_weekend`, `opening_hours_description`, `contact_phone`, `official_website`, `social_media`, `view_count`, `like_count`, `share_count`, `comment_count`, `average_rating`, `rating_count`, `weight`, `is_hot`, `is_new`, `is_top`, `status`, `season`, `festival`, `tips`, `suitable_crowd`, `facility_info`, `seo_title`, `seo_keywords`, `seo_description`, `content`, `summary`, `source`, `author`, `created_at`, `updated_at`, `published_at`, `expired_at`) VALUES (1,'082d6297-1d5b-11f1-833c-c4c6e607efc7','故宫博物院','The Palace Museum',NULL,2,'历史文化','世界遗产,拍照圣地,亲子游,研学','中国','北京市','北京市','东城区','景山前街4号',NULL,NULL,NULL,NULL,'地铁1号线天安门东站A口出，步行约10分钟','穿越六百年时光，感受明清两代的皇家气派。故宫不仅是中国古代建筑的精髓，更是中华文明的瑰宝。','三大殿太和殿中和殿保和殿;珍宝馆;钟表馆;故宫角楼;景山俯瞰全景','春秋两季（4月-5月，9月-10月）','半天至一天','http://localhost:8080/upload/9c3efbcb-5afe-4b4f-8bdf-46c58d485660.png',NULL,NULL,'[\"https://example.com/images/gugong_1.jpg\", \"https://example.com/images/gugong_2.jpg\"]','旺季60元/人，淡季40元/人，珍宝馆和钟表馆需另购票',40.00,60.00,'CNY',0,'08:30-17:00','08:30-17:00','每周一闭馆（法定节假日除外）','010-85007421','https://www.dpm.org.cn',NULL,0,0,0,0,0.00,0,100,1,0,0,1,'autumn',NULL,'建议提前网上预约购票，携带身份证入场。避开节假日高峰期，可租讲解器或请导游。','历史文化爱好者,亲子家庭,摄影爱好者',NULL,NULL,NULL,NULL,'故宫博物院建立于1925年，是在明朝、清朝两代皇宫及其收藏的基础上建立起来的中国综合性博物馆。','穿越六百年时光，探寻紫禁城的秘密',NULL,NULL,'2026-03-11 15:00:24','2026-03-11 15:00:24',NULL,NULL),(2,'082daddd-1d5b-11f1-833c-c4c6e607efc7','成都大熊猫繁育研究基地','Chengdu Research Base of Giant Panda Breeding',NULL,1,'自然风光','熊猫,亲子,动物保护,网红','中国','四川省','成都市','成华区','熊猫大道1375号',NULL,NULL,NULL,NULL,'地铁3号线熊猫大道站A口出，转乘景区直通车','近距离观察国宝大熊猫的日常生活，看软萌的熊猫宝宝嬉戏玩耍，是成都必打卡的亲子胜地。','月亮产房看熊猫幼崽;太阳产房;成年熊猫别墅;小熊猫活动区;熊猫博物馆','全年（最佳是春秋季，上午熊猫最活跃）','3-4小时','https://example.com/images/panda_cover.jpg',NULL,NULL,'[\"https://example.com/images/panda_1.jpg\", \"https://example.com/images/panda_2.jpg\"]','成人票55元，学生票27元',27.00,55.00,'CNY',0,'07:30-18:00','07:30-18:00','全年开放','028-83510033','http://www.panda.org.cn',NULL,0,0,0,0,0.00,0,95,1,1,0,1,'spring',NULL,'尽量上午8-10点前往，此时熊猫最活跃，是喂食时间。穿舒适的鞋子，园区较大。','亲子家庭,动物爱好者,自然爱好者',NULL,NULL,NULL,NULL,'成都大熊猫繁育研究基地是一个专门保护和繁育大熊猫、小熊猫等珍稀动物的机构，也是全球最大的大熊猫人工繁育机构。','近距离感受国宝熊猫的萌力暴击',NULL,NULL,'2026-03-11 15:00:24','2026-03-11 15:00:24',NULL,NULL),(3,'4689cc18-1d60-11f1-833c-c4c6e607efc7','上海迪士尼度假区','Shanghai Disney Resort',NULL,3,'主题乐园','亲子,情侣,童话,烟花秀,网红打卡','中国','上海市','上海市','浦东新区','川沙镇黄赵路310号',NULL,NULL,NULL,NULL,'地铁11号线迪士尼站直达','中国大陆首座迪士尼主题乐园，带你进入梦幻童话世界。七大主题园区各具特色，烟花秀震撼人心。','奇幻童话城堡;创极速光轮;加勒比海盗;飞跃地平线;烟花灯光秀;花车巡游','春秋季（避开寒暑假和节假日）','1-2天','http://localhost:8080/upload/d646cecc-0184-47f5-b0e1-b3c9d0c28d91.png',NULL,'https://videos.travel.com/disney_intro.mp4','[\"https://images.travel.com/disney_1.jpg\", \"https://images.travel.com/disney_2.jpg\", \"https://images.travel.com/disney_3.jpg\"]','平日399元，高峰日599元，特别高峰日699元',399.00,699.00,'CNY',0,'09:00-20:30','09:00-21:00','开放时间随季节和活动调整，以官网为准','400-180-0000','https://www.shanghaidisneyresort.com','{\"weibo\": \"上海迪士尼度假区\", \"douyin\": \"shanghaidisney\", \"wechat\": \"ShangHaiDisneyResort\"}',125890,45678,28900,12345,4.80,32000,100,1,0,0,1,'all','圣诞节,春节,万圣节','提前下载官方APP查看排队时间；可购买早享卡提前入园；烟花秀建议提前占位；可带未开封零食和水。','亲子家庭,情侣,年轻人,迪士尼粉丝','有停车场（收费）；有母婴室；提供轮椅和婴儿车租赁；有快速通行证服务','上海迪士尼度假区_门票_攻略_游玩项目','上海迪士尼,迪士尼攻略,主题乐园','上海迪士尼度假区完整攻略：门票价格、开放时间、必玩项目、交通指南、游玩贴士','上海迪士尼度假区是中国内地首座迪士尼主题乐园，于2016年6月正式开园。园区分为米奇大街、奇想花园、探险岛、宝藏湾、明日世界、梦幻世界、玩具总动员七大主题园区。','点亮心中奇梦，重返童年时光','官方合作','迪士尼小编','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-01 01:00:00',NULL),(4,'468a1d1e-1d60-11f1-833c-c4c6e607efc7','张家界国家森林公园','Zhangjiajie National Forest Park',NULL,1,'自然风光','自然遗产,登山,摄影,阿凡达,玻璃桥','中国','湖南省','张家界市','武陵源区','金鞭路279号',NULL,NULL,NULL,NULL,'张家界中心汽车站乘景区专线车','电影《阿凡达》取景地，三千奇峰拔地而起，云雾缭绕宛如仙境。是世界自然遗产、世界地质公园。','袁家界哈利路亚山;天子山;金鞭溪;十里画廊;百龙天梯;天门山玻璃栈道','4月-6月，9月-11月（雨后初晴云海最美）','2-3天','https://images.travel.com/zhangjiajie_cover.jpg',NULL,'https://videos.travel.com/zhangjiajie_intro.mp4','[\"https://images.travel.com/zhangjiajie_1.jpg\", \"https://images.travel.com/zhangjiajie_2.jpg\", \"https://images.travel.com/zhangjiajie_3.jpg\"]','森林公园门票228元（4日内有效），天门山门票278元',228.00,278.00,'CNY',0,'07:00-18:00','07:00-18:00','景区旺季延长开放时间','0744-5712333','https://www.zjjpark.com','{\"douyin\": \"zjjtour\", \"wechat\": \"张家界旅游\"}',87650,32100,15400,8760,4.70,18700,98,1,0,0,1,'autumn','国庆节','山路较多，穿舒适运动鞋；景区内环保车免费；建议住一晚武陵源区；注意防猴。','登山爱好者,摄影爱好者,自然探索者','有停车场；山上有简餐售卖点；部分路段有索道和电梯；游客中心可寄存行李','张家界国家森林公园旅游攻略_门票_住宿_交通','张家界,武陵源,阿凡达,玻璃桥','张家界国家森林公园完整攻略：门票价格、开放时间、最佳路线、住宿推荐','张家界国家森林公园位于湖南省张家界市武陵源区，是中国第一个国家森林公园，1992年被列入世界自然遗产名录。公园以独特的石英砂岩峰林地貌著称，有“三千奇峰，八百秀水”之美誉。','探秘阿凡达的悬浮山仙境','小编踩点','张旅行','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-15 02:30:00',NULL),(5,'468a2045-1d60-11f1-833c-c4c6e607efc7','回民街','Muslim Quarter',NULL,4,'美食探店','美食,小吃,夜市,历史文化,清真','中国','陕西省','西安市','莲湖区','北院门街道',NULL,NULL,NULL,NULL,'地铁2号线钟楼站下车步行5分钟','西安最著名的小吃街，三百多种美食让你从街头吃到巷尾。青石板路两旁是明清风格建筑，烟火气十足。','牛羊肉泡馍;肉夹馍;凉皮;甑糕;烤肉串;柿子饼;酸梅汤','全年（晚上最热闹）','2-3小时','https://images.travel.com/huiminjie_cover.jpg',NULL,NULL,'[\"https://images.travel.com/huiminjie_1.jpg\", \"https://images.travel.com/huiminjie_2.jpg\"]','丰俭由人，人均消费约50-100元',0.00,0.00,'CNY',0,'全天开放','全天开放','商户一般10:00-23:00营业',NULL,NULL,'{\"douyin\": \"xianfood\"}',45670,18760,8900,5430,4.50,8900,85,1,1,0,1,'all',NULL,'选择人多的店一般不会踩雷；可以每家少吃一点多尝几种；注意清真饮食禁忌。','美食爱好者,吃货,夜猫子','公共厕所在街区两端；部分老店只收现金；有座位的小吃店较少','西安回民街美食攻略_必吃小吃_夜市推荐','回民街,西安美食,小吃街','西安回民街必吃美食清单+避坑指南+营业时间','回民街是西安最具代表性的美食文化街区，距今已有上千年历史。这里聚集了300多种西北特色小吃，是了解西安饮食文化的绝佳去处。','从街头吃到巷尾的西安美食天堂','用户投稿','吃货小马','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-20 10:00:00',NULL),(6,'468a242c-1d60-11f1-833c-c4c6e607efc7','洱海','Erhai Lake',NULL,1,'自然风光','洱海,骑行,拍照,民宿,发呆,治愈系','中国','云南省','大理白族自治州','大理市','洱海周边',NULL,NULL,NULL,NULL,'大理古城租电动车或包车环湖','苍山洱海，风花雪月。环湖120公里，每一处都是风景。在双廊看日落，在喜洲吃粑粑，在才村等日出。','环海东路;双廊古镇;喜洲古镇;海舌公园;小普陀;理想邦圣托里尼','3月-5月（樱花季），10月-12月（海鸥季）','2-3天','https://images.travel.com/erhai_cover.jpg',NULL,'https://videos.travel.com/erhai_intro.mp4','[\"https://images.travel.com/erhai_1.jpg\", \"https://images.travel.com/erhai_2.jpg\", \"https://images.travel.com/erhai_3.jpg\"]','免费开放（部分景点单独收费）',0.00,0.00,'CNY',1,'全天开放','全天开放','全天开放',NULL,NULL,'{\"wechat\": \"大理旅游\", \"xiaohongshu\": \"dali\"}',67890,28900,12300,7890,4.85,15600,96,1,0,0,1,'spring','春节','注意防晒；租车记得砍价；海东适合看日落，海西适合看日出；保护环境不乱扔垃圾。','情侣,闺蜜,摄影爱好者,文艺青年','环湖有多个停车场；沿途有公共厕所；民宿集中；有自行车租赁点','大理洱海旅游攻略_环湖路线_民宿推荐','洱海,大理旅游,环洱海','洱海环湖攻略：最佳路线+拍照打卡点+住宿推荐','洱海是云南省第二大高原湖泊，因形状像耳朵而得名。环湖一周约120公里，沿途有双廊、喜洲等多个古镇，是感受大理慢生活的理想之地。','苍山洱海，许你一场风花雪月','小编踩点','风花雪月','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-10 06:20:00',NULL),(7,'468a2778-1d60-11f1-833c-c4c6e607efc7','哈尔滨冰雪大世界','Harbin Ice and Snow World',NULL,3,'主题乐园','冰雪,冬季,冰雕,灯光秀,亲子','中国','黑龙江省','哈尔滨市','松北区','松北大道与太阳大道交叉口',NULL,NULL,NULL,NULL,'公交29路、42路冰雪大世界站','世界最大的冰雪主题乐园，用冰雪打造出一个梦幻的童话王国。白天晶莹剔透，夜晚流光溢彩。','冰雕建筑群;超级冰滑梯;冰雪城堡;灯光秀;雪圈;冰雪演出','12月下旬-次年2月底（春节前后最热闹）','4-6小时','https://images.travel.com/ice_cover.jpg',NULL,NULL,'[\"https://images.travel.com/ice_1.jpg\", \"https://images.travel.com/ice_2.jpg\", \"https://images.travel.com/ice_3.jpg\"]','标准票330元，优惠票200元',200.00,330.00,'CNY',0,'11:00-22:00','11:00-22:30','视天气情况调整','0451-58561111','http://www.hrbicesnow.com','{\"weibo\": \"哈尔滨冰雪大世界\"}',54320,23450,8900,4560,4.65,7800,92,1,0,0,1,'winter','元旦,春节','做好保暖，贴暖宝宝；手机贴暖宝宝防止冻关机；下午3点后去能看日夜两种景色。','亲子家庭,年轻人,南方游客','有停车场；园区内有餐饮区；有取暖屋；提供热饮售卖','哈尔滨冰雪大世界攻略_门票_开放时间','冰雪大世界,哈尔滨旅游,冰雕','哈尔滨冰雪大世界游玩攻略：门票价格、保暖贴士、必玩项目','哈尔滨冰雪大世界始创于1999年，每年冬季在松花江畔举办，用松花江的天然冰雪打造出规模宏大的冰雪建筑群，是哈尔滨国际冰雪节的重要组成部分。','闯入零下三十度的冰雪童话世界','官方合作','冰城小编','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-05 03:15:00',NULL),(8,'468a2ae4-1d60-11f1-833c-c4c6e607efc7','宽窄巷子','Kuanzhai Alley',NULL,4,'历史文化','古街,茶馆,美食,拍照,文艺','中国','四川省','成都市','青羊区','长顺上街127号',NULL,NULL,NULL,NULL,'地铁4号线宽窄巷子站','最能体现成都慢生活的地方，由宽巷子、窄巷子、井巷子三条平行古街组成。盖碗茶、采耳、变脸，巴适得很。','宽巷子(老成都);窄巷子(慢生活);井巷子(新时尚);茶馆看变脸;掏耳朵;三大炮','全年（春秋最舒适）','3-4小时','https://images.travel.com/kuanzhai_cover.jpg',NULL,NULL,'[\"https://images.travel.com/kuanzhai_1.jpg\", \"https://images.travel.com/kuanzhai_2.jpg\"]','免费进入（消费项目另算）',0.00,0.00,'CNY',1,'全天开放','全天开放','商户一般10:00-22:00营业','028-86253168',NULL,'{\"xiaohongshu\": \"成都宽窄巷子\"}',45678,19870,7600,4320,4.55,6500,80,1,1,0,1,'all',NULL,'不要只在主街逛，旁边的小巷子更有味道；采耳记得先问价；茶馆选有表演的。','文艺青年,美食爱好者,游客','有公共厕所；多处休息座椅；游客中心可寄存行李','成都宽窄巷子攻略_美食_茶馆','宽窄巷子,成都旅游,盖碗茶','宽窄巷子逛吃指南+采耳体验+表演推荐','宽窄巷子是成都遗留下来的较成规模的清朝古街道，由宽巷子、窄巷子、井巷子平行排列组成，全为青黛砖瓦的仿古四合院落，是成都三大历史文化保护区之一。','体验成都慢生活，泡茶馆看变脸','用户投稿','成都小甜甜','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-28 08:45:00',NULL),(9,'468a2dcc-1d60-11f1-833c-c4c6e607efc7','西湖','West Lake',NULL,1,'自然风光','世界遗产,免费,游船,断桥,雷峰塔,十景','中国','浙江省','杭州市','西湖区','西湖风景区',NULL,NULL,NULL,NULL,'地铁1号线龙翔桥站/凤起路站','欲把西湖比西子，淡妆浓抹总相宜。西湖的美，在晴雨雾雪，在春夏秋冬，在每一个不经意的回眸。','苏堤春晓;断桥残雪;雷峰夕照;三潭印月;曲院风荷;灵隐寺','3月-5月（桃花），9月-11月（桂花）','1天','https://images.travel.com/westlake_cover.jpg',NULL,'https://videos.travel.com/westlake_intro.mp4','[\"https://images.travel.com/westlake_1.jpg\", \"https://images.travel.com/westlake_2.jpg\", \"https://images.travel.com/westlake_3.jpg\"]','免费开放（部分景点单独收费）',0.00,0.00,'CNY',1,'全天开放','全天开放','全天开放','0571-87179570','http://www.hzwestlake.com','{\"wechat\": \"杭州西湖\"}',156780,67890,23400,12345,4.90,45200,99,1,0,1,1,'all','清明节,中秋节','建议骑行或步行；旺季人非常多；可以坐一次手摇船；看日落去长桥公园。','所有人群','环湖有多处停车场；公共厕所密集；有游船码头；多处游客中心','杭州西湖旅游攻略_景点_游船_美食','西湖,杭州旅游,断桥','西湖一日游攻略：最佳路线+必看景点+美食推荐','西湖位于杭州市区西部，是中国主要的观赏性淡水湖泊之一，也是首批国家重点风景名胜区。2011年被列入世界遗产名录，以其秀丽的湖光山色和众多的名胜古迹而闻名中外。','欲把西湖比西子，淡妆浓抹总相宜','小编踩点','西湖诗人','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-15 01:30:00',NULL),(10,'468a2f7b-1d60-11f1-833c-c4c6e607efc7','长隆野生动物世界','Chimelong Safari Park',NULL,1,'主题乐园','亲子,动物,自驾,科普,表演','中国','广东省','广州市','番禺区','大石镇105国道大石段593号',NULL,NULL,NULL,NULL,'地铁3号线汉溪长隆站E出口','亚洲最大的野生动物主题公园，可以自驾进入与动物零距离接触。从澳洲的袋鼠到非洲的长颈鹿，带你环游动物世界。','自驾区;熊猫乐园;考拉馆;白虎山;长颈鹿广场;动物表演;小火车','10月-4月（避开夏季高温）','1天','https://images.travel.com/chimelong_cover.jpg',NULL,NULL,'[\"https://images.travel.com/chimelong_1.jpg\", \"https://images.travel.com/chimelong_2.jpg\"]','成人票350元，儿童/老人票245元',245.00,350.00,'CNY',0,'09:30-18:00','09:30-18:00','节假日延长开放','400-883-0083','https://www.chimelong.com','{\"weibo\": \"广州长隆\"}',98760,43210,18700,8900,4.75,23400,97,1,0,0,1,'winter','儿童节,国庆节','建议北门进，先坐小火车；自驾需另购车票；不能自带食物喂动物；提前查表演时间。','亲子家庭,动物爱好者','大型停车场；园区有餐厅；婴儿车租赁；母婴室；轮椅租赁','广州长隆野生动物世界攻略_门票_自驾','长隆,野生动物园,亲子游','长隆野生动物世界游玩攻略：路线规划+表演时间+注意事项','广州长隆野生动物世界隶属长隆旅游度假区，以大规模野生动物种群放养和自驾车观赏为特色，拥有20000余只珍奇动物，是亚洲最大的野生动物主题公园。','穿越五大洲，和动物做朋友','官方合作','长隆小编','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-10 02:00:00',NULL),(11,'468a761b-1d60-11f1-833c-c4c6e607efc7','洪崖洞','Hongya Cave',NULL,5,'休闲度假','夜景,网红,吊脚楼,火锅,拍照','中国','重庆市','重庆市','渝中区','嘉陵江滨江路88号',NULL,NULL,NULL,NULL,'地铁1号线小什字站','现实版的《千与千寻》汤屋，11层吊脚楼依山而建，夜晚灯火辉煌。在这里吃火锅看江景，感受8D魔幻城市的魅力。','洪崖洞夜景;民俗街;长江索道;千厮门大桥;洞子火锅;巴渝剧场','全年（晚上7点后灯亮）','2-3小时','http://localhost:8080/upload/51178d8d-baf0-459e-9ea4-fcb8f59061c9.png',NULL,'https://videos.travel.com/hongyadong_intro.mp4','[\"https://images.travel.com/hongyadong_1.jpg\", \"https://images.travel.com/hongyadong_2.jpg\", \"https://images.travel.com/hongyadong_3.jpg\"]','免费开放',0.00,0.00,'CNY',1,'全天开放','全天开放','商户10:00-23:00',NULL,NULL,'{\"douyin\": \"重庆洪崖洞\", \"xiaohongshu\": \"chongqing\"}',234560,98760,34500,15670,4.70,28900,100,1,1,0,1,'all',NULL,'拍照最佳点在千厮门大桥或对面江滩；不要轻信带路收费的人；电梯排队久，建议走楼梯。','年轻人,摄影爱好者,吃货','有停车场；多部电梯；公共厕所；餐饮集中','重庆洪崖洞攻略_夜景_火锅','洪崖洞,重庆夜景,网红打卡','洪崖洞拍照攻略+避坑指南+火锅推荐','洪崖洞位于重庆市核心商圈解放碑沧白路，以最具巴渝传统建筑特色的“吊脚楼”风貌为主体，依山就势，沿江而建，是重庆历史文化的见证和城市精神的象征。','闯入千与千寻的奇幻世界','用户投稿','山城妹子','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-05 11:30:00',NULL),(12,'468a792f-1d60-11f1-833c-c4c6e607efc7','布达拉宫','Potala Palace',NULL,2,'历史文化','世界遗产,朝圣,藏传佛教,高原,宏伟','中国','西藏自治区','拉萨市','城关区','北京中路35号',NULL,NULL,NULL,NULL,'拉萨市内乘出租车或步行','世界上海拔最高、最宏伟的宫殿，藏传佛教圣地。红白相间的建筑屹立在红山之巅，见证了西藏的千年历史。','白宫;红宫;灵塔殿;壁画;佛像;雪城;珍宝馆','5月-10月（氧气充足，气候宜人）','3-4小时','https://images.travel.com/potala_cover.jpg',NULL,NULL,'[\"https://images.travel.com/potala_1.jpg\", \"https://images.travel.com/potala_2.jpg\"]','旺季200元，淡季100元',100.00,200.00,'CNY',0,'09:00-16:00','09:00-16:00','周一闭馆（节假日除外）','0891-6834331','http://www.potalapalace.cn','{\"wechat\": \"布达拉宫官方\"}',67890,28760,12340,5670,4.95,12300,95,1,0,0,1,'summer','藏历新年','需提前一天预约门票；注意高原反应；禁止穿短裙短裤入内；内部禁止拍照；准备零钱添香火钱。','文化爱好者,朝圣者,摄影爱好者','有停车场；需爬坡；有讲解器租赁；游客中心提供氧气瓶','布达拉宫攻略_门票_高原反应','布达拉宫,西藏旅游,朝圣','布达拉宫参观攻略：预约方式+注意事项+最佳时间','布达拉宫位于拉萨市区西北的玛布日山上，是一座宫堡式建筑群，最初是吐蕃王朝赞普松赞干布为迎娶文成公主而兴建。是历代达赖喇嘛的冬宫居所，也是西藏政教合一的统治中心。','离天堂最近的圣殿，信仰的归宿','小编踩点','高原红','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-18 03:45:00',NULL),(13,'468a7c0d-1d60-11f1-833c-c4c6e607efc7','夫子庙-秦淮风光带','Confucius Temple-Qinhuai River',NULL,2,'历史文化','夜景,游船,小吃,古都,科举','中国','江苏省','南京市','秦淮区','贡院西街53号',NULL,NULL,NULL,NULL,'地铁1号线三山街站/3号线夫子庙站','烟笼寒水月笼沙，夜泊秦淮近酒家。这里是南京最繁华的地方，十里秦淮灯火璀璨，画舫凌波，梦回金陵。','夫子庙;江南贡院;乌衣巷;秦淮河画舫;老门东;科举博物馆','3月-5月，9月-11月（夜景全年皆宜）','3-4小时','https://images.travel.com/qinhuai_cover.jpg',NULL,NULL,'[\"https://images.travel.com/qinhuai_1.jpg\", \"https://images.travel.com/qinhuai_2.jpg\"]','免费进入（画舫船票80元）',0.00,80.00,'CNY',1,'全天开放','全天开放','画舫运营至22:00','025-52202058',NULL,'{\"xiaohongshu\": \"南京夫子庙\"}',54320,24560,9800,6540,4.60,8900,82,1,1,0,1,'all','元宵节','夫子庙小吃推荐莲湖糕团店；不要在主街买特产；夜游秦淮一定要坐船；元宵节有灯会。','文化爱好者,情侣,家庭','有停车场；多处公共厕所；游客中心；游船码头','南京夫子庙攻略_秦淮河_小吃','夫子庙,秦淮河,南京旅游','夫子庙-秦淮河游玩攻略：游船体验+美食推荐','夫子庙-秦淮风光带位于南京市秦淮区，以夫子庙为核心、十里秦淮为轴线，串联起众多名胜古迹，是南京历史文化荟萃之地，也是中国最大的传统古街市。','夜泊秦淮，梦回六朝金粉地','用户投稿','金陵客','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-20 12:15:00',NULL),(14,'468a7eab-1d60-11f1-833c-c4c6e607efc7','漓江风景区','Lijiang River Scenic Area',NULL,1,'自然风光','山水,竹筏,20元人民币,摄影,喀斯特','中国','广西壮族自治区','桂林市','阳朔县','漓江景区杨堤码头',NULL,NULL,NULL,NULL,'桂林汽车站乘大巴到杨堤或兴坪','桂林山水甲天下，漓江山水甲桂林。乘竹筏顺流而下，看两岸奇峰倒影，碧水青山，人在画中游。','杨堤-兴坪竹筏;九马画山;黄布倒影(20元背景);兴坪古镇;相公山日出','4月-10月（雨后初晴烟雨漓江最美）','1天','https://images.travel.com/lijiang_cover.jpg',NULL,'https://videos.travel.com/lijiang_intro.mp4','[\"https://images.travel.com/lijiang_1.jpg\", \"https://images.travel.com/lijiang_2.jpg\", \"https://images.travel.com/lijiang_3.jpg\"]','竹筏杨堤-兴坪段约120元/人',120.00,120.00,'CNY',0,'08:00-17:30','08:00-17:30','视水位情况调整','0773-2825502','http://www.gltour.com','{\"wechat\": \"桂林旅游\"}',76540,34560,15670,8760,4.80,15600,93,1,0,0,1,'spring','五一,国庆','选择正规竹筏；穿救生衣；准备好20元纸币拍照；建议从杨堤漂到兴坪精华段。','摄影爱好者,自然爱好者,家庭','有停车场；码头有厕所；筏工服务；兴坪古镇有餐饮','桂林漓江攻略_竹筏_20元人民币','漓江,桂林山水,阳朔','漓江竹筏游攻略：最佳路线+拍照点+避坑指南','漓江发源于桂林市兴安县的猫儿山，流经桂林、阳朔，全长164公里。沿江风光秀丽，奇峰林立，构成了一幅绚丽多彩的画卷，是桂林山水的精华所在。','舟行碧波上，人在画中游','小编踩点','山水甲天下','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-08 05:20:00',NULL),(15,'468a9a1a-1d60-11f1-833c-c4c6e607efc7','崂山风景区','Mount Lao Scenic Area',NULL,1,'自然风光','道教名山,海上第一山,登山,观海,道教文化','中国','山东省','青岛市','崂山区','崂山路',NULL,NULL,NULL,NULL,'公交104路、304路崂山游客中心','泰山虽云高，不如东海崂。中国海岸线第一高峰，山海相连，道观林立。在这里既能看海又能登山访道。','巨峰顶(最高峰);太清宫;仰口海滩;九水十八潭;明霞洞;棋盘石','4月-11月（夏季避暑，秋季看叶）','1天','https://images.travel.com/laoshan_cover.jpg',NULL,NULL,'[\"https://images.travel.com/laoshan_1.jpg\", \"https://images.travel.com/laoshan_2.jpg\"]','南线票130元，北线票90元，巨峰票120元',90.00,130.00,'CNY',0,'07:00-17:00','07:00-17:00','淡季时间缩短','0532-88899000','http://www.qdlaoshan.cn','{\"wechat\": \"青岛崂山\"}',34560,15670,6700,3450,4.65,5600,78,1,0,0,1,'summer',NULL,'穿登山鞋；带够水和干粮；索道可节省体力；建议玩1-2条线路；注意天气预报。','登山爱好者,道教文化爱好者','多个停车场；索道；山上小卖部；游客中心','青岛崂山攻略_门票_登山路线','崂山,青岛旅游,道教名山','崂山一日游攻略：路线选择+交通+注意事项','崂山位于青岛市东部，濒临黄海，是中国重要的道教名山和旅游胜地。主峰巨峰海拔1132.7米，是中国大陆海岸线第一高峰，素有“海上第一名山”之称。','海上第一名山，寻仙问道处','官方合作','青岛小哥','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-05 01:45:00',NULL),(16,'468a9c8c-1d60-11f1-833c-c4c6e607efc7','鼓浪屿','Gulangyu Island',NULL,5,'休闲度假','世界遗产,小岛,万国建筑,钢琴,文艺,沙滩','中国','福建省','厦门市','思明区','鼓浪屿岛',NULL,NULL,NULL,NULL,'厦门轮渡码头乘船至鼓浪屿三丘田码头','万国建筑博览，钢琴之岛，猫岛。没有车马喧嚣，只有小巷蜿蜒，琴声悠扬，找个咖啡馆发呆就是最好的旅行。','日光岩;菽庄花园;皓月园;龙头路商业街;风琴博物馆;沙滩;最美转角','3月-5月，10月-12月（避开台风季）','1-2天','https://images.travel.com/gulangyu_cover.jpg',NULL,NULL,'[\"https://images.travel.com/gulangyu_1.jpg\", \"https://images.travel.com/gulangyu_2.jpg\", \"https://images.travel.com/gulangyu_3.jpg\"]','上岛免费（需购买35元船票），景点联票90元',0.00,90.00,'CNY',1,'全天开放','全天开放','轮渡5:30-0:30运营','0592-2060777','http://www.gly.cn','{\"xiaohongshu\": \"鼓浪屿\"}',89000,39870,17800,9800,4.75,18700,94,1,0,0,1,'autumn',NULL,'提前2-3天购买船票；穿舒适的鞋子；不要买路边的海鲜；建议住一晚感受清晨和夜晚。','文艺青年,情侣,家庭','码头有游客中心；岛上有观光车；公共厕所；大量民宿','厦门鼓浪屿攻略_船票_民宿','鼓浪屿,厦门旅游,文艺小岛','鼓浪屿一日游攻略：拍照打卡+美食+避坑指南','鼓浪屿位于厦门半岛西南隅，与厦门岛隔海相望，因岛西南方有一礁石，每当涨潮水涌，浪击礁石，声似擂鼓，人们称“鼓浪石”而得名。岛上气候宜人，四季如春，有“海上花园”之称。','漫步万国建筑，聆听海岛琴声','小编踩点','海岛姑娘','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-12 07:10:00',NULL),(17,'468a9fe7-1d60-11f1-833c-c4c6e607efc7','莫高窟','Mogao Caves',NULL,2,'历史文化','世界遗产,佛教艺术,壁画,丝路,研学','中国','甘肃省','酒泉市','敦煌市','莫高窟景区',NULL,NULL,NULL,NULL,'敦煌市区乘绿皮公交12路','沙漠中的艺术宝库，千年的佛教画廊。735个洞窟，4.5万平方米壁画，2415尊彩塑，每一眼都是与千年的对望。','九层楼;藏经洞;飞天壁画;反弹琵琶;敦煌壁画;特窟参观','5月-10月（9月最佳，葡萄熟了）','半天','https://images.travel.com/mogao_cover.jpg',NULL,'https://videos.travel.com/mogao_intro.mp4','[\"https://images.travel.com/mogao_1.jpg\", \"https://images.travel.com/mogao_2.jpg\"]','A类票238元（看8个窟+电影），B类票100元（看4个窟）',100.00,238.00,'CNY',0,'08:00-18:00','08:00-18:00','淡季缩短开放时间','0937-8869060','https://www.mogaoku.com','{\"wechat\": \"敦煌研究院\"}',56780,26780,10900,5430,4.92,14500,96,1,1,0,1,'autumn','国庆节','提前一个月预约门票；禁止拍照；建议看两部介绍电影；特窟需另外购票；带望远镜看细节。','历史文化爱好者,艺术爱好者,研学团','数字展示中心停车场；有讲解服务；书店和文创店；需存包','敦煌莫高窟攻略_门票_特窟','莫高窟,敦煌旅游,壁画','莫高窟参观攻略：票务预约+洞窟介绍+注意事项','莫高窟位于敦煌市东南25公里处鸣沙山东麓的断崖上，是中国四大石窟之一，也是世界上现存规模最宏大、保存最完好的佛教艺术宝库，1987年被列为世界文化遗产。','一眼千年，聆听丝路驼铃','官方合作','敦煌守护者','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-25 06:30:00',NULL);
UNLOCK TABLES;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '酒店ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '酒店名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片列表',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `star_level` int DEFAULT NULL COMMENT '星级',
  `rating` int DEFAULT '0' COMMENT '评分',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `facilities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设施',
  `status` int DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_city` (`city`) USING BTREE,
  KEY `idx_star_level` (`star_level`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE,
  KEY `idx_like_count` (`like_count`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='酒店表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
INSERT INTO `hotel` (`id`, `name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `star_level`, `rating`, `like_count`, `collect_count`, `comment_count`, `facilities`, `status`, `create_time`, `update_time`) VALUES (4,'北京王府井希尔顿酒店','北京','北京','北京市东城区王府井东街8号','位于王府井商业区中心，交通便利，设施豪华。','http://localhost:8080/upload/e7f9d92b-eec9-4c6f-94e2-6e3328898820.png','[\"https://example.com/hotel1_1.jpg\", \"https://example.com/hotel1_2.jpg\"]',1200.00,1,5,800,600,250,'1',1,'2026-02-14 17:21:56','2026-03-08 22:19:50'),(5,'上海外滩华尔道夫酒店','上海','上海','上海市黄浦区中山东一路2号','百年历史建筑，俯瞰外滩和黄浦江美景。','https://example.com/hotel2.jpg','[\"https://example.com/hotel2_1.jpg\", \"https://example.com/hotel2_2.jpg\"]',1500.00,5,5,900,700,280,'免费WiFi,健身房,游泳池,餐厅,停车场,SPA',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,'杭州西子湖四季酒店','杭州','浙江','浙江省杭州市西湖区龙井路5号','毗邻西湖，环境优美，中式园林设计。','https://example.com/hotel3.jpg','[\"https://example.com/hotel3_1.jpg\", \"https://example.com/hotel3_2.jpg\"]',1300.00,5,5,850,650,260,'免费WiFi,健身房,游泳池,餐厅,停车场,SPA',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,'广州四季酒店','广州','广东','广东省广州市天河区珠江西路5号','位于珠江新城，现代化豪华酒店。','https://example.com/hotel4.jpg','[\"https://example.com/hotel4_1.jpg\", \"https://example.com/hotel4_2.jpg\"]',1100.00,5,4,750,550,230,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,'深圳福田香格里拉大酒店','深圳','广东','广东省深圳市福田区益田路4088号','位于福田中心区，交通便利，设施齐全。','https://example.com/hotel5.jpg','[\"https://example.com/hotel5_1.jpg\", \"https://example.com/hotel5_2.jpg\"]',980.00,5,4,700,500,210,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,'成都太古里博舍酒店','成都','四川','四川省成都市锦江区笔帖式街81号','位于太古里商业区，融合传统与现代设计。','https://example.com/hotel6.jpg','[\"https://example.com/hotel6_1.jpg\", \"https://example.com/hotel6_2.jpg\"]',1080.00,5,5,820,620,240,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'三亚亚特兰蒂斯酒店','三亚','海南','海南省三亚市海棠湾海棠北路36号','豪华度假酒店，有水族馆和水上乐园。','http://localhost:8080/upload/f03c3668-7889-48b6-812f-a2a661b56955.png','[\"https://example.com/hotel7_1.jpg\", \"https://example.com/hotel7_2.jpg\"]',1800.00,5,5,950,750,300,'免费WiFi,健身房,游泳池,餐厅,停车场,水上乐园',1,'2026-02-14 17:21:56','2026-03-12 22:51:58'),(11,'厦门鼓浪屿海景酒店','厦门','福建','福建省厦门市思明区鼓浪屿','岛上的精品酒店，环境清幽，海景优美。','https://example.com/hotel8.jpg','[\"https://example.com/hotel8_1.jpg\", \"https://example.com/hotel8_2.jpg\"]',680.00,4,4,600,400,180,'免费WiFi,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,'西安香格里拉大酒店','西安','陕西','陕西省西安市雁塔区科技路38号乙','位于高新区，靠近大雁塔等景点。','https://example.com/hotel9.jpg','[\"https://example.com/hotel9_1.jpg\", \"https://example.com/hotel9_2.jpg\"]',880.00,5,4,650,450,200,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(13,'南京金陵饭店','南京','江苏','江苏省南京市鼓楼区汉中路2号','南京地标性建筑，历史悠久，服务优质。','https://example.com/hotel10.jpg','[\"https://example.com/hotel10_1.jpg\", \"https://example.com/hotel10_2.jpg\"]',780.00,5,4,620,420,190,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `like_record`
--

DROP TABLE IF EXISTS `like_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `like_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '点赞类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区 comment-评论',
  `item_id` bigint NOT NULL COMMENT '点赞对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_item` (`user_id`,`item_type`,`item_id`) USING BTREE,
  KEY `idx_item_type_id` (`item_type`,`item_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='点赞记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like_record`
--

LOCK TABLES `like_record` WRITE;
INSERT INTO `like_record` (`id`, `user_id`, `item_type`, `item_id`, `create_time`) VALUES (1,1,'attraction',1,'2026-02-14 17:21:56'),(2,1,'hotel',1,'2026-02-14 17:21:56'),(3,1,'food',1,'2026-02-14 17:21:56'),(4,2,'attraction',2,'2026-02-14 17:21:56'),(5,2,'hotel',2,'2026-02-14 17:21:56'),(6,2,'food',2,'2026-02-14 17:21:56'),(7,3,'attraction',3,'2026-02-14 17:21:56'),(8,3,'hotel',3,'2026-02-14 17:21:56'),(9,3,'food',3,'2026-02-14 17:21:56'),(10,4,'attraction',4,'2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `login_info`
--

DROP TABLE IF EXISTS `login_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '登录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `ipaddr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作系统',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录状态 0-失败 1-成功',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提示消息',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_login_time` (`login_time`) USING BTREE,
  KEY `idx_ipaddr` (`ipaddr`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='登录信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_info`
--

LOCK TABLES `login_info` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `login_log`
--

DROP TABLE IF EXISTS `login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `account` varchar(50) DEFAULT NULL COMMENT '登录账号',
  `ipaddr` varchar(128) DEFAULT NULL COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `status` varchar(10) DEFAULT NULL COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT NULL COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_username` (`username`),
  KEY `idx_account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_log`
--

LOCK TABLES `login_log` WRITE;
INSERT INTO `login_log` (`id`, `user_id`, `username`, `account`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (69,NULL,NULL,'20260314813731','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','0','用户不存在','2026-04-16 21:36:40'),(104,21,'王涛','20260314207018','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','1','登录成功','2026-05-13 20:51:56'),(105,22,'yun xiao','20260512079914','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','1','登录成功','2026-05-13 20:52:06'),(110,15,'爱吃薯条的小懒猪','20260219132871','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','1','登录成功','2026-05-13 21:34:37');
UNLOCK TABLES;

--
-- Table structure for table `monthly_recommendations`
--

DROP TABLE IF EXISTS `monthly_recommendations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_recommendations` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '推荐ID，自增',
  `month_id` int NOT NULL COMMENT '关联月份ID',
  `destination_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '目的地名称',
  `image_url` text COLLATE utf8mb4_bin COMMENT '图片URL',
  `description` text COLLATE utf8mb4_bin COMMENT '目的地描述',
  `tags` json DEFAULT NULL COMMENT '标签（JSON数组格式，如：["雪山","滑雪","温泉"]）',
  `season_features` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '当季特色',
  `climate_info` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '气候信息',
  `travel_tips` text COLLATE utf8mb4_bin COMMENT '旅行小贴士',
  `recommended_days` tinyint DEFAULT NULL COMMENT '建议游玩天数',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序（数字越小越靠前）',
  `is_recommended` tinyint(1) DEFAULT '1' COMMENT '是否推荐（0：不推荐，1：推荐）',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_month_id` (`month_id`) COMMENT '月份ID索引',
  KEY `idx_is_recommended` (`is_recommended`) COMMENT '推荐状态索引',
  KEY `idx_sort_order` (`sort_order`) COMMENT '排序索引',
  CONSTRAINT `monthly_recommendations_ibfk_1` FOREIGN KEY (`month_id`) REFERENCES `months` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='月份旅行推荐内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_recommendations`
--

LOCK TABLES `monthly_recommendations` WRITE;
INSERT INTO `monthly_recommendations` (`id`, `month_id`, `destination_name`, `image_url`, `description`, `tags`, `season_features`, `climate_info`, `travel_tips`, `recommended_days`, `sort_order`, `is_recommended`, `view_count`, `created_at`, `updated_at`) VALUES (1,1,'哈尔滨','/images/harbin.jpg','冰雪之城，冬季旅游胜地','[\"冰雪\", \"冰雕\", \"滑雪\"]','哈尔滨国际冰雪节，欣赏精美冰雕雪雕','气温约-20℃至-10℃，寒冷干燥','注意保暖，穿羽绒服、雪地靴，携带保温杯',3,1,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(2,1,'三亚','/images/sanya.jpg','避寒胜地，热带海滨风光','[\"海滩\", \"阳光\", \"海鲜\"]','温暖如春，适合避寒度假','气温约20℃-28℃，舒适宜人','防晒必备，可下海游泳',4,2,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(3,1,'长白山','/images/changbaishan.jpg','冬季滑雪泡温泉的绝佳去处','[\"滑雪\", \"温泉\", \"雪景\"]','林海雪原，雾凇奇观','气温约-25℃至-15℃，山区更冷','滑雪需注意安全，泡温泉要控制时间',3,3,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(4,1,'昆明','/images/kunming.jpg','春城，四季如春','[\"暖冬\", \"花卉\", \"民族风情\"]','冬季温暖，鲜花盛开','气温约5℃-18℃，早晚温差大','早晚需穿外套，可游览滇池、石林',2,4,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(5,7,'青岛','/images/qingdao.jpg','海滨城市，避暑胜地','[\"海滩\", \"啤酒\", \"海鲜\"]','青岛国际啤酒节，海滨浴场开放','气温约22℃-28℃，凉爽海风','品尝海鲜啤酒，注意防晒',3,1,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(6,7,'承德避暑山庄','/images/chengde.jpg','清代皇家避暑胜地','[\"古迹\", \"避暑\", \"园林\"]','山区凉爽，适合避暑','气温约20℃-28℃，山区凉爽','游览皇家园林，了解清代历史',2,2,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(7,7,'九寨沟','/images/jiuzhaigou.jpg','童话世界，夏季水量充沛','[\"山水\", \"瀑布\", \"原始森林\"]','水量充沛，瀑布壮观','气温约15℃-25℃，昼夜温差大','带雨具，注意高原反应',4,3,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(8,7,'呼伦贝尔','/images/hulunbeier.jpg','大草原最美的季节','[\"草原\", \"骑马\", \"蒙古包\"]','草原最美季节，那达慕大会','气温约18℃-28℃，昼夜温差大','体验草原文化，骑马需注意安全',4,4,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06');
UNLOCK TABLES;

--
-- Table structure for table `months`
--

DROP TABLE IF EXISTS `months`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `months` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '月份ID，自增',
  `month_name` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '月份名称（如：一月、二月等）',
  `month_order` tinyint NOT NULL COMMENT '月份顺序（1-12）',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用（0：禁用，1：启用）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='月份配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `months`
--

LOCK TABLES `months` WRITE;
INSERT INTO `months` (`id`, `month_name`, `month_order`, `is_active`, `created_at`, `updated_at`) VALUES (1,'一月',1,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(2,'二月',2,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(3,'三月',3,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(4,'四月',4,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(5,'五月',5,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(6,'六月',6,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(7,'七月',7,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(8,'八月',8,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(9,'九月',9,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(10,'十月',10,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(11,'十一月',11,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(12,'十二月',12,1,'2026-02-22 14:23:06','2026-02-22 14:23:06');
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源',
  `view_count` int DEFAULT '0' COMMENT '浏览数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `status` int DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_view_count` (`view_count`) USING BTREE,
  KEY `idx_like_count` (`like_count`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='新闻资讯表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
INSERT INTO `news` (`id`, `title`, `content`, `cover_image`, `source`, `view_count`, `like_count`, `collect_count`, `comment_count`, `status`, `create_time`, `update_time`) VALUES (3,'2024年五一假期旅游市场火爆，国内游人次突破2亿','据文化和旅游部数据中心测算，2024年五一假期全国国内旅游出游人次达到2.1亿，同比增长120%，旅游收入达到1200亿元，同比增长150%。','http://localhost:8080/upload/f3235027-bacf-4650-a976-e634e1a3a266.png','人民日报',50006,2000,1500,800,1,'2026-02-14 17:21:56','2026-03-08 14:49:56'),(4,'故宫博物院推出\"清明上河图3.0\"数字艺术展','故宫博物院将于5月1日起推出\"清明上河图3.0\"数字艺术展，通过现代科技手段重现北宋都城汴京的繁华景象。','http://localhost:8080/upload/006a97b8-0305-4916-9a60-a7294505654b.png','新华社',35003,1500,1200,600,1,'2026-02-14 17:21:56','2026-04-05 13:25:31'),(5,'三亚国际旅游岛建设取得新进展，免税购物销售额突破500亿','三亚国际旅游岛建设近年来取得显著成效，2024年第一季度免税购物销售额达到520亿元，同比增长35%。','https://example.com/news3.jpg','海南日报',28001,1200,900,450,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,'2024年中国国际旅游交易会在上海开幕','为期三天的2024年中国国际旅游交易会在上海新国际博览中心开幕，来自全球50多个国家和地区的旅游机构参展。','https://example.com/news4.jpg','上海日报',25000,1000,800,400,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,'中国高铁旅游线路成为热门选择，\"高铁游\"成新趋势','随着高铁网络的不断完善，\"高铁游\"成为越来越多游客的选择。2024年第一季度，高铁沿线城市旅游收入同比增长45%。','https://example.com/news5.jpg','经济日报',32000,1300,1000,550,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,'九寨沟景区将于4月1日起恢复全域开放','经过两年的生态修复，九寨沟景区将于2024年4月1日起恢复全域开放，开放区域包括之前因地震关闭的部分景点。','https://example.com/news6.jpg','四川日报',40000,1800,1300,700,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,'2024年中国旅游日主题确定为\"美好中国，幸福旅程\"','文化和旅游部宣布，2024年中国旅游日主题为\"美好中国，幸福旅程\"，全国各地将推出多项旅游惠民措施。','https://example.com/news7.jpg','文旅部官网',22000,900,700,350,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'北京冬奥会场馆成为热门旅游景点，年接待游客突破1000万','北京冬奥会结束后，冬奥会场馆成为热门旅游景点。2023年，冬奥会场馆群共接待游客1050万人次，旅游收入达到80亿元。','https://example.com/news8.jpg','北京日报',38000,1600,1100,650,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(11,'全国首个\"智慧旅游城市\"试点在杭州启动','杭州市成为全国首个\"智慧旅游城市\"试点，通过大数据、人工智能等技术，为游客提供更加便捷、个性化的旅游服务。','https://example.com/news9.jpg','科技日报',26001,1100,850,420,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,'中国出境游市场逐步恢复，2024年第一季度出境游人次同比增长200%','随着全球疫情防控形势的好转，中国出境游市场逐步恢复。2024年第一季度，中国公民出境游人次达到1200万，同比增长200%。','http://localhost:8080/upload/01a608e4-d51e-424e-b293-05d35bc53816.png','环球时报',45003,1900,1400,750,1,'2026-02-14 17:21:56','2026-04-05 13:30:59');
UNLOCK TABLES;

--
-- Table structure for table `online_users`
--

DROP TABLE IF EXISTS `online_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `online_users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `role` varchar(20) NOT NULL COMMENT '角色',
  `login_ip` varchar(50) NOT NULL COMMENT '登录IP',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `last_activity_time` datetime NOT NULL COMMENT '最后活动时间',
  `browser` varchar(100) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) DEFAULT NULL COMMENT '操作系统',
  `status` varchar(20) NOT NULL COMMENT '在线状态',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_last_activity_time` (`last_activity_time`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='在线用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `online_users`
--

LOCK TABLES `online_users` WRITE;
INSERT INTO `online_users` (`id`, `username`, `nickname`, `role`, `login_ip`, `login_time`, `last_activity_time`, `browser`, `os`, `status`) VALUES (15,'爱吃薯条的小懒猪','小可爱','admin','0:0:0:0:0:0:0:1','2026-05-13 21:34:37','2026-05-13 21:34:37','Chrome','Windows','online'),(19,'李小明','李小明','vip','0:0:0:0:0:0:0:1','2026-05-13 20:53:51','2026-05-13 20:53:51','Chrome','Windows','online'),(21,'王涛','王涛','vip','0:0:0:0:0:0:0:1','2026-05-13 20:51:56','2026-05-13 20:51:56','Chrome','Windows','online'),(22,'yun xiao','yun xiao','vip','0:0:0:0:0:0:0:1','2026-05-13 20:52:06','2026-05-13 20:52:06','Chrome','Windows','online');
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品类型 ticket-票务 product-商品',
  `item_id` bigint NOT NULL COMMENT '商品ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品名称',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '订单金额',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '订单状态 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-已退款',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
INSERT INTO `orders` (`id`, `user_id`, `order_no`, `item_type`, `item_id`, `item_name`, `amount`, `quantity`, `remark`, `status`, `create_time`, `update_time`, `image`) VALUES (59,15,'202603220005','product',15,'北京烤鸭礼盒',792.00,4,'','1','2026-03-22 18:02:02','2026-03-22 18:02:07','http://localhost:8080/upload/dc857d4f-9991-450e-849a-6d4a43a493c8.png'),(61,15,'202604260001','product',6,'云南普洱茶',368.00,1,'','0','2026-04-26 18:40:36','2026-04-26 18:40:36','https://example.com/product6.jpg'),(62,15,'202604260002','food',4,'上海小笼包',38.00,1,'','0','2026-04-26 18:57:45','2026-04-26 18:57:45','http://localhost:8080/upload/396217d8-909d-4f79-9176-38711f8d185d.png'),(63,15,'202604260003','food',9,'西安肉夹馍',15.00,1,'','1','2026-04-26 18:58:08','2026-04-26 18:58:12','https://example.com/food7.jpg');
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付流水号',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式 alipay-支付宝 wechat-微信 card-银行卡',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '支付状态 0-待支付 1-支付成功 2-支付失败 3-已取消 4-退款中 5-已退款',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_payment_no` (`payment_no`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='支付表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
INSERT INTO `payment` (`id`, `order_id`, `user_id`, `payment_no`, `amount`, `payment_method`, `status`, `create_time`, `update_time`) VALUES (28,54,15,'PAY17741725170458530',198.00,'支付宝','2','2026-03-22 17:41:57','2026-03-22 17:41:57'),(29,56,15,'PAY17741727547711735',398.00,'支付宝','1','2026-03-22 17:45:55','2026-03-22 17:45:55'),(30,57,15,'PAY17741727891287678',198.00,'支付宝','1','2026-03-22 17:46:29','2026-03-22 17:46:29'),(31,57,15,'PAY17741728034593460',198.00,'支付宝','1','2026-03-22 17:46:43','2026-03-22 17:46:43'),(32,57,15,'PAY17741728067189122',198.00,'微信','1','2026-03-22 17:46:47','2026-03-22 17:46:47'),(33,58,15,'PAY17741728316456864',398.00,'支付宝','1','2026-03-22 17:47:12','2026-03-22 17:47:12'),(34,59,15,'PAY17741737265069875',3168.00,'银联','1','2026-03-22 18:02:07','2026-03-22 18:02:07'),(35,60,15,'PAY17771999672011221',298.00,'支付宝','2','2026-04-26 18:39:27','2026-04-26 18:39:27'),(39,61,15,'PAY17772010160339082',368.00,'微信','2','2026-04-26 18:56:56','2026-04-26 18:56:56'),(40,63,15,'PAY17772010917083913',15.00,'微信','1','2026-04-26 18:58:12','2026-04-26 18:58:12');
UNLOCK TABLES;

--
-- Table structure for table `points_products`
--

DROP TABLE IF EXISTS `points_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_products` (
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_products`
--

LOCK TABLES `points_products` WRITE;
INSERT INTO `points_products` (`id`, `name`, `description`, `points_price`, `stock`, `image_url`, `status`, `create_time`, `update_time`) VALUES (1,'小米手环','智能运动手环',500,100,'https://via.placeholder.com/300x200',1,'2026-04-26 14:42:13','2026-04-26 14:42:13'),(2,'蓝牙耳机','无线蓝牙耳机',800,50,'https://via.placeholder.com/300x200',1,'2026-04-26 14:42:13','2026-04-26 14:42:13'),(3,'充电宝','大容量移动电源',300,200,'https://via.placeholder.com/300x200',1,'2026-04-26 14:42:13','2026-04-26 14:42:13');
UNLOCK TABLES;

--
-- Table structure for table `points_record`
--

DROP TABLE IF EXISTS `points_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_record` (
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_record`
--

LOCK TABLES `points_record` WRITE;
INSERT INTO `points_record` (`id`, `user_id`, `points`, `before_points`, `after_points`, `type`, `remark`, `create_time`) VALUES (1,15,15,0,15,'INCOME','支付订单获得积分','2026-04-26 18:58:11');
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片列表',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `stock` int DEFAULT '0' COMMENT '库存',
  `rating` int DEFAULT '0' COMMENT '评分',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签',
  `status` int DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `commodity` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_city` (`city`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE,
  KEY `idx_like_count` (`like_count`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='特产/纪念品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
INSERT INTO `product` (`id`, `name`, `type`, `city`, `province`, `description`, `cover_image`, `images`, `price`, `stock`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`, `create_time`, `update_time`, `commodity`) VALUES (1,'北京烤鸭礼盒','食品','北京','北京','真空包装的北京烤鸭，方便携带，味道正宗。','http://localhost:8080/upload/dc857d4f-9991-450e-849a-6d4a43a493c8.png','https://example.com/product1_1.jpg,https://example.com/product1_2.jpg',198.00,500,5,600,400,200,'北京特产,食品,烤鸭',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(2,'上海雪花膏','化妆品','上海','上海','上海传统化妆品，滋润保湿，香气宜人。','https://example.com/product2.jpg','https://example.com/product2_1.jpg,https://example.com/product2_2.jpg',68.00,1000,4,400,300,150,'上海特产,化妆品,传统',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(3,'杭州西湖龙井茶叶','食品','杭州','浙江','西湖龙井是中国十大名茶之一，香气清高，滋味鲜爽。','https://example.com/product3.jpg','https://example.com/product3_1.jpg,https://example.com/product3_2.jpg',298.00,300,5,700,500,250,'杭州特产,茶叶,龙井',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(4,'四川大熊猫玩偶','工艺品','成都','四川','可爱的大熊猫玩偶，是四川的标志性纪念品。','https://example.com/product4.jpg','https://example.com/product4_1.jpg,https://example.com/product4_2.jpg',88.00,800,5,500,400,180,'四川特产,工艺品,熊猫',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(5,'广东广绣','工艺品','广州','广东','广绣是中国四大名绣之一，图案精美，工艺精湛。','https://example.com/product5.jpg','https://example.com/product5_1.jpg,https://example.com/product5_2.jpg',398.00,200,5,450,350,160,'广东特产,工艺品,刺绣',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(6,'云南普洱茶','食品','昆明','云南','普洱茶是云南特产，具有降脂减肥、降压、抗动脉硬化等功效。','https://example.com/product6.jpg','https://example.com/product6_1.jpg,https://example.com/product6_2.jpg',368.00,400,5,650,450,220,'云南特产,茶叶,普洱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(7,'西安兵马俑模型','工艺品','西安','陕西','兵马俑模型是西安的特色纪念品，做工精细，造型逼真。','https://example.com/product7.jpg','https://example.com/product7_1.jpg,https://example.com/product7_2.jpg',128.00,600,4,350,250,130,'西安特产,工艺品,兵马俑',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(8,'湖南湘绣','工艺品','长沙','湖南','湘绣是中国四大名绣之一，以其精湛的刺绣技艺和独特的艺术风格著称。','https://example.com/product8.jpg','https://example.com/product8_1.jpg,https://example.com/product8_2.jpg',328.00,250,4,300,200,110,'湖南特产,工艺品,刺绣',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(9,'福建安溪铁观音','食品','泉州','福建','铁观音是中国十大名茶之一，香气馥郁持久，滋味醇厚甘鲜。','https://example.com/product9.jpg','https://example.com/product9_1.jpg,https://example.com/product9_2.jpg',268.00,350,5,550,350,190,'福建特产,茶叶,铁观音',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(10,'新疆和田玉饰品','珠宝','乌鲁木齐','新疆','和田玉是中国四大名玉之一，质地温润，色泽优美。','https://example.com/product10.jpg','https://example.com/product10_1.jpg,https://example.com/product10_2.jpg',888.00,150,5,400,300,140,'新疆特产,珠宝,和田玉',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1');
UNLOCK TABLES;

--
-- Table structure for table `recommend`
--

DROP TABLE IF EXISTS `recommend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recommend` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `details` varchar(5000) COLLATE utf8mb4_bin DEFAULT NULL,
  `state` int DEFAULT '0' COMMENT '状态',
  `type` int DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='推荐';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommend`
--

LOCK TABLES `recommend` WRITE;
INSERT INTO `recommend` (`id`, `image`, `name`, `description`, `details`, `state`, `type`) VALUES (7,'http://localhost:8080/upload/7292fcb2-1e6f-493e-8e1f-65e3403e5618.png','故宫博物院','明清皇家宫殿，世界文化遗产','故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。故宫以三大殿为中心，占地面积约72万平方米，建筑面积约15万平方米，有大小宫殿七十多座，房屋九千余间。故宫是世界上现存规模最大、保存最为完整的木质结构古建筑之一，是国家AAAAA级旅游景区，1961年被列为第一批全国重点文物保护单位；1987年被列为世界文化遗产。',0,NULL),(8,'http://localhost:8080/upload/939df816-4cf1-4005-a46a-83f0d1f98f23.png','长城','世界新七大奇迹之一','长城又称万里长城，是中国古代的军事防御工事，是一道高大、坚固而且连绵不断的长垣，用以限隔敌骑的行动。长城不是一道单纯孤立的城墙，而是以城墙为主体，同大量的城、障、亭、标相结合的防御体系。长城修筑的历史可上溯到西周时期，发生在首都镐京的著名典故“烽火戏诸侯”就源于此。春秋战国时期列国争霸，互相防守，长城修筑进入第一个高潮，但此时修筑的长度都比较短。秦灭六国统一天下后，秦始皇连接和修缮战国长城，始有万里长城之称。明朝是最后一个大修长城的朝代，今天人们所看到的长城多是此时修筑。',0,NULL),(9,'http://localhost:8080/upload/96cba0e1-e8ef-4335-9f45-6e167ba90181.png','西湖','人间天堂，杭州明珠','西湖位于浙江省杭州市西湖区龙井路1号，杭州市区西部，景区总面积49平方千米，汇水面积为21.22平方千米，湖面面积为6.38平方千米。西湖有100多处公园景点，有“西湖十景”、“新西湖十景”、“三评西湖十景”之说，有60多处国家、省、市级重点文物保护单位和20多座博物馆，有断桥、雷峰塔、钱王祠、净慈寺、苏小小墓等景点。2007年，杭州市西湖风景名胜区被评为“国家AAAAA级旅游景区”。2011年6月24日，“杭州西湖文化景观”正式被列入《世界遗产名录》。',0,NULL),(10,'http://localhost:8080/upload/ad066095-c0af-43dd-a7ae-708e633bdb68.png','兵马俑','世界第八大奇迹','兵马俑，即秦始皇兵马俑，亦简称秦兵马俑或秦俑，是古代墓葬雕塑的一个类别。位于今陕西省西安市临潼区秦始皇陵以东1.5千米处的兵马俑坑内。兵马俑是制成兵马（战车、战马、士兵）形状的殉葬品。1961年3月4日，秦始皇陵被国务院公布为第一批全国重点文物保护单位。1974年3月，兵马俑被发现。1987年，秦始皇陵及兵马俑坑被联合国教科文组织批准列入《世界遗产名录》，并被誉为“世界第八大奇迹”，先后有200多位外国元首和政府首脑参观访问，成为中国古代辉煌文明的一张金字名片，被誉为世界十大古墓稀世珍宝之一。',0,NULL),(11,'http://localhost:2025/upload/4fc497b9-0e0b-439a-bd56-170fb49f2c05.png','张家界','奇峰三千，秀水八百','张家界国家森林公园位于湖南省西北部张家界市境内，是中国第一个国家森林公园。公园自然风光以峰称奇、以谷显幽、以林见秀。其间有奇峰3000多座，这些石峰如人如兽、如器如物，形象逼真，气势壮观。峰间峡谷，溪流潺潺，浓荫蔽日。有“三千奇峰，八百秀水”之美称。公园不仅自然风光壮美绝伦，而且森林植物和野生动物资源极为丰富，森林覆盖率达98%，是一座巨大的生物宝库和天然氧吧，被称为“自然博物馆和天然植物园”。',1,NULL),(12,'http://localhost:2025/upload/87df6577-08cd-4a3e-82e4-ce8274d6e203.png','九寨沟','童话世界，人间仙境','九寨沟位于四川省阿坝藏族羌族自治州九寨沟县境内，是一条纵深50余千米的山沟谷地，总面积64297公顷，森林覆盖率超过80%。因沟内有树正寨、荷叶寨、则查洼寨等九个藏族村寨坐落在这片高山湖泊群中而得名。九寨沟国家级自然保护区主要保护对象是大熊猫、金丝猴等珍稀动物及其自然生态环境。有74种国家保护珍稀植物，有18种国家保护动物，还有丰富的古生物化石、古冰川地貌。“九寨归来不看水”，是对九寨沟景色真实的诠释。泉、瀑、河、滩108个海子，构成一个个五彩斑斓的瑶池玉盆。',1,NULL),(13,'http://localhost:2025/upload/2a99bc39-f02b-45cd-8f22-fd9eadca74ec.png','黄山','五岳归来不看山，黄山归来不看岳','黄山位于安徽省南部黄山市境内，有72峰，主峰莲花峰海拔1864米，与光明顶、天都峰并称三大黄山主峰，为36大峰之一。黄山是安徽旅游的标志，是中国十大风景名胜唯一的山岳风光。黄山原名“黟山”，因峰岩青黑，遥望苍黛而名。后因传说轩辕黄帝曾在此炼丹，故改名为“黄山”。黄山代表景观有“四绝三瀑”，四绝：奇松、怪石、云海、温泉；三瀑：人字瀑、百丈泉、九龙瀑。黄山迎客松是安徽人民热情友好的象征，承载着拥抱世界的东方礼仪文化。',1,NULL),(14,'http://localhost:2025/upload/00f6eccf-1e43-41cd-b410-1ee3fc524678.png','桂林山水','桂林山水甲天下','桂林山水是对桂林旅游资源的总称。国家AAAAA级旅游景区，中国十大风景名胜之一，桂林山水是中国山水的代表，典型的喀斯特地形构成了别具一格的桂林山水，桂林山水所指的范围很广，项目繁多。桂林山水“山青、水秀、洞奇、石美\"，包括山、水、喀斯特岩洞、石刻等等，其境内的山水风光举世闻名，千百年来享有“桂林山水甲天下”的美誉。漓江属珠江水系的桂江上游河段，发源于华南第一峰猫儿山，是桂林山水风光的精华。闻名全球的漓江景区是桂林山水的核心，漓江像一条青绸绿带，盘绕在万点峰峦之间。',1,NULL),(15,'http://localhost:2025/upload/f787f69a-274b-442a-9ed3-9b5030673180.png','鼓浪屿','海上花园，钢琴之岛','鼓浪屿原名“圆沙洲”，别名“圆洲仔”，南宋时期命“五龙屿”，明朝改称“鼓浪屿”。因岛西南方海滩上有一块两米多高、中有洞穴的礁石，每当涨潮水涌，浪击礁石，声似擂鼓，人们称“鼓浪石”，鼓浪屿因此而得名。鼓浪屿风景名胜区获得国家5A级旅游景区、全国重点文物保护单位、中国最美五大城区等荣誉。2017年7月8日，“鼓浪屿：历史国际社区”被列入世界遗产名录，成为中国第52项世界遗产项目。岛上气候宜人，四季如春，无车马喧嚣，有鸟语花香，素有“海上花园”之誉。',1,NULL),(16,'http://localhost:2025/upload/01130228-67ad-4d32-be47-25e751b8f9ba.png','亚龙湾','天下第一湾','亚龙湾是中华人民共和国海南省三亚市东郊的一处优质热带海滨风景区，距离市中心区约10公里。海湾以中心有野猪岛为中心，南有东洲岛、西洲岛，西面有东排、西排，可开展多种水上运动。亚龙湾为一个月牙湾，拥有7千米长的银白色海滩，沙质相当细腻。而这里的南海没有受到污染，海水洁净透明，远望呈现几种不同的蓝色，而水面下珊瑚种类丰富，可清楚观赏珊瑚，适合多种水面下活动包括潜水等，令海底成为了当地的旅游的核心。岸上林木郁郁葱葱。冬季这里的气温27摄氏度，水温20摄氏度，是一处理想的冬季避寒和休闲度假胜地。号称\"东方夏威夷\"。',1,NULL);
UNLOCK TABLES;

--
-- Table structure for table `review_likes`
--

DROP TABLE IF EXISTS `review_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_likes` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论点赞ID',
  `review_id` int NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_review_user` (`review_id`,`user_id`) USING BTREE,
  KEY `idx_review_id` (`review_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `fk_review_likes_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_likes_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='评论点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_likes`
--

LOCK TABLES `review_likes` WRITE;
INSERT INTO `review_likes` (`id`, `review_id`, `user_id`, `created_at`) VALUES (1,1,3,'2026-02-14 09:23:52'),(2,1,4,'2026-02-14 09:23:52'),(4,2,1,'2026-02-14 09:23:52'),(6,3,2,'2026-02-14 09:23:52'),(9,4,3,'2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `review_replies`
--

DROP TABLE IF EXISTS `review_replies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_replies` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论回复ID',
  `review_id` int NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '回复用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回复内容',
  `likes_count` int DEFAULT '0' COMMENT '点赞数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_review_id` (`review_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `fk_review_replies_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_replies_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='评论回复表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_replies`
--

LOCK TABLES `review_replies` WRITE;
INSERT INTO `review_replies` (`id`, `review_id`, `user_id`, `content`, `likes_count`, `created_at`, `updated_at`) VALUES (1,1,2,'同意！早上的长城人少，拍照效果确实更好。',5,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,1,3,'我也想去长城，请问需要爬很多台阶吗？',2,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,2,1,'是的，长城的台阶很多，建议穿舒适的鞋子。',3,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,3,4,'故宫确实很值得参观，我去年去过，印象深刻。',4,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(6,4,3,'工作日去确实人少很多，我就是工作日去的。',2,'2026-02-14 09:23:52','2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` decimal(2,1) NOT NULL COMMENT '评分(1-5)',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `visit_date` date DEFAULT NULL COMMENT '游玩日期',
  `travel_with` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '和谁一起(独自/伴侣/家庭/朋友)',
  `photos_count` int DEFAULT '0' COMMENT '照片数量',
  `likes_count` int DEFAULT '0' COMMENT '点赞数',
  `helpful_count` int DEFAULT '0' COMMENT '有用数',
  `is_verified` tinyint(1) DEFAULT '0' COMMENT '是否验证过',
  `status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '审核状态',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_attraction_rating` (`attraction_id`,`rating`) USING BTREE,
  KEY `idx_user_created` (`user_id`,`created_at`) USING BTREE,
  CONSTRAINT `fk_reviews_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
INSERT INTO `reviews` (`id`, `attraction_id`, `user_id`, `rating`, `title`, `content`, `visit_date`, `travel_with`, `photos_count`, `likes_count`, `helpful_count`, `is_verified`, `status`, `created_at`, `updated_at`) VALUES (1,1,1,5.0,'长城真的很震撼！','第一次来长城，被它的规模和历史感深深震撼。建议早上早点来，人少一些，拍照效果更好。','2024-04-01','朋友',5,20,15,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,1,2,4.5,'值得一去的景点','长城很壮观，但是人真的很多。建议穿舒适的鞋子，因为需要走很多台阶。','2024-04-10','家人',3,12,8,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,2,3,5.0,'故宫的历史文化底蕴深厚','故宫是中国历史文化的瑰宝，每一个宫殿都有其独特的故事。建议请个导游，了解更多历史背景。','2024-04-05','家人',8,25,20,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,2,4,4.5,'人太多了','故宫确实很美丽，但是人真的太多了，尤其是节假日。建议工作日来参观。','2024-04-15','朋友',2,8,5,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `safetytips`
--

DROP TABLE IF EXISTS `safetytips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `safetytips` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `description` text NOT NULL COMMENT '描述内容',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `sort_weight` int DEFAULT '0' COMMENT '排序权重(0-999)',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_sort` (`sort_weight`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='安全知识提示表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `safetytips`
--

LOCK TABLES `safetytips` WRITE;
INSERT INTO `safetytips` (`id`, `title`, `description`, `image_url`, `category_id`, `sort_weight`, `status`, `creator_id`, `updater_id`, `create_time`, `update_time`) VALUES (31,'旅游前的准备工作','旅游前要检查身份证、护照等证件，准备好常用药品，了解目的地的天气情况...','https://example.com/safety1.jpg',NULL,1,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(32,'在景区游玩的安全注意事项','在景区游玩时要遵守景区规定，不要离开指定路线，注意防滑防摔...','https://example.com/safety2.jpg',NULL,2,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(33,'食品安全注意事项','在旅游过程中要注意饮食卫生，避免食用生冷食物，选择正规餐厅...','https://example.com/safety3.jpg',NULL,3,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(34,'交通安全注意事项','在旅游过程中要遵守交通规则，选择正规的交通工具，系好安全带...','https://example.com/safety4.jpg',NULL,4,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(35,'财物安全注意事项','在旅游过程中要保管好个人财物，避免携带大量现金，注意防盗防骗...','https://example.com/safety5.jpg',NULL,5,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(36,'应急处理方法','在旅游过程中如果遇到紧急情况，要保持冷静，及时拨打当地的紧急救援电话...','https://example.com/safety6.jpg',NULL,6,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(37,'高原旅游注意事项','在高原地区旅游时要注意预防高原反应，避免剧烈运动，保持充足的睡眠...','https://example.com/safety7.jpg',NULL,7,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(38,'水上活动安全注意事项','在进行水上活动时要穿戴好救生设备，听从教练的指导，不要独自下水...','https://example.com/safety8.jpg',NULL,8,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(39,'登山安全注意事项','在登山时要选择合适的装备，遵循登山路线，注意天气变化...','https://example.com/safety9.jpg',NULL,9,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(40,'夜间出行安全注意事项','在夜间出行时要选择明亮的路线，避免前往偏僻的地方，注意自身安全...','http://localhost:8080/upload/e43d21d9-02d5-453a-82fb-ce9e04389950.png',NULL,10,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13');
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `chinese_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签分类(主题/适合人群/特色等)',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签图标',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签颜色',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
INSERT INTO `tags` (`id`, `name`, `chinese_name`, `category`, `icon`, `color`, `sort_order`, `created_at`) VALUES (1,'historical','历史古迹','theme','🏯','#8B4513',1,'2026-02-14 09:23:52'),(2,'natural','自然风光','theme','🏞️','#228B22',2,'2026-02-14 09:23:52'),(3,'modern','现代建筑','theme','🏙️','#4682B4',3,'2026-02-14 09:23:52'),(4,'cultural','文化体验','theme','🎭','#DC143C',4,'2026-02-14 09:23:52'),(5,'family','适合家庭','audience','👨‍👩‍👧‍👦','#FFD700',5,'2026-02-14 09:23:52'),(6,'romantic','浪漫情侣','audience','💕','#FF69B4',6,'2026-02-14 09:23:52'),(7,'adventure','冒险刺激','feature','🚀','#FF4500',7,'2026-02-14 09:23:52'),(8,'relaxation','休闲放松','feature','🧘','#98FB98',8,'2026-02-14 09:23:52'),(9,'food','美食之旅','feature','🍜','#FFA500',9,'2026-02-14 09:23:52'),(10,'shopping','购物天堂','feature','🛍️','#9370DB',10,'2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '票务ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型 train-火车 flight-飞机 taxi-出租车',
  `departure_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '出发城市',
  `arrival_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '到达城市',
  `departure_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出发时间',
  `arrival_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '到达时间',
  `carrier` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '承运商',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `stock` int DEFAULT '0' COMMENT '库存',
  `seat_class` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '座位等级',
  `status` int DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_departure_city` (`departure_city`) USING BTREE,
  KEY `idx_arrival_city` (`arrival_city`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='票务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
INSERT INTO `ticket` (`id`, `type`, `departure_city`, `arrival_city`, `departure_time`, `arrival_time`, `carrier`, `price`, `stock`, `seat_class`, `status`, `create_time`, `update_time`) VALUES (1,'train','北京','上海','08:00','13:00','中国铁路',553.00,100,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,'train','上海','北京','14:00','19:00','中国铁路',553.00,100,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,'flight','北京','广州','09:30','12:30','中国国航',1200.00,50,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,'flight','广州','北京','13:30','16:30','中国国航',1200.00,50,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,'train','北京','西安','07:30','12:30','中国铁路',515.00,80,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,'train','西安','北京','13:30','18:30','中国铁路',515.00,80,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,'flight','上海','成都','10:00','13:00','东方航空',1000.00,40,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,'flight','成都','上海','14:00','17:00','东方航空',1000.00,40,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,'taxi','北京','北京','全天','全天','首汽约车',2.30,1000,'普通车',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'taxi','上海','上海','全天','全天','滴滴出行',2.60,1000,'普通车',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
UNLOCK TABLES;

--
-- Table structure for table `ticket_prices`
--

DROP TABLE IF EXISTS `ticket_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_prices` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '门票价格ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `ticket_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '门票类型(成人票/学生票/儿童票/老人票)',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CNY' COMMENT '货币单位',
  `season` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适用季节(旺季/淡季/全年)',
  `start_date` date DEFAULT NULL COMMENT '有效期开始',
  `end_date` date DEFAULT NULL COMMENT '有效期结束',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '价格说明',
  `purchase_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '购票链接',
  `discount_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠信息',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_attraction_id` (`attraction_id`) USING BTREE,
  CONSTRAINT `fk_ticket_prices_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='门票价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_prices`
--

LOCK TABLES `ticket_prices` WRITE;
INSERT INTO `ticket_prices` (`id`, `attraction_id`, `ticket_type`, `price`, `currency`, `season`, `start_date`, `end_date`, `description`, `purchase_url`, `discount_info`, `created_at`, `updated_at`) VALUES (1,1,'成人票',60.00,'CNY','全年','2024-01-01','2024-12-31','长城成人门票','https://example.com/tickets/greatwall','学生票半价','2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,1,'学生票',30.00,'CNY','全年','2024-01-01','2024-12-31','长城学生门票','https://example.com/tickets/greatwall','需持有效学生证','2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,2,'成人票',80.00,'CNY','全年','2024-01-01','2024-12-31','故宫成人门票','https://example.com/tickets/forbidden_city','老人票半价','2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,2,'老人票',40.00,'CNY','全年','2024-01-01','2024-12-31','故宫老人门票','https://example.com/tickets/forbidden_city','需年满60周岁','2026-02-14 09:23:52','2026-02-14 09:23:52'),(5,3,'成人票',25.50,'EUR','全年','2024-01-01','2024-12-31','埃菲尔铁塔成人门票','https://example.com/tickets/eiffel_tower','儿童票半价','2026-02-14 09:23:52','2026-02-14 09:23:52'),(6,3,'儿童票',12.70,'EUR','全年','2024-01-01','2024-12-31','埃菲尔铁塔儿童门票','https://example.com/tickets/eiffel_tower','4-11岁','2026-02-14 09:23:52','2026-02-14 09:23:52'),(7,4,'成人票',24.00,'USD','全年','2024-01-01','2024-12-31','自由女神像成人门票','https://example.com/tickets/statue_of_liberty','老人票优惠','2026-02-14 09:23:52','2026-02-14 09:23:52'),(8,4,'老人票',18.00,'USD','全年','2024-01-01','2024-12-31','自由女神像老人门票','https://example.com/tickets/statue_of_liberty','62岁以上','2026-02-14 09:23:52','2026-02-14 09:23:52'),(9,5,'成人票',900.00,'JPY','全年','2024-01-01','2024-12-31','东京塔成人门票','https://example.com/tickets/tokyo_tower','学生票优惠','2026-02-14 09:23:52','2026-02-14 09:23:52'),(10,6,'成人票',42.00,'AUD','全年','2024-01-01','2024-12-31','悉尼歌剧院成人门票','https://example.com/tickets/sydney_opera_house','家庭票优惠','2026-02-14 09:23:52','2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `transportation`
--

DROP TABLE IF EXISTS `transportation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transportation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '交通方式ID',
  `attraction_id` int NOT NULL COMMENT '景点ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交通类型(公交/地铁/出租车/步行/缆车等)',
  `route_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '路线信息',
  `from_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '起始地点',
  `duration` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所需时间',
  `distance` decimal(8,2) DEFAULT NULL COMMENT '距离(公里)',
  `cost` decimal(8,2) DEFAULT NULL COMMENT '费用',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CNY' COMMENT '货币单位',
  `schedule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '运营时间',
  `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '交通提示',
  `is_recommended` tinyint(1) DEFAULT '0' COMMENT '是否推荐',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_attraction_id` (`attraction_id`) USING BTREE,
  CONSTRAINT `fk_transportation_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='交通方式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transportation`
--

LOCK TABLES `transportation` WRITE;
INSERT INTO `transportation` (`id`, `attraction_id`, `type`, `route_info`, `from_location`, `duration`, `distance`, `cost`, `currency`, `schedule`, `tips`, `is_recommended`, `sort_order`, `created_at`) VALUES (1,1,'bus','乘坐877路公交车直达八达岭长城','北京市区','约1.5小时',75.00,12.00,'CNY','06:00-19:00','建议提前30分钟到达车站，避免排队',1,1,'2026-02-14 09:23:52'),(2,1,'taxi','从北京市区打车前往','北京市区','约1小时',75.00,200.00,'CNY','全天','价格较贵，但时间灵活',0,2,'2026-02-14 09:23:52'),(3,2,'subway','乘坐地铁1号线或2号线到天安门东站，步行约10分钟','北京市区','约30分钟',5.00,3.00,'CNY','05:00-23:00','建议避开高峰期',1,1,'2026-02-14 09:23:52'),(4,2,'bus','乘坐1路、2路、52路等公交车到天安门站','北京市区','约40分钟',5.00,2.00,'CNY','06:00-22:00','站点较多，可能会堵车',0,2,'2026-02-14 09:23:52'),(5,3,'metro','乘坐地铁6号线到Bir-Hakeim站，步行约10分钟','巴黎市区','约20分钟',3.00,2.10,'EUR','05:30-00:30','最便捷的交通方式',1,1,'2026-02-14 09:23:52'),(6,3,'bus','乘坐42路、69路等公交车到Champ de Mars站','巴黎市区','约30分钟',3.00,2.10,'EUR','06:00-22:00','可以欣赏沿途风景',0,2,'2026-02-14 09:23:52'),(7,4,'ferry','从Battery Park乘坐渡轮前往自由岛','纽约市区','约25分钟',3.00,24.00,'USD','09:00-17:00','唯一的交通方式，建议提前购票',1,1,'2026-02-14 09:23:52'),(8,5,'train','乘坐JR山手线到滨松町站，换乘东京塔专线巴士','东京市区','约30分钟',5.00,200.00,'JPY','06:00-23:00','便捷且经济',1,1,'2026-02-14 09:23:52'),(9,6,'ferry','从Circular Quay乘坐渡轮前往悉尼歌剧院','悉尼市区','约10分钟',1.00,10.00,'AUD','07:00-22:00','可以欣赏悉尼港的美景',1,1,'2026-02-14 09:23:52'),(10,7,'cable car','乘坐桌山缆车上下山','开普敦市区','约10分钟',1.00,350.00,'ZAR','08:00-20:00','建议提前购票，避免排队',1,1,'2026-02-14 09:23:52');
UNLOCK TABLES;

--
-- Table structure for table `travel_plan`
--

DROP TABLE IF EXISTS `travel_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `travel_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '计划内容',
  `status` int DEFAULT '1' COMMENT '状态 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_start_date` (`start_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='出行计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `travel_plan`
--

LOCK TABLES `travel_plan` WRITE;
INSERT INTO `travel_plan` (`id`, `user_id`, `title`, `description`, `start_date`, `end_date`, `city`, `content`, `status`, `create_time`, `update_time`) VALUES (1,1,'五一北京之行','游览北京著名景点，品尝北京美食','2024-05-01','2024-05-05','北京','Day1: 故宫博物院\nDay2: 长城\nDay3: 颐和园\nDay4: 天安门广场\nDay5: 王府井购物',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'周末上海游','短暂的上海周末之旅','2024-04-27','2024-04-28','上海','Day1: 外滩、南京路\nDay2: 豫园、田子坊',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'杭州西湖之旅','欣赏西湖美景，品尝杭州美食','2024-05-10','2024-05-12','杭州','Day1: 西湖游览\nDay2: 灵隐寺\nDay3: 宋城',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'成都美食之旅','品尝成都各种美食，游览主要景点','2024-05-15','2024-05-18','成都','Day1: 大熊猫基地\nDay2: 宽窄巷子\nDay3: 锦里\nDay4: 都江堰',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'三亚度假','享受阳光沙滩，放松身心','2024-05-20','2024-05-25','三亚','Day1: 亚龙湾\nDay2: 天涯海角\nDay3: 蜈支洲岛\nDay4: 南山寺\nDay5: 免税店购物',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'西安文化之旅','感受古都西安的历史文化','2024-05-28','2024-06-01','西安','Day1: 兵马俑\nDay2: 华清池\nDay3: 大雁塔\nDay4: 古城墙\nDay5: 回民街',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'厦门鼓浪屿之行','游览厦门市区和鼓浪屿','2024-06-05','2024-06-08','厦门','Day1: 厦门市区\nDay2: 鼓浪屿\nDay3: 厦门大学\nDay4: 环岛路',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,8,'张家界探险之旅','挑战张家界的山峰和玻璃栈道','2024-06-10','2024-06-14','张家界','Day1: 张家界国家森林公园\nDay2: 袁家界\nDay3: 天门山\nDay4: 玻璃栈道\nDay5: 黄龙洞',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,9,'云南大理丽江游','游览大理和丽江的古城风光','2024-06-15','2024-06-20','大理,丽江','Day1: 大理古城\nDay2: 洱海\nDay3: 丽江古城\nDay4: 玉龙雪山\nDay5: 束河古镇\nDay6: 黑龙潭公园',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,10,'黄山登山之旅','挑战黄山，欣赏云海日出','2024-06-25','2024-06-28','黄山','Day1: 黄山风景区\nDay2: 光明顶\nDay3: 天都峰\nDay4: 下山',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,15,'啊实打实','啊伟大伟大a','2026-05-14','2026-05-22','撒大苏打','啊伟大伟大',1,'2026-05-03 10:45:41','2026-05-03 10:45:41');
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `age` int DEFAULT NULL COMMENT '年龄',
  `occupation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职业',
  `hobbies` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '爱好',
  `bio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `signature` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个性签名',
  `experience` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经验值',
  `points` int DEFAULT '0' COMMENT '积分',
  `online` int DEFAULT '0' COMMENT '在线状态 0-离线 1-在线',
  `status` int DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `permissions` int DEFAULT '1' COMMENT '权限，0管理员，1普通用户',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_account` (`account`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
INSERT INTO `user` (`id`, `account`, `username`, `nickname`, `password`, `email`, `phone`, `avatar`, `gender`, `age`, `occupation`, `hobbies`, `bio`, `signature`, `experience`, `points`, `online`, `status`, `create_time`, `update_time`, `permissions`) VALUES (15,'20260219132871','爱吃薯条的小懒猪','小可爱','$2a$10$l7ZIij1Ef3Sl/jGjcv.fROPL2eaFQyM3Wb9X6vE3YiZB2qqxoBwFC','2560177364@qq.com','18213200129','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','1',22,'销售','打羽毛球','喜欢的羽毛球','努力奋斗','大学刚毕业',15,0,1,'2026-02-19 15:11:47','2026-04-26 18:58:11',0),(19,'20260314838329','李小明','','$2a$10$bwzjm1U1rC6bISy20mMzguSbjtUgBI/Ef14f6s3rRvVswqnJ8DA.S','2530166354@qq.com','17312900128','http://localhost:8080/upload/d333bdc6-a972-403b-8c2f-1e5da3b73edb.jpeg',NULL,NULL,'','','','','',0,0,1,'2026-03-14 23:40:17','2026-05-09 22:23:42',1),(21,'20260314207018','王涛','','$2a$10$u634T2zfipiqXyqDZbnNgeLT5aXYNkVBobg7oWYIeErq0sAAcSWc6','17241200127@qq.com','17213200129','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','1',NULL,'','','','','',0,0,1,'2026-03-14 23:55:17','2026-05-09 14:57:54',1),(22,'20260512079914','yun xiao',NULL,'$2a$10$td6BZByvXp5iexePPPKi3.qMKLg4NgrMkfkqhyKh/3soVpaDfOBhe','17219277108@qq.com','18213200128','http://localhost:8080/upload/39adb7e5-e022-459d-862b-57da1244f8e3.png',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,1,'2026-05-12 12:52:35','2026-05-12 12:52:35',1);
UNLOCK TABLES;

--
-- Table structure for table `user_activity_log`
--

DROP TABLE IF EXISTS `user_activity_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_activity_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `activity_time` datetime NOT NULL COMMENT '活动时间',
  `activity_type` varchar(50) NOT NULL COMMENT '活动类型',
  `activity_content` varchar(500) NOT NULL COMMENT '活动内容',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_time` (`activity_time`),
  KEY `idx_activity_type` (`activity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户活动日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_activity_log`
--

LOCK TABLES `user_activity_log` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `user_emoji`
--

DROP TABLE IF EXISTS `user_emoji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_emoji` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `emoji_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情包图片路径',
  `emoji_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表情包名称',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_user_emoji_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表情包表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_emoji`
--

LOCK TABLES `user_emoji` WRITE;
INSERT INTO `user_emoji` (`id`, `user_id`, `emoji_url`, `emoji_name`, `sort_order`, `create_time`, `update_time`) VALUES (2,15,'http://localhost:8080/upload/45c4666e-37b7-4cf2-963e-bf0d4ece3042.jpeg','7904.jpg_wh860.jpg',0,'2026-05-10 16:58:22','2026-05-10 16:58:22'),(3,15,'http://localhost:8080/upload/a7ad4450-614d-4766-b7bc-d3e74bbe0922.jpeg','R-C (7).jpg',1,'2026-05-10 17:19:47','2026-05-10 17:19:47'),(4,22,'http://localhost:8080/upload/3988b87e-6425-4419-94ff-cb516ee41beb.webp','5af0f72d-bad8-4c51-9072-6c0b32def4a4.webp',0,'2026-05-12 14:22:12','2026-05-12 14:22:12');
UNLOCK TABLES;

--
-- Table structure for table `user_login_log`
--

DROP TABLE IF EXISTS `user_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `login_ip` varchar(50) NOT NULL COMMENT '登录IP',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `browser` varchar(100) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) DEFAULT NULL COMMENT '操作系统',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '登录状态(1:成功,0:失败)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_login_log`
--

LOCK TABLES `user_login_log` WRITE;
UNLOCK TABLES;

--
-- Temporary view structure for view `view_monthly_recommendations`
--

DROP TABLE IF EXISTS `view_monthly_recommendations`;
/*!50001 DROP VIEW IF EXISTS `view_monthly_recommendations`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_monthly_recommendations` AS SELECT 
 1 AS `month_id`,
 1 AS `month_name`,
 1 AS `month_order`,
 1 AS `recommendation_id`,
 1 AS `destination_name`,
 1 AS `image_url`,
 1 AS `description`,
 1 AS `tags`,
 1 AS `season_features`,
 1 AS `climate_info`,
 1 AS `travel_tips`,
 1 AS `recommended_days`,
 1 AS `sort_order`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `worldcharacteristics`
--

DROP TABLE IF EXISTS `worldcharacteristics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `worldcharacteristics` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '名称（如：长城、埃菲尔铁塔等）',
  `country` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '所属国家',
  `image` text COLLATE utf8mb4_bin COMMENT '图片URL或Base64数据',
  `description` text COLLATE utf8mb4_bin COMMENT '简要描述',
  `features` text COLLATE utf8mb4_bin COMMENT '特色/特点',
  `history` text COLLATE utf8mb4_bin COMMENT '历史背景',
  `culture` text COLLATE utf8mb4_bin COMMENT '文化意义',
  `tags` text COLLATE utf8mb4_bin COMMENT '标签（多个标签可用逗号分隔）',
  `category` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分类（如：建筑、自然景观、文物等）',
  `color` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '主题颜色（用于前端展示）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='世界特征信息表（存储各国特色建筑、景观、文化等）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `worldcharacteristics`
--

LOCK TABLES `worldcharacteristics` WRITE;
INSERT INTO `worldcharacteristics` (`id`, `name`, `country`, `image`, `description`, `features`, `history`, `culture`, `tags`, `category`, `color`) VALUES (25,'长城','中国','http://localhost:8080/upload/27ddbd99-e568-4f61-aedd-08c4faea7aff.png','中国古代的军事防御工程','世界文化遗产','始建于西周时期','中国古代文化的象征','长城,历史,文化','历史建筑','#FF0000'),(26,'埃菲尔铁塔','法国','https://example.com/world2.jpg','法国巴黎的标志性建筑','世界著名建筑','建成于1889年','法国工业革命的象征','埃菲尔铁塔,法国,建筑','城市建筑','#0000FF'),(27,'金字塔','埃及','https://example.com/world3.jpg','埃及古代法老的陵墓','世界文化遗产','始建于公元前2600年','古埃及文明的象征','金字塔,埃及,历史','历史建筑','#FFFF00'),(28,'悉尼歌剧院','澳大利亚','https://example.com/world4.jpg','澳大利亚悉尼的标志性建筑','世界文化遗产','建成于1973年','现代建筑的代表','悉尼歌剧院,澳大利亚,建筑','现代建筑','#00FF00'),(29,'自由女神像','美国','https://example.com/world5.jpg','美国纽约的标志性建筑','世界文化遗产','建成于1886年','自由和民主的象征','自由女神像,美国,建筑','城市建筑','#00FFFF'),(30,'泰姬陵','印度','https://example.com/world6.jpg','印度阿格拉的标志性建筑','世界文化遗产','建成于1653年','爱情的象征','泰姬陵,印度,建筑','历史建筑','#FF00FF'),(31,'大本钟','英国','https://example.com/world7.jpg','英国伦敦的标志性建筑','世界著名建筑','建成于1859年','英国议会制度的象征','大本钟,英国,建筑','城市建筑','#FFA500'),(32,'东京塔','日本','https://example.com/world8.jpg','日本东京的标志性建筑','世界著名建筑','建成于1958年','日本战后复兴的象征','东京塔,日本,建筑','城市建筑','#800080'),(33,'圣索菲亚大教堂','土耳其','https://example.com/world9.jpg','土耳其伊斯坦布尔的标志性建筑','世界文化遗产','建成于537年','拜占庭建筑的代表','圣索菲亚大教堂,土耳其,建筑','历史建筑','#008000'),(34,'罗马斗兽场','意大利','https://example.com/world10.jpg','意大利罗马的标志性建筑','世界文化遗产','建成于80年','古罗马文明的象征','罗马斗兽场,意大利,历史','历史建筑','#800000');
UNLOCK TABLES;

--
-- Final view structure for view `view_monthly_recommendations`
--

/*!50001 DROP VIEW IF EXISTS `view_monthly_recommendations`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_monthly_recommendations` AS select `m`.`id` AS `month_id`,`m`.`month_name` AS `month_name`,`m`.`month_order` AS `month_order`,`r`.`id` AS `recommendation_id`,`r`.`destination_name` AS `destination_name`,`r`.`image_url` AS `image_url`,`r`.`description` AS `description`,`r`.`tags` AS `tags`,`r`.`season_features` AS `season_features`,`r`.`climate_info` AS `climate_info`,`r`.`travel_tips` AS `travel_tips`,`r`.`recommended_days` AS `recommended_days`,`r`.`sort_order` AS `sort_order` from (`months` `m` left join `monthly_recommendations` `r` on(((`m`.`id` = `r`.`month_id`) and (`r`.`is_recommended` = 1)))) order by `m`.`month_order`,`r`.`sort_order` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-14 22:18:25
