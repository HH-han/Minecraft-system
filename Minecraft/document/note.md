# Minecraft 旅游系统 — 旅游笔记系统设计方案

> 基于现有项目架构（Spring Boot 3 + Vue 3 + MyBatis Plus + Redis + WebSocket），在"旅游笔记"领域做增量设计，复用现有用户体系、文件上传、OSS、IM、推荐系统等能力，避免重复造轮子。

---

## 一、需求定位

旅游笔记系统是"社区交流"的垂直深化版本。与现有 `community_post`（社区帖子）的区别：

| 维度 | 社区帖子 | 旅游笔记 |
|------|----------|----------|
| 内容形态 | 短文本 + 图 | 长图文 + 行程 + 地图轨迹 + 视频 |
| 结构化程度 | 低 | 高（关联景点/酒店/美食/行程） |
| 生命周期 | 短 | 长（可沉淀为攻略） |
| 核心场景 | 讨论互动 | 记录 / 攻略 / 种草 |

**核心目标**：让用户把一次真实旅行，沉淀为"可被搜索、可被复用、可被推荐"的结构化内容资产。

---

## 二、功能模块设计

### 2.1 用户端（minecraft-ui / minecraft-App）

| 模块 | 功能点 |
|------|--------|
| 笔记创作 | 富文本/Markdown 编辑器、多图上传（OSS）、视频嵌入、封面选择、草稿自动保存 |
| 结构化绑定 | 关联景点/酒店/美食/商品（从现有库选择）、添加行程天数、地图打点 |
| 笔记浏览 | 信息流（推荐/关注/最新/同城）、瀑布流、沉浸式详情页、图文目录 |
| 互动 | 点赞、收藏、评论（二级）、@用户、转发、举报 |
| 分类体系 | 目的地（大洲/国家/城市）、主题标签（亲子/自驾/穷游/美食）、季节 |
| 我的笔记 | 草稿箱、已发布、私密笔记、笔记数据（浏览量/点赞/收藏） |
| 笔记 → 行程 | 一键"复制为我的出行计划"（对接 `travel_plan`） |
| 笔记 → 跟团 | 笔记可关联 `group_travel`，作为团队游记素材 |
| 搜索 | 关键词、目的地、标签、作者、时间范围 |

### 2.2 管理端（minecraft-admin-main）

| 模块 | 功能 |
|------|------|
| 笔记管理 | 列表、详情、审核（通过/驳回/下架）、批量操作 |
| 内容治理 | 敏感词、举报处理、违规记录 |
| 标签管理 | 标签 CRUD、标签合并、热门标签配置 |
| 专题运营 | 笔记专题（如"五一自驾"）、置顶、加精、banner 推荐 |
| 数据看板 | 发布量、互动量、热门目的地、创作者榜单 |
| 推荐干预 | 对接现有 `recommendation_*`，人工干预笔记推荐位 |

---

## 三、数据库设计

在 `db_minecraft` 中新增以下表（命名风格与现有表保持一致，SQL 放至 `Minecraft/src/main/resources/static/sql/travel_note.sql`）。

### 3.1 主表

```sql
-- 旅游笔记主表
CREATE TABLE travel_note (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL COMMENT '作者ID',
  title           VARCHAR(120) NOT NULL COMMENT '标题',
  summary         VARCHAR(300) COMMENT '摘要',
  cover_url       VARCHAR(500) COMMENT '封面图',
  content         LONGTEXT COMMENT '正文（HTML/Markdown）',
  content_type    TINYINT DEFAULT 1 COMMENT '1-HTML 2-Markdown',
  destination     VARCHAR(100) COMMENT '目的地文本',
  continent_id    BIGINT COMMENT '大洲ID',
  country_id      BIGINT COMMENT '国家ID',
  city_id         BIGINT COMMENT '城市ID',
  travel_days     INT COMMENT '旅行天数',
  travel_date     DATE COMMENT '出行日期',
  cost            DECIMAL(10,2) COMMENT '人均花费',
  status          TINYINT DEFAULT 0 COMMENT '0-草稿 1-待审 2-已发布 3-驳回 4-下架 5-私密',
  is_top          TINYINT DEFAULT 0 COMMENT '是否置顶',
  is_featured     TINYINT DEFAULT 0 COMMENT '是否加精',
  view_count      INT DEFAULT 0,
  like_count      INT DEFAULT 0,
  collect_count   INT DEFAULT 0,
  comment_count   INT DEFAULT 0,
  share_count     INT DEFAULT 0,
  reject_reason   VARCHAR(255),
  publish_time    DATETIME,
  create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted         TINYINT DEFAULT 0,
  INDEX idx_user (user_id),
  INDEX idx_city_status (city_id, status, publish_time),
  INDEX idx_publish (status, publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='旅游笔记';
```

