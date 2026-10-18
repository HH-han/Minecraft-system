基于您提供的《Minecraft 旅游系统》开发文档以及12306的截图，我将为您重新设计**铁路票务模块（12306 仿制模块）**。

该设计将完全融入现有的 Spring Boot 3 + Vue 3 架构，复用现有的 `db_minecraft` 数据库、JWT 鉴权体系、Redis 缓存以及统一的前端 UI 风格（Element Plus）。

---

# 铁路票务系统（12306 模块）集成开发文档

## 一、 项目架构与集成说明
本模块作为 `Minecraft 旅游系统` 的核心子模块（`ticket` 票务模块的扩展），依托现有前后端分离架构：

- **后端**：`Minecraft` 项目，新增 `com.minecraft.controller.railway` 等包路径，复用 MyBatis Plus、Redis、Spring Security。
- **用户端**：`minecraft-ui`，新增 `/railway` 路由，UI 高度还原截图中的蓝白配色、悬浮菜单、卡片式布局。
- **管理端**：`minecraft-admin-main`，新增“铁路管理”菜单，用于维护车站、车次、余票及订单。
- **数据库**：在 `db_minecraft` 中新增以 `railway_` 为前缀的表，避免与现有旅游模块（如现有的 `ticket` 演出票务）冲突。

## 二、 UI/UX 还原设计（基于截图）

根据提供的截图，需要在 `minecraft-ui` 中还原以下视觉与交互元素：

1. **顶部导航栏 (Header)**
    - 左侧：中国铁路 12306 Logo (红标)。
    - 中间：全局搜索框（可搜索车次、攻略等）。
    - 右侧：无障碍 | 敬老版 | English | 我的12306 (下拉) | 登录/注册。
2. **主导航菜单 (Navbar)**
    - 深蓝色背景 `#3b7cff`。
    - 包含：首页、车票、团购服务、会员服务、站车服务、商旅服务、出行指南、信息查询。
    - **悬浮下拉菜单还原**：当鼠标悬浮“车票”时，展示白色大面板，分三列：“购买（单程、往返、中转换乘）”、“变更（退票、改签、变更到站）”、“更多（中铁银通卡、国际列车）”。
3. **核心查询面板 (Search Panel)**
    - 位于首页轮播图上方，白色卡片浮层。
    - 左侧标签页：车票、常用查询、订餐。
    - 表单区：单程/往返/中转换乘/退改签 单选按钮。
    - 输入框：出发地、到达地（带定位 Icon 和交换按钮）、出发日期（带日历 Icon）。
    - 复选框：学生、高铁/动车。
    - 底部：橙色大按钮“查 询”。
4. **底部服务卡片区 (Quick Access)**
    - 包含图标入口：重点旅客预约、遗失物品查找、约车服务、便民托运、车站引导、站车风采、用户反馈。
    - 下方为四宫格 Banner：会员服务、餐饮·特产、铁路保险、计次·定期票。

---

## 三、 数据库设计（追加至 `db_minecraft`）

### 3.1 车站表 `railway_station`
```sql
CREATE TABLE `railway_station` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `station_code` varchar(10) NOT NULL COMMENT '电报码',
  `station_name` varchar(50) NOT NULL COMMENT '车站名',
  `city_name` varchar(50) NOT NULL COMMENT '城市名',
  `pinyin` varchar(100) DEFAULT NULL COMMENT '全拼',
  `initial` char(1) DEFAULT NULL COMMENT '首字母',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`station_code`),
  KEY `idx_city` (`city_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='火车站基础表';
```

### 3.2 车次表 `railway_train`
```sql
CREATE TABLE `railway_train` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `train_no` varchar(20) NOT NULL COMMENT '车次号 G101',
  `train_type` varchar(10) NOT NULL COMMENT 'G/D/Z/T/K',
  `start_station_id` bigint NOT NULL,
  `end_station_id` bigint NOT NULL,
  `status` tinyint DEFAULT '1' COMMENT '1正常 0停运',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_train_no` (`train_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次基础表';
