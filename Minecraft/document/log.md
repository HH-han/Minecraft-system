Spring Boot 操作日志别再手写了：我用一个注解，把“谁在什么时候改了什么”自动记下来
图片
Spring Boot 操作日志别再手写了：我用一个注解，把“谁在什么时候改了什么”自动记下来

前段时间业务提了一个看起来很简单的需求。

订单后台里，想知道一张订单到底是谁改的。

比如原来状态是：

待审核
后来变成：

审核通过
产品希望能直接看到：

2026-10-18 10:23

操作人：张三

操作内容：
将退款状态从“待审核”修改为“审核通过”
再比如商品价格从：

99.00
改成：

89.00
希望后台能够看到：

张三将商品价格从 99.00 修改为 89.00
这种需求在后台系统里特别常见。

订单要。

商品要。

用户要。

权限要。

审核要。

合同要。

刚开始我们处理得非常直接。

每个 Service 自己写操作日志。

比如订单审核：

@Transactional
public void approveOrder(
        Long orderId,
        Long operatorId) {
    Order order =
            orderRepository
                    .findById(orderId)
                    .orElseThrow();
    order.approve();
    operationLogService.save(
            operatorId,
            "ORDER",
            orderId,
            "审核通过订单"
    );
}
修改商品价格：

@Transactional
public void updatePrice(
        Long productId,
        BigDecimal price,
        Long operatorId) {
    Product product =
            productRepository
                    .findById(productId)
                    .orElseThrow();
    BigDecimal oldPrice =
            product.getPrice();
    product.setPrice(price);
    operationLogService.save(
            operatorId,
            "PRODUCT",
            productId,
            "价格从 "
                    + oldPrice
                    + " 修改为 "
                    + price
    );
}
一开始只有两三个地方。

没什么问题。

后来操作日志越来越多。

代码慢慢变成：

修改订单
记日志

修改商品
记日志

修改用户
记日志

修改权限
记日志

审核退款
记日志

修改合同
记日志
最后很多 Service 里面真正的业务代码只有十几行。

操作日志相关代码反而占了一大块。

更麻烦的是，大家写法还不一样。

有人写：

修改订单
有人写：

订单更新成功
有人写：

update order
还有人干脆忘了写。

这时候我开始觉得：

操作日志不应该继续散落在业务代码里。


后来我们先做了一个很简单的注解。

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    String module();
    String action();
    String bizId() default "";
}
比如订单审核：

@OperationLog(
        module = "ORDER",
        action = "审核订单",
        bizId = "
#orderId
"
)
@Transactional
public void approveOrder(
        Long orderId) {
    Order order =
            orderRepository
                    .findById(orderId)
                    .orElseThrow();
    order.approve();
}
修改价格：

@OperationLog(
        module = "PRODUCT",
        action = "修改商品价格",
        bizId = "
#productId
"
)
@Transactional
public void updatePrice(
        Long productId,
        BigDecimal price) {
    Product product =
            productRepository
                    .findById(productId)
                    .orElseThrow();
    product.setPrice(price);
}
业务代码一下干净了很多。

但注解本身不会干任何事情。

真正负责记录日志的是 AOP。

切面大概这样：

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private final OperationLogService logService;
    @Around("@annotation(operationLog)")
    public Object around(
            ProceedingJoinPoint joinPoint,
            OperationLog operationLog)
            throws Throwable {
        long start =
                System.currentTimeMillis();
        try {
            Object result =
                    joinPoint.proceed();
            logService.saveSuccess(
                    operationLog.module(),
                    operationLog.action(),
                    System.currentTimeMillis()
                            - start
            );
            return result;
        } catch (Throwable e) {
            logService.saveFailure(
                    operationLog.module(),
                    operationLog.action(),
                    e.getMessage(),
                    System.currentTimeMillis()
                            - start
            );
            throw e;
        }
    }
}
最开始我们只记录几个字段：

模块
操作类型
操作人
操作时间
耗时
成功/失败
例如：

模块：ORDER
操作：审核订单
操作人：10086
结果：SUCCESS
耗时：83ms
这样已经比每个 Service 手写：

operationLogService.save(...)
舒服很多。

因为以后新接口需要日志，只加：

@OperationLog(...)
就行。

但很快产品又提出一个问题。

他说：

我知道这个人点了“修改商品”，但他到底改了什么？

