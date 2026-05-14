# MyBatis-Plus 使用说明

## 概述

本项目已完全使用 MyBatis-Plus 进行数据增删改查，无需编写复杂的 SQL 语句。

## MyBatis-Plus 核心方法

### 1. 基础 CRUD 方法

#### 插入
```java
// 插入单条记录
save(T entity)

// 批量插入
saveBatch(Collection<T> entityList)
```

#### 删除
```java
// 根据 ID 删除
removeById(Serializable id)

// 根据 ID 批量删除
removeByIds(Collection<? extends Serializable> idList)

// 根据条件删除
remove(Wrapper<T> wrapper)
```

#### 更新
```java
// 根据 ID 更新
updateById(T entity)

// 根据条件更新
update(T entity, Wrapper<T> wrapper)

// 批量更新
updateBatchById(Collection<T> entityList)
```

#### 查询
```java
// 根据 ID 查询
getById(Serializable id)

// 根据 ID 批量查询
listByIds(Collection<? extends Serializable> idList)

// 查询所有
list()

// 根据条件查询单条
getOne(Wrapper<T> wrapper)

// 根据条件查询列表
list(Wrapper<T> wrapper)

// 分页查询
page(Page<T> page, Wrapper<T> wrapper)

// 查询数量
count(Wrapper<T> wrapper)
```

### 2. 条件构造器

#### LambdaQueryWrapper
```java
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

// 等于
wrapper.eq(User::getName, "张三");

// 不等于
wrapper.ne(User::getStatus, 0);

// 大于
wrapper.gt(User::getAge, 18);

// 小于
wrapper.lt(User::getAge, 60);

// 大于等于
wrapper.ge(User::getScore, 60);

// 小于等于
wrapper.le(User::getScore, 100);

// 模糊查询
wrapper.like(User::getName, "张");

// 在...之间
wrapper.between(User::getAge, 18, 30);

// 在列表中
wrapper.in(User::getId, Arrays.asList(1, 2, 3));

// 不在列表中
wrapper.notIn(User::getStatus, Arrays.asList(0, 2));

// 为空
wrapper.isNull(User::getDeleteTime);

// 不为空
wrapper.isNotNull(User::getUpdateTime);

// 排序
wrapper.orderByDesc(User::getCreateTime);
wrapper.orderByAsc(User::getName);

// 分组
wrapper.groupBy(User::getCity);

// OR 条件
wrapper.eq(User::getName, "张三")
        .or()
        .eq(User::getName, "李四");

// 嵌套条件
wrapper.and(w -> w.eq(User::getCity, "北京")
        .eq(User::getStatus, 1))
        .or()
        .eq(User::getCity, "上海");

// 限制数量
wrapper.last("LIMIT 10");
```

### 3. 分页查询

```java
// 创建分页对象
Page<User> page = new Page<>(pageNum, pageSize);

// 创建查询条件
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getStatus, 1)
        .orderByDesc(User::getCreateTime);

// 执行分页查询
Page<User> result = page(page, wrapper);

// 获取结果
List<User> records = result.getRecords();  // 数据列表
long total = result.getTotal();              // 总记录数
long pages = result.getPages();              // 总页数
```

### 4. Service 层方法

所有 Service 实现类都继承自 `ServiceImpl<Mapper, Entity>`，可以直接使用以下方法：

```java
// 插入
boolean save(T entity);
boolean saveBatch(Collection<T> entityList);

// 删除
boolean removeById(Serializable id);
boolean removeByIds(Collection<? extends Serializable> idList);
boolean remove(Wrapper<T> wrapper);

// 更新
boolean updateById(T entity);
boolean update(T entity, Wrapper<T> wrapper);
boolean updateBatchById(Collection<T> entityList);

// 查询
T getById(Serializable id);
List<T> list();
List<T> list(Wrapper<T> wrapper);
T getOne(Wrapper<T> wrapper);
Page<T> page(Page<T> page, Wrapper<T> wrapper);
int count(Wrapper<T> wrapper);
```

## 项目中的使用示例

