# 数据库连接错误诊断报告

## 错误信息

### 主要错误
```
java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed
```

### 根本原因
```
com.mysql.cj.exceptions.UnableToConnectException: Public Key Retrieval is not allowed
```

### 完整错误堆栈
```
2026-02-13 01:24:09 [http-nio-8080-exec-3] ERROR com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Exception during pool initialization.
java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:111)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:825)
	...
Caused by: com.mysql.cj.exceptions.UnableToConnectException: Public Key Retrieval is not allowed
	at java.base/jdk.internal.reflect.DirectConstructorHandleAccessor.newInstance(DirectConstructorHandleAccessor.java:62)
	at java.base/jdk.reflect.Constructor.newInstance(Constructor.java:486)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:62)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:86)
	at com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin.nextAuthenticationStep(CachingSha2PasswordPlugin.java:130)
	at com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin.nextAuthenticationStep(CachingSha2PasswordPlugin.java:49)
	at com.mysql.cj.protocol.a.NativeAuthenticationProvider.proceedHandshakeWithPluggableAuthentication(NativeAuthenticationProvider.java:443)
	at com.mysql.cj.protocol.a.NativeAuthenticationProvider.connect(NativeAuthenticationProvider.java:213)
	at com.mysql.cj.protocol.a.NativeProtocol.connect(NativeProtocol.java:1430)
	at com.mysql.cj.NativeSession.connect(NativeSession.java:134)
	at com.mysql.cj.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:945)
	at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:815)
	... 141 common frames omitted
```

## 问题分析

### 1. 错误发生的位置
- **Controller**: `AttractionController.getAttractionList()` (Line 27)
- **Service**: `AttractionServiceImpl.getAttractionList()` (Line 49)
- **操作**: `BaseMapper.selectPage()` - 分页查询景点列表

### 2. 错误原因分析

#### MySQL 8.0+ 认证机制
MySQL 8.0+ 默认使用 `caching_sha2_password` 认证插件，这个插件需要公钥检索才能完成认证过程。

#### 配置检查
当前 `application.yml` 配置：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/db_minecraft?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
    username: root
    password: 123456
```

**配置已包含 `allowPublicKeyRetrieval=true`，但仍然报错。**

### 3. 可能的原因

#### 原因 1: MySQL 服务未启动 ⭐⭐⭐⭐⭐
**最可能的原因**：MySQL 服务没有运行。

**检查方法**：
```bash
# Windows
net start mysql

# 或者检查服务
sc query mysql
```

#### 原因 2: MySQL 用户密码不正确 ⭐⭐⭐⭐
MySQL 用户 `root` 的密码可能不是 `123456`。

**检查方法**：
```bash
mysql -uroot -p
```

#### 原因 3: MySQL 数据库未创建 ⭐⭐⭐
数据库 `db_minecraft` 可能没有创建。

**检查方法**：
```bash
mysql -uroot -p -e "SHOW DATABASES;"
```

#### 原因 4: MySQL 端口不正确 ⭐⭐
MySQL 可能没有运行在默认端口 3306。

**检查方法**：
```bash
mysql -uroot -p -e "SHOW VARIABLES LIKE 'port';"
```

#### 原因 5: MySQL 配置问题 ⭐
MySQL 配置可能禁用了公钥检索。

**检查方法**：
```bash
mysql -uroot -p -e "SHOW VARIABLES LIKE 'sha256_password_public_key_path';"
```

## 解决方案

### 方案 1: 检查 MySQL 服务状态 ⭐⭐⭐⭐⭐

#### Windows 系统
```bash
# 检查 MySQL 服务状态
sc query mysql

# 如果服务未运行，启动 MySQL 服务
net start mysql

# 如果服务名称不是 mysql，尝试其他常见名称
net start mysql80
net start MySQL80
net start MySQL57
```

#### 查看所有 MySQL 相关服务
```bash
sc query | findstr /i mysql
```

### 方案 2: 验证 MySQL 连接 ⭐⭐⭐⭐

#### 使用命令行测试连接
```bash
mysql -uroot -p123456 -e "SELECT 1;"
```

#### 如果密码错误，修改密码
```bash
# 登录 MySQL
mysql -uroot -p

# 修改密码
ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
FLUSH PRIVILEGES;
```

### 方案 3: 创建数据库 ⭐⭐⭐

#### 执行 SQL 脚本
```bash
mysql -uroot -p123456 < src/main/resources/sql/db_minecraft.sql
```

#### 或者在 MySQL 命令行中执行
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS db_minecraft DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE db_minecraft;

-- 检查数据库是否创建成功
SHOW DATABASES;
```

