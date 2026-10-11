# Minecraft 旅游系统 — 物流与售后系统设计方案

> 在现有商城（`orders` / `payment` / `cart` / `product`）基础上，扩展**发货 → 物流跟踪 → 签收 → 售后 → 退款**全链路能力。
>
> **本文档基于仓库当前实际代码与库表校订**（后端 `Minecraft`、用户端 `minecraft-ui`、新管理端 `minecraft-admin-main`、移动端 `minecraft-App`）。所有表名、字段、包名、目录、鉴权方式均以当前项目为准，替换此前的通用电商假设稿。

---

## 〇、当前项目基线（落地前必读）

### 0.1 现有订单/支付模型与通用电商的差异

当前 `orders` 表是**单商品订单**（一单一品类），**没有订单明细表、没有 SKU、没有收货地址字段**，物流设计必须据此调整：

| 事实 | 说明 | 对设计的影响 |
|------|------|--------------|
| `orders` 无明细子表 | 每条订单自带 `item_type` / `item_id` / `item_name` / `image` / `quantity` | 取消原稿的 `order_shipping_item`，包裹内商品直接**冗余快照**在 `order_shipping` |
| `orders` 无收货地址 | 表内没有 receiver/address 字段 | 收货信息需在**发货时录入**（存 `order_shipping`），可选新增 `user_address` |
| `orders.status` 为 varchar 存枚举码 | `0待支付 1已支付 2已发货 3已完成 4已取消 5已退款`（`OrderStatus`），已含"已发货/已完成" | 复用 `status`，物流变更时同步；新表用 `tinyint` |
| `payment` 为**模拟网关** | `PaymentServiceImpl` 80% 随机成功率，无真实支付宝/微信渠道；`payment_method` 存中文名（支付宝/微信/银联/银行卡） | 退款同样只能**模拟/人工**，写入新 `refund_order` 记录，不能调用真实渠道退款 API |
| `product` 无重量/体积 | 只有 `price`/`stock` | 按重量计费需先给 `product` 扩列，或 MVP 先用**按件计费** |
| 文件上传是**本地存储** | `UploadController` + `FileUtils` + `file_info` 表，落盘 `./uploads`，访问前缀 `http://localhost:8080/upload/`；**非 OSS** | 售后凭证/面单复用该上传服务，勿写"OSS 直传" |
| 库存字段在 `product.stock` | 下单时未做扣减 | 发货可跳过库存校验，或补充扣减逻辑 |

### 0.2 现有可复用能力

| 现有模块 | 复用方式 |
|----------|----------|
| `orders` / `OrderStatus` | 增加 `shipping_status`、`after_sale_status` 等增量字段；`status` 与物流状态同步 |
| `payment` / `PaymentStatus` | 复用支付单，退款单关联 `pay_id`；`PaymentStatus` 已有 `REFUNDING(4)`/`REFUNDED(5)` 可直接用 |
| `cart` | 无需改动 |
| `product` | 发货读取名称/图片快照；运费如需按重量需扩列 |
| `file_info` + `UploadController` | 售后凭证、物流面单文件复用 |
| `chat_message` / WebSocket | 售后协商可复用 IM；物流节点推送复用 `WebSocketHandler` / SSE |
| `announcement` | 物流停发/异常通知复用公告 |
| `recommendation_*` / `AdminAuthorizationService` | 后台鉴权直接复用 |

### 0.3 工程与代码约定（务必遵守）

- **后端包名**：`com.minecraft`。Controller **统一扁平放在 `controller/`**，管理端控制器命名 `*AdminController` 并映射 `/api/admin/**`（参考 `AnnouncementAdminController`）。
- **后端鉴权**：`SecurityConfig` 对接口 `anyRequest().permitAll()`，**管理端接口必须在方法内显式调用** `adminAuthorizationService.requireAdmin()`（`user.permissions = '0'` 为管理员），否则无权限控制。
- **定时任务**：放在 `com.minecraft.task`，用 `@Scheduled(cron = "...")`（参考 `AnnouncementScheduleTask`）。
- **枚举**：放在 `com.minecraft.enums`，风格为 `CODE(Integer code, String description)` + `getCode()`（参考 `OrderStatus` / `PaymentStatus`）。
- **ORM**：MyBatis-Plus；实体 `@TableName("xxx")` + `@TableId(type = IdType.AUTO)`；驼峰字段自动映射下划线；`createTime`/`updateTime` 用 `@TableField(fill = ...)` 交由 `MyMetaObjectHandler` 自动填充。
- **统一返回**：`ApiResponse<T>`；分页用 `PageResponse` + `PageRequest`。
- **管理员判定**：`user.permissions == '0'`。

### 0.4 各端目录基线