```

### 3.3 每日开行服务与经停站 `railway_service`
```sql
-- 每日运行实例
CREATE TABLE `railway_service` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `train_id` bigint NOT NULL,
  `service_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_train_date` (`train_id`,`service_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 经停站序列（用于计算行程时间）
CREATE TABLE `railway_stop_station` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_id` bigint NOT NULL,
  `station_id` bigint NOT NULL,
  `stop_seq` int NOT NULL COMMENT '站序，从1开始',
  `arrive_time` time DEFAULT NULL,
  `depart_time` time DEFAULT NULL,
  `stop_over` int DEFAULT '0' COMMENT '停留分钟数',
  PRIMARY KEY (`id`),
  KEY `idx_service_seq` (`service_id`,`stop_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.4 区间库存表 `railway_segment_inventory`（核心）
**注意**：库存按“相邻的两个站”进行切割，例如北京-石家庄、石家庄-郑州。买北京-郑州，需要同时扣除这两段。
```sql
CREATE TABLE `railway_segment_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_id` bigint NOT NULL,
  `from_stop_seq` int NOT NULL,
  `to_stop_seq` int NOT NULL COMMENT '= from_stop_seq + 1',
  `seat_type` varchar(20) NOT NULL COMMENT 'SECOND_CLASS, FIRST_CLASS, SLEEPER',
  `total_seats` int NOT NULL,
  `left_seats` int NOT NULL,
  `price_cents` int NOT NULL COMMENT '该段票价(分)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_adj_seg` (`service_id`,`from_stop_seq`,`to_stop_seq`,`seat_type`),
  KEY `idx_service_seat` (`service_id`,`seat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.5 乘车人表 `railway_passenger`
```sql
CREATE TABLE `railway_passenger` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '关联系统用户',
  `name` varchar(50) NOT NULL,
  `id_card_no` varchar(18) NOT NULL,
  `passenger_type` varchar(20) DEFAULT 'ADULT' COMMENT 'ADULT/CHILD/STUDENT',
  `verify_status` tinyint DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_card` (`user_id`,`id_card_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.6 铁路订单表 `railway_order`
```sql
CREATE TABLE `railway_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL,
  `user_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `from_stop_seq` int NOT NULL,
  `to_stop_seq` int NOT NULL,
  `seat_type` varchar(20) NOT NULL,
  `total_amount` int NOT NULL COMMENT '总金额(分)',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PAID/CANCELLED/REFUNDED',
  `expire_time` datetime NOT NULL COMMENT '15分钟支付超时',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.7 车票明细表 `railway_ticket`
```sql
CREATE TABLE `railway_ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `passenger_id` bigint NOT NULL,
  `seat_no` varchar(10) DEFAULT NULL COMMENT '05车12A',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/REFUNDED',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 四、 核心业务逻辑与接口设计

### 4.1 车票查询接口
**`GET /api/railway/tickets/search`**
- **参数**：`fromStation` (如：北京), `toStation` (如：上海), `date` (2026-10-18)
- **逻辑**：
    1. 根据日期和起止站，查询 `railway_service` 及 `railway_stop_station`，找到所有匹配的 `service_id` 及站序 (`from_seq`, `to_seq`)。
    2. **余票计算 SQL**：查询目标车次在该起止区间内所有相邻段的 `left_seats`，取**最小值**。
       ```sql
       SELECT MIN(left_seats) FROM railway_segment_inventory 
       WHERE service_id = ? AND seat_type = ? 
       AND from_stop_seq >= ? AND to_stop_seq <= ?;
       ```
    3. 返回车次信息、起止时间、历时、各席别余票及票价。

### 4.2 提交订单（占座扣减库存）
**`POST /api/railway/orders`**
- **事务处理**：
    1. 校验当前用户/乘车人是否已购买同日同车次车票。
    2. **逐段扣减库存**（利用 Redis 分布式锁 + MySQL 行锁防超卖）：
       ```java
       for (int seq = fromSeq; seq < toSeq; seq++) {
           // UPDATE railway_segment_inventory SET left_seats = left_seats - 1 
           // WHERE service_id = ? AND from_stop_seq = seq AND to_stop_seq = seq+1 AND left_seats > 0
           int updated = inventoryMapper.decrement(...);
           if (updated == 0) {
               throw new RuntimeException("余票不足");
           }
       }
       ```
    3. 生成 `railway_order` 状态为 `PENDING`，过期时间设为 `now + 15分钟`。
    4. 生成 `railway_ticket` 记录。
    5. 触发延时消息（RabbitMQ）或定时任务，15分钟后若未支付则取消订单并归还库存。

### 4.3 常用查询（基于截图中的下拉菜单）
- **正晚点查询 `GET /api/railway/status`**：模拟返回车次正晚点状态。
- **时刻表查询 `GET /api/railway/timetable`**：按车次号查询所有经停站及时间。
- **代售点/天气**：调用第三方 API 或静态数据展示。

---

## 五、 前端实现要点 (minecraft-ui)

### 5.1 路由配置 (`src/router/index.js`)
```javascript
{
  path: '/railway',
  name: 'Railway',
  component: () => import('@/views/railway/RailwayLayout.vue'),
  children: [
    { path: '', name: 'RailwayHome', component: () => import('@/views/railway/Home.vue') },
    { path: 'list', name: 'TrainList', component: () => import('@/views/railway/TrainList.vue') },
    { path: 'order', name: 'CreateOrder', component: () => import('@/views/railway/CreateOrder.vue') },
    { path: 'my-orders', name: 'MyOrders', component: () => import('@/views/railway/MyOrders.vue') }
  ]
}
```

### 5.2 核心组件开发
1. **顶部菜单组件 `RailwayHeader.vue`**
    - 使用纯 CSS 实现截图中的下拉悬浮大菜单，配合 Vue 的 `@mouseenter` 和 `@mouseleave` 控制显示隐藏。
    - 提取主色调 `var(--el-color-primary)`，确保与现有的 Element Plus 主题融合。
2. **车票查询组件 `TicketSearchForm.vue`**
    - 包含单程、往返单选框。
    - 使用 Element Plus 的 `<el-autocomplete>` 实现出发地、到达地的拼音/汉字联想搜索（调用本地车站字典）。
    - 使用 `<el-date-picker>` 限制日期选择范围（如今天起 15 天内）。
    - 点击查询后，带着参数跳转到 `/railway/list` 页面。
3. **车次列表页 `TrainList.vue`**
    - 左侧为过滤条件（出发时间段、车次类型等）。
    - 右侧为车次列表，展示“历时”、“各席别余票（有/无/数字）”、“票价”，点击预订进入下单页。

---

## 六、 管理端接入 (minecraft-admin-main)

在管理端新增【铁路管理】模块，包含以下功能：
1. **基础数据管理**：车站字典管理、车次基础信息管理。
2. **排班与库存管理**：批量生成未来 15 天的 `railway_service` 和 `railway_segment_inventory`（提供可视化的一键生成按钮）。
3. **订单与财务**：查看所有铁路订单、处理异常订单、查看收入统计（融入现有的 ECharts 看板）。

---

## 七、 开发里程碑

| 阶段 | 任务 | 预计工时 |
|------|------|----------|
| 1 | 数据库建表、SQL脚本编写，集成至 `db_minecraft.sql` | 1 天 |
| 2 | 后端基础接口：车站查询、拼音/汉字联想接口 | 1 天 |
| 3 | 前端还原 12306 首页 UI、顶部导航栏及悬浮菜单 | 2 天 |
| 4 | 后端车次查询、余票动态计算逻辑 | 2 天 |
| 5 | 下单占座事务、延时取消、模拟支付 | 2 天 |
| 6 | 前端车次列表页、订单确认页、我的订单页 | 3 天 |
| 7 | 管理端排班库存配置与联调测试 | 2 天 |
| **总计** | | **13 天** |

## 八、 关键技术风险提示
1. **区间库存扣减**：千万不要使用总库存扣减，必须利用 `for` 循环按站序扣减 `railway_segment_inventory` 中相邻段的库存。
2. **防止一人多单**：下单前必须校验 `railway_ticket` 关联的 `railway_order` 中是否存在同一乘客、同一日期的有效订单。
3. **高并发防超卖**：在 Spring Boot 3 中，除了 MySQL 的行锁 `UPDATE ... WHERE left_seats > 0` 之外，可以在 Service 层通过 Redis 的 `RedissonClient` 对 `service_id + seat_type` 加分布式锁，提高系统的抗压能力。