-- 测试数据
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 插入用户表数据
-- ----------------------------
INSERT INTO `user` (`account`, `username`, `password`, `email`, `phone`, `avatar`, `gender`, `age`, `occupation`, `hobbies`, `bio`, `online`, `status`) VALUES
('user001', '张三', '123456', 'zhangsan@example.com', '13800138001', 'https://example.com/avatar1.jpg', '男', 25, '工程师', '旅游,摄影,阅读', '喜欢旅行和探索新地方', 1, 1),
('user002', '李四', '123456', 'lisi@example.com', '13800138002', 'https://example.com/avatar2.jpg', '女', 28, '设计师', '绘画,音乐,美食', '热爱艺术和美食', 1, 1),
('user003', '王五', '123456', 'wangwu@example.com', '13800138003', 'https://example.com/avatar3.jpg', '男', 30, '教师', '读书,运动,旅游', '喜欢教书育人，也喜欢旅行', 0, 1),
('user004', '赵六', '123456', 'zhaoliu@example.com', '13800138004', 'https://example.com/avatar4.jpg', '女', 22, '学生', '游戏,动漫,旅游', '大学生，喜欢旅游和玩游戏', 1, 1),
('user005', '钱七', '123456', 'qianqi@example.com', '13800138005', 'https://example.com/avatar5.jpg', '男', 35, '医生', '健身,阅读,旅游', '医生，喜欢健康的生活方式', 0, 1),
('user006', '孙八', '123456', 'sunba@example.com', '13800138006', 'https://example.com/avatar6.jpg', '女', 27, '程序员', '编程,电影,旅游', '程序员，喜欢技术和旅行', 1, 1),
('user007', '周九', '123456', 'zhoujiu@example.com', '13800138007', 'https://example.com/avatar7.jpg', '男', 29, '销售', '社交,运动,旅游', '销售人员，喜欢与人交流', 0, 1),
('user008', '吴十', '123456', 'wushi@example.com', '13800138008', 'https://example.com/avatar8.jpg', '女', 24, '护士', '护理,音乐,旅游', '护士，喜欢帮助他人', 1, 1),
('user009', '郑十一', '123456', 'zhengshiyi@example.com', '13800138009', 'https://example.com/avatar9.jpg', '男', 32, '律师', '阅读,辩论,旅游', '律师，喜欢逻辑思考', 0, 1),
('user010', '王十二', '123456', 'wangshier@example.com', '13800138010', 'https://example.com/avatar10.jpg', '女', 26, '记者', '写作,摄影,旅游', '记者，喜欢记录生活', 1, 1);

-- ----------------------------
-- 插入景点表数据
-- ----------------------------
INSERT INTO `attraction` (`name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `season`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`) VALUES
('故宫博物院', '北京', '北京', '北京市东城区景山前街4号', '中国明清两代的皇家宫殿，世界上现存规模最大、保存最为完整的木质结构古建筑之一。', 'https://example.com/attraction1.jpg', '["https://example.com/attraction1_1.jpg", "https://example.com/attraction1_2.jpg"]', 60.00, '四季皆宜', 5, 1200, 800, 350, '文化遗产,历史,建筑', 1),
('长城', '北京', '北京', '北京市怀柔区', '中国古代的军事防御工程，是世界文化遗产之一。', 'https://example.com/attraction2.jpg', '["https://example.com/attraction2_1.jpg", "https://example.com/attraction2_2.jpg"]', 40.00, '春秋', 5, 1500, 900, 420, '文化遗产,历史,自然', 1),
('西湖', '杭州', '浙江', '浙江省杭州市西湖区', '中国古典园林代表作，世界文化遗产。', 'https://example.com/attraction3.jpg', '["https://example.com/attraction3_1.jpg", "https://example.com/attraction3_2.jpg"]', 0.00, '四季皆宜', 5, 1800, 1200, 500, '文化遗产,自然,湖泊', 1),
('黄山', '黄山', '安徽', '安徽省黄山市黄山区', '以奇松、怪石、云海、温泉、冬雪"五绝"著称于世。', 'https://example.com/attraction4.jpg', '["https://example.com/attraction4_1.jpg", "https://example.com/attraction4_2.jpg"]', 230.00, '春夏秋', 5, 1600, 1000, 450, '自然,山岳,云海', 1),
('九寨沟', '阿坝', '四川', '四川省阿坝藏族羌族自治州九寨沟县', '以翠海、叠瀑、彩林、雪峰、藏情、蓝冰"六绝"著称。', 'https://example.com/attraction5.jpg', '["https://example.com/attraction5_1.jpg", "https://example.com/attraction5_2.jpg"]', 220.00, '秋季', 5, 1400, 950, 400, '自然,湖泊,瀑布', 1),
('兵马俑', '西安', '陕西', '陕西省西安市临潼区秦始皇陵以东1.5公里处', '世界文化遗产，被誉为"世界第八大奇迹"。', 'https://example.com/attraction6.jpg', '["https://example.com/attraction6_1.jpg", "https://example.com/attraction6_2.jpg"]', 150.00, '四季皆宜', 5, 1300, 850, 380, '文化遗产,历史,考古', 1),
('三亚湾', '三亚', '海南', '海南省三亚市三亚湾路', '绵长的海岸线，椰梦长廊，是著名的海滨度假区。', 'https://example.com/attraction7.jpg', '["https://example.com/attraction7_1.jpg", "https://example.com/attraction7_2.jpg"]', 0.00, '冬季', 4, 1100, 750, 320, '海滨,度假,自然', 1),
('张家界', '张家界', '湖南', '湖南省张家界市武陵源区', '以张家界国家森林公园、天门山等著名景点著称。', 'https://example.com/attraction8.jpg', '["https://example.com/attraction8_1.jpg", "https://example.com/attraction8_2.jpg"]', 248.00, '春夏秋', 5, 1550, 1050, 480, '自然,山岳,森林公园', 1),
('颐和园', '北京', '北京', '北京市海淀区新建宫门路19号', '中国现存规模最大、保存最完整的皇家园林。', 'https://example.com/attraction9.jpg', '["https://example.com/attraction9_1.jpg", "https://example.com/attraction9_2.jpg"]', 30.00, '四季皆宜', 4, 900, 600, 280, '文化遗产,园林,历史', 1),
('鼓浪屿', '厦门', '福建', '福建省厦门市思明区鼓浪屿', '有着"海上花园"的美誉，是国家5A级旅游景区。', 'https://example.com/attraction10.jpg', '["https://example.com/attraction10_1.jpg", "https://example.com/attraction10_2.jpg"]', 80.00, '四季皆宜', 4, 1000, 700, 300, '海岛,历史,建筑', 1);

