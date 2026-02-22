生成一份
Springboot+mybatis-puls+redise+mysql+websockt前后端分离，前端使用技术栈为vue3+vite的实现旅游推荐订票
（包含酒店，景点，美食，特产，纪念品，按季节推荐按），热门旅游城市推荐，车票+机票+出租车
（支持购买-添加购物车，同时以上功能支持评论，点赞，收藏，详细信息），出行计划，社区（分享自己的旅游视频图片支持添加文字和标题），
跟团游（创建旅游团加入旅行团分享视频图片添加好友），个人中心（编辑个人信息（爱好，职业，补充其他功能），
可以发布朋友圈分享旅行图片视频，点赞评论功能，），旅游推荐，多级评论，旅游视频，
好友添加（好友聊天即时通讯），订单，购物车，支付，注册（注册使用邮箱，用户名，密码登录（注册完成生成按注册时间加随机6数字账号））
登录（注册完直接进入主页），使用登录拦截器，跨越配置，视频图片分片上传视频图片保持本地数据库存储URL，
热点旅游新闻推荐，旅游景点-酒店-美食推荐，以上内容根据收藏和点赞数进行排序），
完善以上内容并补充其他没有提到的功能已经模块，生产完整后端框架。
功能模块及详细设计


tourism-backend/
├── src/main/java/com/tourism/
│   ├── TourismApplication.java
│   ├── config/
│   │   ├── WebConfig.java
│   │   ├── RedisConfig.java
│   │   ├── WebSocketConfig.java
│   │   ├── MybatisPlusConfig.java
│   │   ├── SwaggerConfig.java
│   │   ├── CorsConfig.java
│   │   └── FileUploadConfig.java
│   ├── interceptor/
│   │   ├── LoginInterceptor.java
│   │   └── TokenInterceptor.java
│   ├── controller/
│   │   ├── UserController.java
│   │   ├── AuthController.java
│   │   ├── AttractionController.java
│   │   ├── HotelController.java
│   │   ├── FoodController.java
│   │   ├── ProductController.java
│   │   ├── TicketController.java
│   │   ├── TravelController.java
│   │   ├── CommunityController.java
│   │   ├── GroupTravelController.java
│   │   ├── FriendController.java
│   │   ├── ChatController.java
│   │   ├── OrderController.java
│   │   ├── CartController.java
│   │   ├── PaymentController.java
│   │   ├── CommentController.java
│   │   ├── LikeController.java
│   │   ├── CollectionController.java
│   │   ├── NewsController.java
│   │   └── UploadController.java
│   ├── service/
│   │   ├── impl/
│   │   │   ├── UserServiceImpl.java
│   │   │   ├── ...
│   │   │   └── NewsServiceImpl.java
│   │   ├── UserService.java
│   │   ├── ...
│   │   └── NewsService.java
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   ├── ...
│   │   └── NewsMapper.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Attraction.java
│   │   ├── Hotel.java
│   │   ├── Food.java
│   │   ├── Product.java
│   │   ├── Ticket.java
│   │   ├── TravelPlan.java
│   │   ├── CommunityPost.java
│   │   ├── GroupTravel.java
│   │   ├── Friend.java
│   │   ├── ChatMessage.java
│   │   ├── Order.java
│   │   ├── Cart.java
│   │   ├── Payment.java
│   │   ├── Comment.java
│   │   ├── Like.java
│   │   ├── Collection.java
│   │   ├── News.java
│   │   └── FileInfo.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── OrderRequest.java
│   │   │   └── ...
│   │   └── response/
│   │       ├── ApiResponse.java
│   │       ├── PageResponse.java
│   │       ├── LoginResponse.java
│   │       └── ...
│   ├── vo/
│   │   ├── UserVO.java
│   │   ├── AttractionVO.java
│   │   ├── CommentVO.java
│   │   └── ...
│   ├── enums/
│   │   ├── OrderStatus.java
│   │   ├── PaymentStatus.java
│   │   ├── TravelType.java
│   │   └── ...
│   ├── utils/
│   │   ├── JwtUtils.java
│   │   ├── RedisUtils.java
│   │   ├── FileUtils.java
│   │   ├── DateUtils.java
│   │   ├── AccountGenerator.java
│   │   └── ...
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       ├── BusinessException.java
│       └── ...
└── src/main/resources/
├── application.yml
├── application-dev.yml
├── application-prod.yml
└── mapper/
├── UserMapper.xml
└── ...