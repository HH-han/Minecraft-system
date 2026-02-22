# MySQL数据库连接问题解决方案

## 问题描述
错误信息：`java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed`

## 解决步骤

### 步骤1：确认MySQL服务是否运行

#### Windows系统：
1. 按 `Win + R`，输入 `services.msc`，回车
2. 在服务列表中找到 `MySQL` 或 `MySQL80`（版本号可能不同）
3. 确认状态为"正在运行"
4. 如果未运行，右键点击选择"启动"

#### 或者使用命令行检查：
```powershell
# 检查MySQL服务状态
Get-Service -Name MySQL*
```

### 步骤2：创建数据库和表

#### 方法1：使用初始化脚本（推荐）
双击运行项目根目录下的 `init-database.bat` 文件，输入MySQL root密码即可。

#### 方法2：使用MySQL命令行
```bash
# 进入MySQL命令行
mysql -u root -p

# 然后执行以下SQL命令
source d:\IDEA\work\Minecraft\src\main\resources\sql\tourism.sql;
```

#### 方法3：使用MySQL客户端工具
使用Navicat、MySQL Workbench等工具：
1. 连接到MySQL服务器
2. 打开文件：`d:\IDEA\work\Minecraft\src\main\resources\sql\tourism.sql`
3. 执行SQL脚本

### 步骤3：修改application.yml配置

如果MySQL密码不是`root`，请修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tourism?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
    username: root
    password: 您的实际密码  # 修改这里
```

### 步骤4：验证数据库连接

#### 方法1：使用MySQL命令行
```bash
mysql -u root -p tourism
```

#### 方法2：使用Java测试
运行 `MinecraftApplicationTests.java` 中的测试方法。

## 常见问题

### 问题1：MySQL命令未找到
**原因**：MySQL未安装或未添加到系统PATH

**解决方法**：
1. 确认MySQL已安装
2. 添加MySQL bin目录到系统PATH
   - 默认路径：`C:\Program Files\MySQL\MySQL Server 8.0\bin`
3. 或使用MySQL客户端工具

### 问题2：密码错误
**解决方法**：
1. 重置MySQL root密码
2. 或修改application.yml中的密码为正确的密码

### 问题3：端口被占用
**解决方法**：
1. 检查3306端口是否被占用：
   ```powershell
   netstat -ano | findstr :3306
   ```
2. 如果被占用，可以修改MySQL端口或application.yml中的端口

### 问题4：数据库不存在
**解决方法**：
执行步骤2中的SQL脚本创建数据库

## 数据库配置说明

### application.yml配置项：
- `url`: 数据库连接URL
  - `localhost:3306`: MySQL服务器地址和端口
  - `tourism`: 数据库名称
  - `allowPublicKeyRetrieval=true`: 允许公钥检索（解决此错误的关键）
  - `useSSL=false`: 不使用SSL连接
  - `serverTimezone=Asia/Shanghai`: 设置时区
- `username`: MySQL用户名
- `password`: MySQL密码

### HikariCP连接池配置：
```yaml
hikari:
  minimum-idle: 5              # 最小空闲连接数
  maximum-pool-size: 20        # 最大连接池大小
  idle-timeout: 30000          # 空闲连接超时时间（毫秒）
  connection-timeout: 30000    # 连接超时时间（毫秒）
  connection-test-query: SELECT 1  # 连接测试查询
  validation-timeout: 5000     # 验证超时时间（毫秒）
  max-lifetime: 1800000        # 连接最大存活时间（毫秒）
```

## 验证连接成功

启动应用后，看到以下日志表示连接成功：
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

访问 http://localhost:8080/swagger-ui.html 查看API文档。

## 联系支持

如果以上方法都无法解决问题，请提供：
1. MySQL版本
2. 错误日志完整信息
3. application.yml配置（隐藏密码）
4. MySQL服务状态