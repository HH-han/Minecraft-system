## 旅游网站群组功能设计方案

### 一、需求概述

**核心功能模块：**

| 模块 | 说明 |
|------|------|
| 群组创建与管理 | 用户可创建旅行群组，设置名称、封面、目的地、出行时间、简介、人数上限、是否公开 |
| 群组成员管理 | 邀请/申请加入、审批、角色权限（创建者/管理员/普通成员）、移除成员、退出群组 |
| 群组动态 | 成员可发布图文动态（类似朋友圈），其他成员可点赞、评论 |
| 行程计划 | 群组内共同编辑每日行程（时间、地点、活动、交通方式），支持版本记录 |
| 群组相册 | 成员上传照片，按时间/地点归类，支持批量上传、下载、删除 |
| 费用AA | 记录群组公共支出，自动计算人均分摊，支持账单添加、删除、结算标记 |
| 消息通知 | 入群申请、@提及、动态回复、行程变更等实时通知 |
| 群组设置 | 群组信息修改、转让群主、解散群组、成员禁言等管理功能 |

---

### 二、技术栈

| 层级 | 技术选型 |
|------|----------|
| 后端框架 | Spring Boot 3.x |
| 安全框架 | Spring Security + JWT |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.0 + Redis（缓存/会话） |
| 消息队列 | RabbitMQ / Kafka（异步通知） |
| 文件存储 | 阿里云OSS / 腾讯云COS / MinIO |
| 前端框架 | Vue 3 + TypeScript |
| 状态管理 | Pinia |
| UI组件库 | Element Plus / Ant Design Vue |
| 构建工具 | Vite |

---

### 三、数据库设计（核心表结构）

```sql
-- 群组主表
CREATE TABLE `travel_group` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '群组名称',
  `cover_url` varchar(500) COMMENT '封面图片',
  `destination` varchar(200) COMMENT '目的地',
  `start_date` date COMMENT '出发日期',
  `end_date` date COMMENT '结束日期',
  `description` text COMMENT '群组简介',
  `max_members` int DEFAULT 20 COMMENT '人数上限',
  `is_public` tinyint(1) DEFAULT 0 COMMENT '是否公开',
  `status` tinyint DEFAULT 1 COMMENT '1-进行中 2-已结束 3-已解散',
  `created_by` bigint COMMENT '创建者ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0
);

-- 群组成员表
CREATE TABLE `group_member` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role` tinyint DEFAULT 2 COMMENT '1-群主 2-管理员 3-普通成员',
  `status` tinyint DEFAULT 1 COMMENT '1-正常 2-禁言 3-已退出',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `exit_time` datetime,
  `nickname` varchar(50) COMMENT '群内昵称',
  UNIQUE KEY `uk_group_user` (`group_id`, `user_id`)
);

-- 群组动态表
CREATE TABLE `group_post` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text,
  `images` json COMMENT '图片URL数组',
  `location` varchar(200) COMMENT '位置信息',
  `like_count` int DEFAULT 0,
  `comment_count` int DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0
);

-- 动态评论表
CREATE TABLE `group_comment` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT 0 COMMENT '回复的评论ID，0表示一级评论',
  `reply_to_user_id` bigint COMMENT '@回复的用户ID',
  `content` varchar(500) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0
);

-- 行程计划表
CREATE TABLE `group_trip_plan` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `day_index` int NOT NULL COMMENT '第几天',
  `date` date COMMENT '具体日期',
  `time_slot` varchar(50) COMMENT '时间段：上午/下午/晚上',
  `activity` varchar(200) NOT NULL COMMENT '活动名称',
  `location` varchar(200) COMMENT '地点',
  `transport` varchar(50) COMMENT '交通方式',
  `notes` text COMMENT '备注',
  `created_by` bigint,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- AA账单表
