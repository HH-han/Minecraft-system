# Spring Boot 3 使用 Druid 数据库连接池配置方案

## 1. 为什么选择 Druid？

Druid 是阿里巴巴开源的高性能数据库连接池，具备以下核心优势：

| 能力维度 | 说明 |
|---------|------|
| **SQL 监控** | 实时记录每条 SQL 的执行次数、耗时、事务状态 |
| **慢查询检测** | 自动标记超过阈值的慢 SQL，便于性能优化 |
| **SQL 防火墙** | 拦截 SQL 注入、批量删除等高危操作 |
| **连接池管理** | 高性能连接复用，支持 PSCache、连接泄漏检测 |
| **Web 关联分析** | 将 SQL 与 HTTP 请求 URI 关联，定位性能瓶颈 |

> Druid 的真正价值不仅在于连接池管理，更在于其强大的**可观测性**和**安全防护**能力。

---

## 2. 环境要求

| 组件 | 版本要求 |
|------|---------|
| Spring Boot | 3.x |
| JDK | 17+ |
| Druid | ≥ 1.2.16（推荐 1.2.20+） |
| Servlet API | Jakarta EE 9+ |

> ⚠️ **关键提示**：Spring Boot 3 基于 Jakarta EE，Servlet 包名从 `javax.servlet` 变为 `jakarta.servlet`。必须使用 `druid-spring-boot-3-starter`（版本 ≥ 1.2.16），否则监控页面无法访问。

---

## 3. 依赖配置

在 `pom.xml` 中添加依赖：

```xml
<!-- Spring Boot 3 专用 Druid Starter（必须使用此依赖） -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-3-starter</artifactId>
    <version>1.2.24</version>
</dependency>

<!-- 数据库驱动（以 MySQL 为例） -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- JDBC 依赖（通常已包含在 MyBatis/JPA 中） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

> 📌 如果使用 Gradle：
> ```gradle
> implementation 'com.alibaba:druid-spring-boot-3-starter:1.2.24'
> ```

---

## 4. 基础配置（application.yml）

```yaml
spring:
  datasource:
    # 指定 Druid 数据源类型（可选，starter 会自动配置）
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_database?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: your_username
    password: your_password
    
    # Druid 连接池专属配置
    druid:
      # ========== 连接池基础参数 ==========
      initial-size: 5                    # 初始化连接数
      min-idle: 5                        # 最小空闲连接数
      max-active: 20                     # 最大活跃连接数
      max-wait: 60000                    # 获取连接超时时间（毫秒）
      
      # ========== 连接检测与回收 ==========
      time-between-eviction-runs-millis: 60000   # 检测间隔（毫秒）
      min-evictable-idle-time-millis: 300000    # 连接最小生存时间（毫秒）
      validation-query: SELECT 1                # 验证 SQL
      test-while-idle: true                     # 空闲时检测
      test-on-borrow: false                     # 借出时检测（影响性能）
      test-on-return: false                     # 归还时检测
      
      # ========== PSCache 配置（MySQL 建议关闭） ==========
      pool-prepared-statements: false
      max-pool-prepared-statement-per-connection-size: -1
      
      # ========== 监控与过滤器 ==========
      filters: stat,wall,slf4j           # 启用监控统计、防火墙、日志
      
      # ========== 慢 SQL 记录 ==========
      connect-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=2000
