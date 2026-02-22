# Minecraft 旅游系统

> 一站式智慧旅游服务平台，为用户提供景点推荐、酒店预订、票务预订、美食推荐、特产商城、出行计划、社区交流等全方位旅游服务。

## 项目简介

Minecraft 旅游系统是一个基于 Spring Boot 3 + Vue 3 构建的综合性旅游服务平台，采用前后端分离架构，集成多种现代化技术栈，为用户提供从出行规划到旅游体验的完整服务链路。

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.0.2 | 基础框架 |
| Spring Security | - | 安全框架 |
| MyBatis Plus | 3.5.3.2 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | - | 缓存/会话存储 |
| Elasticsearch | - | 搜索引擎 |
| WebSocket | - | 实时通信 |
| JWT | 0.12.5 | 身份认证 |
| Aliyun OSS | 3.15.1 | 对象存储 |
| SpringDoc | 2.0.2 | API 文档 |

### 前端技术

#### 用户端 (minecraft-ui)
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.25 | 前端框架 |
| Vite | 7.3.1 | 构建工具 |
| Element Plus | 2.13.2 | UI 组件库 |
| Vue Router | 4.6.4 | 路由管理 |
| Pinia | 3.0.4 | 状态管理 |
| Axios | 1.13.5 | HTTP 客户端 |

#### 管理端 (minecraft-admin)
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.25 | 前端框架 |
| Vite | 8.0.0-beta.13 | 构建工具 |
| Element Plus | 2.13.2 | UI 组件库 |
| ECharts | 6.0.0 | 数据可视化 |
| Vue Router | 4.6.4 | 路由管理 |
| Pinia | 3.0.4 | 状态管理 |

## 核心功能

### 用户端功能

| 模块 | 功能 |
|------|------|
| 用户认证 | 注册、登录（账号/手机/二维码）、退出、个人信息管理 |
| 景点推荐 | 景点列表、景点详情、按城市/季节筛选、评分点赞评论 |
| 酒店预订 | 酒店列表、酒店详情、按星级筛选、预订下单 |
| 票务预订 | 火车票/机票/出租车票查询预订 |
| 美食推荐 | 美食列表、美食详情、按菜系筛选、评分点赞评论 |
| 特产商城 | 商品浏览、购物车、订单管理、支付 |
| 出行计划 | 创建/编辑/删除出行计划、规划管理 |
| 社区交流 | 发布帖子、浏览社区、点赞收藏评论 |
| 好友互动 | 添加好友、好友列表、在线聊天 |
| 跟团游 | 跟团游列表、发起/加入团队、团队管理 |
| 新闻资讯 | 新闻浏览、分类查看、分享评论 |

### 管理端功能

| 模块 | 功能 |
|------|------|
| 用户管理 | 用户列表、用户状态管理、权限控制 |
| 内容管理 | 景点/酒店/美食/商品/新闻的增删改查 |
| 订单管理 | 订单查看、状态更新、退款处理 |
| 轮播管理 | 轮播图配置、展示设置 |
| 数据统计 | 用户增长、订单统计、访问量分析 |
| 系统设置 | 基础配置、参数设置 |

## 数据库设计

### 核心表结构

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| attraction | 景点表 |
| hotel | 酒店表 |
| food | 美食表 |
| product | 特产/商品表 |
| ticket | 票务表 |
| orders | 订单表 |
| payment | 支付表 |
| cart | 购物车表 |
| comment | 评论表 |
| like_record | 点赞记录表 |
| collection | 收藏表 |
| community_post | 社区帖子表 |
| chat_message | 聊天消息表 |
| friend | 好友表 |
| travel_plan | 出行计划表 |
| group_travel | 跟团游表 |
| news | 新闻资讯表 |
| file_info | 文件信息表 |

## 项目结构

```
Minecraft-system/
├── Minecraft/                    # 后端项目
│   ├── src/main/java/com/minecraft/
│   │   ├── controller/           # 控制器层 (27个控制器)
│   │   ├── service/              # 服务层
│   │   ├── mapper/               # 数据访问层
│   │   ├── entity/               # 实体类 (38个实体)
│   │   ├── dto/                  # 数据传输对象
│   │   ├── vo/                   # 视图对象
│   │   ├── config/               # 配置类
│   │   ├── interceptor/          # 拦截器
│   │   ├── aspect/               # 切面
│   │   ├── utils/                # 工具类
│   │   ├── enums/                # 枚举类
│   │   ├── exception/            # 异常处理
│   │   └── MinecraftApplication.java
│   ├── src/main/resources/
│   │   ├── mapper/               # MyBatis映射文件
│   │   ├── application.yml       # 配置文件
│   │   └── sql/                  # SQL脚本
│   └── pom.xml
├── minecraft-ui/                 # 用户端前端
│   ├── src/
│   │   ├── views/                # 页面组件
│   │   ├── components/           # 公共组件
│   │   ├── router/               # 路由配置
│   │   ├── store/                # Pinia状态管理
│   │   └── api/                  # API接口
│   └── package.json
├── minecraft-admin/              # 管理端前端
│   ├── src/
│   │   ├── views/                # 页面组件
│   │   ├── components/           # 公共组件
│   │   ├── router/               # 路由配置
│   │   └── api/                  # API接口
│   └── package.json
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 数据库初始化

```bash
# 进入SQL目录
cd Minecraft/src/main/resources/sql

# 执行建表脚本
mysql -u root -p < db_minecraft.sql
```

### 后端启动

```bash
# 进入后端项目
cd Minecraft

# 修改配置文件
# 编辑 src/main/resources/application.yml，配置数据库和Redis连接信息

# 启动项目
mvn spring-boot:run

# 或使用IDE运行 com.minecraft.MinecraftApplication
```

后端服务启动后访问：`http://localhost:8080`

API文档访问：`http://localhost:8080/doc.html`

### 用户端启动

```bash
# 进入用户端项目
cd minecraft-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

用户端启动后访问：`http://localhost:5173`

### 管理端启动

```bash
# 进入管理端项目
cd minecraft-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

管理端启动后访问：`http://localhost:5174`

## 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_minecraft
    username: root
    password: your_password
```

### Redis配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:
```

### JWT配置

```yaml
jwt:
  secret: your-secret-key
  expiration: 604800000  # 7天
```

### 文件上传配置

```yaml
file:
  upload-path: ./uploads
  allowed-types: jpg,jpeg,png,gif,bmp
  max-size: 104857600  # 100MB
```

## 接口文档

项目集成了 Knife4j（基于 Swagger），启动后端服务后访问：

- API文档：`http://localhost:8080/doc.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 安全机制

- **JWT认证**：Token-based身份认证
- **Spring Security**：权限控制与安全防护
- **密码加密**：BCrypt加密存储
- **请求拦截**：Token拦截器验证
- **XSS防护**：输入输出过滤

## 部署说明

### 后端打包

```bash
cd Minecraft
mvn clean package
java -jar target/Minecraft-0.0.1-SNAPSHOT.jar
```

### 前端打包

```bash
# 用户端
cd minecraft-ui
npm run build

# 管理端
cd minecraft-admin
npm run build
```

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 用户端
    location / {
        root /path/to/minecraft-ui/dist;
        try_files $uri $uri/ /index.html;
    }

    # 管理端
    location /admin {
        root /path/to/minecraft-admin/dist;
        try_files $uri $uri/ /admin/index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助完善项目。

## 许可证

本项目采用 MIT 许可证。

## 联系方式

- 项目地址：https://github.com/HH-han/Minecraft-system
- 问题反馈：请提交 Issue

---

**Minecraft 旅游系统 - 让旅行更简单、更美好**
