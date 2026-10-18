# Minecraft 旅游系统支付模块开发文档

**文档版本**：v1.0  
**编写日期**：2026-10-18  
**适用项目**：Minecraft 旅游系统（Spring Boot 3 + Vue 3 + uni-app）  
**目标读者**：后端开发、前端开发、测试、运维


## 1. 背景与问题定义

### 1.1 现状问题

当前 Minecraft 旅游系统涉及支付的业务场景分散且实现方式不统一：

| 业务模块 | 当前支付实现 | 问题 |
|---------|-------------|------|
| 特产商城 | 独立对接（或模拟） | 与订单系统耦合，无统一流水 |
| 酒店预订 | 独立逻辑 | 退款无标准流程 |
| 票务预订 | 独立逻辑 | 回调处理分散，状态不一致 |
| 积分商城兑换 | 仅积分抵扣 | 混合支付场景缺失 |
| 跟团游账单分摊 | 线下/手动 | 无系统化支付链路 |

**核心痛点**：
- 各业务模块各自处理支付逻辑，代码重复，状态流转不一致
- 无统一支付流水表，对账困难，资金流向不可追溯
- 缺少退款、部分退款、混合支付（积分+现金）能力
- 开发环境与生产环境切换缺乏规范，上线接入真实支付风险高
- 新增支付渠道（如云闪付）需要改动多个业务模块

### 1.2 建设目标

引入**聚合支付网关**作为系统中唯一的支付出口，实现：

1. **统一入口**：所有业务模块通过同一套 API 发起支付，不感知底层渠道差异
2. **统一流水**：每笔支付尝试生成独立支付流水号，与业务订单解耦
3. **模拟/真实可切换**：开发与测试使用模拟支付，上线时通过配置切换至真实渠道
4. **可扩展**：新增支付渠道（微信、支付宝、云闪付、数字人民币）只需实现适配器，业务代码不动
5. **完整闭环**：支付 → 回调 → 查单 → 退款 → 对账


## 2. 总体架构设计

### 2.1 分层架构