```

---

## 5. 监控面板配置

在 `application.yml` 中继续添加监控相关配置：

```yaml
spring:
  datasource:
    druid:
      # ========== SQL 监控（StatFilter） ==========
      filter:
        stat:
          enabled: true
          db-type: mysql
          log-slow-sql: true              # 记录慢 SQL
          slow-sql-millis: 2000           # 慢 SQL 阈值（毫秒）
          
        # ========== SQL 防火墙（WallFilter） ==========
        wall:
          enabled: true
          db-type: mysql
          config:
            delete-allow: true             # 是否允许 DELETE 操作
            drop-table-allow: false        # 是否允许 DROP TABLE（生产环境建议 false）
            truncate-allow: false          # 是否允许 TRUNCATE
            
      # ========== Web 请求监控（URI 监控） ==========
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: /druid/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico
        session-stat-enable: true          # 启用 Session 监控
        session-stat-max-count: 1000
        
      # ========== 监控面板（StatViewServlet） ==========
      stat-view-servlet:
        enabled: true                       # 必须显式开启
        url-pattern: /druid/*               # 访问路径
        reset-enable: false                 # 禁止重置监控数据（安全）
        login-username: admin               # 登录用户名
        login-password: admin123            # 登录密码（生产环境使用环境变量）
        # IP 白名单（多个用逗号分隔）
        allow: 127.0.0.1,192.168.1.0/24
        # deny: 192.168.1.100               # IP 黑名单（优先级高于白名单）
```

> ⚠️ **安全提醒**：
> - 生产环境务必修改默认密码，建议使用环境变量 `${DRUID_PASSWORD}`
> - 设置 `reset-enable: false` 防止恶意清空监控日志
> - 通过 `allow` 限制访问 IP，避免监控面板暴露公网

---

## 6. 配置说明速查表

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `initial-size` | 0 | 启动时创建的物理连接数 |
| `min-idle` | 0 | 最小空闲连接数 |
| `max-active` | 8 | 最大活跃连接数 |
| `max-wait` | -1 | 获取连接最大等待时间（毫秒），-1 表示无限等待 |
| `time-between-eviction-runs-millis` | 60s | 空闲连接检测周期 |
| `min-evictable-idle-time-millis` | 30min | 连接空闲超过此时间被回收 |
| `validation-query` | 无 | 检测连接是否有效的 SQL |
| `test-while-idle` | false | 空闲时检测连接有效性 |
| `pool-prepared-statements` | false | 是否缓存 PreparedStatement（MySQL 建议关闭） |
| `filters` | 无 | 监控过滤器：`stat`(监控)、`wall`(防火墙)、`log4j2`(日志) |
| `slow-sql-millis` | 3000 | 慢 SQL 判定阈值（毫秒） |

---

## 7. 验证与测试

### 7.1 启动日志验证

启动应用后，出现以下日志表示 Druid 已成功初始化：

```
INFO  com.alibaba.druid.pool.DruidDataSource : {dataSource-1} inited
INFO  c.a.d.s.b.a.DruidDataSourceAutoConfigure : Init DruidDataSource
```

### 7.2 访问监控面板

启动应用后，访问：`http://localhost:8080/druid/login.html`

使用配置的用户名密码登录后，可查看：
- **数据源**：连接池状态、活跃连接数等
- **SQL 监控**：SQL 执行统计、慢 SQL 标红
- **URI 监控**：请求路径与 SQL 关联分析
- **防火墙**：被拦截的 SQL 记录

### 7.3 快速测试 Controller

```java
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/query")
    public List<Map<String, Object>> query() {
        // 执行后会出现在 Druid SQL 监控面板
        return jdbcTemplate.queryForList("SELECT * FROM your_table");
    }
    
    @GetMapping("/slow")
    public String slowQuery() {
        // 模拟慢查询（会被标记为红色）
        jdbcTemplate.execute("SELECT SLEEP(3)");
        return "slow query executed";
    }
}
```

---

## 8. 常见问题排查

| 问题现象 | 可能原因 | 解决方案 |
|---------|---------|---------|
| 监控页面 404 | 依赖版本错误 | 使用 `druid-spring-boot-3-starter` ≥ 1.2.16 |
| SQL 监控无数据 | `stat` 过滤器未启用 | 配置 `filters: stat` 并确保 `stat.enabled: true` |
| URI 监控无数据 | `web-stat-filter` 未启用 | 配置 `web-stat-filter.enabled: true` |
| 慢 SQL 未标红 | 阈值未生效 | 检查 `slow-sql-millis` 配置是否正确 |
| 防火墙不拦截 | 测试语句语法非法 | 使用语法合法但危险的 SQL（如 `DROP TABLE IF EXISTS`） |
| 密码明文暴露 | 配置硬编码 | 使用环境变量：`login-password: ${DRUID_PWD}` |

> 🔧 **端口问题**：如果监控页面无法访问，尝试将 Druid 版本降到 1.2.20，启动成功后再升级回新版。

---

## 9. 生产环境安全建议

```yaml
# 推荐的生产环境配置
spring:
  datasource:
    druid:
      stat-view-servlet:
        enabled: true
        reset-enable: false                    # 禁止重置
        login-username: ${DRUID_USER:admin}
        login-password: ${DRUID_PASSWORD}      # 从环境变量读取
        allow: 10.0.0.0/8,172.16.0.0/12       # 仅允许内网访问
        
      filter:
        wall:
          enabled: true
          config:
            delete-where-none-check: true      # 禁止无 WHERE 条件的 DELETE
            drop-table-allow: false            # 禁止删表
            truncate-allow: false              # 禁止清空表
```

---

## 10. 总结

Spring Boot 3 集成 Druid 的核心要点：

1. **依赖**：必须使用 `druid-spring-boot-3-starter`，版本 ≥ 1.2.16
2. **监控**：`stat-view-servlet.enabled: true` 必须显式开启
3. **安全**：设置强密码、IP 白名单、禁用重置按钮
4. **验证**：访问 `/druid/login.html` 确认配置生效

按照本文档配置，你将获得一个具备 SQL 监控、慢查询分析、安全防护能力的数据库连接池。