-- ----------------------------
-- 插入酒店表数据
-- ----------------------------
INSERT INTO `hotel` (`name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `star_level`, `rating`, `like_count`, `collect_count`, `comment_count`, `facilities`, `status`) VALUES
('北京王府井希尔顿酒店', '北京', '北京', '北京市东城区王府井东街8号', '位于王府井商业区中心，交通便利，设施豪华。', 'https://example.com/hotel1.jpg', '["https://example.com/hotel1_1.jpg", "https://example.com/hotel1_2.jpg"]', 1200.00, 5, 5, 800, 600, 250, '免费WiFi,健身房,游泳池,餐厅,停车场', 1),
('上海外滩华尔道夫酒店', '上海', '上海', '上海市黄浦区中山东一路2号', '百年历史建筑，俯瞰外滩和黄浦江美景。', 'https://example.com/hotel2.jpg', '["https://example.com/hotel2_1.jpg", "https://example.com/hotel2_2.jpg"]', 1500.00, 5, 5, 900, 700, 280, '免费WiFi,健身房,游泳池,餐厅,停车场,SPA', 1),
('杭州西子湖四季酒店', '杭州', '浙江', '浙江省杭州市西湖区龙井路5号', '毗邻西湖，环境优美，中式园林设计。', 'https://example.com/hotel3.jpg', '["https://example.com/hotel3_1.jpg", "https://example.com/hotel3_2.jpg"]', 1300.00, 5, 5, 850, 650, 260, '免费WiFi,健身房,游泳池,餐厅,停车场,SPA', 1),
('广州四季酒店', '广州', '广东', '广东省广州市天河区珠江西路5号', '位于珠江新城，现代化豪华酒店。', 'https://example.com/hotel4.jpg', '["https://example.com/hotel4_1.jpg", "https://example.com/hotel4_2.jpg"]', 1100.00, 5, 4, 750, 550, 230, '免费WiFi,健身房,游泳池,餐厅,停车场', 1),
('深圳福田香格里拉大酒店', '深圳', '广东', '广东省深圳市福田区益田路4088号', '位于福田中心区，交通便利，设施齐全。', 'https://example.com/hotel5.jpg', '["https://example.com/hotel5_1.jpg", "https://example.com/hotel5_2.jpg"]', 980.00, 5, 4, 700, 500, 210, '免费WiFi,健身房,游泳池,餐厅,停车场', 1),
('成都太古里博舍酒店', '成都', '四川', '四川省成都市锦江区笔帖式街81号', '位于太古里商业区，融合传统与现代设计。', 'https://example.com/hotel6.jpg', '["https://example.com/hotel6_1.jpg", "https://example.com/hotel6_2.jpg"]', 1080.00, 5, 5, 820, 620, 240, '免费WiFi,健身房,游泳池,餐厅,停车场', 1),
('三亚亚特兰蒂斯酒店', '三亚', '海南', '海南省三亚市海棠湾海棠北路36号', '豪华度假酒店，有水族馆和水上乐园。', 'https://example.com/hotel7.jpg', '["https://example.com/hotel7_1.jpg", "https://example.com/hotel7_2.jpg"]', 1800.00, 5, 5, 950, 750, 300, '免费WiFi,健身房,游泳池,餐厅,停车场,水上乐园', 1),
('厦门鼓浪屿海景酒店', '厦门', '福建', '福建省厦门市思明区鼓浪屿', '岛上的精品酒店，环境清幽，海景优美。', 'https://example.com/hotel8.jpg', '["https://example.com/hotel8_1.jpg", "https://example.com/hotel8_2.jpg"]', 680.00, 4, 4, 600, 400, 180, '免费WiFi,餐厅,停车场', 1),
('西安香格里拉大酒店', '西安', '陕西', '陕西省西安市雁塔区科技路38号乙', '位于高新区，靠近大雁塔等景点。', 'https://example.com/hotel9.jpg', '["https://example.com/hotel9_1.jpg", "https://example.com/hotel9_2.jpg"]', 880.00, 5, 4, 650, 450, 200, '免费WiFi,健身房,游泳池,餐厅,停车场', 1),
('南京金陵饭店', '南京', '江苏', '江苏省南京市鼓楼区汉中路2号', '南京地标性建筑，历史悠久，服务优质。', 'https://example.com/hotel10.jpg', '["https://example.com/hotel10_1.jpg", "https://example.com/hotel10_2.jpg"]', 780.00, 5, 4, 620, 420, 190, '免费WiFi,健身房,游泳池,餐厅,停车场', 1);

-- ----------------------------
-- 插入美食表数据
-- ----------------------------
INSERT INTO `food` (`name`, `city`, `province`, `address`, `description`, `cover_image`, `images`, `price`, `cuisine_type`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`) VALUES
('北京烤鸭', '北京', '北京', '北京市东城区前门大街30号', '北京传统名菜，外酥里嫩，香气四溢。', 'https://example.com/food1.jpg', '["https://example.com/food1_1.jpg", "https://example.com/food1_2.jpg"]', 268.00, '京菜', 5, 1200, 800, 400, '传统,烤鸭,必吃', 1),
('上海小笼包', '上海', '上海', '上海市黄浦区豫园路85号', '上海传统点心，皮薄馅大，汤汁丰富。', 'https://example.com/food2.jpg', '["https://example.com/food2_1.jpg", "https://example.com/food2_2.jpg"]', 38.00, '沪菜', 5, 1000, 700, 350, '传统,点心,必吃', 1),
('杭州西湖醋鱼', '杭州', '浙江', '浙江省杭州市西湖区楼外楼', '杭州传统名菜，鱼肉鲜嫩，酸甜可口。', 'https://example.com/food3.jpg', '["https://example.com/food3_1.jpg", "https://example.com/food3_2.jpg"]', 88.00, '浙菜', 4, 800, 500, 280, '传统,西湖,必吃', 1),
('四川火锅', '成都', '四川', '四川省成都市锦江区春熙路', '四川特色美食，麻辣鲜香，回味无穷。', 'https://example.com/food4.jpg', '["https://example.com/food4_1.jpg", "https://example.com/food4_2.jpg"]', 128.00, '川菜', 5, 1500, 1000, 500, '麻辣,火锅,必吃', 1),
('广东早茶', '广州', '广东', '广东省广州市越秀区上下九', '广东传统饮食文化，品种丰富，精致美味。', 'https://example.com/food5.jpg', '["https://example.com/food5_1.jpg", "https://example.com/food5_2.jpg"]', 68.00, '粤菜', 5, 900, 600, 320, '传统,点心,早茶', 1),
('云南过桥米线', '昆明', '云南', '云南省昆明市五华区正义路', '云南特色美食，汤鲜料足，风味独特。', 'https://example.com/food6.jpg', '["https://example.com/food6_1.jpg", "https://example.com/food6_2.jpg"]', 45.00, '滇菜', 4, 700, 400, 250, '特色,米线,必吃', 1),
('西安肉夹馍', '西安', '陕西', '陕西省西安市莲湖区回民街', '西安传统小吃，馍香肉嫩，肥而不腻。', 'https://example.com/food7.jpg', '["https://example.com/food7_1.jpg", "https://example.com/food7_2.jpg"]', 15.00, '陕菜', 5, 1100, 800, 380, '传统,小吃,必吃', 1),
('湖南臭豆腐', '长沙', '湖南', '湖南省长沙市芙蓉区黄兴南路', '湖南特色小吃，闻起来臭，吃起来香。', 'https://example.com/food8.jpg', '["https://example.com/food8_1.jpg", "https://example.com/food8_2.jpg"]', 10.00, '湘菜', 4, 600, 300, 200, '特色,小吃,臭豆腐', 1),
('福建沙县小吃', '福州', '福建', '福建省福州市鼓楼区八一七路', '福建传统小吃，品种丰富，价格实惠。', 'https://example.com/food9.jpg', '["https://example.com/food9_1.jpg", "https://example.com/food9_2.jpg"]', 25.00, '闽菜', 4, 500, 250, 180, '小吃,实惠,传统', 1),
('新疆大盘鸡', '乌鲁木齐', '新疆', '新疆维吾尔自治区乌鲁木齐市天山区', '新疆特色美食，鸡肉鲜嫩，土豆软糯，辣味十足。', 'https://example.com/food10.jpg', '["https://example.com/food10_1.jpg", "https://example.com/food10_2.jpg"]', 98.00, '新疆菜', 5, 850, 550, 300, '特色,大盘鸡,必吃', 1);

-- ----------------------------
-- 插入新闻资讯表数据
-- ----------------------------
INSERT INTO `news` (`title`, `content`, `cover_image`, `source`, `view_count`, `like_count`, `collect_count`, `comment_count`, `status`) VALUES
('2024年五一假期旅游市场火爆，国内游人次突破2亿', '据文化和旅游部数据中心测算，2024年五一假期全国国内旅游出游人次达到2.1亿，同比增长120%，旅游收入达到1200亿元，同比增长150%。', 'https://example.com/news1.jpg', '人民日报', 50000, 2000, 1500, 800, 1),
('故宫博物院推出"清明上河图3.0"数字艺术展', '故宫博物院将于5月1日起推出"清明上河图3.0"数字艺术展，通过现代科技手段重现北宋都城汴京的繁华景象。', 'https://example.com/news2.jpg', '新华社', 35000, 1500, 1200, 600, 1),
('三亚国际旅游岛建设取得新进展，免税购物销售额突破500亿', '三亚国际旅游岛建设近年来取得显著成效，2024年第一季度免税购物销售额达到520亿元，同比增长35%。', 'https://example.com/news3.jpg', '海南日报', 28000, 1200, 900, 450, 1),
('2024年中国国际旅游交易会在上海开幕', '为期三天的2024年中国国际旅游交易会在上海新国际博览中心开幕，来自全球50多个国家和地区的旅游机构参展。', 'https://example.com/news4.jpg', '上海日报', 25000, 1000, 800, 400, 1),
('中国高铁旅游线路成为热门选择，"高铁游"成新趋势', '随着高铁网络的不断完善，"高铁游"成为越来越多游客的选择。2024年第一季度，高铁沿线城市旅游收入同比增长45%。', 'https://example.com/news5.jpg', '经济日报', 32000, 1300, 1000, 550, 1),
('九寨沟景区将于4月1日起恢复全域开放', '经过两年的生态修复，九寨沟景区将于2024年4月1日起恢复全域开放，开放区域包括之前因地震关闭的部分景点。', 'https://example.com/news6.jpg', '四川日报', 40000, 1800, 1300, 700, 1),
('2024年中国旅游日主题确定为"美好中国，幸福旅程"', '文化和旅游部宣布，2024年中国旅游日主题为"美好中国，幸福旅程"，全国各地将推出多项旅游惠民措施。', 'https://example.com/news7.jpg', '文旅部官网', 22000, 900, 700, 350, 1),
('北京冬奥会场馆成为热门旅游景点，年接待游客突破1000万', '北京冬奥会结束后，冬奥会场馆成为热门旅游景点。2023年，冬奥会场馆群共接待游客1050万人次，旅游收入达到80亿元。', 'https://example.com/news8.jpg', '北京日报', 38000, 1600, 1100, 650, 1),
('全国首个"智慧旅游城市"试点在杭州启动', '杭州市成为全国首个"智慧旅游城市"试点，通过大数据、人工智能等技术，为游客提供更加便捷、个性化的旅游服务。', 'https://example.com/news9.jpg', '科技日报', 26000, 1100, 850, 420, 1),
('中国出境游市场逐步恢复，2024年第一季度出境游人次同比增长200%', '随着全球疫情防控形势的好转，中国出境游市场逐步恢复。2024年第一季度，中国公民出境游人次达到1200万，同比增长200%。', 'https://example.com/news10.jpg', '环球时报', 45000, 1900, 1400, 750, 1);