```
┌─────────────────────────────────────────────────────────────┐
│  业务层（Minecraft 后端各业务模块）                            │
│  商城 / 酒店 / 票务 / 积分商城 / 跟团游账单                    │
│  调用方只需：createPayment(orderId, amount, channelCode)       │
└─────────────────────┬───────────────────────────────────────┘
                      │ 统一支付 API（内部 HTTP / Feign）
┌─────────────────────▼───────────────────────────────────────┐
│  聚合支付网关（pay-gateway 模块）                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ 支付编排器   │  │ 渠道路由器   │  │ 回调统一接收器       │  │
│  │ (Orchestrator)│ │ (Router)    │  │ (NotifyReceiver)    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ 渠道适配器层（Channel Adapter）                          │ │
│  │  MockAdapter │ WechatAdapter │ AlipayAdapter │ UnionAdapter│ │
│  │  （开发/测试）  （生产）        （生产）         （扩展）    │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│  数据层                                                      │
│  pay_order（支付主单） │ pay_flow（支付流水） │ pay_refund（退款）│
│  pay_notify_log（回调日志） │ pay_channel_config（渠道配置）    │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 关键设计决策

| 决策 | 方案 | 理由 |
|------|------|------|
| 支付网关位置 | 作为 Minecraft 后端内部模块（`com.minecraft.pay`），非独立微服务 | 项目当前为单体架构，独立部署增加运维成本；通过包结构隔离保持可拆分性 |
| 渠道隔离 | 每个渠道实现 `PayChannelAdapter` 接口，业务层只依赖接口 | 新增渠道不改动业务代码，符合开闭原则 |
| 模拟/真实切换 | 通过 `pay.channel.mock-enabled` 配置控制，MockAdapter 与真实 Adapter 互斥加载 | 开发期零依赖第三方，上线只改配置不改代码 |
| 幂等保障 | 支付流水号唯一索引 + 回调验签 + 状态机校验 | 防止重复支付与重复回调 |
| 回调统一入口 | `/api/pay/notify/{channelCode}` 统一接收，按渠道分发验签 | 避免每新增渠道开发新回调接口 |


## 3. 数据库设计

### 3.1 支付主单表 `pay_order`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| pay_no | varchar(64) UK | 支付主单号（平台生成，全局唯一） |
| biz_order_no | varchar(64) | 业务订单号（如商城订单号、酒店订单号） |
| biz_type | varchar(32) | 业务类型：MALL / HOTEL / TICKET / POINTS / GROUP |
| user_id | bigint | 付款用户 ID |
| total_amount | decimal(12,2) | 订单总金额（元） |
| pay_amount | decimal(12,2) | 实付金额（元） |
| points_deduct | decimal(12,2) | 积分抵扣金额（元，混合支付场景） |
| channel_code | varchar(32) | 支付渠道：MOCK / WECHAT / ALIPAY / UNIONPAY |
| status | varchar(20) | 状态：INIT / PAYING / SUCCESS / FAILED / CLOSED / REFUNDED |
| channel_trade_no | varchar(128) | 渠道交易号（微信 transaction_id 等） |
| expire_time | datetime | 支付过期时间 |
| pay_time | datetime | 支付成功时间 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

**索引**：`uk_pay_no`（唯一）、`idx_biz_order`（biz_order_no, biz_type）、`idx_user_status`（user_id, status）、`idx_channel_trade`（channel_code, channel_trade_no）

### 3.2 支付流水表 `pay_flow`

每发起一次支付尝试（包括重新支付、更换渠道）生成一条流水，用于追踪支付尝试历史。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| flow_no | varchar(64) UK | 流水号（唯一索引，防并发重复） |
| pay_no | varchar(64) | 关联支付主单号 |
| channel_code | varchar(32) | 本次尝试的渠道 |
| channel_prepay_id | varchar(128) | 渠道预支付标识（微信 prepay_id 等） |
| amount | decimal(12,2) | 本次支付金额 |
| status | varchar(20) | INIT / PAYING / SUCCESS / FAILED |
| channel_response | text | 渠道原始响应（JSON） |
| created_at | datetime | 创建时间 |

**索引**：`uk_flow_no`（唯一）、`idx_pay_no`

### 3.3 退款表 `pay_refund`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| refund_no | varchar(64) UK | 退款单号 |
| pay_no | varchar(64) | 原支付单号 |
| refund_amount | decimal(12,2) | 退款金额 |
| reason | varchar(256) | 退款原因 |
| status | varchar(20) | INIT / PROCESSING / SUCCESS / FAILED |
| channel_refund_no | varchar(128) | 渠道退款单号 |
| refund_time | datetime | 退款完成时间 |
| created_at | datetime | 创建时间 |

### 3.4 回调日志表 `pay_notify_log`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| channel_code | varchar(32) | 渠道标识 |
| raw_body | text | 原始回调报文 |
| headers | text | 请求头（JSON） |
| verify_result | tinyint | 验签结果：0-失败 1-成功 |
| process_result | varchar(32) | 处理结果：SUCCESS / IGNORED / ERROR |
| created_at | datetime | 接收时间 |

用于问题排查与审计，不参与业务逻辑。


## 4. 核心接口设计

### 4.1 统一支付 API（供业务模块调用）

**POST** `/api/pay/create`

请求体：
```json
{
  "bizOrderNo": "MALL202610180001",
  "bizType": "MALL",
  "userId": 10086,
  "totalAmount": 299.00,
  "pointsDeduct": 0,
  "channelCode": "WECHAT",
  "subject": "云南六日游特产礼包",
  "expireMinutes": 30
}
```

响应：
```json
{
  "code": 200,
  "data": {
    "payNo": "PAY20261018000001",
    "flowNo": "FLOW20261018000001",
    "channelCode": "WECHAT",
    "payParams": {
      "prepayId": "wx20261018...",
      "nonceStr": "...",
      "paySign": "...",
      "appId": "..."
    },
    "expireTime": "2026-10-18 12:30:00"
  }
}
```

业务模块拿到 `payParams` 后，交由前端对应 SDK 调起支付（微信 JSAPI / 支付宝表单 / 模拟支付确认页）。

### 4.2 统一回调入口

**POST** `/api/pay/notify/{channelCode}`

- `channelCode` 匹配渠道适配器，调用其 `verifyNotify()` 与 `parseNotify()`
- 验签失败返回 400，记录日志
- 验签成功且金额校验通过，更新 `pay_order` 与 `pay_flow` 状态
- **幂等**：若 `pay_order.status` 已为 `SUCCESS`，直接返回成功，不重复处理

### 4.3 查单接口

**GET** `/api/pay/query/{payNo}`

返回支付主单当前状态。业务前端在支付返回后主动查询一次，作为回调的补充（防止回调延迟）。

### 4.4 退款接口

**POST** `/api/pay/refund`

请求体：
```json
{
  "payNo": "PAY20261018000001",
  "refundAmount": 100.00,
  "reason": "用户取消部分商品"
}
```

支持全额/部分退款。退款成功后更新原 `pay_order.status` 为 `REFUNDED`（全额）或保持 `SUCCESS` 但记录部分退款金额（部分退款场景）。


## 5. 支付完整流程

### 5.1 正向支付流程

```
业务前端                业务后端              支付网关              渠道适配器
   │                      │                     │                     │
   │ ① 提交订单            │                     │                     │
   │─────────────────────>│                     │                     │
   │                      │ ② 创建支付单         │                     │
   │                      │────────────────────>│                     │
   │                      │                     │ ③ 路由到适配器       │
   │                      │                     │────────────────────>│
   │                      │                     │ ④ 调用渠道下单       │
   │                      │                     │<────────────────────│
   │                      │ ⑤ 返回 payParams     │                     │
   │                      │<────────────────────│                     │
   │ ⑥ 返回支付参数        │                     │                     │
   │<─────────────────────│                     │                     │
   │                      │                     │                     │
   │ ⑦ 用户完成支付（模拟确认 / 微信收银台 / 支付宝）                     │
   │                      │                     │                     │
   │                      │        ⑧ 渠道异步回调 │                     │
   │                      │<────────────────────│                     │
   │                      │ ⑨ 验签、更新状态     │                     │
   │                      │ ⑩ 通知业务模块       │                     │
   │                      │                     │                     │
   │ ⑪ 前端轮询/查单确认   │                     │                     │
   │─────────────────────>│                     │                     │
