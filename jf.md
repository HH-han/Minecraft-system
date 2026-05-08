我将为您创建一个完整的购物积分兑换系统，包含前后端代码。

## 项目结构

```
points-exchange-system/
├── backend/                 # SpringBoot后端
└── frontend/               # Vue3前端
```

## 后端代码

### 1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>points-exchange</artifactId>
    <version>1.0.0</version>
    <name>points-exchange-system</name>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.5</version>
        </dependency>
        
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-generator</artifactId>
            <version>3.5.5</version>
        </dependency>
        
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.23</version>
        </dependency>
    </dependencies>
</project>
```

### 2. application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/points_exchange?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    timeout: 5000ms
  
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

server:
  port: 8080

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: points-exchange-secret-key-2024
  expiration: 86400000
```

### 3. 实体类

```java
// User.java
package com.example.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Integer points;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

// Product.java
package com.example.points.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Integer pointsPrice;
    private Integer stock;
    private String imageUrl;
    private Integer status; // 0:下架 1:上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

// ExchangeOrder.java
package com.example.points.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exchange_order")
public class ExchangeOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer pointsUsed;
    private Integer quantity;
    private String status; // PENDING, SUCCESS, FAILED, CANCELLED
    private String address;
    private String phone;
    private String receiver;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

// PointsRecord.java
package com.example.points.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("points_record")
public class PointsRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer points;
    private Integer beforePoints;
    private Integer afterPoints;
    private String type; // INCOME, EXCHANGE, REFUND
    private String remark;
    private LocalDateTime createTime;
}
```

### 4. Mapper接口

```java
// UserMapper.java
package com.example.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.points.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Update("UPDATE user SET points = points + #{points} WHERE id = #{userId}")
    int addPoints(Long userId, Integer points);
    
    @Update("UPDATE user SET points = points - #{points} WHERE id = #{userId} AND points >= #{points}")
    int deductPoints(Long userId, Integer points);
}

// ProductMapper.java
package com.example.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.points.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int deductStock(Long productId, Integer quantity);
}

// ExchangeOrderMapper.java
package com.example.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.points.entity.ExchangeOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExchangeOrderMapper extends BaseMapper<ExchangeOrder> {
}

// PointsRecordMapper.java
package com.example.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.points.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {
}
```

### 5. Service层