这个问题一出来，日志需求就从：

记录动作
升级成了：

记录变化
而这两个东西完全不是一个难度。

最简单的方案，是把方法参数直接存下来。

比如：

public void updatePrice(
        Long productId,
        BigDecimal price)
记录：

{
  "productId": 10086,
  "price": 89.00
}
虽然能查，但用户看不懂。

产品真正想看到的是：

商品价格：
99.00 → 89.00
修改用户：

手机号：
138****1111 → 139****2222

会员等级：
普通会员 → VIP
也就是说，需要知道：

修改前
和：

修改后
于是第二版开始做数据快照。

比如修改商品。

方法执行之前，先取一次：

Product before =
        productRepository
                .findById(productId)
                .orElseThrow();
方法执行之后，再取一次：

Product after =
        productRepository
                .findById(productId)
                .orElseThrow();
然后比较：

before

vs

after。
但这样很快又发现不太行。

因为每一次写操作都多：

2 次数据库查询
如果原来的业务里本来就已经查过 Entity，又重复查询，非常浪费。

而且如果操作比较复杂，一次修改涉及：

订单主表
订单明细
退款表
状态流水
简单的 before / after Entity 已经不能描述整个业务。


所以我们后来没有做一个所谓的：

万能自动 Diff
而是做了一个折中的设计。

框架负责“什么时候记日志、谁操作、结果如何”。

业务自己负责描述“到底改了什么”。

于是注解改成：

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    String module();
    String action();
    String bizId() default "";
    String detail() default "";
}
使用 Spring EL 表达式。

比如：

@OperationLog(
        module = "PRODUCT",
        action = "修改商品价格",
        bizId = "
#productId
",
        detail = "'价格修改为：' + 
#price
"
)
public void updatePrice(
        Long productId,
        BigDecimal price) {
    ...
}
记录结果：

模块：PRODUCT

业务 ID：10086

动作：修改商品价格

详情：价格修改为：89.00
这种适合简单操作。

但像：

99 → 89
这种需要旧值的，就让业务返回一个结果对象。

例如：

public record PriceChangeResult(
        Long productId,
        BigDecimal oldPrice,
        BigDecimal newPrice
) {
}
Service：

@OperationLog(
        module = "PRODUCT",
        action = "修改商品价格",
        bizId = "
#result
.productId",
        detail =
                "'商品价格从 '
                + 
#result
.oldPrice
                + ' 修改为 '
                + 
#result
.newPrice"
)
@Transactional
public PriceChangeResult updatePrice(
        Long productId,
        BigDecimal price) {
    Product product =
            productRepository
                    .findById(productId)
                    .orElseThrow();
    BigDecimal oldPrice =
            product.getPrice();
    product.setPrice(price);
    return new PriceChangeResult(
            productId,
            oldPrice,
            price
    );
}
这样切面执行完业务方法以后，就可以拿到：result

最终记录：

商品价格从 99.00 修改为 89.00
既没有额外查数据库，也没有让 AOP 去猜业务含义。

后来这个设计慢慢变得很好用。

比如订单审核：

@OperationLog(
        module = "ORDER",
        action = "审核订单",
        bizId = "
#orderId
",
        detail = "'订单审核通过'"
)
public void approve(
        Long orderId) {
    ...
}
驳回：

@OperationLog(
        module = "ORDER",
        action = "驳回订单",
        bizId = "
#orderId
",
        detail = "'驳回原因：' + 
#reason
"
)
public void reject(
        Long orderId,
        String reason) {
    ...
}
权限调整：

@OperationLog(
        module = "USER",
        action = "修改用户角色",
        bizId = "
#userId
",
        detail =
                "'角色调整为：' + 
#role
"
)
public void changeRole(
        Long userId,
        String role) {
    ...
}
这些日志不再需要业务代码里主动：

operationLogService.save(...)
业务只描述：

我是谁

我在做什么

需要记录什么。
怎么取当前用户、IP、请求路径、耗时、是否失败，全都交给日志框架。

然后我们把操作日志表也重新设计了一下。

原来只有：

operation_log
几个简单字段。

后来变成：

CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    biz_id VARCHAR(100),
    operator_id BIGINT,
    operator_name VARCHAR(100),
    request_uri VARCHAR(500),
    request_method VARCHAR(20),
    client_ip VARCHAR(64),
    detail TEXT,
    success TINYINT NOT NULL,
    error_message VARCHAR(1000),
    cost_ms BIGINT,
    create_time DATETIME NOT NULL
);
这样一条日志能回答的问题就比较完整：

