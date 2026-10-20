# Minecraft IM Android 客户端开发文档

> **文档版本**：v1.0  
> **对应后端**：Minecraft 旅游系统（Spring Boot 3）  
> **Android 端定位**：即时通讯（IM）独立模块，可嵌入 Minecraft 旅游 App 或独立运行  
> **目标**：提供一套可落地、可扩展、与现有后端无缝对接的 Kotlin Android IM 开发规范与架构方案


## 1. 项目概述

### 1.1 背景

Minecraft 旅游系统后端已具备 WebSocket 即时通讯能力，涵盖单聊、群聊、好友管理、音视频信令等场景。当前移动端 `minecraft-App` 基于 uni-app 实现，在 IM 场景下存在长连接稳定性、消息列表性能、原生音视频集成等局限。本项目旨在以 **Kotlin + Jetpack Compose** 构建原生 Android IM 客户端，复用现有 Spring Boot 后端，为后续功能扩展（如语音消息、视频通话、消息回执、多端同步）预留架构空间。

### 1.2 技术选型总览

| 维度 | 选型 | 理由 |
|------|------|------|
| 语言 | Kotlin 2.x | 官方推荐，协程原生支持 |
| UI 框架 | Jetpack Compose + Material 3 | 声明式 UI，状态驱动，适配 IM 高频刷新场景 |
| 架构模式 | MVI + Clean Architecture | 单向数据流，状态可预测，便于测试 |
| 依赖注入 | Hilt | 官方推荐，与 Compose/ViewModel 集成良好 |
| 网络 | OkHttp + Retrofit | 成熟稳定，与后端 REST API 对接 |
| WebSocket | OkHttp WebSocket | 轻量，与后端 Spring WebSocket 兼容 |
| 序列化 | kotlinx.serialization | Kotlin 原生，协程友好，避免反射开销 |
| 本地存储 | Room | 消息本地持久化，离线可查 |
| 图片加载 | Coil Compose | 轻量，Compose 原生支持，协程驱动 |
| 分页 | Paging 3 | 消息列表、会话列表懒加载 |
| 导航 | Compose Navigation | 类型安全路由，与单 Activity 架构契合 |
| 异步 | Kotlin Coroutines + Flow | 替代 RxJava，更 Kotlin 化 |

### 1.3 与后端对接约定

后端 `Minecraft` 模块已暴露以下能力（基于文档推断）：

- **认证**：JWT Token（`Authorization: Bearer <token>`）
- **REST API**：`http://<host>:8080/api/**`
- **WebSocket 端点**：`ws://<host>:8080/ws`（需确认后端实际路径，参考 `WebSocketConfig` 配置）
- **消息协议**：后端若使用 STOMP，Android 端需引入 STOMP 客户端；若使用原生 WebSocket 文本帧，则直接 OkHttp WebSocket。**建议在开发前与后端确认实际协议**，本文档按**原生 WebSocket + JSON 文本帧**方案编写，STOMP 方案在附录中说明差异。


## 2. 项目开发规范

### 2.1 包结构规范

采用 Clean Architecture 分层，包名根为 `com.minecraft.im`：

```
com.minecraft.im
├── data/
│   ├── remote/
│   │   ├── api/              # Retrofit 接口定义
│   │   ├── ws/               # WebSocket 连接管理、消息收发
│   │   ├── dto/              # 网络请求/响应 DTO
│   │   └── interceptor/      # OkHttp 拦截器（Token、日志）
│   ├── local/
│   │   ├── db/               # Room Database、DAO
│   │   ├── entity/           # 本地实体
│   │   └── datastore/        # DataStore（用户偏好、Token）
│   └── repository/           # Repository 实现
├── domain/
│   ├── model/                # 领域模型（与 DTO 解耦）
│   ├── repository/           # Repository 接口
│   └── usecase/              # 用例（SendMessageUseCase、ObserveConversationsUseCase 等）
├── presentation/
│   ├── conversation/         # 会话列表
│   ├── chat/                 # 聊天详情
│   ├── contact/              # 好友/联系人
│   ├── profile/              # 个人资料
│   └── common/               # 公共 Compose 组件、主题、导航
├── di/                       # Hilt Module
└── core/                     # 工具类、扩展函数、常量
```