| 端 | 目录/规范 |
|----|-----------|
| 后端 `Minecraft` | `src/main/java/com/minecraft/{controller,service,service/impl,entity,mapper,enums,dto,task,utils}`；SQL 在 `src/main/resources/static/sql/` |
| 用户端 `minecraft-ui` | 页面 `src/views/<Module>/index.vue`；接口 `src/api/*.js` + `src/constants/api.js`；状态 `src/stores/*`（Pinia）；路由 `src/router/routes/modules/*.js` |
| 新管理端 `minecraft-admin-main/apps/web-antdv-next` | 页面 `src/views/<module>_management/*.vue`；接口 `src/api/management/{group}/*.ts`（含 barrel `index.ts`）；路由 `src/router/routes/modules/*.ts`；i18n `src/locales/langs/{zh-CN,en-US}/*.json`；**图标用 `@vben/icons` 的 lucide 组件，禁用 `i-lucide-*` 类名** |
| 移动端 `minecraft-App`（uni-app） | 页面 `pages/<module>/index.vue`；接口 `api/*.js`；状态 `stores/*`（Pinia）；`pages.json` 注册 |
| 旧管理端 `minecraft-admin` | 已废弃，本文不涉及 |

---

## 一、需求定位

商城目前只覆盖"下单 + 支付"，缺少商品从**仓库出库到用户签收**及**售后**的闭环。物流系统解决三件事：

| 目标 | 说明 |
|------|------|
| 发货管理 | 管理员对已支付订单拣货、打包、录入运单（含收货信息） |
| 物流跟踪 | 对接第三方物流（快递100 / 快递鸟 / 阿里云），拉取/接收轨迹并展示、推送 |
| 售后处理 | 用户发起退款/退货/换货，管理员审核，逆向物流，退款（**当前为模拟/人工**） |

**核心原则**：
- 一单一包裹为 MVP，结构上预留一单多包裹（按 `quantity` 拆分）；
- 物流商可插拔（策略模式 + 适配器）；
- 售后与订单解耦，独立状态机；
- 所有状态变更留痕可追溯；
- **不破坏现有表结构**，仅新增表 + `orders` 增量字段。

---

## 二、功能模块设计

### 2.1 用户端（minecraft-ui / minecraft-App）

| 模块 | 功能点 |
|------|--------|
| 我的订单 | 订单列表按物流状态筛选（待发货/已发货/运输中/已签收/售后中） |
| 物流详情 | 运单号、承运商、轨迹时间轴、预计送达、签收状态 |
| 确认收货 | 手动确认 / 超时自动确认（默认 10 天） |
| 售后申请 | 仅退款 / 退货退款 / 换货；上传凭证（复用上传服务）；填写原因 |
| 售后进度 | 审核状态、退货地址、退货物流填写、退款进度 |
| 退款到账 | 展示退款流水号与状态（当前为模拟/人工退款） |
| 物流订阅 | 关注运单，异常/节点推送（WebSocket / SSE / 站内信） |

### 2.2 管理端（minecraft-admin-main）

| 模块 | 功能 |
|------|------|
| 发货管理 | 待发货列表、录入运单、批量发货、面单文件上传/预览 |
| 物流配置 | 承运商管理（编码、名称、接口 Key、启用状态）、运费模板 |
| 物流监控 | 运单列表、轨迹查询、异常件（超时未签收、拒收、退回） |
| 售后审核 | 售后单列表、审核通过/驳回、退货地址设置、发起退款 |
| 退款管理 | 退款单列表、退款状态、模拟渠道回调处理 |
| 数据看板 | 发货时效、签收率、售后率、退款金额、承运商对比 |

---

## 三、数据库设计

新增脚本：`Minecraft/src/main/resources/static/sql/logistics.sql`（与现有 `announcement.sql` / `travel_note.sql` 同目录，命名风格一致）。
新增表沿用项目风格：`bigint` 主键自增、`snake_case` 命名、`datetime DEFAULT CURRENT_TIMESTAMP`、`utf8mb4_unicode_ci`、中文 COMMENT。

### 3.1 承运商与运费