### 1. UserServiceImpl.java
```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public LoginResponse login(LoginRequest request) {
        // 使用 LambdaQueryWrapper 查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = getOne(wrapper);  // MyBatis-Plus 方法
        
        // 使用 save 插入
        save(user);  // MyBatis-Plus 方法
        
        // 使用 updateById 更新
        updateById(user);  // MyBatis-Plus 方法
        
        // 使用 getById 查询
        User user = getById(userId);  // MyBatis-Plus 方法
    }
}
```

### 2. AttractionServiceImpl.java
```java
@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements AttractionService {

    @Override
    public PageResponse<AttractionVO> getAttractionList(PageRequest request) {
        // 分页查询
        Page<Attraction> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (request.getKeyword() != null) {
            wrapper.like(Attraction::getName, request.getKeyword())
                    .or()
                    .like(Attraction::getCity, request.getKeyword());
        }
        
        // 排序
        wrapper.orderByDesc(Attraction::getCreateTime);
        
        // 执行分页查询
        Page<Attraction> result = page(page, wrapper);  // MyBatis-Plus 方法
    }

    @Override
    public List<AttractionVO> getRecommendAttractions(String city, String season, Integer limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, 1);
        
        if (city != null) {
            wrapper.eq(Attraction::getCity, city);
        }
        
        if (season != null) {
            wrapper.like(Attraction::getSeason, season);
        }
        
        // 排序和限制数量
        wrapper.orderByDesc(Attraction::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));
        
        // 查询列表
        List<Attraction> list = list(wrapper);  // MyBatis-Plus 方法
    }
}
```

### 3. LikeServiceImpl.java
```java
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Override
    public void like(LikeRequest request, Long userId) {
        // 查询是否已点赞
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getItemType, request.getItemType())
                .eq(Like::getItemId, request.getItemId());
        
        if (getOne(wrapper) != null) {
            throw new RuntimeException("已经点赞过了");
        }

        // 插入点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setItemType(request.getItemType());
        like.setItemId(request.getItemId());
        save(like);  // MyBatis-Plus 方法
    }

    @Override
    public void unlike(LikeRequest request, Long userId) {
        // 根据条件删除
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getItemType, request.getItemType())
                .eq(Like::getItemId, request.getItemId());
        remove(wrapper);  // MyBatis-Plus 方法
    }
}
```

### 4. CommentServiceImpl.java
```java
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public void addComment(CommentRequest request, Long userId) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setItemType(request.getItemType());
        comment.setItemId(request.getItemId());
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());
        comment.setImages(request.getImages());
        comment.setRating(request.getRating());
        save(comment);  // MyBatis-Plus 方法
    }

    @Override
    public void deleteComment(Long id) {
        removeById(id);  // MyBatis-Plus 方法
    }

    @Override
    public PageResponse<CommentVO> getCommentList(String itemType, Long itemId, PageRequest request) {
        // 分页查询
        Page<Comment> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getItemType, itemType)
                .eq(Comment::getItemId, itemId)
                .isNull(Comment::getParentId)
                .orderByDesc(Comment::getCreateTime);

        Page<Comment> result = page(page, wrapper);  // MyBatis-Plus 方法
    }

    @Override
    public List<CommentVO> getCommentTree(String itemType, Long itemId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getItemType, itemType)
                .eq(Comment::getItemId, itemId)
                .orderByDesc(Comment::getCreateTime);

        List<Comment> allComments = list(wrapper);  // MyBatis-Plus 方法
        // ... 业务逻辑处理
    }
}
```

### 5. OrderServiceImpl.java
```java
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    public Order createOrder(Long userId, OrderRequest request) {
        Order order = new Order();
        order.setUserId(userId);
        order.setItemType(request.getItemType());
        order.setItemId(request.getItemId());
        order.setQuantity(request.getQuantity());
        order.setRemark(request.getRemark());
        order.setStatus(OrderStatus.PENDING.getCode().toString());
        save(order);  // MyBatis-Plus 方法
        return order;
    }

    @Override
    public Order getOrderDetail(Long id) {
        return getById(id);  // MyBatis-Plus 方法
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = getById(id);  // MyBatis-Plus 方法
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(OrderStatus.CANCELLED.getCode().toString());
        updateById(order);  // MyBatis-Plus 方法
    }
}
```

