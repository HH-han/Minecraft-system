# 500 错误修复说明

## 问题描述

访问 `http://localhost:8080/api/user/info` 时返回 500 错误：
```json
{
   "code": 500,
   "message": "系统异常：null",
   "data": null
}
```

## 问题原因

### 根本原因
`userId` 为 `null` 导致 `getById(null)` 抛出异常。

### 详细分析

1. **JwtAuthenticationFilter** 只设置了 `SecurityContextHolder`：
```java
SecurityContextHolder.getContext().setAuthentication(authentication);
```

2. **Controller** 从 `request.getAttribute("userId")` 获取用户 ID：
```java
Long userId = (Long) request.getAttribute("userId");
```

3. **问题**：`request.getAttribute("userId")` 返回 `null`，因为 Filter 没有设置这个属性

4. **后果**：调用 `getById(null)` 时抛出异常

## 解决方案

### 1. 创建 SecurityUtils 工具类

创建了 [SecurityUtils.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\utils\SecurityUtils.java) 工具类：

```java
package com.minecraft.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return Long.valueOf(authentication.getName());
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
```

### 2. 修改所有 Controller

将所有 Controller 中的：
```java
Long userId = (Long) request.getAttribute("userId");
```

改为：
```java
Long userId = SecurityUtils.getCurrentUserId();
```

### 3. 移除 HttpServletRequest 参数

移除了方法签名中的 `HttpServletRequest request` 参数，简化了代码。

## 修改的文件列表

### 新增文件
- [SecurityUtils.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\utils\SecurityUtils.java)

### 修改的 Controller 文件

1. [UserController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\UserController.java)
   - `getUserInfo()` - 移除 HttpServletRequest 参数
   - `updatePassword()` - 移除 HttpServletRequest 参数

2. [FriendController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\FriendController.java)
   - `addFriend()` - 使用 SecurityUtils
   - `acceptFriend()` - 使用 SecurityUtils
   - `deleteFriend()` - 使用 SecurityUtils
   - `getFriendList()` - 使用 SecurityUtils
   - `getFriendRequestList()` - 使用 SecurityUtils

3. [CommentController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\CommentController.java)
   - `addComment()` - 使用 SecurityUtils

4. [CartController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\CartController.java)
   - `addToCart()` - 使用 SecurityUtils
   - `clearCart()` - 使用 SecurityUtils
   - `getCartList()` - 使用 SecurityUtils

5. [CollectionController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\CollectionController.java)
   - `collect()` - 使用 SecurityUtils
   - `uncollect()` - 使用 SecurityUtils
   - `isCollected()` - 使用 SecurityUtils

6. [GroupTravelController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\GroupTravelController.java)
   - `joinGroup()` - 使用 SecurityUtils
   - `leaveGroup()` - 使用 SecurityUtils

7. [PaymentController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\PaymentController.java)
   - `createPayment()` - 使用 SecurityUtils

8. [LikeController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\LikeController.java)
   - `like()` - 使用 SecurityUtils
   - `unlike()` - 使用 SecurityUtils
   - `isLiked()` - 使用 SecurityUtils

9. [CommunityController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\CommunityController.java)
   - `getPostDetail()` - 使用 SecurityUtils

10. [OrderController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\OrderController.java)
    - `createOrder()` - 使用 SecurityUtils
    - `getOrderList()` - 使用 SecurityUtils

11. [ChatController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\ChatController.java)
    - `sendMessage()` - 使用 SecurityUtils
    - `getChatHistory()` - 使用 SecurityUtils
    - `markAsRead()` - 使用 SecurityUtils

12. [AttractionController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\AttractionController.java)
    - `getAttractionDetail()` - 使用 SecurityUtils

13. [TravelController.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\controller\TravelController.java)
    - `getTravelPlanList()` - 使用 SecurityUtils

## 使用示例

### 修改前
```java
@GetMapping("/info")
public ApiResponse<User> getUserInfo(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    return ApiResponse.success(userService.getUserInfo(userId));
}
```

### 修改后
```java
@GetMapping("/info")
public ApiResponse<User> getUserInfo() {
    Long userId = SecurityUtils.getCurrentUserId();
    return ApiResponse.success(userService.getUserInfo(userId));
}
```

## 优势

1. **统一管理**：所有获取当前用户 ID 的逻辑都通过 SecurityUtils 统一管理
2. **代码简洁**：移除了不必要的 HttpServletRequest 参数
3. **类型安全**：使用 Spring Security 的标准方式获取认证信息
4. **易于维护**：如果需要修改获取用户 ID 的逻辑，只需修改 SecurityUtils
5. **避免空指针**：SecurityUtils 提供了空值检查

## 测试

### 测试步骤

1. **登录获取 Token**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "test001",
  "password": "123456"
}
```

2. **使用 Token 访问需要认证的接口**
```bash
GET http://localhost:8080/api/user/info
Authorization: Bearer {token}
```

3. **预期结果**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "test001",
    "email": "test1@example.com",
    ...
  }
}
```

## 注意事项

1. **Token 格式**：必须在请求头中添加 `Authorization: Bearer {token}`
2. **Token 有效性**：Token 有过期时间，过期后需要重新登录
3. **未认证访问**：如果没有提供有效的 Token，`SecurityUtils.getCurrentUserId()` 会返回 `null`
4. **数据库连接**：确保数据库已创建并连接正常

## 相关文件

- [JwtAuthenticationFilter.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\handler\JwtAuthenticationFilter.java) - JWT 认证过滤器
- [SecurityConfig.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\config\SecurityConfig.java) - Spring Security 配置
- [JwtUtil.java](file:///d:\IDEA\work\Minecraft\src\main\java\com\minecraft\utils\JwtUtil.java) - JWT 工具类