谁？

什么时候？

操作了哪个业务对象？

执行了什么动作？

具体修改了什么？

成功还是失败？

失败原因是什么？

耗时多久？

从哪个 IP 发起？
以后业务查问题就不用再翻几十 MB 的应用日志。

直接后台搜索：

订单 ID = 10086
就能看到整条操作历史。

但做操作日志还有一个很容易踩的坑。

就是：

不要什么参数都直接序列化。

比如登录接口：

public void login(
        String username,
        String password) {
}
如果切面里简单粗暴：

objectMapper.writeValueAsString(
        joinPoint.getArgs()
);
操作日志里可能直接出现：

{
  "username": "zhangsan",
  "password": "123456"
}
这就不是审计日志了。

这是给自己挖坑。

还有：

身份证
银行卡
手机号
AccessToken
Authorization
Cookie
这类数据也不能随便落日志。

所以我们后来明确规定：

操作日志默认不记录完整请求参数。

只有业务通过：

detail = "..."
明确声明允许记录的内容，才写进去。

一些确实需要结构化参数的场景，再配一个：

@LogIgnore
例如：

public record UpdateUserCommand(
        String nickname,
        String mobile,
        @LogIgnore
        String idCard
) {
}

日志框架序列化时自动跳过敏感字段。

这个设计比：

先什么都记录

以后再脱敏
安全得多。

还有一个问题：

操作日志到底应该在事务里保存，还是事务外保存？

比如：

@Transactional
@OperationLog(...)
public void approveOrder(
        Long orderId) {
    ...
}
假设业务执行失败。

订单事务：

ROLLBACK。
但我们反而希望保留一条：

张三尝试审核订单失败

原因：订单已经取消。
如果操作日志跟业务使用同一个事务：

业务 rollback
日志也：

rollback。
最后失败行为反而看不到。

所以日志保存我一般会使用一个独立事务。

例如：

@Transactional(
        propagation = Propagation.REQUIRES_NEW
)
public void save(
        OperationLogEntity log) {
    repository.save(log);
}
这样：

业务事务
    ↓
失败
    ↓
ROLLBACK
操作日志：

新事务
    ↓
保存失败信息
    ↓
COMMIT
业务失败了。

审计记录仍然存在。

这对：

权限修改

审核

退款

财务

后台管理
这种系统特别重要。

不过这里又有一个性能问题。

如果每一个普通后台操作，都要同步：

INSERT operation_log
一次，业务请求就多一条 SQL。

量不大的后台系统，其实完全无所谓。

但如果日志量已经非常大，比如：

每秒几百
甚至几千条
我会再考虑：

异步写入
MQ
日志专库。
不过我不会一开始就把操作日志架构做得特别重。

如果系统一天只有：

几万次后台操作
一个：

REQUIRES_NEW
+
INSERT
通常已经够了。

先把日志真正记完整，比一上来搭 Kafka 更重要。

后来又遇到一个特别实用的需求。

产品说：

能不能把操作日志显示得更像人看的，而不是机器看的？

比如原来：

refundStatus:
PENDING → APPROVED
开发人员能看懂。

客服更希望看到：

退款状态：
待审核 → 审核通过
这个时候不能简单调用：

enum.name()。
我们给业务枚举统一加了展示名称：

public enum RefundStatus {
    PENDING("待审核"),
    APPROVED("审核通过"),
    REJECTED("已驳回");
    private final String label;
    RefundStatus(
            String label) {
        this.label = label;
    }
    public String label() {
        return label;
    }
}
日志结果对象直接返回：

public record RefundStatusChange(
        String oldStatus,
        String newStatus
) {
}
最后日志：

退款状态从“待审核”
修改为“审核通过”。
这样操作日志终于从：

开发排查工具
变成：

业务也真正能用的功能。
后来我们还把这个模式用到了很多地方。

比如配置变更。

以前：

配置已修改
现在：

短信每日发送上限：

1000 → 2000。
商品：

商品销售状态：

下架 → 上架
权限：

用户角色：

客服 → 客服主管
订单：

退款金额：

100.00 → 80.00
这种日志在真正排查业务问题的时候非常有用。