-- ----------------------------
-- 插入特产/纪念品表数据
-- ----------------------------
INSERT INTO `product` (`name`, `type`, `city`, `province`, `description`, `cover_image`, `images`, `price`, `stock`, `rating`, `like_count`, `collect_count`, `comment_count`, `tags`, `status`) VALUES
('北京烤鸭礼盒', '食品', '北京', '北京', '真空包装的北京烤鸭，方便携带，味道正宗。', 'https://example.com/product1.jpg', '["https://example.com/product1_1.jpg", "https://example.com/product1_2.jpg"]', 198.00, 500, 5, 600, 400, 200, '北京特产,食品,烤鸭', 1),
('上海雪花膏', '化妆品', '上海', '上海', '上海传统化妆品，滋润保湿，香气宜人。', 'https://example.com/product2.jpg', '["https://example.com/product2_1.jpg", "https://example.com/product2_2.jpg"]', 68.00, 1000, 4, 400, 300, 150, '上海特产,化妆品,传统', 1),
('杭州西湖龙井茶叶', '食品', '杭州', '浙江', '西湖龙井是中国十大名茶之一，香气清高，滋味鲜爽。', 'https://example.com/product3.jpg', '["https://example.com/product3_1.jpg", "https://example.com/product3_2.jpg"]', 298.00, 300, 5, 700, 500, 250, '杭州特产,茶叶,龙井', 1),
('四川大熊猫玩偶', '工艺品', '成都', '四川', '可爱的大熊猫玩偶，是四川的标志性纪念品。', 'https://example.com/product4.jpg', '["https://example.com/product4_1.jpg", "https://example.com/product4_2.jpg"]', 88.00, 800, 5, 500, 400, 180, '四川特产,工艺品,熊猫', 1),
('广东广绣', '工艺品', '广州', '广东', '广绣是中国四大名绣之一，图案精美，工艺精湛。', 'https://example.com/product5.jpg', '["https://example.com/product5_1.jpg", "https://example.com/product5_2.jpg"]', 398.00, 200, 5, 450, 350, 160, '广东特产,工艺品,刺绣', 1),
('云南普洱茶', '食品', '昆明', '云南', '普洱茶是云南特产，具有降脂减肥、降压、抗动脉硬化等功效。', 'https://example.com/product6.jpg', '["https://example.com/product6_1.jpg", "https://example.com/product6_2.jpg"]', 368.00, 400, 5, 650, 450, 220, '云南特产,茶叶,普洱', 1),
('西安兵马俑模型', '工艺品', '西安', '陕西', '兵马俑模型是西安的特色纪念品，做工精细，造型逼真。', 'https://example.com/product7.jpg', '["https://example.com/product7_1.jpg", "https://example.com/product7_2.jpg"]', 128.00, 600, 4, 350, 250, 130, '西安特产,工艺品,兵马俑', 1),
('湖南湘绣', '工艺品', '长沙', '湖南', '湘绣是中国四大名绣之一，以其精湛的刺绣技艺和独特的艺术风格著称。', 'https://example.com/product8.jpg', '["https://example.com/product8_1.jpg", "https://example.com/product8_2.jpg"]', 328.00, 250, 4, 300, 200, 110, '湖南特产,工艺品,刺绣', 1),
('福建安溪铁观音', '食品', '泉州', '福建', '铁观音是中国十大名茶之一，香气馥郁持久，滋味醇厚甘鲜。', 'https://example.com/product9.jpg', '["https://example.com/product9_1.jpg", "https://example.com/product9_2.jpg"]', 268.00, 350, 5, 550, 350, 190, '福建特产,茶叶,铁观音', 1),
('新疆和田玉饰品', '珠宝', '乌鲁木齐', '新疆', '和田玉是中国四大名玉之一，质地温润，色泽优美。', 'https://example.com/product10.jpg', '["https://example.com/product10_1.jpg", "https://example.com/product10_2.jpg"]', 888.00, 150, 5, 400, 300, 140, '新疆特产,珠宝,和田玉', 1);

-- ----------------------------
-- 插入票务表数据
-- ----------------------------
INSERT INTO `ticket` (`type`, `departure_city`, `arrival_city`, `departure_time`, `arrival_time`, `carrier`, `price`, `stock`, `seat_class`, `status`) VALUES
('train', '北京', '上海', '08:00', '13:00', '中国铁路', 553.00, 100, '二等座', 1),
('train', '上海', '北京', '14:00', '19:00', '中国铁路', 553.00, 100, '二等座', 1),
('flight', '北京', '广州', '09:30', '12:30', '中国国航', 1200.00, 50, '经济舱', 1),
('flight', '广州', '北京', '13:30', '16:30', '中国国航', 1200.00, 50, '经济舱', 1),
('train', '北京', '西安', '07:30', '12:30', '中国铁路', 515.00, 80, '二等座', 1),
('train', '西安', '北京', '13:30', '18:30', '中国铁路', 515.00, 80, '二等座', 1),
('flight', '上海', '成都', '10:00', '13:00', '东方航空', 1000.00, 40, '经济舱', 1),
('flight', '成都', '上海', '14:00', '17:00', '东方航空', 1000.00, 40, '经济舱', 1),
('taxi', '北京', '北京', '全天', '全天', '首汽约车', 2.30, 1000, '普通车', 1),
('taxi', '上海', '上海', '全天', '全天', '滴滴出行', 2.60, 1000, '普通车', 1);

-- ----------------------------
-- 插入跟团游表数据
-- ----------------------------
INSERT INTO `group_travel` (`creator_id`, `name`, `description`, `destination`, `departure_time`, `return_time`, `max_members`, `current_members`, `price`, `images`, `status`) VALUES
(1, '北京5日经典游', '游览北京主要景点，包括故宫、长城、颐和园等。', '北京', '2024-05-01 08:00:00', '2024-05-05 18:00:00', 30, 25, 2800.00, '["https://example.com/group1_1.jpg", "https://example.com/group1_2.jpg"]', 1),
(2, '上海+杭州4日游', '游览上海外滩、杭州西湖等著名景点。', '上海,杭州', '2024-05-02 09:00:00', '2024-05-05 17:00:00', 25, 20, 2200.00, '["https://example.com/group2_1.jpg", "https://example.com/group2_2.jpg"]', 1),
(3, '成都+九寨沟5日游', '游览成都大熊猫基地、九寨沟等景点。', '成都,九寨沟', '2024-05-03 07:30:00', '2024-05-07 19:00:00', 20, 15, 3500.00, '["https://example.com/group3_1.jpg", "https://example.com/group3_2.jpg"]', 1),
(4, '三亚3日度假游', '享受三亚阳光沙滩，游览天涯海角等景点。', '三亚', '2024-05-04 10:00:00', '2024-05-06 16:00:00', 35, 30, 2900.00, '["https://example.com/group4_1.jpg", "https://example.com/group4_2.jpg"]', 1),
(5, '西安4日文化游', '游览兵马俑、华清池等历史文化景点。', '西安', '2024-05-05 08:30:00', '2024-05-08 18:30:00', 28, 22, 2500.00, '["https://example.com/group5_1.jpg", "https://example.com/group5_2.jpg"]', 1),
(6, '云南大理+丽江6日游', '游览大理古城、丽江古城等景点。', '大理,丽江', '2024-05-06 07:00:00', '2024-05-11 20:00:00', 22, 18, 3800.00, '["https://example.com/group6_1.jpg", "https://example.com/group6_2.jpg"]', 1),
(7, '厦门+鼓浪屿3日游', '游览厦门市区、鼓浪屿等景点。', '厦门,鼓浪屿', '2024-05-07 09:30:00', '2024-05-09 17:30:00', 30, 26, 1900.00, '["https://example.com/group7_1.jpg", "https://example.com/group7_2.jpg"]', 1),
(8, '张家界4日游', '游览张家界国家森林公园、天门山等景点。', '张家界', '2024-05-08 08:00:00', '2024-05-11 18:00:00', 26, 20, 3200.00, '["https://example.com/group8_1.jpg", "https://example.com/group8_2.jpg"]', 1),
(9, '广州+深圳4日游', '游览广州塔、深圳世界之窗等景点。', '广州,深圳', '2024-05-09 09:00:00', '2024-05-12 17:00:00', 24, 19, 2100.00, '["https://example.com/group9_1.jpg", "https://example.com/group9_2.jpg"]', 1),
(10, '黄山3日游', '游览黄山风景区，欣赏奇松怪石云海。', '黄山', '2024-05-10 07:30:00', '2024-05-12 18:30:00', 20, 16, 2800.00, '["https://example.com/group10_1.jpg", "https://example.com/group10_2.jpg"]', 1);

-- ----------------------------
-- 插入出行计划表数据
-- ----------------------------
INSERT INTO `travel_plan` (`user_id`, `title`, `description`, `start_date`, `end_date`, `city`, `content`, `status`) VALUES
(1, '五一北京之行', '游览北京著名景点，品尝北京美食', '2024-05-01', '2024-05-05', '北京', 'Day1: 故宫博物院\nDay2: 长城\nDay3: 颐和园\nDay4: 天安门广场\nDay5: 王府井购物', 1),
(2, '周末上海游', '短暂的上海周末之旅', '2024-04-27', '2024-04-28', '上海', 'Day1: 外滩、南京路\nDay2: 豫园、田子坊', 1),
(3, '杭州西湖之旅', '欣赏西湖美景，品尝杭州美食', '2024-05-10', '2024-05-12', '杭州', 'Day1: 西湖游览\nDay2: 灵隐寺\nDay3: 宋城', 1),
(4, '成都美食之旅', '品尝成都各种美食，游览主要景点', '2024-05-15', '2024-05-18', '成都', 'Day1: 大熊猫基地\nDay2: 宽窄巷子\nDay3: 锦里\nDay4: 都江堰', 1),
(5, '三亚度假', '享受阳光沙滩，放松身心', '2024-05-20', '2024-05-25', '三亚', 'Day1: 亚龙湾\nDay2: 天涯海角\nDay3: 蜈支洲岛\nDay4: 南山寺\nDay5: 免税店购物', 1),
(6, '西安文化之旅', '感受古都西安的历史文化', '2024-05-28', '2024-06-01', '西安', 'Day1: 兵马俑\nDay2: 华清池\nDay3: 大雁塔\nDay4: 古城墙\nDay5: 回民街', 1),
(7, '厦门鼓浪屿之行', '游览厦门市区和鼓浪屿', '2024-06-05', '2024-06-08', '厦门', 'Day1: 厦门市区\nDay2: 鼓浪屿\nDay3: 厦门大学\nDay4: 环岛路', 1),
(8, '张家界探险之旅', '挑战张家界的山峰和玻璃栈道', '2024-06-10', '2024-06-14', '张家界', 'Day1: 张家界国家森林公园\nDay2: 袁家界\nDay3: 天门山\nDay4: 玻璃栈道\nDay5: 黄龙洞', 1),
(9, '云南大理丽江游', '游览大理和丽江的古城风光', '2024-06-15', '2024-06-20', '大理,丽江', 'Day1: 大理古城\nDay2: 洱海\nDay3: 丽江古城\nDay4: 玉龙雪山\nDay5: 束河古镇\nDay6: 黑龙潭公园', 1),
(10, '黄山登山之旅', '挑战黄山，欣赏云海日出', '2024-06-25', '2024-06-28', '黄山', 'Day1: 黄山风景区\nDay2: 光明顶\nDay3: 天都峰\nDay4: 下山', 1);

