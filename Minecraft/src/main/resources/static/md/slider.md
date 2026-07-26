### 核心表结构设计

#### 1. 图片主表 (`captcha_image`)
存储原始背景图片的元数据，是验证码素材的基础。

| 字段名 | 类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 图片唯一ID |
| `image_key` | VARCHAR(64) | NOT NULL, UNIQUE | 图片唯一标识符（如UUID），用于业务关联 |
| `file_name` | VARCHAR(255) | NOT NULL | 原始文件名 |
| `file_path` | VARCHAR(500) | NOT NULL | 图片存储的物理路径或CDN URL |
| `file_size` | INT | NOT NULL | 文件大小（单位：字节） |
| `width` | SMALLINT | NOT NULL | 图片宽度（像素） |
| `height` | SMALLINT | NOT NULL | 图片高度（像素） |
| `mime_type` | VARCHAR(50) | NOT NULL | 图片MIME类型，如 `image/png` |
| `md5_hash` | CHAR(32) | NOT NULL, INDEX | 文件MD5值，用于去重和完整性校验 |
| `status` | TINYINT | NOT NULL, DEFAULT 1 | 状态：1-启用，0-禁用 |
| `create_time` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| `update_time` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 2. 拼图配置表 (`captcha_puzzle_config`)
存储每张图片对应的拼图块参数，这是验证逻辑的核心。

| 字段名 | 类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 配置唯一ID |
| `image_id` | BIGINT | NOT NULL, FOREIGN KEY (`image_id`) REFERENCES `captcha_image`(`id`) | 关联的图片ID |
| `piece_x` | SMALLINT | NOT NULL | 拼图块在背景图中的X坐标（起始位置） |
| `piece_y` | SMALLINT | NOT NULL | 拼图块在背景图中的Y坐标（起始位置） |
| `piece_width` | SMALLINT | NOT NULL | 拼图块宽度 |
| `piece_height` | SMALLINT | NOT NULL | 拼图块高度 |
| `target_x` | SMALLINT | NOT NULL | 目标位置在背景图中的X坐标 |
| `target_y` | SMALLINT | NOT NULL | 目标位置在背景图中的Y坐标 |
| `slider_percent` | DECIMAL(5,4) | NOT NULL | 滑块应滑动的百分比位置（0-1），用于前端定位 |
| `version` | INT | NOT NULL, DEFAULT 1 | 配置版本号，用于灰度或A/B测试 |
| `create_time` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 3. 验证记录表 (`captcha_verify_record`)
用于记录每一次验证请求的详细信息，便于数据分析和安全审计。

| 字段名 | 类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 记录唯一ID |
| `trace_id` | VARCHAR(64) | NOT NULL, INDEX | 请求追踪ID，用于关联前端会话 |
| `image_id` | BIGINT | NOT NULL, INDEX | 使用的图片ID |
| `client_ip` | VARCHAR(45) | NOT NULL | 客户端IP地址 |
| `user_agent` | TEXT | | 客户端User-Agent信息 |
| `slider_offset` | SMALLINT | NOT NULL | 用户实际拖动的偏移量（像素） |
| `target_offset` | SMALLINT | NOT NULL | 目标位置的偏移量（像素） |
| `is_passed` | TINYINT(1) | NOT NULL | 验证结果：1-通过，0-失败 |
| `cost_time` | INT | NOT NULL | 验证耗时（毫秒） |
| `create_time` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | 记录创建时间 |

---

## 一、项目依赖配置

### 1. Maven依赖
```xml
<dependencies>
    <!-- SpringBoot 核心 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Redis 缓存 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    
    <!-- 本地缓存 Caffeine -->
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
    </dependency>
    
    <!-- 数据库 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <!-- 连接池 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.2.20</version>
    </dependency>
    
    <!-- 工具类 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.8.25</version>
    </dependency>
    
    <!-- 异步处理 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

### 2. application.yml 配置
```yaml
spring:
  # Redis配置
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 2000ms
  
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/captcha_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    druid:
      initial-size: 5
      max-active: 20
      min-idle: 5
      max-wait: 60000
      test-on-borrow: true
      test-while-idle: true
  
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true