CREATE TABLE `group_bill` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `payer_id` bigint NOT NULL COMMENT '付款人',
  `amount` decimal(10,2) NOT NULL,
  `category` varchar(50) COMMENT '分类：餐饮/住宿/交通/门票/其他',
  `description` varchar(200),
  `bill_date` date,
  `is_settled` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
);

-- 账单分摊明细表
CREATE TABLE `group_bill_split` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `bill_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `share_amount` decimal(10,2) NOT NULL,
  `is_paid` tinyint(1) DEFAULT 0 COMMENT '是否已还'
);

-- 邀请/申请记录表
CREATE TABLE `group_invite` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `inviter_id` bigint COMMENT '邀请人',
  `invitee_id` bigint NOT NULL COMMENT '被邀请人/申请人',
  `type` tinyint DEFAULT 1 COMMENT '1-邀请 2-申请加入',
  `status` tinyint DEFAULT 0 COMMENT '0-待处理 1-同意 2-拒绝',
  `expire_at` datetime,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `handled_at` datetime
);

-- 消息通知表
CREATE TABLE `group_notification` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `receiver_id` bigint NOT NULL,
  `group_id` bigint,
  `type` varchar(30) COMMENT 'JOIN_APPLY/NEW_POST/COMMENT/MENTION/BILL_UPDATE',
  `title` varchar(100),
  `content` varchar(500),
  `target_id` bigint COMMENT '关联业务ID',
  `is_read` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
);

-- 群组相册表
CREATE TABLE `group_album` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `thumbnail_url` varchar(500),
  `description` varchar(200),
  `location` varchar(200),
  `taken_at` datetime COMMENT '拍摄时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0
);
```

---

### 四、后端接口设计（RESTful API）

#### 4.1 群组管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/group/create` | 创建群组 |
| PUT | `/api/group/{groupId}` | 更新群组信息 |
| DELETE | `/api/group/{groupId}` | 解散群组 |
| GET | `/api/group/{groupId}` | 获取群组详情 |
| GET | `/api/group/list` | 获取我的群组列表（分页） |
| GET | `/api/group/search` | 搜索公开群组 |
| POST | `/api/group/{groupId}/transfer` | 转让群主 |

#### 4.2 成员管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/group/{groupId}/member/invite` | 邀请成员（可批量） |
| POST | `/api/group/{groupId}/member/apply` | 申请加入群组 |
| PUT | `/api/group/{groupId}/member/{userId}/approve` | 审批入群申请 |
| DELETE | `/api/group/{groupId}/member/{userId}` | 移除成员 |
| POST | `/api/group/{groupId}/member/exit` | 退出群组 |
| PUT | `/api/group/{groupId}/member/{userId}/role` | 修改成员角色 |
| GET | `/api/group/{groupId}/member/list` | 获取成员列表 |
| GET | `/api/group/{groupId}/member/{userId}` | 获取成员信息 |

#### 4.3 群组动态接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/group/{groupId}/post` | 发布动态（含图片上传） |
| DELETE | `/api/group/post/{postId}` | 删除动态 |
| GET | `/api/group/{groupId}/post/list` | 获取动态列表（分页，含点赞状态） |
| POST | `/api/group/post/{postId}/like` | 点赞/取消点赞 |
| POST | `/api/group/post/{postId}/comment` | 发表评论 |
| DELETE | `/api/group/comment/{commentId}` | 删除评论 |
| GET | `/api/group/post/{postId}/comment/list` | 获取评论列表 |

#### 4.4 行程计划接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/group/{groupId}/trip` | 添加行程项 |
| PUT | `/api/group/trip/{tripId}` | 更新行程项 |
| DELETE | `/api/group/trip/{tripId}` | 删除行程项 |
| GET | `/api/group/{groupId}/trip/list` | 获取完整行程（按天分组） |
| GET | `/api/group/{groupId}/trip/export` | 导出行程为PDF/图片 |