-- ----------------------------
-- 插入社区帖子表数据
-- ----------------------------
INSERT INTO `community_post` (`user_id`, `title`, `content`, `images`, `videos`, `like_count`, `comment_count`, `collect_count`, `status`) VALUES
(1, '北京之行归来，分享一些照片', '五一假期去了北京，游览了故宫、长城等景点，人真的很多，但是景色很美。分享一些照片给大家。', '["https://example.com/post1_1.jpg", "https://example.com/post1_2.jpg"]', NULL, 250, 80, 120, 1),
(2, '上海美食推荐', '作为一个上海本地人，给大家推荐一些地道的上海美食，包括小笼包、生煎包、白斩鸡等。', '["https://example.com/post2_1.jpg", "https://example.com/post2_2.jpg"]', NULL, 180, 60, 90, 1),
(3, '杭州西湖一日游攻略', '整理了一份西湖一日游的详细攻略，包括最佳游览路线、必看景点、美食推荐等。', '["https://example.com/post3_1.jpg", "https://example.com/post3_2.jpg"]', NULL, 220, 70, 100, 1),
(4, '成都大熊猫基地游玩体验', '去了成都大熊猫基地，看到了很多可爱的大熊猫，分享一些照片和游玩体验。', '["https://example.com/post4_1.jpg", "https://example.com/post4_2.jpg"]', NULL, 300, 90, 150, 1),
(5, '三亚海滩度假心得', '在三亚度过了一个愉快的假期，分享一些海滩度假的心得和照片。', '["https://example.com/post5_1.jpg", "https://example.com/post5_2.jpg"]', NULL, 280, 85, 130, 1),
(6, '西安兵马俑震撼之旅', '参观了秦始皇兵马俑，被其规模和工艺所震撼，分享一些照片和感受。', '["https://example.com/post6_1.jpg", "https://example.com/post6_2.jpg"]', NULL, 200, 65, 85, 1),
(7, '厦门鼓浪屿的慢生活', '在鼓浪屿度过了两天慢生活，感受了岛上的文艺气息，分享一些照片和体验。', '["https://example.com/post7_1.jpg", "https://example.com/post7_2.jpg"]', NULL, 190, 62, 88, 1),
(8, '张家界天门山玻璃栈道体验', '挑战了天门山的玻璃栈道，虽然有点害怕，但是风景真的很美，分享一些照片和体验。', '["https://example.com/post8_1.jpg", "https://example.com/post8_2.jpg"]', NULL, 230, 75, 110, 1),
(9, '云南大理洱海骑行', '在大理洱海骑行，沿途风景如画，分享一些照片和骑行路线推荐。', '["https://example.com/post9_1.jpg", "https://example.com/post9_2.jpg"]', NULL, 210, 68, 95, 1),
(10, '黄山云海日出绝美景色', '在黄山山顶等待日出，看到了壮观的云海日出，分享一些照片和拍摄技巧。', '["https://example.com/post10_1.jpg", "https://example.com/post10_2.jpg"]', NULL, 320, 95, 160, 1);

-- ----------------------------
-- 插入评论表数据
-- ----------------------------
INSERT INTO `comment` (`user_id`, `item_type`, `item_id`, `parent_id`, `content`, `images`, `rating`, `like_count`, `status`) VALUES
(1, 'attraction', 1, 0, '故宫真的很震撼，历史感很强，值得一去。', '["https://example.com/comment1_1.jpg"]', 5, 30, 1),
(2, 'attraction', 1, 0, '人太多了，建议早点去，避开高峰期。', NULL, 4, 15, 1),
(3, 'hotel', 1, 0, '酒店位置很好，服务态度也不错，下次还会选择。', NULL, 5, 20, 1),
(4, 'hotel', 1, 0, '房间很干净，设施齐全，就是价格有点贵。', NULL, 4, 10, 1),
(5, 'food', 1, 0, '北京烤鸭真的很正宗，皮脆肉嫩，推荐大家尝试。', '["https://example.com/comment5_1.jpg"]', 5, 25, 1),
(6, 'food', 1, 0, '价格有点贵，但是味道确实不错。', NULL, 4, 8, 1),
(7, 'product', 1, 0, '包装很好，味道也很正宗，送礼很合适。', NULL, 5, 15, 1),
(8, 'product', 1, 0, '真空包装的，方便携带，但是没有现做的好吃。', NULL, 4, 5, 1),
(9, 'news', 1, 0, '五一确实人很多，但是旅游体验还是不错的。', NULL, NULL, 12, 1),
(10, 'community', 1, 0, '照片拍得很好，下次有机会也要去北京看看。', NULL, NULL, 8, 1);

-- ----------------------------
-- 插入点赞记录表数据
-- ----------------------------
INSERT INTO `like_record` (`user_id`, `item_type`, `item_id`) VALUES
(1, 'attraction', 1),
(1, 'hotel', 1),
(1, 'food', 1),
(2, 'attraction', 2),
(2, 'hotel', 2),
(2, 'food', 2),
(3, 'attraction', 3),
(3, 'hotel', 3),
(3, 'food', 3),
(4, 'attraction', 4);

-- ----------------------------
-- 插入收藏表数据
-- ----------------------------
INSERT INTO `collection` (`user_id`, `item_type`, `item_id`) VALUES
(1, 'attraction', 1),
(1, 'hotel', 1),
(1, 'food', 1),
(2, 'attraction', 2),
(2, 'hotel', 2),
(2, 'food', 2),
(3, 'attraction', 3),
(3, 'hotel', 3),
(3, 'food', 3),
(4, 'attraction', 4);

-- ----------------------------
-- 插入购物车表数据
-- ----------------------------
INSERT INTO `cart` (`user_id`, `item_type`, `item_id`, `item_name`, `price`, `quantity`) VALUES
(1, 'product', 1, '北京烤鸭礼盒', 198.00, 2),
(1, 'ticket', 1, '北京-上海火车票', 553.00, 1),
(2, 'product', 2, '上海雪花膏', 68.00, 3),
(2, 'ticket', 2, '上海-北京火车票', 553.00, 1),
(3, 'product', 3, '杭州西湖龙井茶叶', 298.00, 1),
(3, 'ticket', 3, '北京-广州机票', 1200.00, 1),
(4, 'product', 4, '四川大熊猫玩偶', 88.00, 2),
(4, 'ticket', 4, '广州-北京机票', 1200.00, 1),
(5, 'product', 5, '广东广绣', 398.00, 1),
(5, 'ticket', 5, '北京-西安火车票', 515.00, 1);

-- ----------------------------
-- 插入订单表数据
-- ----------------------------
INSERT INTO `orders` (`user_id`, `order_no`, `item_type`, `item_id`, `item_name`, `amount`, `quantity`, `remark`, `status`) VALUES
(1, '202405010001', 'product', 1, '北京烤鸭礼盒', 396.00, 2, '尽快发货', '3'),
(1, '202405010002', 'ticket', 1, '北京-上海火车票', 553.00, 1, '无', '3'),
(2, '202405010003', 'product', 2, '上海雪花膏', 204.00, 3, '送朋友', '3'),
(2, '202405010004', 'ticket', 2, '上海-北京火车票', 553.00, 1, '无', '3'),
(3, '202405010005', 'product', 3, '杭州西湖龙井茶叶', 298.00, 1, '自己喝', '3'),
(3, '202405010006', 'ticket', 3, '北京-广州机票', 1200.00, 1, '无', '3'),
(4, '202405010007', 'product', 4, '四川大熊猫玩偶', 176.00, 2, '送孩子', '3'),
(4, '202405010008', 'ticket', 4, '广州-北京机票', 1200.00, 1, '无', '3'),
(5, '202405010009', 'product', 5, '广东广绣', 398.00, 1, '收藏', '3'),
(5, '202405010010', 'ticket', 5, '北京-西安火车票', 515.00, 1, '无', '3');

-- ----------------------------
-- 插入支付表数据
-- ----------------------------
INSERT INTO `payment` (`order_id`, `user_id`, `payment_no`, `amount`, `payment_method`, `status`) VALUES
(1, 1, 'P202405010001', 396.00, 'alipay', '1'),
(2, 1, 'P202405010002', 553.00, 'wechat', '1'),
(3, 2, 'P202405010003', 204.00, 'alipay', '1'),
(4, 2, 'P202405010004', 553.00, 'wechat', '1'),
(5, 3, 'P202405010005', 298.00, 'alipay', '1'),
(6, 3, 'P202405010006', 1200.00, 'wechat', '1'),
(7, 4, 'P202405010007', 176.00, 'alipay', '1'),
(8, 4, 'P202405010008', 1200.00, 'wechat', '1'),
(9, 5, 'P202405010009', 398.00, 'alipay', '1'),
(10, 5, 'P202405010010', 515.00, 'wechat', '1');

