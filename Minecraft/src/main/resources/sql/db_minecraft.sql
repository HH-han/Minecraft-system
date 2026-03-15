create table attraction
(
    id            bigint auto_increment comment '景点ID'
        primary key,
    name          varchar(100)                       not null comment '景点名称',
    city          varchar(50)                        not null comment '城市',
    province      varchar(50)                        not null comment '省份',
    address       varchar(255)                       null comment '地址',
    description   text                               null comment '描述',
    cover_image   varchar(255)                       null comment '封面图',
    images        text                               null comment '图片列表',
    price         decimal(10, 2)                     null comment '价格',
    season        varchar(20)                        null comment '季节',
    rating        int      default 0                 null comment '评分',
    like_count    int      default 0                 null comment '点赞数',
    collect_count int      default 0                 null comment '收藏数',
    comment_count int      default 0                 null comment '评论数',
    tags          varchar(255)                       null comment '标签',
    status        int      default 1                 null comment '状态 0-下架 1-上架',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '景点表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_city
    on attraction (city);

create index idx_like_count
    on attraction (like_count);

create index idx_rating
    on attraction (rating);

create index idx_season
    on attraction (season);

create table carousel_groups
(
    id                 int auto_increment
        primary key,
    group_name         varchar(100)                                                          not null comment '分组名称',
    group_code         varchar(50)                                                           not null comment '分组代码(用于前端调用)',
    description        varchar(500)                                                          null comment '分组描述',
    display_type       enum ('single', 'multiple', 'fullscreen')   default 'multiple'        null comment '展示类型',
    auto_play          tinyint(1)                                  default 1                 null comment '是否自动播放',
    auto_play_interval int                                         default 5000              null comment '自动播放间隔(毫秒)',
    animation_type     enum ('slide', 'fade', 'cube', 'coverflow') default 'slide'           null comment '切换动画',
    show_indicators    tinyint(1)                                  default 1                 null comment '是否显示指示点',
    show_arrows        tinyint(1)                                  default 1                 null comment '是否显示箭头',
    pause_on_hover     tinyint(1)                                  default 1                 null comment '鼠标悬停时暂停',
    width              int                                                                   null comment '宽度(像素)',
    height             int                                                                   null comment '高度(像素)',
    aspect_ratio       varchar(20)                                                           null comment '宽高比(如16:9)',
    max_items          int                                         default 10                null comment '最大显示数量',
    is_active          tinyint(1)                                  default 1                 null comment '是否启用',
    sort_order         int                                         default 0                 null comment '排序',
    created_at         timestamp                                   default CURRENT_TIMESTAMP null,
    updated_at         timestamp                                   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint group_code
        unique (group_code)
)
    comment '轮播分组表';

create table carousels
(
    id               int auto_increment
        primary key,
    title            varchar(200)                                                                                   not null comment '轮播标题',
    subtitle         varchar(500)                                                                                   null comment '副标题/描述',
    image_url        varchar(500)                                                                                   not null comment '图片URL',
    mobile_image_url varchar(500)                                                                                   null comment '移动端图片URL(适配手机)',
    thumbnail_url    varchar(500)                                                                                   null comment '缩略图URL',
    link_type        enum ('none', 'url', 'attraction', 'city', 'country', 'custom_page') default 'none'            null comment '链接类型',
    link_url         varchar(500)                                                                                   null comment '跳转链接URL',
    link_target      enum ('_self', '_blank')                                             default '_self'           null comment '链接打开方式',
    target_id        int                                                                                            null comment '目标ID(当link_type为attraction/city/country时使用)',
    button_text      varchar(50)                                                                                    null comment '按钮文字',
    button_color     varchar(20)                                                                                    null comment '按钮颜色',
    text_color       varchar(20)                                                          default '#FFFFFF'         null comment '文字颜色',
    text_shadow      tinyint(1)                                                           default 1                 null comment '文字阴影',
    overlay_color    varchar(20)                                                                                    null comment '遮罩层颜色',
    overlay_opacity  decimal(2, 1)                                                        default 0.3               null comment '遮罩层透明度(0-1)',
    position         varchar(50)                                                                                    null comment '展示位置(home/home_top/home_middle/home_bottom/attraction_page等)',
    device_type      enum ('all', 'pc', 'mobile')                                         default 'all'             null comment '适配设备',
    user_type        enum ('all', 'visitor', 'logged_in', 'vip')                          default 'all'             null comment '用户类型',
    start_time       datetime                                                                                       null comment '开始展示时间',
    end_time         datetime                                                                                       null comment '结束展示时间',
    is_always_show   tinyint(1)                                                           default 1                 null comment '是否永久展示',
    click_count      int                                                                  default 0                 null comment '点击次数',
    impression_count int                                                                  default 0                 null comment '展示次数',
    sort_order       int                                                                  default 0                 null comment '排序(数字越小越靠前)',
    is_active        tinyint(1)                                                           default 1                 null comment '是否启用',
    is_deleted       tinyint(1)                                                           default 0                 null comment '是否删除(软删除)',
    remark           varchar(500)                                                                                   null comment '备注',
    created_by       varchar(50)                                                                                    null comment '创建人',
    created_at       timestamp                                                            default CURRENT_TIMESTAMP null,
    updated_at       timestamp                                                            default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '轮播图表';

create table carousel_group_items
(
    id          int auto_increment
        primary key,
    group_id    int                                  not null comment '分组ID',
    carousel_id int                                  not null comment '轮播ID',
    sort_order  int        default 0                 null comment '在分组内的排序',
    is_active   tinyint(1) default 1                 null comment '在分组内是否启用',
    created_at  timestamp  default CURRENT_TIMESTAMP null,
    constraint unique_group_carousel
        unique (group_id, carousel_id),
    constraint carousel_group_items_ibfk_1
        foreign key (group_id) references carousel_groups (id)
            on delete cascade,
    constraint carousel_group_items_ibfk_2
        foreign key (carousel_id) references carousels (id)
            on delete cascade
)
    comment '轮播分组项关联表';

create index carousel_id
    on carousel_group_items (carousel_id);

create table carousel_stats
(
    id                      int auto_increment
        primary key,
    carousel_id             int                                 not null comment '轮播ID',
    stat_date               date                                not null comment '统计日期',
    click_count             int       default 0                 null comment '当日点击次数',
    impression_count        int       default 0                 null comment '当日展示次数',
    unique_click_count      int       default 0                 null comment '独立IP点击次数',
    unique_impression_count int       default 0                 null comment '独立IP展示次数',
    pc_click_count          int       default 0                 null comment 'PC端点击',
    mobile_click_count      int       default 0                 null comment '移动端点击',
    pc_impression_count     int       default 0                 null comment 'PC端展示',
    mobile_impression_count int       default 0                 null comment '移动端展示',
    created_at              timestamp default CURRENT_TIMESTAMP null,
    updated_at              timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint unique_carousel_date
        unique (carousel_id, stat_date),
    constraint carousel_stats_ibfk_1
        foreign key (carousel_id) references carousels (id)
            on delete cascade
)
    comment '轮播统计表';

create index idx_click_count
    on carousels (click_count);

create index idx_position_active
    on carousels (position, is_active, sort_order);

create index idx_time_range
    on carousels (start_time, end_time);

create table cart
(
    id          bigint auto_increment comment '购物车ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    item_type   varchar(20)                        not null comment '商品类型 ticket-票务 product-商品',
    item_id     bigint                             not null comment '商品ID',
    item_name   varchar(100)                       null comment '商品名称',
    price       decimal(10, 2)                     null comment '价格',
    quantity    int      default 1                 null comment '数量',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_item
        unique (user_id, item_type, item_id)
)
    comment '购物车表' collate = utf8mb4_unicode_ci
                       row_format = DYNAMIC;

create index idx_user_id
    on cart (user_id);

create table chat_message
(
    id           bigint auto_increment comment '消息ID'
        primary key,
    sender_id    bigint                                not null comment '发送者ID',
    receiver_id  bigint                                not null comment '接收者ID',
    content      text                                  not null comment '消息内容',
    message_type varchar(20) default 'text'            null comment '消息类型 text-文本 image-图片 video-视频 audio-音频',
    is_read      int         default 0                 null comment '是否已读 0-未读 1-已读',
    create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '聊天消息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_create_time
    on chat_message (create_time);

create index idx_receiver_id
    on chat_message (receiver_id);

create index idx_sender_id
    on chat_message (sender_id);

create table collection
(
    id          bigint auto_increment comment '收藏ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    item_type   varchar(20)                        not null comment '收藏类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区',
    item_id     bigint                             not null comment '收藏对象ID',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_user_item
        unique (user_id, item_type, item_id)
)
    comment '收藏表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_item_type_id
    on collection (item_type, item_id);

create table comment
(
    id          bigint auto_increment comment '评论ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    item_type   varchar(20)                        not null comment '评论类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区',
    item_id     bigint                             not null comment '评论对象ID',
    parent_id   bigint   default 0                 null comment '父评论ID 0-顶级评论',
    content     text                               not null comment '评论内容',
    images      text                               null comment '图片列表',
    rating      int                                null comment '评分 1-5星',
    like_count  int      default 0                 null comment '点赞数',
    status      int      default 1                 null comment '状态 0-删除 1-正常',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '评论表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_item_type_id
    on comment (item_type, item_id);

create index idx_parent_id
    on comment (parent_id);

create index idx_user_id
    on comment (user_id);

create table community_post
(
    id            bigint auto_increment comment '帖子ID'
        primary key,
    user_id       bigint                             not null comment '用户ID',
    title         varchar(200)                       null comment '标题',
    content       text                               not null comment '内容',
    images        text                               null comment '图片列表',
    videos        text                               null comment '视频列表',
    like_count    int      default 0                 null comment '点赞数',
    comment_count int      default 0                 null comment '评论数',
    collect_count int      default 0                 null comment '收藏数',
    status        int      default 1                 null comment '状态 0-删除 1-正常',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '社区帖子表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_create_time
    on community_post (create_time);

create index idx_user_id
    on community_post (user_id);

create table continents
(
    id           int auto_increment comment '大洲ID'
        primary key,
    name         varchar(50)                         not null comment '大洲名称',
    chinese_name varchar(50)                         not null comment '中文名称',
    area         decimal(10, 2)                      null comment '面积(万平方公里)',
    population   int                                 null comment '人口(万)',
    description  text                                null comment '描述',
    created_at   timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '大洲表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table countries
(
    id           int auto_increment comment '国家ID'
        primary key,
    name         varchar(100)                        not null comment '国家名称',
    chinese_name varchar(100)                        not null comment '中文名称',
    continent_id int                                 not null comment '所属大洲ID',
    capital      varchar(100)                        null comment '首都',
    area         decimal(10, 2)                      null comment '面积(万平方公里)',
    population   int                                 null comment '人口(万)',
    currency     varchar(50)                         null comment '货币',
    language     varchar(100)                        null comment '官方语言',
    timezone     varchar(50)                         null comment '时区',
    country_code varchar(10)                         null comment '国家代码',
    phone_code   varchar(10)                         null comment '电话区号',
    flag_emoji   varchar(10)                         null comment '国旗emoji',
    description  text                                null comment '简介',
    created_at   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    image        varchar(155)                        null comment '城市图片',
    constraint fk_countries_continent
        foreign key (continent_id) references continents (id)
)
    comment '国家表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table cities
(
    id           int auto_increment comment '城市ID'
        primary key,
    name         varchar(100)                         not null comment '城市名称',
    chinese_name varchar(100)                         not null comment '中文名称',
    country_id   int                                  not null comment '所属国家ID',
    is_capital   tinyint(1) default 0                 null comment '是否是首都',
    area         decimal(8, 2)                        null comment '面积(平方公里)',
    population   int                                  null comment '人口(万)',
    timezone     varchar(50)                          null comment '时区',
    description  text                                 null comment '城市简介',
    latitude     decimal(10, 8)                       null comment '纬度',
    longitude    decimal(11, 8)                       null comment '经度',
    famous_for   varchar(255)                         null comment '闻名于世的原因',
    best_season  varchar(100)                         null comment '最佳旅游季节',
    created_at   timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint fk_cities_country
        foreign key (country_id) references countries (id)
)
    comment '城市表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table attractions
(
    id               int auto_increment comment '景点ID'
        primary key,
    name             varchar(200)                        not null comment '景点名称',
    chinese_name     varchar(200)                        not null comment '中文名称',
    city_id          int                                 not null comment '所在城市ID',
    type             varchar(50)                         null comment '类型(古迹/自然/现代建筑等)',
    rating           decimal(2, 1)                       null comment '评分(1-5)',
    price_level      varchar(20)                         null comment '消费水平(免费/便宜/中等/昂贵)',
    visiting_hours   varchar(200)                        null comment '开放时间',
    best_season      varchar(100)                        null comment '最佳游览季节',
    visit_duration   varchar(50)                         null comment '建议游览时长',
    description      text                                null comment '详细描述',
    history          text                                null comment '历史背景',
    tips             text                                null comment '游玩建议',
    address          varchar(255)                        null comment '具体地址',
    latitude         decimal(10, 8)                      null comment '纬度',
    longitude        decimal(11, 8)                      null comment '经度',
    image_url        varchar(500)                        null comment '主图URL',
    official_website varchar(500)                        null comment '官方网站',
    year_built       varchar(50)                         null comment '建造年份',
    architect        varchar(200)                        null comment '建筑师',
    created_at       timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at       timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_attractions_city
        foreign key (city_id) references cities (id)
)
    comment '景点表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table attraction_images
(
    id            int auto_increment comment '图片ID'
        primary key,
    attraction_id int                                  not null comment '景点ID',
    image_url     varchar(500)                         not null comment '图片URL',
    thumbnail_url varchar(500)                         null comment '缩略图URL',
    title         varchar(200)                         null comment '图片标题',
    description   text                                 null comment '图片描述',
    photographer  varchar(100)                         null comment '摄影师',
    license       varchar(50)                          null comment '版权信息',
    is_primary    tinyint(1) default 0                 null comment '是否是主图',
    sort_order    int        default 0                 null comment '排序',
    width         int                                  null comment '图片宽度',
    height        int                                  null comment '图片高度',
    file_size     int                                  null comment '文件大小(KB)',
    upload_time   timestamp  default CURRENT_TIMESTAMP not null comment '上传时间',
    constraint fk_attraction_images_attraction
        foreign key (attraction_id) references attractions (id)
            on delete cascade
)
    comment '图片表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_attraction_id
    on attraction_images (attraction_id);

create index idx_city_id
    on attractions (city_id);

create index idx_country_id
    on cities (country_id);

create index idx_continent_id
    on countries (continent_id);

create table file_info
(
    id            bigint auto_increment comment '文件ID'
        primary key,
    file_name     varchar(255)                       not null comment '文件名',
    original_name varchar(255)                       null comment '原始文件名',
    file_path     varchar(500)                       not null comment '文件路径',
    file_type     varchar(50)                        null comment '文件类型',
    file_size     bigint                             null comment '文件大小',
    chunk_index   int      default 0                 null comment '分片索引',
    total_chunks  int      default 1                 null comment '总分片数',
    file_md5      varchar(32)                        null comment '文件MD5',
    status        int      default 0                 null comment '状态 0-上传中 1-上传完成',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '文件信息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_file_md5
    on file_info (file_md5);

create table food
(
    id            bigint auto_increment comment '美食ID'
        primary key,
    name          varchar(100)                       not null comment '美食名称',
    city          varchar(50)                        not null comment '城市',
    province      varchar(50)                        not null comment '省份',
    address       varchar(255)                       null comment '地址',
    description   text                               null comment '描述',
    cover_image   varchar(255)                       null comment '封面图',
    images        text                               null comment '图片列表',
    price         decimal(10, 2)                     null comment '价格',
    cuisine_type  varchar(50)                        null comment '菜系类型',
    rating        int      default 0                 null comment '评分',
    like_count    int      default 0                 null comment '点赞数',
    collect_count int      default 0                 null comment '收藏数',
    comment_count int      default 0                 null comment '评论数',
    tags          varchar(255)                       null comment '标签',
    status        int      default 1                 null comment '状态 0-下架 1-上架',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '美食表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_city
    on food (city);

create index idx_cuisine_type
    on food (cuisine_type);

create index idx_like_count
    on food (like_count);

create index idx_rating
    on food (rating);

create table friend
(
    id          bigint auto_increment comment '好友ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    friend_id   bigint                             not null comment '好友ID',
    remark      varchar(50)                        null comment '备注',
    status      int      default 0                 null comment '状态 0-待确认 1-已添加',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_friend
        unique (user_id, friend_id)
)
    comment '好友表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_status
    on friend (status);

create table group_travel
(
    id              bigint auto_increment comment '跟团游ID'
        primary key,
    creator_id      bigint                             not null comment '创建者ID',
    name            varchar(100)                       not null comment '团名',
    description     text                               null comment '描述',
    destination     varchar(100)                       null comment '目的地',
    departure_time  datetime                           null comment '出发时间',
    return_time     datetime                           null comment '返回时间',
    max_members     int                                null comment '最大人数',
    current_members int      default 0                 null comment '当前人数',
    price           decimal(10, 2)                     null comment '价格',
    images          text                               null comment '图片列表',
    status          int      default 1                 null comment '状态 0-取消 1-正常',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '跟团游表' collate = utf8mb4_unicode_ci
                       row_format = DYNAMIC;

create index idx_creator_id
    on group_travel (creator_id);

create index idx_departure_time
    on group_travel (departure_time);

create table home_recommendations
(
    id                        bigint unsigned auto_increment comment '主键ID，自增长'
        primary key,
    uuid                      char(36)                                         not null comment '全局唯一标识符，用于对外接口',
    name                      varchar(200)                                     not null comment '景点/项目名称',
    english_name              varchar(500)                                     null comment '英文名称（如果有）',
    alias_name                varchar(500)                                     null comment '别名/俗称',
    category_id               int unsigned                                     not null comment '分类ID（如：1-自然风光，2-历史文化，3-主题乐园，4-美食探店，5-休闲度假）',
    category_name             varchar(100)                                     not null comment '分类名称（冗余字段，便于查询）',
    tags                      varchar(500)                                     null comment '标签，多个用逗号分隔（如：亲子,情侣,网红打卡,拍照圣地）',
    country                   varchar(100)           default '中国'            not null comment '国家',
    province                  varchar(100)                                     not null comment '省份/直辖市',
    city                      varchar(100)                                     not null comment '城市',
    district                  varchar(100)                                     null comment '区/县',
    address                   varchar(500)                                     not null comment '详细地址',
    longitude                 decimal(10, 7)                                   null comment '经度（WGS84坐标系）',
    latitude                  decimal(10, 7)                                   null comment '纬度（WGS84坐标系）',
    google_map_url            varchar(1000)                                    null comment 'Google地图链接',
    baidu_map_url             varchar(1000)                                    null comment '百度地图链接',
    transportation            text                                             null comment '交通方式描述（如：地铁X号线XX站A口出，步行500米）',
    recommendation_reason     text                                             not null comment '推荐理由（首页展示的核心文案）',
    highlights                text                                             null comment '亮点特色（多个亮点可用分号分隔）',
    best_travel_time          varchar(200)                                     null comment '最佳游玩时间（如：春秋两季，9月-11月）',
    visit_duration            varchar(100)                                     null comment '建议游玩时长（如：3-4小时，1天）',
    cover_image_url           varchar(1000)                                    not null comment '封面图片URL（首页列表展示）',
    banner_image_url          varchar(1000)                                    null comment '详情页Banner图URL',
    video_url                 varchar(1000)                                    null comment '宣传视频URL',
    image_urls                text                                             null comment '更多图片URL，JSON数组格式存储',
    ticket_price_description  varchar(500)                                     null comment '门票价格描述（如：成人票100元，学生票50元）',
    min_price                 decimal(10, 2) unsigned                          null comment '最低价格（用于价格筛选）',
    max_price                 decimal(10, 2) unsigned                          null comment '最高价格（用于价格筛选）',
    price_currency            varchar(10)            default 'CNY'             null comment '货币单位（CNY, USD, etc.）',
    is_free                   tinyint(1)             default 0                 null comment '是否免费（TRUE-免费，FALSE-收费）',
    opening_hours_weekday     varchar(200)                                     null comment '工作日开放时间（如：09:00-18:00）',
    opening_hours_weekend     varchar(200)                                     null comment '周末开放时间（如：09:00-20:00）',
    opening_hours_description text                                             null comment '开放时间详细描述（如：每周一闭馆，节假日正常开放）',
    contact_phone             varchar(200)                                     null comment '联系电话（多个用逗号分隔）',
    official_website          varchar(500)                                     null comment '官方网站',
    social_media              json                                             null comment '社交媒体信息（JSON格式，如：{"weibo":"xxx", "wechat":"xxx", "douyin":"xxx"}）',
    view_count                int unsigned           default '0'               not null comment '浏览次数',
    like_count                int unsigned           default '0'               not null comment '点赞/收藏次数',
    share_count               int unsigned           default '0'               not null comment '分享次数',
    comment_count             int unsigned           default '0'               not null comment '评论次数',
    average_rating            decimal(3, 2) unsigned default 0.00              null comment '平均评分（满分5分）',
    rating_count              int unsigned           default '0'               not null comment '评分人数',
    weight                    int                    default 0                 not null comment '推荐权重（数字越大，排名越靠前）',
    is_hot                    tinyint(1)             default 0                 null comment '是否热门推荐',
    is_new                    tinyint(1)             default 0                 null comment '是否新晋推荐',
    is_top                    tinyint(1)             default 0                 null comment '是否置顶',
    status                    tinyint unsigned       default '1'               not null comment '状态：0-下架，1-上架，2-审核中，3-待发布',
    season                    varchar(50)                                      null comment '推荐季节（spring, summer, autumn, winter, all）',
    festival                  varchar(100)                                     null comment '关联节日（如：春节，国庆，圣诞节）',
    tips                      text                                             null comment '游玩小贴士/注意事项',
    suitable_crowd            varchar(200)                                     null comment '适合人群（如：亲子，情侣，朋友，家庭，老人）',
    facility_info             text                                             null comment '设施信息（如：有无停车场，有无母婴室，无障碍设施等）',
    seo_title                 varchar(500)                                     null comment 'SEO标题',
    seo_keywords              varchar(500)                                     null comment 'SEO关键词',
    seo_description           text                                             null comment 'SEO描述',
    content                   text                                             null comment '详细介绍内容（富文本或Markdown）',
    summary                   varchar(1000)                                    null comment '摘要/简介（用于列表页）',
    source                    varchar(200)                                     null comment '信息来源（如：小编踩点，用户投稿，官方合作）',
    author                    varchar(100)                                     null comment '作者/编辑',
    created_at                timestamp              default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at                timestamp              default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    published_at              timestamp                                        null comment '发布时间',
    expired_at                timestamp                                        null comment '过期时间（NULL表示永不过期）',
    constraint uk_uuid
        unique (uuid)
)
    comment '首页旅游推荐内容表' collate = utf8mb4_unicode_ci;

create fulltext index ft_content
    on home_recommendations (name, summary, content);

create index idx_category
    on home_recommendations (category_id, status, weight);

create index idx_city
    on home_recommendations (province, city, status);

create index idx_hot
    on home_recommendations (is_hot, weight);

create index idx_location
    on home_recommendations (longitude, latitude);

create index idx_new
    on home_recommendations (is_new, created_at);

create index idx_price
    on home_recommendations (min_price, max_price);

create index idx_season
    on home_recommendations (season);

create index idx_status_published
    on home_recommendations (status, published_at);

create table hotel
(
    id            bigint auto_increment comment '酒店ID'
        primary key,
    name          varchar(100)                       not null comment '酒店名称',
    city          varchar(50)                        not null comment '城市',
    province      varchar(50)                        not null comment '省份',
    address       varchar(255)                       null comment '地址',
    description   text                               null comment '描述',
    cover_image   varchar(255)                       null comment '封面图',
    images        text                               null comment '图片列表',
    price         decimal(10, 2)                     null comment '价格',
    star_level    int                                null comment '星级',
    rating        int      default 0                 null comment '评分',
    like_count    int      default 0                 null comment '点赞数',
    collect_count int      default 0                 null comment '收藏数',
    comment_count int      default 0                 null comment '评论数',
    facilities    varchar(255)                       null comment '设施',
    status        int      default 1                 null comment '状态 0-下架 1-上架',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '酒店表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_city
    on hotel (city);

create index idx_like_count
    on hotel (like_count);

create index idx_rating
    on hotel (rating);

create index idx_star_level
    on hotel (star_level);

create table like_record
(
    id          bigint auto_increment comment '点赞ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    item_type   varchar(20)                        not null comment '点赞类型 attraction-景点 hotel-酒店 food-美食 product-商品 news-新闻 community-社区 comment-评论',
    item_id     bigint                             not null comment '点赞对象ID',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_user_item
        unique (user_id, item_type, item_id)
)
    comment '点赞记录表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_item_type_id
    on like_record (item_type, item_id);

create table login_info
(
    id             bigint auto_increment comment '登录ID'
        primary key,
    user_id        bigint                             not null comment '用户ID',
    username       varchar(50)                        not null comment '用户名',
    ipaddr         varchar(50)                        not null comment 'IP地址',
    login_location varchar(255)                       null comment '登录地点',
    browser        varchar(100)                       null comment '浏览器',
    os             varchar(100)                       null comment '操作系统',
    status         varchar(10)                        not null comment '登录状态 0-失败 1-成功',
    msg            varchar(255)                       null comment '提示消息',
    login_time     datetime default CURRENT_TIMESTAMP not null comment '登录时间'
)
    comment '登录信息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_ipaddr
    on login_info (ipaddr);

create index idx_login_time
    on login_info (login_time);

create index idx_user_id
    on login_info (user_id);

create table months
(
    id          int auto_increment comment '月份ID，自增'
        primary key,
    month_name  varchar(20)                          not null comment '月份名称（如：一月、二月等）',
    month_order tinyint                              not null comment '月份顺序（1-12）',
    is_active   tinyint(1) default 1                 null comment '是否启用（0：禁用，1：启用）',
    created_at  timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at  timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '月份配置表';

create table monthly_recommendations
(
    id               int auto_increment comment '推荐ID，自增'
        primary key,
    month_id         int                                  not null comment '关联月份ID',
    destination_name varchar(255)                         not null comment '目的地名称',
    image_url        text                                 null comment '图片URL',
    description      text                                 null comment '目的地描述',
    tags             json                                 null comment '标签（JSON数组格式，如：["雪山","滑雪","温泉"]）',
    season_features  varchar(500)                         null comment '当季特色',
    climate_info     varchar(500)                         null comment '气候信息',
    travel_tips      text                                 null comment '旅行小贴士',
    recommended_days tinyint                              null comment '建议游玩天数',
    sort_order       int        default 0                 null comment '排序顺序（数字越小越靠前）',
    is_recommended   tinyint(1) default 1                 null comment '是否推荐（0：不推荐，1：推荐）',
    view_count       int        default 0                 null comment '浏览次数',
    created_at       timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at       timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint monthly_recommendations_ibfk_1
        foreign key (month_id) references months (id)
            on delete cascade
)
    comment '月份旅行推荐内容表';

create index idx_is_recommended
    on monthly_recommendations (is_recommended)
    comment '推荐状态索引';

create index idx_month_id
    on monthly_recommendations (month_id)
    comment '月份ID索引';

create index idx_sort_order
    on monthly_recommendations (sort_order)
    comment '排序索引';

create table news
(
    id            bigint auto_increment comment '新闻ID'
        primary key,
    title         varchar(200)                       not null comment '标题',
    content       text                               null comment '内容',
    cover_image   varchar(255)                       null comment '封面图',
    source        varchar(100)                       null comment '来源',
    view_count    int      default 0                 null comment '浏览数',
    like_count    int      default 0                 null comment '点赞数',
    collect_count int      default 0                 null comment '收藏数',
    comment_count int      default 0                 null comment '评论数',
    status        int      default 1                 null comment '状态 0-下架 1-上架',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '新闻资讯表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_like_count
    on news (like_count);

create index idx_view_count
    on news (view_count);

create table orders
(
    id          bigint auto_increment comment '订单ID'
        primary key,
    user_id     bigint                                not null comment '用户ID',
    order_no    varchar(50)                           not null comment '订单号',
    item_type   varchar(20)                           not null comment '商品类型 ticket-票务 product-商品',
    item_id     bigint                                not null comment '商品ID',
    item_name   varchar(100)                          null comment '商品名称',
    amount      decimal(10, 2)                        null comment '订单金额',
    quantity    int         default 1                 null comment '数量',
    remark      varchar(500)                          null comment '备注',
    status      varchar(20) default '0'               null comment '订单状态 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-已退款',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_order_no
        unique (order_no)
)
    comment '订单表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_status
    on orders (status);

create index idx_user_id
    on orders (user_id);

create table payment
(
    id             bigint auto_increment comment '支付ID'
        primary key,
    order_id       bigint                                not null comment '订单ID',
    user_id        bigint                                not null comment '用户ID',
    payment_no     varchar(50)                           null comment '支付流水号',
    amount         decimal(10, 2)                        null comment '支付金额',
    payment_method varchar(20)                           null comment '支付方式 alipay-支付宝 wechat-微信 card-银行卡',
    status         varchar(20) default '0'               null comment '支付状态 0-待支付 1-支付成功 2-支付失败 3-已取消 4-退款中 5-已退款',
    create_time    datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_payment_no
        unique (payment_no)
)
    comment '支付表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_order_id
    on payment (order_id);

create index idx_user_id
    on payment (user_id);

create table product
(
    id            bigint auto_increment comment '商品ID'
        primary key,
    name          varchar(100)                       not null comment '商品名称',
    type          varchar(50)                        null comment '类型',
    city          varchar(50)                        not null comment '城市',
    province      varchar(50)                        not null comment '省份',
    description   text                               null comment '描述',
    cover_image   varchar(255)                       null comment '封面图',
    images        text                               null comment '图片列表',
    price         decimal(10, 2)                     null comment '价格',
    stock         int      default 0                 null comment '库存',
    rating        int      default 0                 null comment '评分',
    like_count    int      default 0                 null comment '点赞数',
    collect_count int      default 0                 null comment '收藏数',
    comment_count int      default 0                 null comment '评论数',
    tags          varchar(255)                       null comment '标签',
    status        int      default 1                 null comment '状态 0-下架 1-上架',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '特产/纪念品表' collate = utf8mb4_unicode_ci
                            row_format = DYNAMIC;

create index idx_city
    on product (city);

create index idx_like_count
    on product (like_count);

create index idx_rating
    on product (rating);

create index idx_type
    on product (type);

create table recommend
(
    id          int auto_increment
        primary key,
    image       varchar(255)  null,
    name        varchar(255)  not null,
    description varchar(255)  null,
    details     varchar(5000) null,
    state       int default 0 null comment '状态',
    type        int           null comment '类型'
)
    row_format = DYNAMIC;

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

create table tags
(
    id           int auto_increment comment '标签ID'
        primary key,
    name         varchar(50)                         not null comment '标签名称',
    chinese_name varchar(50)                         not null comment '中文名称',
    category     varchar(50)                         null comment '标签分类(主题/适合人群/特色等)',
    icon         varchar(100)                        null comment '标签图标',
    color        varchar(20)                         null comment '标签颜色',
    sort_order   int       default 0                 null comment '排序',
    created_at   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint name
        unique (name)
)
    comment '标签表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table attraction_tags
(
    id            int auto_increment comment '景点-标签关联ID'
        primary key,
    attraction_id int                                 not null comment '景点ID',
    tag_id        int                                 not null comment '标签ID',
    created_at    timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint unique_attraction_tag
        unique (attraction_id, tag_id),
    constraint fk_attraction_tags_attraction
        foreign key (attraction_id) references attractions (id)
            on delete cascade,
    constraint fk_attraction_tags_tag
        foreign key (tag_id) references tags (id)
            on delete cascade
)
    comment '景点-标签关联表' collate = utf8mb4_unicode_ci
                              row_format = DYNAMIC;

create index idx_attraction_id
    on attraction_tags (attraction_id);

create index idx_tag_id
    on attraction_tags (tag_id);

create table ticket
(
    id             bigint auto_increment comment '票务ID'
        primary key,
    type           varchar(20)                        not null comment '类型 train-火车 flight-飞机 taxi-出租车',
    departure_city varchar(50)                        not null comment '出发城市',
    arrival_city   varchar(50)                        not null comment '到达城市',
    departure_time varchar(20)                        null comment '出发时间',
    arrival_time   varchar(20)                        null comment '到达时间',
    carrier        varchar(50)                        null comment '承运商',
    price          decimal(10, 2)                     null comment '价格',
    stock          int      default 0                 null comment '库存',
    seat_class     varchar(20)                        null comment '座位等级',
    status         int      default 1                 null comment '状态 0-下架 1-上架',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '票务表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_arrival_city
    on ticket (arrival_city);

create index idx_departure_city
    on ticket (departure_city);

create index idx_type
    on ticket (type);

create table ticket_prices
(
    id            int auto_increment comment '门票价格ID'
        primary key,
    attraction_id int                                   not null comment '景点ID',
    ticket_type   varchar(50)                           not null comment '门票类型(成人票/学生票/儿童票/老人票)',
    price         decimal(10, 2)                        not null comment '价格',
    currency      varchar(10) default 'CNY'             null comment '货币单位',
    season        varchar(50)                           null comment '适用季节(旺季/淡季/全年)',
    start_date    date                                  null comment '有效期开始',
    end_date      date                                  null comment '有效期结束',
    description   varchar(255)                          null comment '价格说明',
    purchase_url  varchar(500)                          null comment '购票链接',
    discount_info varchar(255)                          null comment '优惠信息',
    created_at    timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_ticket_prices_attraction
        foreign key (attraction_id) references attractions (id)
            on delete cascade
)
    comment '门票价格表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_attraction_id
    on ticket_prices (attraction_id);

create table transportation
(
    id             int auto_increment comment '交通方式ID'
        primary key,
    attraction_id  int                                   not null comment '景点ID',
    type           varchar(50)                           not null comment '交通类型(公交/地铁/出租车/步行/缆车等)',
    route_info     text                                  null comment '路线信息',
    from_location  varchar(255)                          null comment '起始地点',
    duration       varchar(50)                           null comment '所需时间',
    distance       decimal(8, 2)                         null comment '距离(公里)',
    cost           decimal(8, 2)                         null comment '费用',
    currency       varchar(10) default 'CNY'             null comment '货币单位',
    schedule       varchar(255)                          null comment '运营时间',
    tips           text                                  null comment '交通提示',
    is_recommended tinyint(1)  default 0                 null comment '是否推荐',
    sort_order     int         default 0                 null comment '排序',
    created_at     timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint fk_transportation_attraction
        foreign key (attraction_id) references attractions (id)
            on delete cascade
)
    comment '交通方式表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_attraction_id
    on transportation (attraction_id);

create table travel_plan
(
    id          bigint auto_increment comment '计划ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    title       varchar(200)                       not null comment '标题',
    description text                               null comment '描述',
    start_date  date                               null comment '开始日期',
    end_date    date                               null comment '结束日期',
    city        varchar(50)                        null comment '城市',
    content     text                               null comment '计划内容',
    status      int      default 1                 null comment '状态 0-删除 1-正常',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '出行计划表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_start_date
    on travel_plan (start_date);

create index idx_user_id
    on travel_plan (user_id);

create table user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    account     varchar(50)                        not null comment '账号',
    username    varchar(50)                        not null comment '用户名',
    nickname    varchar(50)                        null comment '昵称',
    password    varchar(255)                       not null comment '密码',
    email       varchar(100)                       not null comment '邮箱',
    phone       varchar(20)                        null comment '手机号',
    avatar      varchar(255)                       null comment '头像',
    gender      varchar(10)                        null comment '性别',
    age         int                                null comment '年龄',
    occupation  varchar(50)                        null comment '职业',
    hobbies     varchar(255)                       null comment '爱好',
    bio         varchar(500)                       null comment '个人简介',
    signature   varchar(255)                       null comment '个性签名',
    experience  varchar(255)                       null comment '经验值',
    online      int      default 0                 null comment '在线状态 0-离线 1-在线',
    status      int      default 1                 null comment '状态 0-禁用 1-正常',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_account
        unique (account),
    constraint uk_email
        unique (email),
    constraint uk_username
        unique (username)
)
    comment '用户表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table reviews
(
    id            int auto_increment comment '评论ID'
        primary key,
    attraction_id int                                                                not null comment '景点ID',
    user_id       bigint                                                             not null comment '用户ID',
    rating        decimal(2, 1)                                                      not null comment '评分(1-5)',
    title         varchar(200)                                                       null comment '评论标题',
    content       text                                                               not null comment '评论内容',
    visit_date    date                                                               null comment '游玩日期',
    travel_with   varchar(50)                                                        null comment '和谁一起(独自/伴侣/家庭/朋友)',
    photos_count  int                                      default 0                 null comment '照片数量',
    likes_count   int                                      default 0                 null comment '点赞数',
    helpful_count int                                      default 0                 null comment '有用数',
    is_verified   tinyint(1)                               default 0                 null comment '是否验证过',
    status        enum ('pending', 'approved', 'rejected') default 'pending'         null comment '审核状态',
    created_at    timestamp                                default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp                                default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_reviews_attraction
        foreign key (attraction_id) references attractions (id)
            on delete cascade,
    constraint fk_reviews_user
        foreign key (user_id) references user (id)
            on delete cascade
)
    comment '评论表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create table review_likes
(
    id         int auto_increment comment '评论点赞ID'
        primary key,
    review_id  int                                 not null comment '评论ID',
    user_id    bigint                              not null comment '用户ID',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint unique_review_user
        unique (review_id, user_id),
    constraint fk_review_likes_review
        foreign key (review_id) references reviews (id)
            on delete cascade,
    constraint fk_review_likes_user
        foreign key (user_id) references user (id)
            on delete cascade
)
    comment '评论点赞表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_review_id
    on review_likes (review_id);

create index idx_user_id
    on review_likes (user_id);

create table review_replies
(
    id          int auto_increment comment '评论回复ID'
        primary key,
    review_id   int                                 not null comment '评论ID',
    user_id     bigint                              not null comment '回复用户ID',
    content     text                                not null comment '回复内容',
    likes_count int       default 0                 null comment '点赞数',
    created_at  timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_review_replies_review
        foreign key (review_id) references reviews (id)
            on delete cascade,
    constraint fk_review_replies_user
        foreign key (user_id) references user (id)
            on delete cascade
)
    comment '评论回复表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index idx_review_id
    on review_replies (review_id);

create index idx_user_id
    on review_replies (user_id);

create index idx_attraction_rating
    on reviews (attraction_id, rating);

create index idx_user_created
    on reviews (user_id, created_at);

create table worldcharacteristics
(
    id          int auto_increment comment '主键ID，自增'
        primary key,
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
    comment '世界特征信息表（存储各国特色建筑、景观、文化等）' row_format = DYNAMIC;

create definer = root@localhost view view_monthly_recommendations as
select `m`.`id`               AS `month_id`,
       `m`.`month_name`       AS `month_name`,
       `m`.`month_order`      AS `month_order`,
       `r`.`id`               AS `recommendation_id`,
       `r`.`destination_name` AS `destination_name`,
       `r`.`image_url`        AS `image_url`,
       `r`.`description`      AS `description`,
       `r`.`tags`             AS `tags`,
       `r`.`season_features`  AS `season_features`,
       `r`.`climate_info`     AS `climate_info`,
       `r`.`travel_tips`      AS `travel_tips`,
       `r`.`recommended_days` AS `recommended_days`,
       `r`.`sort_order`       AS `sort_order`
from (`db_minecraft`.`months` `m` left join `db_minecraft`.`monthly_recommendations` `r`
      on (((`m`.`id` = `r`.`month_id`) and (`r`.`is_recommended` = 1))))
order by `m`.`month_order`, `r`.`sort_order`;

-- comment on column view_monthly_recommendations.month_id not supported: 月份ID，自增

-- comment on column view_monthly_recommendations.month_name not supported: 月份名称（如：一月、二月等）

-- comment on column view_monthly_recommendations.month_order not supported: 月份顺序（1-12）

-- comment on column view_monthly_recommendations.recommendation_id not supported: 推荐ID，自增

-- comment on column view_monthly_recommendations.destination_name not supported: 目的地名称

-- comment on column view_monthly_recommendations.image_url not supported: 图片URL

-- comment on column view_monthly_recommendations.description not supported: 目的地描述

-- comment on column view_monthly_recommendations.tags not supported: 标签（JSON数组格式，如：["雪山","滑雪","温泉"]）

-- comment on column view_monthly_recommendations.season_features not supported: 当季特色

-- comment on column view_monthly_recommendations.climate_info not supported: 气候信息

-- comment on column view_monthly_recommendations.travel_tips not supported: 旅行小贴士

-- comment on column view_monthly_recommendations.recommended_days not supported: 建议游玩天数

-- comment on column view_monthly_recommendations.sort_order not supported: 排序顺序（数字越小越靠前）

create
definer = root@localhost procedure get_recommendations_by_month(IN p_month_name varchar(20))
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
END;

CREATE TABLE `login_log` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
                             `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
                             `ipaddr` VARCHAR(128) DEFAULT NULL COMMENT '登录IP地址',
                             `login_location` VARCHAR(255) DEFAULT NULL COMMENT '登录地点',
                             `browser` VARCHAR(50) DEFAULT NULL COMMENT '浏览器类型',
                             `os` VARCHAR(50) DEFAULT NULL COMMENT '操作系统',
                             `status` VARCHAR(10) DEFAULT NULL COMMENT '登录状态（0成功 1失败）',
                             `msg` VARCHAR(255) DEFAULT NULL COMMENT '提示消息',
                             `login_time` DATETIME DEFAULT NULL COMMENT '登录时间',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 添加索引（可选，根据查询需求）
CREATE INDEX `idx_user_id` ON `login_log`(`user_id`);
CREATE INDEX `idx_login_time` ON `login_log`(`login_time`);
CREATE INDEX `idx_username` ON `login_log`(`username`);