因为很多线上问题最后业务问的根本不是：

这个接口有没有报错？
而是：

是谁改的？

什么时候改的？

改之前是什么？

改完是什么？

为什么变成这样？
普通应用日志很难回答这些问题。

审计日志就是专门干这个的。

不过做到后面我也越来越确定一件事：

不要试图让 AOP 自动理解所有业务。

比如你写一个所谓“通用 Diff 引擎”，把所有 Entity 的所有字段都自动比较。

最后很容易出现：

updateTime：
10:21 → 10:22

version：
7 → 8

modifiedBy：
1001 → 1002
这些系统字段全部混进日志。

真正重要的：

退款状态
反而埋在十几个字段里。

还有集合变化。

比如权限：

原来：
[A, B, C]

后来：
[A, C, D]
真正有意义的是：

移除：B
新增：D
而不是简单打印：

[A,B,C] → [A,C,D]。
所以后来我们的原则很简单。

基础框架负责：

操作人
请求信息
耗时
成功/失败
时间
业务 ID。
真正的业务变化：

由业务自己描述。
这样虽然不能做到：

加一个注解什么都自动完成
但日志质量高很多。

我觉得这是值得的。

现在再写后台接口，我一般会先判断：

这个操作以后有没有可能有人问：

谁改的？
如果有，那一开始就把操作日志考虑进去。

因为上线半年以后再补，通常很痛苦。

历史数据没有。

以前谁改过也不知道。

只能翻：

Nginx 日志
应用日志
数据库 binlog
一点点猜。

尤其是：

订单状态

用户权限

商品价格

财务配置

系统参数

审核结果
这些关键数据。

它们不应该只有：

现在是什么。
最好还能回答：

它是怎么一步一步变成现在这样的。

这次改造以后，业务 Service 里少掉了大量：

operationLogService.save(...)。
但真正的价值其实不只是代码变干净。

而是所有关键业务操作终于有了一套统一规则。

以后新增一个接口，开发人员只需要考虑：

@OperationLog(
    module = "...",
    action = "...",
    bizId = "...",
    detail = "..."
)
剩下的：

谁操作
什么时候
从哪里请求
执行多久
成功还是失败
全部统一完成。Spring Boot 操作日志别再手写了：我用一个注解，把“谁在什么时候改了什么”自动记下来
图片
Spring Boot 操作日志别再手写了：我用一个注解，把“谁在什么时候改了什么”自动记下来

前段时间业务提了一个看起来很简单的需求。

订单后台里，想知道一张订单到底是谁改的。

比如原来状态是：

待审核
后来变成：

审核通过
产品希望能直接看到：

2026-10-18 10:23

操作人：张三

操作内容：
将退款状态从“待审核”修改为“审核通过”
再比如商品价格从：

99.00
改成：

89.00
希望后台能够看到：

张三将商品价格从 99.00 修改为 89.00
这种需求在后台系统里特别常见。

订单要。

商品要。

用户要。

权限要。

审核要。

合同要。

刚开始我们处理得非常直接。

每个 Service 自己写操作日志。

比如订单审核：

@Transactional
public void approveOrder(
        Long orderId,
        Long operatorId) {
    Order order =
            orderRepository
                    .findById(orderId)
                    .orElseThrow();
    order.approve();
    operationLogService.save(
            operatorId,
            "ORDER",
            orderId,
            "审核通过订单"
    );
}
修改商品价格：

@Transactional
public void updatePrice(
        Long productId,
        BigDecimal price,
        Long operatorId) {
    Product product =
            productRepository
                    .findById(productId)
                    .orElseThrow();
    BigDecimal oldPrice =
            product.getPrice();
    product.setPrice(price);
    operationLogService.save(
            operatorId,
            "PRODUCT",
            productId,
            "价格从 "
                    + oldPrice
                    + " 修改为 "
                    + price
    );
}
一开始只有两三个地方。

没什么问题。

后来操作日志越来越多。

代码慢慢变成：

修改订单
记日志

修改商品
记日志

修改用户
记日志

修改权限
记日志

审核退款
记日志

修改合同
记日志
最后很多 Service 里面真正的业务代码只有十几行。

操作日志相关代码反而占了一大块。

更麻烦的是，大家写法还不一样。

有人写：

修改订单
有人写：

订单更新成功
有人写：