-- ----------------------------
-- 插入聊天消息表数据
-- ----------------------------
INSERT INTO `chat_message` (`sender_id`, `receiver_id`, `content`, `message_type`, `is_read`) VALUES
(1, 2, '你好，最近怎么样？', 'text', 1),
(2, 1, '挺好的，谢谢关心。', 'text', 1),
(1, 2, '五一假期打算去哪里玩？', 'text', 1),
(2, 1, '打算去上海玩几天，你呢？', 'text', 1),
(1, 2, '我打算去北京。', 'text', 1),
(3, 4, '你好，看到你发的旅游照片，拍得真好看！', 'text', 1),
(4, 3, '谢谢夸奖，你也喜欢旅游吗？', 'text', 1),
(3, 4, '是的，很喜欢，但是平时工作忙，没什么时间。', 'text', 1),
(4, 3, '可以利用假期出去走走，放松一下。', 'text', 1),
(3, 4, '好的，谢谢你的建议。', 'text', 1);

-- ----------------------------
-- 插入好友表数据
-- ----------------------------
INSERT INTO `friend` (`user_id`, `friend_id`, `remark`, `status`) VALUES
(1, 2, '李四', 1),
(2, 1, '张三', 1),
(1, 3, '王五', 1),
(3, 1, '张三', 1),
(2, 3, '王五', 1),
(3, 2, '李四', 1),
(4, 5, '钱七', 1),
(5, 4, '赵六', 1),
(6, 7, '周九', 1),
(7, 6, '孙八', 1);

-- ----------------------------
-- 插入文件信息表数据
-- ----------------------------
INSERT INTO `file_info` (`file_name`, `original_name`, `file_path`, `file_type`, `file_size`, `chunk_index`, `total_chunks`, `file_md5`, `status`) VALUES
('avatar1.jpg', 'avatar1.jpg', '/uploads/avatar1.jpg', 'image/jpeg', 1024000, 0, 1, 'e10adc3949ba59abbe56e057f20f883e', 1),
('avatar2.jpg', 'avatar2.jpg', '/uploads/avatar2.jpg', 'image/jpeg', 1234567, 0, 1, 'c81e728d9d4c2f636f067f89cc14862c', 1),
('avatar3.jpg', 'avatar3.jpg', '/uploads/avatar3.jpg', 'image/jpeg', 987654, 0, 1, 'a87ff679a2f3e71d9181a67b7542122c', 1),
('attraction1.jpg', 'attraction1.jpg', '/uploads/attraction1.jpg', 'image/jpeg', 2048000, 0, 1, 'eccbc87e4b5ce2fe28308fd9f2a7baf3', 1),
('attraction2.jpg', 'attraction2.jpg', '/uploads/attraction2.jpg', 'image/jpeg', 2560000, 0, 1, 'c4ca4238a0b923820dcc509a6f75849b', 1),
('hotel1.jpg', 'hotel1.jpg', '/uploads/hotel1.jpg', 'image/jpeg', 1800000, 0, 1, 'c81e728d9d4c2f636f067f89cc14862c', 1),
('hotel2.jpg', 'hotel2.jpg', '/uploads/hotel2.jpg', 'image/jpeg', 1900000, 0, 1, 'eccbc87e4b5ce2fe28308fd9f2a7baf3', 1),
('food1.jpg', 'food1.jpg', '/uploads/food1.jpg', 'image/jpeg', 1500000, 0, 1, 'a87ff679a2f3e71d9181a67b7542122c', 1),
('food2.jpg', 'food2.jpg', '/uploads/food2.jpg', 'image/jpeg', 1600000, 0, 1, 'c4ca4238a0b923820dcc509a6f75849b', 1),
('product1.jpg', 'product1.jpg', '/uploads/product1.jpg', 'image/jpeg', 1700000, 0, 1, 'c81e728d9d4c2f636f067f89cc14862c', 1);

-- ----------------------------
-- 插入大洲表数据
-- ----------------------------
INSERT INTO `continents` (`name`, `chinese_name`, `area`, `population`, `description`) VALUES
('Asia', '亚洲', 4457.9, 4541668, '亚洲是世界上面积最大、人口最多的大洲，拥有丰富的文化和历史遗产。'),
('Europe', '欧洲', 1016.0, 747636, '欧洲是世界上发达国家最集中的大洲，拥有悠久的历史和灿烂的文化。'),
('North America', '北美洲', 2470.9, 579024, '北美洲包括美国、加拿大等国家，经济发达，科技先进。'),
('South America', '南美洲', 1784.0, 434189, '南美洲拥有丰富的自然资源和多样的生态系统，亚马逊雨林位于此处。'),
('Africa', '非洲', 3037.0, 1340598, '非洲是人类文明的发源地之一，拥有多样的文化和自然景观。'),
('Oceania', '大洋洲', 867.0, 41261, '大洋洲由众多岛屿组成，澳大利亚是其最大的国家。'),
('Antarctica', '南极洲', 1397.0, 0, '南极洲是世界上最寒冷的大洲，也是唯一没有永久居民的大洲。');

-- ----------------------------
-- 插入国家表数据
-- ----------------------------
INSERT INTO `countries` (`name`, `chinese_name`, `continent_id`, `capital`, `area`, `population`, `currency`, `language`, `timezone`, `country_code`, `phone_code`, `flag_emoji`, `description`) VALUES
('China', '中国', 1, '北京', 960.0, 1412600, 'CNY', 'Chinese', 'UTC+8', 'CN', '86', '🇨🇳', '中国是世界上人口最多的国家，拥有悠久的历史和灿烂的文化。'),
('Japan', '日本', 1, '东京', 37.8, 125836, 'JPY', 'Japanese', 'UTC+9', 'JP', '81', '🇯🇵', '日本是一个岛国，拥有独特的文化和先进的科技。'),
('United States', '美国', 3, '华盛顿', 937.3, 331900, 'USD', 'English', 'UTC-5 to UTC-10', 'US', '1', '🇺🇸', '美国是世界上最强大的国家之一，经济和科技发达。'),
('France', '法国', 2, '巴黎', 55.1, 67750, 'EUR', 'French', 'UTC+1', 'FR', '33', '🇫🇷', '法国是欧洲的重要国家，以浪漫的文化和美食著称。'),
('Australia', '澳大利亚', 6, '堪培拉', 769.2, 25739, 'AUD', 'English', 'UTC+8 to UTC+11', 'AU', '61', '🇦🇺', '澳大利亚是世界上面积第六大的国家，拥有独特的自然景观。'),
('South Africa', '南非', 5, '比勒陀利亚', 122.1, 59390, 'ZAR', 'Afrikaans, English', 'UTC+2', 'ZA', '27', '🇿🇦', '南非是非洲最发达的国家之一，拥有丰富的自然资源。'),
('Brazil', '巴西', 4, '巴西利亚', 851.6, 213993, 'BRL', 'Portuguese', 'UTC-2 to UTC-5', 'BR', '55', '🇧🇷', '巴西是南美洲最大的国家，拥有世界上最大的热带雨林。'),
('Germany', '德国', 2, '柏林', 35.7, 83240, 'EUR', 'German', 'UTC+1', 'DE', '49', '🇩🇪', '德国是欧洲经济最强大的国家之一，以精密制造著称。'),
('India', '印度', 1, '新德里', 328.7, 1380004, 'INR', 'Hindi, English', 'UTC+5:30', 'IN', '91', '🇮🇳', '印度是世界上人口第二多的国家，拥有多样的文化。'),
('Canada', '加拿大', 3, '渥太华', 998.5, 38246, 'CAD', 'English, French', 'UTC-3:30 to UTC-8', 'CA', '1', '🇨🇦', '加拿大是世界上面积第二大的国家，自然资源丰富。');

-- ----------------------------
-- 插入城市表数据
-- ----------------------------
INSERT INTO `cities` (`name`, `chinese_name`, `country_id`, `is_capital`, `area`, `population`, `timezone`, `description`, `latitude`, `longitude`, `famous_for`, `best_season`) VALUES
('Beijing', '北京', 1, 1, 16410.54, 2154, 'UTC+8', '中国的首都，拥有悠久的历史和丰富的文化遗产。', 39.9042, 116.4074, '故宫、长城、颐和园', '春秋'),
('Shanghai', '上海', 1, 0, 6340.5, 2487, 'UTC+8', '中国最大的城市，经济金融中心。', 31.2304, 121.4737, '外滩、东方明珠、豫园', '四季皆宜'),
('Tokyo', '东京', 2, 1, 2194.07, 1396, 'UTC+9', '日本的首都，现代化大都市。', 35.6762, 139.6503, '东京塔、明治神宫、涩谷', '春秋'),
('New York', '纽约', 3, 0, 1214.4, 834, 'UTC-5', '美国最大的城市，世界金融中心。', 40.7128, -74.0060, '自由女神像、时代广场、中央公园', '春秋'),
('Paris', '巴黎', 4, 1, 105.4, 215, 'UTC+1', '法国的首都，浪漫之都。', 48.8566, 2.3522, '埃菲尔铁塔、卢浮宫、巴黎圣母院', '春秋'),
('Sydney', '悉尼', 5, 0, 12368.19, 531, 'UTC+10', '澳大利亚最大的城市。', -33.8688, 151.2093, '悉尼歌剧院、海港大桥、邦迪海滩', '春秋'),
('Cape Town', '开普敦', 6, 0, 2499, 471, 'UTC+2', '南非的立法首都，美丽的海滨城市。', -33.9249, 18.4241, '桌山、好望角、维多利亚港', '春秋'),
('Rio de Janeiro', '里约热内卢', 7, 0, 1200, 672, 'UTC-3', '巴西的前首都，以狂欢节著称。', -22.9068, -43.1729, '基督像、科帕卡巴纳海滩、狂欢节', '冬春'),
('Berlin', '柏林', 8, 1, 891.8, 375, 'UTC+1', '德国的首都，历史文化名城。', 52.5200, 13.4050, '勃兰登堡门、柏林墙、博物馆岛', '春秋'),
('Mumbai', '孟买', 9, 0, 603.4, 2041, 'UTC+5:30', '印度最大的城市，经济中心。', 19.0760, 72.8777, '泰姬玛哈酒店、贾特拉帕蒂·希瓦吉终点站、海滩', '冬');

