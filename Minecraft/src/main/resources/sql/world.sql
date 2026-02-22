sql
-- 大洲表
CREATE TABLE continents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '大洲名称',
    chinese_name VARCHAR(50) NOT NULL COMMENT '中文名称',
    area DECIMAL(10,2) COMMENT '面积(万平方公里)',
    population INT COMMENT '人口(万)',
    description TEXT COMMENT '描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 国家表
CREATE TABLE countries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '国家名称',
    chinese_name VARCHAR(100) NOT NULL COMMENT '中文名称',
    continent_id INT NOT NULL COMMENT '所属大洲ID',
    capital VARCHAR(100) COMMENT '首都',
    area DECIMAL(10,2) COMMENT '面积(万平方公里)',
    population INT COMMENT '人口(万)',
    currency VARCHAR(50) COMMENT '货币',
    language VARCHAR(100) COMMENT '官方语言',
    timezone VARCHAR(50) COMMENT '时区',
    country_code VARCHAR(10) COMMENT '国家代码',
    phone_code VARCHAR(10) COMMENT '电话区号',
    flag_emoji VARCHAR(10) COMMENT '国旗emoji',
    description TEXT COMMENT '简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (continent_id) REFERENCES continents(id)
);

-- 城市表
CREATE TABLE cities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '城市名称',
    chinese_name VARCHAR(100) NOT NULL COMMENT '中文名称',
    country_id INT NOT NULL COMMENT '所属国家ID',
    is_capital BOOLEAN DEFAULT FALSE COMMENT '是否是首都',
    area DECIMAL(8,2) COMMENT '面积(平方公里)',
    population INT COMMENT '人口(万)',
    timezone VARCHAR(50) COMMENT '时区',
    description TEXT COMMENT '城市简介',
    latitude DECIMAL(10,8) COMMENT '纬度',
    longitude DECIMAL(11,8) COMMENT '经度',
    famous_for VARCHAR(255) COMMENT '闻名于世的原因',
    best_season VARCHAR(100) COMMENT '最佳旅游季节',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- 景点表
CREATE TABLE attractions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '景点名称',
    chinese_name VARCHAR(200) NOT NULL COMMENT '中文名称',
    city_id INT NOT NULL COMMENT '所在城市ID',
    type VARCHAR(50) COMMENT '类型(古迹/自然/现代建筑等)',
    rating DECIMAL(2,1) COMMENT '评分(1-5)',
    price_level VARCHAR(20) COMMENT '消费水平(免费/便宜/中等/昂贵)',
    visiting_hours VARCHAR(200) COMMENT '开放时间',
    best_season VARCHAR(100) COMMENT '最佳游览季节',
    visit_duration VARCHAR(50) COMMENT '建议游览时长',
    description TEXT COMMENT '详细描述',
    history TEXT COMMENT '历史背景',
    tips TEXT COMMENT '游玩建议',
    address VARCHAR(255) COMMENT '具体地址',
    latitude DECIMAL(10,8) COMMENT '纬度',
    longitude DECIMAL(11,8) COMMENT '经度',
    image_url VARCHAR(500) COMMENT '主图URL',
    official_website VARCHAR(500) COMMENT '官方网站',
    year_built VARCHAR(50) COMMENT '建造年份',
    architect VARCHAR(200) COMMENT '建筑师',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);