### 3.2 附属表

```sql
-- 笔记图片/视频（有序）
CREATE TABLE travel_note_media (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id    BIGINT NOT NULL,
  media_type TINYINT COMMENT '1-图片 2-视频',
  url        VARCHAR(500) NOT NULL,
  thumb_url  VARCHAR(500),
  sort       INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_note (note_id)
);

-- 笔记标签
CREATE TABLE travel_note_tag (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(50) NOT NULL UNIQUE,
  use_count   INT DEFAULT 0,
  is_hot      TINYINT DEFAULT 0,
  status      TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE travel_note_tag_rel (
  id      BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id BIGINT NOT NULL,
  tag_id  BIGINT NOT NULL,
  UNIQUE KEY uk_note_tag (note_id, tag_id),
  INDEX idx_tag (tag_id)
);

-- 结构化关联：笔记 ↔ 景点/酒店/美食/商品
CREATE TABLE travel_note_relation (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id     BIGINT NOT NULL,
  biz_type    TINYINT NOT NULL COMMENT '1-景点 2-酒店 3-美食 4-商品 5-行程',
  biz_id      BIGINT NOT NULL,
  sort        INT DEFAULT 0,
  INDEX idx_note (note_id),
  INDEX idx_biz (biz_type, biz_id)
);

-- 笔记 ↔ 行程节点（可选，做地图轨迹）
CREATE TABLE travel_note_route (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id     BIGINT NOT NULL,
  day_index   INT COMMENT '第几天',
  poi_name    VARCHAR(120),
  longitude   DECIMAL(10,6),
  latitude    DECIMAL(10,6),
  arrive_time DATETIME,
  remark      VARCHAR(255),
  sort        INT DEFAULT 0,
  INDEX idx_note (note_id)
);

-- 互动：点赞
CREATE TABLE travel_note_like (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id    BIGINT NOT NULL,
  user_id    BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_note_user (note_id, user_id)
);

-- 互动：收藏
CREATE TABLE travel_note_collection (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id    BIGINT NOT NULL,
  user_id    BIGINT NOT NULL,
  folder_id  BIGINT COMMENT '收藏夹ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_note_user (note_id, user_id)
);

-- 互动：评论（支持二级）
CREATE TABLE travel_note_comment (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id     BIGINT NOT NULL,
  user_id     BIGINT NOT NULL,
  parent_id   BIGINT DEFAULT 0,
  reply_uid   BIGINT,
  content     VARCHAR(1000) NOT NULL,
  like_count  INT DEFAULT 0,
  status      TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_note (note_id, create_time),
  INDEX idx_parent (parent_id)
);

-- 浏览记录（去重）
CREATE TABLE travel_note_view (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id    BIGINT NOT NULL,
  user_id    BIGINT,
  ip         VARCHAR(64),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_note_user (note_id, user_id)
);

-- 举报
CREATE TABLE travel_note_report (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  note_id     BIGINT NOT NULL,
  reporter_id BIGINT NOT NULL,
  reason      VARCHAR(255),
  status      TINYINT DEFAULT 0 COMMENT '0-待处理 1-已处理 2-驳回',
  handle_by   BIGINT,
  handle_time DATETIME,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

> 说明：`travel_note` 中的 `continent_id/country_id/city_id` 直接复用现有 `continents / countries / cities` 表；`travel_note_relation` 复用 `attraction / hotel / food / product`。

---

## 四、后端设计（Minecraft）

### 4.1 分层结构

```
com.minecraft
├── controller/
│   └── TravelNoteController.java          # 用户端
│   └── admin/TravelNoteAdminController.java # 管理端
├── service/
│   ├── TravelNoteService.java
│   ├── TravelNoteInteractionService.java  # 点赞/收藏/评论
│   ├── TravelNoteSearchService.java       # 搜索（可对接 ES）
│   └── impl/...
├── mapper/
│   └── TravelNoteMapper.java (+ XML)
├── entity/
│   ├── TravelNote.java
│   ├── TravelNoteMedia.java
│   ├── TravelNoteTag.java
│   ├── TravelNoteRelation.java
│   ├── TravelNoteRoute.java
│   ├── TravelNoteComment.java
│   └── ...
├── dto/
│   ├── TravelNoteCreateDTO.java
│   ├── TravelNoteQueryDTO.java
│   └── TravelNoteAuditDTO.java
└── vo/
    ├── TravelNoteVO.java
    ├── TravelNoteDetailVO.java
    └── TravelNoteStatVO.java