**规范要求**：
- `domain` 层不依赖 Android SDK 与 `data` 层实现细节。
- `presentation` 层仅通过 `domain` 的 UseCase 访问数据，不直接调用 Repository 或网络层。
- DTO 与领域模型分离，通过 Mapper 转换，避免后端字段变更污染 UI 层。

### 2.2 Kotlin 编码规范

依据 Kotlin 官方编码约定及 Android 社区最佳实践：

| 规则 | 说明 |
|------|------|
| 不可变性优先 | 本地变量与属性默认 `val`，集合优先 `List`/`Set` 而非 `MutableList` |
| 默认参数 | 用默认参数替代重载函数 |
| 表达式函数 | 简单函数优先表达式体 |
| Lambda 参数 | 单层 lambda 用 `it`，嵌套时显式命名 |
| 命名 | 类/接口 PascalCase；函数/变量 camelCase；常量 UPPER_SNAKE_CASE |
| 布尔命名 | 前缀 `is`/`has`/`can` |
| 函数长度 | 单函数不超过 20 行，单一职责 |
| 类长度 | 单类不超过 200 行，公开方法 ≤ 10 个 |
| 文档注释 | 长注释用 `/** */`，避免冗余 `@param`/`@return`（描述融入正文） |
| 分号 | 省略 |
| Unit 返回 | 省略 `: Unit` |

**示例**：
```kotlin
// 推荐
fun formatMessageTime(timestamp: Long, isToday: Boolean = false): String

// 避免
fun formatMessageTime(timestamp: Long): String
fun formatMessageTime(timestamp: Long, isToday: Boolean): String
```

### 2.3 Git 提交规范

采用 Conventional Commits：

```
<type>(<scope>): <subject>

feat(chat): 支持发送图片消息
fix(ws): 修复重连后消息丢失问题
refactor(data): 抽离 WebSocket 连接状态管理
docs(readme): 更新环境配置说明
```

**分支策略**：`main`（稳定）、`develop`（集成）、`feature/*`、`hotfix/*`。

### 2.4 代码审查清单

- [ ] 新增依赖是否必要？版本是否与 BOM 对齐？
- [ ] Compose 组件是否可预览（`@Preview`）？
- [ ] ViewModel 是否无 Android Context 引用（用 `AndroidViewModel` 或注入）？
- [ ] WebSocket 消息是否做了去重与顺序保证？
- [ ] 敏感信息（Token、用户 ID）是否未打印到日志？
- [ ] 新增数据库字段是否包含 Migration？


## 3. UI 设计规范

### 3.1 设计语言

采用 **Material Design 3** + 自定义旅游品牌色。Compose 的 `MaterialTheme` 支持动态取色（Android 12+）与深色模式。

**品牌色定义**（可替换为 Minecraft 旅游系统实际色值）：
```kotlin
// ui/theme/Color.kt
val MinecraftGreen = Color(0xFF4CAF50)      // 主色：自然/旅游
val MinecraftDarkGreen = Color(0xFF2E7D32)
val MinecraftAccent = Color(0xFFFF9800)     // 强调色：探索/活力
val ChatBubbleSent = Color(0xFFDCF8C6)      // 发送气泡（浅色模式）
val ChatBubbleReceived = Color(0xFFFFFFFF)  // 接收气泡
val ChatBackground = Color(0xFFF5F5F5)
```

### 3.2 核心页面 UI 规格

#### 3.2.1 会话列表页

| 元素 | 规格 |
|------|------|
| 顶部栏 | 标题“消息” + 搜索图标 + 新建会话图标 |
| 列表项 | 头像（48dp 圆角 8dp） + 名称 + 最后消息摘要 + 时间 + 未读数 Badge |
| 未读 Badge | `Badge` 组件，红底白字，最大显示“99+” |
| 空状态 | 居中插画 + “暂无会话，去发现好友吧” + 按钮 |
| 下拉刷新 | `PullToRefreshBox`（Material 3） |
| 侧滑 | 左滑删除会话（`SwipeToDismissBox`） |

#### 3.2.2 聊天详情页