update order
还有人干脆忘了写。

这时候我开始觉得：

操作日志不应该继续散落在业务代码里。


后来我们先做了一个很简单的注解。

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    String module();
    String action();
    String bizId() default "";
}
比如订单审核：

@OperationLog(
        module = "ORDER",
        action = "审核订单",
        bizId = "
#orderId
"
)
@Transactional
public void approveOrder(
        Long orderId) {
    Order order =
            orderRepository
                    .findById(orderId)
                    .orElseThrow();
    order.approve();
}
修改价格：

@OperationLog(
        module = "PRODUCT",
        action = "修改商品价格",
        bizId = "
#productId
"
)
@Transactional
public void updatePrice(
        Long productId,
        BigDecimal price) {
    Product product =
            productRepository
                    .findById(productId)
                    .orElseThrow();
    product.setPrice(price);
}
业务代码一下干净了很多。

但注解本身不会干任何事情。

真正负责记录日志的是 AOP。

切面大概这样：

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private final OperationLogService logService;
    @Around("@annotation(operationLog)")
    public Object around(
            ProceedingJoinPoint joinPoint,
            OperationLog operationLog)
            throws Throwable {
        long start =
                System.currentTimeMillis();
        try {
            Object result =
                    joinPoint.proceed();
            logService.saveSuccess(
                    operationLog.module(),
                    operationLog.action(),
                    System.currentTimeMillis()
                            - start
            );
            return result;
        } catch (Throwable e) {
            logService.saveFailure(
                    operationLog.module(),
                    operationLog.action(),
                    e.getMessage(),
                    System.currentTimeMillis()
                            - start
            );
            throw e;
        }
    }
}
最开始我们只记录几个字段：

模块
操作类型
操作人
操作时间
耗时
成功/失败
例如：

模块：ORDER
操作：审核订单
操作人：10086
结果：SUCCESS
耗时：83ms
这样已经比每个 Service 手写：

operationLogService.save(...)
舒服很多。

因为以后新接口需要日志，只加：

@OperationLog(...)
就行。

但很快产品又提出一个问题。

他说：

我知道这个人点了“修改商品”，但他到底改了什么？

这个问题一出来，日志需求就从：

记录动作
升级成了：

记录变化
而这两个东西完全不是一个难度。

最简单的方案，是把方法参数直接存下来。

比如：

public void updatePrice(
        Long productId,
        BigDecimal price)
记录：

{
  "productId": 10086,
  "price": 89.00
}
虽然能查，但用户看不懂。

产品真正想看到的是：

商品价格：
99.00 → 89.00
修改用户：

手机号：
138****1111 → 139****2222

会员等级：
普通会员 → VIP
也就是说，需要知道：

修改前
和：

修改后
于是第二版开始做数据快照。

比如修改商品。

方法执行之前，先取一次：

Product before =
        productRepository
                .findById(productId)
                .orElseThrow();
方法执行之后，再取一次：

Product after =
        productRepository
                .findById(productId)
                .orElseThrow();
然后比较：

before

vs

after。
但这样很快又发现不太行。

因为每一次写操作都多：

2 次数据库查询
如果原来的业务里本来就已经查过 Entity，又重复查询，非常浪费。

而且如果操作比较复杂，一次修改涉及：

订单主表
订单明细
退款表
状态流水
简单的 before / after Entity 已经不能描述整个业务。


所以我们后来没有做一个所谓的：

万能自动 Diff
而是做了一个折中的设计。

框架负责“什么时候记日志、谁操作、结果如何”。

业务自己负责描述“到底改了什么”。

于是注解改成：

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    String module();
    String action();
    String bizId() default "";
    String detail() default "";
}
使用 Spring EL 表达式。

比如：

@OperationLog(
        module = "PRODUCT",
        action = "修改商品价格",
        bizId = "
#productId
",
        detail = "'价格修改为：' + 
#price
"
)
public void updatePrice(
        Long productId,
        BigDecimal price) {
    ...
}
记录结果：

模块：PRODUCT

业务 ID：10086

动作：修改商品价格

详情：价格修改为：89.00
这种适合简单操作。

但像：

99 → 89
这种需要旧值的，就让业务返回一个结果对象。

例如：

public record PriceChangeResult(
        Long productId,
        BigDecimal oldPrice,
        BigDecimal newPrice
) {
}
Service：