```sql
-- 物流承运商
CREATE TABLE `logistics_carrier` (
  `id`           bigint NOT NULL AUTO_INCREMENT COMMENT '承运商ID',
  `code`         varchar(50)  NOT NULL COMMENT '承运商编码 SF/YTO/ZTO...',
  `name`         varchar(100) NOT NULL COMMENT '名称',
  `logo_url`     varchar(500) DEFAULT NULL COMMENT 'Logo',
  `api_type`     varchar(50)  DEFAULT NULL COMMENT '接口类型 kuaidi100/kuaidiniao/aliyun',
  `api_key`      varchar(255) DEFAULT NULL COMMENT '接口 Key',
  `api_secret`   varchar(255) DEFAULT NULL COMMENT '接口密钥',
  `api_customer` varchar(100) DEFAULT NULL COMMENT '接口账号/客户号',
  `phone`        varchar(50)  DEFAULT NULL COMMENT '客服电话',
  `website`      varchar(200) DEFAULT NULL COMMENT '官网',
  `sort`         int DEFAULT 0 COMMENT '排序',
  `status`       tinyint DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流承运商表';

-- 运费模板（MVP 建议先用 charge_type=1 按件；按重量需先给 product 扩列 weight/volume）
CREATE TABLE `freight_template` (
  `id`              bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `name`            varchar(100) NOT NULL COMMENT '模板名称',
  `charge_type`     tinyint DEFAULT 1 COMMENT '计费方式 1-按件 2-按重量 3-按体积',
  `first_unit`      decimal(10,2) DEFAULT NULL COMMENT '首件/首重',
  `first_price`     decimal(10,2) DEFAULT NULL COMMENT '首费',
  `extra_unit`      decimal(10,2) DEFAULT NULL COMMENT '续件/续重',
  `extra_price`     decimal(10,2) DEFAULT NULL COMMENT '续费',
  `free_regions`    varchar(500) DEFAULT NULL COMMENT '包邮地区JSON',
  `exclude_regions` varchar(500) DEFAULT NULL COMMENT '不发货地区JSON',
  `status`          tinyint DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
  `create_time`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运费模板表';
```

> 可选：按重量/体积计费需扩展商品表（当前无该字段）：
> ```sql
> ALTER TABLE `product`
>   ADD COLUMN `weight` decimal(10,3) DEFAULT NULL COMMENT '重量kg',
>   ADD COLUMN `volume` decimal(10,6) DEFAULT NULL COMMENT '体积m³';
> ```

### 3.2 发货与运单

> 因 `orders` 为单商品订单且无明细表，包裹内商品以**快照字段**冗余进 `order_shipping`（与 `orders` 自身冗余 `item_name`/`image` 的风格一致），因此**不再需要 `order_shipping_item` 表**。

```sql
-- 发货单（一个订单可拆多个包裹：按 quantity 拆分）
CREATE TABLE `order_shipping` (
  `id`               bigint NOT NULL AUTO_INCREMENT COMMENT '发货单ID',
  `shipping_no`      varchar(50)  NOT NULL COMMENT '发货单号',
  `order_id`         bigint NOT NULL COMMENT '订单ID',
  `order_no`         varchar(50)  NOT NULL COMMENT '订单号',
  `user_id`          bigint NOT NULL COMMENT '用户ID',
  -- 商品快照（来自 orders 单商品模型）
  `item_type`        varchar(20)  DEFAULT NULL COMMENT '商品类型 ticket/product/food...',
  `item_id`          bigint       DEFAULT NULL COMMENT '商品ID',
  `item_name`        varchar(100) DEFAULT NULL COMMENT '商品名称',
  `item_image`       varchar(255) DEFAULT NULL COMMENT '商品图片',
  `quantity`         int NOT NULL DEFAULT 1 COMMENT '本包裹数量',
  -- 承运与收货信息（orders 无地址字段，发货时录入）
  `carrier_id`       bigint DEFAULT NULL COMMENT '承运商ID',
  `carrier_code`     varchar(50)  DEFAULT NULL COMMENT '承运商编码',
  `tracking_no`      varchar(100) DEFAULT NULL COMMENT '运单号',
  `receiver_name`    varchar(50)  DEFAULT NULL COMMENT '收件人',
  `receiver_phone`   varchar(20)  DEFAULT NULL COMMENT '收件电话',
  `receiver_address` varchar(500) DEFAULT NULL COMMENT '收件地址',
  `package_weight`   decimal(10,3) DEFAULT NULL COMMENT '包裹重量kg',
  `freight`          decimal(10,2) DEFAULT NULL COMMENT '运费',
  `status`           tinyint NOT NULL DEFAULT 0 COMMENT '0-待发货 1-已发货 2-运输中 3-派送中 4-已签收 5-拒收 6-退回 7-异常',
  `ship_time`        datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time`     datetime DEFAULT NULL COMMENT '签收时间',
  `expect_time`      datetime DEFAULT NULL COMMENT '预计送达',
  `remark`           varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipping_no` (`shipping_no`),
  KEY `idx_order` (`order_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_tracking` (`tracking_no`),
  KEY `idx_status` (`status`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发货单表';

-- 物流轨迹（每次回传一条，按 shipping_id + track_time + context 去重）
CREATE TABLE `logistics_track` (
  `id`           bigint NOT NULL AUTO_INCREMENT COMMENT '轨迹ID',
  `shipping_id`  bigint NOT NULL COMMENT '发货单ID',
  `tracking_no`  varchar(100) NOT NULL COMMENT '运单号',
  `carrier_code` varchar(50)  DEFAULT NULL COMMENT '承运商编码',
  `track_time`   datetime NOT NULL COMMENT '轨迹时间',
  `status`       varchar(50)  DEFAULT NULL COMMENT '承运商原始状态码',
  `context`      varchar(500) DEFAULT NULL COMMENT '轨迹描述',
  `location`     varchar(200) DEFAULT NULL COMMENT '所在地',
  `longitude`    decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude`     decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `is_latest`    tinyint DEFAULT 0 COMMENT '是否最新 1-是',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_shipping` (`shipping_id`,`track_time`),
  KEY `idx_tracking` (`tracking_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流轨迹表';

