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
-- Table structure for table `ai_trip_recommendation`
--

DROP TABLE IF EXISTS `ai_trip_recommendation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_trip_recommendation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `group_id` bigint DEFAULT NULL COMMENT '关联群组ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `destination` varchar(200) NOT NULL COMMENT '目的地',
  `days` int NOT NULL COMMENT '天数',
  `preferences` json DEFAULT NULL COMMENT '偏好：美食/文化/自然/购物等',
  `recommendation_json` json NOT NULL COMMENT '完整行程方案JSON',
  `status` tinyint DEFAULT '1' COMMENT '1-草稿 2-已发布 3-已采用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_group_id` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI行程推荐记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_trip_recommendation`
--

LOCK TABLES `ai_trip_recommendation` WRITE;
/*!40000 ALTER TABLE `ai_trip_recommendation` DISABLE KEYS */;
INSERT INTO `ai_trip_recommendation` (`id`, `group_id`, `user_id`, `destination`, `days`, `preferences`, `recommendation_json`, `status`, `created_at`) VALUES (1,1,101,'云南丽江',5,'[\"美食\", \"文化\", \"自然\"]','{\"day1\": \"丽江古城\", \"day2\": \"玉龙雪山\", \"day3\": \"虎跳峡\", \"day4\": \"香格里拉\", \"day5\": \"松赞林寺\"}',2,'2026-06-15 10:00:00'),(2,2,102,'川西稻城亚丁',7,'[\"自然\", \"摄影\", \"徒步\"]','{\"day1\": \"成都-康定\", \"day2\": \"康定-新都桥\", \"day3\": \"新都桥-稻城\", \"day4\": \"亚丁景区\", \"day5\": \"亚丁-稻城\", \"day6\": \"稻城-康定\", \"day7\": \"康定-成都\"}',2,'2026-06-16 09:00:00'),(3,3,103,'海南三亚',6,'[\"美食\", \"户外\"]','{\"day1\": \"三亚湾骑行\", \"day2\": \"蜈支洲岛\", \"day3\": \"亚龙湾\", \"day4\": \"天涯海角\", \"day5\": \"后海\", \"day6\": \"三亚市区\"}',1,'2026-06-17 11:00:00'),(4,4,104,'陕西西安',3,'[\"美食\", \"文化\"]','{\"day1\": \"兵马俑-华清宫\", \"day2\": \"西安城墙-钟楼\", \"day3\": \"回民街-大雁塔\"}',2,'2026-06-18 08:00:00'),(5,5,105,'广西桂林',5,'[\"自然\", \"摄影\"]','{\"day1\": \"桂林市区\", \"day2\": \"漓江漂流\", \"day3\": \"阳朔西街\", \"day4\": \"遇龙河\", \"day5\": \"桂林返回\"}',1,'2026-06-19 14:00:00'),(6,6,106,'新疆乌鲁木齐',10,'[\"自然\", \"文化\"]','{\"day1\": \"乌鲁木齐\", \"day2\": \"天山天池\", \"day3\": \"乌鲁木齐-喀纳斯\", \"day4\": \"喀纳斯景区\", \"day5\": \"禾木村\", \"day6\": \"布尔津\", \"day7\": \"可可托海\", \"day8\": \"乌鲁木齐\"}',2,'2026-06-20 07:00:00'),(7,7,107,'福建厦门',4,'[\"美食\", \"文艺\"]','{\"day1\": \"鼓浪屿\", \"day2\": \"曾厝垵\", \"day3\": \"厦门大学-南普陀\", \"day4\": \"环岛路\"}',3,'2026-06-05 16:00:00'),(8,8,108,'西藏拉萨',8,'[\"文化\", \"自然\"]','{\"day1\": \"拉萨适应\", \"day2\": \"布达拉宫\", \"day3\": \"大昭寺-八廓街\", \"day4\": \"纳木错\", \"day5\": \"羊卓雍错\", \"day6\": \"日喀则\"}',2,'2026-06-21 13:00:00'),(9,9,109,'浙江杭州',3,'[\"美食\", \"文化\", \"自然\"]','{\"day1\": \"西湖-断桥\", \"day2\": \"灵隐寺-龙井\", \"day3\": \"西溪湿地\"}',1,'2026-06-10 10:00:00'),(10,10,110,'湖南张家界',5,'[\"自然\", \"户外\"]','{\"day1\": \"张家界国家森林公园\", \"day2\": \"袁家界-天子山\", \"day3\": \"天门山\", \"day4\": \"大峡谷玻璃桥\", \"day5\": \"返回\"}',3,'2026-05-20 09:00:00');
/*!40000 ALTER TABLE `ai_trip_recommendation` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40000 ALTER TABLE `attraction` DISABLE KEYS */;
INSERT INTO `attraction` (`id`, `name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `season`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`, `create_time`, `update_time`) VALUES (4,'故宫博物院','北京','北京','北京市东城区景山前街4号','中国明清两代的皇家宫殿，世界上现存规模最大、保存最为完整的木质结构古建筑之一。','http://localhost:8080/upload/49c46ccc-c693-45cd-aea0-b51172f1e260.png','[\"https://example.com/attraction1_1.jpg\", \"https://example.com/attraction1_2.jpg\"]',60.00,'四季皆宜',5,1200,800,350,'文化遗产,历史,建筑',1,'2026-02-14 17:21:56','2026-03-09 23:18:18'),(5,'长城','北京','北京','北京市怀柔区','中国古代的军事防御工程，是世界文化遗产之一。','http://localhost:8080/upload/a65a8058-e67c-46b6-9627-9b53c8b5a6d5.png','[\"https://example.com/attraction2_1.jpg\",\"https://example.com/attraction2_2.jpg\"]',40.00,'春秋',5,1500,900,420,'文化遗产,历史,自然',1,'2026-02-14 17:21:56','2026-05-16 21:36:43'),(6,'西湖','杭州','浙江','浙江省杭州市西湖区','中国古典园林代表作，世界文化遗产。','http://localhost:8080/upload/d2678cf0-e445-4e56-83e0-6dc774605a9f.png','[\"https://example.com/attraction3_1.jpg\", \"https://example.com/attraction3_2.jpg\"]',0.00,'四季皆宜',5,1800,1200,500,'文化遗产,自然,湖泊',1,'2026-02-14 17:21:56','2026-03-29 17:41:32'),(7,'黄山','黄山','安徽','安徽省黄山市黄山区','以奇松、怪石、云海、温泉、冬雪\"五绝\"著称于世。','http://localhost:8080/upload/4f02254b-49be-44f8-aa80-13ddeff7d8da.png','[\"https://example.com/attraction4_1.jpg\",\"https://example.com/attraction4_2.jpg\"]',230.00,'春夏秋',5,1600,1000,450,'自然,山岳,云海',1,'2026-02-14 17:21:56','2026-06-21 12:20:47'),(8,'九寨沟','阿坝','四川','四川省阿坝藏族羌族自治州九寨沟县','以翠海、叠瀑、彩林、雪峰、藏情、蓝冰\"六绝\"著称。','http://localhost:8080/upload/62d144a5-c980-4225-8fe2-2b5108da519b.png','[\"https://example.com/attraction5_1.jpg\", \"https://example.com/attraction5_2.jpg\"]',220.00,'秋季',5,1400,950,400,'自然,湖泊,瀑布',1,'2026-02-14 17:21:56','2026-06-21 16:11:23'),(9,'兵马俑','西安','陕西','陕西省西安市临潼区秦始皇陵以东1.5公里处','世界文化遗产，被誉为\"世界第八大奇迹\"。','https://example.com/attraction6.jpg','[\"https://example.com/attraction6_1.jpg\", \"https://example.com/attraction6_2.jpg\"]',150.00,'四季皆宜',5,1300,850,380,'文化遗产,历史,考古',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'三亚湾','三亚','海南','海南省三亚市三亚湾路','绵长的海岸线，椰梦长廊，是著名的海滨度假区。','http://localhost:8080/upload/b3b2a9cf-3ab6-4fad-84eb-bde83bc31cb3.png','[\"https://example.com/attraction7_1.jpg\", \"https://example.com/attraction7_2.jpg\"]',0.00,'冬季',4,1100,750,320,'海滨,度假,自然',1,'2026-02-14 17:21:56','2026-03-15 15:00:52'),(11,'张家界','张家界','湖南','湖南省张家界市武陵源区','以张家界国家森林公园、天门山等著名景点著称。','https://example.com/attraction8.jpg','[\"https://example.com/attraction8_1.jpg\", \"https://example.com/attraction8_2.jpg\"]',248.00,'春夏秋',5,1550,1050,480,'自然,山岳,森林公园',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,'颐和园','北京','北京','北京市海淀区新建宫门路19号','中国现存规模最大、保存最完整的皇家园林。','https://example.com/attraction9.jpg','[\"https://example.com/attraction9_1.jpg\", \"https://example.com/attraction9_2.jpg\"]',30.00,'四季皆宜',4,900,600,280,'文化遗产,园林,历史',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(13,'鼓浪屿','厦门','福建','福建省厦门市思明区鼓浪屿','有着\"海上花园\"的美誉，是国家5A级旅游景区。','https://example.com/attraction10.jpg','[\"https://example.com/attraction10_1.jpg\", \"https://example.com/attraction10_2.jpg\"]',80.00,'四季皆宜',4,1000,700,300,'海岛,历史,建筑',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
/*!40000 ALTER TABLE `attraction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attraction_facility`
--

DROP TABLE IF EXISTS `attraction_facility`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attraction_facility` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设施ID',
  `attraction_id` bigint NOT NULL COMMENT '景点ID',
  `facility_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施名称',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标路径',
  `status` int DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_attraction_id` (`attraction_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='景点设施表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attraction_facility`
--

LOCK TABLES `attraction_facility` WRITE;
/*!40000 ALTER TABLE `attraction_facility` DISABLE KEYS */;
INSERT INTO `attraction_facility` (`id`, `attraction_id`, `facility_name`, `icon`, `status`, `create_time`, `update_time`) VALUES (1,1,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(2,1,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(3,1,'观光车','sightseeing',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(4,1,'餐饮','food',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(5,2,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(6,2,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(7,2,'导游服务','guide',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(8,3,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(9,3,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(10,3,'观景台','viewpoint',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(11,4,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(12,4,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(13,4,'游船','boat',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(17,6,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(18,6,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(19,6,'讲解服务','explanation',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(26,9,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(27,9,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(28,9,'摄影点','photo',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(29,10,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(30,10,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(31,10,'表演场地','performance',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(32,11,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(33,11,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(34,11,'温泉','hot-spring',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(35,12,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(36,12,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(37,12,'露营地','camping',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(38,13,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(39,13,'卫生间','restroom',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(40,13,'博物馆商店','shop',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(95,5,'停车场',NULL,1,'2026-05-16 21:36:43','2026-05-16 21:36:43'),(96,5,'卫生间',NULL,1,'2026-05-16 21:36:43','2026-05-16 21:36:43'),(97,5,'缆车',NULL,1,'2026-05-16 21:36:43','2026-05-16 21:36:43'),(98,7,'停车场',NULL,1,'2026-06-21 12:20:47','2026-06-21 12:20:47'),(99,7,'卫生间',NULL,1,'2026-06-21 12:20:47','2026-06-21 12:20:47'),(100,7,'亲子设施',NULL,1,'2026-06-21 12:20:47','2026-06-21 12:20:47'),(101,8,'停车场',NULL,1,'2026-06-21 16:11:23','2026-06-21 16:11:23'),(102,8,'卫生间',NULL,1,'2026-06-21 16:11:23','2026-06-21 16:11:23'),(103,8,'徒步路线',NULL,1,'2026-06-21 16:11:23','2026-06-21 16:11:23');
/*!40000 ALTER TABLE `attraction_facility` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `attraction_images` DISABLE KEYS */;
INSERT INTO `attraction_images` (`id`, `attraction_id`, `image_url`, `thumbnail_url`, `title`, `description`, `photographer`, `license`, `is_primary`, `sort_order`, `width`, `height`, `file_size`, `upload_time`) VALUES (1,1,'https://example.com/greatwall1.jpg','https://example.com/greatwall1_thumb.jpg','长城远景','中国长城的壮丽远景','Zhang Wei','CC BY-SA 4.0',1,1,1920,1080,2048,'2026-02-14 09:23:52'),(2,1,'https://example.com/greatwall2.jpg','https://example.com/greatwall2_thumb.jpg','长城近景','长城的砖石结构','Li Ming','CC BY-SA 4.0',0,2,1920,1080,1800,'2026-02-14 09:23:52'),(3,2,'https://example.com/forbidden_city1.jpg','https://example.com/forbidden_city1_thumb.jpg','故宫全景','故宫的鸟瞰图','Wang Hua','CC BY-SA 4.0',1,1,1920,1080,2200,'2026-02-14 09:23:52'),(4,2,'https://example.com/forbidden_city2.jpg','https://example.com/forbidden_city2_thumb.jpg','故宫宫殿','故宫的太和殿','Zhao Yang','CC BY-SA 4.0',0,2,1920,1080,1900,'2026-02-14 09:23:52'),(5,3,'https://example.com/eiffel_tower1.jpg','https://example.com/eiffel_tower1_thumb.jpg','埃菲尔铁塔','埃菲尔铁塔的正面照','Jean Dupont','CC BY-SA 4.0',1,1,1920,1080,2100,'2026-02-14 09:23:52'),(6,3,'https://example.com/eiffel_tower2.jpg','https://example.com/eiffel_tower2_thumb.jpg','埃菲尔铁塔夜景','夜晚的埃菲尔铁塔','Pierre Martin','CC BY-SA 4.0',0,2,1920,1080,2300,'2026-02-14 09:23:52'),(7,4,'https://example.com/statue_of_liberty1.jpg','https://example.com/statue_of_liberty1_thumb.jpg','自由女神像','自由女神像的正面照','John Smith','CC BY-SA 4.0',1,1,1920,1080,2000,'2026-02-14 09:23:52'),(8,4,'https://example.com/statue_of_liberty2.jpg','https://example.com/statue_of_liberty2_thumb.jpg','自由女神像远景','自由女神像的远景','Jane Doe','CC BY-SA 4.0',0,2,1920,1080,1850,'2026-02-14 09:23:52'),(9,5,'https://example.com/tokyo_tower1.jpg','https://example.com/tokyo_tower1_thumb.jpg','东京塔','东京塔的正面照','Tanaka Yuki','CC BY-SA 4.0',1,1,1920,1080,1950,'2026-02-14 09:23:52'),(10,5,'https://example.com/tokyo_tower2.jpg','https://example.com/tokyo_tower2_thumb.jpg','东京塔夜景','夜晚的东京塔','Sato Hiroshi','CC BY-SA 4.0',0,2,1920,1080,2150,'2026-02-14 09:23:52');
/*!40000 ALTER TABLE `attraction_images` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `attraction_tags` DISABLE KEYS */;
INSERT INTO `attraction_tags` (`id`, `attraction_id`, `tag_id`, `created_at`) VALUES (1,1,1,'2026-02-14 09:23:52'),(2,1,5,'2026-02-14 09:23:52'),(3,1,8,'2026-02-14 09:23:52'),(4,2,1,'2026-02-14 09:23:52'),(5,2,4,'2026-02-14 09:23:52'),(6,2,6,'2026-02-14 09:23:52'),(7,3,3,'2026-02-14 09:23:52'),(8,3,6,'2026-02-14 09:23:52'),(9,3,8,'2026-02-14 09:23:52'),(10,4,3,'2026-02-14 09:23:52'),(11,4,4,'2026-02-14 09:23:52'),(12,4,6,'2026-02-14 09:23:52'),(13,5,3,'2026-02-14 09:23:52'),(14,5,4,'2026-02-14 09:23:52'),(15,5,9,'2026-02-14 09:23:52'),(16,6,3,'2026-02-14 09:23:52'),(17,6,4,'2026-02-14 09:23:52'),(18,6,8,'2026-02-14 09:23:52'),(19,7,2,'2026-02-14 09:23:52'),(20,7,5,'2026-02-14 09:23:52'),(21,7,7,'2026-02-14 09:23:52'),(22,8,3,'2026-02-14 09:23:52'),(23,8,6,'2026-02-14 09:23:52'),(24,8,8,'2026-02-14 09:23:52'),(25,9,1,'2026-02-14 09:23:52'),(26,9,4,'2026-02-14 09:23:52'),(27,9,9,'2026-02-14 09:23:52'),(28,10,1,'2026-02-14 09:23:52'),(29,10,4,'2026-02-14 09:23:52'),(30,10,6,'2026-02-14 09:23:52');
/*!40000 ALTER TABLE `attraction_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attraction_ticket`
--

DROP TABLE IF EXISTS `attraction_ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attraction_ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `attraction_id` bigint NOT NULL COMMENT '景点ID',
  `name` varchar(100) NOT NULL COMMENT '门票名称',
  `description` text COMMENT '门票描述',
  `rules` varchar(500) DEFAULT NULL COMMENT '使用规则，JSON格式',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_attraction_id` (`attraction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点门票表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attraction_ticket`
--

LOCK TABLES `attraction_ticket` WRITE;
/*!40000 ALTER TABLE `attraction_ticket` DISABLE KEYS */;
INSERT INTO `attraction_ticket` (`id`, `attraction_id`, `name`, `description`, `rules`, `price`, `status`, `create_time`, `update_time`) VALUES (1,4,'成人票','适用于18-60周岁成人','[\"有效期当天一次入园\", \"需携带身份证\"]',120.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(2,4,'儿童票','适用于1.2-1.5米儿童','[\"有效期当天一次入园\", \"需携带身份证或户口本\"]',60.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(3,4,'老人票','适用于60周岁以上老人','[\"有效期当天一次入园\", \"需携带身份证\"]',60.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(4,4,'学生票','适用于全日制学生','[\"有效期当天一次入园\", \"需携带学生证\"]',80.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(5,4,'成人票','适用于18-60周岁成人','[\"有效期当天一次入园\", \"需携带身份证\"]',150.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(6,4,'儿童票','适用于1.2-1.5米儿童','[\"有效期当天一次入园\", \"需携带身份证或户口本\"]',75.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(7,4,'亲子套票','1大1小','[\"有效期当天一次入园\", \"需携带身份证\"]',198.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(8,4,'成人票','适用于18-60周岁成人','[\"有效期两天内有效\", \"需携带身份证\"]',200.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(9,4,'团体票','适用于10人以上团体','[\"有效期当天一次入园\", \"需提前预约\"]',160.00,1,'2026-05-15 21:11:34','2026-05-15 23:17:33'),(16,5,'儿童票','身高1.2M','[]',123.00,1,'2026-05-16 21:36:43','2026-05-16 21:36:43');
/*!40000 ALTER TABLE `attraction_ticket` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `attractions` DISABLE KEYS */;
INSERT INTO `attractions` (`id`, `name`, `chinese_name`, `city_id`, `type`, `rating`, `price_level`, `visiting_hours`, `best_season`, `visit_duration`, `description`, `history`, `tips`, `address`, `latitude`, `longitude`, `image_url`, `official_website`, `year_built`, `architect`, `created_at`, `updated_at`) VALUES (1,'Great Wall of China','中国长城',1,'historical',4.8,'中等','08:00-17:00','春秋','3-4小时','中国长城是世界上最伟大的建筑之一，也是世界文化遗产。','长城始建于春秋战国时期，是中国古代为了防御北方游牧民族入侵而修建的。','建议早上早点去，避开人流高峰；穿舒适的鞋子，因为需要走很多台阶。','北京市怀柔区',40.43190000,116.57040000,'https://example.com/greatwall.jpg','https://www.badaling.gov.cn/','公元前7世纪','秦始皇','2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,'Forbidden City','故宫',1,'historical',4.9,'中等','08:30-17:00','四季皆宜','4-5小时','故宫是中国明清两代的皇家宫殿，是世界上现存规模最大、保存最为完整的木质结构古建筑之一。','故宫始建于明永乐四年（1406年），是中国古代宫廷建筑的精华。','建议预留充足的时间游览；可以租讲解器了解更多历史；注意保护文物，不要触摸展品。','北京市东城区景山前街4号',39.91630000,116.39720000,'https://example.com/forbidden_city.jpg','https://www.dpm.org.cn/','1420年','蒯祥','2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,'Eiffel Tower','埃菲尔铁塔',5,'modern',4.7,'中等','09:00-23:45','春秋','2-3小时','埃菲尔铁塔是法国巴黎的标志性建筑，也是世界著名的建筑之一。','埃菲尔铁塔建于1889年，是为了纪念法国大革命100周年而建造的。','建议提前网上购票，避免排队；黄昏时分登塔可以看到美丽的日落；塔顶风大，注意保暖。','法国巴黎战神广场',48.85840000,2.29450000,'https://example.com/eiffel_tower.jpg','https://www.toureiffel.paris/','1889年','古斯塔夫·埃菲尔','2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,'Statue of Liberty','自由女神像',4,'monument',4.6,'中等','09:00-17:00','春秋','2-3小时','自由女神像是美国的标志性建筑，象征着自由和民主。','自由女神像是法国赠送给美国的礼物，于1886年10月28日落成。','建议提前网上购票，尤其是 crown access；需要乘船前往，注意天气情况；可以参观女神像内部和观景台。','美国纽约自由岛',40.68920000,-74.04450000,'https://example.com/statue_of_liberty.jpg','https://www.nps.gov/stli/index.htm','1886年','弗雷德里克·奥古斯特·巴托尔迪','2026-02-14 09:23:52','2026-02-14 09:23:52'),(5,'Tokyo Tower','东京塔',3,'modern',4.5,'便宜','09:00-22:00','四季皆宜','1-2小时','东京塔是日本东京的标志性建筑，高333米。','东京塔建于1958年，是为了纪念日本战后复兴而建造的。','建议黄昏时分登塔，可以看到东京的日景和夜景；塔顶有咖啡厅，可以休息和观景；可以购买纪念品。','日本东京都港区芝公园',35.65860000,139.74540000,'https://example.com/tokyo_tower.jpg','https://www.tokyotower.co.jp/','1958年','内藤多仲','2026-02-14 09:23:52','2026-02-14 09:23:52'),(6,'Sydney Opera House','悉尼歌剧院',6,'modern',4.8,'中等','09:00-17:00','春秋','1-2小时','悉尼歌剧院是澳大利亚悉尼的标志性建筑，也是世界著名的建筑之一。','悉尼歌剧院建于1973年，是20世纪最具特色的建筑之一。','建议参加导览团，了解建筑历史和内部结构；可以观看演出；周边有很多餐厅和咖啡馆。','澳大利亚悉尼本尼朗角',-33.85680000,151.21530000,'https://example.com/sydney_opera_house.jpg','https://www.sydneyoperahouse.com/','1973年','约恩·乌松','2026-02-14 09:23:52','2026-02-14 09:23:52'),(7,'Table Mountain','桌山',7,'natural',4.9,'中等','08:00-20:00','春秋','3-4小时','桌山是南非开普敦的标志性自然景观，山顶平坦如桌。','桌山是开普敦的象征，也是世界自然遗产。','建议乘坐缆车上下山，节省体力；山顶风大，注意保暖；可以徒步登山，但需要一定的体力和时间。','南非开普敦',-33.96280000,18.40360000,'https://example.com/table_mountain.jpg','https://www.tablemountain.net/',NULL,NULL,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(8,'Christ the Redeemer','基督像',8,'monument',4.7,'便宜','08:00-19:00','冬春','1-2小时','基督像是巴西里约热内卢的标志性建筑，高30米。','基督像建于1931年，是为了纪念巴西独立100周年而建造的。','建议早上或下午前往，避开中午的高温；可以乘坐小火车或徒步登山；山顶风大，注意安全。','巴西里约热内卢科尔科瓦多山',-22.95190000,-43.21050000,'https://example.com/christ_the_redeemer.jpg','https://www.corcovado.com.br/','1931年','保罗·兰多斯基','2026-02-14 09:23:52','2026-02-14 09:23:52'),(9,'Brandenburg Gate','勃兰登堡门',9,'historical',4.6,'免费','全天开放','春秋','0.5-1小时','勃兰登堡门是德国柏林的标志性建筑，也是德国统一的象征。','勃兰登堡门建于1791年，是柏林的象征。','建议黄昏时分前往，灯光效果很美；周边有很多历史景点，可以一并游览；注意个人财物安全。','德国柏林市中心',52.51630000,13.37770000,'https://example.com/brandenburg_gate.jpg','https://www.visitberlin.de/en/brandenburg-gate','1791年','卡尔·戈特哈德·朗汉斯','2026-02-14 09:23:52','2026-02-14 09:23:52'),(10,'Taj Mahal','泰姬陵',10,'historical',4.9,'中等','06:00-19:00','冬','2-3小时','泰姬陵是印度的标志性建筑，也是世界文化遗产。','泰姬陵建于1653年，是莫卧儿皇帝沙贾汗为纪念他的妻子穆姆塔兹·玛哈尔而建造的。','建议早上日出时分前往，可以看到泰姬陵的美丽光影；注意着装得体，尊重当地文化；可以聘请导游了解更多历史。','印度阿格拉',27.17510000,78.04210000,'https://example.com/taj_mahal.jpg','https://www.tajmahal.gov.in/','1653年','乌斯塔德·艾哈迈德·拉合里','2026-02-14 09:23:52','2026-02-14 09:23:52');
/*!40000 ALTER TABLE `attractions` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `call_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `call_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `captcha_image`
--

DROP TABLE IF EXISTS `captcha_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `captcha_image` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片唯一ID',
  `image_key` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片唯一标识符',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片存储路径',
  `file_size` int NOT NULL COMMENT '文件大小(字节)',
  `width` smallint NOT NULL COMMENT '图片宽度(像素)',
  `height` smallint NOT NULL COMMENT '图片高度(像素)',
  `mime_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片MIME类型',
  `md5_hash` char(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件MD5值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `file_content` text COLLATE utf8mb4_unicode_ci COMMENT 'SVG文件内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `image_key` (`image_key`),
  KEY `idx_md5_hash` (`md5_hash`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码图片主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `captcha_image`
--

LOCK TABLES `captcha_image` WRITE;
/*!40000 ALTER TABLE `captcha_image` DISABLE KEYS */;
INSERT INTO `captcha_image` (`id`, `image_key`, `file_name`, `file_path`, `file_size`, `width`, `height`, `mime_type`, `md5_hash`, `status`, `file_content`, `create_time`, `update_time`) VALUES ('0117062ba91341b6a5c0a4660e2bd796','0117062ba91341b6a5c0a4660e2bd796','0117062ba91341b6a5c0a4660e2bd796.svg',NULL,371,320,180,'image/svg+xml','7a91b6b0acc5c6ae5eea6ca5c4e4e8b6',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"105\" cy=\"93\" r=\"49\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"188\" cy=\"89\" r=\"28\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:15:07','2026-07-08 22:15:07'),('02c45f07dfd84de8b34b1b4d61acbbd5','02c45f07dfd84de8b34b1b4d61acbbd5','02c45f07dfd84de8b34b1b4d61acbbd5.svg',NULL,372,320,180,'image/svg+xml','6824facce6c2bcea62db665cb3ec4c8d',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"103\" cy=\"108\" r=\"61\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"179\" cy=\"88\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('06a22311a5534645a52022a27ef424c3','06a22311a5534645a52022a27ef424c3','06a22311a5534645a52022a27ef424c3.svg',NULL,371,320,180,'image/svg+xml','d66f155c91b28d16557b92d655940ce0',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"100\" cy=\"58\" r=\"37\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"182\" cy=\"83\" r=\"19\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('08b66172d1bb4b54a126beb135f4db44','08b66172d1bb4b54a126beb135f4db44','08b66172d1bb4b54a126beb135f4db44.svg',NULL,423,320,180,'image/svg+xml','aa1aec5d8bd7ccf3c4340848bd44bb39',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"132\" cy=\"93\" r=\"24\" fill=\"#27ae60\" opacity=\"0.6\"/><rect x=\"188\" y=\"54\" width=\"62\" height=\"63\" fill=\"#27ae60\" opacity=\"0.5\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"131\" cy=\"56\" r=\"13\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 23:04:00','2026-07-08 23:04:00'),('0b05bd8e1d8e4728bcc363adec9a9a15','0b05bd8e1d8e4728bcc363adec9a9a15','0b05bd8e1d8e4728bcc363adec9a9a15.svg',NULL,372,320,180,'image/svg+xml','db959dc3ca24e3b8a5fb087f78f7d4c7',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"142\" cy=\"79\" r=\"49\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"181\" cy=\"109\" r=\"14\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('125b643237984f04b21a5de76e87cb5e','125b643237984f04b21a5de76e87cb5e','125b643237984f04b21a5de76e87cb5e.svg',NULL,371,320,180,'image/svg+xml','610e1ccabc0bde4c1862593cf8569799',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"80\" cy=\"125\" r=\"78\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"174\" cy=\"98\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:48:22','2026-07-08 21:48:22'),('12f04caf00454cc288783395c6b68068','12f04caf00454cc288783395c6b68068','12f04caf00454cc288783395c6b68068.svg',NULL,0,320,180,'image/svg+xml','f980d79f8a6c052e79475d616141a789',1,NULL,'2026-07-08 23:11:09','2026-07-08 23:11:09'),('13e4d100b1654d6cbbd5b6b12f5347c8','13e4d100b1654d6cbbd5b6b12f5347c8','13e4d100b1654d6cbbd5b6b12f5347c8.svg',NULL,0,320,180,'image/svg+xml','8ea192d7e0bea84f839225b86cb03332',1,NULL,'2026-07-08 23:11:46','2026-07-08 23:11:46'),('15188e1ade7e488d8767d4631d17061e','15188e1ade7e488d8767d4631d17061e','15188e1ade7e488d8767d4631d17061e.svg',NULL,373,320,180,'image/svg+xml','7d9b8892235ca1419db89c77e9d83350',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"106\" cy=\"132\" r=\"32\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"182\" cy=\"103\" r=\"22\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:38','2026-07-08 22:14:38'),('15937353a9ef490da365057bffa2e0df','15937353a9ef490da365057bffa2e0df','15937353a9ef490da365057bffa2e0df.svg',NULL,373,320,180,'image/svg+xml','ed097e74d33fd12fd565bd31beb88d02',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"134\" cy=\"101\" r=\"66\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"183\" cy=\"104\" r=\"19\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:40:43','2026-07-08 22:40:43'),('182227cfc1c14c47b3811e10ff46fb64','182227cfc1c14c47b3811e10ff46fb64','182227cfc1c14c47b3811e10ff46fb64.svg',NULL,371,320,180,'image/svg+xml','da1de37f4b1ccba9b82364e345a8c330',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"92\" cy=\"139\" r=\"41\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"176\" cy=\"87\" r=\"24\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:32:17','2026-07-08 22:32:17'),('1a8a78bc6d3d4ee3ba7ead512f5ef71c','1a8a78bc6d3d4ee3ba7ead512f5ef71c','1a8a78bc6d3d4ee3ba7ead512f5ef71c.svg',NULL,372,320,180,'image/svg+xml','8cd839faaff156db2bcfd06828961d45',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"173\" cy=\"82\" r=\"71\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"165\" cy=\"110\" r=\"18\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:02:47','2026-07-08 22:02:47'),('1bd292381feb4038903afa2ae18b9ba0','1bd292381feb4038903afa2ae18b9ba0','1bd292381feb4038903afa2ae18b9ba0.svg',NULL,371,320,180,'image/svg+xml','d1e6b1c7afdc9078941488e395c2c04c',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"142\" cy=\"48\" r=\"73\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"174\" cy=\"99\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:11:45','2026-07-08 22:11:45'),('1d08ef6a586b4189b7662fde4dc78c1f','1d08ef6a586b4189b7662fde4dc78c1f','1d08ef6a586b4189b7662fde4dc78c1f.svg',NULL,371,320,180,'image/svg+xml','e8146ab1706fd8dd4ea1e6b4c4cd8895',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"197\" cy=\"76\" r=\"71\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"163\" cy=\"98\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:13:43','2026-07-08 22:13:43'),('1f0a75341a2c4efd8671dc9317edfed6','1f0a75341a2c4efd8671dc9317edfed6','1f0a75341a2c4efd8671dc9317edfed6.svg',NULL,371,320,180,'image/svg+xml','bd1cf331adee8e7cd30c3c16c72ecee6',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"164\" cy=\"59\" r=\"55\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"162\" cy=\"95\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:16:31','2026-07-08 22:16:31'),('21f521a832a148b9baba77651941571c','21f521a832a148b9baba77651941571c','21f521a832a148b9baba77651941571c.svg',NULL,372,320,180,'image/svg+xml','c66d773952e75297f700e55127fcfc68',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"194\" cy=\"131\" r=\"51\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"187\" cy=\"88\" r=\"22\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('22ea41cd2fcc4927b479fab5ae16b237','22ea41cd2fcc4927b479fab5ae16b237','22ea41cd2fcc4927b479fab5ae16b237.svg',NULL,0,320,180,'image/svg+xml','2665d7e08c1cef8b4842ae6cedc13f8f',1,NULL,'2026-07-08 23:11:23','2026-07-08 23:11:23'),('243d2698b96e423e901a47764acc48e4','243d2698b96e423e901a47764acc48e4','243d2698b96e423e901a47764acc48e4.svg',NULL,371,320,180,'image/svg+xml','cb832e4226d72dd9ac2a5da19e51854b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"195\" cy=\"88\" r=\"61\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"164\" cy=\"86\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('255b67ec4b6b499691aa689a6dff58ae','255b67ec4b6b499691aa689a6dff58ae','255b67ec4b6b499691aa689a6dff58ae.svg',NULL,373,320,180,'image/svg+xml','b0ebd5b4efa8a8aea40b9fe0630d7b9c',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"163\" cy=\"101\" r=\"73\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"180\" cy=\"102\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:53:57','2026-07-08 21:53:57'),('25ffeaba94dc459eba53e83e7bfd8abb','25ffeaba94dc459eba53e83e7bfd8abb','25ffeaba94dc459eba53e83e7bfd8abb.svg',NULL,425,320,180,'image/svg+xml','72ca9fb0fbd803a6b8752d841ef0101b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\"><rect width=\"320\" height=\"180\" fill=\"#98d8c8\"/><circle cx=\"148\" cy=\"135\" r=\"34\" fill=\"#e74c3c\" opacity=\"0.6\"/><rect x=\"200\" y=\"118\" width=\"89\" height=\"79\" fill=\"#e74c3c\" opacity=\"0.5\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"172\" cy=\"82\" r=\"14\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 23:04:25','2026-07-08 23:04:25'),('2650a1470a7e4cf6b64ba43d935c89b5','2650a1470a7e4cf6b64ba43d935c89b5','2650a1470a7e4cf6b64ba43d935c89b5.svg',NULL,372,320,180,'image/svg+xml','e5c7af2285196d46af64035e4cdfd92d',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"123\" cy=\"114\" r=\"51\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"196\" cy=\"81\" r=\"23\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('29c77c73e4764ff9880a55b5e3b43ad7','29c77c73e4764ff9880a55b5e3b43ad7','29c77c73e4764ff9880a55b5e3b43ad7.svg',NULL,371,320,180,'image/svg+xml','31ec5600dfa738e47c1f48fd0330eb17',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"118\" cy=\"53\" r=\"69\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"197\" cy=\"89\" r=\"18\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:48:20','2026-07-08 21:48:20'),('2ca73e21335a4b50bf597edc9534a943','2ca73e21335a4b50bf597edc9534a943','2ca73e21335a4b50bf597edc9534a943.svg',NULL,371,320,180,'image/svg+xml','e924182a32dc9cd2e6a688150fd9b74f',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"189\" cy=\"60\" r=\"73\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"199\" cy=\"92\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('2e0cf39d1d064dcd8842e455630d529f','2e0cf39d1d064dcd8842e455630d529f','2e0cf39d1d064dcd8842e455630d529f.svg',NULL,372,320,180,'image/svg+xml','9b898ad559b8b26fa086362b7d21a7c2',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"84\" cy=\"117\" r=\"76\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"193\" cy=\"117\" r=\"28\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:12:49','2026-07-08 22:12:49'),('35a13742df814c60ab96c06a64591e78','35a13742df814c60ab96c06a64591e78','35a13742df814c60ab96c06a64591e78.svg',NULL,0,320,180,'image/svg+xml','7c60a8882d5359535e81127d6e2024ec',1,NULL,'2026-07-08 23:14:39','2026-07-08 23:14:39'),('36bbeca39b5741b69b5d7cbb66b9f746','36bbeca39b5741b69b5d7cbb66b9f746','36bbeca39b5741b69b5d7cbb66b9f746.svg',NULL,373,320,180,'image/svg+xml','f1b5bf0960014fbb0d6c6daa1d41cb89',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"234\" cy=\"139\" r=\"46\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"170\" cy=\"102\" r=\"18\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('381584438bb9421a8fdf0c3ca8220eca','381584438bb9421a8fdf0c3ca8220eca','381584438bb9421a8fdf0c3ca8220eca.svg',NULL,371,320,180,'image/svg+xml','e7dfed63d9ba387b7cdaba07d24639f2',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"189\" cy=\"64\" r=\"57\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"168\" cy=\"92\" r=\"25\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:48:21','2026-07-08 21:48:21'),('3bc79f956b2f4b8e9fd598a65351fccd','3bc79f956b2f4b8e9fd598a65351fccd','3bc79f956b2f4b8e9fd598a65351fccd.svg',NULL,373,320,180,'image/svg+xml','8c78fbeae09ad37108f890981d6a6995',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"223\" cy=\"114\" r=\"34\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"180\" cy=\"109\" r=\"20\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:06:30','2026-07-08 22:06:30'),('44ceac7484ea4354b40689193a4bf4d7','44ceac7484ea4354b40689193a4bf4d7','44ceac7484ea4354b40689193a4bf4d7.svg',NULL,422,320,180,'image/svg+xml','d55dbbdd7711716dc28e29635ba521b6',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\"><rect width=\"320\" height=\"180\" fill=\"#9370db\"/><circle cx=\"92\" cy=\"84\" r=\"26\" fill=\"#9b59b6\" opacity=\"0.6\"/><rect x=\"53\" y=\"30\" width=\"138\" height=\"41\" fill=\"#9b59b6\" opacity=\"0.5\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"130\" cy=\"78\" r=\"19\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 23:04:45','2026-07-08 23:04:45'),('4546e51448954ca49c9036e65ef1f5e4','4546e51448954ca49c9036e65ef1f5e4','4546e51448954ca49c9036e65ef1f5e4.svg',NULL,371,320,180,'image/svg+xml','73f8cc7737837a12273e1a6e03fc5766',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"84\" cy=\"65\" r=\"56\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"172\" cy=\"111\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('49b47467cce041e3837dde50576f316b','49b47467cce041e3837dde50576f316b','49b47467cce041e3837dde50576f316b.svg',NULL,371,320,180,'image/svg+xml','c65a6e101a93a175778f3b05a0d29dea',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"122\" cy=\"85\" r=\"46\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"166\" cy=\"89\" r=\"26\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:47:20','2026-07-08 22:47:20'),('51cca80e26514564a112fc270e377d45','51cca80e26514564a112fc270e377d45','51cca80e26514564a112fc270e377d45.svg',NULL,372,320,180,'image/svg+xml','d9c69dc85d27954a3fb9b86847b0aa2f',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"163\" cy=\"122\" r=\"49\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"186\" cy=\"96\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:32:38','2026-07-08 22:32:38'),('51d83f196d804e6595dae9a8f5bf4cd2','51d83f196d804e6595dae9a8f5bf4cd2','51d83f196d804e6595dae9a8f5bf4cd2.svg',NULL,372,320,180,'image/svg+xml','cf1fef77126e721d859dfb927ee9720d',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"170\" cy=\"42\" r=\"74\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"198\" cy=\"103\" r=\"26\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:09','2026-07-08 22:14:09'),('5226fc39a244483a983ef287141d6a8d','5226fc39a244483a983ef287141d6a8d','5226fc39a244483a983ef287141d6a8d.svg',NULL,425,320,180,'image/svg+xml','ea5ae50ede5016061e45055d3817ab3b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"233\" cy=\"134\" r=\"50\" fill=\"#3498db\" opacity=\"0.6\"/><rect x=\"205\" y=\"50\" width=\"128\" height=\"45\" fill=\"#3498db\" opacity=\"0.5\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"111\" cy=\"69\" r=\"18\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 23:04:41','2026-07-08 23:04:41'),('55ecc68d6b6d44aebb3476a02d145e7a','55ecc68d6b6d44aebb3476a02d145e7a','55ecc68d6b6d44aebb3476a02d145e7a.svg',NULL,372,320,180,'image/svg+xml','91e652c7f63bda7f8248e40c333d91a5',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"161\" cy=\"102\" r=\"65\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"176\" cy=\"89\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:15:58','2026-07-08 22:15:58'),('59ac9ce15d5848838f44c6a8419b7867','59ac9ce15d5848838f44c6a8419b7867','59ac9ce15d5848838f44c6a8419b7867.svg',NULL,371,320,180,'image/svg+xml','392bd10f137aadf857778bd5becf5516',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"132\" cy=\"44\" r=\"44\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"163\" cy=\"81\" r=\"26\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('5a16e8bda2114dbfbf0698d3c9bee5e4','5a16e8bda2114dbfbf0698d3c9bee5e4','5a16e8bda2114dbfbf0698d3c9bee5e4.svg',NULL,372,320,180,'image/svg+xml','5eec2a514739d4657999509896dab0dc',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"183\" cy=\"137\" r=\"75\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"193\" cy=\"96\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:13:34','2026-07-08 22:13:34'),('5a796735bf4e4d0994bbd8b46855ca2c','5a796735bf4e4d0994bbd8b46855ca2c','5a796735bf4e4d0994bbd8b46855ca2c.svg',NULL,372,320,180,'image/svg+xml','7130fff908b8f020390ec9cb6b8c2299',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"155\" cy=\"83\" r=\"41\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"160\" cy=\"113\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('5ed324977efa47139118e102333ecdf4','5ed324977efa47139118e102333ecdf4','5ed324977efa47139118e102333ecdf4.svg',NULL,372,320,180,'image/svg+xml','b37cc10cac4db437911899c95a2da7e0',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"198\" cy=\"129\" r=\"49\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"187\" cy=\"90\" r=\"11\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:16:59','2026-07-08 22:16:59'),('5ee06a8142064bb18978b46b8f0756dd','5ee06a8142064bb18978b46b8f0756dd','5ee06a8142064bb18978b46b8f0756dd.svg',NULL,425,320,180,'image/svg+xml','09dd22016dd6275a1a55624f45994eea',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\"><rect width=\"320\" height=\"180\" fill=\"#87cefa\"/><circle cx=\"87\" cy=\"108\" r=\"47\" fill=\"#3498db\" opacity=\"0.6\"/><rect x=\"169\" y=\"105\" width=\"138\" height=\"77\" fill=\"#3498db\" opacity=\"0.5\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"157\" cy=\"41\" r=\"29\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 23:04:46','2026-07-08 23:04:46'),('5fc6c05b4f164f0d98b1f8b2e6c954ea','5fc6c05b4f164f0d98b1f8b2e6c954ea','5fc6c05b4f164f0d98b1f8b2e6c954ea.svg',NULL,371,320,180,'image/svg+xml','85d4462d07a405f7bb3c8d4bd68b0e56',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"237\" cy=\"68\" r=\"79\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"199\" cy=\"99\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('6018447323ce46bf9aa62d99235c8f2a','6018447323ce46bf9aa62d99235c8f2a','6018447323ce46bf9aa62d99235c8f2a.svg',NULL,372,320,180,'image/svg+xml','12dd21cdcf53be40f4e1e9c277cf760d',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"153\" cy=\"95\" r=\"77\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"173\" cy=\"111\" r=\"20\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:56','2026-07-08 22:14:56'),('633e8036eab3471a8680825c5c338996','633e8036eab3471a8680825c5c338996','633e8036eab3471a8680825c5c338996.svg',NULL,373,320,180,'image/svg+xml','62a2cc6aaaac61d50e7d4fb997b6febc',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"185\" cy=\"115\" r=\"60\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"172\" cy=\"112\" r=\"20\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('639105ea7106436a984ba32d7e524de3','639105ea7106436a984ba32d7e524de3','639105ea7106436a984ba32d7e524de3.svg',NULL,372,320,180,'image/svg+xml','66fa2bec6fc0def57245f466b818bb36',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"232\" cy=\"96\" r=\"73\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"170\" cy=\"101\" r=\"19\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:07:15','2026-07-08 22:07:15'),('69ad1bf6137a48e284ba5be7c073e784','69ad1bf6137a48e284ba5be7c073e784','69ad1bf6137a48e284ba5be7c073e784.svg',NULL,372,320,180,'image/svg+xml','462d9695880b5155c83e439816faa4c8',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"157\" cy=\"103\" r=\"53\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"187\" cy=\"91\" r=\"13\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:22:44','2026-07-08 22:22:44'),('6db73f199b5e43f2a737f939c757c94e','6db73f199b5e43f2a737f939c757c94e','6db73f199b5e43f2a737f939c757c94e.svg',NULL,372,320,180,'image/svg+xml','6214e22773a33398a973ecee87c2bd6b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"97\" cy=\"122\" r=\"31\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"177\" cy=\"100\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:53:40','2026-07-08 21:53:40'),('70f338535b5a4ae18e636be6797080eb','70f338535b5a4ae18e636be6797080eb','70f338535b5a4ae18e636be6797080eb.svg',NULL,372,320,180,'image/svg+xml','4bb344fcd78de2d02428339cf5f9d1ce',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"230\" cy=\"136\" r=\"54\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"177\" cy=\"83\" r=\"18\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:13:49','2026-07-08 22:13:49'),('749c2b7a3d314c99ba07154c6352b049','749c2b7a3d314c99ba07154c6352b049','749c2b7a3d314c99ba07154c6352b049.svg',NULL,371,320,180,'image/svg+xml','26c368e4a4e6290e882305c19ed82942',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"148\" cy=\"63\" r=\"42\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"184\" cy=\"95\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:16:29','2026-07-08 22:16:29'),('75481382b9784f84b0358f3d3977a59f','75481382b9784f84b0358f3d3977a59f','75481382b9784f84b0358f3d3977a59f.svg',NULL,370,320,180,'image/svg+xml','77af31c75b75a947182ec2891c6a812d',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"97\" cy=\"42\" r=\"62\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"160\" cy=\"90\" r=\"20\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:11:25','2026-07-08 22:11:25'),('780f6cb70d8640398377f3b6eb8402b8','780f6cb70d8640398377f3b6eb8402b8','780f6cb70d8640398377f3b6eb8402b8.svg',NULL,373,320,180,'image/svg+xml','3ec16bfa8de6a55754bacbfc996c1c91',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"136\" cy=\"136\" r=\"75\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"160\" cy=\"113\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:54:54','2026-07-08 21:54:54'),('7890abcd-ef01-2345-6789-abcdef012345','captcha_007','captcha_007.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c598',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"140\" cy=\"100\" r=\"36\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"175\" cy=\"90\" r=\"13\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('7a8cd14d66d94d18ad844a5d78f11872','7a8cd14d66d94d18ad844a5d78f11872','7a8cd14d66d94d18ad844a5d78f11872.svg',NULL,372,320,180,'image/svg+xml','58a7a374963c6645b3482a2abe31936c',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"206\" cy=\"125\" r=\"61\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"176\" cy=\"88\" r=\"28\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('7c485f2e920543cc8af96d8b8b0200bb','7c485f2e920543cc8af96d8b8b0200bb','7c485f2e920543cc8af96d8b8b0200bb.svg',NULL,372,320,180,'image/svg+xml','4a6bb91944d93664d01b1dbf1a2ad24c',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"205\" cy=\"105\" r=\"42\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"161\" cy=\"94\" r=\"19\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:11','2026-07-08 22:14:11'),('7c4be181b82544d8a89e18a2f23bc02d','7c4be181b82544d8a89e18a2f23bc02d','7c4be181b82544d8a89e18a2f23bc02d.svg',NULL,373,320,180,'image/svg+xml','6d5a1a16dc842d077739e0abc060002f',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"172\" cy=\"110\" r=\"66\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"172\" cy=\"108\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:32:00','2026-07-08 22:32:00'),('7e49bd490bed4e5bad5505daad5a0ce4','7e49bd490bed4e5bad5505daad5a0ce4','7e49bd490bed4e5bad5505daad5a0ce4.svg',NULL,0,320,180,'image/svg+xml','a441fdddee2289e5a1812fcdc9ce0ec8',1,NULL,'2026-07-08 23:14:48','2026-07-08 23:14:48'),('7ff4c5af46824568bd903fcff21285c6','7ff4c5af46824568bd903fcff21285c6','7ff4c5af46824568bd903fcff21285c6.svg',NULL,372,320,180,'image/svg+xml','a41e081edb1201775f14a1e88684b2ef',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"177\" cy=\"115\" r=\"43\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"176\" cy=\"96\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:16:00','2026-07-08 22:16:00'),('871338942cc244bab8b210bcda1c5a26','871338942cc244bab8b210bcda1c5a26','871338942cc244bab8b210bcda1c5a26.svg',NULL,371,320,180,'image/svg+xml','c19b41bed4623706eecde0e9a18a69de',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"80\" cy=\"129\" r=\"62\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"171\" cy=\"89\" r=\"10\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:47:26','2026-07-08 22:47:26'),('88e2d7009a234a28a87df47d6cf297ed','88e2d7009a234a28a87df47d6cf297ed','88e2d7009a234a28a87df47d6cf297ed.svg',NULL,371,320,180,'image/svg+xml','6f63a5ed9e3f153a70459d07e711aaeb',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"99\" cy=\"108\" r=\"31\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"168\" cy=\"92\" r=\"27\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:40:41','2026-07-08 22:40:41'),('89abcdef-0123-4567-89ab-cdef01234567','captcha_008','captcha_008.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c599',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"220\" cy=\"80\" r=\"44\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"190\" cy=\"115\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('8a02ee6b668b4d9d92aefc4f002c901e','8a02ee6b668b4d9d92aefc4f002c901e','8a02ee6b668b4d9d92aefc4f002c901e.svg',NULL,371,320,180,'image/svg+xml','97e7511e501d334e377efb1e1c7bac7c',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"237\" cy=\"92\" r=\"40\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"181\" cy=\"89\" r=\"20\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('912ffcf7229a49b59233de2d5fa0250e','912ffcf7229a49b59233de2d5fa0250e','912ffcf7229a49b59233de2d5fa0250e.svg',NULL,371,320,180,'image/svg+xml','777a6588d2fbf17c8af2e915fcbed20e',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"96\" cy=\"139\" r=\"58\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"189\" cy=\"81\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:13:17','2026-07-08 22:13:17'),('93373889dcde4fa8965d692d2aba700a','93373889dcde4fa8965d692d2aba700a','93373889dcde4fa8965d692d2aba700a.svg',NULL,372,320,180,'image/svg+xml','be2c90ecfe17db97e0baa87b3765ce50',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"128\" cy=\"72\" r=\"59\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"171\" cy=\"105\" r=\"26\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:32:27','2026-07-08 22:32:27'),('977cdf4ad37543808239e87f7db2d439','977cdf4ad37543808239e87f7db2d439','977cdf4ad37543808239e87f7db2d439.svg',NULL,424,320,180,'image/svg+xml','c1cdcaa579309436fb66db3ba6f41b39',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\"><rect width=\"320\" height=\"180\" fill=\"#d4ac6e\"/><circle cx=\"191\" cy=\"76\" r=\"53\" fill=\"#9b59b6\" opacity=\"0.6\"/><rect x=\"172\" y=\"92\" width=\"106\" height=\"43\" fill=\"#9b59b6\" opacity=\"0.5\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"134\" cy=\"79\" r=\"25\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 23:04:23','2026-07-08 23:04:23'),('99e890c678b540189cd1ce4bdefdd4cb','99e890c678b540189cd1ce4bdefdd4cb','99e890c678b540189cd1ce4bdefdd4cb.svg',NULL,370,320,180,'image/svg+xml','cd9495dd4d77f95dc27d0a1d5c0110d7',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"85\" cy=\"54\" r=\"62\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"174\" cy=\"82\" r=\"29\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('9abcdef0-1234-5678-9abc-def012345678','captcha_009','captcha_009.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c59a',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"110\" cy=\"95\" r=\"48\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"185\" cy=\"80\" r=\"19\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('9ce0a4eafce04da2a10f35d8b16082b8','9ce0a4eafce04da2a10f35d8b16082b8','9ce0a4eafce04da2a10f35d8b16082b8.svg',NULL,371,320,180,'image/svg+xml','fe38e491b34c745b80bb954d94660433',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"110\" cy=\"66\" r=\"50\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"172\" cy=\"93\" r=\"12\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:16:02','2026-07-08 22:16:02'),('9d4bce23e9024edb9c3a3883650b3fdf','9d4bce23e9024edb9c3a3883650b3fdf','9d4bce23e9024edb9c3a3883650b3fdf.svg',NULL,373,320,180,'image/svg+xml','15830f691c8be411f21d58c9e86f8e70',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"202\" cy=\"138\" r=\"78\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"187\" cy=\"112\" r=\"28\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:02:40','2026-07-08 22:02:40'),('9e75f3a3463d4846ac20b1b6366d364a','9e75f3a3463d4846ac20b1b6366d364a','9e75f3a3463d4846ac20b1b6366d364a.svg',NULL,372,320,180,'image/svg+xml','6e2c3ff65d8667bae5511481d4d29d66',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"152\" cy=\"114\" r=\"47\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"190\" cy=\"80\" r=\"19\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:22','2026-07-08 22:14:22'),('9ea1417f74d842df98b252a45af0152c','9ea1417f74d842df98b252a45af0152c','9ea1417f74d842df98b252a45af0152c.svg',NULL,371,320,180,'image/svg+xml','df2e0f938b1550a4c867b1212694b140',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"102\" cy=\"70\" r=\"78\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"189\" cy=\"83\" r=\"14\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:16:34','2026-07-08 22:16:34'),('a06e9adcbf354a6c83434d6c6de7b968','a06e9adcbf354a6c83434d6c6de7b968','a06e9adcbf354a6c83434d6c6de7b968.svg',NULL,373,320,180,'image/svg+xml','a0d686ea21a5de74a7ddbc93e6b21697',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"125\" cy=\"111\" r=\"30\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"192\" cy=\"113\" r=\"10\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:07:07','2026-07-08 22:07:07'),('a15d6109aa9d403896947a798c3714c9','a15d6109aa9d403896947a798c3714c9','a15d6109aa9d403896947a798c3714c9.svg',NULL,0,320,180,'image/svg+xml','ce012dcb59fb128b92ea6bdaaf885091',1,NULL,'2026-07-08 23:14:47','2026-07-08 23:14:47'),('a1b2c3d4-e5f6-7890-abcd-ef1234567890','captcha_001','captcha_001.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c592',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"120\" cy=\"80\" r=\"40\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"180\" cy=\"100\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('a1cd34c07f084791ab4522d6fc23543b','a1cd34c07f084791ab4522d6fc23543b','a1cd34c07f084791ab4522d6fc23543b.svg',NULL,372,320,180,'image/svg+xml','8644d0d23aeab14cc57d7f673121a59b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"163\" cy=\"138\" r=\"31\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"183\" cy=\"82\" r=\"23\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:40:35','2026-07-08 22:40:35'),('abcdef01-2345-6789-abcd-ef0123456789','captcha_010','captcha_010.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c59b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"170\" cy=\"75\" r=\"34\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"205\" cy=\"95\" r=\"11\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('ac22fffba66d49449e47604af19b40cd','ac22fffba66d49449e47604af19b40cd','ac22fffba66d49449e47604af19b40cd.svg',NULL,372,320,180,'image/svg+xml','b5b3ea6182bef5766511888df6703ace',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"181\" cy=\"116\" r=\"59\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"160\" cy=\"81\" r=\"25\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:52:28','2026-07-08 21:52:28'),('ae5b6e3a6a0b4cfea9c656aee663c457','ae5b6e3a6a0b4cfea9c656aee663c457','ae5b6e3a6a0b4cfea9c656aee663c457.svg',NULL,371,320,180,'image/svg+xml','fb4c811e90672241d80e550f7b16113b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"229\" cy=\"62\" r=\"40\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"195\" cy=\"88\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:16:24','2026-07-08 22:16:24'),('b1effac792eb43c4ae90e0b82cc294e2','b1effac792eb43c4ae90e0b82cc294e2','b1effac792eb43c4ae90e0b82cc294e2.svg',NULL,425,320,180,'image/svg+xml','65c2fcc676d609051660bd4753ddd50e',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"203\" cy=\"133\" r=\"40\" fill=\"#3498db\" opacity=\"0.6\"/><rect x=\"49\" y=\"105\" width=\"128\" height=\"78\" fill=\"#3498db\" opacity=\"0.5\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"170\" cy=\"71\" r=\"22\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 23:04:43','2026-07-08 23:04:43'),('b255ccf70b2142e2a26d8adde83d6d23','b255ccf70b2142e2a26d8adde83d6d23','b255ccf70b2142e2a26d8adde83d6d23.svg',NULL,373,320,180,'image/svg+xml','31658a0e925d79cd150bd36850c24082',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"141\" cy=\"104\" r=\"49\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"193\" cy=\"115\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('b2c3d4e5-f678-90ab-cdef-0123456789ab','captcha_002','captcha_002.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c593',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"180\" cy=\"100\" r=\"50\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"200\" cy=\"90\" r=\"12\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('b4a63ac9bcef48c985e9fae4c3d6ee90','b4a63ac9bcef48c985e9fae4c3d6ee90','b4a63ac9bcef48c985e9fae4c3d6ee90.svg',NULL,371,320,180,'image/svg+xml','81b23619b26c0f6439356967ffda9f9f',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"105\" cy=\"70\" r=\"38\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"161\" cy=\"91\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:32:16','2026-07-08 22:32:16'),('b53aa66174bc47e2b5ae7bdd400bac1b','b53aa66174bc47e2b5ae7bdd400bac1b','b53aa66174bc47e2b5ae7bdd400bac1b.svg',NULL,372,320,180,'image/svg+xml','e215c625ec252576f24aaa83039524f7',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"236\" cy=\"132\" r=\"62\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"164\" cy=\"95\" r=\"22\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:22:58','2026-07-08 22:22:58'),('b7473c5dac564dd69b1339ee97e6ff95','b7473c5dac564dd69b1339ee97e6ff95','b7473c5dac564dd69b1339ee97e6ff95.svg',NULL,371,320,180,'image/svg+xml','dede186e06028156caeafcddcb1042b8',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"177\" cy=\"81\" r=\"39\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"186\" cy=\"88\" r=\"11\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:54:33','2026-07-08 21:54:33'),('b7c6374674344f28b702687a6a827143','b7c6374674344f28b702687a6a827143','b7c6374674344f28b702687a6a827143.svg',NULL,372,320,180,'image/svg+xml','4f3f3e093a1c5d2c8e7091b1a2f57e7e',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"88\" cy=\"100\" r=\"58\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"163\" cy=\"112\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:55:36','2026-07-08 21:55:36'),('b829295129ca415d8f056ede1d05dc27','b829295129ca415d8f056ede1d05dc27','b829295129ca415d8f056ede1d05dc27.svg',NULL,373,320,180,'image/svg+xml','f7265dfd539770b3479f80c1c423583b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"229\" cy=\"136\" r=\"50\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"170\" cy=\"101\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('bc9966347cfe4e6fafa0be0c56b1c112','bc9966347cfe4e6fafa0be0c56b1c112','bc9966347cfe4e6fafa0be0c56b1c112.svg',NULL,371,320,180,'image/svg+xml','074f3e1d1ce9cbc777dd9a9d8d9a5b69',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"172\" cy=\"80\" r=\"46\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"166\" cy=\"84\" r=\"12\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:23:02','2026-07-08 22:23:02'),('c3d4e5f6-7890-abcd-ef01-23456789abcd','captcha_003','captcha_003.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c594',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"150\" cy=\"90\" r=\"35\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"190\" cy=\"85\" r=\"18\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('c8f52729355947908d2e2eb5b2805de9','c8f52729355947908d2e2eb5b2805de9','c8f52729355947908d2e2eb5b2805de9.svg',NULL,372,320,180,'image/svg+xml','a03e8de6aed7b1c79671175d410f5d62',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"124\" cy=\"79\" r=\"77\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"198\" cy=\"105\" r=\"27\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:08','2026-07-08 22:14:08'),('c997300e09904c069963f987acd3376c','c997300e09904c069963f987acd3376c','c997300e09904c069963f987acd3376c.svg',NULL,372,320,180,'image/svg+xml','84ab824c90b885b041a1730f18b0f4e6',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"85\" cy=\"110\" r=\"75\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"192\" cy=\"103\" r=\"28\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:45','2026-07-08 22:14:45'),('ca4c1457e40849ea8d63ebd46cd11601','ca4c1457e40849ea8d63ebd46cd11601','ca4c1457e40849ea8d63ebd46cd11601.svg',NULL,372,320,180,'image/svg+xml','685055d49037d2c1027b35f5303a6d24',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"110\" cy=\"60\" r=\"77\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"171\" cy=\"107\" r=\"27\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:22:57','2026-07-08 22:22:57'),('cc2d026fc69941e697a840050a4c0beb','cc2d026fc69941e697a840050a4c0beb','cc2d026fc69941e697a840050a4c0beb.svg',NULL,372,320,180,'image/svg+xml','f362947252442ea56acc98929875299b',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"212\" cy=\"88\" r=\"55\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"160\" cy=\"101\" r=\"29\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:13:37','2026-07-08 22:13:37'),('ce67c1db85994f6db1697441776ab2af','ce67c1db85994f6db1697441776ab2af','ce67c1db85994f6db1697441776ab2af.svg',NULL,372,320,180,'image/svg+xml','e2115ee753c2f51f2d64a54042bc046a',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"132\" cy=\"69\" r=\"66\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"165\" cy=\"115\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:47:22','2026-07-08 22:47:22'),('cf198c6144bb4c3f858494a0ac30a50b','cf198c6144bb4c3f858494a0ac30a50b','cf198c6144bb4c3f858494a0ac30a50b.svg',NULL,372,320,180,'image/svg+xml','b7738ce9c40348e32718e4cc14261a40',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"124\" cy=\"92\" r=\"66\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"190\" cy=\"107\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:32:02','2026-07-08 22:32:02'),('d0da8a38c9b4470bafc2707a8ce008f7','d0da8a38c9b4470bafc2707a8ce008f7','d0da8a38c9b4470bafc2707a8ce008f7.svg',NULL,371,320,180,'image/svg+xml','7ec2a008b61bafe2cb0a441c4da03ef7',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f7dc6f\"/><circle cx=\"151\" cy=\"87\" r=\"43\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"183\" cy=\"97\" r=\"21\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:52:20','2026-07-08 21:52:20'),('d4e5f678-90ab-cdef-0123-456789abcdef','captcha_004','captcha_004.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c595',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"100\" cy=\"70\" r=\"45\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"170\" cy=\"110\" r=\"20\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('d5e0f2b5f6c04c0cac6e6abc01e28b08','d5e0f2b5f6c04c0cac6e6abc01e28b08','d5e0f2b5f6c04c0cac6e6abc01e28b08.svg',NULL,371,320,180,'image/svg+xml','19b0bf962ab521f3ee6d3486ee67a18a',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"92\" cy=\"97\" r=\"55\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"176\" cy=\"107\" r=\"23\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:47:29','2026-07-08 22:47:29'),('d625529568ab435a97c1f338584a5dcd','d625529568ab435a97c1f338584a5dcd','d625529568ab435a97c1f338584a5dcd.svg',NULL,371,320,180,'image/svg+xml','d22809347f1c8a585065c9bae2a83c45',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"207\" cy=\"71\" r=\"52\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"184\" cy=\"96\" r=\"10\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:54:09','2026-07-08 21:54:09'),('d8eb417d0b524bf6b1a7100e3ebd5366','d8eb417d0b524bf6b1a7100e3ebd5366','d8eb417d0b524bf6b1a7100e3ebd5366.svg',NULL,371,320,180,'image/svg+xml','53ce61c58d2bf8dfd024c9f1572a2ecd',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"101\" cy=\"43\" r=\"55\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"172\" cy=\"84\" r=\"29\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:46:13','2026-07-08 22:46:13'),('de519b40b7f44b6dab4e17b790371e1a','de519b40b7f44b6dab4e17b790371e1a','de519b40b7f44b6dab4e17b790371e1a.svg',NULL,372,320,180,'image/svg+xml','f3691c92b85b5102255c5fc45e95cb62',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"100\" cy=\"83\" r=\"36\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"186\" cy=\"114\" r=\"26\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:55:26','2026-07-08 21:55:26'),('dff53392e1e648f788cba119afabbf09','dff53392e1e648f788cba119afabbf09','dff53392e1e648f788cba119afabbf09.svg',NULL,372,320,180,'image/svg+xml','aab077ef315de997c8a06f72d4646f1d',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"142\" cy=\"51\" r=\"44\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"185\" cy=\"105\" r=\"12\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:23:09','2026-07-08 22:23:09'),('e04a621b37d64c5fa9c6f02d6176a802','e04a621b37d64c5fa9c6f02d6176a802','e04a621b37d64c5fa9c6f02d6176a802.svg',NULL,372,320,180,'image/svg+xml','4479675d080b5a878f138a530f6bfe17',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"183\" cy=\"114\" r=\"38\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"182\" cy=\"90\" r=\"29\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:07:11','2026-07-08 22:07:11'),('e3b178fe7d964a26841382eefe256638','e3b178fe7d964a26841382eefe256638','e3b178fe7d964a26841382eefe256638.svg',NULL,0,320,180,'image/svg+xml','997af1659738054c7c31f77faa334111',1,NULL,'2026-07-08 23:10:58','2026-07-08 23:10:58'),('e47f555d4af248adbc62d9d9b7d876db','e47f555d4af248adbc62d9d9b7d876db','e47f555d4af248adbc62d9d9b7d876db.svg',NULL,373,320,180,'image/svg+xml','91e9bdb1b9650b2373fec484b7a061df',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"114\" cy=\"126\" r=\"52\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"181\" cy=\"110\" r=\"26\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:56:37','2026-07-08 21:56:37'),('e5f67890-abcd-ef01-2345-6789abcdef01','captcha_005','captcha_005.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c596',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"200\" cy=\"85\" r=\"38\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"160\" cy=\"75\" r=\"14\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('e5fbc3adab04463e9d64629dca2770e0','e5fbc3adab04463e9d64629dca2770e0','e5fbc3adab04463e9d64629dca2770e0.svg',NULL,371,320,180,'image/svg+xml','58a75bb41c4785aed95b2ea83c18b4c4',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"205\" cy=\"83\" r=\"72\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"189\" cy=\"99\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:52:39','2026-07-08 21:52:39'),('e697d56a753c49eb88b47428e8e99e6f','e697d56a753c49eb88b47428e8e99e6f','e697d56a753c49eb88b47428e8e99e6f.svg',NULL,371,320,180,'image/svg+xml','228ed820e0b639c6eb1aa33c8521866f',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"195\" cy=\"94\" r=\"69\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"181\" cy=\"87\" r=\"23\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:15:20','2026-07-08 22:15:20'),('e7544fbd00cf4c4d8fe572b0e4226033','e7544fbd00cf4c4d8fe572b0e4226033','e7544fbd00cf4c4d8fe572b0e4226033.svg',NULL,372,320,180,'image/svg+xml','60600243e9f1fbbf8bb7566631d87171',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"97\" cy=\"127\" r=\"67\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"181\" cy=\"102\" r=\"15\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:43:25','2026-07-08 21:43:25'),('e9f18962d5e94b4fbcca5486bc300540','e9f18962d5e94b4fbcca5486bc300540','e9f18962d5e94b4fbcca5486bc300540.svg',NULL,373,320,180,'image/svg+xml','f6b66df8736ae868f8f5f40b6f02ff30',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#d7bde2\"/><circle cx=\"198\" cy=\"139\" r=\"75\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"193\" cy=\"108\" r=\"12\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:52:34','2026-07-08 21:52:34'),('eb17334b83fb44cda1b9b10e27970fb8','eb17334b83fb44cda1b9b10e27970fb8','eb17334b83fb44cda1b9b10e27970fb8.svg',NULL,371,320,180,'image/svg+xml','3108deeeb9988c9662f27f2a2e8592b9',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"113\" cy=\"62\" r=\"40\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"161\" cy=\"80\" r=\"23\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:02:25','2026-07-08 22:02:25'),('ecb9fbe8e5444dcb9d95efaa99575f17','ecb9fbe8e5444dcb9d95efaa99575f17','ecb9fbe8e5444dcb9d95efaa99575f17.svg',NULL,372,320,180,'image/svg+xml','81ac3eca99608810cceb754c719ee94e',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"80\" cy=\"137\" r=\"55\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"193\" cy=\"113\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:15:19','2026-07-08 22:15:19'),('ed5ea8f358eb422fae01dc3cb173f7b7','ed5ea8f358eb422fae01dc3cb173f7b7','ed5ea8f358eb422fae01dc3cb173f7b7.svg',NULL,372,320,180,'image/svg+xml','b394aae8cf77db0b30f1a9b9c4924ce7',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"214\" cy=\"78\" r=\"72\" fill=\"#a3d8a3\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"191\" cy=\"101\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:02:34','2026-07-08 22:02:34'),('edce1b0fe6414ffd922de4634a22c98f','edce1b0fe6414ffd922de4634a22c98f','edce1b0fe6414ffd922de4634a22c98f.svg',NULL,372,320,180,'image/svg+xml','ebc1247ad1e18d45ca139647a42ca04d',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"121\" cy=\"70\" r=\"44\" fill=\"#f7dc6f\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"163\" cy=\"113\" r=\"26\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:32:40','2026-07-08 22:32:40'),('ef6d2ed0e2ee454e8a45b2ff2ec8dcbc','ef6d2ed0e2ee454e8a45b2ff2ec8dcbc','ef6d2ed0e2ee454e8a45b2ff2ec8dcbc.svg',NULL,371,320,180,'image/svg+xml','1ed2d2fb5f00b063c407b8e31386d291',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"237\" cy=\"63\" r=\"55\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"168\" cy=\"93\" r=\"25\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:07:00','2026-07-08 22:07:00'),('f49c646cd4bb4e108cafac4d4ff6bcf2','f49c646cd4bb4e108cafac4d4ff6bcf2','f49c646cd4bb4e108cafac4d4ff6bcf2.svg',NULL,372,320,180,'image/svg+xml','55630c9238b405e82a55ce20c2ef7c22',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#b0c4de\"/><circle cx=\"148\" cy=\"121\" r=\"59\" fill=\"#aed6f1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"166\" cy=\"86\" r=\"22\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:17:12','2026-07-08 22:17:12'),('f67890ab-cdef-0123-4567-89abcdef0123','captcha_006','captcha_006.svg',NULL,512,320,180,'image/svg+xml','5d41402abc4b2a76b9719d911017c597',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#aed6f1\"/><circle cx=\"160\" cy=\"95\" r=\"42\" fill=\"#b0c4de\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"210\" cy=\"100\" r=\"16\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-07 23:26:31','2026-07-07 23:26:31'),('fc694c923cef4bafa9477b6b003de8c0','fc694c923cef4bafa9477b6b003de8c0','fc694c923cef4bafa9477b6b003de8c0.svg',NULL,372,320,180,'image/svg+xml','fe0a69b8d4465868b6d40ab7fd2ebf31',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"185\" cy=\"66\" r=\"59\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"192\" cy=\"114\" r=\"29\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:07:45','2026-07-08 22:07:45'),('fd6d7a51d1b54313be8086da47129b64','fd6d7a51d1b54313be8086da47129b64','fd6d7a51d1b54313be8086da47129b64.svg',NULL,0,320,180,'image/svg+xml','8d84ea85994ffbb90426c45012d275dc',1,NULL,'2026-07-08 23:14:41','2026-07-08 23:14:41'),('fe0afc8ef0f84fb1812a43ce4ac03e23','fe0afc8ef0f84fb1812a43ce4ac03e23','fe0afc8ef0f84fb1812a43ce4ac03e23.svg',NULL,372,320,180,'image/svg+xml','94e19cfb74b6034e6b877fbb72ee062c',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"116\" cy=\"129\" r=\"62\" fill=\"#f5b7b1\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"163\" cy=\"87\" r=\"11\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:52:58','2026-07-08 21:52:58'),('ffc084cd49a04e499a7574a3ddd307b9','ffc084cd49a04e499a7574a3ddd307b9','ffc084cd49a04e499a7574a3ddd307b9.svg',NULL,372,320,180,'image/svg+xml','e18698a347d8968be335422bb4c540db',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#a3d8a3\"/><circle cx=\"194\" cy=\"108\" r=\"58\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"164\" cy=\"96\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 21:51:57','2026-07-08 21:51:57'),('fff39e749bb24661a7ada5ae4976face','fff39e749bb24661a7ada5ae4976face','fff39e749bb24661a7ada5ae4976face.svg',NULL,372,320,180,'image/svg+xml','e4513f27059797edec0d24a8de3d9b68',1,'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"180\" viewBox=\"0 0 320 180\"><rect width=\"320\" height=\"180\" fill=\"#f5b7b1\"/><circle cx=\"199\" cy=\"109\" r=\"66\" fill=\"#d7bde2\" opacity=\"0.6\"/><rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/><circle cx=\"180\" cy=\"94\" r=\"17\" fill=\"#4a6cf7\" opacity=\"0.15\"/></svg>','2026-07-08 22:14:49','2026-07-08 22:14:49');
/*!40000 ALTER TABLE `captcha_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `captcha_puzzle_config`
--

DROP TABLE IF EXISTS `captcha_puzzle_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `captcha_puzzle_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置唯一ID',
  `image_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联的图片ID',
  `piece_x` smallint NOT NULL COMMENT '拼图块X坐标',
  `piece_y` smallint NOT NULL COMMENT '拼图块Y坐标',
  `piece_width` smallint NOT NULL COMMENT '拼图块宽度',
  `piece_height` smallint NOT NULL COMMENT '拼图块高度',
  `target_x` smallint NOT NULL COMMENT '目标位置X坐标',
  `target_y` smallint NOT NULL COMMENT '目标位置Y坐标',
  `slider_percent` decimal(5,4) NOT NULL COMMENT '滑块百分比位置(0-1)',
  `version` int NOT NULL DEFAULT '1' COMMENT '配置版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_image_id` (`image_id`),
  CONSTRAINT `fk_puzzle_image` FOREIGN KEY (`image_id`) REFERENCES `captcha_image` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拼图配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `captcha_puzzle_config`
--

LOCK TABLES `captcha_puzzle_config` WRITE;
/*!40000 ALTER TABLE `captcha_puzzle_config` DISABLE KEYS */;
INSERT INTO `captcha_puzzle_config` (`id`, `image_id`, `piece_x`, `piece_y`, `piece_width`, `piece_height`, `target_x`, `target_y`, `slider_percent`, `version`, `create_time`) VALUES (1,'a1b2c3d4-e5f6-7890-abcd-ef1234567890',80,65,44,44,232,65,0.7250,1,'2026-07-07 23:26:31'),(2,'b2c3d4e5-f678-90ab-cdef-0123456789ab',90,80,44,44,224,80,0.7000,1,'2026-07-07 23:26:31'),(3,'c3d4e5f6-7890-abcd-ef01-23456789abcd',75,55,44,44,236,55,0.7375,1,'2026-07-07 23:26:31'),(4,'d4e5f678-90ab-cdef-0123-456789abcdef',85,90,44,44,228,90,0.7125,1,'2026-07-07 23:26:31'),(5,'e5f67890-abcd-ef01-2345-6789abcdef01',95,70,44,44,220,70,0.6875,1,'2026-07-07 23:26:31'),(6,'f67890ab-cdef-0123-4567-89abcdef0123',70,60,44,44,240,60,0.7500,1,'2026-07-07 23:26:31'),(7,'7890abcd-ef01-2345-6789-abcdef012345',100,85,44,44,216,85,0.6750,1,'2026-07-07 23:26:31'),(8,'89abcdef-0123-4567-89ab-cdef01234567',65,75,44,44,244,75,0.7625,1,'2026-07-07 23:26:31'),(9,'9abcdef0-1234-5678-9abc-def012345678',105,50,44,44,212,50,0.6625,1,'2026-07-07 23:26:31'),(10,'abcdef01-2345-6789-abcd-ef0123456789',60,95,44,44,248,95,0.7750,1,'2026-07-07 23:26:31'),(11,'21f521a832a148b9baba77651941571c',25,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(12,'5a796735bf4e4d0994bbd8b46855ca2c',19,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(13,'2ca73e21335a4b50bf597edc9534a943',9,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(14,'2650a1470a7e4cf6b64ba43d935c89b5',24,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(15,'7a8cd14d66d94d18ad844a5d78f11872',15,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(16,'36bbeca39b5741b69b5d7cbb66b9f746',9,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(17,'4546e51448954ca49c9036e65ef1f5e4',10,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(18,'e7544fbd00cf4c4d8fe572b0e4226033',10,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(19,'02c45f07dfd84de8b34b1b4d61acbbd5',24,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(20,'b255ccf70b2142e2a26d8adde83d6d23',10,6,102,168,212,6,0.9000,1,'2026-07-08 21:43:25'),(21,'29c77c73e4764ff9880a55b5e3b43ad7',8,6,102,168,212,6,0.9000,1,'2026-07-08 21:48:20'),(22,'381584438bb9421a8fdf0c3ca8220eca',8,6,102,168,212,6,0.9000,1,'2026-07-08 21:48:21'),(23,'125b643237984f04b21a5de76e87cb5e',6,6,102,168,212,6,0.9000,1,'2026-07-08 21:48:22'),(24,'ffc084cd49a04e499a7574a3ddd307b9',20,6,102,168,212,6,0.9000,1,'2026-07-08 21:51:57'),(25,'d0da8a38c9b4470bafc2707a8ce008f7',25,6,102,168,212,6,0.9000,1,'2026-07-08 21:52:20'),(26,'ac22fffba66d49449e47604af19b40cd',9,6,102,168,212,6,0.9000,1,'2026-07-08 21:52:28'),(27,'e9f18962d5e94b4fbcca5486bc300540',13,6,102,168,212,6,0.9000,1,'2026-07-08 21:52:34'),(28,'e5fbc3adab04463e9d64629dca2770e0',20,6,102,168,212,6,0.9000,1,'2026-07-08 21:52:39'),(29,'fe0afc8ef0f84fb1812a43ce4ac03e23',17,6,102,168,212,6,0.9000,1,'2026-07-08 21:52:58'),(30,'6db73f199b5e43f2a737f939c757c94e',18,6,102,168,212,6,0.9000,1,'2026-07-08 21:53:40'),(31,'255b67ec4b6b499691aa689a6dff58ae',15,6,102,168,212,6,0.9000,1,'2026-07-08 21:53:57'),(32,'d625529568ab435a97c1f338584a5dcd',21,6,102,168,212,6,0.9000,1,'2026-07-08 21:54:09'),(33,'b7473c5dac564dd69b1339ee97e6ff95',20,6,102,168,212,6,0.9000,1,'2026-07-08 21:54:33'),(34,'780f6cb70d8640398377f3b6eb8402b8',24,6,102,168,212,6,0.9000,1,'2026-07-08 21:54:54'),(35,'de519b40b7f44b6dab4e17b790371e1a',24,6,102,168,212,6,0.9000,1,'2026-07-08 21:55:26'),(36,'b7c6374674344f28b702687a6a827143',25,6,102,168,212,6,0.9000,1,'2026-07-08 21:55:36'),(37,'59ac9ce15d5848838f44c6a8419b7867',82,89,44,44,256,89,0.8969,1,'2026-07-08 21:56:37'),(38,'243d2698b96e423e901a47764acc48e4',63,85,44,44,256,85,0.9000,1,'2026-07-08 21:56:37'),(39,'06a22311a5534645a52022a27ef424c3',69,79,44,44,256,79,0.9000,1,'2026-07-08 21:56:37'),(40,'8a02ee6b668b4d9d92aefc4f002c901e',90,81,44,44,256,81,0.8925,1,'2026-07-08 21:56:37'),(41,'5fc6c05b4f164f0d98b1f8b2e6c954ea',61,71,44,44,256,71,0.9000,1,'2026-07-08 21:56:37'),(42,'0b05bd8e1d8e4728bcc363adec9a9a15',74,74,44,44,256,74,0.9000,1,'2026-07-08 21:56:37'),(43,'b829295129ca415d8f056ede1d05dc27',86,70,44,44,256,70,0.8947,1,'2026-07-08 21:56:37'),(44,'633e8036eab3471a8680825c5c338996',80,64,44,44,256,64,0.8980,1,'2026-07-08 21:56:37'),(45,'e47f555d4af248adbc62d9d9b7d876db',99,85,44,44,256,85,0.8870,1,'2026-07-08 21:56:37'),(46,'99e890c678b540189cd1ce4bdefdd4cb',67,80,44,44,256,80,0.9000,1,'2026-07-08 21:56:37'),(47,'eb17334b83fb44cda1b9b10e27970fb8',74,76,44,44,256,76,0.9000,1,'2026-07-08 22:02:25'),(48,'ed5ea8f358eb422fae01dc3cb173f7b7',74,61,44,44,256,61,0.9000,1,'2026-07-08 22:02:34'),(49,'9d4bce23e9024edb9c3a3883650b3fdf',102,67,44,44,256,67,0.8851,1,'2026-07-08 22:02:40'),(50,'1a8a78bc6d3d4ee3ba7ead512f5ef71c',63,72,44,44,256,72,0.9000,1,'2026-07-08 22:02:47'),(51,'3bc79f956b2f4b8e9fd598a65351fccd',88,83,44,44,256,83,0.8936,1,'2026-07-08 22:06:30'),(52,'ef6d2ed0e2ee454e8a45b2ff2ec8dcbc',84,89,44,44,256,89,0.8958,1,'2026-07-08 22:07:00'),(53,'a06e9adcbf354a6c83434d6c6de7b968',102,63,44,44,256,63,0.8851,1,'2026-07-08 22:07:07'),(54,'e04a621b37d64c5fa9c6f02d6176a802',102,79,44,44,256,79,0.8851,1,'2026-07-08 22:07:11'),(55,'639105ea7106436a984ba32d7e524de3',77,79,44,44,256,79,0.8995,1,'2026-07-08 22:07:15'),(56,'fc694c923cef4bafa9477b6b003de8c0',84,65,44,44,256,65,0.8958,1,'2026-07-08 22:07:45'),(57,'75481382b9784f84b0358f3d3977a59f',91,95,44,44,256,95,0.8919,1,'2026-07-08 22:11:25'),(58,'1bd292381feb4038903afa2ae18b9ba0',86,95,44,44,256,95,0.8947,1,'2026-07-08 22:11:45'),(59,'2e0cf39d1d064dcd8842e455630d529f',93,58,44,44,256,58,0.8907,1,'2026-07-08 22:12:49'),(60,'912ffcf7229a49b59233de2d5fa0250e',66,92,44,44,256,92,0.9000,1,'2026-07-08 22:13:17'),(61,'5a16e8bda2114dbfbf0698d3c9bee5e4',96,69,44,44,256,69,0.8889,1,'2026-07-08 22:13:34'),(62,'cc2d026fc69941e697a840050a4c0beb',69,66,44,44,256,66,0.9000,1,'2026-07-08 22:13:37'),(63,'1d08ef6a586b4189b7662fde4dc78c1f',66,55,44,44,256,55,0.9000,1,'2026-07-08 22:13:43'),(64,'70f338535b5a4ae18e636be6797080eb',76,86,44,44,256,86,0.9000,1,'2026-07-08 22:13:49'),(65,'c8f52729355947908d2e2eb5b2805de9',70,59,44,44,256,59,0.9000,1,'2026-07-08 22:14:08'),(66,'51d83f196d804e6595dae9a8f5bf4cd2',85,75,44,44,256,75,0.8953,1,'2026-07-08 22:14:09'),(67,'7c485f2e920543cc8af96d8b8b0200bb',65,52,44,44,256,52,0.9000,1,'2026-07-08 22:14:11'),(68,'9e75f3a3463d4846ac20b1b6366d364a',79,64,44,44,256,64,0.8985,1,'2026-07-08 22:14:22'),(69,'15188e1ade7e488d8767d4631d17061e',70,93,44,44,256,93,0.9000,1,'2026-07-08 22:14:38'),(70,'c997300e09904c069963f987acd3376c',75,90,44,44,256,90,0.9000,1,'2026-07-08 22:14:45'),(71,'fff39e749bb24661a7ada5ae4976face',72,92,44,44,256,92,0.9000,1,'2026-07-08 22:14:49'),(72,'6018447323ce46bf9aa62d99235c8f2a',83,87,44,44,256,87,0.8964,1,'2026-07-08 22:14:56'),(73,'0117062ba91341b6a5c0a4660e2bd796',70,85,44,44,256,85,0.9000,1,'2026-07-08 22:15:07'),(74,'ecb9fbe8e5444dcb9d95efaa99575f17',89,64,44,44,256,64,0.8930,1,'2026-07-08 22:15:19'),(75,'e697d56a753c49eb88b47428e8e99e6f',98,58,44,44,256,58,0.8876,1,'2026-07-08 22:15:20'),(76,'55ecc68d6b6d44aebb3476a02d145e7a',65,85,44,44,256,85,0.9000,1,'2026-07-08 22:15:58'),(77,'7ff4c5af46824568bd903fcff21285c6',73,60,44,44,256,60,0.9000,1,'2026-07-08 22:16:00'),(78,'9ce0a4eafce04da2a10f35d8b16082b8',92,56,44,44,256,56,0.8913,1,'2026-07-08 22:16:02'),(79,'ae5b6e3a6a0b4cfea9c656aee663c457',88,55,44,44,256,55,0.8936,1,'2026-07-08 22:16:24'),(80,'749c2b7a3d314c99ba07154c6352b049',72,74,44,44,256,74,0.9000,1,'2026-07-08 22:16:29'),(81,'1f0a75341a2c4efd8671dc9317edfed6',96,55,44,44,256,55,0.8889,1,'2026-07-08 22:16:31'),(82,'9ea1417f74d842df98b252a45af0152c',94,56,44,44,256,56,0.8901,1,'2026-07-08 22:16:34'),(83,'5ed324977efa47139118e102333ecdf4',61,54,44,44,256,54,0.9000,1,'2026-07-08 22:16:59'),(84,'f49c646cd4bb4e108cafac4d4ff6bcf2',80,88,44,44,256,88,0.8980,1,'2026-07-08 22:17:12'),(85,'69ad1bf6137a48e284ba5be7c073e784',89,59,44,44,256,59,0.8930,1,'2026-07-08 22:22:44'),(86,'ca4c1457e40849ea8d63ebd46cd11601',68,61,44,44,256,61,0.9000,1,'2026-07-08 22:22:57'),(87,'b53aa66174bc47e2b5ae7bdd400bac1b',75,56,44,44,256,56,0.9000,1,'2026-07-08 22:22:58'),(88,'bc9966347cfe4e6fafa0be0c56b1c112',76,53,44,44,256,53,0.9000,1,'2026-07-08 22:23:02'),(89,'dff53392e1e648f788cba119afabbf09',60,80,44,44,256,80,0.9000,1,'2026-07-08 22:23:09'),(90,'7c4be181b82544d8a89e18a2f23bc02d',63,59,44,44,256,59,0.9000,1,'2026-07-08 22:32:00'),(91,'cf198c6144bb4c3f858494a0ac30a50b',64,95,44,44,256,95,0.9000,1,'2026-07-08 22:32:02'),(92,'b4a63ac9bcef48c985e9fae4c3d6ee90',70,71,44,44,256,71,0.9000,1,'2026-07-08 22:32:16'),(93,'182227cfc1c14c47b3811e10ff46fb64',77,58,44,44,256,58,0.8995,1,'2026-07-08 22:32:17'),(94,'93373889dcde4fa8965d692d2aba700a',61,53,44,44,256,53,0.9000,1,'2026-07-08 22:32:27'),(95,'51cca80e26514564a112fc270e377d45',73,64,44,44,256,64,0.9000,1,'2026-07-08 22:32:38'),(96,'edce1b0fe6414ffd922de4634a22c98f',67,78,44,44,256,78,0.9000,1,'2026-07-08 22:32:40'),(97,'a1cd34c07f084791ab4522d6fc23543b',79,68,44,44,256,68,0.8985,1,'2026-07-08 22:40:35'),(98,'88e2d7009a234a28a87df47d6cf297ed',80,60,44,44,256,60,0.8980,1,'2026-07-08 22:40:41'),(99,'15937353a9ef490da365057bffa2e0df',94,72,44,44,256,72,0.8901,1,'2026-07-08 22:40:43'),(100,'d8eb417d0b524bf6b1a7100e3ebd5366',76,75,44,44,256,75,0.9000,1,'2026-07-08 22:46:13'),(101,'49b47467cce041e3837dde50576f316b',87,70,44,44,256,70,0.8942,1,'2026-07-08 22:47:20'),(102,'ce67c1db85994f6db1697441776ab2af',79,70,44,44,256,70,0.8985,1,'2026-07-08 22:47:22'),(103,'871338942cc244bab8b210bcda1c5a26',94,62,44,44,256,62,0.8901,1,'2026-07-08 22:47:26'),(104,'d5e0f2b5f6c04c0cac6e6abc01e28b08',94,83,44,44,256,83,0.8901,1,'2026-07-08 22:47:29'),(105,'08b66172d1bb4b54a126beb135f4db44',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:04:00'),(106,'977cdf4ad37543808239e87f7db2d439',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:04:23'),(107,'25ffeaba94dc459eba53e83e7bfd8abb',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:04:25'),(108,'5226fc39a244483a983ef287141d6a8d',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:04:41'),(109,'b1effac792eb43c4ae90e0b82cc294e2',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:04:43'),(110,'44ceac7484ea4354b40689193a4bf4d7',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:04:45'),(111,'5ee06a8142064bb18978b46b8f0756dd',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:04:46'),(112,'e3b178fe7d964a26841382eefe256638',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:10:58'),(113,'12f04caf00454cc288783395c6b68068',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:11:09'),(114,'22ea41cd2fcc4927b479fab5ae16b237',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:11:23'),(115,'13e4d100b1654d6cbbd5b6b12f5347c8',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:11:46'),(116,'35a13742df814c60ab96c06a64591e78',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:14:39'),(117,'fd6d7a51d1b54313be8086da47129b64',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:14:41'),(118,'a15d6109aa9d403896947a798c3714c9',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:14:47'),(119,'7e49bd490bed4e5bad5505daad5a0ce4',152,84,44,44,256,84,0.8387,1,'2026-07-08 23:14:48');
/*!40000 ALTER TABLE `captcha_puzzle_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `captcha_verify_record`
--

DROP TABLE IF EXISTS `captcha_verify_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `captcha_verify_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录唯一ID',
  `trace_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求追踪ID',
  `image_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '使用的图片ID',
  `client_ip` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端IP地址',
  `user_agent` text COLLATE utf8mb4_unicode_ci COMMENT '客户端User-Agent',
  `slider_offset` smallint NOT NULL COMMENT '用户拖动偏移量(像素)',
  `target_offset` smallint NOT NULL COMMENT '目标位置偏移量(像素)',
  `is_passed` tinyint(1) NOT NULL COMMENT '验证结果:1-通过,0-失败',
  `cost_time` int NOT NULL COMMENT '验证耗时(毫秒)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_image_id` (`image_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `captcha_verify_record`
--

LOCK TABLES `captcha_verify_record` WRITE;
/*!40000 ALTER TABLE `captcha_verify_record` DISABLE KEYS */;
INSERT INTO `captcha_verify_record` (`id`, `trace_id`, `image_id`, `client_ip`, `user_agent`, `slider_offset`, `target_offset`, `is_passed`, `cost_time`, `create_time`) VALUES (1,'trace_20240101_000001','a1b2c3d4-e5f6-7890-abcd-ef1234567890','192.168.1.100','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0',152,152,1,2340,'2024-01-01 08:00:01'),(2,'trace_20240101_000002','b2c3d4e5-f678-90ab-cdef-0123456789ab','192.168.1.101','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0',134,134,1,1890,'2024-01-01 08:05:12'),(3,'trace_20240101_000003','c3d4e5f6-7890-abcd-ef01-23456789abcd','192.168.1.102','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605.1.15',160,161,1,3120,'2024-01-01 08:10:33'),(4,'trace_20240101_000004','d4e5f678-90ab-cdef-0123-456789abcdef','192.168.1.103','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Edge/120.0.0.0',140,143,0,1560,'2024-01-01 08:15:45'),(5,'trace_20240101_000005','e5f67890-abcd-ef01-2345-6789abcdef01','192.168.1.104','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Firefox/121.0',125,125,1,2780,'2024-01-01 08:20:56'),(6,'trace_20240101_000006','f67890ab-cdef-0123-4567-89abcdef0123','192.168.1.105','Mozilla/5.0 (Linux; Android 13; SM-G998B) Chrome/120.0.0.0',170,170,1,4210,'2024-01-01 08:25:07'),(7,'trace_20240101_000007','7890abcd-ef01-2345-6789-abcdef012345','192.168.1.106','Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) Safari/605.1.15',116,116,1,3540,'2024-01-01 08:30:18'),(8,'trace_20240101_000008','89abcdef-0123-4567-89ab-cdef01234567','192.168.1.107','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0',152,179,0,1920,'2024-01-01 08:35:29'),(9,'trace_20240101_000009','9abcdef0-1234-5678-9abc-def012345678','192.168.1.108','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0',107,107,1,2150,'2024-01-01 08:40:40'),(10,'trace_20240101_000010','abcdef01-2345-6789-abcd-ef0123456789','192.168.1.109','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0',188,188,1,2890,'2024-01-01 08:45:51'),(11,'trace_20240101_000011','a1b2c3d4-e5f6-7890-abcd-ef1234567890','10.0.0.50','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0',145,152,0,1230,'2024-01-01 09:00:00'),(12,'trace_20240101_000012','b2c3d4e5-f678-90ab-cdef-0123456789ab','10.0.0.51','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Edge/120.0.0.0',130,134,0,980,'2024-01-01 09:05:30'),(13,'trace_20240101_000013','c3d4e5f6-7890-abcd-ef01-23456789abcd','10.0.0.52','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Firefox/121.0',158,161,1,3010,'2024-01-01 09:10:45'),(14,'trace_20240101_000014','d4e5f678-90ab-cdef-0123-456789abcdef','10.0.0.53','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605.1.15',142,143,1,2450,'2024-01-01 09:15:20'),(15,'trace_20240101_000015','e5f67890-abcd-ef01-2345-6789abcdef01','10.0.0.54','Mozilla/5.0 (Linux; Android 13; Pixel 7) Chrome/120.0.0.0',128,125,0,1870,'2024-01-01 09:20:10'),(16,'captcha_1783438057023_3thf99cxg','e2efcfc766d9438f867f8628e0980e82','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',0,161,0,2,'2026-07-07 23:27:41'),(17,'captcha_1783438065788_nnbmd6kv2','2ac12c9bf96a4365b2a559b845107c29','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',234,161,0,1,'2026-07-07 23:27:50'),(18,'captcha_1783438070880_xb85dub1f','746a3f62f27347318773806b07b2a7e6','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',175,161,0,4,'2026-07-07 23:27:53'),(19,'captcha_1783438116609_k8bjts843','0f10b82dfd084e6b87fa10144515785d','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',19,161,0,2,'2026-07-07 23:28:39'),(20,'captcha_1783518264083_hrk4xctbr','21f521a832a148b9baba77651941571c','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',25,161,0,3,'2026-07-08 21:44:29'),(21,'captcha_1783518296733_2tav78u30','5a796735bf4e4d0994bbd8b46855ca2c','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',18,161,0,3,'2026-07-08 21:45:01'),(22,'captcha_1783518305366_zxv8abe00','2ca73e21335a4b50bf597edc9534a943','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',187,161,0,1,'2026-07-08 21:45:07'),(23,'captcha_1783518467234_ulv7p67p2','2650a1470a7e4cf6b64ba43d935c89b5','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',26,161,0,2,'2026-07-08 21:47:50'),(24,'captcha_1783518501770_lo91yli7j','125b643237984f04b21a5de76e87cb5e','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',7,161,0,2,'2026-07-08 21:48:27'),(25,'captcha_1783518716943_473aez2o7','ffc084cd49a04e499a7574a3ddd307b9','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',178,161,0,2,'2026-07-08 21:52:01'),(26,'captcha_1783518739207_qb97i97pe','d0da8a38c9b4470bafc2707a8ce008f7','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',20,161,0,0,'2026-07-08 21:52:24'),(27,'captcha_1783518747898_cgq0qjdeo','ac22fffba66d49449e47604af19b40cd','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',21,161,0,1,'2026-07-08 21:52:32'),(28,'captcha_1783518753922_zcwr40y7a','e9f18962d5e94b4fbcca5486bc300540','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',172,161,0,1,'2026-07-08 21:52:36'),(29,'captcha_1783518758548_iptmx1jdx','e5fbc3adab04463e9d64629dca2770e0','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',13,161,0,2,'2026-07-08 21:52:44'),(30,'captcha_1783518925417_ra2zvvkzi','de519b40b7f44b6dab4e17b790371e1a','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',23,161,0,2,'2026-07-08 21:55:28'),(31,'captcha_1783518936192_8zd890vum','b7c6374674344f28b702687a6a827143','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',25,161,0,1,'2026-07-08 21:55:39'),(32,'captcha_1783519009533_axsbzkmy9','59ac9ce15d5848838f44c6a8419b7867','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',4,174,0,4,'2026-07-08 21:56:54'),(33,'captcha_1783519016026_hf88u0jeq','243d2698b96e423e901a47764acc48e4','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',81,193,0,3,'2026-07-08 21:56:59'),(34,'captcha_1783519020854_97u68z16o','06a22311a5534645a52022a27ef424c3','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',63,187,0,2,'2026-07-08 21:57:11'),(35,'captcha_1783519032457_92iygdqvq','8a02ee6b668b4d9d92aefc4f002c901e','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',68,166,0,1,'2026-07-08 21:57:18'),(36,'captcha_1783519344646_nqc44fgcl','eb17334b83fb44cda1b9b10e27970fb8','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',1,182,0,4,'2026-07-08 22:02:30'),(37,'captcha_1783519353556_jsyi0t5fa','ed5ea8f358eb422fae01dc3cb173f7b7','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',73,182,0,3,'2026-07-08 22:02:38'),(38,'captcha_1783519360108_7tl9ch4wr','9d4bce23e9024edb9c3a3883650b3fdf','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',73,154,0,3,'2026-07-08 22:02:45'),(39,'captcha_1783519367081_vg463zpei','1a8a78bc6d3d4ee3ba7ead512f5ef71c','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',100,193,0,1,'2026-07-08 22:02:54'),(40,'captcha_1783519589308_s4cx60vwf','3bc79f956b2f4b8e9fd598a65351fccd','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',168,168,1,5,'2026-07-08 22:06:34'),(41,'captcha_1783519619837_p8d3faxry','ef6d2ed0e2ee454e8a45b2ff2ec8dcbc','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',115,172,0,2,'2026-07-08 22:07:02'),(42,'captcha_1783519626803_yaypn049z','a06e9adcbf354a6c83434d6c6de7b968','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',81,154,0,1,'2026-07-08 22:07:09'),(43,'captcha_1783519630608_yvpbed04l','e04a621b37d64c5fa9c6f02d6176a802','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',101,154,0,2,'2026-07-08 22:07:13'),(44,'captcha_1783519634646_o2khxv0pk','639105ea7106436a984ba32d7e524de3','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',105,179,0,1,'2026-07-08 22:07:19'),(45,'captcha_1783519665419_c09f1ugdz','fc694c923cef4bafa9477b6b003de8c0','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',73,172,0,2,'2026-07-08 22:07:58'),(46,'captcha_1783520017150_elx08y53g','cc2d026fc69941e697a840050a4c0beb','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',94,187,0,2,'2026-07-08 22:13:41'),(47,'captcha_1783520022921_qvhvavl6k','1d08ef6a586b4189b7662fde4dc78c1f','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',255,190,0,0,'2026-07-08 22:13:46'),(48,'captcha_1783520028537_udawttfq2','70f338535b5a4ae18e636be6797080eb','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',66,180,0,1,'2026-07-08 22:13:54'),(49,'captcha_1783520050556_811t661qu','7c485f2e920543cc8af96d8b8b0200bb','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',85,191,0,1,'2026-07-08 22:14:16'),(50,'captcha_1783520061837_bgw0h7bkv','9e75f3a3463d4846ac20b1b6366d364a','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',67,177,0,1,'2026-07-08 22:14:25'),(51,'captcha_1783520084518_rxoe4s4iq','c997300e09904c069963f987acd3376c','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',73,181,0,2,'2026-07-08 22:14:49'),(52,'captcha_1783520095753_q52816zpv','6018447323ce46bf9aa62d99235c8f2a','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',71,173,0,2,'2026-07-08 22:14:58'),(53,'captcha_1783520106471_zcnmkfov3','0117062ba91341b6a5c0a4660e2bd796','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',82,186,0,1,'2026-07-08 22:15:09'),(54,'captcha_1783520120281_pywg22wwu','e697d56a753c49eb88b47428e8e99e6f','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',91,158,0,0,'2026-07-08 22:15:25'),(55,'captcha_1783520162415_ye9swivyd','9ce0a4eafce04da2a10f35d8b16082b8','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',70,164,0,2,'2026-07-08 22:16:05'),(56,'captcha_1783520183958_06m4qixs8','ae5b6e3a6a0b4cfea9c656aee663c457','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',91,168,0,1,'2026-07-08 22:16:27'),(57,'captcha_1783520193320_thi1vw6kp','9ea1417f74d842df98b252a45af0152c','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',99,162,0,1,'2026-07-08 22:16:41'),(58,'captcha_1783520219017_wjgfwie45','5ed324977efa47139118e102333ecdf4','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',195,195,1,0,'2026-07-08 22:17:01'),(59,'captcha_1783520231229_cskinmv5a','f49c646cd4bb4e108cafac4d4ff6bcf2','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',176,176,1,0,'2026-07-08 22:17:13'),(60,'captcha_1783520563345_xwru321e4','69ad1bf6137a48e284ba5be7c073e784','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',167,167,1,2,'2026-07-08 22:22:47'),(61,'captcha_1783520578428_tkfwlsvpz','b53aa66174bc47e2b5ae7bdd400bac1b','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',151,181,0,2,'2026-07-08 22:23:01'),(62,'captcha_1783520582005_l6yo4wump','bc9966347cfe4e6fafa0be0c56b1c112','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',74,180,0,2,'2026-07-08 22:23:08'),(63,'captcha_1783520588735_3miefkgte','dff53392e1e648f788cba119afabbf09','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',196,196,1,1,'2026-07-08 22:23:10'),(64,'captcha_1783521136563_z4e2aka8y','182227cfc1c14c47b3811e10ff46fb64','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',68,179,0,1,'2026-07-08 22:32:25'),(65,'captcha_1783521146196_ecwq73ixp','93373889dcde4fa8965d692d2aba700a','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',195,195,1,1,'2026-07-08 22:32:28'),(66,'captcha_1783521157311_xxzjpnt0x','51cca80e26514564a112fc270e377d45','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',157,183,0,1,'2026-07-08 22:32:39'),(67,'captcha_1783521160024_28r4nen0s','edce1b0fe6414ffd922de4634a22c98f','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',189,189,1,1,'2026-07-08 22:32:42'),(68,'captcha_1783521634496_9s4d1rvy2','a1cd34c07f084791ab4522d6fc23543b','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',9,177,0,1,'2026-07-08 22:40:40'),(69,'captcha_1783521640660_1598zaii1','88e2d7009a234a28a87df47d6cf297ed','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',103,176,0,1,'2026-07-08 22:40:42'),(70,'captcha_1783521642455_voo89660w','15937353a9ef490da365057bffa2e0df','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',162,162,1,1,'2026-07-08 22:40:46'),(71,'captcha_1783522041929_n5lujk2cl','ce67c1db85994f6db1697441776ab2af','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',85,177,0,1,'2026-07-08 22:47:24'),(72,'captcha_1783522045424_xf6vql547','871338942cc244bab8b210bcda1c5a26','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',7,162,0,2,'2026-07-08 22:47:28'),(73,'captcha_1783523039463_5j6zcc41k','08b66172d1bb4b54a126beb135f4db44','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',104,104,1,5,'2026-07-08 23:04:11'),(74,'captcha_1783523065278_9ijgrz6bk','25ffeaba94dc459eba53e83e7bfd8abb','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',104,104,1,1,'2026-07-08 23:04:29'),(75,'captcha_1783523680721_7mvd0bk8n','fd6d7a51d1b54313be8086da47129b64','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',72,104,0,3,'2026-07-08 23:14:46'),(76,'captcha_1783523688304_ew89s9983','7e49bd490bed4e5bad5505daad5a0ce4','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',104,104,1,1,'2026-07-08 23:14:52');
/*!40000 ALTER TABLE `captcha_verify_record` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `carousel_group_items` DISABLE KEYS */;
INSERT INTO `carousel_group_items` (`id`, `group_id`, `carousel_id`, `sort_order`, `is_active`, `created_at`) VALUES (1,1,1,1,1,'2026-02-14 09:30:45'),(2,1,2,2,1,'2026-02-14 09:30:45'),(3,1,3,3,1,'2026-02-14 09:30:45'),(4,2,4,1,1,'2026-02-14 09:30:45'),(5,2,5,2,1,'2026-02-14 09:30:45'),(6,3,6,1,1,'2026-02-14 09:30:45'),(7,3,7,2,1,'2026-02-14 09:30:45'),(8,4,8,1,1,'2026-02-14 09:30:45');
/*!40000 ALTER TABLE `carousel_group_items` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `carousel_groups` DISABLE KEYS */;
INSERT INTO `carousel_groups` (`id`, `group_name`, `group_code`, `description`, `display_type`, `auto_play`, `auto_play_interval`, `animation_type`, `show_indicators`, `show_arrows`, `pause_on_hover`, `width`, `height`, `aspect_ratio`, `max_items`, `is_active`, `sort_order`, `created_at`, `updated_at`) VALUES (1,'首页顶部轮播','home_top','网站首页顶部主轮播','multiple',1,5000,'slide',1,1,1,NULL,600,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(2,'首页中部轮播','home_middle','首页中部推荐轮播','multiple',1,4000,'slide',1,1,1,NULL,400,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(3,'景点详情页轮播','attraction_detail','景点详情页顶部轮播','single',1,3000,'slide',1,1,1,NULL,500,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(4,'APP开屏轮播','app_splash','移动端开屏广告轮播','fullscreen',1,3000,'slide',1,1,1,NULL,800,NULL,10,1,0,'2026-02-14 09:30:45','2026-02-14 09:30:45');
/*!40000 ALTER TABLE `carousel_groups` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `carousel_stats` DISABLE KEYS */;
INSERT INTO `carousel_stats` (`id`, `carousel_id`, `stat_date`, `click_count`, `impression_count`, `unique_click_count`, `unique_impression_count`, `pc_click_count`, `mobile_click_count`, `pc_impression_count`, `mobile_impression_count`, `created_at`, `updated_at`) VALUES (1,1,'2025-02-01',1250,15000,0,0,800,450,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(2,1,'2025-02-02',1350,16200,0,0,850,500,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(3,2,'2025-02-01',980,12000,0,0,600,380,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(4,3,'2025-02-01',2100,25000,0,0,1200,900,0,0,'2026-02-14 09:30:45','2026-02-14 09:30:45');
/*!40000 ALTER TABLE `carousel_stats` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `carousels` DISABLE KEYS */;
INSERT INTO `carousels` (`id`, `title`, `subtitle`, `image_url`, `mobile_image_url`, `thumbnail_url`, `link_type`, `link_url`, `link_target`, `target_id`, `button_text`, `button_color`, `text_color`, `text_shadow`, `overlay_color`, `overlay_opacity`, `position`, `device_type`, `user_type`, `start_time`, `end_time`, `is_always_show`, `click_count`, `impression_count`, `sort_order`, `is_active`, `is_deleted`, `remark`, `created_by`, `created_at`, `updated_at`) VALUES (1,'浪漫巴黎之旅','探索埃菲尔铁塔的浪漫与优雅','http://localhost:8080/upload/65dba708-6f3e-4367-9371-3d14f8f2ca0b.png','/images/carousel/paris_mobile.jpg',NULL,'attraction',NULL,'_self',1,'立即预订',NULL,'#FFFFFF',1,NULL,0.3,'home_top','all','all','2025-01-01 00:00:00','2025-12-31 23:59:00',1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:23:56'),(2,'古罗马文明','穿越千年，感受斗兽场的辉煌','http://localhost:8080/upload/f272c7a6-a6a8-4be2-9a2c-75d33652e8f4.png','/images/carousel/rome_mobile.jpg',NULL,'attraction',NULL,'_self',2,'了解更多',NULL,'#FFD700',1,NULL,0.4,'home_top','all','all',NULL,NULL,1,0,0,2,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:21:39'),(3,'纽约不夜城','自由女神像俯瞰曼哈顿','http://localhost:8080/upload/92096a7a-1e61-439d-b083-5c865d3c4f8d.png','/images/carousel/nyc_mobile.jpg',NULL,'attraction',NULL,'_self',3,'查看详情',NULL,'#FFFFFF',1,NULL,0.2,'attraction','all','all',NULL,NULL,1,0,0,3,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:21:39'),(4,'暑期特惠','欧洲五国游限时8折优惠','http://localhost:8080/upload/7b966a24-32cb-410a-9a1d-c2ba1c0cefb1.png','/images/carousel/summer_sale_mobile.jpg',NULL,'attraction','https://example.com/summer-sale','_self',NULL,'立即抢购',NULL,'#FF0000',1,NULL,0.2,'home_top','all','all','2025-06-01 00:00:00','2025-08-31 23:59:59',1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:23:00'),(5,'亲子游推荐','最适合带娃的十大景点','http://localhost:8080/upload/f9d5edf7-5b9e-4fbf-a331-0d14b66ee33d.png','/images/carousel/family_mobile.jpg',NULL,'attraction','/family-travel','_self',NULL,'查看推荐',NULL,'#4CAF50',1,NULL,0.3,'destination','all','all',NULL,NULL,1,0,0,2,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-23 16:23:00'),(6,'埃菲尔铁塔全景','360度俯瞰巴黎市区','http://localhost:8080/upload/5b3ee4e2-9a69-4606-84ac-f69960a25c1c.png','/images/attractions/eiffel/detail_1_mobile.jpg',NULL,'attraction',NULL,'_self',1,'查看详情',NULL,'#FFFFFF',1,NULL,0.2,'souvenir','all','all',NULL,NULL,1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(7,'埃菲尔铁塔夜景','璀璨灯光秀每晚上演','http://localhost:8080/upload/9f14736f-71b8-477d-b89f-cecb7251481f.png','/images/attractions/eiffel/detail_2_mobile.jpg',NULL,'attraction',NULL,'_self',1,'查看详情',NULL,'#FFFFFF',1,NULL,0.3,'souvenir','all','all',NULL,NULL,1,0,0,2,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(8,'APP限时福利','新用户注册立减100元','http://localhost:8080/upload/66337df7-e754-4921-89c5-20fb12bf80db.png','/images/app_splash/splash_1_mobile.jpg',NULL,'url','https://example.com/download','_self',NULL,'立即下载',NULL,'#FFFFFF',1,NULL,0.2,'destination','all','all','2025-01-01 00:00:00','2025-03-31 23:59:00',1,0,0,1,1,0,NULL,NULL,'2026-02-14 09:30:45','2026-02-14 09:30:45'),(9,'夏日狂欢节','清凉一夏，水上乐园特惠','http://localhost:8080/upload/601c3402-1980-4c6c-bb9b-58e4c9bc70f1.png','https://images.unsplash.com/photo-1518005068251-37900150dfca','https://images.unsplash.com/photo-1477959858617-67f85cf4f1df?w=200','attraction',NULL,'_self',101,'立即预订','#FF6B6B','#FFFFFF',1,'#000000',0.3,'home_top','all','all','2024-07-01 00:00:00','2024-08-31 23:59:00',0,1250,45800,1,1,0,'夏季主题活动','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(10,'浪漫巴黎','埃菲尔铁塔下的爱情故事','https://images.unsplash.com/photo-1502602898657-3e91760cbb34','https://images.unsplash.com/photo-1499856871958-5b9627545d1a','https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=200','country',NULL,'_self',33,'探索巴黎','#9B59B6','#FFFFFF',1,'#2C3E50',0.4,'destination','all','visitor','2024-01-01 00:00:00','2024-12-31 23:59:00',1,3420,89200,2,1,0,'欧洲热门目的地','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(11,'限时特惠','暑期出游，立减500元','http://localhost:8080/upload/ee45b4f2-296a-4dde-81eb-fed535040988.png','https://images.unsplash.com/photo-1508672019048-805c876b267e','https://images.unsplash.com/photo-1503220317375-aaad61436b1b?w=200','url','https://www.example.com/summer-sale','_blank',NULL,'领取优惠','#E74C3C','#FFFFFF',1,'#34495E',0.2,'hotel','all','logged_in','2024-06-01 00:00:00','2024-07-15 23:59:00',1,890,23400,3,1,0,'暑期促销活动','marketing','2026-03-12 13:55:08','2026-03-12 13:55:08'),(12,'日本樱花季','春日赏樱，浪漫之旅','https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e','https://images.unsplash.com/photo-1526481280693-3bfa7568e0f3','https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=200','country',NULL,'_self',45,'查看详情','#FF9F4A','#FFFFFF',1,'#000000',0.3,'community','pc','all','2024-03-01 00:00:00','2024-04-30 23:59:00',1,2100,56700,4,1,0,'春季赏樱专题','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(13,'上海迪士尼','梦幻王国，童心未泯','http://localhost:8080/upload/dd168290-667b-40ee-8dd7-0874be1bf08e.png','https://images.unsplash.com/photo-1559348346-1f1f5c0f1b0b','https://images.unsplash.com/photo-1522804144733-221e3d3a4d8b?w=200','attraction',NULL,'_self',156,'立即购票','#F1C40F','#2C3E50',0,'#FFFFFF',0.2,'hotel','all','vip','2024-01-01 00:00:00','2024-12-31 23:59:00',1,5600,124500,5,1,0,'热门景点','operator','2026-03-12 13:55:08','2026-03-12 13:55:08'),(14,'北京故宫','穿越千年的文化之旅','http://localhost:8080/upload/5c6a9496-84f3-47ee-8ce0-7d24cfeab1d2.png','https://images.unsplash.com/photo-1581790746931-e6b8c3ef7d3c','https://images.unsplash.com/photo-1557872943-16a5ac26437e?w=200','attraction',NULL,'_self',89,'探索历史','#E67E22','#FFFFFF',1,'#2C3E50',0.3,'home_top','all','visitor','2024-02-01 00:00:00','2024-11-30 23:59:00',1,1890,45300,6,1,0,'文化遗产','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(15,'马尔代夫','天堂海岛，蜜月首选','http://localhost:8080/upload/ac98d60e-5c89-4d8a-8165-fcba814c94c8.png','https://images.unsplash.com/photo-1573843981267-be1999ff37cd','https://images.unsplash.com/photo-1514282401047-d79a71a590e8?w=200','country',NULL,'_self',112,'查看套餐','#3498DB','#FFFFFF',1,'#000000',0.4,'food','mobile','logged_in','2024-05-01 00:00:00','2024-10-31 23:59:00',1,3200,67800,7,1,0,'海岛度假','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(16,'桂林山水','甲天下的山水风光','http://localhost:8080/upload/e63a2954-fb15-499b-8f25-a68db3f3703a.png','https://images.unsplash.com/photo-1527090526205-beaac8dc3c62','https://images.unsplash.com/photo-1528297506724-1b832ed7c9e1?w=200','city',NULL,'_self',234,'立即出发','#27AE60','#FFFFFF',1,'#34495E',0.3,'community','pc','all','2024-04-01 00:00:00','2024-09-30 23:59:00',1,980,32100,8,1,0,'自然风光','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(17,'会员专享','VIP专属优惠，立减1000','http://localhost:8080/upload/09479fe2-e04c-4e78-be90-e480d3723193.png','https://images.unsplash.com/photo-1556742502-ec7c0e9f34b4','https://images.unsplash.com/photo-1556742502-ec7c0e9f34b4?w=200','url','https://www.example.com/vip-offer','_blank',NULL,'成为会员','#8E44AD','#FFFFFF',1,'#000000',0.2,'attraction','all','vip','2024-01-01 00:00:00','2024-12-31 23:59:00',1,450,8900,9,1,0,'会员专属活动','marketing','2026-03-12 13:55:08','2026-03-12 13:55:08'),(18,'成都大熊猫','萌宠来袭，国宝之旅','http://localhost:8080/upload/0e845557-a747-4301-b27f-8e172049859f.png','https://images.unsplash.com/photo-1568571428749-26d1a7e9b762','https://images.unsplash.com/photo-1538098848135-8df3c6db1bf1?w=200','attraction',NULL,'_self',178,'预约参观','#2ECC71','#FFFFFF',0,'#27AE60',0.1,'food','all','all','2024-03-01 00:00:00','2024-08-31 23:59:00',1,2300,48900,10,1,0,'亲子游推荐','operator','2026-03-12 13:55:08','2026-03-12 13:55:08'),(19,'纽约时代广场','不夜城的魅力','http://localhost:8080/upload/0dc479bf-9e25-45b8-8315-a997aec910b1.png','https://images.unsplash.com/photo-1534430480872-3498386e7856','https://images.unsplash.com/photo-1485738422979-f5c462d49f74?w=200','city',NULL,'_self',567,'了解更多','#E67E22','#FFFFFF',1,'#000000',0.4,'food','all','visitor','2024-06-01 00:00:00','2024-12-31 23:59:00',1,1500,28700,11,1,0,'城市观光','admin','2026-03-12 13:55:08','2026-03-12 13:55:08'),(20,'张家界','阿凡达的奇幻世界','http://localhost:8080/upload/79f89234-86d9-4eed-a2bc-d5a404c306e8.png','https://images.unsplash.com/photo-1545558014-8692077e9b5c','https://images.unsplash.com/photo-1519046904884-53103b34b5b7?w=200','attraction',NULL,'_self',201,'探秘仙境','#9B59B6','#FFFFFF',1,'#34495E',0.3,'home_top','all','logged_in','2024-05-15 00:00:00','2024-10-15 23:59:00',0,1750,39600,12,1,0,'自然奇观','editor','2026-03-12 13:55:08','2026-03-12 13:55:08'),(21,'春节特辑','阖家欢乐，春节出游','http://localhost:8080/upload/e1adbd4f-aa22-4ab6-8100-636266c8b768.png','https://images.unsplash.com/photo-1545569341-97d0d0c2e2b2','https://images.unsplash.com/photo-1545569341-97d0d0c2e2b2?w=200','none',NULL,'_self',NULL,'查看活动','#E74C3C','#FFD700',1,'#2C3E50',0.2,'home_top','pc','all','2024-01-15 00:00:00','2024-02-15 23:59:00',1,670,15200,13,1,0,'节日活动','marketing','2026-03-12 13:55:08','2026-03-12 13:55:08'),(22,'西安兵马俑','世界第八大奇迹','http://localhost:8080/upload/5a8acf03-12a4-43de-8a8b-3e4fecd0f94d.png','https://images.unsplash.com/photo-1621106036928-6ce0b7d2a7d1','https://images.unsplash.com/photo-1575986767340-5d3b52b1a78b?w=200','attraction',NULL,'_self',67,'探索历史','#D35400','#FFFFFF',1,'#000000',0.4,'strategy','mobile','all','2024-02-01 00:00:00','2024-11-30 23:59:00',1,2100,51200,14,1,0,'历史文化','operator','2026-03-12 13:55:08','2026-03-12 13:55:08'),(23,'周末特惠','说走就走的短途旅行','https://images.unsplash.com/photo-1506905925346-21bda4d32df4','https://images.unsplash.com/photo-1530789253388-582c481c54b0','https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=200','url','https://www.example.com/weekend-deals','_blank',NULL,'查看优惠','#3498DB','#FFFFFF',0,'#2980B9',0.1,'strategy','all','visitor','2024-04-01 00:00:00','2024-09-30 23:59:00',1,890,18900,15,1,0,'周末游推荐','admin','2026-03-12 13:55:08','2026-03-12 13:55:08');
/*!40000 ALTER TABLE `carousels` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` (`id`, `user_id`, `item_type`, `item_id`, `item_name`, `price`, `quantity`, `create_time`, `update_time`, `image`) VALUES (1,1,'product',1,'北京烤鸭礼盒',198.00,2,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(2,1,'ticket',1,'北京-上海火车票',553.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(3,2,'product',2,'上海雪花膏',68.00,3,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(4,2,'ticket',2,'上海-北京火车票',553.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(5,3,'product',3,'杭州西湖龙井茶叶',298.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(6,3,'ticket',3,'北京-广州机票',1200.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(7,4,'product',4,'四川大熊猫玩偶',88.00,2,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(8,4,'ticket',4,'广州-北京机票',1200.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(9,5,'product',5,'广东广绣',398.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL),(10,5,'ticket',5,'北京-西安火车票',515.00,1,'2026-02-14 17:21:56','2026-02-14 17:21:56',NULL);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `chat_group` DISABLE KEYS */;
INSERT INTO `chat_group` (`id`, `name`, `description`, `creator_id`, `max_members`, `member_count`, `avatar`, `create_time`, `update_time`) VALUES (3,'开心聊天群','111',15,200,5,'http://localhost:8080/upload/f8389226-682e-4447-8b10-8ae0353d6ef7.jpeg','2026-05-10 11:43:01','2026-06-05 21:50:08'),(4,'大象世界','分享快乐',15,200,4,'http://localhost:8080/upload/876a0388-5ad0-4d1c-b1a0-b8614b5ee56f.png','2026-05-12 14:06:02','2026-05-12 14:06:02');
/*!40000 ALTER TABLE `chat_group` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` (`id`, `sender_id`, `receiver_id`, `content`, `message_type`, `is_read`, `create_time`) VALUES (58,21,15,'😴','emoji',1,'2026-05-12 12:50:47'),(59,22,15,'http://localhost:8080/upload/3988b87e-6425-4419-94ff-cb516ee41beb.webp','emoji',1,'2026-05-12 14:22:17'),(60,22,15,'仔阿康吗','text',1,'2026-05-12 14:22:23'),(61,22,15,'🚀','emoji',1,'2026-05-12 14:23:48'),(62,19,15,'撒旦','text',1,'2026-05-12 17:32:34'),(63,19,15,'你好呀','text',0,'2026-05-14 22:34:30'),(64,15,19,'嗯嗯干什么','text',0,'2026-05-14 22:34:37'),(65,15,19,'🚀','emoji',0,'2026-05-14 22:34:41'),(66,15,19,'http://localhost:8080/upload/a7ad4450-614d-4766-b7bc-d3e74bbe0922.jpeg','emoji',0,'2026-05-14 22:34:45'),(67,19,15,'http://localhost:8080/upload/06091029-102d-46b6-a900-767389935712.png','image',0,'2026-05-14 22:34:55'),(68,23,15,'📚','emoji',0,'2026-06-05 21:46:57'),(69,23,15,'你在干嘛呢？','text',0,'2026-06-05 21:47:10'),(70,15,23,'吃饭饭','text',1,'2026-06-05 21:47:18'),(71,19,15,'😘','emoji',0,'2026-06-22 23:32:09'),(72,15,19,'😃','text',0,'2026-06-22 23:32:27'),(73,15,19,'😃','text',0,'2026-06-23 23:17:21'),(74,15,19,'123213','text',0,'2026-06-23 23:17:25'),(75,15,22,'🎁','emoji',0,'2026-07-02 22:37:21'),(76,15,22,'在干嘛呀','text',0,'2026-07-02 22:37:31'),(77,22,15,'吃饭呢怎么了','text',1,'2026-07-02 22:37:45'),(78,15,21,'📚','emoji',0,'2026-07-02 22:43:50'),(79,15,21,'🚀','emoji',0,'2026-07-02 22:48:05'),(80,15,19,'🚀','emoji',0,'2026-07-11 23:05:57'),(81,15,19,'在干嘛你🚀','text',0,'2026-07-11 23:06:25'),(82,19,15,'吃饭','text',0,'2026-07-11 23:06:40');
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `cities` DISABLE KEYS */;
INSERT INTO `cities` (`id`, `name`, `chinese_name`, `country_id`, `is_capital`, `area`, `population`, `timezone`, `description`, `latitude`, `longitude`, `famous_for`, `best_season`, `created_at`) VALUES (1,'Beijing','北京',1,1,16410.54,21540000,'UTC+8','中国的首都，拥有悠久的历史和丰富的文化遗产。',39.90420000,116.40740000,'故宫、长城、颐和园','春秋','2026-02-14 09:23:52'),(2,'Shanghai','上海',1,0,6340.50,24870000,'UTC+8','中国最大的城市，经济金融中心。',31.23040000,121.47370000,'外滩、东方明珠、豫园','四季皆宜','2026-02-14 09:23:52'),(3,'Tokyo','东京',2,1,2194.07,13960000,'UTC+9','日本的首都，现代化大都市。',35.67620000,139.65030000,'东京塔、明治神宫、涩谷','春秋','2026-02-14 09:23:52'),(4,'New York','纽约',3,0,1214.40,8336817,'UTC-5','美国最大的城市，世界金融中心。',40.71280000,-74.00600000,'自由女神像、时代广场、中央公园','春秋','2026-02-14 09:23:52'),(5,'Paris','巴黎',4,1,105.40,2148000,'UTC+1','法国的首都，浪漫之都。',48.85660000,2.35220000,'埃菲尔铁塔、卢浮宫、巴黎圣母院','春秋','2026-02-14 09:23:52'),(6,'Sydney','悉尼',5,0,12368.19,5312000,'UTC+10','澳大利亚最大的城市。',-33.86880000,151.20930000,'悉尼歌剧院、海港大桥、邦迪海滩','春秋','2026-02-14 09:23:52'),(7,'Cape Town','开普敦',6,0,2499.00,4710000,'UTC+2','南非的立法首都，美丽的海滨城市。',-33.92490000,18.42410000,'桌山、好望角、维多利亚港','春秋','2026-02-14 09:23:52'),(8,'Rio de Janeiro','里约热内卢',7,0,1200.00,6718903,'UTC-3','巴西的前首都，以狂欢节著称。',-22.90680000,-43.17290000,'基督像、科帕卡巴纳海滩、狂欢节','冬春','2026-02-14 09:23:52'),(9,'Berlin','柏林',8,1,891.80,3748000,'UTC+1','德国的首都，历史文化名城。',52.52000000,13.40500000,'勃兰登堡门、柏林墙、博物馆岛','春秋','2026-02-14 09:23:52'),(10,'Mumbai','孟买',9,0,603.40,20411274,'UTC+5:30','印度最大的城市，经济中心。',19.07600000,72.87770000,'泰姬玛哈酒店、贾特拉帕蒂·希瓦吉终点站、海滩','冬','2026-02-14 09:23:52');
/*!40000 ALTER TABLE `cities` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `collection` DISABLE KEYS */;
INSERT INTO `collection` (`id`, `user_id`, `item_type`, `item_id`, `create_time`) VALUES (1,1,'attraction',1,'2026-02-14 17:21:56'),(2,1,'hotel',1,'2026-02-14 17:21:56'),(3,1,'food',1,'2026-02-14 17:21:56'),(4,2,'attraction',2,'2026-02-14 17:21:56'),(5,2,'hotel',2,'2026-02-14 17:21:56'),(6,2,'food',2,'2026-02-14 17:21:56'),(7,3,'attraction',3,'2026-02-14 17:21:56'),(8,3,'hotel',3,'2026-02-14 17:21:56'),(9,3,'food',3,'2026-02-14 17:21:56'),(10,4,'attraction',4,'2026-02-14 17:21:56');
/*!40000 ALTER TABLE `collection` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` (`id`, `user_id`, `item_type`, `item_id`, `parent_id`, `content`, `images`, `rating`, `like_count`, `status`, `create_time`, `update_time`) VALUES (1,1,'attraction',1,0,'故宫真的很震撼，历史感很强，值得一去。','[\"https://example.com/comment1_1.jpg\"]',5,30,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'attraction',1,0,'人太多了，建议早点去，避开高峰期。',NULL,4,15,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'hotel',1,0,'酒店位置很好，服务态度也不错，下次还会选择。',NULL,5,20,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'hotel',1,0,'房间很干净，设施齐全，就是价格有点贵。',NULL,4,10,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'food',1,0,'北京烤鸭真的很正宗，皮脆肉嫩，推荐大家尝试。','[\"https://example.com/comment5_1.jpg\"]',5,25,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'food',1,0,'价格有点贵，但是味道确实不错。',NULL,4,8,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'product',1,0,'包装很好，味道也很正宗，送礼很合适。',NULL,5,15,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,8,'product',1,0,'真空包装的，方便携带，但是没有现做的好吃。',NULL,4,5,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,9,'news',1,0,'五一确实人很多，但是旅游体验还是不错的。',NULL,NULL,12,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,10,'community',1,0,'照片拍得很好，下次有机会也要去北京看看。',NULL,NULL,8,1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='社区帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_post`
--

LOCK TABLES `community_post` WRITE;
/*!40000 ALTER TABLE `community_post` DISABLE KEYS */;
INSERT INTO `community_post` (`id`, `user_id`, `title`, `content`, `images`, `videos`, `like_count`, `comment_count`, `collect_count`, `status`, `create_time`, `update_time`) VALUES (1,1,'北京之行归来，分享一些照片','五一假期去了北京，游览了故宫、长城等景点，人真的很多，但是景色很美。分享一些照片给大家。','[\"https://example.com/post1_1.jpg\", \"https://example.com/post1_2.jpg\"]',NULL,250,80,120,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'上海美食推荐','作为一个上海本地人，给大家推荐一些地道的上海美食，包括小笼包、生煎包、白斩鸡等。','[\"https://example.com/post2_1.jpg\", \"https://example.com/post2_2.jpg\"]',NULL,180,60,90,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'杭州西湖一日游攻略','整理了一份西湖一日游的详细攻略，包括最佳游览路线、必看景点、美食推荐等。','[\"https://example.com/post3_1.jpg\", \"https://example.com/post3_2.jpg\"]',NULL,220,70,100,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'成都大熊猫基地游玩体验','去了成都大熊猫基地，看到了很多可爱的大熊猫，分享一些照片和游玩体验。','[\"https://example.com/post4_1.jpg\", \"https://example.com/post4_2.jpg\"]',NULL,300,90,150,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'三亚海滩度假心得','在三亚度过了一个愉快的假期，分享一些海滩度假的心得和照片。','[\"https://example.com/post5_1.jpg\", \"https://example.com/post5_2.jpg\"]',NULL,280,85,130,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'西安兵马俑震撼之旅','参观了秦始皇兵马俑，被其规模和工艺所震撼，分享一些照片和感受。','[\"https://example.com/post6_1.jpg\", \"https://example.com/post6_2.jpg\"]',NULL,200,65,85,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'厦门鼓浪屿的慢生活','在鼓浪屿度过了两天慢生活，感受了岛上的文艺气息，分享一些照片和体验。','[\"https://example.com/post7_1.jpg\", \"https://example.com/post7_2.jpg\"]',NULL,190,62,88,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,22,'张家界天门山玻璃栈道体验','挑战了天门山的玻璃栈道，虽然有点害怕，但是风景真的很美，分享一些照片和体验。','http://localhost:8080/upload/fd439b0e-a0d9-4adb-b5db-d237f54656a8.png,http://localhost:8080/upload/146acc31-3846-45f5-b37e-ee2a72d2787d.png,http://localhost:8080/upload/b81c1fae-662b-4fc7-8004-3fe1556a3184.png',NULL,230,75,110,1,'2026-02-14 17:21:56','2026-06-03 23:47:35'),(9,21,'云南大理洱海骑行','在大理洱海骑行，沿途风景如画，分享一些照片和骑行路线推荐。','http://localhost:8080/upload/d1f11e87-b5c5-406f-8053-d2267907a053.png,http://localhost:8080/upload/9ca0dcf4-f204-4865-8b0e-bf87f6e32b99.png,http://localhost:8080/upload/3db42bf1-c68f-4f8c-9e21-61e82007904a.png','',210,68,95,1,'2026-02-14 17:21:56','2026-06-03 23:46:10'),(10,19,'黄山云海日出绝美景色','在黄山山顶等待日出，看到了壮观的云海日出，分享一些照片和拍摄技巧。','http://localhost:8080/upload/981ae605-a712-4166-aaa3-9cc2a48a7aa2.png,http://localhost:8080/upload/b56ff90f-5927-4f16-a061-24988970d2b9.png,http://localhost:8080/upload/d66696da-3e22-49f4-955e-f810383da028.png,http://localhost:8080/upload/dc61e8f2-2839-4582-8a65-9104ddc2eb05.png','',320,95,160,1,'2026-02-14 17:21:56','2026-06-03 23:45:30'),(11,15,'西双版纳一日游','大皖新闻讯“丫头，明年房租再降2万，你放手干！”9月22日晚，合肥潜水街一家小餐馆里，75岁的房东董先生轻描淡写的一句话，让店主杨女士瞬间泪崩。而这已不是他第一次降租——六年来，因目睹杨女士小店经营遭遇困难、女儿患病等困境，这位老人已累计为其减免了房租近10万元。','http://localhost:8080/upload/ed5478f0-0e5e-4c65-adeb-9c3c8fea2b80.png,http://localhost:8080/upload/8251961b-314a-4e20-85d5-93d24b0edc7f.png',NULL,0,0,0,1,'2026-06-03 23:15:10','2026-06-03 23:41:45');
/*!40000 ALTER TABLE `community_post` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `continents` DISABLE KEYS */;
INSERT INTO `continents` (`id`, `name`, `chinese_name`, `area`, `population`, `description`, `created_at`) VALUES (1,'Asia','亚洲',4457.90,4541668,'亚洲是世界上面积最大、人口最多的大洲，拥有丰富的文化和历史遗产。','2026-02-14 09:23:52'),(2,'Europe','欧洲',1016.00,747636,'欧洲是世界上发达国家最集中的大洲，拥有悠久的历史和灿烂的文化。','2026-02-14 09:23:52'),(3,'North America','北美洲',2470.90,579024,'北美洲包括美国、加拿大等国家，经济发达，科技先进。','2026-02-14 09:23:52'),(4,'South America','南美洲',1784.00,434189,'南美洲拥有丰富的自然资源和多样的生态系统，亚马逊雨林位于此处。','2026-02-14 09:23:52'),(5,'Africa','非洲',3037.00,1340598,'非洲是人类文明的发源地之一，拥有多样的文化和自然景观。','2026-02-14 09:23:52'),(6,'Oceania','大洋洲',867.00,41261,'大洋洲由众多岛屿组成，澳大利亚是其最大的国家。','2026-02-14 09:23:52'),(7,'Antarctica','南极洲',1397.00,0,'南极洲是世界上最寒冷的大洲，也是唯一没有永久居民的大洲。','2026-02-14 09:23:52');
/*!40000 ALTER TABLE `continents` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `countries` DISABLE KEYS */;
INSERT INTO `countries` (`id`, `name`, `chinese_name`, `continent_id`, `capital`, `area`, `population`, `currency`, `language`, `timezone`, `country_code`, `phone_code`, `flag_emoji`, `description`, `created_at`, `image`) VALUES (1,'China','中国',1,'北京',960.00,1412600000,'CNY','Chinese','UTC+8','CN','86','🇨🇳','中国是世界上人口最多的国家，拥有悠久的历史和灿烂的文化。','2026-02-14 09:23:52','http://localhost:8080/upload/9cb9edbd-5968-4f9b-a842-24bbd44fcc3e.png'),(2,'Japan','日本',1,'东京',37.80,125836000,'JPY','Japanese','UTC+9','JP','81','🇯🇵','日本是一个岛国，拥有独特的文化和先进的科技。','2026-02-14 09:23:52','http://localhost:8080/upload/c77593e8-2b84-4c5e-8420-200c871e3da3.png'),(3,'United States','美国',3,'华盛顿',937.30,331900000,'USD','English','UTC-5 to UTC-10','US','1','🇺🇸','美国是世界上最强大的国家之一，经济和科技发达。','2026-02-14 09:23:52','http://localhost:8080/upload/8f9cc438-4c95-40da-ba99-39852de990f1.png'),(4,'France','法国',2,'巴黎',55.10,67750000,'EUR','French','UTC+1','FR','33','🇫🇷','法国是欧洲的重要国家，以浪漫的文化和美食著称。','2026-02-14 09:23:52',NULL),(5,'Australia','澳大利亚',6,'堪培拉',769.20,25739000,'AUD','English','UTC+8 to UTC+11','AU','61','🇦🇺','澳大利亚是世界上面积第六大的国家，拥有独特的自然景观。','2026-02-14 09:23:52',NULL),(6,'South Africa','南非',5,'比勒陀利亚',122.10,59390000,'ZAR','Afrikaans, English','UTC+2','ZA','27','🇿🇦','南非是非洲最发达的国家之一，拥有丰富的自然资源。','2026-02-14 09:23:52',NULL),(7,'Brazil','巴西',4,'巴西利亚',851.60,213993000,'BRL','Portuguese','UTC-2 to UTC-5','BR','55','🇧🇷','巴西是南美洲最大的国家，拥有世界上最大的热带雨林。','2026-02-14 09:23:52',NULL),(8,'Germany','德国',2,'柏林',35.70,83240000,'EUR','German','UTC+1','DE','49','🇩🇪','德国是欧洲经济最强大的国家之一，以精密制造著称。','2026-02-14 09:23:52',NULL),(9,'India','印度',1,'新德里',328.70,1380004000,'INR','Hindi, English','UTC+5:30','IN','91','🇮🇳','印度是世界上人口第二多的国家，拥有多样的文化。','2026-02-14 09:23:52',NULL),(10,'Canada','加拿大',3,'渥太华',998.50,38246000,'CAD','English, French','UTC-3:30 to UTC-8','CA','1','🇨🇦','加拿大是世界上面积第二大的国家，自然资源丰富。','2026-02-14 09:23:52',NULL);
/*!40000 ALTER TABLE `countries` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `emoji` DISABLE KEYS */;
INSERT INTO `emoji` (`id`, `emoji_code`, `emoji_name`, `category`, `unicode`, `description`, `sort_order`, `is_active`, `create_time`, `update_time`) VALUES (1,'smile','笑脸','表情','U+1F604','开心的笑脸',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(2,'laugh','大笑','表情','U+1F602','大笑表情',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(3,'love','爱心眼','表情','U+1F60D','花痴表情',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(4,'sad','难过','表情','U+1F622','难过的表情',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(5,'cry','哭泣','表情','U+1F62D','哭泣表情',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(6,'angry','生气','表情','U+1F620','生气的表情',6,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(7,'surprised','惊讶','表情','U+1F62E','惊讶表情',7,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(8,'shy','害羞','表情','U+1F60A','害羞表情',8,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(9,'thumbs_up','点赞','手势','U+1F44D','竖起大拇指',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(10,'thumbs_down','差评','手势','U+1F44E','向下大拇指',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(11,'ok','OK','手势','U+1F44C','OK手势',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(12,'peace','和平','手势','U+270C','和平手势',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(13,'heart','红心','符号','U+2764','红心',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(14,'star','星星','符号','U+2B50','星星',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(15,'fire','火焰','符号','U+1F525','火焰',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(16,'gift','礼物','物品','U+1F381','礼物盒',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(17,'cake','蛋糕','物品','U+1F382','生日蛋糕',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(18,'coffee','咖啡','物品','U+2615','咖啡杯',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(19,'phone','电话','物品','U+1F4DE','电话',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(20,'mail','邮件','物品','U+2709','邮件',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(21,'sun','太阳','自然','U+2600','太阳',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(22,'cloud','云朵','自然','U+2601','云朵',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(23,'rain','下雨','自然','U+2614','下雨',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(24,'snow','雪花','自然','U+2744','雪花',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(25,'flower','花朵','自然','U+1F338','花朵',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(26,'rocket','火箭','科技','U+1F680','火箭',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(27,'plane','飞机','交通','U+2708','飞机',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(28,'car','汽车','交通','U+1F697','汽车',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(29,'train','火车','交通','U+1F682','火车',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(30,'music','音乐','娱乐','U+1F3B5','音符',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(31,'game','游戏','娱乐','U+1F3AE','游戏手柄',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(32,'movie','电影','娱乐','U+1F3A5','电影放映机',3,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(33,'book','书本','学习','U+1F4DA','书本',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(34,'graduation','毕业','学习','U+1F393','毕业帽',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(35,'heart_break','心碎','情感','U+1F494','破碎的心',1,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(36,'kiss','飞吻','情感','U+1F618','飞吻表情',2,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(37,'wink','眨眼','表情','U+1F609','眨眼表情',9,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(38,'tired','疲惫','表情','U+1F62B','疲惫表情',10,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(39,'sleepy','困倦','表情','U+1F634','困倦表情',11,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(40,'sick','生病','表情','U+1F912','生病表情',12,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(41,'money','金钱','符号','U+1F4B0','钞票',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(42,'gem','钻石','符号','U+1F48E','钻石',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(43,'truck','卡车','交通','U+1F69A','卡车',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(44,'bike','自行车','交通','U+1F6B2','自行车',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(45,'food','食物','物品','U+1F34E','苹果',6,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(46,'pizza','披萨','物品','U+1F355','披萨',7,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(47,'hamburger','汉堡','物品','U+1F354','汉堡包',8,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(48,'beer','啤酒','物品','U+1F37A','啤酒杯',9,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(49,'wine','红酒','物品','U+1F377','红酒杯',10,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(50,'sports','运动','娱乐','U+26BD','足球',4,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(51,'trophy','奖杯','娱乐','U+1F3C6','奖杯',5,1,'2026-05-10 17:08:45','2026-05-10 17:08:45'),(52,'medal','奖牌','娱乐','U+1F3C5','奖牌',6,1,'2026-05-10 17:08:45','2026-05-10 17:08:45');
/*!40000 ALTER TABLE `emoji` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `exchange_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `exchange_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feature_items`
--

DROP TABLE IF EXISTS `feature_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feature_items` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '子项ID',
  `feature_id` int NOT NULL COMMENT '所属功能ID',
  `item_name` varchar(100) NOT NULL COMMENT '子项名称',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_feature_id` (`feature_id`),
  CONSTRAINT `feature_items_ibfk_1` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能子项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feature_items`
--

LOCK TABLES `feature_items` WRITE;
/*!40000 ALTER TABLE `feature_items` DISABLE KEYS */;
INSERT INTO `feature_items` (`id`, `feature_id`, `item_name`, `sort_order`, `created_at`) VALUES (1,1,'热门景点推荐',1,'2026-08-04 22:37:06'),(2,1,'季节性旅行指南',2,'2026-08-04 22:37:06'),(3,1,'安全出行提示',3,'2026-08-04 22:37:06'),(4,1,'签证办理攻略',4,'2026-08-04 22:37:06'),(5,2,'酒店比价预订',1,'2026-08-04 22:37:06'),(6,2,'特价机票查询',2,'2026-08-04 22:37:06'),(7,2,'景点门票购买',3,'2026-08-04 22:37:06'),(8,2,'行程套餐定制',4,'2026-08-04 22:37:06'),(9,3,'本地美食推荐',1,'2026-08-04 22:37:06'),(10,3,'餐厅评价排行',2,'2026-08-04 22:37:06'),(11,3,'特色菜谱收藏',3,'2026-08-04 22:37:06'),(12,3,'美食路线规划',4,'2026-08-04 22:37:06'),(13,4,'旅行笔记发布',1,'2026-08-04 22:37:06'),(14,4,'论坛话题讨论',2,'2026-08-04 22:37:06'),(15,4,'私信即时沟通',3,'2026-08-04 22:37:06'),(16,4,'游记点赞收藏',4,'2026-08-04 22:37:06'),(17,5,'旅行装备选购',1,'2026-08-04 22:37:06'),(18,5,'纪念品特卖',2,'2026-08-04 22:37:06'),(19,5,'土特产直购',3,'2026-08-04 22:37:06'),(20,5,'限时优惠活动',4,'2026-08-04 22:37:06'),(21,6,'热门目的地排行',1,'2026-08-04 22:37:06'),(22,6,'价格趋势预测',2,'2026-08-04 22:37:06'),(23,6,'个性化推荐',3,'2026-08-04 22:37:06'),(24,6,'旅行数据分析',4,'2026-08-04 22:37:06');
/*!40000 ALTER TABLE `feature_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `features`
--

DROP TABLE IF EXISTS `features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `features` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '功能ID',
  `icon` varchar(255) NOT NULL COMMENT '图标(SVG图片地址)',
  `title` varchar(50) NOT NULL COMMENT '功能标题',
  `description` varchar(200) NOT NULL COMMENT '功能描述',
  `bg_color` varchar(100) NOT NULL COMMENT '背景渐变色',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='核心功能表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `features`
--

LOCK TABLES `features` WRITE;
/*!40000 ALTER TABLE `features` DISABLE KEYS */;
INSERT INTO `features` (`id`, `icon`, `title`, `description`, `bg_color`, `sort_order`, `created_at`, `updated_at`) VALUES (1,'','资讯中心','提供实时、全面的旅游资讯与攻略','linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)',1,'2026-08-04 22:37:05','2026-08-04 23:02:54'),(2,'/icons/hotel.svg','预订服务','一站式预订酒店、机票与门票','linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)',2,'2026-08-04 22:37:05','2026-08-04 22:37:05'),(3,'/icons/food.svg','美食探索','发现地道美食，品味地方特色','linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%)',3,'2026-08-04 22:37:05','2026-08-04 22:37:05'),(4,'/icons/chat.svg','社区互动','与旅友交流分享，记录旅途点滴','linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%)',4,'2026-08-04 22:37:05','2026-08-04 22:37:05'),(5,'/icons/package.svg','商品商城','精选旅行好物，让出行更轻松','linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%)',5,'2026-08-04 22:37:05','2026-08-04 22:37:05'),(6,'/icons/chart.svg','数据洞察','智能分析旅行趋势与用户偏好','linear-gradient(135deg, #ffe4e6 0%, #fecdd3 100%)',6,'2026-08-04 22:37:05','2026-08-04 22:37:05');
/*!40000 ALTER TABLE `features` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `file_info` DISABLE KEYS */;
INSERT INTO `file_info` (`id`, `file_name`, `original_name`, `file_path`, `file_type`, `file_size`, `chunk_index`, `total_chunks`, `file_md5`, `status`, `create_time`) VALUES (1,'avatar1.jpg','avatar1.jpg','/uploads/avatar1.jpg','image/jpeg',1024000,0,1,'e10adc3949ba59abbe56e057f20f883e',1,'2026-02-14 17:21:56'),(2,'avatar2.jpg','avatar2.jpg','/uploads/avatar2.jpg','image/jpeg',1234567,0,1,'c81e728d9d4c2f636f067f89cc14862c',1,'2026-02-14 17:21:56'),(3,'avatar3.jpg','avatar3.jpg','/uploads/avatar3.jpg','image/jpeg',987654,0,1,'a87ff679a2f3e71d9181a67b7542122c',1,'2026-02-14 17:21:56'),(4,'attraction1.jpg','attraction1.jpg','/uploads/attraction1.jpg','image/jpeg',2048000,0,1,'eccbc87e4b5ce2fe28308fd9f2a7baf3',1,'2026-02-14 17:21:56'),(5,'attraction2.jpg','attraction2.jpg','/uploads/attraction2.jpg','image/jpeg',2560000,0,1,'c4ca4238a0b923820dcc509a6f75849b',1,'2026-02-14 17:21:56'),(6,'hotel1.jpg','hotel1.jpg','/uploads/hotel1.jpg','image/jpeg',1800000,0,1,'c81e728d9d4c2f636f067f89cc14862c',1,'2026-02-14 17:21:56'),(7,'hotel2.jpg','hotel2.jpg','/uploads/hotel2.jpg','image/jpeg',1900000,0,1,'eccbc87e4b5ce2fe28308fd9f2a7baf3',1,'2026-02-14 17:21:56'),(8,'food1.jpg','food1.jpg','/uploads/food1.jpg','image/jpeg',1500000,0,1,'a87ff679a2f3e71d9181a67b7542122c',1,'2026-02-14 17:21:56'),(9,'food2.jpg','food2.jpg','/uploads/food2.jpg','image/jpeg',1600000,0,1,'c4ca4238a0b923820dcc509a6f75849b',1,'2026-02-14 17:21:56'),(10,'product1.jpg','product1.jpg','/uploads/product1.jpg','image/jpeg',1700000,0,1,'c81e728d9d4c2f636f067f89cc14862c',1,'2026-02-14 17:21:56');
/*!40000 ALTER TABLE `file_info` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='美食表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food`
--

LOCK TABLES `food` WRITE;
/*!40000 ALTER TABLE `food` DISABLE KEYS */;
INSERT INTO `food` (`id`, `name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `cuisine_type`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`, `create_time`, `update_time`, `commodity`) VALUES (3,'北京烤鸭','北京','北京','北京市东城区前门大街30号','北京传统名菜，外酥里嫩，香气四溢。','http://localhost:8080/upload/ed071f0c-f397-4e79-984e-81b2abc8df9c.png','https://example.com/food1_1.jpg,https://example.com/food1_2.jpg',268.00,'京菜',5,1200,800,400,'传统,烤鸭,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(4,'上海小笼包','上海','上海','上海市黄浦区豫园路85号','上海传统点心，皮薄馅大，汤汁丰富。','http://localhost:8080/upload/396217d8-909d-4f79-9176-38711f8d185d.png','https://example.com/food2_1.jpg,https://example.com/food2_2.jpg',38.00,'沪菜',5,1000,700,350,'传统,点心,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(5,'杭州西湖醋鱼','杭州','浙江','浙江省杭州市西湖区楼外楼','杭州传统名菜，鱼肉鲜嫩，酸甜可口。','http://localhost:8080/upload/e171c78c-dd8b-44b9-88bf-925d3a4836bd.png','https://example.com/food3_1.jpg,https://example.com/food3_2.jpg',88.00,'浙菜',4,800,500,280,'传统,西湖,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(6,'四川火锅','成都','四川','四川省成都市锦江区春熙路','四川特色美食，麻辣鲜香，回味无穷。','http://localhost:8080/upload/fba4e820-9073-49b8-809f-847ade920057.png','https://example.com/food4_1.jpg,https://example.com/food4_2.jpg',128.00,'川菜',5,1500,1000,500,'麻辣,火锅,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(7,'广东早茶','广州','广东','广东省广州市越秀区上下九','广东传统饮食文化，品种丰富，精致美味。','http://localhost:8080/upload/08081220-c913-4e7e-846b-29293669a0ec.png','https://example.com/food5_1.jpg,https://example.com/food5_2.jpg',68.00,'粤菜',5,900,600,320,'传统,点心,早茶',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(8,'云南过桥米线','昆明','云南','云南省昆明市五华区正义路','云南特色美食，汤鲜料足，风味独特。','https://example.com/food6.jpg','https://example.com/food6_1.jpg,https://example.com/food6_2.jpg',45.00,'滇菜',4,700,400,250,'特色,米线,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(9,'西安肉夹馍','西安','陕西','陕西省西安市莲湖区回民街','西安传统小吃，馍香肉嫩，肥而不腻。','','https://example.com/food7_1.jpg,https://example.com/food7_2.jpg',17.00,'陕菜',5,1100,800,380,'传统,小吃,必吃',1,'2026-02-14 17:21:56','2026-06-21 21:06:36','0'),(10,'湖南臭豆腐','长沙','湖南','湖南省长沙市芙蓉区黄兴南路','湖南特色小吃，闻起来臭，吃起来香。','https://example.com/food8.jpg','https://example.com/food8_1.jpg,https://example.com/food8_2.jpg',10.00,'湘菜',4,600,300,200,'特色,小吃,臭豆腐',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(11,'福建沙县小吃','福州','福建','福建省福州市鼓楼区八一七路','福建传统小吃，品种丰富，价格实惠。','https://example.com/food9.jpg','https://example.com/food9_1.jpg,https://example.com/food9_2.jpg',25.00,'闽菜',4,500,250,180,'小吃,实惠,传统',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0'),(12,'新疆大盘鸡','乌鲁木齐','新疆','新疆维吾尔自治区乌鲁木齐市天山区','新疆特色美食，鸡肉鲜嫩，土豆软糯，辣味十足。','https://example.com/food10.jpg','https://example.com/food10_1.jpg,https://example.com/food10_2.jpg',98.00,'新疆菜',5,850,550,300,'特色,大盘鸡,必吃',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','0');
/*!40000 ALTER TABLE `food` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='好友表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend`
--

LOCK TABLES `friend` WRITE;
/*!40000 ALTER TABLE `friend` DISABLE KEYS */;
INSERT INTO `friend` (`id`, `user_id`, `friend_id`, `remark`, `status`, `create_time`, `update_time`) VALUES (16,15,19,NULL,1,'2026-05-08 23:35:23','2026-05-08 23:35:23'),(17,19,15,NULL,1,'2026-05-08 23:42:48','2026-05-08 23:42:48'),(19,21,15,NULL,1,'2026-05-09 23:04:52','2026-05-09 23:04:52'),(20,15,21,NULL,1,'2026-05-09 23:06:11','2026-05-09 23:06:11'),(21,22,15,NULL,1,'2026-05-12 12:55:26','2026-05-12 12:55:26'),(22,15,22,'老相好',1,'2026-05-12 12:55:43','2026-05-12 12:55:43'),(23,23,15,NULL,1,'2026-06-05 21:46:18','2026-06-05 21:46:18'),(24,15,23,NULL,1,'2026-06-05 21:46:24','2026-06-05 21:46:24');
/*!40000 ALTER TABLE `friend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_album`
--

DROP TABLE IF EXISTS `group_album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_album` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '照片ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `user_id` bigint NOT NULL COMMENT '上传者ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `thumbnail_url` varchar(500) DEFAULT NULL COMMENT '缩略图URL',
  `description` varchar(200) DEFAULT NULL COMMENT '照片描述',
  `location` varchar(200) DEFAULT NULL COMMENT '拍摄地点',
  `taken_at` datetime DEFAULT NULL COMMENT '拍摄时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群组相册表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_album`
--

LOCK TABLES `group_album` WRITE;
/*!40000 ALTER TABLE `group_album` DISABLE KEYS */;
INSERT INTO `group_album` (`id`, `group_id`, `user_id`, `image_url`, `thumbnail_url`, `description`, `location`, `taken_at`, `created_at`, `deleted`) VALUES (1,1,101,'https://img.example.com/album/lijiang1.jpg','https://img.example.com/album/thumb/lijiang1.jpg','丽江古城全景','丽江古城','2026-07-01 08:00:00','2026-07-01 20:00:00',0),(2,1,102,'https://img.example.com/album/yulong1.jpg','https://img.example.com/album/thumb/yulong1.jpg','玉龙雪山日照金山','玉龙雪山','2026-07-02 06:50:00','2026-07-02 07:00:00',0),(3,1,103,'https://img.example.com/album/songzanlin1.jpg','https://img.example.com/album/thumb/songzanlin1.jpg','松赞林寺','香格里拉','2026-07-03 10:00:00','2026-07-03 10:30:00',0),(4,2,102,'https://img.example.com/album/zheduo1.jpg','https://img.example.com/album/thumb/zheduo1.jpg','折多山垭口','折多山','2026-07-10 11:00:00','2026-07-10 12:00:00',0),(5,2,105,'https://img.example.com/album/xinduqiao1.jpg','https://img.example.com/album/thumb/xinduqiao1.jpg','新都桥秋景','新都桥','2026-07-11 17:30:00','2026-07-11 18:00:00',0),(6,3,103,'https://img.example.com/album/sanya1.jpg','https://img.example.com/album/thumb/sanya1.jpg','三亚湾日落','三亚湾','2026-07-05 18:00:00','2026-07-05 19:00:00',0),(7,4,104,'https://img.example.com/album/huimin1.jpg','https://img.example.com/album/thumb/huimin1.jpg','回民街美食','回民街','2026-07-08 12:00:00','2026-07-08 13:00:00',0),(8,5,105,'https://img.example.com/album/lijiangriver1.jpg','https://img.example.com/album/thumb/lijiangriver1.jpg','漓江山水','漓江','2026-07-15 09:30:00','2026-07-15 10:00:00',0),(9,6,106,'https://img.example.com/album/tianchi1.jpg','https://img.example.com/album/thumb/tianchi1.jpg','天山天池','天山天池','2026-07-20 15:00:00','2026-07-20 16:00:00',0),(10,8,108,'https://img.example.com/album/budala1.jpg','https://img.example.com/album/thumb/budala1.jpg','布达拉宫','布达拉宫','2026-08-01 10:00:00','2026-08-01 10:30:00',0);
/*!40000 ALTER TABLE `group_album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_bill`
--

DROP TABLE IF EXISTS `group_bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账单ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `payer_id` bigint NOT NULL COMMENT '付款人ID',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `category` varchar(50) DEFAULT NULL COMMENT '分类：餐饮/住宿/交通/门票/其他',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `bill_date` date DEFAULT NULL COMMENT '账单日期',
  `is_settled` tinyint(1) DEFAULT '0' COMMENT '是否已结算',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_payer_id` (`payer_id`),
  KEY `idx_bill_date` (`bill_date`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AA账单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_bill`
--

LOCK TABLES `group_bill` WRITE;
/*!40000 ALTER TABLE `group_bill` DISABLE KEYS */;
INSERT INTO `group_bill` (`id`, `group_id`, `payer_id`, `amount`, `category`, `description`, `bill_date`, `is_settled`, `created_at`) VALUES (1,1,101,1200.00,'住宿','丽江古城客栈3晚','2026-07-01',0,'2026-07-01 20:00:00'),(2,1,102,450.00,'交通','玉龙雪山包车费','2026-07-02',0,'2026-07-02 08:00:00'),(3,1,103,580.00,'餐饮','丽江特色餐饮','2026-07-01',0,'2026-07-01 19:30:00'),(4,2,102,1800.00,'交通','租车费用','2026-07-10',1,'2026-07-10 09:00:00'),(5,2,105,320.00,'餐饮','康定晚餐','2026-07-10',0,'2026-07-10 18:30:00'),(6,3,103,2800.00,'住宿','三亚民宿','2026-07-05',0,'2026-07-05 15:00:00'),(7,4,104,150.00,'门票','兵马俑门票','2026-07-08',1,'2026-07-08 10:00:00'),(8,5,105,800.00,'门票','漓江漂流','2026-07-15',0,'2026-07-15 09:00:00'),(9,6,106,2500.00,'住宿','乌鲁木齐酒店','2026-07-20',0,'2026-07-20 14:00:00'),(10,8,108,350.00,'其他','布达拉宫门票+讲解','2026-08-01',1,'2026-08-01 09:30:00');
/*!40000 ALTER TABLE `group_bill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_bill_split`
--

DROP TABLE IF EXISTS `group_bill_split`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_bill_split` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分摊ID',
  `bill_id` bigint NOT NULL COMMENT '账单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `share_amount` decimal(10,2) NOT NULL COMMENT '分摊金额',
  `is_paid` tinyint(1) DEFAULT '0' COMMENT '是否已还',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_user` (`bill_id`,`user_id`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账单分摊明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_bill_split`
--

LOCK TABLES `group_bill_split` WRITE;
/*!40000 ALTER TABLE `group_bill_split` DISABLE KEYS */;
INSERT INTO `group_bill_split` (`id`, `bill_id`, `user_id`, `share_amount`, `is_paid`) VALUES (1,1,101,400.00,1),(2,1,102,400.00,0),(3,1,103,400.00,0),(4,2,101,150.00,1),(5,2,102,150.00,1),(6,2,103,150.00,0),(7,4,102,600.00,1),(8,4,105,600.00,1),(9,4,106,600.00,0),(10,7,104,150.00,1);
/*!40000 ALTER TABLE `group_bill_split` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群聊消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_chat_message`
--

LOCK TABLES `group_chat_message` WRITE;
/*!40000 ALTER TABLE `group_chat_message` DISABLE KEYS */;
INSERT INTO `group_chat_message` (`id`, `group_id`, `sender_id`, `sender_name`, `sender_avatar`, `content`, `message_type`, `create_time`) VALUES (1,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','权威的','text','2026-05-10 12:05:15'),(2,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg',' 达瓦达瓦','text','2026-05-10 12:06:30'),(3,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','哇大王的','text','2026-05-10 12:06:35'),(4,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','http://localhost:8080/upload/45c4666e-37b7-4cf2-963e-bf0d4ece3042.jpeg','emoji','2026-05-10 16:59:30'),(6,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','http://localhost:8080/upload/e67ba6aa-a0ee-4cf8-81f7-6a63693e966e.jpg','image','2026-05-12 12:50:27'),(7,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','🚀','emoji','2026-05-12 13:33:12'),(8,3,22,'yun xiao','http://localhost:8080/upload/39adb7e5-e022-459d-862b-57da1244f8e3.png','😭','emoji','2026-05-12 14:04:54'),(9,4,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','http://localhost:8080/upload/38869291-f574-4d77-936c-081c2129e2a2.png','image','2026-05-12 14:06:12'),(10,3,19,'李小明','http://localhost:8080/upload/d333bdc6-a972-403b-8c2f-1e5da3b73edb.jpeg','📞','emoji','2026-05-13 20:54:42'),(11,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','🍎','emoji','2026-05-13 20:55:01'),(12,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','http://localhost:8080/upload/20c8ba8f-5cc7-47f4-ad7d-002aad3a5ac2.png','image','2026-05-13 20:55:08'),(13,3,21,'王涛','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','http://localhost:8080/upload/3988b87e-6425-4419-94ff-cb516ee41beb.webp','emoji','2026-05-13 20:55:41'),(14,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','✉','emoji','2026-05-14 22:35:13'),(15,3,19,'李小明','http://localhost:8080/upload/d333bdc6-a972-403b-8c2f-1e5da3b73edb.jpeg','🚀','emoji','2026-05-14 22:35:18'),(16,3,15,'爱吃薯条的小懒猪','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','欢迎欢迎','text','2026-06-05 21:48:18'),(17,3,23,'王伟明','http://localhost:8080/upload/c373a4e3-5f63-4585-921b-dd548be5f1d8.png','😘','emoji','2026-06-05 21:48:28');
/*!40000 ALTER TABLE `group_chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_comment`
--

DROP TABLE IF EXISTS `group_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '评论者ID',
  `parent_id` bigint DEFAULT '0' COMMENT '回复的评论ID，0表示一级评论',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT '@回复的用户ID',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_comment`
--

LOCK TABLES `group_comment` WRITE;
/*!40000 ALTER TABLE `group_comment` DISABLE KEYS */;
INSERT INTO `group_comment` (`id`, `post_id`, `user_id`, `parent_id`, `reply_to_user_id`, `content`, `created_at`, `deleted`) VALUES (1,1,102,0,NULL,'风景真好！期待明天的行程','2026-07-01 14:40:00',0),(2,1,103,1,102,'是啊，而且天气也给力','2026-07-01 14:45:00',0),(3,2,101,0,NULL,'太美了，能亲眼看到真是幸运','2026-07-02 07:10:00',0),(4,2,103,0,NULL,'求攻略，我也想去！','2026-07-02 08:00:00',0),(5,4,105,0,NULL,'看着就震撼，下次带上我','2026-07-10 11:30:00',0),(6,4,102,5,105,'没问题，等我们一起组团','2026-07-10 11:40:00',0),(7,7,101,0,NULL,'看到肉夹馍流口水了','2026-07-08 12:30:00',0),(8,7,104,7,101,'哈哈，等你来了带你去吃最好吃的','2026-07-08 12:35:00',0),(9,10,109,0,NULL,'神圣的地方，我也要去朝圣','2026-08-01 10:20:00',0),(10,10,108,9,109,'值得一来，心灵都被净化了','2026-08-01 10:30:00',0);
/*!40000 ALTER TABLE `group_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_invite`
--

DROP TABLE IF EXISTS `group_invite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_invite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '邀请ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `inviter_id` bigint DEFAULT NULL COMMENT '邀请人ID',
  `invitee_id` bigint NOT NULL COMMENT '被邀请人/申请人ID',
  `type` tinyint DEFAULT '1' COMMENT '1-邀请 2-申请加入',
  `status` tinyint DEFAULT '0' COMMENT '0-待处理 1-同意 2-拒绝',
  `expire_at` datetime DEFAULT NULL COMMENT '过期时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handled_at` datetime DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_invitee_id` (`invitee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邀请/申请记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_invite`
--

LOCK TABLES `group_invite` WRITE;
/*!40000 ALTER TABLE `group_invite` DISABLE KEYS */;
INSERT INTO `group_invite` (`id`, `group_id`, `inviter_id`, `invitee_id`, `type`, `status`, `expire_at`, `created_at`, `handled_at`) VALUES (1,1,101,201,1,1,'2026-06-30 23:59:59','2026-06-15 10:00:00','2026-06-15 14:00:00'),(2,1,102,202,1,0,'2026-07-01 23:59:59','2026-06-16 09:00:00',NULL),(3,2,102,203,2,1,'2026-06-25 23:59:59','2026-06-16 10:00:00','2026-06-16 16:00:00'),(4,2,105,204,1,2,'2026-06-26 23:59:59','2026-06-17 08:00:00','2026-06-17 09:00:00'),(5,3,103,205,1,1,'2026-06-30 23:59:59','2026-06-17 12:00:00','2026-06-17 18:00:00'),(6,4,104,206,2,0,'2026-06-30 23:59:59','2026-06-18 09:00:00',NULL),(7,5,105,207,1,1,'2026-07-01 23:59:59','2026-06-19 15:00:00','2026-06-19 20:00:00'),(8,6,106,208,1,0,'2026-07-05 23:59:59','2026-06-20 08:00:00',NULL),(9,8,108,209,2,1,'2026-07-10 23:59:59','2026-06-21 14:00:00','2026-06-22 10:00:00'),(10,9,107,210,1,2,'2026-06-20 23:59:59','2026-06-10 11:00:00','2026-06-10 15:00:00'),(11,8,NULL,19,2,0,'2026-07-08 22:34:45',NULL,NULL),(12,6,NULL,19,2,0,'2026-07-08 23:04:25',NULL,NULL);
/*!40000 ALTER TABLE `group_invite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_location_history`
--

DROP TABLE IF EXISTS `group_location_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_location_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '位置记录ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `latitude` decimal(10,7) NOT NULL COMMENT '纬度',
  `longitude` decimal(10,7) NOT NULL COMMENT '经度',
  `accuracy` int DEFAULT NULL COMMENT '精度(米)',
  `recorded_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_group_user_time` (`group_id`,`user_id`,`recorded_at`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='位置历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_location_history`
--

LOCK TABLES `group_location_history` WRITE;
/*!40000 ALTER TABLE `group_location_history` DISABLE KEYS */;
INSERT INTO `group_location_history` (`id`, `group_id`, `user_id`, `latitude`, `longitude`, `accuracy`, `recorded_at`) VALUES (1,1,101,26.8718000,100.2310000,10,'2026-07-01 08:00:00'),(2,1,102,27.0972000,100.2220000,15,'2026-07-02 06:50:00'),(3,1,103,27.8256000,99.7092000,10,'2026-07-03 10:00:00'),(4,2,102,30.0311000,101.9600000,20,'2026-07-10 11:00:00'),(5,2,105,30.0567000,101.5100000,10,'2026-07-11 17:30:00'),(6,3,103,18.2631000,109.5070000,10,'2026-07-05 18:00:00'),(7,4,104,34.3894000,109.2730000,15,'2026-07-08 12:00:00'),(8,5,105,25.0672000,110.4450000,10,'2026-07-15 09:30:00'),(9,6,106,43.8879000,88.0960000,20,'2026-07-20 15:00:00'),(10,8,108,29.6561000,91.1160000,10,'2026-08-01 10:00:00');
/*!40000 ALTER TABLE `group_location_history` ENABLE KEYS */;
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
  `status` tinyint DEFAULT '1' COMMENT '1-正常 2-禁言 3-已退出',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `exit_time` datetime DEFAULT NULL COMMENT '退出时间',
  `nickname` varchar(50) DEFAULT NULL COMMENT '群内昵称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_user` (`group_id`,`user_id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_member`
--

LOCK TABLES `group_member` WRITE;
/*!40000 ALTER TABLE `group_member` DISABLE KEYS */;
INSERT INTO `group_member` (`id`, `group_id`, `user_id`, `role`, `status`, `join_time`, `exit_time`, `nickname`) VALUES (1,1,15,'admin',1,NULL,NULL,NULL),(2,2,19,'member',1,NULL,NULL,NULL),(3,2,21,'member',1,NULL,NULL,NULL),(4,2,15,'admin',1,NULL,NULL,NULL),(5,3,19,'member',1,NULL,NULL,NULL),(6,3,21,'member',1,NULL,NULL,NULL),(7,3,15,'admin',1,NULL,NULL,NULL),(8,3,22,'member',1,NULL,NULL,NULL),(9,4,19,'member',1,NULL,NULL,NULL),(10,4,21,'member',1,NULL,NULL,NULL),(11,4,22,'member',1,NULL,NULL,NULL),(12,4,15,'admin',1,NULL,NULL,NULL),(13,3,23,'member',1,NULL,NULL,NULL),(14,1,101,'owner',1,'2026-06-15 10:05:00',NULL,'领队小张'),(15,1,102,'admin',1,'2026-06-15 10:10:00',NULL,'摄影小王'),(16,1,103,'member',1,'2026-06-16 08:00:00',NULL,'背包客小李'),(17,1,104,'member',1,'2026-06-16 09:30:00',NULL,'美食达人'),(18,2,102,'owner',1,'2026-06-16 09:05:00',NULL,'川西老司机'),(19,2,105,'member',1,'2026-06-17 14:00:00',NULL,'户外爱好者'),(20,3,103,'owner',1,'2026-06-17 11:35:00',NULL,'环岛骑手'),(21,4,104,'owner',1,'2026-06-18 08:20:00',NULL,'西安通'),(22,5,105,'owner',1,'2026-06-19 14:05:00',NULL,'桂林山水甲'),(23,6,106,'owner',1,'2026-06-20 07:35:00',NULL,'新疆老驴');
/*!40000 ALTER TABLE `group_member` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `group_message_read` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_message_read` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_notification`
--

DROP TABLE IF EXISTS `group_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `group_id` bigint DEFAULT NULL COMMENT '群组ID',
  `type` varchar(30) DEFAULT NULL COMMENT '通知类型：JOIN_APPLY/NEW_POST/COMMENT/MENTION/BILL_UPDATE',
  `title` varchar(100) DEFAULT NULL COMMENT '通知标题',
  `content` varchar(500) DEFAULT NULL COMMENT '通知内容',
  `target_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_notification`
--

LOCK TABLES `group_notification` WRITE;
/*!40000 ALTER TABLE `group_notification` DISABLE KEYS */;
INSERT INTO `group_notification` (`id`, `receiver_id`, `group_id`, `type`, `title`, `content`, `target_id`, `is_read`, `created_at`) VALUES (1,102,1,'NEW_POST','新动态提醒','领队小张发布了新动态：第一天到达丽江古城',1,1,'2026-07-01 14:31:00'),(2,103,1,'COMMENT','评论提醒','摄影小王评论了你的动态',1,0,'2026-07-01 14:41:00'),(3,101,1,'MENTION','@提醒','背包客小李在评论中提到了你',3,1,'2026-07-02 07:15:00'),(4,105,2,'JOIN_APPLY','入群申请','用户204申请加入川西自驾游群',4,0,'2026-06-17 08:05:00'),(5,102,2,'BILL_UPDATE','账单更新','租车费用已结算，请查看详情',4,1,'2026-07-10 09:05:00'),(6,101,1,'COMMENT','评论提醒','美食达人评论了你的动态',2,1,'2026-07-02 07:11:00'),(7,104,4,'NEW_POST','新动态提醒','西安通发布了回民街美食攻略',7,0,'2026-07-08 12:05:00'),(8,108,8,'NEW_POST','新动态提醒','西藏朝圣之旅发布了布达拉宫照片',10,1,'2026-08-01 10:05:00'),(9,109,8,'COMMENT','评论提醒','用户109评论了你的动态',10,0,'2026-08-01 10:25:00'),(10,106,6,'JOIN_APPLY','入群申请','用户208申请加入新疆大环线群',8,1,'2026-06-20 08:05:00'),(11,108,8,'JOIN_APPLY','入群申请','有用户申请加入群组',11,0,NULL),(12,106,6,'JOIN_APPLY','入群申请','有用户申请加入群组',12,0,NULL);
/*!40000 ALTER TABLE `group_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_poll`
--

DROP TABLE IF EXISTS `group_poll`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_poll` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '投票ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `title` varchar(200) NOT NULL COMMENT '投票标题',
  `description` text COMMENT '投票描述',
  `type` tinyint DEFAULT '1' COMMENT '1-单选 2-多选 3-评分',
  `options` json NOT NULL COMMENT '选项JSON：[{"id":1,"text":"选项A","image":"url"}]',
  `end_time` datetime DEFAULT NULL COMMENT '截止时间',
  `is_anonymous` tinyint(1) DEFAULT '0' COMMENT '是否匿名',
  `status` tinyint DEFAULT '1' COMMENT '1-进行中 2-已结束',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投票表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_poll`
--

LOCK TABLES `group_poll` WRITE;
/*!40000 ALTER TABLE `group_poll` DISABLE KEYS */;
INSERT INTO `group_poll` (`id`, `group_id`, `creator_id`, `title`, `description`, `type`, `options`, `end_time`, `is_anonymous`, `status`, `created_at`) VALUES (1,1,101,'明天去哪里玩？','投票决定明天的行程',1,'[{\"id\": 1, \"text\": \"虎跳峡\"}, {\"id\": 2, \"text\": \"香格里拉\"}, {\"id\": 3, \"text\": \"丽江古城\"}]','2026-07-01 20:00:00',0,2,'2026-07-01 10:00:00'),(2,1,102,'晚餐吃什么？','大家投票选择今晚的餐厅',1,'[{\"id\": 1, \"text\": \"云南过桥米线\"}, {\"id\": 2, \"text\": \"腊排骨火锅\"}, {\"id\": 3, \"text\": \"菌子火锅\"}]','2026-07-01 18:00:00',1,2,'2026-07-01 12:00:00'),(3,2,102,'是否要去稻城亚丁？','稻城亚丁路途较远，大家表决',1,'[{\"id\": 1, \"text\": \"去\"}, {\"id\": 2, \"text\": \"不去\"}]','2026-07-11 08:00:00',0,1,'2026-07-10 10:00:00'),(4,2,105,'行程安排投票','大家选择最想去的景点',2,'[{\"id\": 1, \"text\": \"稻城亚丁\"}, {\"id\": 2, \"text\": \"海螺沟\"}, {\"id\": 3, \"text\": \"四姑娘山\"}]','2026-07-12 20:00:00',0,1,'2026-07-10 12:00:00'),(5,3,103,'骑行路线选择','环岛骑行路线投票',1,'[{\"id\": 1, \"text\": \"东线\"}, {\"id\": 2, \"text\": \"西线\"}, {\"id\": 3, \"text\": \"中线\"}]','2026-07-06 08:00:00',0,1,'2026-07-05 09:00:00'),(6,4,104,'明天行程投票','兵马俑和华山二选一',1,'[{\"id\": 1, \"text\": \"兵马俑\"}, {\"id\": 2, \"text\": \"华山\"}]','2026-07-08 20:00:00',1,2,'2026-07-08 10:00:00'),(7,5,105,'漂流还是徒步？','明天活动投票',1,'[{\"id\": 1, \"text\": \"漓江漂流\"}, {\"id\": 2, \"text\": \"徒步老寨山\"}]','2026-07-15 08:00:00',0,1,'2026-07-14 16:00:00'),(8,6,106,'住宿选择','乌鲁木齐酒店选择',1,'[{\"id\": 1, \"text\": \"国际酒店\"}, {\"id\": 2, \"text\": \"特色民宿\"}, {\"id\": 3, \"text\": \"青年旅社\"}]','2026-07-20 12:00:00',0,2,'2026-07-19 20:00:00'),(9,8,108,'行程投票','纳木错和羊卓雍错选择',2,'[{\"id\": 1, \"text\": \"纳木错\"}, {\"id\": 2, \"text\": \"羊卓雍错\"}]','2026-08-02 20:00:00',1,1,'2026-08-01 12:00:00'),(10,9,107,'西湖游玩方式','大家选怎么游览西湖',1,'[{\"id\": 1, \"text\": \"步行\"}, {\"id\": 2, \"text\": \"骑行\"}, {\"id\": 3, \"text\": \"游船\"}]','2026-06-25 08:00:00',0,2,'2026-06-24 15:00:00');
/*!40000 ALTER TABLE `group_poll` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_poll_vote`
--

DROP TABLE IF EXISTS `group_poll_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_poll_vote` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '投票记录ID',
  `poll_id` bigint NOT NULL COMMENT '投票ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `option_id` int NOT NULL COMMENT '选项ID',
  `score` int DEFAULT NULL COMMENT '评分制时的分数',
  `voted_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_poll_user` (`poll_id`,`user_id`),
  KEY `idx_poll_id` (`poll_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投票记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_poll_vote`
--

LOCK TABLES `group_poll_vote` WRITE;
/*!40000 ALTER TABLE `group_poll_vote` DISABLE KEYS */;
INSERT INTO `group_poll_vote` (`id`, `poll_id`, `user_id`, `option_id`, `score`, `voted_at`) VALUES (1,1,101,2,NULL,'2026-07-01 10:30:00'),(2,1,102,2,NULL,'2026-07-01 11:00:00'),(3,1,103,1,NULL,'2026-07-01 11:30:00'),(4,2,101,2,NULL,'2026-07-01 12:30:00'),(5,2,103,1,NULL,'2026-07-01 13:00:00'),(6,3,102,1,NULL,'2026-07-10 10:30:00'),(7,3,105,1,NULL,'2026-07-10 11:00:00'),(8,4,102,1,NULL,'2026-07-10 12:30:00'),(9,6,104,1,NULL,'2026-07-08 10:30:00'),(10,10,107,2,NULL,'2026-06-24 15:30:00');
/*!40000 ALTER TABLE `group_poll_vote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_post`
--

DROP TABLE IF EXISTS `group_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `user_id` bigint NOT NULL COMMENT '发布者ID',
  `content` text COMMENT '动态内容',
  `images` json DEFAULT NULL COMMENT '图片URL数组',
  `location` varchar(200) DEFAULT NULL COMMENT '位置信息',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群组动态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_post`
--

LOCK TABLES `group_post` WRITE;
/*!40000 ALTER TABLE `group_post` DISABLE KEYS */;
INSERT INTO `group_post` (`id`, `group_id`, `user_id`, `content`, `images`, `location`, `like_count`, `comment_count`, `created_at`, `updated_at`, `deleted`) VALUES (1,1,101,'第一天到达丽江古城，天气超好！大家集合啦','[\"https://img.example.com/post/lijiang1.jpg\", \"https://img.example.com/post/lijiang2.jpg\"]','丽江古城',12,3,'2026-07-01 14:30:00','2026-07-01 14:30:00',0),(2,1,102,'玉龙雪山的日照金山，太震撼了！','[\"https://img.example.com/post/yulong1.jpg\", \"https://img.example.com/post/yulong2.jpg\"]','玉龙雪山',25,5,'2026-07-02 06:50:00','2026-07-02 06:50:00',0),(3,1,103,'在香格里拉松赞林寺，感受藏传佛教文化','[\"https://img.example.com/post/songzanlin1.jpg\"]','香格里拉',8,2,'2026-07-03 10:20:00','2026-07-03 10:20:00',0),(4,2,102,'折多山垭口打卡，海拔4298米，有点高反但风景绝美','[\"https://img.example.com/post/zheduoshan1.jpg\", \"https://img.example.com/post/zheduoshan2.jpg\", \"https://img.example.com/post/zheduoshan3.jpg\"]','折多山',18,4,'2026-07-10 11:00:00','2026-07-10 11:00:00',0),(5,2,105,'新都桥的秋天，摄影家的天堂','[\"https://img.example.com/post/xinduqiao1.jpg\"]','新都桥',10,1,'2026-07-11 17:30:00','2026-07-11 17:30:00',0),(6,3,103,'三亚湾日落，开始环岛骑行第一天！','[\"https://img.example.com/post/sanya1.jpg\"]','三亚湾',15,3,'2026-07-05 18:20:00','2026-07-05 18:20:00',0),(7,4,104,'回民街的美食攻略，从早吃到晚不重样','[\"https://img.example.com/post/huimin1.jpg\", \"https://img.example.com/post/huimin2.jpg\"]','回民街',20,6,'2026-07-08 12:00:00','2026-07-08 12:00:00',0),(8,5,105,'漓江竹筏漂流，山水如画','[\"https://img.example.com/post/lijiang3.jpg\"]','漓江',9,2,'2026-07-15 09:30:00','2026-07-15 09:30:00',0),(9,6,106,'天山天池，蓝宝石般的高山湖泊','[\"https://img.example.com/post/tianchi1.jpg\", \"https://img.example.com/post/tianchi2.jpg\"]','天山天池',22,4,'2026-07-20 15:00:00','2026-07-20 15:00:00',0),(10,8,108,'布达拉宫，终于见到你了！','[\"https://img.example.com/post/budala1.jpg\"]','布达拉宫',30,7,'2026-08-01 10:00:00','2026-08-01 10:00:00',0);
/*!40000 ALTER TABLE `group_post` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `group_travel` DISABLE KEYS */;
INSERT INTO `group_travel` (`id`, `creator_id`, `name`, `description`, `destination`, `departure_time`, `return_time`, `max_members`, `current_members`, `price`, `images`, `status`, `create_time`, `update_time`) VALUES (1,1,'北京5日经典游','游览北京主要景点，包括故宫、长城、颐和园等。','北京','2024-05-01 08:00:00','2024-05-05 18:00:00',30,25,2800.00,'http://localhost:8080/upload/ffb22709-362c-44b5-b336-ccc706141a9d.png',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'上海+杭州4日游','游览上海外滩、杭州西湖等著名景点。','上海,杭州','2024-05-02 09:00:00','2024-05-05 17:00:00',25,20,2200.00,'[\"https://example.com/group2_1.jpg\", \"https://example.com/group2_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'成都+九寨沟5日游','游览成都大熊猫基地、九寨沟等景点。','成都,九寨沟','2024-05-03 07:30:00','2024-05-07 19:00:00',20,15,3500.00,'[\"https://example.com/group3_1.jpg\", \"https://example.com/group3_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'三亚3日度假游','享受三亚阳光沙滩，游览天涯海角等景点。','三亚','2024-05-04 10:00:00','2024-05-06 16:00:00',35,30,2900.00,'[\"https://example.com/group4_1.jpg\", \"https://example.com/group4_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'西安4日文化游','游览兵马俑、华清池等历史文化景点。','西安','2024-05-05 08:30:00','2024-05-08 18:30:00',28,22,2500.00,'[\"https://example.com/group5_1.jpg\", \"https://example.com/group5_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'云南大理+丽江6日游','游览大理古城、丽江古城等景点。','大理,丽江','2024-05-06 07:00:00','2024-05-11 20:00:00',22,18,3800.00,'[\"https://example.com/group6_1.jpg\", \"https://example.com/group6_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'厦门+鼓浪屿3日游','游览厦门市区、鼓浪屿等景点。','厦门,鼓浪屿','2024-05-07 09:30:00','2024-05-09 17:30:00',30,26,1900.00,'[\"https://example.com/group7_1.jpg\", \"https://example.com/group7_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,8,'张家界4日游','游览张家界国家森林公园、天门山等景点。','张家界','2024-05-08 08:00:00','2024-05-11 18:00:00',26,20,3200.00,'[\"https://example.com/group8_1.jpg\", \"https://example.com/group8_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,9,'广州+深圳4日游','游览广州塔、深圳世界之窗等景点。','广州,深圳','2024-05-09 09:00:00','2024-05-12 17:00:00',24,19,2100.00,'[\"https://example.com/group9_1.jpg\", \"https://example.com/group9_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,10,'黄山3日游','游览黄山风景区，欣赏奇松怪石云海。','黄山','2024-05-10 07:30:00','2024-05-12 18:30:00',20,16,2800.00,'[\"https://example.com/group10_1.jpg\", \"https://example.com/group10_2.jpg\"]',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
/*!40000 ALTER TABLE `group_travel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_travel_diary`
--

DROP TABLE IF EXISTS `group_travel_diary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_travel_diary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日记ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `title` varchar(200) DEFAULT NULL COMMENT '日记标题（自动生成：{destination}之旅）',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `content_json` json NOT NULL COMMENT '完整的日记内容JSON',
  `status` tinyint DEFAULT '1' COMMENT '1-草稿 2-已发布 3-已导出',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='旅行日记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_travel_diary`
--

LOCK TABLES `group_travel_diary` WRITE;
/*!40000 ALTER TABLE `group_travel_diary` DISABLE KEYS */;
INSERT INTO `group_travel_diary` (`id`, `group_id`, `title`, `cover_url`, `content_json`, `status`, `created_at`, `updated_at`) VALUES (1,1,'云南之旅 - 走进秘境','https://img.example.com/diary/yunnan.jpg','{\"days\": [{\"day\": 1, \"text\": \"第一天感受了丽江的慢生活\", \"photos\": [\"photo1.jpg\", \"photo2.jpg\"], \"activities\": [\"丽江古城\", \"束河古镇\"]}, {\"day\": 2, \"text\": \"雪山之巅的震撼\", \"photos\": [\"photo3.jpg\"], \"activities\": [\"玉龙雪山\"]}]}',2,'2026-07-08 14:00:00','2026-07-10 20:00:00'),(2,2,'川西大环线自驾','https://img.example.com/diary/chuanxi.jpg','{\"days\": [{\"day\": 1, \"text\": \"318国道风景绝美\", \"activities\": [\"成都-康定\"]}, {\"day\": 2, \"text\": \"摄影家的天堂\", \"activities\": [\"康定-新都桥\"]}]}',1,'2026-07-20 10:00:00','2026-07-20 10:00:00'),(3,3,'海南环岛骑行记','https://img.example.com/diary/hainan.jpg','{\"days\": [{\"day\": 1, \"text\": \"迎着海风骑行\", \"activities\": [\"三亚-陵水\"]}, {\"day\": 2, \"text\": \"最美沿海公路\", \"activities\": [\"陵水-万宁\"]}]}',1,'2026-07-13 09:00:00','2026-07-13 09:00:00'),(4,4,'西安美食之旅','https://img.example.com/diary/xian.jpg','{\"days\": [{\"day\": 1, \"text\": \"文化+美食完美结合\", \"activities\": [\"兵马俑\", \"回民街\"]}]}',2,'2026-07-11 16:00:00','2026-07-12 08:00:00'),(5,5,'桂林山水甲天下','https://img.example.com/diary/guilin.jpg','{\"days\": [{\"day\": 1, \"text\": \"人在画中游\", \"activities\": [\"漓江漂流\"]}, {\"day\": 2, \"text\": \"悠闲阳朔时光\", \"activities\": [\"阳朔西街\", \"遇龙河\"]}]}',1,'2026-07-22 10:00:00','2026-07-22 10:00:00'),(6,6,'大美新疆游记','https://img.example.com/diary/xinjiang.jpg','{\"days\": [{\"day\": 1, \"text\": \"天池如蓝宝石\", \"activities\": [\"乌鲁木齐\", \"天山天池\"]}, {\"day\": 2, \"text\": \"人间仙境\", \"activities\": [\"喀纳斯\"]}]}',1,'2026-08-01 12:00:00','2026-08-01 12:00:00'),(7,7,'厦门文艺小清新之旅','https://img.example.com/diary/xiamen.jpg','{\"days\": [{\"day\": 1, \"text\": \"钢琴之岛的浪漫\", \"activities\": [\"鼓浪屿\"]}, {\"day\": 2, \"text\": \"海边的文艺\", \"activities\": [\"曾厝垵\", \"环岛路\"]}]}',3,'2026-06-28 20:00:00','2026-07-01 10:00:00'),(8,8,'西藏朝圣日记','https://img.example.com/diary/tibet.jpg','{\"days\": [{\"day\": 1, \"text\": \"雄伟的宫殿\", \"activities\": [\"布达拉宫\"]}, {\"day\": 2, \"text\": \"信仰的力量\", \"activities\": [\"大昭寺\"]}]}',1,'2026-08-10 15:00:00','2026-08-10 15:00:00'),(9,9,'杭州西湖漫游','https://img.example.com/diary/hangzhou.jpg','{\"days\": [{\"day\": 1, \"text\": \"烟雨西湖美如画\", \"activities\": [\"西湖\", \"断桥\"]}, {\"day\": 2, \"text\": \"寺庙的宁静\", \"activities\": [\"灵隐寺\"]}]}',2,'2026-06-29 10:00:00','2026-06-30 08:00:00'),(10,10,'张家界探险之旅','https://img.example.com/diary/zhangjiajie.jpg','{\"days\": [{\"day\": 1, \"text\": \"阿凡达取景地\", \"activities\": [\"张家界国家森林公园\"]}, {\"day\": 2, \"text\": \"惊险的天门洞\", \"activities\": [\"天门山\"]}]}',3,'2026-05-25 14:00:00','2026-06-01 09:00:00');
/*!40000 ALTER TABLE `group_travel_diary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_trip_plan`
--

DROP TABLE IF EXISTS `group_trip_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_trip_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '行程ID',
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `day_index` int NOT NULL COMMENT '第几天',
  `date` date DEFAULT NULL COMMENT '具体日期',
  `time_slot` varchar(50) DEFAULT NULL COMMENT '时间段：上午/下午/晚上',
  `activity` varchar(200) NOT NULL COMMENT '活动名称',
  `location` varchar(200) DEFAULT NULL COMMENT '地点',
  `transport` varchar(50) DEFAULT NULL COMMENT '交通方式',
  `notes` text COMMENT '备注',
  `created_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_day_index` (`day_index`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='行程计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_trip_plan`
--

LOCK TABLES `group_trip_plan` WRITE;
/*!40000 ALTER TABLE `group_trip_plan` DISABLE KEYS */;
INSERT INTO `group_trip_plan` (`id`, `group_id`, `day_index`, `date`, `time_slot`, `activity`, `location`, `transport`, `notes`, `created_by`, `created_at`, `updated_at`) VALUES (1,1,1,'2026-07-01','上午','丽江古城游览','丽江古城','步行','大水车、四方街、木府',101,'2026-06-20 10:00:00','2026-06-20 10:00:00'),(2,1,1,'2026-07-01','下午','束河古镇','束河古镇','打车','比丽江古城更安静',101,'2026-06-20 10:05:00','2026-06-20 10:05:00'),(3,1,2,'2026-07-02','全天','玉龙雪山一日游','玉龙雪山','包车','需提前订票，带好氧气瓶',101,'2026-06-20 10:10:00','2026-06-20 10:10:00'),(4,2,1,'2026-07-10','上午','成都出发到康定','成都-康定','自驾','约4小时车程',102,'2026-06-25 09:00:00','2026-06-25 09:00:00'),(5,2,2,'2026-07-11','全天','康定-新都桥','康定-新都桥','自驾','沿途风景绝美',102,'2026-06-25 09:05:00','2026-06-25 09:05:00'),(6,3,1,'2026-07-05','上午','三亚出发骑行','三亚-陵水','骑行','约80公里',103,'2026-06-28 08:00:00','2026-06-28 08:00:00'),(7,4,1,'2026-07-08','上午','兵马俑参观','兵马俑博物馆','地铁+公交','建议请讲解',104,'2026-06-29 12:00:00','2026-06-29 12:00:00'),(8,4,1,'2026-07-08','下午','回民街美食','回民街','步行','各种陕西小吃',104,'2026-06-29 12:05:00','2026-06-29 12:05:00'),(9,5,1,'2026-07-15','全天','漓江漂流','桂林-阳朔','竹筏','全程约4小时',105,'2026-06-30 14:00:00','2026-06-30 14:00:00'),(10,6,1,'2026-07-20','上午','天山天池','天山天池','包车','带好防晒和外套',106,'2026-07-01 11:00:00','2026-07-01 11:00:00');
/*!40000 ALTER TABLE `group_trip_plan` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `home_recommendations` DISABLE KEYS */;
INSERT INTO `home_recommendations` (`id`, `uuid`, `name`, `english_name`, `alias_name`, `category_id`, `category_name`, `tags`, `country`, `province`, `city`, `district`, `address`, `longitude`, `latitude`, `google_map_url`, `baidu_map_url`, `transportation`, `recommendation_reason`, `highlights`, `best_travel_time`, `visit_duration`, `cover_image_url`, `banner_image_url`, `video_url`, `image_urls`, `ticket_price_description`, `min_price`, `max_price`, `price_currency`, `is_free`, `opening_hours_weekday`, `opening_hours_weekend`, `opening_hours_description`, `contact_phone`, `official_website`, `social_media`, `view_count`, `like_count`, `share_count`, `comment_count`, `average_rating`, `rating_count`, `weight`, `is_hot`, `is_new`, `is_top`, `status`, `season`, `festival`, `tips`, `suitable_crowd`, `facility_info`, `seo_title`, `seo_keywords`, `seo_description`, `content`, `summary`, `source`, `author`, `created_at`, `updated_at`, `published_at`, `expired_at`) VALUES (1,'082d6297-1d5b-11f1-833c-c4c6e607efc7','故宫博物院','The Palace Museum',NULL,2,'历史文化','世界遗产,拍照圣地,亲子游,研学','中国','北京市','北京市','东城区','景山前街4号',NULL,NULL,NULL,NULL,'地铁1号线天安门东站A口出，步行约10分钟','穿越六百年时光，感受明清两代的皇家气派。故宫不仅是中国古代建筑的精髓，更是中华文明的瑰宝。','三大殿太和殿中和殿保和殿;珍宝馆;钟表馆;故宫角楼;景山俯瞰全景','春秋两季（4月-5月，9月-10月）','半天至一天','http://localhost:8080/upload/9c3efbcb-5afe-4b4f-8bdf-46c58d485660.png',NULL,NULL,'[\"https://example.com/images/gugong_1.jpg\", \"https://example.com/images/gugong_2.jpg\"]','旺季60元/人，淡季40元/人，珍宝馆和钟表馆需另购票',40.00,60.00,'CNY',0,'08:30-17:00','08:30-17:00','每周一闭馆（法定节假日除外）','010-85007421','https://www.dpm.org.cn',NULL,5,0,0,0,0.00,0,100,1,0,0,1,'autumn',NULL,'建议提前网上预约购票，携带身份证入场。避开节假日高峰期，可租讲解器或请导游。','历史文化爱好者,亲子家庭,摄影爱好者',NULL,NULL,NULL,NULL,'故宫博物院建立于1925年，是在明朝、清朝两代皇宫及其收藏的基础上建立起来的中国综合性博物馆。','穿越六百年时光，探寻紫禁城的秘密',NULL,NULL,'2026-03-11 15:00:24','2026-03-11 15:00:24',NULL,NULL),(2,'082daddd-1d5b-11f1-833c-c4c6e607efc7','成都大熊猫繁育研究基地','Chengdu Research Base of Giant Panda Breeding',NULL,1,'自然风光','熊猫,亲子,动物保护,网红','中国','四川省','成都市','成华区','熊猫大道1375号',NULL,NULL,NULL,NULL,'地铁3号线熊猫大道站A口出，转乘景区直通车','近距离观察国宝大熊猫的日常生活，看软萌的熊猫宝宝嬉戏玩耍，是成都必打卡的亲子胜地。','月亮产房看熊猫幼崽;太阳产房;成年熊猫别墅;小熊猫活动区;熊猫博物馆','全年（最佳是春秋季，上午熊猫最活跃）','3-4小时','https://example.com/images/panda_cover.jpg',NULL,NULL,'[\"https://example.com/images/panda_1.jpg\", \"https://example.com/images/panda_2.jpg\"]','成人票55元，学生票27元',27.00,55.00,'CNY',0,'07:30-18:00','07:30-18:00','全年开放','028-83510033','http://www.panda.org.cn',NULL,0,0,0,0,0.00,0,95,1,1,0,1,'spring',NULL,'尽量上午8-10点前往，此时熊猫最活跃，是喂食时间。穿舒适的鞋子，园区较大。','亲子家庭,动物爱好者,自然爱好者',NULL,NULL,NULL,NULL,'成都大熊猫繁育研究基地是一个专门保护和繁育大熊猫、小熊猫等珍稀动物的机构，也是全球最大的大熊猫人工繁育机构。','近距离感受国宝熊猫的萌力暴击',NULL,NULL,'2026-03-11 15:00:24','2026-03-11 15:00:24',NULL,NULL),(3,'4689cc18-1d60-11f1-833c-c4c6e607efc7','上海迪士尼度假区','Shanghai Disney Resort',NULL,3,'主题乐园','亲子,情侣,童话,烟花秀,网红打卡','中国','上海市','上海市','浦东新区','川沙镇黄赵路310号',NULL,NULL,NULL,NULL,'地铁11号线迪士尼站直达','中国大陆首座迪士尼主题乐园，带你进入梦幻童话世界。七大主题园区各具特色，烟花秀震撼人心。','奇幻童话城堡;创极速光轮;加勒比海盗;飞跃地平线;烟花灯光秀;花车巡游','春秋季（避开寒暑假和节假日）','1-2天','http://localhost:8080/upload/d646cecc-0184-47f5-b0e1-b3c9d0c28d91.png',NULL,'https://videos.travel.com/disney_intro.mp4','[\"https://images.travel.com/disney_1.jpg\", \"https://images.travel.com/disney_2.jpg\", \"https://images.travel.com/disney_3.jpg\"]','平日399元，高峰日599元，特别高峰日699元',399.00,699.00,'CNY',0,'09:00-20:30','09:00-21:00','开放时间随季节和活动调整，以官网为准','400-180-0000','https://www.shanghaidisneyresort.com','{\"weibo\": \"上海迪士尼度假区\", \"douyin\": \"shanghaidisney\", \"wechat\": \"ShangHaiDisneyResort\"}',125896,45678,28900,12345,4.80,32000,100,1,0,0,1,'all','圣诞节,春节,万圣节','提前下载官方APP查看排队时间；可购买早享卡提前入园；烟花秀建议提前占位；可带未开封零食和水。','亲子家庭,情侣,年轻人,迪士尼粉丝','有停车场（收费）；有母婴室；提供轮椅和婴儿车租赁；有快速通行证服务','上海迪士尼度假区_门票_攻略_游玩项目','上海迪士尼,迪士尼攻略,主题乐园','上海迪士尼度假区完整攻略：门票价格、开放时间、必玩项目、交通指南、游玩贴士','上海迪士尼度假区是中国内地首座迪士尼主题乐园，于2016年6月正式开园。园区分为米奇大街、奇想花园、探险岛、宝藏湾、明日世界、梦幻世界、玩具总动员七大主题园区。','点亮心中奇梦，重返童年时光','官方合作','迪士尼小编','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-01 01:00:00',NULL),(4,'468a1d1e-1d60-11f1-833c-c4c6e607efc7','张家界国家森林公园','Zhangjiajie National Forest Park',NULL,1,'自然风光','自然遗产,登山,摄影,阿凡达,玻璃桥','中国','湖南省','张家界市','武陵源区','金鞭路279号',NULL,NULL,NULL,NULL,'张家界中心汽车站乘景区专线车','电影《阿凡达》取景地，三千奇峰拔地而起，云雾缭绕宛如仙境。是世界自然遗产、世界地质公园。','袁家界哈利路亚山;天子山;金鞭溪;十里画廊;百龙天梯;天门山玻璃栈道','4月-6月，9月-11月（雨后初晴云海最美）','2-3天','https://images.travel.com/zhangjiajie_cover.jpg',NULL,'https://videos.travel.com/zhangjiajie_intro.mp4','[\"https://images.travel.com/zhangjiajie_1.jpg\", \"https://images.travel.com/zhangjiajie_2.jpg\", \"https://images.travel.com/zhangjiajie_3.jpg\"]','森林公园门票228元（4日内有效），天门山门票278元',228.00,278.00,'CNY',0,'07:00-18:00','07:00-18:00','景区旺季延长开放时间','0744-5712333','https://www.zjjpark.com','{\"douyin\": \"zjjtour\", \"wechat\": \"张家界旅游\"}',87651,32100,15400,8760,4.70,18700,98,1,0,0,1,'autumn','国庆节','山路较多，穿舒适运动鞋；景区内环保车免费；建议住一晚武陵源区；注意防猴。','登山爱好者,摄影爱好者,自然探索者','有停车场；山上有简餐售卖点；部分路段有索道和电梯；游客中心可寄存行李','张家界国家森林公园旅游攻略_门票_住宿_交通','张家界,武陵源,阿凡达,玻璃桥','张家界国家森林公园完整攻略：门票价格、开放时间、最佳路线、住宿推荐','张家界国家森林公园位于湖南省张家界市武陵源区，是中国第一个国家森林公园，1992年被列入世界自然遗产名录。公园以独特的石英砂岩峰林地貌著称，有“三千奇峰，八百秀水”之美誉。','探秘阿凡达的悬浮山仙境','小编踩点','张旅行','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-15 02:30:00',NULL),(5,'468a2045-1d60-11f1-833c-c4c6e607efc7','回民街','Muslim Quarter',NULL,4,'美食探店','美食,小吃,夜市,历史文化,清真','中国','陕西省','西安市','莲湖区','北院门街道',NULL,NULL,NULL,NULL,'地铁2号线钟楼站下车步行5分钟','西安最著名的小吃街，三百多种美食让你从街头吃到巷尾。青石板路两旁是明清风格建筑，烟火气十足。','牛羊肉泡馍;肉夹馍;凉皮;甑糕;烤肉串;柿子饼;酸梅汤','全年（晚上最热闹）','2-3小时','https://images.travel.com/huiminjie_cover.jpg',NULL,NULL,'[\"https://images.travel.com/huiminjie_1.jpg\", \"https://images.travel.com/huiminjie_2.jpg\"]','丰俭由人，人均消费约50-100元',0.00,0.00,'CNY',0,'全天开放','全天开放','商户一般10:00-23:00营业',NULL,NULL,'{\"douyin\": \"xianfood\"}',45670,18760,8900,5430,4.50,8900,85,1,1,0,1,'all',NULL,'选择人多的店一般不会踩雷；可以每家少吃一点多尝几种；注意清真饮食禁忌。','美食爱好者,吃货,夜猫子','公共厕所在街区两端；部分老店只收现金；有座位的小吃店较少','西安回民街美食攻略_必吃小吃_夜市推荐','回民街,西安美食,小吃街','西安回民街必吃美食清单+避坑指南+营业时间','回民街是西安最具代表性的美食文化街区，距今已有上千年历史。这里聚集了300多种西北特色小吃，是了解西安饮食文化的绝佳去处。','从街头吃到巷尾的西安美食天堂','用户投稿','吃货小马','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-20 10:00:00',NULL),(6,'468a242c-1d60-11f1-833c-c4c6e607efc7','洱海','Erhai Lake',NULL,1,'自然风光','洱海,骑行,拍照,民宿,发呆,治愈系','中国','云南省','大理白族自治州','大理市','洱海周边',NULL,NULL,NULL,NULL,'大理古城租电动车或包车环湖','苍山洱海，风花雪月。环湖120公里，每一处都是风景。在双廊看日落，在喜洲吃粑粑，在才村等日出。','环海东路;双廊古镇;喜洲古镇;海舌公园;小普陀;理想邦圣托里尼','3月-5月（樱花季），10月-12月（海鸥季）','2-3天','https://images.travel.com/erhai_cover.jpg',NULL,'https://videos.travel.com/erhai_intro.mp4','[\"https://images.travel.com/erhai_1.jpg\", \"https://images.travel.com/erhai_2.jpg\", \"https://images.travel.com/erhai_3.jpg\"]','免费开放（部分景点单独收费）',0.00,0.00,'CNY',1,'全天开放','全天开放','全天开放',NULL,NULL,'{\"wechat\": \"大理旅游\", \"xiaohongshu\": \"dali\"}',67890,28900,12300,7890,4.85,15600,96,1,0,0,1,'spring','春节','注意防晒；租车记得砍价；海东适合看日落，海西适合看日出；保护环境不乱扔垃圾。','情侣,闺蜜,摄影爱好者,文艺青年','环湖有多个停车场；沿途有公共厕所；民宿集中；有自行车租赁点','大理洱海旅游攻略_环湖路线_民宿推荐','洱海,大理旅游,环洱海','洱海环湖攻略：最佳路线+拍照打卡点+住宿推荐','洱海是云南省第二大高原湖泊，因形状像耳朵而得名。环湖一周约120公里，沿途有双廊、喜洲等多个古镇，是感受大理慢生活的理想之地。','苍山洱海，许你一场风花雪月','小编踩点','风花雪月','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-10 06:20:00',NULL),(7,'468a2778-1d60-11f1-833c-c4c6e607efc7','哈尔滨冰雪大世界','Harbin Ice and Snow World',NULL,3,'主题乐园','冰雪,冬季,冰雕,灯光秀,亲子','中国','黑龙江省','哈尔滨市','松北区','松北大道与太阳大道交叉口',NULL,NULL,NULL,NULL,'公交29路、42路冰雪大世界站','世界最大的冰雪主题乐园，用冰雪打造出一个梦幻的童话王国。白天晶莹剔透，夜晚流光溢彩。','冰雕建筑群;超级冰滑梯;冰雪城堡;灯光秀;雪圈;冰雪演出','12月下旬-次年2月底（春节前后最热闹）','4-6小时','https://images.travel.com/ice_cover.jpg',NULL,NULL,'[\"https://images.travel.com/ice_1.jpg\", \"https://images.travel.com/ice_2.jpg\", \"https://images.travel.com/ice_3.jpg\"]','标准票330元，优惠票200元',200.00,330.00,'CNY',0,'11:00-22:00','11:00-22:30','视天气情况调整','0451-58561111','http://www.hrbicesnow.com','{\"weibo\": \"哈尔滨冰雪大世界\"}',54320,23450,8900,4560,4.65,7800,92,1,0,0,1,'winter','元旦,春节','做好保暖，贴暖宝宝；手机贴暖宝宝防止冻关机；下午3点后去能看日夜两种景色。','亲子家庭,年轻人,南方游客','有停车场；园区内有餐饮区；有取暖屋；提供热饮售卖','哈尔滨冰雪大世界攻略_门票_开放时间','冰雪大世界,哈尔滨旅游,冰雕','哈尔滨冰雪大世界游玩攻略：门票价格、保暖贴士、必玩项目','哈尔滨冰雪大世界始创于1999年，每年冬季在松花江畔举办，用松花江的天然冰雪打造出规模宏大的冰雪建筑群，是哈尔滨国际冰雪节的重要组成部分。','闯入零下三十度的冰雪童话世界','官方合作','冰城小编','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-05 03:15:00',NULL),(8,'468a2ae4-1d60-11f1-833c-c4c6e607efc7','宽窄巷子','Kuanzhai Alley',NULL,4,'历史文化','古街,茶馆,美食,拍照,文艺','中国','四川省','成都市','青羊区','长顺上街127号',NULL,NULL,NULL,NULL,'地铁4号线宽窄巷子站','最能体现成都慢生活的地方，由宽巷子、窄巷子、井巷子三条平行古街组成。盖碗茶、采耳、变脸，巴适得很。','宽巷子(老成都);窄巷子(慢生活);井巷子(新时尚);茶馆看变脸;掏耳朵;三大炮','全年（春秋最舒适）','3-4小时','https://images.travel.com/kuanzhai_cover.jpg',NULL,NULL,'[\"https://images.travel.com/kuanzhai_1.jpg\", \"https://images.travel.com/kuanzhai_2.jpg\"]','免费进入（消费项目另算）',0.00,0.00,'CNY',1,'全天开放','全天开放','商户一般10:00-22:00营业','028-86253168',NULL,'{\"xiaohongshu\": \"成都宽窄巷子\"}',45678,19870,7600,4320,4.55,6500,80,1,1,0,1,'all',NULL,'不要只在主街逛，旁边的小巷子更有味道；采耳记得先问价；茶馆选有表演的。','文艺青年,美食爱好者,游客','有公共厕所；多处休息座椅；游客中心可寄存行李','成都宽窄巷子攻略_美食_茶馆','宽窄巷子,成都旅游,盖碗茶','宽窄巷子逛吃指南+采耳体验+表演推荐','宽窄巷子是成都遗留下来的较成规模的清朝古街道，由宽巷子、窄巷子、井巷子平行排列组成，全为青黛砖瓦的仿古四合院落，是成都三大历史文化保护区之一。','体验成都慢生活，泡茶馆看变脸','用户投稿','成都小甜甜','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-28 08:45:00',NULL),(9,'468a2dcc-1d60-11f1-833c-c4c6e607efc7','西湖','West Lake','',1,'自然风光','世界遗产,免费,游船,断桥,雷峰塔,十景','中国','浙江省','杭州市','西湖区','西湖风景区',NULL,NULL,NULL,NULL,'地铁1号线龙翔桥站/凤起路站','欲把西湖比西子，淡妆浓抹总相宜。西湖的美，在晴雨雾雪，在春夏秋冬，在每一个不经意的回眸。','苏堤春晓;断桥残雪;雷峰夕照;三潭印月;曲院风荷;灵隐寺','3月-5月（桃花），9月-11月（桂花）','1天','http://localhost:8080/upload/8faa7cf1-c7ed-408a-a514-5b847749be7b.png',NULL,'https://videos.travel.com/westlake_intro.mp4','[\"https://images.travel.com/westlake_1.jpg\", \"https://images.travel.com/westlake_2.jpg\", \"https://images.travel.com/westlake_3.jpg\"]','免费开放（部分景点单独收费）',0.00,0.00,'CNY',1,'全天开放','全天开放','全天开放','0571-87179570','http://www.hzwestlake.com','{\"wechat\": \"杭州西湖\"}',156781,67890,23400,12345,4.90,45200,99,1,0,1,1,'all','清明节,中秋节','建议骑行或步行；旺季人非常多；可以坐一次手摇船；看日落去长桥公园。','所有人群','环湖有多处停车场；公共厕所密集；有游船码头；多处游客中心','杭州西湖旅游攻略_景点_游船_美食','西湖,杭州旅游,断桥','西湖一日游攻略：最佳路线+必看景点+美食推荐','西湖位于杭州市区西部，是中国主要的观赏性淡水湖泊之一，也是首批国家重点风景名胜区。2011年被列入世界遗产名录，以其秀丽的湖光山色和众多的名胜古迹而闻名中外。','欲把西湖比西子，淡妆浓抹总相宜','小编踩点','西湖诗人','2026-03-11 15:37:56','2026-08-01 15:51:48','2024-03-15 01:30:00',NULL),(10,'468a2f7b-1d60-11f1-833c-c4c6e607efc7','长隆野生动物世界','Chimelong Safari Park',NULL,1,'主题乐园','亲子,动物,自驾,科普,表演','中国','广东省','广州市','番禺区','大石镇105国道大石段593号',NULL,NULL,NULL,NULL,'地铁3号线汉溪长隆站E出口','亚洲最大的野生动物主题公园，可以自驾进入与动物零距离接触。从澳洲的袋鼠到非洲的长颈鹿，带你环游动物世界。','自驾区;熊猫乐园;考拉馆;白虎山;长颈鹿广场;动物表演;小火车','10月-4月（避开夏季高温）','1天','https://images.travel.com/chimelong_cover.jpg',NULL,NULL,'[\"https://images.travel.com/chimelong_1.jpg\", \"https://images.travel.com/chimelong_2.jpg\"]','成人票350元，儿童/老人票245元',245.00,350.00,'CNY',0,'09:30-18:00','09:30-18:00','节假日延长开放','400-883-0083','https://www.chimelong.com','{\"weibo\": \"广州长隆\"}',98761,43210,18700,8900,4.75,23400,97,1,0,0,1,'winter','儿童节,国庆节','建议北门进，先坐小火车；自驾需另购车票；不能自带食物喂动物；提前查表演时间。','亲子家庭,动物爱好者','大型停车场；园区有餐厅；婴儿车租赁；母婴室；轮椅租赁','广州长隆野生动物世界攻略_门票_自驾','长隆,野生动物园,亲子游','长隆野生动物世界游玩攻略：路线规划+表演时间+注意事项','广州长隆野生动物世界隶属长隆旅游度假区，以大规模野生动物种群放养和自驾车观赏为特色，拥有20000余只珍奇动物，是亚洲最大的野生动物主题公园。','穿越五大洲，和动物做朋友','官方合作','长隆小编','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-10 02:00:00',NULL),(11,'468a761b-1d60-11f1-833c-c4c6e607efc7','洪崖洞','Hongya Cave',NULL,5,'休闲度假','夜景,网红,吊脚楼,火锅,拍照','中国','重庆市','重庆市','渝中区','嘉陵江滨江路88号',NULL,NULL,NULL,NULL,'地铁1号线小什字站','现实版的《千与千寻》汤屋，11层吊脚楼依山而建，夜晚灯火辉煌。在这里吃火锅看江景，感受8D魔幻城市的魅力。','洪崖洞夜景;民俗街;长江索道;千厮门大桥;洞子火锅;巴渝剧场','全年（晚上7点后灯亮）','2-3小时','http://localhost:8080/upload/51178d8d-baf0-459e-9ea4-fcb8f59061c9.png',NULL,'https://videos.travel.com/hongyadong_intro.mp4','[\"https://images.travel.com/hongyadong_1.jpg\", \"https://images.travel.com/hongyadong_2.jpg\", \"https://images.travel.com/hongyadong_3.jpg\"]','免费开放',0.00,0.00,'CNY',1,'全天开放','全天开放','商户10:00-23:00',NULL,NULL,'{\"douyin\": \"重庆洪崖洞\", \"xiaohongshu\": \"chongqing\"}',234564,98760,34500,15670,4.70,28900,100,1,1,0,1,'all',NULL,'拍照最佳点在千厮门大桥或对面江滩；不要轻信带路收费的人；电梯排队久，建议走楼梯。','年轻人,摄影爱好者,吃货','有停车场；多部电梯；公共厕所；餐饮集中','重庆洪崖洞攻略_夜景_火锅','洪崖洞,重庆夜景,网红打卡','洪崖洞拍照攻略+避坑指南+火锅推荐','洪崖洞位于重庆市核心商圈解放碑沧白路，以最具巴渝传统建筑特色的“吊脚楼”风貌为主体，依山就势，沿江而建，是重庆历史文化的见证和城市精神的象征。','闯入千与千寻的奇幻世界','用户投稿','山城妹子','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-05 11:30:00',NULL),(12,'468a792f-1d60-11f1-833c-c4c6e607efc7','布达拉宫','Potala Palace',NULL,2,'历史文化','世界遗产,朝圣,藏传佛教,高原,宏伟','中国','西藏自治区','拉萨市','城关区','北京中路35号',NULL,NULL,NULL,NULL,'拉萨市内乘出租车或步行','世界上海拔最高、最宏伟的宫殿，藏传佛教圣地。红白相间的建筑屹立在红山之巅，见证了西藏的千年历史。','白宫;红宫;灵塔殿;壁画;佛像;雪城;珍宝馆','5月-10月（氧气充足，气候宜人）','3-4小时','https://images.travel.com/potala_cover.jpg',NULL,NULL,'[\"https://images.travel.com/potala_1.jpg\", \"https://images.travel.com/potala_2.jpg\"]','旺季200元，淡季100元',100.00,200.00,'CNY',0,'09:00-16:00','09:00-16:00','周一闭馆（节假日除外）','0891-6834331','http://www.potalapalace.cn','{\"wechat\": \"布达拉宫官方\"}',67891,28760,12340,5670,4.95,12300,95,1,0,0,1,'summer','藏历新年','需提前一天预约门票；注意高原反应；禁止穿短裙短裤入内；内部禁止拍照；准备零钱添香火钱。','文化爱好者,朝圣者,摄影爱好者','有停车场；需爬坡；有讲解器租赁；游客中心提供氧气瓶','布达拉宫攻略_门票_高原反应','布达拉宫,西藏旅游,朝圣','布达拉宫参观攻略：预约方式+注意事项+最佳时间','布达拉宫位于拉萨市区西北的玛布日山上，是一座宫堡式建筑群，最初是吐蕃王朝赞普松赞干布为迎娶文成公主而兴建。是历代达赖喇嘛的冬宫居所，也是西藏政教合一的统治中心。','离天堂最近的圣殿，信仰的归宿','小编踩点','高原红','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-18 03:45:00',NULL),(13,'468a7c0d-1d60-11f1-833c-c4c6e607efc7','夫子庙-秦淮风光带','Confucius Temple-Qinhuai River',NULL,2,'历史文化','夜景,游船,小吃,古都,科举','中国','江苏省','南京市','秦淮区','贡院西街53号',NULL,NULL,NULL,NULL,'地铁1号线三山街站/3号线夫子庙站','烟笼寒水月笼沙，夜泊秦淮近酒家。这里是南京最繁华的地方，十里秦淮灯火璀璨，画舫凌波，梦回金陵。','夫子庙;江南贡院;乌衣巷;秦淮河画舫;老门东;科举博物馆','3月-5月，9月-11月（夜景全年皆宜）','3-4小时','https://images.travel.com/qinhuai_cover.jpg',NULL,NULL,'[\"https://images.travel.com/qinhuai_1.jpg\", \"https://images.travel.com/qinhuai_2.jpg\"]','免费进入（画舫船票80元）',0.00,80.00,'CNY',1,'全天开放','全天开放','画舫运营至22:00','025-52202058',NULL,'{\"xiaohongshu\": \"南京夫子庙\"}',54320,24560,9800,6540,4.60,8900,82,1,1,0,1,'all','元宵节','夫子庙小吃推荐莲湖糕团店；不要在主街买特产；夜游秦淮一定要坐船；元宵节有灯会。','文化爱好者,情侣,家庭','有停车场；多处公共厕所；游客中心；游船码头','南京夫子庙攻略_秦淮河_小吃','夫子庙,秦淮河,南京旅游','夫子庙-秦淮河游玩攻略：游船体验+美食推荐','夫子庙-秦淮风光带位于南京市秦淮区，以夫子庙为核心、十里秦淮为轴线，串联起众多名胜古迹，是南京历史文化荟萃之地，也是中国最大的传统古街市。','夜泊秦淮，梦回六朝金粉地','用户投稿','金陵客','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-20 12:15:00',NULL),(14,'468a7eab-1d60-11f1-833c-c4c6e607efc7','漓江风景区','Lijiang River Scenic Area',NULL,1,'自然风光','山水,竹筏,20元人民币,摄影,喀斯特','中国','广西壮族自治区','桂林市','阳朔县','漓江景区杨堤码头',NULL,NULL,NULL,NULL,'桂林汽车站乘大巴到杨堤或兴坪','桂林山水甲天下，漓江山水甲桂林。乘竹筏顺流而下，看两岸奇峰倒影，碧水青山，人在画中游。','杨堤-兴坪竹筏;九马画山;黄布倒影(20元背景);兴坪古镇;相公山日出','4月-10月（雨后初晴烟雨漓江最美）','1天','https://images.travel.com/lijiang_cover.jpg',NULL,'https://videos.travel.com/lijiang_intro.mp4','[\"https://images.travel.com/lijiang_1.jpg\", \"https://images.travel.com/lijiang_2.jpg\", \"https://images.travel.com/lijiang_3.jpg\"]','竹筏杨堤-兴坪段约120元/人',120.00,120.00,'CNY',0,'08:00-17:30','08:00-17:30','视水位情况调整','0773-2825502','http://www.gltour.com','{\"wechat\": \"桂林旅游\"}',76540,34560,15670,8760,4.80,15600,93,1,0,0,1,'spring','五一,国庆','选择正规竹筏；穿救生衣；准备好20元纸币拍照；建议从杨堤漂到兴坪精华段。','摄影爱好者,自然爱好者,家庭','有停车场；码头有厕所；筏工服务；兴坪古镇有餐饮','桂林漓江攻略_竹筏_20元人民币','漓江,桂林山水,阳朔','漓江竹筏游攻略：最佳路线+拍照点+避坑指南','漓江发源于桂林市兴安县的猫儿山，流经桂林、阳朔，全长164公里。沿江风光秀丽，奇峰林立，构成了一幅绚丽多彩的画卷，是桂林山水的精华所在。','舟行碧波上，人在画中游','小编踩点','山水甲天下','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-08 05:20:00',NULL),(15,'468a9a1a-1d60-11f1-833c-c4c6e607efc7','崂山风景区','Mount Lao Scenic Area',NULL,1,'自然风光','道教名山,海上第一山,登山,观海,道教文化','中国','山东省','青岛市','崂山区','崂山路',NULL,NULL,NULL,NULL,'公交104路、304路崂山游客中心','泰山虽云高，不如东海崂。中国海岸线第一高峰，山海相连，道观林立。在这里既能看海又能登山访道。','巨峰顶(最高峰);太清宫;仰口海滩;九水十八潭;明霞洞;棋盘石','4月-11月（夏季避暑，秋季看叶）','1天','https://images.travel.com/laoshan_cover.jpg',NULL,NULL,'[\"https://images.travel.com/laoshan_1.jpg\", \"https://images.travel.com/laoshan_2.jpg\"]','南线票130元，北线票90元，巨峰票120元',90.00,130.00,'CNY',0,'07:00-17:00','07:00-17:00','淡季时间缩短','0532-88899000','http://www.qdlaoshan.cn','{\"wechat\": \"青岛崂山\"}',34562,15670,6700,3450,4.65,5600,78,1,0,0,1,'summer',NULL,'穿登山鞋；带够水和干粮；索道可节省体力；建议玩1-2条线路；注意天气预报。','登山爱好者,道教文化爱好者','多个停车场；索道；山上小卖部；游客中心','青岛崂山攻略_门票_登山路线','崂山,青岛旅游,道教名山','崂山一日游攻略：路线选择+交通+注意事项','崂山位于青岛市东部，濒临黄海，是中国重要的道教名山和旅游胜地。主峰巨峰海拔1132.7米，是中国大陆海岸线第一高峰，素有“海上第一名山”之称。','海上第一名山，寻仙问道处','官方合作','青岛小哥','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-02-05 01:45:00',NULL),(16,'468a9c8c-1d60-11f1-833c-c4c6e607efc7','鼓浪屿','Gulangyu Island',NULL,5,'休闲度假','世界遗产,小岛,万国建筑,钢琴,文艺,沙滩','中国','福建省','厦门市','思明区','鼓浪屿岛',NULL,NULL,NULL,NULL,'厦门轮渡码头乘船至鼓浪屿三丘田码头','万国建筑博览，钢琴之岛，猫岛。没有车马喧嚣，只有小巷蜿蜒，琴声悠扬，找个咖啡馆发呆就是最好的旅行。','日光岩;菽庄花园;皓月园;龙头路商业街;风琴博物馆;沙滩;最美转角','3月-5月，10月-12月（避开台风季）','1-2天','https://images.travel.com/gulangyu_cover.jpg',NULL,NULL,'[\"https://images.travel.com/gulangyu_1.jpg\", \"https://images.travel.com/gulangyu_2.jpg\", \"https://images.travel.com/gulangyu_3.jpg\"]','上岛免费（需购买35元船票），景点联票90元',0.00,90.00,'CNY',1,'全天开放','全天开放','轮渡5:30-0:30运营','0592-2060777','http://www.gly.cn','{\"xiaohongshu\": \"鼓浪屿\"}',89001,39870,17800,9800,4.75,18700,94,1,0,0,1,'autumn',NULL,'提前2-3天购买船票；穿舒适的鞋子；不要买路边的海鲜；建议住一晚感受清晨和夜晚。','文艺青年,情侣,家庭','码头有游客中心；岛上有观光车；公共厕所；大量民宿','厦门鼓浪屿攻略_船票_民宿','鼓浪屿,厦门旅游,文艺小岛','鼓浪屿一日游攻略：拍照打卡+美食+避坑指南','鼓浪屿位于厦门半岛西南隅，与厦门岛隔海相望，因岛西南方有一礁石，每当涨潮水涌，浪击礁石，声似擂鼓，人们称“鼓浪石”而得名。岛上气候宜人，四季如春，有“海上花园”之称。','漫步万国建筑，聆听海岛琴声','小编踩点','海岛姑娘','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-03-12 07:10:00',NULL),(17,'468a9fe7-1d60-11f1-833c-c4c6e607efc7','莫高窟','Mogao Caves',NULL,2,'历史文化','世界遗产,佛教艺术,壁画,丝路,研学','中国','甘肃省','酒泉市','敦煌市','莫高窟景区',NULL,NULL,NULL,NULL,'敦煌市区乘绿皮公交12路','沙漠中的艺术宝库，千年的佛教画廊。735个洞窟，4.5万平方米壁画，2415尊彩塑，每一眼都是与千年的对望。','九层楼;藏经洞;飞天壁画;反弹琵琶;敦煌壁画;特窟参观','5月-10月（9月最佳，葡萄熟了）','半天','https://images.travel.com/mogao_cover.jpg',NULL,'https://videos.travel.com/mogao_intro.mp4','[\"https://images.travel.com/mogao_1.jpg\", \"https://images.travel.com/mogao_2.jpg\"]','A类票238元（看8个窟+电影），B类票100元（看4个窟）',100.00,238.00,'CNY',0,'08:00-18:00','08:00-18:00','淡季缩短开放时间','0937-8869060','https://www.mogaoku.com','{\"wechat\": \"敦煌研究院\"}',56780,26780,10900,5430,4.92,14500,96,1,1,0,1,'autumn','国庆节','提前一个月预约门票；禁止拍照；建议看两部介绍电影；特窟需另外购票；带望远镜看细节。','历史文化爱好者,艺术爱好者,研学团','数字展示中心停车场；有讲解服务；书店和文创店；需存包','敦煌莫高窟攻略_门票_特窟','莫高窟,敦煌旅游,壁画','莫高窟参观攻略：票务预约+洞窟介绍+注意事项','莫高窟位于敦煌市东南25公里处鸣沙山东麓的断崖上，是中国四大石窟之一，也是世界上现存规模最宏大、保存最完好的佛教艺术宝库，1987年被列为世界文化遗产。','一眼千年，聆听丝路驼铃','官方合作','敦煌守护者','2026-03-11 15:37:56','2026-03-11 15:37:56','2024-01-25 06:30:00',NULL);
/*!40000 ALTER TABLE `home_recommendations` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` (`id`, `name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `star_level`, `rating`, `like_count`, `collect_count`, `comment_count`, `facilities`, `status`, `create_time`, `update_time`) VALUES (4,'北京王府井希尔顿酒店','北京','北京','北京市东城区王府井东街8号','位于王府井商业区中心，交通便利，设施豪华。','http://localhost:8080/upload/e7f9d92b-eec9-4c6f-94e2-6e3328898820.png','[\"https://example.com/hotel1_1.jpg\",\"https://example.com/hotel1_2.jpg\"]',1200.00,1,5,800,600,250,'1',1,'2026-02-14 17:21:56','2026-06-20 22:40:14'),(5,'上海外滩华尔道夫酒店','上海','上海','上海市黄浦区中山东一路2号','百年历史建筑，俯瞰外滩和黄浦江美景。','http://localhost:8080/upload/30bbf28a-8c06-4dcf-accf-6c0612cc3462.png','[\"https://example.com/hotel2_1.jpg\",\"https://example.com/hotel2_2.jpg\"]',1500.00,5,5,900,700,280,'免费WiFi,健身房,游泳池,餐厅,停车场,SPA',1,'2026-02-14 17:21:56','2026-06-21 12:05:54'),(6,'杭州西子湖四季酒店','杭州','浙江','浙江省杭州市西湖区龙井路5号','毗邻西湖，环境优美，中式园林设计。','http://localhost:8080/upload/56a8b22d-6066-481c-b021-8861d93377be.png','[\"https://example.com/hotel3_1.jpg\",\"https://example.com/hotel3_2.jpg\"]',1300.00,5,5,850,650,260,'免费WiFi,健身房,游泳池,餐厅,停车场,SPA',1,'2026-02-14 17:21:56','2026-06-21 15:30:52'),(7,'广州四季酒店','广州','广东','广东省广州市天河区珠江西路5号','位于珠江新城，现代化豪华酒店。','https://example.com/hotel4.jpg','[\"https://example.com/hotel4_1.jpg\", \"https://example.com/hotel4_2.jpg\"]',1100.00,5,4,750,550,230,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,'深圳福田香格里拉大酒店','深圳','广东','广东省深圳市福田区益田路4088号','位于福田中心区，交通便利，设施齐全。','https://example.com/hotel5.jpg','[\"https://example.com/hotel5_1.jpg\", \"https://example.com/hotel5_2.jpg\"]',980.00,5,4,700,500,210,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,'成都太古里博舍酒店','成都','四川','四川省成都市锦江区笔帖式街81号','位于太古里商业区，融合传统与现代设计。','https://example.com/hotel6.jpg','[\"https://example.com/hotel6_1.jpg\", \"https://example.com/hotel6_2.jpg\"]',1080.00,5,5,820,620,240,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'三亚亚特兰蒂斯酒店','三亚','海南','海南省三亚市海棠湾海棠北路36号','豪华度假酒店，有水族馆和水上乐园。','http://localhost:8080/upload/f03c3668-7889-48b6-812f-a2a661b56955.png','[\"https://example.com/hotel7_1.jpg\", \"https://example.com/hotel7_2.jpg\"]',1800.00,5,5,950,750,300,'免费WiFi,健身房,游泳池,餐厅,停车场,水上乐园',1,'2026-02-14 17:21:56','2026-03-12 22:51:58'),(11,'厦门鼓浪屿海景酒店','厦门','福建','福建省厦门市思明区鼓浪屿','岛上的精品酒店，环境清幽，海景优美。','https://example.com/hotel8.jpg','[\"https://example.com/hotel8_1.jpg\", \"https://example.com/hotel8_2.jpg\"]',680.00,4,4,600,400,180,'免费WiFi,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,'西安香格里拉大酒店','西安','陕西','陕西省西安市雁塔区科技路38号乙','位于高新区，靠近大雁塔等景点。','https://example.com/hotel9.jpg','[\"https://example.com/hotel9_1.jpg\", \"https://example.com/hotel9_2.jpg\"]',880.00,5,4,650,450,200,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(13,'南京金陵饭店','南京','江苏','江苏省南京市鼓楼区汉中路2号','南京地标性建筑，历史悠久，服务优质。','https://example.com/hotel10.jpg','[\"https://example.com/hotel10_1.jpg\", \"https://example.com/hotel10_2.jpg\"]',780.00,5,4,620,420,190,'免费WiFi,健身房,游泳池,餐厅,停车场',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_facility`
--

DROP TABLE IF EXISTS `hotel_facility`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_facility` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设施ID',
  `hotel_id` bigint NOT NULL COMMENT '酒店ID',
  `facility_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施名称',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标路径',
  `status` int DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_hotel_id` (`hotel_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='酒店设施表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_facility`
--

LOCK TABLES `hotel_facility` WRITE;
/*!40000 ALTER TABLE `hotel_facility` DISABLE KEYS */;
INSERT INTO `hotel_facility` (`id`, `hotel_id`, `facility_name`, `icon`, `status`, `create_time`, `update_time`) VALUES (1,1,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(2,1,'游泳池','swimming',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(3,1,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(4,1,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(5,1,'餐厅','restaurant',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(6,2,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(7,2,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(8,2,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(9,3,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(10,3,'游泳池','swimming',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(11,3,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(12,3,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(13,3,'餐厅','restaurant',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(14,3,'SPA','spa',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(26,7,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(27,7,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(28,8,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(29,8,'游泳池','swimming',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(30,8,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(31,8,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(32,8,'餐厅','restaurant',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(33,9,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(34,9,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(35,9,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(36,10,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(37,10,'游泳池','swimming',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(38,10,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(39,10,'餐厅','restaurant',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(40,11,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(41,11,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(42,12,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(43,12,'游泳池','swimming',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(44,12,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(45,12,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(46,13,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(47,13,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(48,13,'餐厅','restaurant',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(49,14,'免费WiFi','wifi',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(50,14,'游泳池','swimming',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(51,14,'健身房','gym',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(52,14,'停车场','parking',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(53,14,'餐厅','restaurant',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(54,14,'儿童乐园','kids',1,'2026-05-15 20:46:38','2026-05-15 20:46:38'),(66,4,'停车场',NULL,1,'2026-06-20 22:40:14','2026-06-20 22:40:14'),(74,5,'免费WiFi',NULL,1,'2026-06-21 12:05:54','2026-06-21 12:05:54'),(75,6,'免费WiFi',NULL,1,'2026-06-21 15:30:52','2026-06-21 15:30:52'),(76,6,'游泳池',NULL,1,'2026-06-21 15:30:52','2026-06-21 15:30:52'),(77,6,'健身房',NULL,1,'2026-06-21 15:30:52','2026-06-21 15:30:52');
/*!40000 ALTER TABLE `hotel_facility` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_room`
--

DROP TABLE IF EXISTS `hotel_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `hotel_id` bigint NOT NULL COMMENT '酒店ID',
  `name` varchar(100) NOT NULL COMMENT '床型名称',
  `description` text COMMENT '床型描述',
  `facilities` varchar(500) DEFAULT NULL COMMENT '设施列表，JSON格式',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_hotel_id` (`hotel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店床型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_room`
--

LOCK TABLES `hotel_room` WRITE;
/*!40000 ALTER TABLE `hotel_room` DISABLE KEYS */;
INSERT INTO `hotel_room` (`id`, `hotel_id`, `name`, `description`, `facilities`, `price`, `status`, `create_time`, `update_time`) VALUES (1,10,'豪华大床房','20平方米，1张1.8米大床，城市景观','[\"免费WiFi\", \"空调\", \"电视\", \"独立卫浴\"]',888.00,1,'2026-05-15 21:11:34','2026-05-15 22:46:48'),(2,10,'豪华双床房','25平方米，2张1.2米单人床，城市景观','[\"免费WiFi\", \"空调\", \"电视\", \"独立卫浴\"]',988.00,1,'2026-05-15 21:11:34','2026-05-15 22:46:48'),(3,10,'行政套房','40平方米，1张2米大床，行政礼遇，城市景观','[\"免费WiFi\", \"空调\", \"电视\", \"独立卫浴\", \"迷你吧\", \"行政酒廊\"]',1688.00,1,'2026-05-15 21:11:34','2026-05-15 22:46:48'),(4,10,'标准间','18平方米，1张1.5米大床，普通景观','[\"免费WiFi\", \"空调\", \"电视\", \"独立卫浴\"]',588.00,1,'2026-05-15 21:11:34','2026-05-15 22:46:48'),(5,10,'商务大床房','22平方米，1张1.8米大床，商务设施','[\"免费WiFi\", \"空调\", \"电视\", \"独立卫浴\", \"办公桌\"]',788.00,1,'2026-05-15 21:11:34','2026-05-15 22:46:48'),(6,10,'家庭套房','50平方米，1张2米大床+1张1.2米单人床，家庭设施','[\"免费WiFi\", \"空调\", \"电视\", \"独立卫浴\", \"厨房\", \"洗衣机\"]',1288.00,1,'2026-05-15 21:11:34','2026-05-15 22:46:48'),(7,4,'大床房','一米八大床','[\"空调\"]',255.00,1,'2026-06-20 22:40:14','2026-06-20 22:40:14');
/*!40000 ALTER TABLE `hotel_room` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `like_record` DISABLE KEYS */;
INSERT INTO `like_record` (`id`, `user_id`, `item_type`, `item_id`, `create_time`) VALUES (1,1,'attraction',1,'2026-02-14 17:21:56'),(2,1,'hotel',1,'2026-02-14 17:21:56'),(3,1,'food',1,'2026-02-14 17:21:56'),(4,2,'attraction',2,'2026-02-14 17:21:56'),(5,2,'hotel',2,'2026-02-14 17:21:56'),(6,2,'food',2,'2026-02-14 17:21:56'),(7,3,'attraction',3,'2026-02-14 17:21:56'),(8,3,'hotel',3,'2026-02-14 17:21:56'),(9,3,'food',3,'2026-02-14 17:21:56'),(10,4,'attraction',4,'2026-02-14 17:21:56');
/*!40000 ALTER TABLE `like_record` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `login_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_info` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_log`
--

LOCK TABLES `login_log` WRITE;
/*!40000 ALTER TABLE `login_log` DISABLE KEYS */;
INSERT INTO `login_log` (`id`, `user_id`, `username`, `account`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (69,NULL,NULL,'20260314813731','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','0','用户不存在','2026-04-16 21:36:40'),(104,21,'王涛','20260314207018','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','1','登录成功','2026-05-13 20:51:56'),(117,23,'王伟明','20260605552011','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','1','登录成功（缓存）','2026-06-05 21:45:42'),(136,21,'王涛','20260314207018','0:0:0:0:0:0:0:1','内网IP','Safari','iOS','1','登录成功','2026-07-02 22:43:33'),(157,15,'爱吃薯条的小懒猪','20260219132871','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','1','登录成功','2026-07-26 21:49:01'),(158,15,'爱吃薯条的小懒猪','20260219132871','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows','1','登录成功','2026-08-04 19:11:52');
/*!40000 ALTER TABLE `login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `milestones`
--

DROP TABLE IF EXISTS `milestones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `milestones` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '里程碑ID',
  `milestone_date` date NOT NULL COMMENT '里程碑日期',
  `title` varchar(100) NOT NULL COMMENT '里程碑标题',
  `description` text NOT NULL COMMENT '里程碑描述',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_date` (`milestone_date`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发展历程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `milestones`
--

LOCK TABLES `milestones` WRITE;
/*!40000 ALTER TABLE `milestones` DISABLE KEYS */;
INSERT INTO `milestones` (`id`, `milestone_date`, `title`, `description`, `sort_order`, `created_at`, `updated_at`) VALUES (1,'2024-01-01','项目启动','Minecraft 旅游平台正式立项，开始需求分析与架构设计。',1,'2026-08-04 22:37:06','2026-08-04 22:37:06'),(2,'2024-06-01','核心功能上线','完成景点推荐、酒店预订、美食探索等核心模块开发。',2,'2026-08-04 22:37:06','2026-08-04 22:37:06'),(3,'2024-12-01','社区功能完善','上线论坛、评论、私信等社交功能，构建旅行者社区。',3,'2026-08-04 22:37:06','2026-08-04 22:37:06'),(4,'2025-06-01','性能优化迭代','引入 Redis 缓存与 CDN 加速，平台响应速度提升 60%。',4,'2026-08-04 22:37:06','2026-08-04 22:37:06'),(5,'2026-01-01','沉浸式体验','新增全景影像与叙事内容，打造沉浸式旅行预览体验。',5,'2026-08-04 22:37:06','2026-08-04 22:37:06'),(6,'2026-07-01','持续进化中','不断打磨细节，致力于为用户提供更优质的旅行服务。',6,'2026-08-04 22:37:06','2026-08-04 22:37:06');
/*!40000 ALTER TABLE `milestones` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `monthly_recommendations` DISABLE KEYS */;
INSERT INTO `monthly_recommendations` (`id`, `month_id`, `destination_name`, `image_url`, `description`, `tags`, `season_features`, `climate_info`, `travel_tips`, `recommended_days`, `sort_order`, `is_recommended`, `view_count`, `created_at`, `updated_at`) VALUES (1,1,'哈尔滨','data:image/webp;base64,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','冰雪之城，冬季旅游胜地','[\"冰雪\", \"冰雕\", \"滑雪\"]','哈尔滨国际冰雪节，欣赏精美冰雕雪雕','气温约-20℃至-10℃，寒冷干燥','注意保暖，穿羽绒服、雪地靴，携带保温杯',3,1,1,0,'2026-02-22 14:23:06','2026-06-06 15:47:34'),(2,2,'三亚','data:image/jpeg;base64,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','避寒胜地，热带海滨风光','[\"海滩\", \"阳光\", \"海鲜\"]','温暖如春，适合避寒度假','气温约20℃-28℃，舒适宜人','防晒必备，可下海游泳',4,2,1,0,'2026-02-22 14:23:06','2026-06-06 15:52:16'),(3,1,'长白山','/images/changbaishan.jpg','冬季滑雪泡温泉的绝佳去处','[\"滑雪\", \"温泉\", \"雪景\"]','林海雪原，雾凇奇观','气温约-25℃至-15℃，山区更冷','滑雪需注意安全，泡温泉要控制时间',3,3,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(4,1,'昆明','/images/kunming.jpg','春城，四季如春','[\"暖冬\", \"花卉\", \"民族风情\"]','冬季温暖，鲜花盛开','气温约5℃-18℃，早晚温差大','早晚需穿外套，可游览滇池、石林',2,4,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(5,7,'青岛','/images/qingdao.jpg','海滨城市，避暑胜地','[\"海滩\", \"啤酒\", \"海鲜\"]','青岛国际啤酒节，海滨浴场开放','气温约22℃-28℃，凉爽海风','品尝海鲜啤酒，注意防晒',3,1,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(6,7,'承德避暑山庄','/images/chengde.jpg','清代皇家避暑胜地','[\"古迹\", \"避暑\", \"园林\"]','山区凉爽，适合避暑','气温约20℃-28℃，山区凉爽','游览皇家园林，了解清代历史',2,2,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(7,7,'九寨沟','/images/jiuzhaigou.jpg','童话世界，夏季水量充沛','[\"山水\", \"瀑布\", \"原始森林\"]','水量充沛，瀑布壮观','气温约15℃-25℃，昼夜温差大','带雨具，注意高原反应',4,3,1,0,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(8,7,'呼伦贝尔','/images/hulunbeier.jpg','大草原最美的季节','[\"草原\", \"骑马\", \"蒙古包\"]','草原最美季节，那达慕大会','气温约18℃-28℃，昼夜温差大','体验草原文化，骑马需注意安全',4,4,1,0,'2026-02-22 14:23:06','2026-06-06 15:53:15');
/*!40000 ALTER TABLE `monthly_recommendations` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `months` DISABLE KEYS */;
INSERT INTO `months` (`id`, `month_name`, `month_order`, `is_active`, `created_at`, `updated_at`) VALUES (1,'一月',1,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(2,'二月',2,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(3,'三月',3,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(4,'四月',4,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(5,'五月',5,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(6,'六月',6,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(7,'七月',7,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(8,'八月',8,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(9,'九月',9,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(10,'十月',10,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(11,'十一月',11,1,'2026-02-22 14:23:06','2026-02-22 14:23:06'),(12,'十二月',12,1,'2026-02-22 14:23:06','2026-02-22 14:23:06');
/*!40000 ALTER TABLE `months` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` (`id`, `title`, `content`, `cover_image`, `source`, `view_count`, `like_count`, `collect_count`, `comment_count`, `status`, `create_time`, `update_time`) VALUES (3,'2024年五一假期旅游市场火爆，国内游人次突破2亿','据文化和旅游部数据中心测算，2024年五一假期全国国内旅游出游人次达到2.1亿，同比增长120%，旅游收入达到1200亿元，同比增长150%。','http://localhost:8080/upload/f3235027-bacf-4650-a976-e634e1a3a266.png','人民日报',50008,2000,1500,800,1,'2026-02-14 17:21:56','2026-03-08 14:49:56'),(4,'故宫博物院推出\"清明上河图3.0\"数字艺术展','故宫博物院将于5月1日起推出\"清明上河图3.0\"数字艺术展，通过现代科技手段重现北宋都城汴京的繁华景象。','http://localhost:8080/upload/006a97b8-0305-4916-9a60-a7294505654b.png','新华社',35003,1500,1200,600,1,'2026-02-14 17:21:56','2026-04-05 13:25:31'),(5,'三亚国际旅游岛建设取得新进展，免税购物销售额突破500亿','三亚国际旅游岛建设近年来取得显著成效，2024年第一季度免税购物销售额达到520亿元，同比增长35%。','https://example.com/news3.jpg','海南日报',28001,1200,900,450,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,'2024年中国国际旅游交易会在上海开幕','为期三天的2024年中国国际旅游交易会在上海新国际博览中心开幕，来自全球50多个国家和地区的旅游机构参展。','https://example.com/news4.jpg','上海日报',25000,1000,800,400,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,'中国高铁旅游线路成为热门选择，\"高铁游\"成新趋势','随着高铁网络的不断完善，\"高铁游\"成为越来越多游客的选择。2024年第一季度，高铁沿线城市旅游收入同比增长45%。','https://example.com/news5.jpg','经济日报',32000,1300,1000,550,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,'九寨沟景区将于4月1日起恢复全域开放','经过两年的生态修复，九寨沟景区将于2024年4月1日起恢复全域开放，开放区域包括之前因地震关闭的部分景点。','https://example.com/news6.jpg','四川日报',40001,1800,1300,700,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,'2024年中国旅游日主题确定为\"美好中国，幸福旅程\"','文化和旅游部宣布，2024年中国旅游日主题为\"美好中国，幸福旅程\"，全国各地将推出多项旅游惠民措施。','https://example.com/news7.jpg','文旅部官网',22000,900,700,350,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'北京冬奥会场馆成为热门旅游景点，年接待游客突破1000万','北京冬奥会结束后，冬奥会场馆成为热门旅游景点。2023年，冬奥会场馆群共接待游客1050万人次，旅游收入达到80亿元。','https://example.com/news8.jpg','北京日报',38000,1600,1100,650,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(11,'全国首个\"智慧旅游城市\"试点在杭州启动','杭州市成为全国首个\"智慧旅游城市\"试点，通过大数据、人工智能等技术，为游客提供更加便捷、个性化的旅游服务。','https://example.com/news9.jpg','科技日报',26001,1100,850,420,1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,'中国出境游市场逐步恢复，2024年第一季度出境游人次同比增长200%','随着全球疫情防控形势的好转，中国出境游市场逐步恢复。2024年第一季度，中国公民出境游人次达到1200万，同比增长200%。','http://localhost:8080/upload/01a608e4-d51e-424e-b293-05d35bc53816.png','环球时报',45007,1900,1400,750,1,'2026-02-14 17:21:56','2026-04-05 13:30:59');
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_cta`
--

DROP TABLE IF EXISTS `officialwebsite_cta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_cta` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述文字',
  `btn_text` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '按钮文字',
  `btn_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '按钮链接',
  `bg_color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '背景色',
  `is_active` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CTA（行动召唤）表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_cta`
--

LOCK TABLES `officialwebsite_cta` WRITE;
/*!40000 ALTER TABLE `officialwebsite_cta` DISABLE KEYS */;
INSERT INTO `officialwebsite_cta` (`id`, `title`, `description`, `btn_text`, `btn_url`, `bg_color`, `is_active`, `created_at`) VALUES (1,'开启您的旅程','立即预订，享受专属优惠','探索目的地','/destinations','#2c3e50',1,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_cta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_destinations`
--

DROP TABLE IF EXISTS `officialwebsite_destinations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_destinations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目的地名称',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类（欧洲·浪漫）',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '简短描述',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `duration` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '行程时长（如：6天5晚）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `price_currency` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'CNY',
  `is_featured` tinyint DEFAULT '0' COMMENT '是否推荐',
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_destinations_featured` (`is_featured`),
  KEY `idx_destinations_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='目的地表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_destinations`
--

LOCK TABLES `officialwebsite_destinations` WRITE;
/*!40000 ALTER TABLE `officialwebsite_destinations` DISABLE KEYS */;
INSERT INTO `officialwebsite_destinations` (`id`, `name`, `category`, `description`, `image_url`, `duration`, `price`, `price_currency`, `is_featured`, `sort_order`, `created_at`) VALUES (1,'巴黎','欧洲 · 浪漫','光之城，艺术与时尚的永恒之都。','http://localhost:8080/upload/7974ed2c-413f-44cc-816c-a461a5a731a2.png','6 天 5 晚',12800.00,'CNY',0,0,'2026-07-05 07:16:36'),(2,'东京','亚洲 · 现代','传统与现代交融的东方大都会。','http://localhost:8080/upload/015123b0-8ba4-449e-ac18-b664f890050a.png','5 天 4 晚',9800.00,'CNY',0,0,'2026-07-05 07:16:36'),(3,'威尼斯','欧洲 · 水城','漂浮在水上的浪漫梦境。','http://localhost:8080/upload/a8360c18-5679-4eb5-8f4d-80df3b1fe848.png','4 天 3 晚',15600.00,'CNY',0,0,'2026-07-05 07:16:36'),(4,'马尔代夫','海岛 · 度假','印度洋上的珍珠，人间天堂。','http://localhost:8080/upload/0d0e41cf-439a-49b0-bcc5-023d4c870ca2.png','7 天 6 晚',28800.00,'CNY',0,0,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_destinations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_experiences`
--

DROP TABLE IF EXISTS `officialwebsite_experiences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_experiences` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '体验标题',
  `icon_class` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图标类名（FontAwesome）',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '体验描述',
  `is_active` tinyint DEFAULT '1',
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='体验项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_experiences`
--

LOCK TABLES `officialwebsite_experiences` WRITE;
/*!40000 ALTER TABLE `officialwebsite_experiences` DISABLE KEYS */;
INSERT INTO `officialwebsite_experiences` (`id`, `title`, `icon_class`, `description`, `is_active`, `sort_order`, `created_at`) VALUES (1,'私人定制','fa-solid fa-user-check','为您量身打造独一无二的旅行体验',1,0,'2026-07-05 07:16:36'),(2,'高端住宿','fa-solid fa-bed','精选全球顶级酒店与民宿',1,0,'2026-07-05 07:16:36'),(3,'美食探索','fa-solid fa-utensils','品味当地特色美食与米其林餐厅',1,0,'2026-07-05 07:16:36'),(4,'文化体验','fa-solid fa-landmark','深入了解目的地历史与文化',1,0,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_experiences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_footer`
--

DROP TABLE IF EXISTS `officialwebsite_footer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_footer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品牌名称',
  `brand_description` text COLLATE utf8mb4_unicode_ci COMMENT '品牌描述',
  `social_links` json DEFAULT NULL COMMENT '社交媒体链接（JSON格式）',
  `copyright_text` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版权文字',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页脚配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_footer`
--

LOCK TABLES `officialwebsite_footer` WRITE;
/*!40000 ALTER TABLE `officialwebsite_footer` DISABLE KEYS */;
INSERT INTO `officialwebsite_footer` (`id`, `brand_name`, `brand_description`, `social_links`, `copyright_text`, `updated_at`) VALUES (1,'叙事之旅','探索世界，书写故事。我们致力于为每一位旅行者打造独特的旅行体验。','{\"weibo\": \"@travelstory\", \"wechat\": \"travel_story\", \"instagram\": \"@travelstory\"}','© 2026 叙事之旅 版权所有','2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_footer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_footer_links`
--

DROP TABLE IF EXISTS `officialwebsite_footer_links`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_footer_links` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类（探索/关于/支持）',
  `title` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接标题',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接地址',
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页脚链接表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_footer_links`
--

LOCK TABLES `officialwebsite_footer_links` WRITE;
/*!40000 ALTER TABLE `officialwebsite_footer_links` DISABLE KEYS */;
INSERT INTO `officialwebsite_footer_links` (`id`, `category`, `title`, `url`, `sort_order`, `created_at`) VALUES (1,'探索','热门目的地','/destinations',0,'2026-07-05 07:16:36'),(2,'探索','旅行故事','/stories',0,'2026-07-05 07:16:36'),(3,'探索','体验项目','/experiences',0,'2026-07-05 07:16:36'),(4,'关于','关于我们','/about',0,'2026-07-05 07:16:36'),(5,'关于','团队介绍','/team',0,'2026-07-05 07:16:36'),(6,'关于','联系我们','/contact',0,'2026-07-05 07:16:36'),(7,'支持','常见问题','/faq',0,'2026-07-05 07:16:36'),(8,'支持','隐私政策','/privacy',0,'2026-07-05 07:16:36'),(9,'支持','服务条款','/terms',0,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_footer_links` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_hero`
--

DROP TABLE IF EXISTS `officialwebsite_hero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_hero` (
  `id` int NOT NULL AUTO_INCREMENT,
  `badge_text` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签文字',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主标题',
  `subtitle` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题（高亮部分）',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述文字',
  `bg_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '背景图片URL',
  `bg_video` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '背景视频URL',
  `btn_text` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '按钮文字',
  `is_active` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='英雄区配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_hero`
--

LOCK TABLES `officialwebsite_hero` WRITE;
/*!40000 ALTER TABLE `officialwebsite_hero` DISABLE KEYS */;
INSERT INTO `officialwebsite_hero` (`id`, `badge_text`, `title`, `subtitle`, `description`, `bg_image`, `bg_video`, `btn_text`, `is_active`, `created_at`) VALUES (1,'2026 · 叙事之旅','故事始于','足尖之下','每一段旅程都是一部未完成的小说，我们在风景中寻找自己的章节。','http://localhost:8080/upload/62fb05d6-a0e3-4364-b773-f4ad26b31675.png',NULL,'',1,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_hero` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_home_config`
--

DROP TABLE IF EXISTS `officialwebsite_home_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_home_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `section_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区块标识',
  `section_title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区块标题',
  `section_subtitle` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区块副标题',
  `section_label` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区块标签',
  `is_visible` tinyint DEFAULT '1',
  `config_data` json DEFAULT NULL COMMENT '额外配置数据',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `section_key` (`section_key`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_home_config`
--

LOCK TABLES `officialwebsite_home_config` WRITE;
/*!40000 ALTER TABLE `officialwebsite_home_config` DISABLE KEYS */;
INSERT INTO `officialwebsite_home_config` (`id`, `section_key`, `section_title`, `section_subtitle`, `section_label`, `is_visible`, `config_data`, `updated_at`) VALUES (1,'hero','英雄区','首屏展示区域','home',1,NULL,'2026-07-05 07:16:36'),(2,'destinations','目的地推荐','精选热门目的地','home',1,NULL,'2026-07-05 07:16:36'),(3,'experiences','体验项目','独特旅行体验','home',1,NULL,'2026-07-05 07:16:36'),(4,'narrative','叙事章节','品牌故事','home',1,NULL,'2026-07-05 07:16:36'),(5,'stories','旅行故事','用户真实分享','home',1,NULL,'2026-07-05 07:16:36'),(6,'immersion','沉浸式引用','名言警句','home',1,NULL,'2026-07-05 07:16:36'),(7,'cta','行动召唤','引导用户转化','home',1,NULL,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_home_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_immersion`
--

DROP TABLE IF EXISTS `officialwebsite_immersion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_immersion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quote_text` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '引用内容',
  `author` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
  `is_active` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沉浸式引用表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_immersion`
--

LOCK TABLES `officialwebsite_immersion` WRITE;
/*!40000 ALTER TABLE `officialwebsite_immersion` DISABLE KEYS */;
INSERT INTO `officialwebsite_immersion` (`id`, `quote_text`, `author`, `is_active`, `created_at`) VALUES (1,'世界是一本书，而不旅行的人只读了其中一页。','圣奥古斯丁',1,'2026-07-05 07:16:36'),(2,'旅行的本质是发现。发现世界的美好，也发现自己的渺小。','佚名',1,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_immersion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_narrative`
--

DROP TABLE IF EXISTS `officialwebsite_narrative`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_narrative` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节标题',
  `subtitle` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '详细内容',
  `quote_text` text COLLATE utf8mb4_unicode_ci COMMENT '引用文字',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配图URL',
  `is_active` tinyint DEFAULT '1',
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='叙事章节表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_narrative`
--

LOCK TABLES `officialwebsite_narrative` WRITE;
/*!40000 ALTER TABLE `officialwebsite_narrative` DISABLE KEYS */;
INSERT INTO `officialwebsite_narrative` (`id`, `title`, `subtitle`, `content`, `quote_text`, `image_url`, `is_active`, `sort_order`, `created_at`) VALUES (1,'旅程的意义','探索未知，发现自我','旅行不仅仅是到达目的地，更是在路上的每一个瞬间。从出发的那一刻起，我们就在书写属于自己的故事。每一次转角，每一次相遇，都是命运的安排。','旅行是灵魂的修复，是心灵的洗礼。','http://localhost:8080/upload/a938d6dd-e084-4214-b5c4-4aae34b191c2.png',1,0,'2026-07-05 07:16:36'),(2,'目的地的故事','每座城市都有它的秘密','每个目的地都承载着独特的历史与文化。巴黎的浪漫、东京的精致、威尼斯的优雅，它们用自己的方式诉说着岁月的故事。','城市是一本打开的书，不同的人读到不同的故事。','http://localhost:8080/upload/0548e2d8-ed3f-440e-9dc8-3ab9701f51c2.png',1,0,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_narrative` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_navigation`
--

DROP TABLE IF EXISTS `officialwebsite_navigation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_navigation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单标题',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接地址',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `is_active` tinyint DEFAULT '1' COMMENT '是否启用',
  `parent_id` int DEFAULT '0' COMMENT '父级ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_navigation_parent` (`parent_id`),
  KEY `idx_navigation_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导航菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_navigation`
--

LOCK TABLES `officialwebsite_navigation` WRITE;
/*!40000 ALTER TABLE `officialwebsite_navigation` DISABLE KEYS */;
INSERT INTO `officialwebsite_navigation` (`id`, `title`, `url`, `sort_order`, `is_active`, `parent_id`, `created_at`) VALUES (1,'首页','/',1,0,0,'2026-07-05 15:01:06'),(2,'关于我们','/about',2,0,0,'2026-07-05 15:01:06'),(4,'联系我们','/contact',3,0,0,'2026-07-05 15:01:06'),(5,'首页','/home',1,1,0,'2026-07-06 13:41:30'),(7,'公司简介','/about',1,1,2,'2026-07-06 13:41:30');
/*!40000 ALTER TABLE `officialwebsite_navigation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_settings`
--

DROP TABLE IF EXISTS `officialwebsite_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `setting_value` text COLLATE utf8mb4_unicode_ci,
  `setting_group` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'general',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `setting_key` (`setting_key`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_settings`
--

LOCK TABLES `officialwebsite_settings` WRITE;
/*!40000 ALTER TABLE `officialwebsite_settings` DISABLE KEYS */;
INSERT INTO `officialwebsite_settings` (`id`, `setting_key`, `setting_value`, `setting_group`, `updated_at`) VALUES (1,'site_name','叙事之旅','general','2026-07-05 07:16:36'),(2,'site_slogan','故事始于足尖之下','general','2026-07-05 07:16:36'),(3,'site_description','探索世界，书写故事','general','2026-07-05 07:16:36'),(4,'default_currency','CNY','general','2026-07-05 07:16:36'),(5,'language','zh-CN','general','2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officialwebsite_stories`
--

DROP TABLE IF EXISTS `officialwebsite_stories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officialwebsite_stories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `author_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作者名字',
  `author_role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者角色（如：自由撰稿人）',
  `author_avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者头像URL',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '故事内容',
  `rating` tinyint DEFAULT '5' COMMENT '评分（1-5）',
  `destination` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联目的地',
  `is_featured` tinyint DEFAULT '0',
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_stories_featured` (`is_featured`),
  KEY `idx_stories_rating` (`rating`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅行故事表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officialwebsite_stories`
--

LOCK TABLES `officialwebsite_stories` WRITE;
/*!40000 ALTER TABLE `officialwebsite_stories` DISABLE KEYS */;
INSERT INTO `officialwebsite_stories` (`id`, `author_name`, `author_role`, `author_avatar`, `content`, `rating`, `destination`, `is_featured`, `sort_order`, `created_at`) VALUES (1,'林小雨','自由撰稿人',NULL,'在巴黎的街头漫步，感受着这座城市的浪漫气息。从埃菲尔铁塔到卢浮宫，每一处都让人流连忘返。最喜欢的是塞纳河畔的咖啡馆，坐在那里看行人匆匆，仿佛时间都慢了下来。',5,'巴黎',1,0,'2026-07-05 07:16:36'),(2,'张明远','旅行摄影师',NULL,'马尔代夫的海水是我见过最美的蓝色，清澈见底，仿佛可以看到海底的珊瑚和鱼群。清晨在水上别墅醒来，推开门就是无边无际的大海，这种感觉太不真实了。',5,'马尔代夫',1,0,'2026-07-05 07:16:36');
/*!40000 ALTER TABLE `officialwebsite_stories` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `online_users` DISABLE KEYS */;
INSERT INTO `online_users` (`id`, `username`, `nickname`, `role`, `login_ip`, `login_time`, `last_activity_time`, `browser`, `os`, `status`) VALUES (15,'爱吃薯条的小懒猪','小可爱','admin','0:0:0:0:0:0:0:1','2026-08-04 19:11:52','2026-08-04 19:11:52','Chrome','Windows','online'),(19,'李小明','李小明','vip','0:0:0:0:0:0:0:1','2026-07-11 22:27:20','2026-07-11 22:27:20','Chrome','Windows','online'),(21,'王涛','王涛','vip','0:0:0:0:0:0:0:1','2026-07-02 22:43:33','2026-07-02 22:43:33','Safari','iOS','online'),(22,'云天亦','云天亦','vip','0:0:0:0:0:0:0:1','2026-07-02 22:36:51','2026-07-02 22:36:51','Chrome','Windows','online');
/*!40000 ALTER TABLE `online_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_logs`
--

DROP TABLE IF EXISTS `operation_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` int DEFAULT NULL COMMENT '操作用户ID',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型',
  `operation_desc` varchar(255) NOT NULL COMMENT '操作描述',
  `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_logs`
--

LOCK TABLES `operation_logs` WRITE;
/*!40000 ALTER TABLE `operation_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_logs` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` (`id`, `user_id`, `order_no`, `item_type`, `item_id`, `item_name`, `amount`, `quantity`, `remark`, `status`, `create_time`, `update_time`, `image`) VALUES (59,15,'202603220005','product',15,'北京烤鸭礼盒',792.00,4,'','1','2026-03-22 18:02:02','2026-03-22 18:02:07','http://localhost:8080/upload/dc857d4f-9991-450e-849a-6d4a43a493c8.png'),(61,15,'202604260001','product',6,'云南普洱茶',368.00,1,'','0','2026-04-26 18:40:36','2026-04-26 18:40:36','https://example.com/product6.jpg'),(62,15,'202604260002','food',4,'上海小笼包',38.00,1,'','0','2026-04-26 18:57:45','2026-04-26 18:57:45','http://localhost:8080/upload/396217d8-909d-4f79-9176-38711f8d185d.png'),(63,15,'202604260003','food',9,'西安肉夹馍',15.00,1,'','1','2026-04-26 18:58:08','2026-04-26 18:58:12','https://example.com/food7.jpg'),(64,15,'202607030001','product',10,'新疆和田玉饰品',888.00,1,'','0','2026-07-03 21:08:51','2026-07-03 21:08:51','https://example.com/product10.jpg');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` (`id`, `order_id`, `user_id`, `payment_no`, `amount`, `payment_method`, `status`, `create_time`, `update_time`) VALUES (28,54,15,'PAY17741725170458530',198.00,'支付宝','2','2026-03-22 17:41:57','2026-03-22 17:41:57'),(29,56,15,'PAY17741727547711735',398.00,'支付宝','1','2026-03-22 17:45:55','2026-03-22 17:45:55'),(30,57,15,'PAY17741727891287678',198.00,'支付宝','1','2026-03-22 17:46:29','2026-03-22 17:46:29'),(31,57,15,'PAY17741728034593460',198.00,'支付宝','1','2026-03-22 17:46:43','2026-03-22 17:46:43'),(32,57,15,'PAY17741728067189122',198.00,'微信','1','2026-03-22 17:46:47','2026-03-22 17:46:47'),(33,58,15,'PAY17741728316456864',398.00,'支付宝','1','2026-03-22 17:47:12','2026-03-22 17:47:12'),(34,59,15,'PAY17741737265069875',3168.00,'银联','1','2026-03-22 18:02:07','2026-03-22 18:02:07'),(35,60,15,'PAY17771999672011221',298.00,'支付宝','2','2026-04-26 18:39:27','2026-04-26 18:39:27'),(39,61,15,'PAY17772010160339082',368.00,'微信','2','2026-04-26 18:56:56','2026-04-26 18:56:56'),(40,63,15,'PAY17772010917083913',15.00,'微信','1','2026-04-26 18:58:12','2026-04-26 18:58:12');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `platform_stats`
--

DROP TABLE IF EXISTS `platform_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `platform_stats` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_label` varchar(50) NOT NULL COMMENT '统计标签',
  `stat_value` varchar(50) NOT NULL COMMENT '统计数值',
  `stat_icon` varchar(255) DEFAULT NULL COMMENT '统计图标(SVG地址)',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='平台数据统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `platform_stats`
--

LOCK TABLES `platform_stats` WRITE;
/*!40000 ALTER TABLE `platform_stats` DISABLE KEYS */;
INSERT INTO `platform_stats` (`id`, `stat_label`, `stat_value`, `stat_icon`, `sort_order`, `updated_at`) VALUES (1,'注册用户','52万+',NULL,1,'2026-08-04 22:37:06'),(2,'目的地覆盖','120+',NULL,2,'2026-08-04 22:37:06'),(3,'旅行笔记','8.6万+',NULL,3,'2026-08-04 22:37:06'),(4,'好评率','98%',NULL,4,'2026-08-04 22:37:06');
/*!40000 ALTER TABLE `platform_stats` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `points_products` DISABLE KEYS */;
INSERT INTO `points_products` (`id`, `name`, `description`, `points_price`, `stock`, `image_url`, `status`, `create_time`, `update_time`) VALUES (1,'小米手环','智能运动手环',500,100,'https://via.placeholder.com/300x200',1,'2026-04-26 14:42:13','2026-04-26 14:42:13'),(2,'蓝牙耳机','无线蓝牙耳机',800,50,'https://via.placeholder.com/300x200',1,'2026-04-26 14:42:13','2026-04-26 14:42:13'),(3,'充电宝','大容量移动电源',300,200,'https://via.placeholder.com/300x200',1,'2026-04-26 14:42:13','2026-04-26 14:42:13');
/*!40000 ALTER TABLE `points_products` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `points_record` DISABLE KEYS */;
INSERT INTO `points_record` (`id`, `user_id`, `points`, `before_points`, `after_points`, `type`, `remark`, `create_time`) VALUES (1,15,15,0,15,'INCOME','支付订单获得积分','2026-04-26 18:58:11');
/*!40000 ALTER TABLE `points_record` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` (`id`, `name`, `type`, `city`, `province`, `description`, `cover_image`, `images`, `price`, `stock`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`, `create_time`, `update_time`, `commodity`) VALUES (1,'北京烤鸭礼盒','食品','北京','北京','真空包装的北京烤鸭，方便携带，味道正宗。','http://localhost:8080/upload/dc857d4f-9991-450e-849a-6d4a43a493c8.png','https://example.com/product1_1.jpg,https://example.com/product1_2.jpg',198.00,500,5,600,400,200,'北京特产,食品,烤鸭',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(2,'上海雪花膏','化妆品','上海','上海','上海传统化妆品，滋润保湿，香气宜人。','https://example.com/product2.jpg','https://example.com/product2_1.jpg,https://example.com/product2_2.jpg',68.00,1000,4,400,300,150,'上海特产,化妆品,传统',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(3,'杭州西湖龙井茶叶','食品','杭州','浙江','西湖龙井是中国十大名茶之一，香气清高，滋味鲜爽。','https://example.com/product3.jpg','https://example.com/product3_1.jpg,https://example.com/product3_2.jpg',298.00,300,5,700,500,250,'杭州特产,茶叶,龙井',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(4,'四川大熊猫玩偶','工艺品','成都','四川','可爱的大熊猫玩偶，是四川的标志性纪念品。','https://example.com/product4.jpg','https://example.com/product4_1.jpg,https://example.com/product4_2.jpg',88.00,800,5,500,400,180,'四川特产,工艺品,熊猫',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(5,'广东广绣','工艺品','广州','广东','广绣是中国四大名绣之一，图案精美，工艺精湛。','https://example.com/product5.jpg','https://example.com/product5_1.jpg,https://example.com/product5_2.jpg',398.00,200,5,450,350,160,'广东特产,工艺品,刺绣',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(6,'云南普洱茶','食品','昆明','云南','普洱茶是云南特产，具有降脂减肥、降压、抗动脉硬化等功效。','https://example.com/product6.jpg','https://example.com/product6_1.jpg,https://example.com/product6_2.jpg',368.00,400,5,650,450,220,'云南特产,茶叶,普洱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(7,'西安兵马俑模型','工艺品','西安','陕西','兵马俑模型是西安的特色纪念品，做工精细，造型逼真。','https://example.com/product7.jpg','https://example.com/product7_1.jpg,https://example.com/product7_2.jpg',128.00,600,4,350,250,130,'西安特产,工艺品,兵马俑',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(8,'湖南湘绣','工艺品','长沙','湖南','湘绣是中国四大名绣之一，以其精湛的刺绣技艺和独特的艺术风格著称。','https://example.com/product8.jpg','https://example.com/product8_1.jpg,https://example.com/product8_2.jpg',328.00,250,4,300,200,110,'湖南特产,工艺品,刺绣',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(9,'福建安溪铁观音','食品','泉州','福建','铁观音是中国十大名茶之一，香气馥郁持久，滋味醇厚甘鲜。','https://example.com/product9.jpg','https://example.com/product9_1.jpg,https://example.com/product9_2.jpg',268.00,350,5,550,350,190,'福建特产,茶叶,铁观音',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1'),(10,'新疆和田玉饰品','珠宝','乌鲁木齐','新疆','和田玉是中国四大名玉之一，质地温润，色泽优美。','https://example.com/product10.jpg','https://example.com/product10_1.jpg,https://example.com/product10_2.jpg',888.00,150,5,400,300,140,'新疆特产,珠宝,和田玉',1,'2026-02-14 17:21:56','2026-02-14 17:21:56','1');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `recommend` DISABLE KEYS */;
INSERT INTO `recommend` (`id`, `image`, `name`, `description`, `details`, `state`, `type`) VALUES (7,'http://localhost:8080/upload/7292fcb2-1e6f-493e-8e1f-65e3403e5618.png','故宫博物院','明清皇家宫殿，世界文化遗产','故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。故宫以三大殿为中心，占地面积约72万平方米，建筑面积约15万平方米，有大小宫殿七十多座，房屋九千余间。故宫是世界上现存规模最大、保存最为完整的木质结构古建筑之一，是国家AAAAA级旅游景区，1961年被列为第一批全国重点文物保护单位；1987年被列为世界文化遗产。',0,NULL),(8,'http://localhost:8080/upload/939df816-4cf1-4005-a46a-83f0d1f98f23.png','长城','世界新七大奇迹之一','长城又称万里长城，是中国古代的军事防御工事，是一道高大、坚固而且连绵不断的长垣，用以限隔敌骑的行动。长城不是一道单纯孤立的城墙，而是以城墙为主体，同大量的城、障、亭、标相结合的防御体系。长城修筑的历史可上溯到西周时期，发生在首都镐京的著名典故“烽火戏诸侯”就源于此。春秋战国时期列国争霸，互相防守，长城修筑进入第一个高潮，但此时修筑的长度都比较短。秦灭六国统一天下后，秦始皇连接和修缮战国长城，始有万里长城之称。明朝是最后一个大修长城的朝代，今天人们所看到的长城多是此时修筑。',0,NULL),(9,'http://localhost:8080/upload/96cba0e1-e8ef-4335-9f45-6e167ba90181.png','西湖','人间天堂，杭州明珠','西湖位于浙江省杭州市西湖区龙井路1号，杭州市区西部，景区总面积49平方千米，汇水面积为21.22平方千米，湖面面积为6.38平方千米。西湖有100多处公园景点，有“西湖十景”、“新西湖十景”、“三评西湖十景”之说，有60多处国家、省、市级重点文物保护单位和20多座博物馆，有断桥、雷峰塔、钱王祠、净慈寺、苏小小墓等景点。2007年，杭州市西湖风景名胜区被评为“国家AAAAA级旅游景区”。2011年6月24日，“杭州西湖文化景观”正式被列入《世界遗产名录》。',0,NULL),(10,'http://localhost:8080/upload/ad066095-c0af-43dd-a7ae-708e633bdb68.png','兵马俑','世界第八大奇迹','兵马俑，即秦始皇兵马俑，亦简称秦兵马俑或秦俑，是古代墓葬雕塑的一个类别。位于今陕西省西安市临潼区秦始皇陵以东1.5千米处的兵马俑坑内。兵马俑是制成兵马（战车、战马、士兵）形状的殉葬品。1961年3月4日，秦始皇陵被国务院公布为第一批全国重点文物保护单位。1974年3月，兵马俑被发现。1987年，秦始皇陵及兵马俑坑被联合国教科文组织批准列入《世界遗产名录》，并被誉为“世界第八大奇迹”，先后有200多位外国元首和政府首脑参观访问，成为中国古代辉煌文明的一张金字名片，被誉为世界十大古墓稀世珍宝之一。',0,NULL),(11,'http://localhost:8080/upload/a36c2e53-5145-4d73-8d72-f7cf477939cc.png','张家界','奇峰三千，秀水八百','张家界国家森林公园位于湖南省西北部张家界市境内，是中国第一个国家森林公园。公园自然风光以峰称奇、以谷显幽、以林见秀。其间有奇峰3000多座，这些石峰如人如兽、如器如物，形象逼真，气势壮观。峰间峡谷，溪流潺潺，浓荫蔽日。有“三千奇峰，八百秀水”之美称。公园不仅自然风光壮美绝伦，而且森林植物和野生动物资源极为丰富，森林覆盖率达98%，是一座巨大的生物宝库和天然氧吧，被称为“自然博物馆和天然植物园”。',0,NULL),(12,'http://localhost:8080/upload/9f4747fa-b496-4b60-b28d-9e28b1b806ef.png','九寨沟','童话世界，人间仙境','九寨沟位于四川省阿坝藏族羌族自治州九寨沟县境内，是一条纵深50余千米的山沟谷地，总面积64297公顷，森林覆盖率超过80%。因沟内有树正寨、荷叶寨、则查洼寨等九个藏族村寨坐落在这片高山湖泊群中而得名。九寨沟国家级自然保护区主要保护对象是大熊猫、金丝猴等珍稀动物及其自然生态环境。有74种国家保护珍稀植物，有18种国家保护动物，还有丰富的古生物化石、古冰川地貌。“九寨归来不看水”，是对九寨沟景色真实的诠释。泉、瀑、河、滩108个海子，构成一个个五彩斑斓的瑶池玉盆。',0,NULL),(13,'http://localhost:8080/upload/2f870d6c-ba3f-420c-94e4-9db795200a5c.png','黄山','五岳归来不看山，黄山归来不看岳','黄山位于安徽省南部黄山市境内，有72峰，主峰莲花峰海拔1864米，与光明顶、天都峰并称三大黄山主峰，为36大峰之一。黄山是安徽旅游的标志，是中国十大风景名胜唯一的山岳风光。黄山原名“黟山”，因峰岩青黑，遥望苍黛而名。后因传说轩辕黄帝曾在此炼丹，故改名为“黄山”。黄山代表景观有“四绝三瀑”，四绝：奇松、怪石、云海、温泉；三瀑：人字瀑、百丈泉、九龙瀑。黄山迎客松是安徽人民热情友好的象征，承载着拥抱世界的东方礼仪文化。',0,NULL),(14,'http://localhost:8080/upload/415ee572-6a5f-478d-aafa-3a9783591ea3.png','桂林山水','桂林山水甲天下','桂林山水是对桂林旅游资源的总称。国家AAAAA级旅游景区，中国十大风景名胜之一，桂林山水是中国山水的代表，典型的喀斯特地形构成了别具一格的桂林山水，桂林山水所指的范围很广，项目繁多。桂林山水“山青、水秀、洞奇、石美\"，包括山、水、喀斯特岩洞、石刻等等，其境内的山水风光举世闻名，千百年来享有“桂林山水甲天下”的美誉。漓江属珠江水系的桂江上游河段，发源于华南第一峰猫儿山，是桂林山水风光的精华。闻名全球的漓江景区是桂林山水的核心，漓江像一条青绸绿带，盘绕在万点峰峦之间。',0,NULL),(15,'http://localhost:2025/upload/f787f69a-274b-442a-9ed3-9b5030673180.png','鼓浪屿','海上花园，钢琴之岛','鼓浪屿原名“圆沙洲”，别名“圆洲仔”，南宋时期命“五龙屿”，明朝改称“鼓浪屿”。因岛西南方海滩上有一块两米多高、中有洞穴的礁石，每当涨潮水涌，浪击礁石，声似擂鼓，人们称“鼓浪石”，鼓浪屿因此而得名。鼓浪屿风景名胜区获得国家5A级旅游景区、全国重点文物保护单位、中国最美五大城区等荣誉。2017年7月8日，“鼓浪屿：历史国际社区”被列入世界遗产名录，成为中国第52项世界遗产项目。岛上气候宜人，四季如春，无车马喧嚣，有鸟语花香，素有“海上花园”之誉。',1,NULL),(16,'http://localhost:2025/upload/01130228-67ad-4d32-be47-25e751b8f9ba.png','亚龙湾','天下第一湾','亚龙湾是中华人民共和国海南省三亚市东郊的一处优质热带海滨风景区，距离市中心区约10公里。海湾以中心有野猪岛为中心，南有东洲岛、西洲岛，西面有东排、西排，可开展多种水上运动。亚龙湾为一个月牙湾，拥有7千米长的银白色海滩，沙质相当细腻。而这里的南海没有受到污染，海水洁净透明，远望呈现几种不同的蓝色，而水面下珊瑚种类丰富，可清楚观赏珊瑚，适合多种水面下活动包括潜水等，令海底成为了当地的旅游的核心。岸上林木郁郁葱葱。冬季这里的气温27摄氏度，水温20摄氏度，是一处理想的冬季避寒和休闲度假胜地。号称\"东方夏威夷\"。',1,NULL);
/*!40000 ALTER TABLE `recommend` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `review_likes` DISABLE KEYS */;
INSERT INTO `review_likes` (`id`, `review_id`, `user_id`, `created_at`) VALUES (1,1,3,'2026-02-14 09:23:52'),(2,1,4,'2026-02-14 09:23:52'),(4,2,1,'2026-02-14 09:23:52'),(6,3,2,'2026-02-14 09:23:52'),(9,4,3,'2026-02-14 09:23:52');
/*!40000 ALTER TABLE `review_likes` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `review_replies` DISABLE KEYS */;
INSERT INTO `review_replies` (`id`, `review_id`, `user_id`, `content`, `likes_count`, `created_at`, `updated_at`) VALUES (1,1,2,'同意！早上的长城人少，拍照效果确实更好。',5,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,1,3,'我也想去长城，请问需要爬很多台阶吗？',2,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,2,1,'是的，长城的台阶很多，建议穿舒适的鞋子。',3,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,3,4,'故宫确实很值得参观，我去年去过，印象深刻。',4,'2026-02-14 09:23:52','2026-02-14 09:23:52'),(6,4,3,'工作日去确实人少很多，我就是工作日去的。',2,'2026-02-14 09:23:52','2026-02-14 09:23:52');
/*!40000 ALTER TABLE `review_replies` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` (`id`, `attraction_id`, `user_id`, `rating`, `title`, `content`, `visit_date`, `travel_with`, `photos_count`, `likes_count`, `helpful_count`, `is_verified`, `status`, `created_at`, `updated_at`) VALUES (1,1,1,5.0,'长城真的很震撼！','第一次来长城，被它的规模和历史感深深震撼。建议早上早点来，人少一些，拍照效果更好。','2024-04-01','朋友',5,20,15,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,1,2,4.5,'值得一去的景点','长城很壮观，但是人真的很多。建议穿舒适的鞋子，因为需要走很多台阶。','2024-04-10','家人',3,12,8,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,2,3,5.0,'故宫的历史文化底蕴深厚','故宫是中国历史文化的瑰宝，每一个宫殿都有其独特的故事。建议请个导游，了解更多历史背景。','2024-04-05','家人',8,25,20,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,2,4,4.5,'人太多了','故宫确实很美丽，但是人真的太多了，尤其是节假日。建议工作日来参观。','2024-04-15','朋友',2,8,5,1,'approved','2026-02-14 09:23:52','2026-02-14 09:23:52');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `safetytips` DISABLE KEYS */;
INSERT INTO `safetytips` (`id`, `title`, `description`, `image_url`, `category_id`, `sort_weight`, `status`, `creator_id`, `updater_id`, `create_time`, `update_time`) VALUES (31,'旅游前的准备工作','旅游前要检查身份证、护照等证件，准备好常用药品，了解目的地的天气情况...','https://example.com/safety1.jpg',NULL,1,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(32,'在景区游玩的安全注意事项','在景区游玩时要遵守景区规定，不要离开指定路线，注意防滑防摔...','https://example.com/safety2.jpg',NULL,2,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(33,'食品安全注意事项','在旅游过程中要注意饮食卫生，避免食用生冷食物，选择正规餐厅...','https://example.com/safety3.jpg',NULL,3,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(34,'交通安全注意事项','在旅游过程中要遵守交通规则，选择正规的交通工具，系好安全带...','https://example.com/safety4.jpg',NULL,4,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(35,'财物安全注意事项','在旅游过程中要保管好个人财物，避免携带大量现金，注意防盗防骗...','https://example.com/safety5.jpg',NULL,5,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(36,'应急处理方法','在旅游过程中如果遇到紧急情况，要保持冷静，及时拨打当地的紧急救援电话...','https://example.com/safety6.jpg',NULL,6,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(37,'高原旅游注意事项','在高原地区旅游时要注意预防高原反应，避免剧烈运动，保持充足的睡眠...','https://example.com/safety7.jpg',NULL,7,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(38,'水上活动安全注意事项','在进行水上活动时要穿戴好救生设备，听从教练的指导，不要独自下水...','https://example.com/safety8.jpg',NULL,8,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(39,'登山安全注意事项','在登山时要选择合适的装备，遵循登山路线，注意天气变化...','https://example.com/safety9.jpg',NULL,9,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13'),(40,'夜间出行安全注意事项','在夜间出行时要选择明亮的路线，避免前往偏僻的地方，注意自身安全...','http://localhost:8080/upload/e43d21d9-02d5-453a-82fb-ce9e04389950.png',NULL,10,1,NULL,NULL,'2025-12-21 12:02:13','2025-12-21 12:02:13');
/*!40000 ALTER TABLE `safetytips` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` (`id`, `name`, `chinese_name`, `category`, `icon`, `color`, `sort_order`, `created_at`) VALUES (1,'historical','历史古迹','theme','🏯','#8B4513',1,'2026-02-14 09:23:52'),(2,'natural','自然风光','theme','🏞️','#228B22',2,'2026-02-14 09:23:52'),(3,'modern','现代建筑','theme','🏙️','#4682B4',3,'2026-02-14 09:23:52'),(4,'cultural','文化体验','theme','🎭','#DC143C',4,'2026-02-14 09:23:52'),(5,'family','适合家庭','audience','👨‍👩‍👧‍👦','#FFD700',5,'2026-02-14 09:23:52'),(6,'romantic','浪漫情侣','audience','💕','#FF69B4',6,'2026-02-14 09:23:52'),(7,'adventure','冒险刺激','feature','🚀','#FF4500',7,'2026-02-14 09:23:52'),(8,'relaxation','休闲放松','feature','🧘','#98FB98',8,'2026-02-14 09:23:52'),(9,'food','美食之旅','feature','🍜','#FFA500',9,'2026-02-14 09:23:52'),(10,'shopping','购物天堂','feature','🛍️','#9370DB',10,'2026-02-14 09:23:52');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tech_categories`
--

DROP TABLE IF EXISTS `tech_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tech_categories` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) NOT NULL COMMENT '分类名称(后端/前端/数据)',
  `theme_class` varchar(50) NOT NULL COMMENT '主题样式类名',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技术分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tech_categories`
--

LOCK TABLES `tech_categories` WRITE;
/*!40000 ALTER TABLE `tech_categories` DISABLE KEYS */;
INSERT INTO `tech_categories` (`id`, `category_name`, `theme_class`, `sort_order`, `created_at`) VALUES (1,'后端','backend',1,'2026-08-04 22:37:06'),(2,'前端','frontend',2,'2026-08-04 22:37:06'),(3,'数据','database',3,'2026-08-04 22:37:06');
/*!40000 ALTER TABLE `tech_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tech_items`
--

DROP TABLE IF EXISTS `tech_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tech_items` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '技术项ID',
  `category_id` int NOT NULL COMMENT '所属分类ID',
  `tech_name` varchar(50) NOT NULL COMMENT '技术名称',
  `description` varchar(100) NOT NULL COMMENT '技术描述',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  CONSTRAINT `tech_items_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `tech_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技术项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tech_items`
--

LOCK TABLES `tech_items` WRITE;
/*!40000 ALTER TABLE `tech_items` DISABLE KEYS */;
INSERT INTO `tech_items` (`id`, `category_id`, `tech_name`, `description`, `sort_order`, `created_at`) VALUES (1,1,'Spring Boot','应用框架',1,'2026-08-04 22:37:06'),(2,1,'Spring Security','安全认证',2,'2026-08-04 22:37:06'),(3,1,'MyBatis','ORM 框架',3,'2026-08-04 22:37:06'),(4,1,'JWT','令牌认证',4,'2026-08-04 22:37:06'),(5,1,'Redis','缓存中间件',5,'2026-08-04 22:37:06'),(6,2,'Vue 3','视图框架',1,'2026-08-04 22:37:06'),(7,2,'Element Plus','UI 组件库',2,'2026-08-04 22:37:06'),(8,2,'Vite','构建工具',3,'2026-08-04 22:37:06'),(9,2,'Pinia','状态管理',4,'2026-08-04 22:37:06'),(10,2,'Vue Router','路由管理',5,'2026-08-04 22:37:06'),(11,3,'MySQL','关系型数据库',1,'2026-08-04 22:37:06'),(12,3,'Druid','连接池',2,'2026-08-04 22:37:06'),(13,3,'Redis','缓存数据库',3,'2026-08-04 22:37:06'),(14,3,'MyBatis','数据访问层',4,'2026-08-04 22:37:06');
/*!40000 ALTER TABLE `tech_items` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
INSERT INTO `ticket` (`id`, `type`, `departure_city`, `arrival_city`, `departure_time`, `arrival_time`, `carrier`, `price`, `stock`, `seat_class`, `status`, `create_time`, `update_time`) VALUES (1,'train','北京','上海','08:00','13:00','中国铁路',553.00,100,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,'train','上海','北京','14:00','19:00','中国铁路',553.00,100,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,'flight','北京','广州','09:30','12:30','中国国航',1200.00,50,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,'flight','广州','北京','13:30','16:30','中国国航',1200.00,50,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,'train','北京','西安','07:30','12:30','中国铁路',515.00,80,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,'train','西安','北京','13:30','18:30','中国铁路',515.00,80,'二等座',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,'flight','上海','成都','10:00','13:00','东方航空',1000.00,40,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,'flight','成都','上海','14:00','17:00','东方航空',1000.00,40,'经济舱',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,'taxi','北京','北京','全天','全天','首汽约车',2.30,1000,'普通车',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,'taxi','上海','上海','全天','全天','滴滴出行',2.60,1000,'普通车',1,'2026-02-14 17:21:56','2026-02-14 17:21:56');
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `ticket_prices` DISABLE KEYS */;
INSERT INTO `ticket_prices` (`id`, `attraction_id`, `ticket_type`, `price`, `currency`, `season`, `start_date`, `end_date`, `description`, `purchase_url`, `discount_info`, `created_at`, `updated_at`) VALUES (1,1,'成人票',60.00,'CNY','全年','2024-01-01','2024-12-31','长城成人门票','https://example.com/tickets/greatwall','学生票半价','2026-02-14 09:23:52','2026-02-14 09:23:52'),(2,1,'学生票',30.00,'CNY','全年','2024-01-01','2024-12-31','长城学生门票','https://example.com/tickets/greatwall','需持有效学生证','2026-02-14 09:23:52','2026-02-14 09:23:52'),(3,2,'成人票',80.00,'CNY','全年','2024-01-01','2024-12-31','故宫成人门票','https://example.com/tickets/forbidden_city','老人票半价','2026-02-14 09:23:52','2026-02-14 09:23:52'),(4,2,'老人票',40.00,'CNY','全年','2024-01-01','2024-12-31','故宫老人门票','https://example.com/tickets/forbidden_city','需年满60周岁','2026-02-14 09:23:52','2026-02-14 09:23:52'),(5,3,'成人票',25.50,'EUR','全年','2024-01-01','2024-12-31','埃菲尔铁塔成人门票','https://example.com/tickets/eiffel_tower','儿童票半价','2026-02-14 09:23:52','2026-02-14 09:23:52'),(6,3,'儿童票',12.70,'EUR','全年','2024-01-01','2024-12-31','埃菲尔铁塔儿童门票','https://example.com/tickets/eiffel_tower','4-11岁','2026-02-14 09:23:52','2026-02-14 09:23:52'),(7,4,'成人票',24.00,'USD','全年','2024-01-01','2024-12-31','自由女神像成人门票','https://example.com/tickets/statue_of_liberty','老人票优惠','2026-02-14 09:23:52','2026-02-14 09:23:52'),(8,4,'老人票',18.00,'USD','全年','2024-01-01','2024-12-31','自由女神像老人门票','https://example.com/tickets/statue_of_liberty','62岁以上','2026-02-14 09:23:52','2026-02-14 09:23:52'),(9,5,'成人票',900.00,'JPY','全年','2024-01-01','2024-12-31','东京塔成人门票','https://example.com/tickets/tokyo_tower','学生票优惠','2026-02-14 09:23:52','2026-02-14 09:23:52'),(10,6,'成人票',42.00,'AUD','全年','2024-01-01','2024-12-31','悉尼歌剧院成人门票','https://example.com/tickets/sydney_opera_house','家庭票优惠','2026-02-14 09:23:52','2026-02-14 09:23:52');
/*!40000 ALTER TABLE `ticket_prices` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `transportation` DISABLE KEYS */;
INSERT INTO `transportation` (`id`, `attraction_id`, `type`, `route_info`, `from_location`, `duration`, `distance`, `cost`, `currency`, `schedule`, `tips`, `is_recommended`, `sort_order`, `created_at`) VALUES (1,1,'bus','乘坐877路公交车直达八达岭长城','北京市区','约1.5小时',75.00,12.00,'CNY','06:00-19:00','建议提前30分钟到达车站，避免排队',1,1,'2026-02-14 09:23:52'),(2,1,'taxi','从北京市区打车前往','北京市区','约1小时',75.00,200.00,'CNY','全天','价格较贵，但时间灵活',0,2,'2026-02-14 09:23:52'),(3,2,'subway','乘坐地铁1号线或2号线到天安门东站，步行约10分钟','北京市区','约30分钟',5.00,3.00,'CNY','05:00-23:00','建议避开高峰期',1,1,'2026-02-14 09:23:52'),(4,2,'bus','乘坐1路、2路、52路等公交车到天安门站','北京市区','约40分钟',5.00,2.00,'CNY','06:00-22:00','站点较多，可能会堵车',0,2,'2026-02-14 09:23:52'),(5,3,'metro','乘坐地铁6号线到Bir-Hakeim站，步行约10分钟','巴黎市区','约20分钟',3.00,2.10,'EUR','05:30-00:30','最便捷的交通方式',1,1,'2026-02-14 09:23:52'),(6,3,'bus','乘坐42路、69路等公交车到Champ de Mars站','巴黎市区','约30分钟',3.00,2.10,'EUR','06:00-22:00','可以欣赏沿途风景',0,2,'2026-02-14 09:23:52'),(7,4,'ferry','从Battery Park乘坐渡轮前往自由岛','纽约市区','约25分钟',3.00,24.00,'USD','09:00-17:00','唯一的交通方式，建议提前购票',1,1,'2026-02-14 09:23:52'),(8,5,'train','乘坐JR山手线到滨松町站，换乘东京塔专线巴士','东京市区','约30分钟',5.00,200.00,'JPY','06:00-23:00','便捷且经济',1,1,'2026-02-14 09:23:52'),(9,6,'ferry','从Circular Quay乘坐渡轮前往悉尼歌剧院','悉尼市区','约10分钟',1.00,10.00,'AUD','07:00-22:00','可以欣赏悉尼港的美景',1,1,'2026-02-14 09:23:52'),(10,7,'cable car','乘坐桌山缆车上下山','开普敦市区','约10分钟',1.00,350.00,'ZAR','08:00-20:00','建议提前购票，避免排队',1,1,'2026-02-14 09:23:52');
/*!40000 ALTER TABLE `transportation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `travel_group`
--

DROP TABLE IF EXISTS `travel_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `travel_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '群组ID',
  `name` varchar(100) NOT NULL COMMENT '群组名称',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图片',
  `destination` varchar(200) DEFAULT NULL COMMENT '目的地',
  `start_date` date DEFAULT NULL COMMENT '出发日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `description` text COMMENT '群组简介',
  `max_members` int DEFAULT '20' COMMENT '人数上限',
  `is_public` tinyint(1) DEFAULT '0' COMMENT '是否公开',
  `status` tinyint DEFAULT '1' COMMENT '1-进行中 2-已结束 3-已解散',
  `created_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_destination` (`destination`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='旅行群组主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `travel_group`
--

LOCK TABLES `travel_group` WRITE;
/*!40000 ALTER TABLE `travel_group` DISABLE KEYS */;
INSERT INTO `travel_group` (`id`, `name`, `cover_url`, `destination`, `start_date`, `end_date`, `description`, `max_members`, `is_public`, `status`, `created_by`, `created_at`, `updated_at`, `deleted`) VALUES (1,'云南秘境探险小队','https://img.example.com/cover/yunnan.jpg','云南丽江-香格里拉','2026-07-01','2026-07-07','探索云南的自然风光和民族文化',8,1,1,101,'2026-06-15 10:00:00','2026-06-29 14:30:00',0),(2,'川西自驾游','https://img.example.com/cover/chuanxi.jpg','四川稻城亚丁','2026-07-10','2026-07-18','自驾穿越川西高原',6,1,1,102,'2026-06-16 09:00:00','2026-06-29 10:20:00',0),(3,'海南环岛骑行','https://img.example.com/cover/hainan.jpg','海南三亚','2026-07-05','2026-07-12','骑行环岛，感受热带风情',10,1,1,103,'2026-06-17 11:30:00','2026-06-28 16:45:00',0),(4,'西安美食文化之旅','https://img.example.com/cover/xian.jpg','陕西西安','2026-07-08','2026-07-10','深度体验古都美食与文化',5,0,1,104,'2026-06-18 08:15:00','2026-06-29 09:00:00',0),(5,'桂林山水徒步','https://img.example.com/cover/guilin.jpg','广西桂林','2026-07-15','2026-07-20','徒步漓江，欣赏山水甲天下',12,1,1,105,'2026-06-19 14:00:00','2026-06-27 20:10:00',0),(6,'新疆大环线','https://img.example.com/cover/xinjiang.jpg','新疆乌鲁木齐-喀纳斯','2026-07-20','2026-07-30','走遍北疆最美风景',8,1,1,106,'2026-06-20 07:30:00','2026-06-29 11:40:00',0),(7,'厦门文艺小清新','https://img.example.com/cover/xiamen.jpg','福建厦门','2026-06-20','2026-06-25','鼓浪屿、曾厝垵文艺之旅',4,0,2,107,'2026-06-05 16:20:00','2026-06-26 18:00:00',0),(8,'西藏朝圣之旅','https://img.example.com/cover/tibet.jpg','西藏拉萨','2026-08-01','2026-08-10','感受高原圣地的纯净',6,1,1,108,'2026-06-21 13:45:00','2026-06-28 22:15:00',0),(9,'杭州西湖休闲游','https://img.example.com/cover/hangzhou.jpg','浙江杭州','2026-06-25','2026-06-28','悠闲漫步西湖边',5,0,2,109,'2026-06-10 10:00:00','2026-06-29 08:30:00',0),(10,'张家界探险','https://img.example.com/cover/zhangjiajie.jpg','湖南张家界','2026-07-12','2026-07-17','探秘阿凡达取景地',10,1,3,110,'2026-05-20 09:00:00','2026-06-25 15:00:00',1);
/*!40000 ALTER TABLE `travel_group` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `travel_plan` DISABLE KEYS */;
INSERT INTO `travel_plan` (`id`, `user_id`, `title`, `description`, `start_date`, `end_date`, `city`, `content`, `status`, `create_time`, `update_time`) VALUES (1,1,'五一北京之行','游览北京著名景点，品尝北京美食','2024-05-01','2024-05-05','北京','Day1: 故宫博物院\nDay2: 长城\nDay3: 颐和园\nDay4: 天安门广场\nDay5: 王府井购物',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(2,2,'周末上海游','短暂的上海周末之旅','2024-04-27','2024-04-28','上海','Day1: 外滩、南京路\nDay2: 豫园、田子坊',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(3,3,'杭州西湖之旅','欣赏西湖美景，品尝杭州美食','2024-05-10','2024-05-12','杭州','Day1: 西湖游览\nDay2: 灵隐寺\nDay3: 宋城',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(4,4,'成都美食之旅','品尝成都各种美食，游览主要景点','2024-05-15','2024-05-18','成都','Day1: 大熊猫基地\nDay2: 宽窄巷子\nDay3: 锦里\nDay4: 都江堰',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(5,5,'三亚度假','享受阳光沙滩，放松身心','2024-05-20','2024-05-25','三亚','Day1: 亚龙湾\nDay2: 天涯海角\nDay3: 蜈支洲岛\nDay4: 南山寺\nDay5: 免税店购物',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(6,6,'西安文化之旅','感受古都西安的历史文化','2024-05-28','2024-06-01','西安','Day1: 兵马俑\nDay2: 华清池\nDay3: 大雁塔\nDay4: 古城墙\nDay5: 回民街',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(7,7,'厦门鼓浪屿之行','游览厦门市区和鼓浪屿','2024-06-05','2024-06-08','厦门','Day1: 厦门市区\nDay2: 鼓浪屿\nDay3: 厦门大学\nDay4: 环岛路',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(8,8,'张家界探险之旅','挑战张家界的山峰和玻璃栈道','2024-06-10','2024-06-14','张家界','Day1: 张家界国家森林公园\nDay2: 袁家界\nDay3: 天门山\nDay4: 玻璃栈道\nDay5: 黄龙洞',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(9,9,'云南大理丽江游','游览大理和丽江的古城风光','2024-06-15','2024-06-20','大理,丽江','Day1: 大理古城\nDay2: 洱海\nDay3: 丽江古城\nDay4: 玉龙雪山\nDay5: 束河古镇\nDay6: 黑龙潭公园',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(10,10,'黄山登山之旅','挑战黄山，欣赏云海日出','2024-06-25','2024-06-28','黄山','Day1: 黄山风景区\nDay2: 光明顶\nDay3: 天都峰\nDay4: 下山',1,'2026-02-14 17:21:56','2026-02-14 17:21:56'),(12,15,'啊实打实','啊伟大伟大a','2026-05-14','2026-05-22','撒大苏打','啊伟大伟大',1,'2026-05-03 10:45:41','2026-05-03 10:45:41');
/*!40000 ALTER TABLE `travel_plan` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `account`, `username`, `nickname`, `password`, `email`, `phone`, `avatar`, `gender`, `age`, `occupation`, `hobbies`, `bio`, `signature`, `experience`, `points`, `online`, `status`, `create_time`, `update_time`, `permissions`) VALUES (15,'20260219132871','爱吃薯条的小懒猪','小可爱','$2a$10$l7ZIij1Ef3Sl/jGjcv.fROPL2eaFQyM3Wb9X6vE3YiZB2qqxoBwFC','2560177364@qq.com','18213200129','http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg','1',22,'销售','打羽毛球','喜欢的羽毛球','努力奋斗','大学刚毕业',15,0,1,'2026-02-19 15:11:47','2026-04-26 18:58:11',0),(19,'20260314838329','李小明','','$2a$10$bwzjm1U1rC6bISy20mMzguSbjtUgBI/Ef14f6s3rRvVswqnJ8DA.S','2530166354@qq.com','17312900128','http://localhost:8080/upload/d333bdc6-a972-403b-8c2f-1e5da3b73edb.jpeg',NULL,NULL,'','','','','',0,0,1,'2026-03-14 23:40:17','2026-05-09 22:23:42',1),(21,'20260314207018','王涛','','$2a$10$u634T2zfipiqXyqDZbnNgeLT5aXYNkVBobg7oWYIeErq0sAAcSWc6','17241200127@qq.com','17213200129','http://localhost:8080/upload/d33f6e07-cab2-4abf-9813-0134cb28f3f8.png','1',NULL,'','','','','',0,0,1,'2026-03-14 23:55:17','2026-08-04 15:10:48',1),(22,'20260512079914','云天亦','','$2a$10$td6BZByvXp5iexePPPKi3.qMKLg4NgrMkfkqhyKh/3soVpaDfOBhe','17219277108@qq.com','18213200128','http://localhost:8080/upload/39adb7e5-e022-459d-862b-57da1244f8e3.png','',NULL,'','','','','',0,0,1,'2026-05-12 12:52:35','2026-06-05 13:51:04',1),(23,'20260605552011','王伟明',NULL,'$2a$10$l35.srYV7joHXCG9PLClFegXMNaSP7lfKcHosfunX9zTsayptDbt.','2560177824@qq.com','18213200409','http://localhost:8080/upload/c373a4e3-5f63-4585-921b-dd548be5f1d8.png',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,1,'2026-06-05 21:45:32','2026-06-05 21:45:32',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `user_activity_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_activity_log` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `user_emoji` DISABLE KEYS */;
INSERT INTO `user_emoji` (`id`, `user_id`, `emoji_url`, `emoji_name`, `sort_order`, `create_time`, `update_time`) VALUES (4,22,'http://localhost:8080/upload/3988b87e-6425-4419-94ff-cb516ee41beb.webp','5af0f72d-bad8-4c51-9072-6c0b32def4a4.webp',0,'2026-05-12 14:22:12','2026-05-12 14:22:12');
/*!40000 ALTER TABLE `user_emoji` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `user_login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_login_log` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `worldcharacteristics` DISABLE KEYS */;
INSERT INTO `worldcharacteristics` (`id`, `name`, `country`, `image`, `description`, `features`, `history`, `culture`, `tags`, `category`, `color`) VALUES (25,'长城','中国','http://localhost:8080/upload/27ddbd99-e568-4f61-aedd-08c4faea7aff.png','中国古代的军事防御工程','世界文化遗产','始建于西周时期','中国古代文化的象征','长城,历史,文化','历史建筑','#FF0000'),(26,'埃菲尔铁塔','法国','https://example.com/world2.jpg','法国巴黎的标志性建筑','世界著名建筑','建成于1889年','法国工业革命的象征','埃菲尔铁塔,法国,建筑','城市建筑','#0000FF'),(27,'金字塔','埃及','https://example.com/world3.jpg','埃及古代法老的陵墓','世界文化遗产','始建于公元前2600年','古埃及文明的象征','金字塔,埃及,历史','历史建筑','#FFFF00'),(28,'悉尼歌剧院','澳大利亚','https://example.com/world4.jpg','澳大利亚悉尼的标志性建筑','世界文化遗产','建成于1973年','现代建筑的代表','悉尼歌剧院,澳大利亚,建筑','现代建筑','#00FF00'),(29,'自由女神像','美国','https://example.com/world5.jpg','美国纽约的标志性建筑','世界文化遗产','建成于1886年','自由和民主的象征','自由女神像,美国,建筑','城市建筑','#00FFFF'),(30,'泰姬陵','印度','https://example.com/world6.jpg','印度阿格拉的标志性建筑','世界文化遗产','建成于1653年','爱情的象征','泰姬陵,印度,建筑','历史建筑','#FF00FF'),(31,'大本钟','英国','https://example.com/world7.jpg','英国伦敦的标志性建筑','世界著名建筑','建成于1859年','英国议会制度的象征','大本钟,英国,建筑','城市建筑','#FFA500'),(32,'东京塔','日本','https://example.com/world8.jpg','日本东京的标志性建筑','世界著名建筑','建成于1958年','日本战后复兴的象征','东京塔,日本,建筑','城市建筑','#800080'),(33,'圣索菲亚大教堂','土耳其','https://example.com/world9.jpg','土耳其伊斯坦布尔的标志性建筑','世界文化遗产','建成于537年','拜占庭建筑的代表','圣索菲亚大教堂,土耳其,建筑','历史建筑','#008000'),(34,'罗马斗兽场','意大利','https://example.com/world10.jpg','意大利罗马的标志性建筑','世界文化遗产','建成于80年','古罗马文明的象征','罗马斗兽场,意大利,历史','历史建筑','#800000');
/*!40000 ALTER TABLE `worldcharacteristics` ENABLE KEYS */;
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

-- Dump completed on 2026-08-04 23:13:01