@OperationLog(
        module = "PRODUCT",
        action = "修改商品价格",
        bizId = "
#result
.productId",
        detail =
                "'商品价格从 '
                + 
#result
.oldPrice
                + ' 修改为 '
                + 
#result
.newPrice"
)
@Transactional
public PriceChangeResult updatePrice(
        Long productId,
        BigDecimal price) {
    Product product =
            productRepository
                    .findById(productId)
                    .orElseThrow();
    BigDecimal oldPrice =
            product.getPrice();
    product.setPrice(price);
    return new PriceChangeResult(
            productId,
            oldPrice,
            price
    );
}
这样切面执行完业务方法以后，就可以拿到：result

最终记录：

商品价格从 99.00 修改为 89.00
既没有额外查数据库，也没有让 AOP 去猜业务含义。

后来这个设计慢慢变得很好用。

比如订单审核：

@OperationLog(
        module = "ORDER",
        action = "审核订单",
        bizId = "
#orderId
",
        detail = "'订单审核通过'"
)
public void approve(
        Long orderId) {
    ...
}
驳回：

@OperationLog(
        module = "ORDER",
        action = "驳回订单",
        bizId = "
#orderId
",
        detail = "'驳回原因：' + 
#reason
"
)
public void reject(
        Long orderId,
        String reason) {
    ...
}
权限调整：

@OperationLog(
        module = "USER",
        action = "修改用户角色",
        bizId = "
#userId
",
        detail =
                "'角色调整为：' + 
#role
"
)
public void changeRole(
        Long userId,
        String role) {
    ...
}
这些日志不再需要业务代码里主动：

operationLogService.save(...)
业务只描述：

我是谁

我在做什么

需要记录什么。
怎么取当前用户、IP、请求路径、耗时、是否失败，全都交给日志框架。

然后我们把操作日志表也重新设计了一下。

原来只有：

operation_log
几个简单字段。

后来变成：

CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    biz_id VARCHAR(100),
    operator_id BIGINT,
    operator_name VARCHAR(100),
    request_uri VARCHAR(500),
    request_method VARCHAR(20),
    client_ip VARCHAR(64),
    detail TEXT,
    success TINYINT NOT NULL,
    error_message VARCHAR(1000),
    cost_ms BIGINT,
    create_time DATETIME NOT NULL
);
这样一条日志能回答的问题就比较完整：

谁？

什么时候？

操作了哪个业务对象？

执行了什么动作？

具体修改了什么？

成功还是失败？

失败原因是什么？

耗时多久？

从哪个 IP 发起？
以后业务查问题就不用再翻几十 MB 的应用日志。

直接后台搜索：

订单 ID = 10086
就能看到整条操作历史。

但做操作日志还有一个很容易踩的坑。

就是：

不要什么参数都直接序列化。

比如登录接口：

public void login(
        String username,
        String password) {
}
如果切面里简单粗暴：

objectMapper.writeValueAsString(
        joinPoint.getArgs()
);
操作日志里可能直接出现：

{
  "username": "zhangsan",
  "password": "123456"
}
这就不是审计日志了。

这是给自己挖坑。

还有：

身份证
银行卡
手机号
AccessToken
Authorization
Cookie
这类数据也不能随便落日志。

所以我们后来明确规定：

操作日志默认不记录完整请求参数。

只有业务通过：

detail = "..."
明确声明允许记录的内容，才写进去。

一些确实需要结构化参数的场景，再配一个：

@LogIgnore
例如：

public record UpdateUserCommand(
        String nickname,
        String mobile,
        @LogIgnore
        String idCard
) {
}

日志框架序列化时自动跳过敏感字段。

这个设计比：

先什么都记录

以后再脱敏
安全得多。

还有一个问题：

操作日志到底应该在事务里保存，还是事务外保存？

比如：

@Transactional
@OperationLog(...)
public void approveOrder(
        Long orderId) {
    ...
}
假设业务执行失败。

订单事务：

ROLLBACK。
但我们反而希望保留一条：

张三尝试审核订单失败

原因：订单已经取消。
如果操作日志跟业务使用同一个事务：

业务 rollback
日志也：

rollback。
最后失败行为反而看不到。

所以日志保存我一般会使用一个独立事务。

例如：

@Transactional(
        propagation = Propagation.REQUIRES_NEW
)
public void save(
        OperationLogEntity log) {
    repository.save(log);
}
这样：