#### 4.5 AA账单接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/group/{groupId}/bill` | 添加账单 |
| PUT | `/api/group/bill/{billId}` | 修改账单 |
| DELETE | `/api/group/bill/{billId}` | 删除账单 |
| GET | `/api/group/{groupId}/bill/list` | 获取账单列表 |
| GET | `/api/group/{groupId}/bill/settlement` | 获取结算汇总（每人应付款） |
| PUT | `/api/group/bill/{billId}/settle` | 标记账单已结清 |

#### 4.6 相册接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/group/{groupId}/album/upload` | 批量上传照片 |
| DELETE | `/api/group/album/{photoId}` | 删除照片 |
| GET | `/api/group/{groupId}/album/list` | 获取相册列表（分页） |

#### 4.7 通知接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/notification/list` | 获取通知列表 |
| PUT | `/api/notification/{id}/read` | 标记已读 |
| PUT | `/api/notification/read-all` | 全部已读 |
| GET | `/api/notification/unread-count` | 获取未读数量 |

---

### 五、后端核心代码示例

#### 5.1 实体类（使用Lombok）

```java
@Data
@TableName("travel_group")
public class TravelGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String coverUrl;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Integer maxMembers;
    private Boolean isPublic;
    private Integer status;
    private Long createdBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
```

#### 5.2 群组创建Service

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final TravelGroupMapper groupMapper;
    private final GroupMemberMapper memberMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public Long createGroup(GroupCreateDTO dto, Long userId) {
        // 1. 创建群组
        TravelGroup group = new TravelGroup();
        BeanUtils.copyProperties(dto, group);
        group.setCreatedBy(userId);
        group.setStatus(1);
        groupMapper.insert(group);

        // 2. 将创建者设为群主
        GroupMember member = new GroupMember();
        member.setGroupId(group.getId());
        member.setUserId(userId);
        member.setRole(1); // 群主
        member.setStatus(1);
        memberMapper.insert(member);

        // 3. 缓存群组基本信息
        redisTemplate.opsForValue().set(
            "group:detail:" + group.getId(),
            group,
            3600, TimeUnit.SECONDS
        );

        return group.getId();
    }
}
```

#### 5.3 统一响应封装

```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

#### 5.4 Controller示例

```java
@RestController
@RequestMapping("/api/group")
@Tag(name = "群组管理")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/create")
    @Operation(summary = "创建群组")
    public Result<Long> createGroup(@RequestBody @Valid GroupCreateDTO dto,
                                    @AuthenticationPrincipal UserPrincipal user) {
        Long groupId = groupService.createGroup(dto, user.getId());
        return Result.success(groupId);
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "获取群组详情")
    public Result<TravelGroupVO> getGroupDetail(@PathVariable Long groupId,
                                                 @AuthenticationPrincipal UserPrincipal user) {
        TravelGroupVO vo = groupService.getGroupDetail(groupId, user.getId());
        return Result.success(vo);
    }
}
```

---

### 六、前端架构设计（Vue 3）

#### 6.1 项目目录结构

```
src/
├── api/                    # API接口
│   ├── group.ts
│   ├── member.ts
│   ├── post.ts
│   ├── trip.ts
│   └── bill.ts
├── assets/                 # 静态资源
├── components/             # 公共组件
│   ├── GroupCard.vue       # 群组卡片
│   ├── PostItem.vue        # 动态条目
│   ├── CommentList.vue     # 评论列表
│   ├── BillItem.vue        # 账单条目
│   └── ImageUploader.vue   # 图片上传
├── composables/            # 组合式函数
│   ├── useGroup.ts
│   ├── useNotification.ts
│   └── useWebSocket.ts
├── layout/                 # 布局组件
├── router/                 # 路由配置
├── stores/                 # Pinia状态管理
│   ├── group.ts
│   ├── user.ts
│   └── notification.ts
├── types/                  # TypeScript类型定义
├── utils/                  # 工具函数
│   ├── request.ts          # Axios封装
│   └── date.ts
└── views/                  # 页面视图
    ├── GroupList.vue       # 群组列表
    ├── GroupDetail.vue     # 群组详情（Tab切换）
    │   ├── GroupInfo.vue
    │   ├── GroupMembers.vue
    │   ├── GroupPosts.vue
    │   ├── GroupTrip.vue
    │   ├── GroupAlbum.vue
    │   └── GroupBill.vue
    └── CreateGroup.vue     # 创建群组
```