| 区域 | 规格 |
|------|------|
| 顶部栏 | 返回箭头 + 对方头像/名称/在线状态 + 更多菜单（语音通话、视频通话、清空记录） |
| 消息列表 | `LazyColumn` + `reverseLayout = true`，自动滚动到底部 |
| 发送气泡 | 右对齐，`ChatBubbleSent` 背景，圆角 16dp（右下角 4dp），最大宽度 75% 屏宽 |
| 接收气泡 | 左对齐，`ChatBubbleReceived` 背景 + 浅阴影，圆角 16dp（左下角 4dp） |
| 时间戳 | 居中显示，`LabelSmall` 样式，超过 5 分钟间隔才显示 |
| 输入栏 | 底部固定：表情按钮 + 文本输入框（圆角 24dp）+ 附件按钮 + 发送按钮 |
| 附件面板 | 点击附件展开 BottomSheet：图片、拍照、位置、文件 |
| 新消息提示 | 若用户未在底部，显示“↓ 新消息”浮动按钮 |

**Compose 组件拆分建议**：
```kotlin
// 可复用的消息气泡
@Composable
fun MessageBubble(
    message: MessageUiModel,
    isMine: Boolean,
    modifier: Modifier = Modifier
)

// 消息列表
@Composable
fun MessageList(
    messages: List<MessageUiModel>,
    listState: LazyListState,
    onLoadMore: () -> Unit
)
```

#### 3.2.3 好友/联系人页

- 顶部搜索栏
- 字母索引快速滚动（`LazyColumn` + `stickyHeader`）
- 好友项右侧显示在线状态小圆点（绿=在线，灰=离线）

### 3.3 主题与深色模式

```kotlin
// ui/theme/Theme.kt
@Composable
fun MinecraftImTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MinecraftTypography,
        content = content
    )
}
```

**深色模式气泡适配**：
- 发送气泡：深色模式下使用 `Color(0xFF1B5E20)` 深绿
- 接收气泡：`Color(0xFF2C2C2C)`


## 4. 核心插件与依赖库

### 4.1 Gradle 依赖清单

```kotlin
// app/build.gradle.kts
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // 网络
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    
    // WebSocket 已包含在 OkHttp 中，无需额外依赖
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Paging
    implementation("androidx.paging:paging-compose:3.3.0")
    
    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    
    // 测试
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

### 4.2 关键库说明

| 库 | 作用 | 扩展空间 |
|------|------|----------|
| **OkHttp WebSocket** | 与后端 Spring WebSocket 建立全双工连接 | 后续可替换为 Netty 客户端以支持更复杂的心跳/重连策略 |
| **Room** | 消息本地存储，支持离线查看与分页查询 | 可扩展消息加密存储（SQLCipher） |
| **Paging 3** | 会话列表与消息列表分页加载 | 支持 RemoteMediator 实现“网络+本地”混合分页 |
| **Coil** | 头像与图片消息加载 | 可扩展自定义 ImageLoader 配置缓存策略 |


## 5. 架构设计（MVI + Clean Architecture）

### 5.1 分层职责

```
┌─────────────────────────────────────────┐
│  Presentation (Compose + ViewModel)      │
│  - UI State (单一不可变数据类)            │
│  - UI Event (用户意图)                    │
│  - UI Effect (一次性副作用：导航、Toast)   │
└─────────────────┬───────────────────────┘
                  │ UseCase
┌─────────────────▼───────────────────────┐
│  Domain                                  │
│  - Model (Message, Conversation, User)   │
│  - UseCase (SendMessage, ObserveChat...) │
│  - Repository 接口                       │
└─────────────────┬───────────────────────┘
                  │ 实现
┌─────────────────▼───────────────────────┐
│  Data                                    │
│  - Remote: Retrofit API + WebSocket 管理  │
│  - Local: Room DAO + DataStore           │
│  - Repository 实现 (单一数据源协调)        │
└─────────────────────────────────────────┘
```

### 5.2 ViewModel 状态定义示例

```kotlin
// presentation/chat/ChatContract.kt
data class ChatUiState(
    val messages: List<MessageUiModel> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.Connected,
    val error: String? = null
)

sealed interface ChatUiEvent {
    data class InputChanged(val text: String) : ChatUiEvent
    data object SendClicked : ChatUiEvent
    data class MessageLongClicked(val messageId: String) : ChatUiEvent
    data object RetryConnection : ChatUiEvent
}

