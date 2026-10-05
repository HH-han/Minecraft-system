# Minecraft 旅游系统 - 系统公告模块完整开发文档

> 版本：v1.0
> 适用范围：Minecraft 旅游系统（Vue 3 + Vite + Element Plus + Spring Boot 3 + MyBatis-Plus + MySQL + Redis）
> 文档目标：提供公告模块从前端到后端、从数据库到部署的完整可落地开发方案

---

## 目录

1. [项目概述](#一项目概述)
2. [需求分析](#二需求分析)
3. [数据库设计](#三数据库设计)
4. [后端架构设计](#四后端架构设计)
5. [前端架构设计](#五前端架构设计)
6. [性能优化方案](#六性能优化方案)
7. [扩展能力设计](#七扩展能力设计)
8. [接入现有系统](#八接入现有系统)
9. [测试方案](#九测试方案)
10. [部署与运维](#十部署与运维)
11. [开发排期与交付清单](#十一开发排期与交付清单)
12. [附录](#十二附录)

---

## 一、项目概述

### 1.1 背景

Minecraft 旅游系统是一个现代化的旅游预订平台，提供酒店、景点、美食、纪念品等服务的预订功能。随着业务增长，运营方需要一个统一的公告系统，用于：

- 向用户推送系统通知、活动信息、维护通知、版本更新
- 支持多场景展示（弹窗、轮播、横幅、列表）
- 支持定向投放（全体/登录用户/新用户/指定用户）
- 支持管理员后台管理与数据统计

### 1.2 目标

| 目标 | 说明 |
|------|------|
| 功能完整 | 覆盖公告全生命周期：草稿 → 发布 → 展示 → 已读 → 过期 → 下架 |
| 高性能 | 首页公告读取 P99 < 50ms，未读数查询 P99 < 30ms |
| 高可用 | 缓存降级、DB 兜底、定时任务幂等 |
| 易扩展 | 支持 SSE 实时推送、多语言、多端适配 |
| 低侵入 | 不破坏现有系统架构，按模块独立接入 |

### 1.3 技术栈

| 层次 | 技术 |
|------|------|
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| HTTP | Axios |
| 后端框架 | Spring Boot 3.x |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7.x |
| 安全 | Spring Security + JWT |
| 定时任务 | Spring @Scheduled / XXL-Job |
| 实时推送 | SSE（Server-Sent Events） |

---

## 二、需求分析

### 2.1 角色划分

| 角色 | 权限 |
|------|------|
| 游客 | 查看公开公告 |
| 登录用户 | 查看定向公告、标记已读、查看未读数 |
| 管理员 | 公告增删改查、发布/下架、置顶、统计 |

### 2.2 功能需求

#### 用户端
- 首页轮播/弹窗/横幅展示公告
- 公告列表分页查询、分类筛选
- 公告详情查看
- 未读数红点提示
- 已读标记与本地缓存

#### 管理端
- 公告列表（含草稿、已发布、已下架）
- 新建/编辑/删除公告（富文本编辑器）
- 发布/下架/置顶操作
- 定时发布与自动过期
- 阅读统计（浏览量、已读数、阅读率）

### 2.3 非功能需求

| 指标 | 目标值 |
|------|--------|
| 首页公告接口响应 | P99 < 50ms |
| 未读数接口响应 | P99 < 30ms |
| 并发支持 | 1000 QPS |
| 公告列表加载 | 首屏 < 200ms |
| 数据一致性 | 最终一致（缓存 + DB） |
| 可用性 | 99.9% |

---

## 三、数据库设计

### 3.1 表结构总览

| 表名 | 说明 |
|------|------|
| `sys_announcement` | 公告主表 |
| `sys_announcement_read` | 已读记录表 |
| `sys_announcement_category` | 分类表（扩展） |
| `sys_announcement_target` | 定向用户表（扩展） |

### 3.2 公告主表 `sys_announcement`

```sql
CREATE TABLE `sys_announcement` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`           VARCHAR(200) NOT NULL                COMMENT '公告标题',
  `summary`         VARCHAR(500) DEFAULT NULL            COMMENT '公告摘要',
  `content`         LONGTEXT     NOT NULL                COMMENT '公告内容(富文本HTML)',
  `type`            TINYINT      NOT NULL DEFAULT 1      COMMENT '类型: 1-系统公告 2-活动通知 3-维护通知 4-版本更新',
  `level`           TINYINT      NOT NULL DEFAULT 1      COMMENT '级别: 1-普通 2-重要 3-紧急',
  `category_id`     BIGINT       DEFAULT NULL            COMMENT '分类ID',
  `cover_image`     VARCHAR(500) DEFAULT NULL            COMMENT '封面图URL',
  `display_mode`    TINYINT      NOT NULL DEFAULT 1      COMMENT '展示方式: 1-仅列表 2-弹窗 3-轮播 4-顶部横幅',
  `target_audience` TINYINT      NOT NULL DEFAULT 1      COMMENT '目标人群: 1-全体 2-登录用户 3-新用户 4-指定用户',
  `status`          TINYINT      NOT NULL DEFAULT 0      COMMENT '状态: 0-草稿 1-已发布 2-已下架',
  `is_top`          TINYINT      NOT NULL DEFAULT 0      COMMENT '是否置顶: 0-否 1-是',
  `sort_weight`     INT          NOT NULL DEFAULT 0      COMMENT '排序权重(越大越靠前)',
  `publish_time`    DATETIME     DEFAULT NULL            COMMENT '发布时间',
  `expire_time`     DATETIME     DEFAULT NULL            COMMENT '过期时间(NULL=永不过期)',
  `view_count`      BIGINT       NOT NULL DEFAULT 0      COMMENT '浏览量',
  `read_count`      BIGINT       NOT NULL DEFAULT 0      COMMENT '已读数',
  `creator_id`      BIGINT       DEFAULT NULL            COMMENT '创建人ID',
  `creator_name`    VARCHAR(64)  DEFAULT NULL            COMMENT '创建人名称',
  `deleted`         TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除: 0-正常 1-已删除',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_publish` (`status`, `publish_time`),
  KEY `idx_type_level` (`type`, `level`),
  KEY `idx_category` (`category_id`),
  KEY `idx_expire` (`expire_time`),
  KEY `idx_top_weight` (`is_top`, `sort_weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';
```

### 3.3 已读记录表 `sys_announcement_read`

```sql
CREATE TABLE `sys_announcement_read` (
  `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `announcement_id` BIGINT   NOT NULL                COMMENT '公告ID',
  `user_id`         BIGINT   NOT NULL                COMMENT '用户ID',
  `read_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  `is_popup_shown`  TINYINT  NOT NULL DEFAULT 0      COMMENT '弹窗是否已展示: 0-未展示 1-已展示',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ann_user` (`announcement_id`, `user_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_user_time` (`user_id`, `read_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告已读记录表';
```

### 3.4 分类表 `sys_announcement_category`

```sql
CREATE TABLE `sys_announcement_category` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`        VARCHAR(64) NOT NULL                COMMENT '分类名称',
  `code`        VARCHAR(64) NOT NULL                COMMENT '分类编码',
  `sort_order`  INT         NOT NULL DEFAULT 0      COMMENT '排序',
  `deleted`     TINYINT     NOT NULL DEFAULT 0      COMMENT '逻辑删除',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告分类表';
```

### 3.5 定向用户表 `sys_announcement_target`

```sql
CREATE TABLE `sys_announcement_target` (
  `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `announcement_id` BIGINT   NOT NULL                COMMENT '公告ID',
  `user_id`         BIGINT   NOT NULL                COMMENT '目标用户ID',
  `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ann_user` (`announcement_id`, `user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告定向用户表';
```

### 3.6 初始化数据

```sql
INSERT INTO `sys_announcement`
(`title`, `summary`, `content`, `type`, `level`, `display_mode`, `target_audience`,
 `status`, `is_top`, `sort_weight`, `publish_time`, `creator_name`)
VALUES
('欢迎来到 Minecraft 旅游系统', '全新上线的旅游预订平台',
 '<p>欢迎使用 Minecraft 旅游系统！在这里您可以预订酒店、景点门票、品尝美食、购买纪念品。</p>',
 1, 2, 2, 1, 1, 1, 100, NOW(), 'system'),
('五一活动开启', '全场酒店 8 折优惠',
 '<p>五一期间预订酒店享受 8 折优惠，活动时间 5.1-5.5。</p>',
 2, 1, 3, 1, 1, 0, 50, NOW(), 'system');
```

---

## 四、后端架构设计

### 4.1 分层架构

```
┌─────────────────────────────────────────────┐
│  Controller 层（参数校验、权限）              │
├─────────────────────────────────────────────┤
│  Service 层（业务逻辑、事务、缓存）           │
├─────────────────────────────────────────────┤
│  Mapper 层（MyBatis-Plus 数据访问）           │
├─────────────────────────────────────────────┤
│  Entity/DTO/VO（数据模型）                    │
├─────────────────────────────────────────────┤
│  Task 层（定时任务）                          │
├─────────────────────────────────────────────┤
│  Cache 层（Redis 缓存）                       │
├─────────────────────────────────────────────┤
│  SSE 层（实时推送）                           │
└─────────────────────────────────────────────┘
```

### 4.2 目录结构

```
com.minecraft.tourism.modules.announcement/
├── controller/
│   ├── AnnouncementController.java          # 用户端接口
│   ├── AnnouncementAdminController.java     # 管理端接口
│   └── AnnouncementSseController.java       # SSE 推送
├── service/
│   ├── AnnouncementService.java
│   ├── AnnouncementReadService.java
│   ├── AnnouncementCacheService.java        # 缓存服务
│   ├── AnnouncementSseService.java          # SSE 服务
│   └── impl/
│       ├── AnnouncementServiceImpl.java
│       ├── AnnouncementReadServiceImpl.java
│       ├── AnnouncementCacheServiceImpl.java
│       └── AnnouncementSseServiceImpl.java
├── mapper/
│   ├── AnnouncementMapper.java
│   ├── AnnouncementReadMapper.java
│   └── AnnouncementTargetMapper.java
├── entity/
│   ├── Announcement.java
│   ├── AnnouncementRead.java
│   └── AnnouncementTarget.java
├── dto/
│   ├── AnnouncementQueryDTO.java
│   ├── AnnouncementSaveDTO.java
│   └── AnnouncementVO.java
├── task/
│   └── AnnouncementScheduleTask.java
├── enums/
│   ├── AnnouncementStatusEnum.java
│   ├── AnnouncementDisplayModeEnum.java
│   └── AnnouncementTypeEnum.java
├── config/
│   └── AnnouncementCacheConfig.java
└── exception/
    └── AnnouncementException.java
```

### 4.3 核心实体

```java
@Data
@TableName("sys_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;
    private Integer type;
    private Integer level;
    private Long categoryId;
    private String coverImage;
    private Integer displayMode;
    private Integer targetAudience;
    private Integer status;
    private Integer isTop;
    private Integer sortWeight;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private Long viewCount;
    private Long readCount;
    private Long creatorId;
    private String creatorName;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 4.4 REST API 设计

#### 4.4.1 用户端接口（`/api/announcements`）

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/announcements` | 分页查询已发布公告 | 匿名 |
| GET | `/api/announcements/{id}` | 公告详情（+浏览量） | 匿名 |
| GET | `/api/announcements/active` | 首页展示公告 | 匿名 |
| GET | `/api/announcements/unread-count` | 未读数 | 登录 |
| GET | `/api/announcements/my-unread` | 我的未读列表 | 登录 |
| POST | `/api/announcements/{id}/read` | 标记已读 | 登录 |
| GET | `/api/announcements/sse` | SSE 实时推送 | 登录 |

#### 4.4.2 管理端接口（`/api/admin/announcements`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/announcements` | 分页查询（含草稿） |
| POST | `/api/admin/announcements` | 新建公告 |
| PUT | `/api/admin/announcements/{id}` | 修改公告 |
| DELETE | `/api/admin/announcements/{id}` | 逻辑删除 |
| POST | `/api/admin/announcements/{id}/publish` | 发布 |
| POST | `/api/admin/announcements/{id}/offline` | 下架 |
| POST | `/api/admin/announcements/{id}/top` | 置顶/取消 |
| GET | `/api/admin/announcements/{id}/read-stat` | 阅读统计 |

#### 4.4.3 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "欢迎来到 Minecraft 旅游系统",
        "summary": "全新上线的旅游预订平台",
        "type": 1,
        "level": 2,
        "displayMode": 2,
        "isTop": 1,
        "publishTime": "2025-01-15 10:00:00",
        "isRead": false
      }
    ],
    "total": 15,
    "current": 1,
    "size": 10
  }
}
```

### 4.5 Service 核心逻辑

```java
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final AnnouncementReadMapper readMapper;
    private final AnnouncementCacheService cacheService;
    private final AnnouncementSseService sseService;

    @Override
    public Page<AnnouncementVO> pageForUser(AnnouncementQueryDTO dto) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
            .eq(Announcement::getStatus, 1)
            .and(w -> w.isNull(Announcement::getExpireTime)
                     .or().gt(Announcement::getExpireTime, LocalDateTime.now()))
            .eq(dto.getType() != null, Announcement::getType, dto.getType())
            .orderByDesc(Announcement::getIsTop)
            .orderByDesc(Announcement::getSortWeight)
            .orderByDesc(Announcement::getPublishTime);
        Page<Announcement> page = announcementMapper.selectPage(
            new Page<>(dto.getPage(), dto.getSize()), wrapper);
        return convert(page, SecurityUtils.getCurrentUserId());
    }

    @Override
    @CacheEvict(cacheNames = "announcement:active", allEntries = true)
    public void publish(Long id) {
        Announcement ann = announcementMapper.selectById(id);
        Assert.notNull(ann, "公告不存在");
        ann.setStatus(1);
        ann.setPublishTime(LocalDateTime.now());
        announcementMapper.updateById(ann);
        // SSE 实时推送
        sseService.broadcast(convertToVO(ann, null));
    }

    @Override
    public void markRead(Long id, Long userId) {
        int inserted = readMapper.insertIgnore(id, userId);
        if (inserted > 0) {
            announcementMapper.incrReadCount(id);
            cacheService.evictUnread(userId);
        }
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return cacheService.getUnreadCount(userId, () -> readMapper.countUnreadByUser(userId));
    }
}
```

### 4.6 定时任务

```java
@Component
@RequiredArgsConstructor
public class AnnouncementScheduleTask {

    private final AnnouncementMapper announcementMapper;
    private final AnnouncementCacheService cacheService;

    /** 每分钟扫描：过期公告自动下架 */
    @Scheduled(cron = "0 * * * * ?")
    public void autoOfflineExpired() {
        int rows = announcementMapper.update(null,
            new LambdaUpdateWrapper<Announcement>()
                .set(Announcement::getStatus, 2)
                .eq(Announcement::getStatus, 1)
                .isNotNull(Announcement::getExpireTime)
                .lt(Announcement::getExpireTime, LocalDateTime.now()));
        if (rows > 0) cacheService.evictActive();
    }

    /** 每分钟扫描：到点自动发布 */
    @Scheduled(cron = "0 * * * * ?")
    public void autoPublishScheduled() {
        int rows = announcementMapper.update(null,
            new LambdaUpdateWrapper<Announcement>()
                .set(Announcement::getStatus, 1)
                .eq(Announcement::getStatus, 0)
                .isNotNull(Announcement::getPublishTime)
                .le(Announcement::getPublishTime, LocalDateTime.now()));
        if (rows > 0) cacheService.evictActive();
    }
}
```

### 4.7 安全与权限

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/announcements/active").permitAll()
            .requestMatchers("/api/announcements/**").permitAll()
            .requestMatchers("/api/admin/announcements/**").hasRole("ADMIN")
            .anyRequest().authenticated());
        return http.build();
    }
}
```

---

## 五、前端架构设计

### 5.1 目录结构

```
src/
├── api/
│   └── announcement.js                    # 公告 API
├── stores/
│   └── announcementStore.js               # Pinia 状态
├── views/
│   └── announcement/
│       ├── AnnouncementList.vue           # 公告列表
│       ├── AnnouncementDetail.vue         # 公告详情
│       └── admin/
│           ├── AnnouncementManage.vue     # 管理列表
│           └── AnnouncementEditor.vue     # 新建/编辑
├── components/
│   └── announcement/
│       ├── AnnouncementPopup.vue          # 弹窗公告
│       ├── AnnouncementCarousel.vue       # 轮播公告
│       ├── AnnouncementBanner.vue         # 顶部横幅
│       └── AnnouncementBell.vue           # 导航栏铃铛
└── utils/
    └── sse.js                             # SSE 客户端
```

### 5.2 API 封装

```javascript
// src/api/announcement.js
import request from '@/utils/request'

export const getAnnouncementList = (params) =>
  request.get('/api/announcements', { params })

export const getAnnouncementDetail = (id) =>
  request.get(`/api/announcements/${id}`)

export const getActiveAnnouncements = () =>
  request.get('/api/announcements/active')

export const getUnreadCount = () =>
  request.get('/api/announcements/unread-count')

export const markAsRead = (id) =>
  request.post(`/api/announcements/${id}/read`)

// 管理端
export const adminPageAnnouncements = (params) =>
  request.get('/api/admin/announcements', { params })

export const adminSaveAnnouncement = (data) =>
  request.post('/api/admin/announcements', data)

export const adminUpdateAnnouncement = (id, data) =>
  request.put(`/api/admin/announcements/${id}`, data)

export const adminPublishAnnouncement = (id) =>
  request.post(`/api/admin/announcements/${id}/publish`)

export const adminOfflineAnnouncement = (id) =>
  request.post(`/api/admin/announcements/${id}/offline`)

export const adminDeleteAnnouncement = (id) =>
  request.delete(`/api/admin/announcements/${id}`)
```

### 5.3 Pinia Store

```javascript
// src/stores/announcementStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getActiveAnnouncements,
  getUnreadCount,
  markAsRead
} from '@/api/announcement'

export const useAnnouncementStore = defineStore('announcement', () => {
  const activeList   = ref([])
  const unreadCount  = ref(0)
  const popupQueue   = ref([])
  const readIds      = ref(new Set(JSON.parse(localStorage.getItem('ann_read') || '[]')))

  async function fetchActive() {
    const { data } = await getActiveAnnouncements()
    activeList.value = data || []
    popupQueue.value = activeList.value
      .filter(a => a.displayMode === 2 && !readIds.value.has(a.id))
  }

  async function fetchUnread() {
    if (!localStorage.getItem('token')) return
    const { data } = await getUnreadCount()
    unreadCount.value = data || 0
  }

  async function read(id) {
    await markAsRead(id)
    readIds.value.add(id)
    localStorage.setItem('ann_read', JSON.stringify([...readIds.value]))
    if (unreadCount.value > 0) unreadCount.value--
    popupQueue.value = popupQueue.value.filter(a => a.id !== id)
  }

  function pushRealtime(ann) {
    activeList.value.unshift(ann)
    if (ann.displayMode === 2 && !readIds.value.has(ann.id)) {
      popupQueue.value.push(ann)
    }
    unreadCount.value++
  }

  return { activeList, unreadCount, popupQueue, readIds,
           fetchActive, fetchUnread, read, pushRealtime }
})
```

### 5.4 弹窗公告组件

```vue
<!-- src/components/announcement/AnnouncementPopup.vue -->
<template>
  <el-dialog
    v-model="visible"
    :title="current?.title"
    width="520px"
    class="glass-dialog"
    :close-on-click-modal="false"
    @closed="handleClose"
  >
    <div class="popup-body" v-html="current?.content" />
    <template #footer>
      <el-button @click="handleClose">我知道了</el-button>
      <el-button type="primary" @click="goDetail">查看详情</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAnnouncementStore } from '@/stores/announcementStore'

const store = useAnnouncementStore()
const router = useRouter()
const current = ref(null)
const visible = ref(false)

watch(() => store.popupQueue, (queue) => {
  if (!visible.value && queue.length) {
    current.value = queue[0]
    visible.value = true
  }
}, { immediate: true })

async function handleClose() {
  if (current.value) await store.read(current.value.id)
  visible.value = false
  current.value = null
}

function goDetail() {
  const id = current.value.id
  handleClose()
  router.push(`/announcement/${id}`)
}
</script>

<style scoped>
.glass-dialog {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
}
.popup-body { line-height: 1.8; color: #333; max-height: 50vh; overflow-y: auto; }
</style>
```

### 5.5 导航栏铃铛组件

```vue
<!-- src/components/announcement/AnnouncementBell.vue -->
<template>
  <el-badge :value="store.unreadCount" :hidden="!store.unreadCount" :max="99">
    <el-icon class="bell-icon" @click="goList"><Bell /></el-icon>
  </el-badge>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { useAnnouncementStore } from '@/stores/announcementStore'

const store = useAnnouncementStore()
const router = useRouter()

onMounted(() => store.fetchUnread())

function goList() { router.push('/announcement') }
</script>
```

### 5.6 在 App.vue 挂载

```vue
<template>
  <router-view />
  <AnnouncementPopup />
</template>

<script setup>
import { onMounted } from 'vue'
import AnnouncementPopup from '@/components/announcement/AnnouncementPopup.vue'
import { useAnnouncementStore } from '@/stores/announcementStore'
import { initAnnouncementSse } from '@/utils/sse'

const store = useAnnouncementStore()
onMounted(() => {
  store.fetchActive()
  store.fetchUnread()
  initAnnouncementSse(store)
})
</script>
```

### 5.7 路由配置

```javascript
// src/router/index.js 追加
{
  path: '/announcement',
  name: 'AnnouncementList',
  component: () => import('@/views/announcement/AnnouncementList.vue')
},
{
  path: '/announcement/:id',
  name: 'AnnouncementDetail',
  component: () => import('@/views/announcement/AnnouncementDetail.vue')
},
{
  path: '/admin/announcement',
  name: 'AdminAnnouncement',
  component: () => import('@/views/announcement/admin/AnnouncementManage.vue'),
  meta: { requiresAuth: true, role: 'admin' }
}
```

---

## 六、性能优化方案

### 6.1 缓存策略（Redis）

#### 6.1.1 缓存键设计

| 键 | 类型 | TTL | 说明 |
|----|------|-----|------|
| `announcement:active` | String(JSON) | 10min | 首页活跃公告 |
| `announcement:unread:{userId}` | String | 5min | 用户未读数 |
| `announcement:detail:{id}` | String(JSON) | 30min | 公告详情 |
| `announcement:view:{id}` | String | 1h | 浏览量缓冲 |
| `announcement:read:{id}:{userId}` | String | 24h | 已读去重 |

#### 6.1.2 缓存服务实现

```java
@Service
@RequiredArgsConstructor
public class AnnouncementCacheServiceImpl implements AnnouncementCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AnnouncementMapper announcementMapper;
    private final AnnouncementReadMapper readMapper;

    private static final String ACTIVE_KEY = "announcement:active";
    private static final String UNREAD_KEY = "announcement:unread:";
    private static final String DETAIL_KEY = "announcement:detail:";
    private static final String VIEW_KEY   = "announcement:view:";

    @Override
    public List<AnnouncementVO> getActive() {
        Object cached = redisTemplate.opsForValue().get(ACTIVE_KEY);
        if (cached != null) return (List<AnnouncementVO>) cached;
        List<AnnouncementVO> list = loadActiveFromDb();
        redisTemplate.opsForValue().set(ACTIVE_KEY, list, 10, TimeUnit.MINUTES);
        return list;
    }

    @Override
    public Long getUnreadCount(Long userId, Supplier<Long> loader) {
        String key = UNREAD_KEY + userId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) return Long.parseLong(cached.toString());
        Long count = loader.get();
        redisTemplate.opsForValue().set(key, count, 5, TimeUnit.MINUTES);
        return count;
    }

    @Override
    public void evictActive() {
        redisTemplate.delete(ACTIVE_KEY);
    }

    @Override
    public void evictUnread(Long userId) {
        redisTemplate.delete(UNREAD_KEY + userId);
    }

    /** 浏览量缓冲：每 100 次或每分钟落库一次 */
    @Override
    public void incrView(Long id) {
        String key = VIEW_KEY + id;
        Long cnt = redisTemplate.opsForValue().increment(key);
        if (cnt != null && cnt % 100 == 0) {
            announcementMapper.incrViewCount(id, 100);
            redisTemplate.delete(key);
        }
    }
}
```

#### 6.1.3 缓存降级

```java
@Override
public List<AnnouncementVO> getActive() {
    try {
        Object cached = redisTemplate.opsForValue().get(ACTIVE_KEY);
        if (cached != null) return (List<AnnouncementVO>) cached;
    } catch (Exception e) {
        log.warn("Redis 不可用，降级查 DB", e);
    }
    List<AnnouncementVO> list = loadActiveFromDb();
    try {
        redisTemplate.opsForValue().set(ACTIVE_KEY, list, 10, TimeUnit.MINUTES);
    } catch (Exception ignored) {}
    return list;
}
```

### 6.2 数据库优化

#### 6.2.1 索引优化

```sql
-- 首页活跃公告查询覆盖索引
ALTER TABLE sys_announcement
  ADD INDEX idx_active (status, is_top, sort_weight, publish_time, expire_time);

-- 未读数查询覆盖索引
ALTER TABLE sys_announcement_read
  ADD INDEX idx_unread (user_id, announcement_id);
```

#### 6.2.2 分页优化

```java
// 使用游标分页替代 offset 分页（大偏移量场景）
@Select("SELECT * FROM sys_announcement " +
        "WHERE status = 1 AND id < #{lastId} " +
        "ORDER BY id DESC LIMIT #{size}")
List<Announcement> selectByCursor(@Param("lastId") Long lastId, @Param("size") int size);
```

#### 6.2.3 慢查询监控

```yaml
# application.yml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 6.3 接口优化

#### 6.3.1 批量接口

```java
@PostMapping("/batch-read")
public Result<Void> batchRead(@RequestBody List<Long> ids) {
    Long userId = SecurityUtils.getCurrentUserId();
    readService.batchMarkRead(ids, userId);
    return Result.ok();
}
```

#### 6.3.2 接口合并

首页一次性拉取：活跃公告 + 未读数，减少请求数。

```java
@GetMapping("/home-init")
public Result<HomeInitVO> homeInit() {
    Long userId = SecurityUtils.getCurrentUserId();
    HomeInitVO vo = new HomeInitVO();
    vo.setActiveList(cacheService.getActive());
    vo.setUnreadCount(userId != null ? cacheService.getUnreadCount(userId, ...) : 0L);
    return Result.ok(vo);
}
```

### 6.4 前端优化

| 优化点 | 方案 |
|--------|------|
| 首屏加载 | 公告组件懒加载 `defineAsyncComponent` |
| 图片懒加载 | `v-lazy` 或 `loading="lazy"` |
| 列表虚拟滚动 | `el-virtual-list` 或 `vue-virtual-scroller` |
| 请求防抖 | 搜索/筛选 300ms 防抖 |
| 本地缓存 | 已读 ID 存 localStorage |
| 骨架屏 | 列表加载时展示骨架 |
| CDN | 富文本图片走 CDN |

### 6.5 SSE 实时推送

#### 6.5.1 后端 SSE 服务

```java
@Service
@RequiredArgsConstructor
public class AnnouncementSseServiceImpl implements AnnouncementSseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30min
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
        return emitter;
    }

    @Override
    public void broadcast(AnnouncementVO vo) {
        emitters.forEach((uid, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("announcement")
                    .data(vo));
            } catch (IOException e) {
                emitters.remove(uid);
            }
        });
    }
}
```

#### 6.5.2 前端 SSE 客户端

```javascript
// src/utils/sse.js
import { ElNotification } from 'element-plus'

let eventSource = null

export function initAnnouncementSse(store) {
  const token = localStorage.getItem('token')
  if (!token) return
  if (eventSource) eventSource.close()

  eventSource = new EventSource(
    `/api/announcements/sse?token=${encodeURIComponent(token)}`
  )

  eventSource.addEventListener('announcement', (e) => {
    const ann = JSON.parse(e.data)
    store.pushRealtime(ann)
    ElNotification({
      title: ann.title,
      message: ann.summary || '有新公告',
      type: 'info',
      duration: 5000
    })
  })

  eventSource.onerror = () => {
    eventSource.close()
    setTimeout(() => initAnnouncementSse(store), 10000)
  }
}

export function closeAnnouncementSse() {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}
```

### 6.6 性能指标监控

| 指标 | 采集方式 | 目标 |
|------|---------|------|
| 接口 P99 | Micrometer + Prometheus | < 50ms |
| 缓存命中率 | Redis INFO | > 95% |
| SSE 连接数 | 自定义 Gauge | < 5000 |
| 慢查询数 | MyBatis 拦截器 | 0 |

---

## 七、扩展能力设计

### 7.1 多语言支持

#### 7.1.1 表结构扩展

```sql
ALTER TABLE sys_announcement
  ADD COLUMN lang VARCHAR(16) NOT NULL DEFAULT 'zh-CN' COMMENT '语言',
  ADD INDEX idx_lang (lang, status);
```

#### 7.1.2 前端 i18n

```javascript
// src/i18n/index.js
import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'

export default createI18n({
  locale: localStorage.getItem('lang') || 'zh-CN',
  messages: { 'zh-CN': zhCN, 'en-US': enUS }
})
```

### 7.2 定向投放

```sql
-- 定向用户表已设计：sys_announcement_target
-- 查询时按 user_id 过滤
SELECT a.* FROM sys_announcement a
LEFT JOIN sys_announcement_target t ON t.announcement_id = a.id
WHERE a.status = 1
  AND (a.target_audience != 4 OR t.user_id = #{userId})
```

### 7.3 消息推送

| 渠道 | 实现 |
|------|------|
| 站内信 | 已有 SSE + 未读数 |
| 邮件 | Spring Mail + 模板引擎 |
| 短信 | 阿里云 SMS / 腾讯云 SMS |
| 微信 | 公众号模板消息 |
| App Push | 极光推送 / 个推 |

#### 7.3.1 推送抽象接口

```java
public interface AnnouncementPushChannel {
    String channelName();
    void push(Announcement announcement, List<Long> userIds);
}

@Component
public class EmailPushChannel implements AnnouncementPushChannel { ... }

@Component
public class SmsPushChannel implements AnnouncementPushChannel { ... }

@Service
@RequiredArgsConstructor
public class AnnouncementPushService {
    private final List<AnnouncementPushChannel> channels;

    public void pushAll(Announcement ann) {
        channels.forEach(c -> c.push(ann, resolveTargets(ann)));
    }
}
```

### 7.4 灰度发布

```java
// 按用户 ID 哈希灰度
public boolean isVisibleTo(Announcement ann, Long userId) {
    if (ann.getGrayPercent() == null || ann.getGrayPercent() >= 100) return true;
    if (userId == null) return false;
    return Math.abs(userId.hashCode()) % 100 < ann.getGrayPercent();
}
```

### 7.5 A/B 测试

```sql
ALTER TABLE sys_announcement
  ADD COLUMN ab_group VARCHAR(16) DEFAULT NULL COMMENT 'A/B 组',
  ADD COLUMN ab_weight INT DEFAULT 50 COMMENT '权重';
```

### 7.6 数据分析

```sql
-- 阅读率统计
SELECT
  a.id,
  a.title,
  a.view_count,
  a.read_count,
  ROUND(a.read_count / NULLIF(a.view_count, 0) * 100, 2) AS read_rate
FROM sys_announcement a
WHERE a.status = 1
ORDER BY a.publish_time DESC;
```

### 7.7 内容审核

```java
// 接入敏感词过滤
@Service
public class AnnouncementAuditService {
    private final SensitiveWordFilter filter;

    public void audit(String content) {
        if (filter.containsSensitive(content)) {
            throw new AnnouncementException("内容包含敏感词");
        }
    }
}
```

### 7.8 多租户

```sql
ALTER TABLE sys_announcement
  ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  ADD INDEX idx_tenant (tenant_id, status);
```

### 7.9 版本回溯

```sql
CREATE TABLE sys_announcement_history (
  `id`              BIGINT   NOT NULL AUTO_INCREMENT,
  `announcement_id` BIGINT   NOT NULL,
  `snapshot`        JSON     NOT NULL COMMENT '公告快照',
  `operator_id`     BIGINT   NOT NULL,
  `operator_name`   VARCHAR(64),
  `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ann` (`announcement_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告历史版本表';
```

### 7.10 国际化时区

```java
// 统一使用 UTC 存储，前端按用户时区展示
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
private LocalDateTime publishTime;
```

---

## 八、接入现有系统

### 8.1 接入点清单

| 接入点 | 说明 | 优先级 |
|--------|------|--------|
| 路由 | 添加 `/announcement`、`/announcement/:id`、`/admin/announcement` | P0 |
| 导航栏 | 插入 `<AnnouncementBell />` | P0 |
| 首页 | 插入 `<AnnouncementCarousel />` / `<AnnouncementBanner />` | P0 |
| App.vue | 全局挂载 `<AnnouncementPopup />` | P0 |
| 请求拦截 | 统一处理 401，刷新未读数 | P1 |
| 样式 | 沿用液态玻璃风格 | P1 |
| 权限 | 管理端路由守卫 | P1 |
| SSE | 登录后初始化 | P2 |

### 8.2 样式规范

```css
/* 液态玻璃风格 */
.glass-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  transition: all 0.3s ease;
}

.glass-card:hover {
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.25);
  transform: translateY(-2px);
}
```

### 8.3 响应式适配

```css
/* 桌面端 */
@media (min-width: 1200px) {
  .announcement-list { grid-template-columns: repeat(3, 1fr); }
}
/* 平板端 */
@media (min-width: 768px) and (max-width: 1199px) {
  .announcement-list { grid-template-columns: repeat(2, 1fr); }
}
/* 移动端 */
@media (max-width: 767px) {
  .announcement-list { grid-template-columns: 1fr; }
  .glass-dialog { width: 90% !important; }
}
```

---

## 九、测试方案

### 9.1 单元测试

```java
@SpringBootTest
class AnnouncementServiceTest {

    @Autowired
    private AnnouncementService service;

    @Test
    void testPublish() {
        service.publish(1L);
        Announcement ann = service.getById(1L);
        assertEquals(1, ann.getStatus());
        assertNotNull(ann.getPublishTime());
    }

    @Test
    void testMarkReadIdempotent() {
        service.markRead(1L, 100L);
        service.markRead(1L, 100L); // 重复标记
        assertEquals(1L, service.getReadCount(1L));
    }
}
```

### 9.2 集成测试

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AnnouncementApiTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void testActiveEndpoint() {
        ResponseEntity<String> resp = rest.getForEntity(
            "/api/announcements/active", String.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
```

### 9.3 前端测试

```javascript
// tests/announcementStore.spec.js
import { setActivePinia, createPinia } from 'pinia'
import { useAnnouncementStore } from '@/stores/announcementStore'

describe('AnnouncementStore', () => {
  beforeEach(() => setActivePinia(createPinia()))

  it('pushRealtime 增加未读数', () => {
    const store = useAnnouncementStore()
    store.pushRealtime({ id: 1, displayMode: 2 })
    expect(store.unreadCount).toBe(1)
  })
})
```

### 9.4 性能测试

```bash
# 使用 wrk 压测
wrk -t4 -c100 -d30s http://localhost:8080/api/announcements/active

# 使用 JMeter 压测未读数接口
jmeter -n -t unread.jmx -l result.jtl
```

### 9.5 测试用例清单

| 编号 | 场景 | 预期 |
|------|------|------|
| TC-01 | 游客访问活跃公告 | 200，返回列表 |
| TC-02 | 未登录访问未读数 | 401 |
| TC-03 | 重复标记已读 | 幂等，不重复计数 |
| TC-04 | 过期公告自动下架 | 定时任务生效 |
| TC-05 | 定时发布 | 到点自动发布 |
| TC-06 | 缓存击穿 | 降级查 DB |
| TC-07 | SSE 断线重连 | 10s 后重连 |
| TC-08 | 富文本 XSS | 后端过滤 |

---

## 十、部署与运维

### 10.1 环境要求

| 组件 | 版本 |
|------|------|
| JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 7.0+ |
| Nginx | 1.20+ |

### 10.2 配置示例

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/minecraft?useSSL=false&serverTimezone=UTC
    username: ${DB_USER}
    password: ${DB_PWD}
  redis:
    host: redis
    port: 6379
    password: ${REDIS_PWD}
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 50
        max-idle: 20
        min-idle: 5
  task:
    scheduling:
      pool:
        size: 4
```

### 10.3 Nginx 配置

```nginx
server {
    listen 80;
    server_name minecraft-tourism.com;

    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/announcements/sse {
        proxy_pass http://backend:8080;
        proxy_set_header Connection '';
        proxy_http_version 1.1;
        chunked_transfer_encoding off;
        proxy_buffering off;
        proxy_cache off;
        proxy_read_timeout 24h;
    }
}
```

### 10.4 Docker Compose

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PWD}
      MYSQL_DATABASE: minecraft
    volumes:
      - ./sql:/docker-entrypoint-initdb.d
  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PWD}
  backend:
    build: ./backend
    depends_on: [mysql, redis]
    environment:
      DB_USER: root
      DB_PWD: ${DB_PWD}
      REDIS_PWD: ${REDIS_PWD}
  frontend:
    build: ./frontend
    depends_on: [backend]
  nginx:
    image: nginx:alpine
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
    ports:
      - "80:80"
```

### 10.5 监控告警

| 监控项 | 工具 | 阈值 |
|--------|------|------|
| 接口 P99 | Prometheus + Grafana | > 100ms 告警 |
| 缓存命中率 | Redis Exporter | < 90% 告警 |
| SSE 连接数 | 自定义 Gauge | > 5000 告警 |
| 慢查询 | MySQL Slow Log | > 1s 告警 |
| 错误率 | ELK | > 1% 告警 |

### 10.6 日志规范

```java
log.info("[公告] 发布成功 id={} title={}", id, title);
log.warn("[公告] 缓存未命中 key={}", key);
log.error("[公告] SSE 推送失败 userId={}", userId, e);
```

### 10.7 数据备份

```bash
# 每日全量备份
0 2 * * * mysqldump -u root -p minecraft sys_announcement sys_announcement_read > /backup/ann_$(date +\%Y\%m\%d).sql
```

---

## 十一、开发排期与交付清单

### 11.1 开发排期（建议 5 个工作日）

| 天数 | 任务 | 交付 |
|------|------|------|
| Day 1 | 数据库建表 + 后端 Entity/Mapper | SQL 脚本、实体类 |
| Day 2 | Service + Controller + 缓存 | 后端接口 |
| Day 3 | 定时任务 + SSE + 权限 | 完整后端 |
| Day 4 | 前端 API + Store + 组件 | 前端组件 |
| Day 5 | 管理端页面 + 联调 + 测试 | 可运行版本 |

### 11.2 交付清单

- ✅ SQL 建表脚本（4 张表 + 初始化数据）
- ✅ 后端分层架构（Controller / Service / Mapper / Task / Enum / SSE）
- ✅ REST API 规范（用户端 7 个 + 管理端 8 个）
- ✅ 缓存策略（Redis 键设计 + 降级）
- ✅ 定时任务（自动发布 + 自动下架）
- ✅ SSE 实时推送（后端 + 前端）
- ✅ 前端目录结构、API 封装、Pinia Store
- ✅ 4 个 Vue 组件（弹窗 / 轮播 / 横幅 / 铃铛）
- ✅ 接入现有系统的关键改动点
- ✅ 测试方案（单元 / 集成 / 性能）
- ✅ 部署方案（Docker + Nginx + 监控）
- ✅ 扩展能力（多语言 / 定向 / 推送 / 灰度 / A/B / 多租户 / 版本回溯）

### 11.3 验收标准

| 项 | 标准 |
|----|------|
| 功能 | 所有 P0 需求实现并通过测试 |
| 性能 | 接口 P99 < 50ms，缓存命中率 > 95% |
| 兼容 | Chrome 90+ / Firefox 88+ / Safari 14+ / Edge 90+ |
| 响应式 | 桌面 / 平板 / 移动三端正常 |
| 文档 | 本文档 + API 文档（Swagger） |

---

## 十二、附录

### 12.1 枚举定义

```java
public enum AnnouncementStatusEnum {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下架");
    // ...
}

public enum AnnouncementDisplayModeEnum {
    LIST(1, "仅列表"),
    POPUP(2, "弹窗"),
    CAROUSEL(3, "轮播"),
    BANNER(4, "顶部横幅");
}

public enum AnnouncementTypeEnum {
    SYSTEM(1, "系统公告"),
    ACTIVITY(2, "活动通知"),
    MAINTENANCE(3, "维护通知"),
    VERSION(4, "版本更新");
}
```

### 12.2 Swagger 注解示例

```java
@Operation(summary = "分页查询公告")
@GetMapping
public Result<Page<AnnouncementVO>> page(
    @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
    @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
    @Parameter(description = "类型") @RequestParam(required = false) Integer type) {
    // ...
}
```

### 12.3 常见问题

| 问题 | 解决方案 |
|------|---------|
| 弹窗重复展示 | localStorage 记录已读 ID |
| SSE 断线 | 前端 10s 自动重连 |
| 缓存雪崩 | TTL 加随机抖动 |
| 缓存穿透 | 空值缓存 60s |
| 富文本 XSS | 后端 Jsoup 过滤 |
| 大偏移分页 | 游标分页 |

### 12.4 参考文档

- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis-Plus](https://baomidou.com/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Redis 官方文档](https://redis.io/docs/)

### 12.5 变更记录

| 版本 | 日期 | 变更 |
|------|------|------|
| v1.0 | 2025-01-15 | 初版发布 |

---

**文档结束**

> 本开发文档涵盖公告模块从设计到部署的完整方案，可直接用于开发落地。如在实施过程中遇到问题，请联系项目维护者。