# 验证码业务配置
captcha:
  pool:
    min-size: 200          # 图片池最小数量
    max-size: 1000         # 图片池最大数量
    refill-threshold: 50   # 低于此值触发补充
  cache:
    local:
      max-size: 10000      # 本地缓存最大条目
      expire-after-write: 5m
    redis:
      image-ttl: 10m       # 图片缓存时间
      puzzle-ttl: 30m      # 拼图配置缓存时间
      session-ttl: 3m      # 会话缓存时间
  image:
    width: 320
    height: 180
    format: svg            # svg / png / webp
    quality: 0.85

# 异步配置
task:
  pool:
    core-size: 5
    max-size: 20
    queue-capacity: 200
    keep-alive: 60s
```

---

## 二、核心缓存设计

### 1. 多级缓存配置
```java
@Configuration
public class CacheConfig {
    
    /**
     * Caffeine本地缓存
     */
    @Bean("localCache")
    public Cache<String, CaptchaImage> localCache() {
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .refreshAfterWrite(3, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }
    
    /**
     * Redis缓存管理器
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
        
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }
}
```

### 2. 缓存服务实现
```java
@Service
@Slf4j
public class CaptchaCacheService {
    
    @Autowired
    @Qualifier("localCache")
    private Cache<String, CaptchaImage> localCache;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String IMAGE_KEY_PREFIX = "captcha:img:";
    private static final String PUZZLE_KEY_PREFIX = "captcha:puzzle:";
    private static final String SESSION_KEY_PREFIX = "captcha:session:";
    private static final String POOL_KEY = "captcha:pool";
    
    /**
     * 获取图片（多级缓存）
     */
    public CaptchaImage getImage(String imageId) {
        // 1. 查询本地缓存
        CaptchaImage image = localCache.getIfPresent(imageId);
        if (image != null) {
            log.debug("本地缓存命中: {}", imageId);
            return image;
        }
        
        // 2. 查询Redis缓存
        String redisKey = IMAGE_KEY_PREFIX + imageId;
        Object cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            image = objectMapper.convertValue(cached, CaptchaImage.class);
            // 回填本地缓存
            localCache.put(imageId, image);
            log.debug("Redis缓存命中: {}", imageId);
            return image;
        }
        
        // 3. 查询数据库
        image = captchaImageRepository.findById(imageId).orElse(null);
        if (image != null) {
            // 写入Redis（TTL: 10分钟）
            redisTemplate.opsForValue().set(redisKey, image, 10, TimeUnit.MINUTES);
            // 写入本地缓存
            localCache.put(imageId, image);
            log.debug("数据库加载并缓存: {}", imageId);
        }
        
        return image;
    }
    
    /**
     * 获取拼图配置（带版本控制）
     */
    public PuzzleConfig getPuzzleConfig(String imageId, Integer version) {
        String cacheKey = PUZZLE_KEY_PREFIX + imageId + ":v" + version;
        
        // 直接从Redis获取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return objectMapper.convertValue(cached, PuzzleConfig.class);
        }
        
        // 查询数据库
        PuzzleConfig config = puzzleConfigRepository
                .findByImageIdAndVersion(imageId, version)
                .orElse(null);
        
        if (config != null) {
            redisTemplate.opsForValue().set(cacheKey, config, 30, TimeUnit.MINUTES);
        }
        