2. 新增表
标签表 (tags)
sql
CREATE TABLE tags (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    chinese_name VARCHAR(50) NOT NULL COMMENT '中文名称',
    category VARCHAR(50) COMMENT '标签分类(主题/适合人群/特色等)',
    icon VARCHAR(100) COMMENT '标签图标',
    color VARCHAR(20) COMMENT '标签颜色',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tags (name, chinese_name, category) VALUES
('photo_spot', '拍照圣地', '主题'),
('family_friendly', '亲子游', '适合人群'),
('couples', '情侣约会', '适合人群'),
('history', '历史古迹', '主题'),
('nature', '自然风光', '主题'),
('free_entry', '免费入园', '特色'),
('night_view', '夜景', '特色'),
('hiking', '徒步', '活动'),
('museum', '博物馆', '类型'),
('beach', '海滩', '自然');
景点-标签关联表 (attraction_tags)
sql
CREATE TABLE attraction_tags (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attraction_id INT NOT NULL COMMENT '景点ID',
    tag_id INT NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attraction_id) REFERENCES attractions(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    UNIQUE KEY unique_attraction_tag (attraction_id, tag_id)
);
图片表 (attraction_images)
sql
CREATE TABLE attraction_images (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attraction_id INT NOT NULL COMMENT '景点ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    title VARCHAR(200) COMMENT '图片标题',
    description TEXT COMMENT '图片描述',
    photographer VARCHAR(100) COMMENT '摄影师',
    license VARCHAR(50) COMMENT '版权信息',
    is_primary BOOLEAN DEFAULT FALSE COMMENT '是否是主图',
    sort_order INT DEFAULT 0 COMMENT '排序',
    width INT COMMENT '图片宽度',
    height INT COMMENT '图片高度',
    file_size INT COMMENT '文件大小(KB)',
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attraction_id) REFERENCES attractions(id) ON DELETE CASCADE
);
门票价格表 (ticket_prices)
sql
CREATE TABLE ticket_prices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attraction_id INT NOT NULL COMMENT '景点ID',
    ticket_type VARCHAR(50) NOT NULL COMMENT '门票类型(成人票/学生票/儿童票/老人票)',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '货币单位',
    season VARCHAR(50) COMMENT '适用季节(旺季/淡季/全年)',
    start_date DATE COMMENT '有效期开始',
    end_date DATE COMMENT '有效期结束',
    description VARCHAR(255) COMMENT '价格说明',
    purchase_url VARCHAR(500) COMMENT '购票链接',
    discount_info VARCHAR(255) COMMENT '优惠信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (attraction_id) REFERENCES attractions(id) ON DELETE CASCADE
);
交通方式表 (transportation)
sql
CREATE TABLE transportation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attraction_id INT NOT NULL COMMENT '景点ID',
    type VARCHAR(50) NOT NULL COMMENT '交通类型(公交/地铁/出租车/步行/缆车等)',
    route_info TEXT COMMENT '路线信息',
    from_location VARCHAR(255) COMMENT '起始地点',
    duration VARCHAR(50) COMMENT '所需时间',
    distance DECIMAL(8,2) COMMENT '距离(公里)',
    cost DECIMAL(8,2) COMMENT '费用',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '货币单位',
    schedule VARCHAR(255) COMMENT '运营时间',
    tips TEXT COMMENT '交通提示',
    is_recommended BOOLEAN DEFAULT FALSE COMMENT '是否推荐',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attraction_id) REFERENCES attractions(id) ON DELETE CASCADE
);
用户表 (users)
sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    nickname VARCHAR(100) COMMENT '昵称',
    bio TEXT COMMENT '个人简介',
    level INT DEFAULT 1 COMMENT '用户等级',
    points INT DEFAULT 0 COMMENT '积分',
    travel_count INT DEFAULT 0 COMMENT '旅行次数',
    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    status ENUM('active', 'inactive', 'banned') DEFAULT 'active' COMMENT '状态'
);
评论表 (reviews)
sql
CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attraction_id INT NOT NULL COMMENT '景点ID',
    user_id INT NOT NULL COMMENT '用户ID',
    rating DECIMAL(2,1) NOT NULL COMMENT '评分(1-5)',
    title VARCHAR(200) COMMENT '评论标题',
    content TEXT NOT NULL COMMENT '评论内容',
    visit_date DATE COMMENT '游玩日期',
    travel_with VARCHAR(50) COMMENT '和谁一起(独自/伴侣/家庭/朋友)',
    photos_count INT DEFAULT 0 COMMENT '照片数量',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    helpful_count INT DEFAULT 0 COMMENT '有用数',
    is_verified BOOLEAN DEFAULT FALSE COMMENT '是否验证过',
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '审核状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (attraction_id) REFERENCES attractions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_attraction_rating (attraction_id, rating),
    INDEX idx_user_created (user_id, created_at)
);
评论回复表 (review_replies)
sql
CREATE TABLE review_replies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    review_id INT NOT NULL COMMENT '评论ID',
    user_id INT NOT NULL COMMENT '回复用户ID',
    content TEXT NOT NULL COMMENT '回复内容',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