```

### 4.2 核心接口（节选）

**用户端 `/api/travel-note`**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/` | 创建/保存草稿 |
| PUT | `/{id}` | 编辑 |
| DELETE | `/{id}` | 删除（逻辑删） |
| GET | `/{id}` | 详情（含互动状态） |
| GET | `/page` | 分页列表（推荐/关注/最新/同城） |
| GET | `/search` | 搜索 |
| POST | `/{id}/like` | 点赞/取消 |
| POST | `/{id}/collect` | 收藏/取消 |
| POST | `/{id}/comment` | 评论 |
| GET | `/{id}/comment/page` | 评论分页 |
| POST | `/{id}/report` | 举报 |
| GET | `/mine` | 我的笔记 |
| POST | `/{id}/copy-to-plan` | 复制为出行计划 |

**管理端 `/api/admin/travel-note`**

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/page` | 全量列表（含草稿/下架） |
| POST | `/{id}/audit` | 审核通过/驳回 |
| POST | `/{id}/top` | 置顶切换 |
| POST | `/{id}/feature` | 加精切换 |
| POST | `/{id}/offline` | 下架 |
| GET | `/stat/overview` | 数据看板 |
| GET/POST/PUT/DELETE | `/tag/**` | 标签管理 |

### 4.3 关键实现要点

**1）计数与缓存**
- 浏览量：Redis `INCR note:view:{id}`，定时任务（或每 1000 次）回写数据库，避免高频写。
- 点赞/收藏数：Redis Hash `note:stat:{id}`，DB 落库由定时任务或事件驱动。
- 详情缓存：`note:detail:{id}` 缓存 5 分钟，更新/审核后主动失效。

**2）点赞去重**
- 直接依赖 `travel_note_like` 唯一索引，Redis Set 做前置判重：`SADD note:like:{id} {userId}`。

**3）内容安全**
- 复用 `TIANAI-CAPTCHA` 用于发布风控；
- 接入敏感词过滤（Hutool `SensitiveUtil` 或 `Dfa` 算法）；
- 审核状态机：`草稿 → 待审 → 已发布/驳回`，管理端可下架。

**4）搜索**
- 初期：MySQL `LIKE` + 全文索引（`title/summary/content`）；
- 中期：对接 ElasticSearch，索引 `travel_note`，支持分词、目的地聚合、标签聚合。

**5）推荐**
- 复用现有 `recommendation_*` 表，新增 `biz_type=note`：
  - 召回：热门（`view/like/collect` 加权）、同城、关注作者、标签相似；
  - 排序：CTR/CVR 模型或规则加权；
  - 干预：管理端配置笔记置顶/加精。

**6）与现有模块联动**
- 发布笔记后：WebSocket 推送给粉丝（复用 `chat_message` 通道）；
- 笔记关联景点后：景点详情页展示"相关笔记"；
- 笔记可一键复制为 `travel_plan`（复用 `TravelPlanService`）。

---

## 五、前端设计

### 5.1 用户端（minecraft-ui）

**新增页面**
```
src/views/travel-note/
├── index.vue          # 笔记广场（信息流）
├── detail.vue         # 笔记详情
├── editor.vue         # 创作/编辑
├── mine.vue           # 我的笔记
├── search.vue         # 搜索结果
└── components/
    ├── NoteCard.vue       # 卡片
    ├── NoteWaterfall.vue  # 瀑布流
    ├── NoteComment.vue    # 评论区
    ├── NoteEditor.vue     # 编辑器（含富文本）
    └── RouteMap.vue       # 轨迹地图（复用高德）
```

**技术要点**
- 编辑器：`WangEditor` 或 `md-editor-v3`，图片走 OSS 直传；
- 瀑布流：`vue-waterfall` 或 CSS `columns`；
- 地图：复用已引入的高德 JS API 2.0；
- 3D 地球：笔记详情可在大洲/国家维度调用 `globe.gl`；
- i18n：新增 `travelNote.*` 词条。

### 5.2 管理端（minecraft-admin-main）

在 `apps/web-antdv-next/src/views/travel/` 下新增：
```
travel-note/
├── list.vue        # 笔记列表 + 审核
├── detail.vue      # 详情/审核弹窗
├── tag.vue         # 标签管理
├── topic.vue       # 专题运营
└── stat.vue        # 数据看板
```
配套 API 放 `src/api/travel-note/`，使用 `@tanstack/vue-query` 管理服务端状态。

### 5.3 移动端（minecraft-App）

新增 `pages/travel-note/`：列表、详情、发布（调用 `uni.chooseImage` + `uni.uploadFile`）、我的。

---

## 六、开发计划（建议排期）

| 阶段 | 内容 | 工期 |
|------|------|------|
| P0 | 数据库建表、实体/Mapper、基础 CRUD | 3 天 |
| P0 | 用户端发布/详情/列表 | 5 天 |
| P1 | 互动（点赞/收藏/评论/举报） | 3 天 |
| P1 | 管理端审核与列表 | 3 天 |
| P2 | 标签体系、搜索、目的地筛选 | 3 天 |
| P2 | 推荐接入、缓存优化 | 3 天 |
| P3 | 地图轨迹、复制为行程、IM 联动 | 4 天 |
| P3 | 移动端适配 | 4 天 |

---

## 七、风险与注意事项

1. **内容合规**：UGC 必须过审，敏感词 + 人工双保险；
2. **性能**：详情页图片多，必须走 OSS + CDN，列表用缩略图；
3. **计数一致性**：Redis 与 DB 最终一致，需补偿任务；
4. **与社区帖子边界**：明确"笔记=长图文攻略，帖子=短动态"，避免功能重叠；
5. **数据迁移**：如需把已有 `community_post` 升级为笔记，需按 `status/type` 做灰度映射。

---

## 八、交付物清单

- [ ] `travel_note.sql` 建表脚本
- [ ] 后端：Entity / Mapper / Service / Controller / VO / DTO
- [ ] 用户端：列表 / 详情 / 编辑 / 我的 / 搜索
- [ ] 管理端：列表 / 审核 / 标签 / 专题 / 看板
- [ ] 移动端：列表 / 详情 / 发布
- [ ] 接口文档（Knife4j 自动生成）
- [ ] 推荐规则配置（`recommendation_rule` 中新增 note 类型）

---

如需我进一步输出**具体的 Entity / Mapper XML / Controller 代码骨架**，或**前端 `editor.vue` 的完整实现**，告诉我优先哪一块，我可以直接按你项目现有代码风格生成。