### 方案 4: 修改 MySQL 用户认证方式 ⭐⭐

#### 将 root 用户改为使用 mysql_native_password
```sql
-- 登录 MySQL
mysql -uroot -p

-- 修改认证方式
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456';
FLUSH PRIVILEGES;

-- 验证修改
SELECT user, host, plugin FROM mysql.user WHERE user = 'root';
```

### 方案 5: 检查 MySQL 配置文件 ⭐

#### 查看 MySQL 配置文件
**Windows**: `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`

#### 添加或修改配置
```ini
[mysqld]
default_authentication_plugin=mysql_native_password
```

#### 重启 MySQL 服务
```bash
net stop mysql
net start mysql
```

### 方案 6: 使用不同的 JDBC URL 配置 ⭐

#### 修改 application.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_minecraft?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true&useSSL=false
```

#### 或者使用 SSL
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_minecraft?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
```

## 诊断步骤

### 步骤 1: 检查 MySQL 服务
```bash
# 检查 MySQL 服务状态
sc query mysql

# 如果服务未运行，启动服务
net start mysql
```

### 步骤 2: 测试 MySQL 连接
```bash
# 测试连接
mysql -uroot -p123456 -e "SELECT 1;"

# 如果连接失败，尝试不使用密码
mysql -uroot -e "SELECT 1;"
```

### 步骤 3: 检查数据库是否存在
```bash
mysql -uroot -p123456 -e "SHOW DATABASES;"
```

### 步骤 4: 创建数据库（如果不存在）
```bash
mysql -uroot -p123456 < src/main/resources/sql/db_minecraft.sql
```

### 步骤 5: 重启应用
```bash
# 停止应用
# 然后重新启动应用
```

## 验证修复

### 1. 检查应用日志
应用启动成功后，应该看到：
```
2026-02-13 01:27:26 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8080 (http) with context path ''
2026-02-13 01:27:26 [main] INFO  com.minecraft.MinecraftApplication - Started MinecraftApplication in 4.325 seconds
```

### 2. 测试数据库连接
```bash
# 访问景点列表接口
curl http://localhost:8080/api/attraction/list
```

### 3. 预期结果
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 0,
    "size": 10,
    "current": 1,
    "pages": 0
  }
}
```

## 常见问题

### Q1: 为什么配置了 allowPublicKeyRetrieval=true 还是报错？
**A**: 这个参数只是允许公钥检索，但前提是：
1. MySQL 服务必须运行
2. 用户名和密码必须正确
3. 数据库必须存在
4. 网络连接必须正常

### Q2: 如何查看 MySQL 服务名称？
**A**: 使用以下命令：
```bash
sc query | findstr /i mysql
```

### Q3: 如何重置 MySQL root 密码？
**A**:
```bash
# 停止 MySQL 服务
net stop mysql

# 以安全模式启动 MySQL
mysqld --skip-grant-tables

# 在另一个窗口中，登录并修改密码
mysql -uroot
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
FLUSH PRIVILEGES;
EXIT;

# 重启 MySQL 服务
net start mysql
```

### Q4: 如何查看 MySQL 版本？
**A**:
```bash
mysql --version
```

### Q5: MySQL 8.0+ 和 5.7 有什么区别？
**A**:
- MySQL 8.0+ 默认使用 `caching_sha2_password` 认证插件
- MySQL 5.7 默认使用 `mysql_native_password` 认证插件
- `caching_sha2_password` 更安全，但需要配置 `allowPublicKeyRetrieval=true`

## 总结

**最可能的原因是 MySQL 服务未启动。**

**建议的解决步骤**：
1. 检查 MySQL 服务状态：`sc query mysql`
2. 启动 MySQL 服务：`net start mysql`
3. 测试 MySQL 连接：`mysql -uroot -p123456 -e "SELECT 1;"`
4. 创建数据库：`mysql -uroot -p123456 < src/main/resources/sql/db_minecraft.sql`
5. 重启应用

## 相关文件

- [application.yml](file:///d:\IDEA\work\Minecraft\src\main\resources\application.yml) - 数据库配置
- [db_minecraft.sql](file:///d:\IDEA\work\Minecraft\src\main\resources\sql\db_minecraft.sql) - 数据库脚本
- [AttractionController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\AttractionController.java) - 发生错误的 Controller
- [AttractionServiceImpl.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\service\impl\AttractionServiceImpl.java) - 发生错误的 Service