```

**步骤说明**：

1. 用户在商城/酒店/票务页面提交订单（业务订单已创建，状态为“待支付”）
2. 业务后端调用 `/api/pay/create`，传入业务订单号与金额
3. 支付网关根据 `channelCode` 选择适配器（Mock / Wechat / Alipay）
4. 适配器调用渠道 API（模拟渠道直接返回成功预支付标识；真实渠道返回 prepay_id 等）
5. 支付网关保存 `pay_order`（INIT）与 `pay_flow`，返回前端调起支付所需参数
6. 前端根据渠道类型调起支付：微信 JSAPI 调 `wx.requestPayment`，模拟渠道展示“确认支付”弹窗
7. 用户完成支付操作
8. 渠道异步回调至 `/api/pay/notify/{channelCode}`
9. 支付网关验签、金额校验、幂等检查后更新 `pay_order` 为 `SUCCESS`，记录 `channel_trade_no`
10. 支付网关通过内部事件（Spring Event 或直接调用）通知业务模块：`bizOrderNo` 对应订单已支付
11. 前端支付返回后调用查单接口确认最终状态，更新页面

### 5.2 模拟支付实现

**MockAdapter 行为**：

| 环节 | 行为 |
|------|------|
| 下单 | 生成模拟 `prepay_id`（如 `mock_prepay_xxx`），返回给前端 |
| 前端调起 | 前端检测到 `channelCode=MOCK` 时，展示“模拟支付确认框”（确认支付 / 取消） |
| 用户确认 | 前端调用 `/api/pay/mock/confirm/{flowNo}`，MockAdapter 直接返回成功 |
| 回调 | MockAdapter 主动触发一次内部回调（或由前端 confirm 接口触发状态更新） |
| 状态更新 | 与真实回调走相同逻辑：验签（模拟跳过）、金额校验、更新状态 |

**关键**：模拟支付的代码路径与真实支付**共享同一套状态机与数据表**，仅渠道适配器不同。这样切换到真实渠道时，业务模块与前端逻辑无需改动。

### 5.3 退款流程

```
业务后端                支付网关              渠道适配器
   │                      │                     │
   │ ① 发起退款           │                     │
   │────────────────────>│                     │
   │                      │ ② 校验原单状态       │
   │                      │ ③ 调用渠道退款       │
   │                      │────────────────────>│
   │                      │ ④ 渠道退款结果       │
   │                      │<────────────────────│
   │                      │ ⑤ 更新退款表与主单   │
   │ ⑥ 返回退款结果        │                     │
   │<────────────────────│                     │