        return config;
    }
    
    /**
     * 缓存验证会话
     */
    public void cacheSession(String traceId, CaptchaSession session) {
        String key = SESSION_KEY_PREFIX + traceId;
        redisTemplate.opsForValue().set(key, session, 3, TimeUnit.MINUTES);
    }
    
    /**
     * 获取验证会话（验证完成后立即删除，防止重放攻击）
     */
    public CaptchaSession getAndRemoveSession(String traceId) {
        String key = SESSION_KEY_PREFIX + traceId;
        Object session = redisTemplate.opsForValue().get(key);
        if (session != null) {
            redisTemplate.delete(key);
            return objectMapper.convertValue(session, CaptchaSession.class);
        }
        return null;
    }
}
```

---

## 三、图片池管理

### 1. 图片池服务
```java
@Component
@Slf4j
public class CaptchaPoolService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CaptchaImageService imageService;
    
    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;
    
    private static final String POOL_KEY = "captcha:pool";
    private static final String PROCESSING_KEY = "captcha:processing";
    
    @Value("${captcha.pool.min-size:200}")
    private int minPoolSize;
    
    @Value("${captcha.pool.max-size:1000}")
    private int maxPoolSize;
    
    @Value("${captcha.pool.refill-threshold:50}")
    private int refillThreshold;
    
    /**
     * 初始化图片池（系统启动时执行）
     */
    @PostConstruct
    public void initPool() {
        log.info("初始化图片池...");
        // 检查池大小
        Long poolSize = redisTemplate.opsForList().size(POOL_KEY);
        if (poolSize == null || poolSize < minPoolSize) {
            int toGenerate = minPoolSize - (poolSize != null ? poolSize.intValue() : 0);
            log.info("需要预生成 {} 张验证码图片", toGenerate);
            generateImagesAsync(toGenerate);
        }
    }
    
    /**
     * 从池中获取图片ID（非阻塞）
     */
    public String getImageFromPool() {
        // 检查池水位
        Long poolSize = redisTemplate.opsForList().size(POOL_KEY);
        if (poolSize != null && poolSize < refillThreshold) {
            // 异步补充
            int toGenerate = Math.min(minPoolSize - poolSize.intValue(), 50);
            generateImagesAsync(toGenerate);
        }
        
        // 弹出图片ID
        Object imageId = redisTemplate.opsForList().leftPop(POOL_KEY);
        if (imageId != null) {
            return imageId.toString();
        }
        
        // 池为空，紧急生成
        log.warn("图片池为空，紧急生成...");
        return imageService.generateAndCacheImage();
    }
    
    /**
     * 异步生成图片
     */
    @Async("asyncExecutor")
    public void generateImagesAsync(int count) {
        if (count <= 0) return;
        log.info("开始异步生成 {} 张验证码图片", count);
        
        // 使用分布式锁防止重复生成
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(PROCESSING_KEY, "1", Duration.ofSeconds(30));
        
        if (!Boolean.TRUE.equals(locked)) {
            log.debug("其他实例正在生成图片，跳过");
            return;
        }
        
        try {
            List<String> generatedIds = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String imageId = imageService.generateAndCacheImage();
                generatedIds.add(imageId);
                
                // 控制生成速率
                if (i % 10 == 0) {
                    Thread.sleep(50);
                }
            }
            
            // 批量入池
            if (!generatedIds.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(POOL_KEY, generatedIds.toArray());
                // 裁剪池大小
                redisTemplate.opsForList().trim(POOL_KEY, 0, maxPoolSize - 1);
            }
            
            log.info("异步生成完成，新增 {} 张图片", generatedIds.size());
        } catch (Exception e) {
            log.error("异步生成图片失败", e);
        } finally {
            redisTemplate.delete(PROCESSING_KEY);
        }
    }
    
    /**
     * 定时任务：维护池水位
     */
    @Scheduled(fixedDelay = 60000) // 每分钟检查一次
    public void maintainPool() {
        Long poolSize = redisTemplate.opsForList().size(POOL_KEY);
        if (poolSize == null) return;
        
        if (poolSize < refillThreshold) {
            int toGenerate = Math.min(minPoolSize - poolSize.intValue(), 100);
            log.info("定时维护：补充 {} 张图片", toGenerate);
            generateImagesAsync(toGenerate);
        }
    }
}
```

### 2. 图片生成服务
```java
@Service
@Slf4j
public class CaptchaImageService {
    
    @Autowired
    private CaptchaImageRepository imageRepository;
    
    @Autowired
    private CaptchaCacheService cacheService;
    
    @Value("${captcha.image.width:320}")
    private int width;
    
    @Value("${captcha.image.height:180}")
    private int height;
    