#### 6.2 Pinia Store示例（群组）

```typescript
// stores/group.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getGroupList, getGroupDetail, createGroup } from '@/api/group'

export const useGroupStore = defineStore('group', () => {
  const currentGroup = ref<Group | null>(null)
  const groupList = ref<Group[]>([])
  const loading = ref(false)

  const fetchGroupList = async (params?: ListParams) => {
    loading.value = true
    try {
      const res = await getGroupList(params)
      groupList.value = res.data
    } finally {
      loading.value = false
    }
  }

  const fetchGroupDetail = async (groupId: number) => {
    const res = await getGroupDetail(groupId)
    currentGroup.value = res.data
    return res.data
  }

  const createNewGroup = async (data: CreateGroupData) => {
    const res = await createGroup(data)
    await fetchGroupList()
    return res.data
  }

  return {
    currentGroup,
    groupList,
    loading,
    fetchGroupList,
    fetchGroupDetail,
    createNewGroup
  }
})
```

#### 6.3 Axios封装（请求拦截）

```typescript
// utils/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    } else if (code === 401) {
      // 跳转登录
      useUserStore().logout()
      window.location.href = '/login'
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

#### 6.4 群组详情页（核心视图）

```vue
<!-- views/GroupDetail.vue -->
<template>
  <div class="group-detail">
    <!-- 群组头部信息 -->
    <div class="group-header">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-avatar :size="120" :src="group?.coverUrl" />
        </el-col>
        <el-col :span="18">
          <h2>{{ group?.name }}</h2>
          <p>目的地：{{ group?.destination }}</p>
          <p>时间：{{ group?.startDate }} ~ {{ group?.endDate }}</p>
          <p>成员：{{ memberCount }}/{{ group?.maxMembers }}</p>
          <el-button-group>
            <el-button type="primary" @click="inviteMember">邀请成员</el-button>
            <el-button @click="exitGroup" v-if="!isOwner">退出群组</el-button>
            <el-button type="danger" @click="dissolveGroup" v-if="isOwner">解散群组</el-button>
          </el-button-group>
        </el-col>
      </el-row>
    </div>

    <!-- Tab切换 -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="动态" name="posts">
        <GroupPosts :groupId="groupId" />
      </el-tab-pane>
      <el-tab-pane label="行程" name="trip">
        <GroupTrip :groupId="groupId" />
      </el-tab-pane>
      <el-tab-pane label="相册" name="album">
        <GroupAlbum :groupId="groupId" />
      </el-tab-pane>
      <el-tab-pane label="账单" name="bill">
        <GroupBill :groupId="groupId" />
      </el-tab-pane>
      <el-tab-pane label="成员" name="members">
        <GroupMembers :groupId="groupId" />
      </el-tab-pane>
      <el-tab-pane label="设置" name="settings" v-if="isOwner || isAdmin">
        <GroupSettings :groupId="groupId" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useGroupStore } from '@/stores/group'
import GroupPosts from './components/GroupPosts.vue'
import GroupTrip from './components/GroupTrip.vue'
import GroupAlbum from './components/GroupAlbum.vue'
import GroupBill from './components/GroupBill.vue'
import GroupMembers from './components/GroupMembers.vue'
import GroupSettings from './components/GroupSettings.vue'

const route = useRoute()
const groupStore = useGroupStore()
const groupId = Number(route.params.id)
const activeTab = ref('posts')

const group = computed(() => groupStore.currentGroup)
const isOwner = computed(() => group.value?.role === 1)
const isAdmin = computed(() => group.value?.role === 2)
const memberCount = ref(0)