sealed interface ChatUiEffect {
    data class ShowToast(val message: String) : ChatUiEffect
    data object ScrollToBottom : ChatUiEffect
    data class NavigateToImagePreview(val url: String) : ChatUiEffect
}
```

**ViewModel**：
```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private val _effect = Channel<ChatUiEffect>()
    val effect = _effect.receiveAsFlow()
    
    private val conversationId: String = checkNotNull(savedStateHandle["conversationId"])
    
    init {
        observeMessages()
        observeConnectionState()
    }
    
    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.InputChanged -> _uiState.update { it.copy(inputText = event.text) }
            is ChatUiEvent.SendClicked -> sendMessage()
            // ...
        }
    }
}
```

### 5.3 WebSocket 连接管理设计（扩展重点）

WebSocket 连接是 IM 的核心，需设计为**单例可观测**组件，便于后续扩展：

```kotlin
// data/remote/ws/ImWebSocketClient.kt
@Singleton
class ImWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    private var webSocket: WebSocket? = null
    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _incomingMessages = MutableSharedFlow<WsMessage>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages: SharedFlow<WsMessage> = _incomingMessages.asSharedFlow()
    
    fun connect(token: String) {
        val request = Request.Builder()
            .url("ws://<host>:8080/ws?token=$token")
            .build()
        webSocket = okHttpClient.newWebSocket(request, listener)
    }
    
    fun send(message: WsMessage) {
        webSocket?.send(json.encodeToString(message))
    }
    
    fun disconnect() {
        webSocket?.close(1000, "user logout")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }
    
    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.value = ConnectionState.Connected
        }
        override fun onMessage(webSocket: WebSocket, text: String) {
            val msg = json.decodeFromString<WsMessage>(text)
            _incomingMessages.tryEmit(msg)
        }
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _connectionState.value = ConnectionState.Disconnected
            // 触发重连逻辑（指数退避）
        }
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.Disconnected
        }
    }
}
```

**扩展预留**：

- **心跳机制**：在 `onOpen` 中启动协程定时发送 `Ping` 帧或 JSON 心跳消息。后端需配合响应。
- **重连策略**：`onFailure` 后启动指数退避重连（1s, 2s, 4s, 8s…），最大 30s。可通过 `ConnectivityManager` 监听网络恢复后立即重连。
- **消息去重与排序**：使用 `messageId` + `timestamp` 去重，本地 Room 按时间排序。
- **多端同步**：后端若推送“其他端已读”消息，本地更新已读状态。Android 端在发送已读回执时通知后端。

### 5.4 与后端 Spring WebSocket 的协议对接

**方案 A（推荐，若后端使用原生 WebSocket）**：

客户端发送 JSON 文本帧：
```json
{
  "type": "CHAT",
  "messageId": "uuid",
  "senderId": "user123",
  "receiverId": "user456",
  "content": "你好",
  "contentType": "TEXT",
  "timestamp": 1710000000000
}
```

服务端推送格式一致，增加 `serverTimestamp` 字段用于校准。

**方案 B（若后端使用 STOMP）**：

需引入 `com.github.NaikSoftware:StompProtocolAndroid` 或 `okhttp3` + STOMP 帧解析。Spring Boot 的 `@EnableWebSocketMessageBroker` 配置了 `/topic` 和 `/queue` 前缀。Android 端需订阅 `/user/queue/messages` 接收个人消息，发送到 `/app/chat.sendMessage`。**STOMP 方案下，重连后需重新 SUBSCRIBE**，需在连接状态变为 Connected 时触发订阅恢复。

**建议**：开发前与后端确认 `WebSocketConfig` 的实际配置。若尚未确定，优先采用**原生 WebSocket + JSON 文本帧**，协议简单、可控性强，后续如需 STOMP 可增加适配层而不改动上层业务。


## 6. 后续功能扩展预留

### 6.1 消息类型扩展

当前支持文本，预留以下类型的 `ContentType` 枚举与渲染扩展：

| 类型 | 扩展点 |
|------|--------|
| `IMAGE` | 消息体中含 `url`、`width`、`height`；UI 用 Coil `AsyncImage` 渲染 |
| `VOICE` | 含 `url`、`duration`；UI 显示波形与时长，点击播放 |
| `VIDEO` | 含 `url`、`thumbnail`、`duration`；UI 显示缩略图 + 播放按钮 |
| `LOCATION` | 含 `latitude`、`longitude`、`address`；UI 嵌入高德地图静态图或跳转地图 |
| `FILE` | 含 `url`、`fileName`、`fileSize`；UI 显示文件卡片 |

**实现方式**：在 `MessageBubble` 中根据 `contentType` 分发到不同子 Composable，新增类型只需增加一个 `when` 分支和一个子组件。

### 6.2 音视频通话

后端已有音视频信令 WebSocket 能力。Android 端可集成 **WebRTC** 或 **声网 Agora SDK**。信令通道复用现有 WebSocket，消息类型增加：
- `CALL_INVITE`：发起通话
- `CALL_ACCEPT`：接受
- `CALL_REJECT`：拒绝
- `CALL_END`：挂断
- `CALL_CANDIDATE`：ICE 候选交换

**架构预留**：`ImWebSocketClient` 的 `incomingMessages` 已被多个 UseCase 消费，通话信令处理可独立为一个 `CallSignalingUseCase`，与消息处理解耦。

### 6.3 离线推送

后端需集成 FCM/厂商推送通道。Android 端预留：
- `FirebaseMessagingService` 接收推送
- 推送 payload 仅含 `messageId` 与 `conversationId`，客户端拉取完整消息
- 避免推送携带大量内容，减少带宽与隐私风险

### 6.4 消息搜索

Room 支持 FTS（Full-Text Search）。后续可为 `messages` 表增加 FTS4 虚拟表，实现本地全文搜索。UI 层在会话列表页顶部搜索栏扩展搜索范围切换（当前会话/全部会话）。

### 6.5 多设备同步

若后端支持多端在线，Android 端需处理：
- 同一账号在手机与平板同时登录时，WebSocket 连接独立
- 已读回执：一端已读后，后端推送 `READ_RECEIPT` 消息，另一端更新未读数
- 消息撤回：`RECALL` 消息类型，更新本地消息状态为“已撤回”


## 7. 快速开始

### 7.1 环境要求

- Android Studio Ladybug 或更高版本
- JDK 17
- Android SDK API 26+（最低支持 Android 8.0）
- 后端 Minecraft 已启动，WebSocket 端点可达

### 7.2 创建项目

1. Android Studio → New Project → **Empty Activity**（Compose）
2. 包名：`com.minecraft.im`
3. Minimum SDK：API 26

### 7.3 配置后端地址

在 `local.properties` 或 `build.gradle.kts` 中定义：
```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/\"")
buildConfigField("String", "WS_URL", "\"ws://10.0.2.2:8080/ws\"")
```
`10.0.2.2` 为 Android 模拟器访问宿主机 localhost 的地址。真机调试替换为局域网 IP。

### 7.4 最小可运行链路

1. **登录**：调用 `POST /api/auth/login` 获取 JWT
2. **建立 WebSocket**：`ws://<host>:8080/ws?token=<jwt>`
3. **发送消息**：`webSocket.send(json)`，本地 Room 插入一条“发送中”消息
4. **接收消息**：`onMessage` 回调中解析 JSON，Room 插入，Flow 触发 UI 刷新