    /**
     * 生成验证码图片并缓存
     */
    @Transactional
    public String generateAndCacheImage() {
        // 1. 生成SVG图片
        String imageId = IdUtil.simpleUUID();
        String svgContent = generateSVG();
        byte[] imageBytes = svgContent.getBytes(StandardCharsets.UTF_8);
        
        // 2. 计算MD5
        String md5 = DigestUtils.md5Hex(imageBytes);
        
        // 3. 检查是否已存在相同图片（去重）
        Optional<CaptchaImage> existing = imageRepository.findByMd5Hash(md5);
        if (existing.isPresent()) {
            CaptchaImage image = existing.get();
            // 生成新的拼图配置
            PuzzleConfig puzzle = generatePuzzleConfig(image);
            puzzleConfigRepository.save(puzzle);
            
            // 更新缓存
            cacheService.cachePuzzleConfig(puzzle);
            return image.getId();
        }
        
        // 4. 保存图片元数据
        CaptchaImage image = new CaptchaImage();
        image.setId(imageId);
        image.setImageKey(imageId);
        image.setFileName(imageId + ".svg");
        image.setFileContent(svgContent); // 存储SVG字符串
        image.setFileSize(imageBytes.length);
        image.setWidth(width);
        image.setHeight(height);
        image.setMimeType("image/svg+xml");
        image.setMd5Hash(md5);
        image.setStatus(1);
        
        // 5. 生成拼图配置
        PuzzleConfig puzzle = generatePuzzleConfig(image);
        
        // 6. 批量保存
        imageRepository.save(image);
        puzzleConfigRepository.save(puzzle);
        
        // 7. 写入Redis缓存
        cacheService.cacheImage(image);
        cacheService.cachePuzzleConfig(puzzle);
        
        log.info("生成图片成功: {}, 大小: {} bytes", imageId, imageBytes.length);
        return imageId;
    }
    
    /**
     * 生成SVG图片（轻量级）
     */
    private String generateSVG() {
        List<String> colors = Arrays.asList("#b0c4de", "#f7dc6f", "#a3d8a3", "#f5b7b1", "#d7bde2", "#aed6f1");
        String bgColor = colors.get(ThreadLocalRandom.current().nextInt(colors.size()));
        String accentColor = colors.get(ThreadLocalRandom.current().nextInt(colors.size()));
        
        int cx = 80 + ThreadLocalRandom.current().nextInt(160);
        int cy = 40 + ThreadLocalRandom.current().nextInt(100);
        int r = 30 + ThreadLocalRandom.current().nextInt(50);
        
        return String.format("""
            <svg xmlns="http://www.w3.org/2000/svg" width="%d" height="%d" viewBox="0 0 %d %d">
                <rect width="%d" height="%d" fill="%s"/>
                <circle cx="%d" cy="%d" r="%d" fill="%s" opacity="0.6"/>
                <rect x="60" y="30" width="200" height="120" fill="none" stroke="#2c3e50" stroke-width="2" opacity="0.2"/>
                <circle cx="%d" cy="%d" r="%d" fill="#4a6cf7" opacity="0.15"/>
            </svg>
            """, 
            width, height, width, height,
            width, height, bgColor,
            cx, cy, r, accentColor,
            160 + ThreadLocalRandom.current().nextInt(40),
            80 + ThreadLocalRandom.current().nextInt(40),
            10 + ThreadLocalRandom.current().nextInt(20)
        );
    }
    