onMounted(async () => {
  await groupStore.fetchGroupDetail(groupId)
})
</script>
```

---

### 七、实时通知方案（WebSocket）

#### 7.1 后端WebSocket配置

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
```

#### 7.2 通知推送Service

```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendGroupNotification(Long userId, NotificationDTO dto) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notification",
            dto
        );
    }

    public void sendGroupBroadcast(Long groupId, String message) {
        messagingTemplate.convertAndSend(
            "/topic/group/" + groupId,
            message
        );
    }
}
```

#### 7.3 前端WebSocket连接

```typescript
// composables/useWebSocket.ts
import { ref, onMounted, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useNotificationStore } from '@/stores/notification'

export function useWebSocket() {
  const client = ref<Client | null>(null)
  const notificationStore = useNotificationStore()

  const connect = (userId: number) => {
    const stompClient = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      onConnect: () => {
        // 订阅个人通知
        stompClient.subscribe(`/user/${userId}/queue/notification`, (msg) => {
          const data = JSON.parse(msg.body)
          notificationStore.addNotification(data)
        })
      }
    })
    stompClient.activate()
    client.value = stompClient
  }

  const disconnect = () => {
    client.value?.deactivate()
  }

  return { connect, disconnect }
}
```

---

### 八、部署与性能优化建议

| 优化点 | 建议方案 |
|--------|----------|
| **数据库** | 对 `group_id`、`user_id` 建立索引；使用读写分离 |
| **缓存** | Redis缓存群组基本信息、成员列表（5分钟过期） |
| **图片处理** | 使用OSS的图片处理服务（缩略图、水印、格式转换） |
| **分页查询** | 动态列表使用游标分页（`lastId`方式），避免深分页 |
| **消息队列** | 通知、日志等异步操作使用RabbitMQ解耦 |
| **CDN加速** | 静态资源（图片、CSS、JS）使用CDN加速 |
| **前端优化** | 路由懒加载、图片懒加载、虚拟滚动（长列表） |
| **安全性** | 接口限流（Guava RateLimiter）、防XSS、SQL注入防护 |

---

### 九、扩展功能建议

1. **AI行程推荐**：接入大模型API，根据目的地和天数自动生成行程建议
2. **位置共享**：群组成员可实时共享位置（集成高德/百度地图）
3. **投票功能**：群组内发起投票（如：去哪家餐厅）
4. **旅行日记**：自动将动态按时间线生成旅行回忆录
5. **小程序/H5适配**：可额外开发微信小程序端
基于您提出的四个扩展功能，结合现有旅游群组基础，以下是完整的补充设计与合并方案。

---

## 扩展功能详细设计方案

### 一、AI行程推荐

#### 1.1 设计思路

AI行程推荐的核心是“人机协作”——AI作为智能助手生成草案，用户在此基础上调整优化。关键在于利用大模型的文本生成能力，结合实时数据（天气、景点开放状态）确保方案可行性，避免“幻觉”问题。

#### 1.2 技术实现

**大模型选型与集成：**

选择GPT-4/DeepSeek等主流模型，采用结构化提示词设计。参考PARTS框架：

```
P (Persona)：你是一位拥有10年经验的资深旅行规划师
A (Act)：请为{{destination}}生成{{days}}天的详细行程
R (Rules)：按天输出，包含上午/下午/晚上时段、景点名称、交通方式、餐饮推荐
T (Tone)：专业、实用、考虑旅行者体力
S (Scope)：聚焦于{{destination}}，避开已关闭景点
```

**工具链集成（关键）：**

单靠大模型生成的行程存在信息不实时的问题。为实现可靠推荐，需集成三类工具：
1. **信息检索**：通过搜索API获取景点实时开放状态、门票价格
2. **地图服务**：计算景点间交通时长，验证行程时间可行性
3. **天气数据**：获取目的地天气预报，提供出行建议

**处理流程：**

```
用户输入(目的地+天数) → 大模型生成初始行程 → 
工具调用(验证景点/计算交通/查询天气) → 
冲突检测与修正 → 返回结构化行程JSON
```