```java
// UserService.java
package com.example.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.points.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
    boolean addPoints(Long userId, Integer points, String remark);
    boolean deductPoints(Long userId, Integer points, String remark);
}

// UserServiceImpl.java
package com.example.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.points.entity.PointsRecord;
import com.example.points.entity.User;
import com.example.points.mapper.PointsRecordMapper;
import com.example.points.mapper.UserMapper;
import com.example.points.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final UserMapper userMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            User result = new User();
            BeanUtils.copyProperties(user, result);
            result.setPassword(null);
            return result;
        }
        return null;
    }
    
    @Override
    @Transactional
    public boolean addPoints(Long userId, Integer points, String remark) {
        int result = userMapper.addPoints(userId, points);
        if (result > 0) {
            User user = userMapper.selectById(userId);
            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setPoints(points);
            record.setBeforePoints(user.getPoints() - points);
            record.setAfterPoints(user.getPoints());
            record.setType("INCOME");
            record.setRemark(remark);
            pointsRecordMapper.insert(record);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean deductPoints(Long userId, Integer points, String remark) {
        int result = userMapper.deductPoints(userId, points);
        if (result > 0) {
            User user = userMapper.selectById(userId);
            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setPoints(points);
            record.setBeforePoints(user.getPoints() + points);
            record.setAfterPoints(user.getPoints());
            record.setType("EXCHANGE");
            record.setRemark(remark);
            pointsRecordMapper.insert(record);
            return true;
        }
        return false;
    }
}

// ExchangeService.java
package com.example.points.service;

import com.example.points.entity.ExchangeOrder;

public interface ExchangeService {
    ExchangeOrder exchangeProduct(Long userId, Long productId, Integer quantity, 
                                  String address, String phone, String receiver);
    void processOrderStatus(String orderNo, String status);
}

// ExchangeServiceImpl.java
package com.example.points.service.impl;

import cn.hutool.core.util.IdUtil;
import com.example.points.entity.ExchangeOrder;
import com.example.points.entity.Product;
import com.example.points.entity.User;
import com.example.points.mapper.ExchangeOrderMapper;
import com.example.points.mapper.ProductMapper;
import com.example.points.mapper.UserMapper;
import com.example.points.service.ExchangeService;
import com.example.points.service.UserService;
import com.example.points.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final ExchangeOrderMapper exchangeOrderMapper;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebSocketServer webSocketServer;
    
    @Override
    @Transactional
    public ExchangeOrder exchangeProduct(Long userId, Long productId, Integer quantity,
                                        String address, String phone, String receiver) {
        // 加锁防止并发
        String lockKey = "exchange:lock:" + userId + ":" + productId;
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(lock)) {
            throw new RuntimeException("操作太过频繁，请稍后再试");
        }
        
        try {
            // 获取商品信息
            Product product = productMapper.selectById(productId);
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品不存在或已下架");
            }
            
            // 检查库存
            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            
            // 计算所需积分
            int totalPoints = product.getPointsPrice() * quantity;
            
            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user.getPoints() < totalPoints) {
                throw new RuntimeException("积分不足");
            }
            
            // 生成订单号
            String orderNo = IdUtil.fastSimpleUUID();
            
            // 创建订单
            ExchangeOrder order = new ExchangeOrder();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setProductId(productId);
            order.setProductName(product.getName());
            order.setPointsUsed(totalPoints);
            order.setQuantity(quantity);
            order.setStatus("PENDING");
            order.setAddress(address);
            order.setPhone(phone);
            order.setReceiver(receiver);
            order.setCreateTime(LocalDateTime.now());
            exchangeOrderMapper.insert(order);
            
            // 扣减积分
            boolean deductPoints = userService.deductPoints(userId, totalPoints, 
                "兑换商品：" + product.getName() + " x" + quantity);
            
            if (!deductPoints) {
                order.setStatus("FAILED");
                exchangeOrderMapper.updateById(order);
                throw new RuntimeException("积分扣减失败");
            }
            
            // 扣减库存
            int deductStock = productMapper.deductStock(productId, quantity);
            if (deductStock <= 0) {
                // 回滚积分
                userService.addPoints(userId, totalPoints, "库存不足，退回积分");
                order.setStatus("FAILED");
                exchangeOrderMapper.updateById(order);
                throw new RuntimeException("扣减库存失败");
            }
            
            // 更新订单状态
            order.setStatus("SUCCESS");
            order.setUpdateTime(LocalDateTime.now());
            exchangeOrderMapper.updateById(order);
            
            // 发送WebSocket通知
            webSocketServer.sendMessageToUser(userId.toString(), 
                "您已成功兑换商品：" + product.getName() + "，积分扣除：" + totalPoints);
            
            return order;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
    
    @Override
    public void processOrderStatus(String orderNo, String status) {
        ExchangeOrder order = exchangeOrderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExchangeOrder>()
                .eq(ExchangeOrder::getOrderNo, orderNo));
        
        if (order != null) {
            order.setStatus(status);
            order.setUpdateTime(LocalDateTime.now());
            exchangeOrderMapper.updateById(order);
            
            webSocketServer.sendMessageToUser(order.getUserId().toString(), 
                "订单：" + orderNo + " 状态更新为：" + status);
        }
    }
}
```

### 6. WebSocket配置

```java
// WebSocketConfig.java
package com.example.points.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

// WebSocketServer.java
package com.example.points.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {
    
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessionMap.put(userId, session);
        log.info("WebSocket连接建立: userId={}", userId);
    }
    
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        sessionMap.remove(userId);
        log.info("WebSocket连接关闭: userId={}", userId);
    }
    
    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") String userId) {
        log.error("WebSocket错误: userId={}", userId, error);
        sessionMap.remove(userId);
    }
    
    public void sendMessageToUser(String userId, String message) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }
    
    public void sendMessageToAll(String message) {
        sessionMap.forEach((userId, session) -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }
}
```

### 7. Controller层