### 7.5 调试建议

- 使用 `OkHttp LoggingInterceptor` 打印 WebSocket 帧内容（`Level.BODY`）
- 后端日志确认 WebSocket 握手是否成功（HTTP 101）
- 消息顺序问题优先检查 `timestamp` 是否由服务端统一生成


## 附录 A：与 STOMP 方案的差异说明

若后端使用 Spring STOMP（`@EnableWebSocketMessageBroker`），Android 端调整点：

| 项目 | 原生 WebSocket | STOMP |
|------|---------------|-------|
| 连接 | `ws://host/ws` | `ws://host/ws` + STOMP CONNECT 帧 |
| 发送 | 直接发 JSON 文本帧 | `SEND` 帧 + `destination:/app/chat` |
| 接收 | `onMessage` 解析 | `SUBSCRIBE` 帧 + `destination:/user/queue/messages` |
| 重连后 | 重新连接即可 | 需**重新发送 SUBSCRIBE**，否则收不到消息 |
| 客户端库 | OkHttp WebSocket | StompProtocolAndroid 或自实现帧解析 |

**建议**：在 `ImWebSocketClient` 中抽象出 `MessageTransport` 接口，原生实现与 STOMP 实现可切换，业务层不感知差异。

## 附录 B：参考资源

- Kotlin 编码规范：https://kotlinlang.org/docs/coding-conventions.html
- Jetpack Compose 官方文档：https://developer.android.com/develop/ui/compose/documentation
- Coil Compose 图片加载：https://coil-kt.github.io/coil/compose/
- Spring Boot WebSocket 指南：https://websocket.org/guides/frameworks/spring-boot/

---

**文档维护**：本文档随项目迭代更新，重大架构变更需同步修订。