-- ----------------------------
-- 插入标签表数据
-- ----------------------------
INSERT INTO `tags` (`name`, `chinese_name`, `category`, `icon`, `color`, `sort_order`) VALUES
('historical', '历史古迹', 'theme', '🏯', '#8B4513', 1),
('natural', '自然风光', 'theme', '🏞️', '#228B22', 2),
('modern', '现代建筑', 'theme', '🏙️', '#4682B4', 3),
('cultural', '文化体验', 'theme', '🎭', '#DC143C', 4),
('family', '适合家庭', 'audience', '👨‍👩‍👧‍👦', '#FFD700', 5),
('romantic', '浪漫情侣', 'audience', '💕', '#FF69B4', 6),
('adventure', '冒险刺激', 'feature', '🚀', '#FF4500', 7),
('relaxation', '休闲放松', 'feature', '🧘', '#98FB98', 8),
('food', '美食之旅', 'feature', '🍜', '#FFA500', 9),
('shopping', '购物天堂', 'feature', '🛍️', '#9370DB', 10);

-- ----------------------------
-- 插入景点表数据（新）
-- ----------------------------
INSERT INTO `attractions` (`name`, `chinese_name`, `city_id`, `type`, `rating`, `price_level`, `visiting_hours`, `best_season`, `visit_duration`, `description`, `history`, `tips`, `address`, `latitude`, `longitude`, `image_url`, `official_website`, `year_built`, `architect`) VALUES
('Great Wall of China', '中国长城', 1, 'historical', 4.8, '中等', '08:00-17:00', '春秋', '3-4小时', '中国长城是世界上最伟大的建筑之一，也是世界文化遗产。', '长城始建于春秋战国时期，是中国古代为了防御北方游牧民族入侵而修建的。', '建议早上早点去，避开人流高峰；穿舒适的鞋子，因为需要走很多台阶。', '北京市怀柔区', 40.4319, 116.5704, 'https://example.com/greatwall.jpg', 'https://www.badaling.gov.cn/', '公元前7世纪', '秦始皇'),
('Forbidden City', '故宫', 1, 'historical', 4.9, '中等', '08:30-17:00', '四季皆宜', '4-5小时', '故宫是中国明清两代的皇家宫殿，是世界上现存规模最大、保存最为完整的木质结构古建筑之一。', '故宫始建于明永乐四年（1406年），是中国古代宫廷建筑的精华。', '建议预留充足的时间游览；可以租讲解器了解更多历史；注意保护文物，不要触摸展品。', '北京市东城区景山前街4号', 39.9163, 116.3972, 'https://example.com/forbidden_city.jpg', 'https://www.dpm.org.cn/', '1420年', '蒯祥'),
('Eiffel Tower', '埃菲尔铁塔', 5, 'modern', 4.7, '中等', '09:00-23:45', '春秋', '2-3小时', '埃菲尔铁塔是法国巴黎的标志性建筑，也是世界著名的建筑之一。', '埃菲尔铁塔建于1889年，是为了纪念法国大革命100周年而建造的。', '建议提前网上购票，避免排队；黄昏时分登塔可以看到美丽的日落；塔顶风大，注意保暖。', '法国巴黎战神广场', 48.8584, 2.2945, 'https://example.com/eiffel_tower.jpg', 'https://www.toureiffel.paris/', '1889年', '古斯塔夫·埃菲尔'),
('Statue of Liberty', '自由女神像', 4, 'monument', 4.6, '中等', '09:00-17:00', '春秋', '2-3小时', '自由女神像是美国的标志性建筑，象征着自由和民主。', '自由女神像是法国赠送给美国的礼物，于1886年10月28日落成。', '建议提前网上购票，尤其是 crown access；需要乘船前往，注意天气情况；可以参观女神像内部和观景台。', '美国纽约自由岛', 40.6892, -74.0445, 'https://example.com/statue_of_liberty.jpg', 'https://www.nps.gov/stli/index.htm', '1886年', '弗雷德里克·奥古斯特·巴托尔迪'),
('Tokyo Tower', '东京塔', 3, 'modern', 4.5, '便宜', '09:00-22:00', '四季皆宜', '1-2小时', '东京塔是日本东京的标志性建筑，高333米。', '东京塔建于1958年，是为了纪念日本战后复兴而建造的。', '建议黄昏时分登塔，可以看到东京的日景和夜景；塔顶有咖啡厅，可以休息和观景；可以购买纪念品。', '日本东京都港区芝公园', 35.6586, 139.7454, 'https://example.com/tokyo_tower.jpg', 'https://www.tokyotower.co.jp/', '1958年', '内藤多仲'),
('Sydney Opera House', '悉尼歌剧院', 6, 'modern', 4.8, '中等', '09:00-17:00', '春秋', '1-2小时', '悉尼歌剧院是澳大利亚悉尼的标志性建筑，也是世界著名的建筑之一。', '悉尼歌剧院建于1973年，是20世纪最具特色的建筑之一。', '建议参加导览团，了解建筑历史和内部结构；可以观看演出；周边有很多餐厅和咖啡馆。', '澳大利亚悉尼本尼朗角', -33.8568, 151.2153, 'https://example.com/sydney_opera_house.jpg', 'https://www.sydneyoperahouse.com/', '1973年', '约恩·乌松'),
('Table Mountain', '桌山', 7, 'natural', 4.9, '中等', '08:00-20:00', '春秋', '3-4小时', '桌山是南非开普敦的标志性自然景观，山顶平坦如桌。', '桌山是开普敦的象征，也是世界自然遗产。', '建议乘坐缆车上下山，节省体力；山顶风大，注意保暖；可以徒步登山，但需要一定的体力和时间。', '南非开普敦', -33.9628, 18.4036, 'https://example.com/table_mountain.jpg', 'https://www.tablemountain.net/', NULL, NULL),
('Christ the Redeemer', '基督像', 8, 'monument', 4.7, '便宜', '08:00-19:00', '冬春', '1-2小时', '基督像是巴西里约热内卢的标志性建筑，高30米。', '基督像建于1931年，是为了纪念巴西独立100周年而建造的。', '建议早上或下午前往，避开中午的高温；可以乘坐小火车或徒步登山；山顶风大，注意安全。', '巴西里约热内卢科尔科瓦多山', -22.9519, -43.2105, 'https://example.com/christ_the_redeemer.jpg', 'https://www.corcovado.com.br/', '1931年', '保罗·兰多斯基'),
('Brandenburg Gate', '勃兰登堡门', 9, 'historical', 4.6, '免费', '全天开放', '春秋', '0.5-1小时', '勃兰登堡门是德国柏林的标志性建筑，也是德国统一的象征。', '勃兰登堡门建于1791年，是柏林的象征。', '建议黄昏时分前往，灯光效果很美；周边有很多历史景点，可以一并游览；注意个人财物安全。', '德国柏林市中心', 52.5163, 13.3777, 'https://example.com/brandenburg_gate.jpg', 'https://www.visitberlin.de/en/brandenburg-gate', '1791年', '卡尔·戈特哈德·朗汉斯'),
('Taj Mahal', '泰姬陵', 10, 'historical', 4.9, '中等', '06:00-19:00', '冬', '2-3小时', '泰姬陵是印度的标志性建筑，也是世界文化遗产。', '泰姬陵建于1653年，是莫卧儿皇帝沙贾汗为纪念他的妻子穆姆塔兹·玛哈尔而建造的。', '建议早上日出时分前往，可以看到泰姬陵的美丽光影；注意着装得体，尊重当地文化；可以聘请导游了解更多历史。', '印度阿格拉', 27.1751, 78.0421, 'https://example.com/taj_mahal.jpg', 'https://www.tajmahal.gov.in/', '1653年', '乌斯塔德·艾哈迈德·拉合里');

-- ----------------------------
-- 插入景点-标签关联表数据
-- ----------------------------
INSERT INTO `attraction_tags` (`attraction_id`, `tag_id`) VALUES
(1, 1), (1, 5), (1, 8),
(2, 1), (2, 4), (2, 6),
(3, 3), (3, 6), (3, 8),
(4, 3), (4, 4), (4, 6),
(5, 3), (5, 4), (5, 9),
(6, 3), (6, 4), (6, 8),
(7, 2), (7, 5), (7, 7),
(8, 3), (8, 6), (8, 8),
(9, 1), (9, 4), (9, 9),
(10, 1), (10, 4), (10, 6);