### 6. ChatServiceImpl.java
```java
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    @Override
    public void sendMessage(ChatMessage message) {
        save(message);  // MyBatis-Plus 方法
    }

    @Override
    public List<ChatMessage> getChatHistory(Long userId, Long friendId, Integer limit) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        // 复杂条件查询
        wrapper.and(w -> w.eq(ChatMessage::getSenderId, userId)
                .eq(ChatMessage::getReceiverId, friendId))
                .or()
                .eq(ChatMessage::getSenderId, friendId)
                .eq(ChatMessage::getReceiverId, userId);
        wrapper.orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + (limit != null ? limit : 50));
        return list(wrapper);  // MyBatis-Plus 方法
    }

    @Override
    public void markAsRead(Long userId, Long friendId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSenderId, friendId)
                .eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getIsRead, 0);
        
        List<ChatMessage> messages = list(wrapper);  // MyBatis-Plus 方法
        messages.forEach(msg -> {
            msg.setIsRead(1);
            updateById(msg);  // MyBatis-Plus 方法
        });
    }
}
```

### 7. FriendServiceImpl.java
```java
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Override
    public void addFriend(Long userId, Long friendId) {
        // 检查是否已是好友
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getUserId, userId)
                .eq(Friend::getFriendId, friendId);
        if (getOne(wrapper) != null) {  // MyBatis-Plus 方法
            throw new BusinessException("已经是好友或已发送好友请求");
        }

        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus(FriendStatus.PENDING.ordinal());
        save(friend);  // MyBatis-Plus 方法
    }

    @Override
    public void acceptFriend(Long userId, Long friendId) {
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getUserId, friendId)
                .eq(Friend::getFriendId, userId);
        Friend friend = getOne(wrapper);  // MyBatis-Plus 方法
        if (friend == null) {
            throw new BusinessException("好友请求不存在");
        }
        friend.setStatus(FriendStatus.ACCEPTED.ordinal());
        updateById(friend);  // MyBatis-Plus 方法

        Friend reverseFriend = new Friend();
        reverseFriend.setUserId(userId);
        reverseFriend.setFriendId(friendId);
        reverseFriend.setStatus(FriendStatus.ACCEPTED.ordinal());
        save(reverseFriend);  // MyBatis-Plus 方法
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Friend::getUserId, userId)
                .eq(Friend::getFriendId, friendId))
                .or()
                .eq(Friend::getUserId, friendId)
                .eq(Friend::getFriendId, userId);
        remove(wrapper);  // MyBatis-Plus 方法
    }

    @Override
    public List<Friend> getFriendList(Long userId) {
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getUserId, userId)
                .eq(Friend::getStatus, FriendStatus.ACCEPTED.ordinal());
        return list(wrapper);  // MyBatis-Plus 方法
    }
}
```

## Mapper 接口

所有 Mapper 接口都继承自 `BaseMapper<Entity>`，无需编写 XML 文件：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 无需编写方法，MyBatis-Plus 自动提供 CRUD 方法
}
```

## 配置文件

### application.yml
```yaml
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.bolan.minecraft.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### MybatisPlusConfig.java
```java
@Configuration
@MapperScan("com.bolan.minecraft.mapper")
public class MybatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

## 优势

1. **无需编写 SQL**：大部分 CRUD 操作无需编写 SQL 语句
2. **类型安全**：使用 Lambda 表达式，编译时检查字段名
3. **代码简洁**：减少大量样板代码
4. **易于维护**：统一的 API，降低学习成本
5. **性能优化**：内置分页、批量操作等优化

## 注意事项

1. 所有 Mapper XML 文件都是空的，仅包含基本的 mapper 标签
2. 所有 Service 实现类都继承自 `ServiceImpl<Mapper, Entity>`
3. 所有 Mapper 接口都继承自 `BaseMapper<Entity>`
4. 使用 `LambdaQueryWrapper` 进行条件查询，避免硬编码字段名
5. 复杂查询可以使用 `last()` 方法添加原生 SQL 片段