```

退款同样支持模拟与真实：MockAdapter 直接返回退款成功；真实适配器调用渠道退款 API 并处理异步退款通知（如有）。


## 6. 模拟支付与真实支付切换

### 6.1 配置项

`application.yml`：

```yaml
pay:
  # 模拟支付开关：true=所有渠道走 MockAdapter，false=按 channelCode 走真实适配器
  mock-enabled: true
  # 模拟支付时允许的渠道（可选，为空表示全部走 mock）
  mock-channels: MOCK, WECHAT, ALIPAY
  
  wechat:
    app-id: ${WECHAT_APP_ID:}
    mch-id: ${WECHAT_MCH_ID:}
    api-v3-key: ${WECHAT_API_V3_KEY:}
    notify-url: ${PAY_NOTIFY_BASE:http://localhost:8080}/api/pay/notify/WECHAT
  
  alipay:
    app-id: ${ALIPAY_APP_ID:}
    private-key: ${ALIPAY_PRIVATE_KEY:}
    alipay-public-key: ${ALIPAY_PUBLIC_KEY:}
    notify-url: ${PAY_NOTIFY_BASE:http://localhost:8080}/api/pay/notify/ALIPAY
```

### 6.2 切换机制

支付网关启动时根据配置决定加载哪些适配器 Bean：

```java
@Configuration
public class PayChannelConfig {

    @Bean
    @ConditionalOnProperty(name = "pay.mock-enabled", havingValue = "true")
    public PayChannelAdapter mockAdapter() {
        return new MockChannelAdapter();
    }

    @Bean
    @ConditionalOnProperty(name = "pay.mock-enabled", havingValue = "false")
    public PayChannelAdapter wechatAdapter(WechatPayProperties props) {
        return new WechatChannelAdapter(props);
    }

    // alipayAdapter 同理
}
```

**上线切换步骤**：
1. 在 `application-prod.yml` 中设置 `pay.mock-enabled: false`
2. 填入真实的微信/支付宝商户配置（通过环境变量注入，不写死在配置文件）
3. 确认回调 URL 为公网可访问地址（`pay.notify-base`）
4. 在渠道商户平台配置回调地址为 `https://your-domain.com/api/pay/notify/WECHAT`
5. 用小额真实订单验证全链路（下单 → 支付 → 回调 → 查单 → 退款）

### 6.3 环境隔离建议

| 环境 | `mock-enabled` | 用途 |
|------|---------------|------|
| 本地开发 | true | 零依赖调试 |
| 测试环境 | true | 集成测试、自动化测试 |
| 预发布 | false（沙箱配置） | 渠道沙箱联调 |
| 生产 | false | 真实交易 |


## 7. 后续扩展能力

### 7.1 新增支付渠道

实现 `PayChannelAdapter` 接口：

```java
public interface PayChannelAdapter {
    String channelCode();
    PrepayResult prepay(PrepayRequest request);
    NotifyResult verifyAndParseNotify(HttpServletRequest request);
    RefundResult refund(RefundRequest request);
    QueryResult query(String payNo, String channelTradeNo);
}
```

以云闪付为例：
1. 新建 `UnionPayChannelAdapter implements PayChannelAdapter`
2. 实现 `prepay()` 调用银联全渠道下单接口
3. 实现 `verifyAndParseNotify()` 验签银联回调
4. 在 `PayChannelConfig` 中注册 Bean
5. 业务层无需改动，前端根据 `channelCode` 调起银联支付

### 7.2 混合支付（积分 + 现金）

当前设计已在 `pay_order` 中预留 `points_deduct` 字段。扩展步骤：
1. 支付创建时，先扣减用户积分（记录积分流水）
2. 剩余金额走渠道支付
3. 若渠道支付失败，**回滚积分扣减**
4. 积分部分退款时，优先退回积分，超出部分退现金

### 7.3 分账能力（跟团游账单场景）

跟团游账单分摊涉及多人收款，可扩展分账能力：
- 接入微信分账 API 或支付宝分账 API
- 在 `pay_order` 中记录 `profit_sharing` 标识
- 支付成功后按预设比例分账给团长/平台

### 7.4 对账系统

**日终对账**（建议二期实现）：
- 每日凌晨下载渠道对账单（微信 `downloadbill`、支付宝对账单）
- 与本地 `pay_flow` 逐笔比对
- 差异记录告警（少收、多收、渠道有本地无、本地有渠道无）
- 自动触发补单或退款流程


## 8. 安全与幂等

### 8.1 安全措施

| 措施 | 实现 |
|------|------|
| 回调验签 | 微信 V3 使用平台证书验签；支付宝使用 RSA2 验签；Mock 跳过 |
| 金额校验 | 回调金额必须与 `pay_flow.amount` 一致，不一致拒绝处理并告警 |
| 配置加密 | 商户密钥使用环境变量注入，不写入代码仓库 |
| 接口鉴权 | `/api/pay/create` 仅允许内部服务调用（通过内部 Token 或 Spring Security 配置） |
| 防重放 | 回调日志记录原始报文，重复通知幂等返回成功 |

### 8.2 幂等设计

**支付创建幂等**：
- 同一 `bizOrderNo + bizType` 若已有 `SUCCESS` 的 `pay_order`，直接返回已有结果
- 若已有 `INIT` 或 `PAYING` 的 `pay_order` 且未过期，返回原支付参数

**回调幂等**：
- `pay_flow.flow_no` 唯一索引，`pay_order.status` 状态机校验
- 更新语句带条件：`UPDATE pay_order SET status='SUCCESS' WHERE pay_no=? AND status IN ('INIT','PAYING')`
- 影响行数为 0 说明已处理过，直接返回成功


## 9. 测试要点

| 测试场景 | 预期 |
|---------|------|
| 正常支付（Mock） | 确认后订单状态变为 SUCCESS，业务模块收到通知 |
| 重复回调 | 第二次回调返回成功，不重复更新状态 |
| 金额篡改回调 | 验签通过但金额不匹配 → 拒绝，记录告警日志 |
| 支付过期 | `expire_time` 后发起支付 → 拒绝或重新创建 |
| 部分退款 | 退款 50 元后，原单仍为 SUCCESS，退款表记录成功 |
| 全额退款 | 退款全额后，原单变为 REFUNDED |
| 切换真实渠道（沙箱） | 使用微信/支付宝沙箱环境验证下单、回调、查单全链路 |


## 10. 开发任务拆分

| 序号 | 任务 | 负责 | 预估 |
|------|------|------|------|
| 1 | 数据库表创建（pay_order / pay_flow / pay_refund / pay_notify_log） | 后端 | 0.5d |
| 2 | `PayChannelAdapter` 接口与 DTO 定义 | 后端 | 0.5d |
| 3 | MockChannelAdapter 实现（含模拟回调触发） | 后端 | 1d |
| 4 | 支付编排器（创建、查单、退款、状态机） | 后端 | 2d |
| 5 | 统一回调入口与验签分发 | 后端 | 1d |
| 6 | 业务模块改造：商城下单接入聚合支付 | 后端 | 1d |
| 7 | 业务模块改造：酒店/票务接入（复用同一 API） | 后端 | 0.5d |
| 8 | 前端：支付调起组件（适配 Mock / 微信 / 支付宝） | 前端 | 1.5d |
| 9 | 前端：模拟支付确认弹窗 | 前端 | 0.5d |
| 10 | 微信/支付宝真实适配器实现（上线前） | 后端 | 3d |
| 11 | 全链路联调与测试 | 全员 | 2d |

**一期目标**：Mock 支付全链路跑通，商城模块完成接入。  
**二期目标**：真实渠道适配器完成，生产环境切换验证。


## 附录：与现有系统的集成点

| 现有模块 | 改造方式 |
|---------|----------|
| `orders` 表 | 增加 `pay_no` 字段关联支付主单 |
| `hotel_booking` / `ticket_booking` | 同上 |
| 积分商城 `exchange_order` | 支付成功后扣减积分（或积分抵扣后剩余金额走支付） |
| 跟团游账单 | 调用同一支付 API，`bizType=GROUP` |
| 管理端 | 新增“支付管理”菜单：支付流水查询、退款操作、回调日志查看 |