```java
// AuthController.java
package com.example.points.controller;

import com.example.points.entity.User;
import com.example.points.service.UserService;
import com.example.points.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        User user = userService.login(username, password);
        if (user != null) {
            String token = jwtUtil.generateToken(user.getId().toString(), user.getUsername());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            return result;
        }
        throw new RuntimeException("用户名或密码错误");
    }
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
            .encode(user.getPassword()));
        user.setPoints(0);
        userService.save(user);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "注册成功");
        return result;
    }
}

// ProductController.java
package com.example.points.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.points.entity.Product;
import com.example.points.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductMapper productMapper;
    
    @GetMapping
    public Page<Product> list(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(pageParam, wrapper);
    }
    
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productMapper.selectById(id);
    }
}

// ExchangeController.java
package com.example.points.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.points.entity.ExchangeOrder;
import com.example.points.service.ExchangeService;
import com.example.points.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {
    
    private final ExchangeService exchangeService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/product")
    public ExchangeOrder exchange(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(token));
        
        Long productId = Long.parseLong(params.get("productId").toString());
        Integer quantity = Integer.parseInt(params.get("quantity").toString());
        String address = params.get("address").toString();
        String phone = params.get("phone").toString();
        String receiver = params.get("receiver").toString();
        
        return exchangeService.exchangeProduct(userId, productId, quantity, address, phone, receiver);
    }
    
    @GetMapping("/orders")
    public Page<ExchangeOrder> getOrders(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(token));
        
        Page<ExchangeOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ExchangeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExchangeOrder::getUserId, userId);
        wrapper.orderByDesc(ExchangeOrder::getCreateTime);
        
        return exchangeService.page(pageParam, wrapper);
    }
}

// UserInfoController.java
package com.example.points.controller;

import com.example.points.entity.PointsRecord;
import com.example.points.entity.User;
import com.example.points.mapper.PointsRecordMapper;
import com.example.points.mapper.UserMapper;
import com.example.points.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {
    
    private final UserMapper userMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final JwtUtil jwtUtil;
    
    @GetMapping("/info")
    public User getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(token));
        return userMapper.selectById(userId);
    }
    
    @GetMapping("/points/records")
    public List<PointsRecord> getPointsRecords(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(token));
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PointsRecord> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        wrapper.orderByDesc(PointsRecord::getCreateTime);
        wrapper.last("limit 50");
        
        return pointsRecordMapper.selectList(wrapper);
    }
}
```

## 前端代码 (Vue3)

### 1. package.json

```json
{
  "name": "points-exchange-frontend",
  "version": "1.0.0",
  "scripts": {
    "dev": "vite",
    "build": "vite build"
  },
  "dependencies": {
    "vue": "^3.3.4",
    "vue-router": "^4.2.4",
    "axios": "^1.5.0",
    "element-plus": "^2.3.12",
    "pinia": "^2.1.6",
    "@vueuse/core": "^10.4.1"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^4.3.4",
    "vite": "^4.4.9"
  }
}
```

### 2. main.js

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
```

### 3. App.vue

```vue
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from './stores/user'

const userStore = useUserStore()

onMounted(() => {
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (token) {
    userStore.token = token
    userStore.getUserInfo()
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #f5f5f5;
}

#app {
  min-height: 100vh;
}
</style>
```

### 4. 路由配置 router/index.js

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: '/products',
        name: 'Products',
        component: () => import('../views/Products.vue')
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue')
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
```

### 5. 状态管理 stores/user.js

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const points = ref(0)
  
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
    axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`
  }
  
  const getUserInfo = async () => {
    try {
      const res = await axios.get('/api/user/info')
      userInfo.value = res.data
      points.value = res.data.points
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }
  
  const logout = () => {
    token.value = ''
    userInfo.value = null
    points.value = 0
    localStorage.removeItem('token')
    delete axios.defaults.headers.common['Authorization']
    window.location.href = '/login'
  }
  
  return {
    token,
    userInfo,
    points,
    setToken,
    getUserInfo,
    logout
  }
})
```

### 6. 主要页面

```vue
<!-- views/Home.vue -->
<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="6" v-for="product in products" :key="product.id">
        <el-card class="product-card" :body-style="{ padding: '0px' }">
          <img :src="product.imageUrl || 'https://via.placeholder.com/300x200'" class="product-image">
          <div style="padding: 14px;">
            <h3>{{ product.name }}</h3>
            <p class="points-price">{{ product.pointsPrice }} 积分</p>
            <p class="stock">库存: {{ product.stock }}</p>
            <el-button type="primary" size="small" @click="exchangeProduct(product)">
              立即兑换
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 兑换弹窗 -->
    <el-dialog v-model="dialogVisible" title="兑换商品" width="500px">
      <el-form :model="exchangeForm" :rules="exchangeRules" ref="exchangeFormRef">
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="exchangeForm.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="exchangeForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="exchangeForm.address" type="textarea" placeholder="请输入收货地址" />
        </el-form-item>
        <el-form-item label="兑换数量" prop="quantity">
          <el-input-number v-model="exchangeForm.quantity" :min="1" :max="currentProduct?.stock" />
        </el-form-item>
        <el-form-item label="所需积分">
          <span style="color: #ff6b6b; font-size: 18px;">
            {{ (currentProduct?.pointsPrice || 0) * exchangeForm.quantity }} 积分
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitExchange">确认兑换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const products = ref([])
const dialogVisible = ref(false)
const currentProduct = ref(null)
const exchangeFormRef = ref(null)

const exchangeForm = ref({
  receiver: '',
  phone: '',
  address: '',
  quantity: 1
})

const exchangeRules = {
  receiver: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const loadProducts = async () => {
  const res = await axios.get('/api/products')
  products.value = res.data.records
}

const exchangeProduct = (product) => {
  currentProduct.value = product
  exchangeForm.value.quantity = 1
  dialogVisible.value = true
}

const submitExchange = async () => {
  if (!exchangeFormRef.value) return
  
  await exchangeFormRef.value.validate()
  
  try {
    await axios.post('/api/exchange/product', {
      productId: currentProduct.value.id,
      quantity: exchangeForm.value.quantity,
      address: exchangeForm.value.address,
      phone: exchangeForm.value.phone,
      receiver: exchangeForm.value.receiver
    })
    
    ElMessage.success('兑换成功！')
    dialogVisible.value = false
    loadProducts()
    userStore.getUserInfo()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '兑换失败')
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.home {
  padding: 20px;
}

.product-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.points-price {
  color: #ff6b6b;
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0;
}

.stock {
  color: #999;
  font-size: 12px;
  margin-bottom: 10px;
}
</style>
```

### 7. 订单列表页面

```vue
<!-- views/Orders.vue -->
<template>
  <div class="orders">
    <el-card>
      <template #header>
        <span>我的兑换记录</span>
      </template>
      
      <el-table :data="orders" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="pointsUsed" label="使用积分" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="兑换时间" width="180" />
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadOrders"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const orders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'SUCCESS': 'success',
    'FAILED': 'danger',
    'CANCELLED': 'info'
  }
  return map[status] || 'info'
}

const statusText = (status) => {
  const map = {
    'PENDING': '处理中',
    'SUCCESS': '成功',
    'FAILED': '失败',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const loadOrders = async () => {
  const res = await axios.get('/api/exchange/orders', {
    params: {
      page: currentPage.value,
      size: pageSize.value
    }
  })
  orders.value = res.data.records
  total.value = res.data.total
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders {
  padding: 20px;
}
</style>
```

### 8. WebSocket连接

```javascript
// utils/websocket.js
import { useUserStore } from '../stores/user'
import { ElNotification } from 'element-plus'

class WebSocketClient {
  constructor() {
    this.ws = null
    this.userId = null
  }
  
  connect() {
    const userStore = useUserStore()
    if (!userStore.userInfo) return
    
    this.userId = userStore.userInfo.id
    const wsUrl = `ws://localhost:8080/ws/${this.userId}`
    
    this.ws = new WebSocket(wsUrl)
    
    this.ws.onopen = () => {
      console.log('WebSocket连接成功')
    }
    
    this.ws.onmessage = (event) => {
      ElNotification({
        title: '系统通知',
        message: event.data,
        type: 'success',
        duration: 5000
      })
    }
    
    this.ws.onerror = (error) => {
      console.error('WebSocket错误', error)
    }
    
    this.ws.onclose = () => {
      console.log('WebSocket连接关闭')
      // 5秒后重连
      setTimeout(() => {
        if (userStore.token) {
          this.connect()
        }
      }, 5000)
    }
  }
  
  disconnect() {
    if (this.ws) {
      this.ws.close()
    }
  }
}