    /**
     * 生成拼图配置
     */
    private PuzzleConfig generatePuzzleConfig(CaptchaImage image) {
        PuzzleConfig config = new PuzzleConfig();
        config.setImageId(image.getId());
        config.setVersion(1);
        
        // 拼图块在左侧 (X: 6~40, Y: 6~height-6)
        int gap = 6;
        int pieceWidth = (width - 2 * gap) / 3;
        int pieceHeight = height - 2 * gap;
        
        int pieceX = gap + ThreadLocalRandom.current().nextInt(0, 20);
        int pieceY = gap;
        
        config.setPieceX(pieceX);
        config.setPieceY(pieceY);
        config.setPieceWidth(pieceWidth);
        config.setPieceHeight(pieceHeight);
        
        // 目标位置在右侧
        config.setTargetX(width - pieceWidth - gap);
        config.setTargetY(pieceY);
        
        // 滑块百分比
        double sliderPercent = (double)(config.getTargetX() - pieceX) / (width - pieceWidth - gap - pieceX);
        config.setSliderPercent(BigDecimal.valueOf(Math.min(Math.max(sliderPercent, 0.3), 0.9)));
        
        return config;
    }
}
```

---

## 四、异步配置

### 1. 线程池配置
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("captcha-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
```

### 2. 异步验证记录
```java
@Service
@Slf4j
public class VerifyRecordService {
    
    @Autowired
    private VerifyRecordRepository recordRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate; // 或使用Kafka
    
    /**
     * 异步保存验证记录（通过MQ）
     */
    public void saveRecordAsync(VerifyRecord record) {
        try {
            // 发送到MQ，由消费者异步落库
            rabbitTemplate.convertAndSend("captcha.verify.exchange", 
                                          "verify.record", 
                                          record);
        } catch (Exception e) {
            log.error("发送验证记录到MQ失败", e);
            // 降级：直接写入数据库
            saveRecordSync(record);
        }
    }
    
    /**
     * 同步保存（降级方案）
     */
    @Transactional
    public void saveRecordSync(VerifyRecord record) {
        recordRepository.save(record);
    }
}
```

---

## 五、验证接口优化

### 1. 优化后的Controller
```java
@RestController
@RequestMapping("/api/captcha")
@Slf4j
public class CaptchaController {
    
    @Autowired
    private CaptchaPoolService poolService;
    
    @Autowired
    private CaptchaCacheService cacheService;
    
    @Autowired
    private CaptchaImageService imageService;
    
    @Autowired
    private VerifyRecordService recordService;
    
    private static final int TOLERANCE = 8;
    
    /**
     * 获取验证码（从池中获取）
     */
    @GetMapping("/get")
    public ResponseEntity<CaptchaResponse> getCaptcha(@RequestParam String traceId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 从池中获取图片ID
            String imageId = poolService.getImageFromPool();
            
            // 2. 获取图片和配置（缓存）
            CaptchaImage image = cacheService.getImage(imageId);
            PuzzleConfig puzzle = cacheService.getPuzzleConfig(imageId, 1);
            
            // 3. 构建响应
            CaptchaResponse response = CaptchaResponse.builder()
                    .traceId(traceId)
                    .imageId(imageId)
                    .imageData(image.getFileContent())
                    .puzzleX(puzzle.getTargetX())
                    .puzzleY(puzzle.getTargetY())
                    .pieceWidth(puzzle.getPieceWidth())
                    .pieceHeight(puzzle.getPieceHeight())
                    .sliderPercent(puzzle.getSliderPercent())
                    .build();
            
            // 4. 缓存会话
            CaptchaSession session = new CaptchaSession();
            session.setImageId(imageId);
            session.setTargetX(puzzle.getTargetX());
            session.setTargetY(puzzle.getTargetY());
            session.setPieceWidth(puzzle.getPieceWidth());
            session.setPieceHeight(puzzle.getPieceHeight());
            session.setCreateTime(System.currentTimeMillis());
            cacheService.cacheSession(traceId, session);
            
            long costTime = System.currentTimeMillis() - startTime;
            log.debug("获取验证码成功, traceId: {}, cost: {}ms", traceId, costTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取验证码失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CaptchaResponse.error("获取验证码失败"));
        }
    }
    
    /**
     * 验证（带防重放攻击）
     */
    @PostMapping("/verify")
    public ResponseEntity<VerifyResult> verify(@RequestBody VerifyRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 获取会话（验证后立即删除）
            CaptchaSession session = cacheService.getAndRemoveSession(request.getTraceId());
            if (session == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(VerifyResult.fail("会话已过期或无效"));
            }
            
            // 2. 计算实际偏移量
            int actualOffset = request.getSliderPosition();
            int targetOffset = session.getTargetX() - session.getPieceWidth() / 2;
            
            // 3. 验证
            boolean passed = Math.abs(actualOffset - targetOffset) <= TOLERANCE;
            
            // 4. 异步保存记录
            VerifyRecord record = VerifyRecord.builder()
                    .traceId(request.getTraceId())
                    .imageId(session.getImageId())
                    .clientIp(getClientIP())
                    .userAgent(request.getUserAgent())
                    .sliderOffset(actualOffset)
                    .targetOffset(targetOffset)
                    .isPassed(passed)
                    .costTime((int)(System.currentTimeMillis() - startTime))
                    .build();
            recordService.saveRecordAsync(record);
            
            // 5. 返回结果
            VerifyResult result = VerifyResult.builder()
                    .passed(passed)
                    .message(passed ? "验证成功" : "验证失败，请重试")
                    .build();
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("验证失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(VerifyResult.fail("验证服务异常"));
        }
    }
}
```