#### 1.3 数据库扩展

```sql
-- AI行程推荐记录表
CREATE TABLE `ai_trip_recommendation` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint COMMENT '关联群组',
  `user_id` bigint NOT NULL,
  `destination` varchar(200) NOT NULL,
  `days` int NOT NULL,
  `preferences` json COMMENT '偏好：美食/文化/自然/购物等',
  `recommendation_json` json NOT NULL COMMENT '完整行程方案',
  `status` tinyint DEFAULT 1 COMMENT '1-草稿 2-已发布 3-已采用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
);
```

#### 1.4 关键风险与应对

根据实际项目经验，AI行程规划主要有三个风险点：

| 风险 | 应对方案 |
|------|----------|
| AI生成不存在的景点 | 禁止模型直接命名，所有推荐经地图API验证 |
| 时间冲突（如闭馆日） | 加入验证层，检查景点开放时间与实际日期 |
| 多个AI节点叠加误差 | 减少模型调用节点，能用规则算法处理的不用AI |


### 二、位置共享

#### 2.1 设计思路

群组成员在出行期间实时共享位置，便于集合、调整行程。采用“按需开启+隐私保护”模式：成员自主决定是否开启位置共享，可随时关闭。

#### 2.2 技术实现

**地图服务选型：** 推荐高德地图（国内覆盖好、文档完善），需自行申请API Key并配置。

**核心能力：**

- **实时位置上传**：前端定时（如10秒）获取GPS坐标，通过WebSocket推送到群组
- **多人位置展示**：群组地图上同时显示多个成员位置，可切换查看
- **位置历史轨迹**：可选记录成员移动轨迹（需用户授权）

**数据结构：**

```sql
-- 成员位置实时表（Redis存储，TTL 30秒）
-- 结构：group_id:member_id:location → {"lat": 39.9, "lng": 116.4, "updated_at": 1234567890}

-- 位置历史表（MySQL）
CREATE TABLE `group_location_history` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `latitude` decimal(10,7) NOT NULL,
  `longitude` decimal(10,7) NOT NULL,
  `accuracy` int COMMENT '精度(米)',
  `recorded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_group_user_time (`group_id`, `user_id`, `recorded_at`)
);
```

**隐私控制：**

成员可设置位置共享状态：`开启`/`对管理员可见`/`关闭`。退出群组后自动清除位置数据。

> **注意**：集成高德地图需合规，在隐私协议中声明位置权限用途，获取用户授权。


### 三、投票功能

#### 3.1 设计思路

群组内对旅行事项（去哪家餐厅、住哪个酒店、是否增加某景点）发起投票，成员参与决策，提升群组互动。

#### 3.2 功能规格

| 项目 | 说明 |
|------|------|
| 投票类型 | 单选 / 多选 / 评分制 |
| 投票选项 | 支持文本+图片 |
| 截止时间 | 可设截止日期，自动结束 |
| 结果可见性 | 公开可见（投票后实时显示） |
| 匿名投票 | 支持匿名模式 |

#### 3.3 数据结构

```sql
CREATE TABLE `group_poll` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `creator_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text,
  `type` tinyint DEFAULT 1 COMMENT '1-单选 2-多选 3-评分',
  `options` json NOT NULL COMMENT '[{"id":1,"text":"选项A","image":"url"}]',
  `end_time` datetime,
  `is_anonymous` tinyint(1) DEFAULT 0,
  `status` tinyint DEFAULT 1 COMMENT '1-进行中 2-已结束',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `group_poll_vote` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `poll_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `option_id` int NOT NULL,       -- 选项ID
  `score` int COMMENT '评分制时使用',
  `voted_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_poll_user (poll_id, user_id)
);
```


### 四、旅行日记

#### 4.1 设计思路

将群组内的动态、照片、行程按时间线自动编排，生成一份“旅行回忆录”。该功能是在现有群组动态基础上做的智能聚合。

