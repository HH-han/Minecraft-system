# AndroidIM

Minecraft 即时通讯 Android 客户端（Kotlin + Jetpack Compose），复用 `Minecraft` 后端已有的 IM / 朋友圈 / 用户接口，接口路径与 Web 端 `minecraft-ui/src/views/ImChat` 保持一致。

## 功能

- 登录（账号密码 + 可配置服务器地址）
- 会话列表：好友与群聊合并、最后消息、时间、未读角标
- 聊天：文本 / 图片 / Unicode 表情，历史消息、时间分隔、消息气泡、图片预览
- 实时消息：OkHttp WebSocket（`/ws?token=`）+ 30s 心跳 + 断线重连
- 通讯录：星标好友、A-Z 字母索引与拖动定位、群聊列表
- 好友：添加好友、好友申请同意/拒绝、修改备注、删除好友、在线状态
- 群聊：发起群聊、群资料编辑、邀请好友入群、群成员列表
- 朋友圈：信息流分页、发布（文字/最多 9 图/位置/可见范围）、点赞、评论、删除
- 我的：资料编辑与头像上传、设置、退出登录
- 主题：跟随系统 / 浅色 / 深色 / 游戏（Minecraft 绿）
- 国际化：简体中文（默认）与 English，可在设置中切换

## 环境要求

- Android Studio Ladybug 及以上
- JDK 17 及以上（工程以 JDK 21 构建、字节码目标 17）
- Android SDK Platform 35，minSdk 26（Android 8.0）
- 后端 `Minecraft` 已启动（默认 `http://<host>:8080`）

## 配置后端地址

- 登录页可直接填写服务器地址，默认 `http://10.0.2.2:8080/api/`（Android 模拟器访问宿主机 localhost 的地址）。
- 真机调试改为局域网地址，例如 `http://192.168.1.10:8080/api/`。
- 地址会持久化到 DataStore，下次启动自动使用。
- 后端上传接口返回的 `http://localhost:8080/upload/...` 会自动改写为当前服务器域名。

## 构建

```bash
./gradlew :app:assembleDebug
```

产物：`app/build/outputs/apk/debug/app-debug.apk`

首次构建需要联网下载依赖；`local.properties` 中的 `sdk.dir` 指向本机 Android SDK。

## 目录结构

```
app/src/main/kotlin/com/minecraft/im
├── MainActivity.kt / MinecraftImApp.kt
├── core/                 # 时间格式化、拼音首字母、URL 处理、异常模型
├── data/
│   ├── local/            # DataStore：登录态、服务器地址、主题、语言、星标
│   ├── remote/
│   │   ├── ApiService.kt # Retrofit 接口（与 Web 端 API 对齐）
│   │   ├── ApiProvider.kt
│   │   ├── dto/          # 网络模型
│   │   └── ws/           # 原生 WebSocket 客户端与事件模型
│   ├── repository/       # 登录/资料、IM、朋友圈仓库
│   ├── SessionManager.kt # 登录态、主题、语言、星标中心
│   └── Mappers.kt
├── di/                   # Hilt 模块（OkHttp、Json、协程作用域）
├── domain/model/         # Contact / MessageUi / MomentUi 等领域模型
└── ui/
    ├── components/       # 头像、未读角标、顶栏、搜索框、表情数据
    ├── theme/            # 三套主题色（浅色/深色/游戏）
    ├── navigation/       # Compose Navigation 路由
    ├── login/ chat/ home/ contact/ group/ moments/ profile/ settings/
```

## 协议说明

- 发送消息走 REST（`/api/public/im/single/send`、`/group/send`），保证落库。
- 接收实时消息走 WebSocket 文本帧（`PRIVATE_MESSAGE` / `GROUP_MESSAGE` / `READ_RECEIPT` / `TYPING`）。
- 服务端统一响应 `{ code, message, data }`，`code == 200` 视为成功。