export default new WebSocketClient()
```

### 9. 数据库SQL

```sql
CREATE DATABASE IF NOT EXISTS points_exchange DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE points_exchange;

-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `points` int DEFAULT '0' COMMENT '积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品表
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `points_price` int NOT NULL COMMENT '所需积分',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片地址',
  `status` tinyint DEFAULT '1' COMMENT '状态 0:下架 1:上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 兑换订单表
CREATE TABLE `exchange_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(100) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `points_used` int NOT NULL COMMENT '使用积分',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `address` varchar(500) DEFAULT NULL COMMENT '收货地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `receiver` varchar(50) DEFAULT NULL COMMENT '收货人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分记录表
CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '变动积分',
  `before_points` int NOT NULL COMMENT '变动前积分',
  `after_points` int NOT NULL COMMENT '变动后积分',
  `type` varchar(20) NOT NULL COMMENT '类型',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO `user` (`username`, `password`, `phone`, `points`) VALUES
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', 1000);

INSERT INTO `product` (`name`, `description`, `points_price`, `stock`, `image_url`) VALUES
('小米手环', '智能运动手环', 500, 100, 'https://via.placeholder.com/300x200'),
('蓝牙耳机', '无线蓝牙耳机', 800, 50, 'https://via.placeholder.com/300x200'),
('充电宝', '大容量移动电源', 300, 200, 'https://via.placeholder.com/300x200');
```

## 运行说明

1. **后端运行**：
    - 修改application.yml中的数据库和Redis配置
    - 运行PointsExchangeApplication主类

2. **前端运行**：
```bash
cd frontend
npm install
npm run dev
```

3. **测试账号**：
    - 用户名：test
    - 密码：123456

这个系统实现了：
- 用户注册登录
- 商品浏览和积分兑换
- 订单管理
- 积分记录查询
- WebSocket实时通知
- Redis分布式锁防止超卖
- MyBatis-Plus数据操作