-- 物流订阅（用于推送；项目已有 WebSocket/SSE，可据此触达）
CREATE TABLE `logistics_subscription` (
  `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '订阅ID',
  `shipping_id`   bigint NOT NULL COMMENT '发货单ID',
  `user_id`       bigint NOT NULL COMMENT '用户ID',
  `carrier_code`  varchar(50)  DEFAULT NULL COMMENT '承运商编码',
  `tracking_no`   varchar(100) DEFAULT NULL COMMENT '运单号',
  `subscribe_at`  datetime DEFAULT NULL COMMENT '订阅时间',
  `last_push_at`  datetime DEFAULT NULL COMMENT '最近推送时间',
  `status`        tinyint DEFAULT 1 COMMENT '状态 1-订阅中 0-已取消',
  `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipping_user` (`shipping_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流订阅表';
```

### 3.3 售后与退款

> 同理，售后商品以快照字段冗余，**不再单独建 `after_sale_item`**。

```sql
-- 售后单
CREATE TABLE `after_sale` (
  `id`              bigint NOT NULL AUTO_INCREMENT COMMENT '售后单ID',
  `after_sale_no`   varchar(50) NOT NULL COMMENT '售后单号',
  `order_id`        bigint NOT NULL COMMENT '订单ID',
  `order_no`        varchar(50) NOT NULL COMMENT '订单号',
  `user_id`         bigint NOT NULL COMMENT '用户ID',
  -- 商品快照
  `item_type`       varchar(20)  DEFAULT NULL COMMENT '商品类型',
  `item_id`         bigint       DEFAULT NULL COMMENT '商品ID',
  `item_name`       varchar(100) DEFAULT NULL COMMENT '商品名称',
  `item_image`      varchar(255) DEFAULT NULL COMMENT '商品图片',
  `quantity`        int NOT NULL DEFAULT 1 COMMENT '售后数量',
  -- 申请信息
  `type`            tinyint NOT NULL COMMENT '类型 1-仅退款 2-退货退款 3-换货',
  `reason_type`     tinyint DEFAULT NULL COMMENT '原因分类',
  `reason`          varchar(500) DEFAULT NULL COMMENT '问题描述',
  `evidence_urls`   varchar(2000) DEFAULT NULL COMMENT '凭证图片JSON(复用上传服务)',
  `refund_amount`   decimal(10,2) DEFAULT NULL COMMENT '申请退款金额',
  `actual_amount`   decimal(10,2) DEFAULT NULL COMMENT '实际退款金额',
  `status`          tinyint NOT NULL DEFAULT 0 COMMENT '0-待审核 1-审核通过 2-已驳回 3-待买家退货 4-待商家收货 5-退款中 6-已完成 7-已关闭',
  `audit_by`        bigint DEFAULT NULL COMMENT '审核人',
  `audit_time`      datetime DEFAULT NULL COMMENT '审核时间',
  `audit_remark`    varchar(500) DEFAULT NULL COMMENT '审核备注',
  `return_carrier`  varchar(50)  DEFAULT NULL COMMENT '买家退货承运商',
  `return_tracking` varchar(100) DEFAULT NULL COMMENT '买家退货单号',
  `return_time`     datetime DEFAULT NULL COMMENT '买家退货时间',
  `receive_time`    datetime DEFAULT NULL COMMENT '商家收货时间',
  `refund_time`     datetime DEFAULT NULL COMMENT '退款完成时间',
  `refund_no`       varchar(100) DEFAULT NULL COMMENT '退款流水号',
  `close_reason`    varchar(255) DEFAULT NULL COMMENT '关闭原因',
  `create_time`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_after_sale_no` (`after_sale_no`),
  KEY `idx_order` (`order_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='售后单表';

-- 售后协商记录（买卖双方留言）
CREATE TABLE `after_sale_message` (
  `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '留言ID',
  `after_sale_id` bigint NOT NULL COMMENT '售后单ID',
  `sender_type`   tinyint DEFAULT NULL COMMENT '发送方 1-用户 2-商家 3-系统',
  `sender_id`     bigint DEFAULT NULL COMMENT '发送人ID',
  `content`       varchar(1000) DEFAULT NULL COMMENT '内容',
  `evidence_urls` varchar(2000) DEFAULT NULL COMMENT '附件JSON',
  `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_after_sale` (`after_sale_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='售后协商记录表';

-- 退款单（注意：当前支付为模拟网关，refund 为模拟/人工入账，记录流水分录）
CREATE TABLE `refund_order` (
  `id`                bigint NOT NULL AUTO_INCREMENT COMMENT '退款单ID',
  `refund_no`         varchar(50) NOT NULL COMMENT '退款单号',
  `after_sale_id`     bigint DEFAULT NULL COMMENT '售后单ID',
  `order_id`          bigint NOT NULL COMMENT '订单ID',
  `order_no`          varchar(50) NOT NULL COMMENT '订单号',
  `user_id`           bigint NOT NULL COMMENT '用户ID',
  `pay_id`            bigint DEFAULT NULL COMMENT '原支付单ID(payment.id)',
  `payment_no`        varchar(50) DEFAULT NULL COMMENT '原支付流水号',
  `channel`           varchar(20) DEFAULT NULL COMMENT '渠道 支付宝/微信/银联/银行卡(取自 payment.payment_method)',
  `amount`            decimal(10,2) NOT NULL COMMENT '退款金额',
  `status`            tinyint NOT NULL DEFAULT 0 COMMENT '0-待退款 1-退款中 2-成功 3-失败',
  `channel_refund_no` varchar(100) DEFAULT NULL COMMENT '渠道退款单号',
  `fail_reason`       varchar(255) DEFAULT NULL COMMENT '失败原因',
  `refund_time`       datetime DEFAULT NULL COMMENT '退款完成时间',
  `create_time`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order` (`order_id`),
  KEY `idx_status` (`status`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款单表';
```

### 3.4 orders 表增量字段

```sql
ALTER TABLE `orders`
  ADD COLUMN `shipping_status`   tinyint DEFAULT 0 COMMENT '物流状态 0-待发货 1-部分发货 2-已发货 3-已签收' AFTER `status`,
  ADD COLUMN `after_sale_status` tinyint DEFAULT 0 COMMENT '售后状态 0-无 1-售后中 2-售后完成' AFTER `shipping_status`,
  ADD COLUMN `carrier_code`      varchar(50)  DEFAULT NULL COMMENT '承运商编码' AFTER `after_sale_status`,
  ADD COLUMN `tracking_no`       varchar(100) DEFAULT NULL COMMENT '运单号' AFTER `carrier_code`,
  ADD COLUMN `ship_time`         datetime DEFAULT NULL COMMENT '发货时间' AFTER `tracking_no`,
  ADD COLUMN `receive_time`      datetime DEFAULT NULL COMMENT '签收时间' AFTER `ship_time`,
  ADD KEY `idx_shipping_status` (`shipping_status`);
```

> 同步关系：发货/签收时除更新上表字段外，还需同步 `orders.status`（`OrderStatus.SHIPPED=2` / `COMPLETED=3`），保持一致。

### 3.5 枚举（`com.minecraft.enums`）

```java
public enum ShippingStatus {         // 对应 order_shipping.status
    PENDING(0, "待发货"), SHIPPED(1, "已发货"), IN_TRANSIT(2, "运输中"), DELIVERING(3, "派送中"),
    RECEIVED(4, "已签收"), REJECTED(5, "拒收"), RETURNED(6, "退回"), EXCEPTION(7, "异常");
    private final Integer code; private final String description;
    ShippingStatus(Integer code, String description) { this.code = code; this.description = description; }
    public Integer getCode() { return code; }
    public String getDescription() { return description; }
}

public enum AfterSaleStatus {        // 对应 after_sale.status
    PENDING(0, "待审核"), APPROVED(1, "审核通过"), REJECTED(2, "已驳回"),
    WAIT_BUYER_SEND(3, "待买家退货"), WAIT_SELLER_RECEIVE(4, "待商家收货"),
    REFUNDING(5, "退款中"), COMPLETED(6, "已完成"), CLOSED(7, "已关闭");
    private final Integer code; private final String description;
    // 构造/getter 同上
}

public enum AfterSaleType {  ONLY_REFUND(1, "仅退款"), RETURN_REFUND(2, "退货退款"), EXCHANGE(3, "换货");
    // 构造/getter 同上
}

public enum RefundStatus {  PENDING(0, "待退款"), REFUNDING(1, "退款中"), SUCCESS(2, "成功"), FAILED(3, "失败");
    // 构造/getter 同上
}
```

---

## 四、后端设计（Minecraft）

### 4.1 分层结构（贴合项目真实约定）

```
com.minecraft
├── controller/                               # 扁平目录，管理端以 *AdminController 命名
│   ├── OrderShippingController.java          # 用户端 /api/logistics
│   ├── AfterSaleController.java              # 用户端 /api/after-sale
│   ├── LogisticsAdminController.java         # 管理端 /api/admin/logistics（发货/承运商/运费/轨迹）
│   ├── AfterSaleAdminController.java         # 管理端 /api/admin/after-sale
│   └── RefundAdminController.java            # 管理端 /api/admin/refund
├── service/                                  # 接口
│   ├── OrderShippingService.java
│   ├── LogisticsTrackService.java
│   ├── LogisticsProviderService.java         # 承运商适配器调度
│   ├── AfterSaleService.java
│   ├── RefundService.java
│   └── impl/                                 # 实现（继承 ServiceImpl<Mapper, Entity>）
├── logistics/                                # 承运商适配器（策略模式）
│   ├── LogisticsProvider.java                # 接口
│   ├── Kuaidi100Provider.java
│   ├── KuaidiniaoProvider.java
│   └── AliyunLogisticsProvider.java
├── entity/         # 对应新表（@TableName + @TableId(AUTO)）
├── mapper/         # extends BaseMapper<T>
├── enums/          # ShippingStatus / AfterSaleStatus / AfterSaleType / RefundStatus
├── dto/request|response/ vo/
└── task/                                     # @Scheduled 定时任务
    ├── LogisticsSyncTask.java                # 定时拉取轨迹
    └── AutoConfirmReceiveTask.java           # 超时自动收货
```

### 4.2 承运商适配器（策略模式）

```java
public interface LogisticsProvider {
    /** 承运商编码，对应 logistics_carrier.code */
    String code();
    /** 查询实时轨迹 */
    List<LogisticsTrackDTO> queryTrack(String trackingNo, String phone);
    /** 订阅推送 */
    boolean subscribe(String trackingNo, String callbackUrl);
    /** 取消订阅 */
    boolean unsubscribe(String trackingNo);
}
```

`LogisticsProviderService` 启动时收集所有 `LogisticsProvider` Bean（`Map<String, LogisticsProvider>`），按 `logistics_carrier.api_type` 选择实现；新增承运商只需实现接口 + 插入配置数据。

### 4.3 核心接口（节选）

**用户端 `/api/logistics`**

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/logistics/order/{orderId}` | 查询订单下所有包裹 |
| GET | `/logistics/track/{trackingNo}` | 查询运单轨迹 |
| POST | `/logistics/confirm/{shippingId}` | 确认收货 |
| POST | `/logistics/subscribe/{shippingId}` | 订阅物流推送 |

**用户端 `/api/after-sale`**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/after-sale/apply` | 申请售后 |
| GET | `/after-sale/page` | 我的售后列表 |
| GET | `/after-sale/{id}` | 售后详情 |
| POST | `/after-sale/{id}/return` | 填写退货物流 |
| POST | `/after-sale/{id}/cancel` | 撤销申请 |
| POST | `/after-sale/{id}/message` | 协商留言 |

**管理端 `/api/admin/logistics`**（每个方法首行 `adminAuthorizationService.requireAdmin()`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/logistics/shipping/page` | 发货单列表 |
| POST | `/admin/logistics/shipping` | 创建发货单（录入收货信息） |
| POST | `/admin/logistics/shipping/batch` | 批量发货 |
| GET | `/admin/logistics/shipping/{id}/track` | 查询轨迹 |
| GET/POST/PUT/DELETE | `/admin/logistics/carrier/**` | 承运商管理 |
| GET/POST/PUT/DELETE | `/admin/logistics/freight/**` | 运费模板 |

**管理端 `/api/admin/after-sale` / `/api/admin/refund`**

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/after-sale/page` | 售后列表 |
| POST | `/admin/after-sale/{id}/audit` | 审核 |
| POST | `/admin/after-sale/{id}/receive` | 确认收货 |
| POST | `/admin/after-sale/{id}/refund` | 发起退款 |
| POST | `/admin/after-sale/{id}/close` | 关闭售后 |
| GET | `/admin/refund/page` | 退款单列表 |
| POST | `/admin/refund/{id}/notify` | 模拟渠道回调（标记成功/失败） |

### 4.4 状态机

**发货单状态**（`order_shipping.status`）
```
待发货(0) → 已发货(1) → 运输中(2) → 派送中(3) → 已签收(4)
                              ↘ 拒收(5) → 退回(6)
                              ↘ 异常(7)
```

**售后单状态**（`after_sale.status`）
```
待审核(0) ──审核通过──> 审核通过(1)
   │                      ├─ 仅退款 → 退款中(5) → 已完成(6)
   │                      └─ 退货退款 → 待买家退货(3) → 待商家收货(4) → 退款中(5) → 已完成(6)
   ├──驳回──> 已驳回(2)
   └──撤销──> 已关闭(7)
```

### 4.5 关键实现要点

**1）发货流程**
- 校验订单存在且 `status = OrderStatus.PAID`（"1"）且未全额退款；
- 库存视需要校验/扣减（当前下单未扣减，可跳过）；
- 生成发货单号（沿用 `yyyyMMdd + 4 位序列` 风格，参考 `OrderServiceImpl.generateOrderNo`）；写 `order_shipping`（含收货信息与商品快照）；
- 更新 `orders`：`shipping_status`、`carrier_code`、`tracking_no`、`ship_time`，并同步 `status = SHIPPED(2)`；
- 调用承运商 `subscribe`；通过 WebSocket/SSE 推送"已发货"。

**2）轨迹同步**
- `LogisticsSyncTask` 每 30 分钟拉取"未签收"运单轨迹（`@Scheduled(cron = "0 0/30 * * * ?")`）；
- 去重：`logistics_track` 按 `(shipping_id, track_time, context)` 唯一；
- 更新 `order_shipping.status` 与 `is_latest`；状态变化时推送。

**3）自动确认收货**
- `AutoConfirmReceiveTask` 每日扫描 `ship_time + N天 < now` 且未签收的运单（`@Scheduled(cron = "0 0 3 * * ?")`）；
- 置为已签收，更新 `orders.receive_time` 与 `status = COMPLETED(3)`；天数走系统参数。

**4）售后退款（当前为模拟/人工）**
- 审核通过后**不调用真实渠道**（现网 `PaymentServiceImpl` 为模拟网关）；生成 `refund_order`，将 `payment.status` 置为 `REFUNDING(4)`；
- 由 `RefundAdminController.notify`（模拟渠道回调）标记 `SUCCESS/FAILED`，成功后置 `payment.status = REFUNDED(5)`、回写 `after_sale`、更新 `orders.after_sale_status`；
- 失败重试（3 次）后转人工；`refund_no` 唯一索引防重。

**5）幂等与并发**
- 发货、审核、退款接口使用幂等键（订单号/售后单号 + 操作类型）；
- 用 Redis `SETNX`（项目已有 `RedisUtil` / `RedisConfig`）或唯一索引防重；
- 状态流转用条件更新 `UPDATE ... WHERE status = #{old}` 保证 CAS。

**6）缓存**
- 轨迹缓存 Redis `logistics:track:{trackingNo}`，TTL 10 分钟（项目已有 Redis）；
- 承运商配置缓存本地（如 Caffeine 或 Spring Cache）。

---

## 五、前端设计

### 5.1 用户端（minecraft-ui）

在 `src/views/` 下新增（沿用扁平 `<Module>/index.vue` 约定）：

```
src/views/
├── Logistics/index.vue          # 物流详情（时间轴）
└── AfterSale/
    ├── Apply.vue                # 申请售后
    ├── List.vue                 # 售后列表
    └── Detail.vue               # 售后详情（协商/退货/退款进度）
```

- 接口：新增 `src/api/logistics.js`、`src/api/afterSale.js`，并在 `src/constants/api.js` 补充端点常量（参考现有 `ORDER` 块）；
- 路由：在 `src/router/routes/modules/user.js` 增加条目（`requiresAuth: true`）；
- 状态：如需缓存列表，加 `src/stores/` 下 Pinia store；
- 上传凭证：复用现有上传接口（`src/api/upload.js`）；
- 时间轴：Element Plus `el-timeline`（用户端为 Element Plus）。

### 5.2 管理端（minecraft-admin-main / web-antdv-next）

在 `src/views/` 下新增 `logistics_management/`（沿用 `<module>_management` 命名）：

```
src/views/logistics_management/
├── shipping.vue          # 发货单列表 + 创建/批量发货（弹窗）
├── carrier.vue           # 承运商管理
├── freight.vue           # 运费模板
├── afterSale.vue         # 售后审核
├── refund.vue            # 退款管理
└── dashboard.vue         # 物流看板
```

- 接口：新增 `src/api/management/logistics/{shipping,carrier,freight,afterSale,refund}.ts` + `index.ts` barrel，并登记到 `src/api/management/index.ts`；
- 路由：新增 `src/router/routes/modules/logistics.ts`（参考 `content_management.ts`），在 root router 汇总引入；
- i18n：在 `src/locales/langs/zh-CN/logistics.json` 与 `en-US/logistics.json` 增加文案；
- **图标**：使用 `@vben/icons` 导出的 lucide Vue 组件（如 `<Plus class="size-4" />`）；缺失图标需在 `packages/@core/base/icons/src/lucide.ts` 按字母序补充 re-export，**严禁**使用 `i-lucide-*` 类名（本项目无 UnoCSS）；
- 数据请求：可用 `@tanstack/vue-query`（若该模块采用）或项目 `#/utils/request`；
- 注意：`pnpm typecheck` 存在既有基线错误，改动后对比基线数，不要求归零。

### 5.3 移动端（minecraft-App，uni-app）

```
pages/
├── order/logistics.vue        # 物流详情
└── afterSale/
    ├── index.vue              # 售后列表/入口
    ├── apply.vue              # 申请售后
    └── detail.vue             # 售后详情
```

- 接口：新增 `api/logistics.js`、`api/afterSale.js`（参考 `api/order.js` 的 `request` 封装）；
- 在 `pages.json` 注册新页面；tabbar/入口复用 `pages/order/index.vue`。

---

## 六、第三方物流对接

| 服务商 | 用途 | 说明 |
|--------|------|------|
| 快递100 | 轨迹查询 + 订阅 | 支持 2000+ 承运商，回调推送 |
| 快递鸟 | 轨迹查询 + 电子面单 | 适合需要打印面单 |
| 阿里云物流 | 轨迹 + 面单 | 生态完整 |

**回调接口**：新增 `POST /api/logistics/callback/{carrierCode}`（用户端通道，需在 `SecurityConfig` 保持放行后**自行验签**），接收轨迹推送并写入 `logistics_track`，触发 WebSocket/SSE。

**电子面单**：若需打印，调用承运商面单接口，返回 PDF/图片经现有上传服务（`UploadController` + `file_info`）保存，管理端预览打印。

> 说明：支付侧目前无真实渠道，退款以模拟/人工方式落地；物流侧的第三方对接是**唯一真实外部依赖**，建议先用快递100。

---

## 七、开发计划（建议排期）

| 阶段 | 内容 | 工期 |
|------|------|------|
| P0 | 建表（`logistics.sql`）+ 实体/Mapper/枚举/承运商配置 | 2 天 |
| P0 | 管理端发货（录入收货信息/批量/运单） | 4 天 |
| P0 | 用户端物流详情 + 确认收货 | 3 天 |
| P1 | 轨迹对接（快递100）+ 定时同步 + WebSocket 推送 | 4 天 |
| P1 | 售后申请/审核/退货/退款（模拟渠道） | 5 天 |
| P2 | 运费模板、自动确认收货、异常件监控 | 3 天 |
| P2 | 数据看板、对账 | 2 天 |
| P3 | 移动端适配 | 3 天 |

---

## 八、风险与注意事项

1. **承运商 API 限流**：轨迹查询按单计费，需缓存 + 批量 + 降级（失败展示最后缓存）；
2. **退款幂等**：回调可能重复，必须用 `refund_no` 唯一索引防重；当前无真实渠道，退款为模拟/人工；
3. **无收货地址字段**：`orders` 不含地址，发货必须录入收件信息；建议后续补 `user_address` 表做地址簿；
4. **单商品订单**：一单一品类，暂无明细/SKU；多品类需求需先改造订单模型，本设计已预留快照字段；
5. **超时未发货**：配置超时提醒（管理端待办 + 用户端催发货）；
6. **售后时效**：配置可申请售后时间窗口（如签收后 7 天）；
7. **逆向物流**：买家退货单号需与正向运单区分，避免混淆；
8. **冗余一致性**：`orders.shipping_status` / `after_sale_status` 为冗余字段，发货/签收/售后时须同步，建议封装统一方法；
9. **合规**：面单含用户隐私，打印与存储需脱敏（手机号中间四位）；
10. **鉴权**：管理端接口必须显式 `requireAdmin()`（`SecurityConfig` 全放行，不调用即无保护）。

---

## 九、交付物清单

- [ ] `logistics.sql` 建表脚本 + `orders` 增量字段（可选 `product` 重量/体积）
- [ ] 后端：Entity / Mapper / Service / Controller / VO / DTO / 适配器 / 枚举
- [ ] 定时任务：轨迹同步（`task/LogisticsSyncTask`）、自动收货（`task/AutoConfirmReceiveTask`）
- [ ] 用户端：物流详情 / 确认收货 / 售后申请 / 售后列表 / 售后详情
- [ ] 管理端：发货管理 / 承运商 / 运费模板 / 售后审核 / 退款管理 / 看板
- [ ] 移动端：物流详情 / 售后
- [ ] 第三方对接：快递100（或快递鸟）配置与回调
- [ ] 接口文档（Knife4j/OpenAPI 自动生成，`@Tag` + `@Operation`）

---

如需继续输出**具体的 Entity / Mapper / Service 代码骨架**、**售后状态机的完整 Java 实现（含幂等与并发控制）**，或**快递100 适配器的完整代码**，告知优先项即可按现有代码风格生成。