---

## 六、性能监控

### 1. 自定义监控指标
```java
@Component
@Slf4j
public class CaptchaMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // 计数器
    private Counter getCaptchaCounter;
    private Counter verifySuccessCounter;
    private Counter verifyFailCounter;
    private Counter cacheHitCounter;
    private Counter cacheMissCounter;
    
    // 计时器
    private Timer getCaptchaTimer;
    private Timer verifyTimer;
    
    public CaptchaMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initMetrics();
    }
    
    private void initMetrics() {
        this.getCaptchaCounter = Counter.builder("captcha.get.total")
                .description("获取验证码总次数")
                .register(meterRegistry);
        
        this.verifySuccessCounter = Counter.builder("captcha.verify.success")
                .description("验证成功次数")
                .register(meterRegistry);
        
        this.verifyFailCounter = Counter.builder("captcha.verify.fail")
                .description("验证失败次数")
                .register(meterRegistry);
        
        this.cacheHitCounter = Counter.builder("captcha.cache.hit")
                .description("缓存命中次数")
                .register(meterRegistry);
        
        this.cacheMissCounter = Counter.builder("captcha.cache.miss")
                .description("缓存未命中次数")
                .register(meterRegistry);
        
        this.getCaptchaTimer = Timer.builder("captcha.get.duration")
                .description("获取验证码耗时")
                .register(meterRegistry);
        
        this.verifyTimer = Timer.builder("captcha.verify.duration")
                .description("验证耗时")
                .register(meterRegistry);
    }
    
    public void recordGetCaptcha(long duration) {
        getCaptchaCounter.increment();
        getCaptchaTimer.record(duration, TimeUnit.MILLISECONDS);
    }
    
    public void recordVerify(boolean passed, long duration) {
        if (passed) {
            verifySuccessCounter.increment();
        } else {
            verifyFailCounter.increment();
        }
        verifyTimer.record(duration, TimeUnit.MILLISECONDS);
    }
    
    public void recordCacheHit() {
        cacheHitCounter.increment();
    }
    
    public void recordCacheMiss() {
        cacheMissCounter.increment();
    }
}
```

### 2. Actuator监控端点
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: captcha-service
```

访问监控指标：`/actuator/metrics/captcha.get.duration`

---

## 七、优化效果测试

### 性能测试对比
```java
@SpringBootTest
@Slf4j
public class CaptchaPerformanceTest {
    
    @Autowired
    private CaptchaController controller;
    
    @Test
    public void testPerformance() throws Exception {
        // 使用JMeter或wrk进行压力测试
        // 预期结果：
        // - 单次获取耗时: 5-15ms (缓存命中)
        // - 单次验证耗时: 10-20ms
        // - 支持QPS: 5000+
        // - 缓存命中率: 95%+
    }
}
```

---

这套SpringBoot实现方案的核心优化点在于**多级缓存**、**图片预生成池**和**异步处理**，能够显著提升系统的响应速度和并发能力。您可以根据实际业务规模调整缓存大小和线程池参数。