业务事务
    ↓
失败
    ↓
ROLLBACK
操作日志：

新事务
    ↓
保存失败信息
    ↓
COMMIT
业务失败了。

审计记录仍然存在。

这对：

权限修改

审核

退款

财务

后台管理
这种系统特别重要。

不过这里又有一个性能问题。

如果每一个普通后台操作，都要同步：

INSERT operation_log
一次，业务请求就多一条 SQL。

量不大的后台系统，其实完全无所谓。

但如果日志量已经非常大，比如：

每秒几百
甚至几千条
我会再考虑：

异步写入
MQ
日志专库。
不过我不会一开始就把操作日志架构做得特别重。

如果系统一天只有：

几万次后台操作
一个：

REQUIRES_NEW
+
INSERT
通常已经够了。

先把日志真正记完整，比一上来搭 Kafka 更重要。

后来又遇到一个特别实用的需求。

产品说：

能不能把操作日志显示得更像人看的，而不是机器看的？

比如原来：

refundStatus:
PENDING → APPROVED
开发人员能看懂。

客服更希望看到：

退款状态：
待审核 → 审核通过
这个时候不能简单调用：

enum.name()。
我们给业务枚举统一加了展示名称：

public enum RefundStatus {
    PENDING("待审核"),
    APPROVED("审核通过"),
    REJECTED("已驳回");
    private final String label;
    RefundStatus(
            String label) {
        this.label = label;
    }
    public String label() {
        return label;
    }
}
日志结果对象直接返回：

public record RefundStatusChange(
        String oldStatus,
        String newStatus
) {
}
最后日志：

退款状态从“待审核”
修改为“审核通过”。
这样操作日志终于从：

开发排查工具
变成：

业务也真正能用的功能。
后来我们还把这个模式用到了很多地方。

比如配置变更。

以前：

配置已修改
现在：

短信每日发送上限：

1000 → 2000。
商品：

商品销售状态：

下架 → 上架
权限：

用户角色：

客服 → 客服主管
订单：

退款金额：

100.00 → 80.00
这种日志在真正排查业务问题的时候非常有用。

因为很多线上问题最后业务问的根本不是：

这个接口有没有报错？
而是：

是谁改的？

什么时候改的？

改之前是什么？

改完是什么？

为什么变成这样？
普通应用日志很难回答这些问题。

审计日志就是专门干这个的。

不过做到后面我也越来越确定一件事：

不要试图让 AOP 自动理解所有业务。

比如你写一个所谓“通用 Diff 引擎”，把所有 Entity 的所有字段都自动比较。

最后很容易出现：

updateTime：
10:21 → 10:22

version：
7 → 8

modifiedBy：
1001 → 1002
这些系统字段全部混进日志。

真正重要的：

退款状态
反而埋在十几个字段里。

还有集合变化。

比如权限：

原来：
[A, B, C]

后来：
[A, C, D]
真正有意义的是：

移除：B
新增：D
而不是简单打印：

[A,B,C] → [A,C,D]。
所以后来我们的原则很简单。

基础框架负责：

操作人
请求信息
耗时
成功/失败
时间
业务 ID。
真正的业务变化：

由业务自己描述。
这样虽然不能做到：

加一个注解什么都自动完成
但日志质量高很多。

我觉得这是值得的。

现在再写后台接口，我一般会先判断：

这个操作以后有没有可能有人问：

谁改的？
如果有，那一开始就把操作日志考虑进去。

因为上线半年以后再补，通常很痛苦。

历史数据没有。

以前谁改过也不知道。

只能翻：

Nginx 日志
应用日志
数据库 binlog
一点点猜。

尤其是：

订单状态

用户权限

商品价格

财务配置

系统参数

审核结果
这些关键数据。

它们不应该只有：

现在是什么。
最好还能回答：

它是怎么一步一步变成现在这样的。

这次改造以后，业务 Service 里少掉了大量：

operationLogService.save(...)。
但真正的价值其实不只是代码变干净。

而是所有关键业务操作终于有了一套统一规则。

以后新增一个接口，开发人员只需要考虑：

@OperationLog(
    module = "...",
    action = "...",
    bizId = "...",
    detail = "..."
)
剩下的：

谁操作
什么时候
从哪里请求
执行多久
成功还是失败
全部统一完成。