#### 4.2 自动生成逻辑

**数据来源：**
- 群组动态（文字+图片）
- 群组相册照片（按拍摄时间/上传时间）
- 群组行程计划（每日安排）
- 位置签到记录

**编排规则：**
1. 按日期分组，每日为一个章节
2. 当日有行程计划 → 作为章节标题（“Day 1：抵达丽江”）
3. 当日有动态发布 → 按时间线插入动态内容
4. 当日有照片上传 → 以图集/轮播形式展示
5. 无内容但有行程 → 仅显示行程摘要

#### 4.3 数据结构

```sql
CREATE TABLE `group_travel_diary` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `title` varchar(200) COMMENT '自动生成：{destination}之旅',
  `cover_url` varchar(500) COMMENT '封面图（取首日首张照片）',
  `content_json` json NOT NULL COMMENT '完整的日记内容',
  `status` tinyint DEFAULT 1 COMMENT '1-草稿 2-已发布 3-已导出',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 内容JSON结构示例
{
  "days": [
    {
      "date": "2026-07-01",
      "title": "Day 1：抵达丽江",
      "trip_items": ["上午：抵达机场", "下午：束河古镇"],
      "posts": [
        {"user": "张三", "content": "丽江古城太美了！", "images": ["url1", "url2"], "time": "18:30"}
      ],
      "photos": ["url3", "url4", "url5"]
    }
  ],
  "statistics": {"total_days": 5, "total_posts": 12, "total_photos": 45}
}
```

#### 4.4 导出功能

支持导出为：
- **PDF**：适合打印保存
- **HTML/网页**：可分享链接
- **长图**：适合社交分享


## 完整设计方案总览

将四个扩展功能与原群组设计合并，形成完整的系统功能架构：

| 层级 | 模块 | 核心功能 |
|------|------|----------|
| **用户层** | 群组管理 | 创建/解散/设置/转让 |
| | 成员管理 | 邀请/申请/审批/角色 |
| | 权限控制 | 群主/管理员/普通成员 |
| **交互层** | 群组动态 | 发布/点赞/评论 |
| | 投票功能 | 发起/参与/结果 |
| | 位置共享 | 实时位置/历史轨迹 |
| **规划层** | 行程计划 | 编辑/查看/导出 |
| | AI行程推荐 | 智能生成/验证/采用 |
| **存储层** | 群组相册 | 上传/归类/下载 |
| | 旅行日记 | 自动生成/导出 |
| **财务层** | AA账单 | 记录/分摊/结算 |
| **通知层** | 消息通知 | 实时推送/已读管理 |
| **技术层** | WebSocket | 实时通信 |
| | Redis | 缓存/位置数据 |
| | 第三方服务 | 地图API/大模型API/OSS |


## 新增接口清单

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| AI推荐 | POST | `/api/ai/trip/generate` | 生成AI行程方案 |
| AI推荐 | POST | `/api/ai/trip/adopt/{id}` | 采用方案到群组行程 |
| AI推荐 | GET | `/api/ai/trip/history` | 获取历史推荐记录 |
| 位置共享 | POST | `/api/location/update` | 更新当前位置 |
| 位置共享 | GET | `/api/location/group/{groupId}` | 获取群组成员的实时位置 |
| 位置共享 | GET | `/api/location/history/{groupId}/{userId}` | 获取成员位置历史 |
| 投票 | POST | `/api/poll/create` | 发起投票 |
| 投票 | POST | `/api/poll/{pollId}/vote` | 投票 |
| 投票 | GET | `/api/poll/{pollId}/result` | 获取投票结果 |
| 投票 | PUT | `/api/poll/{pollId}/close` | 结束投票 |
| 旅行日记 | GET | `/api/diary/{groupId}/generate` | 生成旅行日记 |
| 旅行日记 | GET | `/api/diary/{groupId}` | 获取日记 |
| 旅行日记 | POST | `/api/diary/{diaryId}/export` | 导出日记 |