-- ----------------------------
-- 插入图片表数据
-- ----------------------------
INSERT INTO `attraction_images` (`attraction_id`, `image_url`, `thumbnail_url`, `title`, `description`, `photographer`, `license`, `is_primary`, `sort_order`, `width`, `height`, `file_size`) VALUES
(1, 'https://example.com/greatwall1.jpg', 'https://example.com/greatwall1_thumb.jpg', '长城远景', '中国长城的壮丽远景', 'Zhang Wei', 'CC BY-SA 4.0', 1, 1, 1920, 1080, 2048),
(1, 'https://example.com/greatwall2.jpg', 'https://example.com/greatwall2_thumb.jpg', '长城近景', '长城的砖石结构', 'Li Ming', 'CC BY-SA 4.0', 0, 2, 1920, 1080, 1800),
(2, 'https://example.com/forbidden_city1.jpg', 'https://example.com/forbidden_city1_thumb.jpg', '故宫全景', '故宫的鸟瞰图', 'Wang Hua', 'CC BY-SA 4.0', 1, 1, 1920, 1080, 2200),
(2, 'https://example.com/forbidden_city2.jpg', 'https://example.com/forbidden_city2_thumb.jpg', '故宫宫殿', '故宫的太和殿', 'Zhao Yang', 'CC BY-SA 4.0', 0, 2, 1920, 1080, 1900),
(3, 'https://example.com/eiffel_tower1.jpg', 'https://example.com/eiffel_tower1_thumb.jpg', '埃菲尔铁塔', '埃菲尔铁塔的正面照', 'Jean Dupont', 'CC BY-SA 4.0', 1, 1, 1920, 1080, 2100),
(3, 'https://example.com/eiffel_tower2.jpg', 'https://example.com/eiffel_tower2_thumb.jpg', '埃菲尔铁塔夜景', '夜晚的埃菲尔铁塔', 'Pierre Martin', 'CC BY-SA 4.0', 0, 2, 1920, 1080, 2300),
(4, 'https://example.com/statue_of_liberty1.jpg', 'https://example.com/statue_of_liberty1_thumb.jpg', '自由女神像', '自由女神像的正面照', 'John Smith', 'CC BY-SA 4.0', 1, 1, 1920, 1080, 2000),
(4, 'https://example.com/statue_of_liberty2.jpg', 'https://example.com/statue_of_liberty2_thumb.jpg', '自由女神像远景', '自由女神像的远景', 'Jane Doe', 'CC BY-SA 4.0', 0, 2, 1920, 1080, 1850),
(5, 'https://example.com/tokyo_tower1.jpg', 'https://example.com/tokyo_tower1_thumb.jpg', '东京塔', '东京塔的正面照', 'Tanaka Yuki', 'CC BY-SA 4.0', 1, 1, 1920, 1080, 1950),
(5, 'https://example.com/tokyo_tower2.jpg', 'https://example.com/tokyo_tower2_thumb.jpg', '东京塔夜景', '夜晚的东京塔', 'Sato Hiroshi', 'CC BY-SA 4.0', 0, 2, 1920, 1080, 2150);

-- ----------------------------
-- 插入门票价格表数据
-- ----------------------------
INSERT INTO `ticket_prices` (`attraction_id`, `ticket_type`, `price`, `currency`, `season`, `start_date`, `end_date`, `description`, `purchase_url`, `discount_info`) VALUES
(1, '成人票', 60.00, 'CNY', '全年', '2024-01-01', '2024-12-31', '长城成人门票', 'https://example.com/tickets/greatwall', '学生票半价'),
(1, '学生票', 30.00, 'CNY', '全年', '2024-01-01', '2024-12-31', '长城学生门票', 'https://example.com/tickets/greatwall', '需持有效学生证'),
(2, '成人票', 80.00, 'CNY', '全年', '2024-01-01', '2024-12-31', '故宫成人门票', 'https://example.com/tickets/forbidden_city', '老人票半价'),
(2, '老人票', 40.00, 'CNY', '全年', '2024-01-01', '2024-12-31', '故宫老人门票', 'https://example.com/tickets/forbidden_city', '需年满60周岁'),
(3, '成人票', 25.50, 'EUR', '全年', '2024-01-01', '2024-12-31', '埃菲尔铁塔成人门票', 'https://example.com/tickets/eiffel_tower', '儿童票半价'),
(3, '儿童票', 12.70, 'EUR', '全年', '2024-01-01', '2024-12-31', '埃菲尔铁塔儿童门票', 'https://example.com/tickets/eiffel_tower', '4-11岁'),
(4, '成人票', 24.00, 'USD', '全年', '2024-01-01', '2024-12-31', '自由女神像成人门票', 'https://example.com/tickets/statue_of_liberty', '老人票优惠'),
(4, '老人票', 18.00, 'USD', '全年', '2024-01-01', '2024-12-31', '自由女神像老人门票', 'https://example.com/tickets/statue_of_liberty', '62岁以上'),
(5, '成人票', 900.00, 'JPY', '全年', '2024-01-01', '2024-12-31', '东京塔成人门票', 'https://example.com/tickets/tokyo_tower', '学生票优惠'),
(6, '成人票', 42.00, 'AUD', '全年', '2024-01-01', '2024-12-31', '悉尼歌剧院成人门票', 'https://example.com/tickets/sydney_opera_house', '家庭票优惠');

-- ----------------------------
-- 插入交通方式表数据
-- ----------------------------
INSERT INTO `transportation` (`attraction_id`, `type`, `route_info`, `from_location`, `duration`, `distance`, `cost`, `currency`, `schedule`, `tips`, `is_recommended`, `sort_order`) VALUES
(1, 'bus', '乘坐877路公交车直达八达岭长城', '北京市区', '约1.5小时', 75.0, 12.00, 'CNY', '06:00-19:00', '建议提前30分钟到达车站，避免排队', 1, 1),
(1, 'taxi', '从北京市区打车前往', '北京市区', '约1小时', 75.0, 200.00, 'CNY', '全天', '价格较贵，但时间灵活', 0, 2),
(2, 'subway', '乘坐地铁1号线或2号线到天安门东站，步行约10分钟', '北京市区', '约30分钟', 5.0, 3.00, 'CNY', '05:00-23:00', '建议避开高峰期', 1, 1),
(2, 'bus', '乘坐1路、2路、52路等公交车到天安门站', '北京市区', '约40分钟', 5.0, 2.00, 'CNY', '06:00-22:00', '站点较多，可能会堵车', 0, 2),
(3, 'metro', '乘坐地铁6号线到Bir-Hakeim站，步行约10分钟', '巴黎市区', '约20分钟', 3.0, 2.10, 'EUR', '05:30-00:30', '最便捷的交通方式', 1, 1),
(3, 'bus', '乘坐42路、69路等公交车到Champ de Mars站', '巴黎市区', '约30分钟', 3.0, 2.10, 'EUR', '06:00-22:00', '可以欣赏沿途风景', 0, 2),
(4, 'ferry', '从Battery Park乘坐渡轮前往自由岛', '纽约市区', '约25分钟', 3.0, 24.00, 'USD', '09:00-17:00', '唯一的交通方式，建议提前购票', 1, 1),
(5, 'train', '乘坐JR山手线到滨松町站，换乘东京塔专线巴士', '东京市区', '约30分钟', 5.0, 200.00, 'JPY', '06:00-23:00', '便捷且经济', 1, 1),
(6, 'ferry', '从Circular Quay乘坐渡轮前往悉尼歌剧院', '悉尼市区', '约10分钟', 1.0, 10.00, 'AUD', '07:00-22:00', '可以欣赏悉尼港的美景', 1, 1),
(7, 'cable car', '乘坐桌山缆车上下山', '开普敦市区', '约10分钟', 1.0, 350.00, 'ZAR', '08:00-20:00', '建议提前购票，避免排队', 1, 1);

-- ----------------------------
-- 插入评论表数据
-- ----------------------------
INSERT INTO `reviews` (`attraction_id`, `user_id`, `rating`, `title`, `content`, `visit_date`, `travel_with`, `photos_count`, `likes_count`, `helpful_count`, `is_verified`, `status`) VALUES
(1, 1, 5.0, '长城真的很震撼！', '第一次来长城，被它的规模和历史感深深震撼。建议早上早点来，人少一些，拍照效果更好。', '2024-04-01', '朋友', 5, 20, 15, 1, 'approved'),
(1, 2, 4.5, '值得一去的景点', '长城很壮观，但是人真的很多。建议穿舒适的鞋子，因为需要走很多台阶。', '2024-04-10', '家人', 3, 12, 8, 1, 'approved'),
(2, 3, 5.0, '故宫的历史文化底蕴深厚', '故宫是中国历史文化的瑰宝，每一个宫殿都有其独特的故事。建议请个导游，了解更多历史背景。', '2024-04-05', '家人', 8, 25, 20, 1, 'approved'),
(2, 4, 4.5, '人太多了', '故宫确实很美丽，但是人真的太多了，尤其是节假日。建议工作日来参观。', '2024-04-15', '朋友', 2, 8, 5, 1, 'approved'),
(3, 5, 5.0, '埃菲尔铁塔的夜景太美了！', '傍晚时分登塔，可以看到巴黎的日落和夜景，非常浪漫。建议提前购票，避免排队。', '2024-03-20', '伴侣', 10, 30, 25, 1, 'approved'),
(3, 6, 4.0, '需要爬很多楼梯', '埃菲尔铁塔很壮观，但是如果不坐电梯，需要爬很多楼梯。建议体力不好的人选择电梯。', '2024-03-25', '朋友', 4, 10, 6, 1, 'approved'),
(4, 7, 4.5, '自由女神像的象征意义', '自由女神像是美国的象征，代表着自由和民主。建议提前购票，因为人流量很大。', '2024-03-15', '家人', 6, 15, 10, 1, 'approved'),
(4, 8, 4.0, '天气不好影响体验', '去的时候天气不好，雾很大，看不到很远的景色。建议选择晴天前往。', '2024-03-10', '朋友', 2, 5, 3, 1, 'approved'),
(5, 9, 4.5, '东京塔的夜景很棒', '东京塔的夜景非常美丽，可以看到整个东京的灯光。建议傍晚时分前往。', '2024-03-05', '伴侣', 7, 18, 12, 1, 'approved'),
(5, 10, 4.0, '门票有点贵', '东京塔的景色不错，但是门票有点贵。建议购买联票，可以参观更多景点。', '2024-03-12', '朋友', 3, 8, 5, 1, 'approved');