评论点赞表 (review_likes)
sql
CREATE TABLE review_likes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    review_id INT NOT NULL COMMENT '评论ID',
    user_id INT NOT NULL COMMENT '用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_review_user (review_id, user_id)
);
3. 示例数据插入
sql
-- 插入示例标签关联
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES
(1, 1), (1, 3), (1, 7),  -- 埃菲尔铁塔：拍照圣地、情侣约会、夜景
(2, 4), (2, 9),          -- 罗马斗兽场：历史古迹、博物馆
(3, 1), (3, 6),          -- 自由女神像：拍照圣地、免费入园
(4, 4), (4, 8),          -- 长城：历史古迹、徒步
(5, 1), (5, 9);          -- 悉尼歌剧院：拍照圣地、博物馆

-- 插入示例图片
INSERT INTO attraction_images (attraction_id, image_url, title, is_primary, sort_order) VALUES
(1, '/images/eiffel/day.jpg', '埃菲尔铁塔日景', TRUE, 1),
(1, '/images/eiffel/night.jpg', '埃菲尔铁塔夜景', FALSE, 2),
(1, '/images/eiffel/top_view.jpg', '塔顶俯瞰巴黎', FALSE, 3),
(2, '/images/colosseum/outside.jpg', '罗马斗兽场外观', TRUE, 1),
(2, '/images/colosseum/inside.jpg', '斗兽场内部', FALSE, 2);

-- 插入示例门票价格
INSERT INTO ticket_prices (attraction_id, ticket_type, price, season, description) VALUES
(1, '成人票', 25.00, '旺季', '电梯至顶层'),
(1, '成人票', 16.00, '淡季', '电梯至顶层'),
(1, '青年票', 12.50, '全年', '12-24岁'),
(1, '儿童票', 6.00, '全年', '4-11岁'),
(2, '成人票', 16.00, '全年', '普通门票'),
(2, '优惠票', 10.00, '全年', '欧盟居民18-25岁');

-- 插入示例交通方式
INSERT INTO transportation (attraction_id, type, route_info, duration, cost, tips) VALUES
(1, '地铁', '乘坐6号线至Bir-Hakeim站', '15分钟', 1.90, '出站步行5分钟'),
(1, '公交', '42、69、72、82路至Champ de Mars站', '25分钟', 1.90, '直接到达景点门口'),
(1, '步行', '从战神广场步行', '10分钟', 0, '欣赏沿途风景'),
(2, '地铁', '乘坐B号线至Colosseo站', '10分钟', 1.50, '出站即达'),
(2, '公交', '75、81、85路至Colosseo站', '20分钟', 1.50, '避免高峰期');

-- 插入示例用户
INSERT INTO users (username, email, password_hash, nickname, bio) VALUES
('travel_lover', 'user1@example.com', 'hash123', '旅行达人', '热爱探索世界的每一个角落'),
('photo_enthusiast', 'user2@example.com', 'hash456', '摄影爱好者', '用镜头记录美好瞬间');

-- 插入示例评论
INSERT INTO reviews (attraction_id, user_id, rating, title, content, visit_date, travel_with, is_verified) VALUES
(1, 1, 4.5, '浪漫的巴黎地标', '非常震撼的建筑，晚上灯光秀特别美。建议提前购票避免排队。', '2024-06-15', '伴侣', TRUE),
(1, 2, 5.0, '完美的拍照地点', '从各个角度拍都很美，建议黄昏时分去，可以看到日落和夜景。', '2024-07-20', '独自', TRUE),
(2, 1, 4.8, '穿越时空的体验', '古罗马的辉煌在这里展现，建议租用语音导览。', '2024-05-10', '朋友', TRUE);
4. 完整的关联关系图
text
continents (1) ────< (n) countries
      ↑                        ↑
      |                        |
      |                        |
countries (1) ────< (n) cities
      ↑                        ↑
      |                        |
      |                        |
cities (1) ───────< (n) attractions
                        ↑
                        |
            ┌───────────┼───────────┐
            │           │           │
            │           │           │
        tags <        images     ticket_prices
            │                       ↑
            │                       │
            └───> attraction_tags    │
                                    │
                              transportation
                                    ↑
                                    │
                              reviews <─── users
                                    ↑
                                    │
                              review_replies