-- ----------------------------
-- 插入评论回复表数据
-- ----------------------------
INSERT INTO `review_replies` (`review_id`, `user_id`, `content`, `likes_count`) VALUES
(1, 2, '同意！早上的长城人少，拍照效果确实更好。', 5),
(1, 3, '我也想去长城，请问需要爬很多台阶吗？', 2),
(2, 1, '是的，长城的台阶很多，建议穿舒适的鞋子。', 3),
(3, 4, '故宫确实很值得参观，我去年去过，印象深刻。', 4),
(3, 5, '请问导游的费用大概是多少？', 1),
(4, 3, '工作日去确实人少很多，我就是工作日去的。', 2),
(5, 6, '埃菲尔铁塔的夜景确实很美，我也很喜欢。', 6),
(5, 7, '请问傍晚时分人多吗？需要提前多久去排队？', 3),
(6, 5, '是的，爬楼梯确实很累，我爬了一半就放弃了，选择了电梯。', 4),
(7, 8, '自由女神像的象征意义确实很重要，值得一去。', 3);

-- ----------------------------
-- 插入评论点赞表数据
-- ----------------------------
INSERT INTO `review_likes` (`review_id`, `user_id`) VALUES
(1, 3), (1, 4), (1, 5),
(2, 1), (2, 6),
(3, 2), (3, 7), (3, 8),
(4, 3), (4, 9),
(5, 1), (5, 4), (5, 6), (5, 10),
(6, 2), (6, 5),
(7, 3), (7, 7),
(8, 1), (8, 9),
(9, 2), (9, 4), (9, 8),
(10, 3), (10, 5);

-- ----------------------------
-- 插入轮播分组
-- ----------------------------
INSERT INTO `carousel_groups` (`group_name`, `group_code`, `description`, `display_type`, `auto_play_interval`, `height`) VALUES
('首页顶部轮播', 'home_top', '网站首页顶部主轮播', 'multiple', 5000, 600),
('首页中部轮播', 'home_middle', '首页中部推荐轮播', 'multiple', 4000, 400),
('景点详情页轮播', 'attraction_detail', '景点详情页顶部轮播', 'single', 3000, 500),
('APP开屏轮播', 'app_splash', '移动端开屏广告轮播', 'fullscreen', 3000, 800);

-- ----------------------------
-- 插入轮播图片
-- ----------------------------
INSERT INTO `carousels` (
    `title`, `subtitle`, `image_url`, `mobile_image_url`,
    `link_type`, `link_url`, `target_id`, `button_text`,
    `text_color`, `overlay_opacity`, `position`, `sort_order`,
    `start_time`, `end_time`, `is_always_show`, `is_active`
) VALUES
(
    '浪漫巴黎之旅', '探索埃菲尔铁塔的浪漫与优雅', 
    '/images/carousel/paris.jpg', '/images/carousel/paris_mobile.jpg',
    'attraction', NULL, 1, '立即预订',
    '#FFFFFF', 0.3, 'home_top', 1,
    '2025-01-01 00:00:00', '2025-12-31 23:59:59', FALSE, TRUE
),
(
    '古罗马文明', '穿越千年，感受斗兽场的辉煌',
    '/images/carousel/rome.jpg', '/images/carousel/rome_mobile.jpg',
    'attraction', NULL, 2, '了解更多',
    '#FFD700', 0.4, 'home_top', 2,
    NULL, NULL, TRUE, TRUE
),
(
    '纽约不夜城', '自由女神像俯瞰曼哈顿',
    '/images/carousel/nyc.jpg', '/images/carousel/nyc_mobile.jpg',
    'attraction', NULL, 3, '查看详情',
    '#FFFFFF', 0.2, 'home_top', 3,
    NULL, NULL, TRUE, TRUE
),
(
    '暑期特惠', '欧洲五国游限时8折优惠',
    '/images/carousel/summer_sale.jpg', '/images/carousel/summer_sale_mobile.jpg',
    'url', 'https://example.com/summer-sale', NULL, '立即抢购',
    '#FF0000', 0.2, 'home_middle', 1,
    '2025-06-01 00:00:00', '2025-08-31 23:59:59', FALSE, TRUE
),
(
    '亲子游推荐', '最适合带娃的十大景点',
    '/images/carousel/family.jpg', '/images/carousel/family_mobile.jpg',
    'custom_page', '/family-travel', NULL, '查看推荐',
    '#4CAF50', 0.25, 'home_middle', 2,
    NULL, NULL, TRUE, TRUE
),
(
    '埃菲尔铁塔全景', '360度俯瞰巴黎市区',
    '/images/attractions/eiffel/detail_1.jpg', '/images/attractions/eiffel/detail_1_mobile.jpg',
    'attraction', NULL, 1, '查看详情',
    '#FFFFFF', 0.2, 'attraction_detail', 1,
    NULL, NULL, TRUE, TRUE
),
(
    '埃菲尔铁塔夜景', '璀璨灯光秀每晚上演',
    '/images/attractions/eiffel/detail_2.jpg', '/images/attractions/eiffel/detail_2_mobile.jpg',
    'attraction', NULL, 1, '查看详情',
    '#FFFFFF', 0.3, 'attraction_detail', 2,
    NULL, NULL, TRUE, TRUE
),
(
    'APP限时福利', '新用户注册立减100元',
    '/images/app_splash/splash_1.jpg', '/images/app_splash/splash_1_mobile.jpg',
    'url', 'https://example.com/download', NULL, '立即下载',
    '#FFFFFF', 0.2, 'app_splash', 1,
    '2025-01-01 00:00:00', '2025-03-31 23:59:59', FALSE, TRUE
);

-- ----------------------------
-- 关联轮播到分组
-- ----------------------------
INSERT INTO `carousel_group_items` (`group_id`, `carousel_id`, `sort_order`) VALUES
(1, 1, 1), (1, 2, 2), (1, 3, 3),  -- 首页顶部：巴黎、罗马、纽约
(2, 4, 1), (2, 5, 2),              -- 首页中部：暑期特惠、亲子游
(3, 6, 1), (3, 7, 2),              -- 景点详情：埃菲尔铁塔图片
(4, 8, 1);                          -- APP开屏：APP福利

-- ----------------------------
-- 插入轮播统计示例数据
-- ----------------------------
INSERT INTO `carousel_stats` (`carousel_id`, `stat_date`, `click_count`, `impression_count`, `pc_click_count`, `mobile_click_count`) VALUES
(1, '2025-02-01', 1250, 15000, 800, 450),
(1, '2025-02-02', 1350, 16200, 850, 500),
(2, '2025-02-01', 980, 12000, 600, 380),
(3, '2025-02-01', 2100, 25000, 1200, 900);

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (25, '长城', '中国', 'http://localhost:2025/upload/c6813676-52f5-4235-b599-595cdb735dd4.png', '中国古代的军事防御工程', '世界文化遗产', '始建于西周时期', '中国古代文化的象征', '长城,历史,文化', '历史建筑', '#FF0000');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (26, '埃菲尔铁塔', '法国', 'https://example.com/world2.jpg', '法国巴黎的标志性建筑', '世界著名建筑', '建成于1889年', '法国工业革命的象征', '埃菲尔铁塔,法国,建筑', '城市建筑', '#0000FF');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (27, '金字塔', '埃及', 'https://example.com/world3.jpg', '埃及古代法老的陵墓', '世界文化遗产', '始建于公元前2600年', '古埃及文明的象征', '金字塔,埃及,历史', '历史建筑', '#FFFF00');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (28, '悉尼歌剧院', '澳大利亚', 'https://example.com/world4.jpg', '澳大利亚悉尼的标志性建筑', '世界文化遗产', '建成于1973年', '现代建筑的代表', '悉尼歌剧院,澳大利亚,建筑', '现代建筑', '#00FF00');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (29, '自由女神像', '美国', 'https://example.com/world5.jpg', '美国纽约的标志性建筑', '世界文化遗产', '建成于1886年', '自由和民主的象征', '自由女神像,美国,建筑', '城市建筑', '#00FFFF');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (30, '泰姬陵', '印度', 'https://example.com/world6.jpg', '印度阿格拉的标志性建筑', '世界文化遗产', '建成于1653年', '爱情的象征', '泰姬陵,印度,建筑', '历史建筑', '#FF00FF');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (31, '大本钟', '英国', 'https://example.com/world7.jpg', '英国伦敦的标志性建筑', '世界著名建筑', '建成于1859年', '英国议会制度的象征', '大本钟,英国,建筑', '城市建筑', '#FFA500');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (32, '东京塔', '日本', 'https://example.com/world8.jpg', '日本东京的标志性建筑', '世界著名建筑', '建成于1958年', '日本战后复兴的象征', '东京塔,日本,建筑', '城市建筑', '#800080');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (33, '圣索菲亚大教堂', '土耳其', 'https://example.com/world9.jpg', '土耳其伊斯坦布尔的标志性建筑', '世界文化遗产', '建成于537年', '拜占庭建筑的代表', '圣索菲亚大教堂,土耳其,建筑', '历史建筑', '#008000');
INSERT INTO db_minecraft.worldcharacteristics (id, name, country, image, description, features, history, culture, tags, category, color) VALUES (34, '罗马斗兽场', '意大利', 'https://example.com/world10.jpg', '意大利罗马的标志性建筑', '世界文化遗产', '建成于80年', '古罗马文明的象征', '罗马斗兽场,意大利,历史', '历史建筑', '#800000');

