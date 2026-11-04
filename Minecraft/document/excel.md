# Minecraft 旅游系统 — Excel 批量导入导出模块设计

## 一、设计目标

| 目标 | 说明 |
|------|------|
| 通用性 | 一套框架覆盖景点/酒店/美食/商品/票务/城市/积分商品等 100+ 实体的导入导出 |
| 可扩展 | 新增实体只需增加 Excel 映射类 + 配置，无需改核心代码 |
| 高性能 | 流式读写（EasyExcel），支持 10 万+ 行；分批插入 + 事务控制 |
| 可观测 | 异步任务、进度查询、失败明细下载、操作日志留痕 |
| 安全 | 文件类型/大小校验、权限校验、SQL 注入防护（复用 Druid wall）、敏感字段导出脱敏 |

---

## 二、技术选型

| 技术 | 用途 |
|------|------|
| **EasyExcel 3.3.x**（阿里） | 流式读写 Excel，低内存，支持 SAX 解析 |
| Spring Boot 3 + MyBatis Plus | 现有栈，复用 Service/Mapper |
| Redis | 异步任务进度缓存、幂等锁 |
| WebSocket | 导入进度实时推送（可选，复用现有 IM 通道） |
| Spring Async / 线程池 | 异步导入导出任务 |
| Hutool | 文件、校验工具 |

> 选 EasyExcel 而非 POI：POI 全量加载内存易 OOM，EasyExcel 基于 SAX 事件模型，100 万行也能稳定运行。

---

## 三、整体架构

```
┌──────────────────────────────────────────────────────────────┐
│ 前端（用户端/管理端）                                           │
│  上传 .xlsx → 调 /import 接口 → 返回 taskId                    │
│  轮询 /task/{taskId}/progress 或 WebSocket 推送 → 显示进度     │
│  完成后下载 /task/{taskId}/error-file（失败明细）              │
│  导出：调 /export 接口 → 同步小数据 / 异步大数据 → 下载 URL     │
└──────────────────────────────────────────────────────────────┘
                              │
┌──────────────────────────────────────────────────────────────┐
│ 后端 Excel 模块                                               │
│  ┌────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Controller │→ │ ImportService │→ │ ExcelListener(SAX)  │  │
│  │            │  │ ExportService │  │ 行级校验/转换/入库   │  │
│  └────────────┘  └──────────────┘  └──────────────────────┘  │
│         │               │                    │               │
│  ┌──────▼──────┐ ┌──────▼──────┐ ┌──────────▼───────────┐   │
│  │ 任务管理    │ │ 模板管理    │ │ 进度/日志（Redis+DB）│   │
│  │ ExcelTask   │ │ ExcelTemplate│ │ ExcelTaskDetail      │   │
│  └─────────────┘ └─────────────┘ └──────────────────────┘   │
│         │                                                    │
│  ┌──────▼──────────────────────────────────────────────┐     │
│  │ 业务层（各实体 Service，批量 saveBatch/updateBatch） │     │
│  └─────────────────────────────────────────────────────┘     │
└──────────────────────────────────────────────────────────────┘
```

---

## 四、数据库设计（新增 3 张表）

```sql
-- 1. 导入导出任务表
CREATE TABLE excel_task (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_no       VARCHAR(64)  NOT NULL COMMENT '任务编号(UUID)',
    biz_type      VARCHAR(64)  NOT NULL COMMENT '业务类型：attraction/hotel/food/product...',
    task_type     TINYINT      NOT NULL COMMENT '1=导入 2=导出',
    file_name     VARCHAR(255) COMMENT '原始文件名',
    file_path     VARCHAR(512) COMMENT 'OSS/本地存储路径',
    file_size     BIGINT       COMMENT '文件字节数',
    total_rows    INT DEFAULT 0 COMMENT '总行数(解析后)',
    success_rows  INT DEFAULT 0 COMMENT '成功行数',
    fail_rows     INT DEFAULT 0 COMMENT '失败行数',
    status        TINYINT DEFAULT 0 COMMENT '0待处理 1处理中 2成功 3部分失败 4失败 5已取消',
    error_file    VARCHAR(512) COMMENT '失败明细文件路径',
    error_msg     VARCHAR(2000) COMMENT '整体失败原因',
    params        JSON         COMMENT '导入参数(如覆盖策略、默认分类)',
    operator_id   BIGINT       COMMENT '操作人',
    start_time    DATETIME,
    end_time      DATETIME,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_biz_status (biz_type, status),
    KEY idx_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel导入导出任务表';

-- 2. 任务明细表（失败行记录，避免写爆主表）
CREATE TABLE excel_task_detail (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_no     VARCHAR(64) NOT NULL,
    row_index   INT         COMMENT 'Excel行号(从1开始，含表头)',
    row_data    TEXT        COMMENT '原始行JSON',
    error_field VARCHAR(128) COMMENT '出错字段',
    error_msg   VARCHAR(512) COMMENT '错误描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_task (task_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel任务明细/错误明细';

-- 3. 模板配置表（可选，支持动态模板）
CREATE TABLE excel_template (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    biz_type    VARCHAR(64) NOT NULL,
    template_name VARCHAR(128),
    file_path   VARCHAR(512) COMMENT '模板文件路径',
    version     INT DEFAULT 1,
    status      TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_biz (biz_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel模板配置表';
```

---

## 五、后端代码实现

### 5.1 依赖（pom.xml）

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.4</version>
</dependency>
```

### 5.2 核心注解与基类

**业务类型枚举**

```java
public enum ExcelBizType {
    ATTRACTION("attraction", "景点"),
    HOTEL("hotel", "酒店"),
    FOOD("food", "美食"),
    PRODUCT("product", "商品"),
    TICKET("ticket", "票务"),
    CITY("city", "城市"),
    POINTS_PRODUCT("points_product", "积分商品");
    // ... 按需扩展

    private final String code;
    private final String desc;
    // 构造 + getter
}
```

**通用导入行接口**

```java
public interface ImportRow {
    /** 行级校验，返回错误信息，null 表示通过 */
    List<String> validate();
    /** 转换为实体 */
    Object toEntity();
}
```

### 5.3 实体 Excel 映射（以景点为例）

```java
@Data
public class AttractionExcelRow implements ImportRow {

    @ExcelProperty("景点名称")
    @NotBlank(message = "景点名称不能为空")
    private String name;

    @ExcelProperty("所属城市")
    @NotBlank(message = "所属城市不能为空")
    private String cityName;   // 导入时按名称查 city_id

    @ExcelProperty("景点分类")
    private String categoryName;

    @ExcelProperty("门票价格")
    @NumberFormat("#.##")
    private BigDecimal price;

    @ExcelProperty("开放时间")
    private String openTime;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("简介")
    @ExcelProperty(index = 6) // 也可按列索引
    private String description;

    @ExcelIgnore
    private Long cityId;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(name)) errors.add("景点名称不能为空");
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) errors.add("门票价格不能为负");
        return errors;
    }

    @Override
    public Object toEntity() {
        Attraction a = new Attraction();
        BeanUtils.copyProperties(this, a);
        a.setCityId(cityId);
        a.setCreateTime(LocalDateTime.now());
        return a;
    }
}
```

### 5.4 通用导入监听器（核心）

```java
@Slf4j
public class GenericImportListener<T extends ImportRow> extends AnalysisEventListener<T> {

    private static final int BATCH_SIZE = 500;

    private final List<T> buffer = new ArrayList<>(BATCH_SIZE);
    private final ExcelTaskContext ctx;          // 任务上下文
    private final BiConsumer<List<T>, ExcelTaskContext> batchHandler; // 批量入库回调
    private final Function<T, List<String>> validator;                // 行校验

    public GenericImportListener(ExcelTaskContext ctx,
                                 BiConsumer<List<T>, ExcelTaskContext> batchHandler) {
        this.ctx = ctx;
        this.batchHandler = batchHandler;
        this.validator = T::validate;
    }

    @Override
    public void invoke(T row, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex() + 1; // Excel行号
        ctx.increaseTotal();

        // 1. 行级校验
        List<String> errors = validator.apply(row);
        if (errors != null && !errors.isEmpty()) {
            ctx.recordFail(rowIndex, row, String.join(";", errors));
            return;
        }

        buffer.add(row);

        // 2. 攒批入库
        if (buffer.size() >= BATCH_SIZE) {
            flush();
        }

        // 3. 进度上报（每 100 行）
        if (ctx.getTotal() % 100 == 0) {
            ctx.reportProgress();
        }
    }

    private void flush() {
        if (buffer.isEmpty()) return;
        try {
            batchHandler.accept(buffer, ctx);
            ctx.increaseSuccess(buffer.size());
        } catch (Exception e) {
            log.error("批量入库失败", e);
            // 整批回退为失败明细
            buffer.forEach(r -> ctx.recordFail(ctx.getTotal(), r, e.getMessage()));
        } finally {
            buffer.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        flush();          // 处理剩余
        ctx.finish();     // 标记完成
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析异常，行号={}", context.readRowHolder().getRowIndex(), exception);
        ctx.recordFail(context.readRowHolder().getRowIndex() + 1, null, exception.getMessage());
    }
}
```

### 5.5 任务上下文（进度与错误收集）

```java
@Slf4j
public class ExcelTaskContext {
    private final String taskNo;
    private final ExcelTaskService taskService;
    private final StringRedisTemplate redis;
    private final List<ExcelTaskDetail> errorDetails = new ArrayList<>();
    private final AtomicInteger total = new AtomicInteger(0);
    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger fail = new AtomicInteger(0);
    private volatile boolean cancelled = false;

    public void increaseTotal() { total.incrementAndGet(); }
    public void increaseSuccess(int n) { success.addAndGet(n); }

    public void recordFail(int rowIndex, Object row, String msg) {
        fail.incrementAndGet();
        ExcelTaskDetail d = new ExcelTaskDetail();
        d.setTaskNo(taskNo);
        d.setRowIndex(rowIndex);
        d.setRowData(JSON.toJSONString(row));
        d.setErrorMsg(msg);
        errorDetails.add(d);
    }

    /** 每100行更新Redis进度，前端轮询读取 */
    public void reportProgress() {
        Map<String, Object> p = Map.of(
            "total", total.get(), "success", success.get(), "fail", fail.get(),
            "status", 1, "percent", total.get() == 0 ? 0 : success.get() * 100 / total.get()
        );
        redis.opsForHash().putAll("excel:task:" + taskNo, p);
        redis.expire("excel:task:" + taskNo, Duration.ofHours(24));
    }

    public void finish() {
        if (cancelled) return;
        taskService.updateTaskResult(taskNo, total.get(), success.get(), fail.get(), errorDetails);
        reportProgress();
    }

    public void cancel() { this.cancelled = true; }
}
```

### 5.6 导入服务

```java
@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final ExcelTaskService taskService;
    private final AttractionService attractionService;
    private final CityService cityService;
    // ... 其他业务 Service

    @Async("excelExecutor")
    public void doImportAsync(String taskNo, MultipartFile file, ExcelBizType bizType, Long operatorId) {
        ExcelTaskContext ctx = taskService.createContext(taskNo);
        taskService.markProcessing(taskNo);

        try (InputStream in = file.getInputStream()) {
            switch (bizType) {
                case ATTRACTION -> importAttraction(in, ctx);
                // case HOTEL -> ...
                default -> throw new BizException("不支持的导入类型");
            }
        } catch (Exception e) {
            log.error("导入失败 taskNo={}", taskNo, e);
            taskService.markFailed(taskNo, e.getMessage());
            return;
        }

        // 生成失败明细文件
        if (ctx.getFail() > 0) {
            String errPath = taskService.exportErrorFile(taskNo, ctx.getErrorDetails());
            taskService.updateErrorFile(taskNo, errPath);
        }
        taskService.markDone(taskNo);
    }

    private void importAttraction(InputStream in, ExcelTaskContext ctx) {
        EasyExcel.read(in, AttractionExcelRow.class, new GenericImportListener<>(
            ctx, (rows, context) -> {
                // 1. 城市名称 → id 转换（一次批量查）
                Set<String> cityNames = rows.stream()
                    .map(AttractionExcelRow::getCityName).collect(Collectors.toSet());
                Map<String, Long> cityMap = cityService.findIdByNameIn(cityNames);

                List<Attraction> entities = new ArrayList<>(rows.size());
                for (AttractionExcelRow row : rows) {
                    Long cityId = cityMap.get(row.getCityName());
                    if (cityId == null) {
                        context.recordFail(0, row, "城市不存在：" + row.getCityName());
                        continue;
                    }
                    row.setCityId(cityId);
                    entities.add((Attraction) row.toEntity());
                }
                if (!entities.isEmpty()) {
                    attractionService.saveBatch(entities);   // MyBatis Plus 批量插入
                }
            }
        )).sheet().doRead();
    }
}
```

### 5.7 导出服务

```java
@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final AttractionMapper attractionMapper;
    // ... 其他 Mapper

    /** 同步导出（<1万行） */
    public void exportSync(HttpServletResponse response, ExcelBizType bizType, Query query) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(bizType.getDesc() + "_" + DateUtil.today(), UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), AttractionExcelRow.class)
                 .sheet("景点数据")
                 .doWrite(() -> streamData(bizType, query));  // 分页流式写
    }

    /** 分页流式取数，避免一次性加载 */
    private List<AttractionExcelRow> streamData(ExcelBizType bizType, Query q) {
        List<AttractionExcelRow> result = new ArrayList<>();
        long lastId = 0;
        int size = 1000;
        while (true) {
            List<Attraction> page = attractionMapper.selectByCursor(lastId, size, q);
            if (page.isEmpty()) break;
            page.forEach(a -> result.add(convert(a)));
            lastId = page.get(page.size() - 1).getId();
            if (page.size() < size) break;
        }
        return result;
    }

    /** 异步导出（大数据量） */
    @Async("excelExecutor")
    public void exportAsync(String taskNo, ExcelBizType bizType, Query query, Long operatorId) {
        String tmpPath = "/tmp/excel/export/" + taskNo + ".xlsx";
        EasyExcel.write(tmpPath, AttractionExcelRow.class)
                 .sheet(bizType.getDesc())
                 .doWrite(() -> streamData(bizType, query));
        taskService.updateFile(taskNo, tmpPath);
        taskService.markDone(taskNo);
    }
}
```

### 5.8 控制器

```java
@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelImportService importService;
    private final ExcelExportService exportService;
    private final ExcelTaskService taskService;

    /** 下载导入模板 */
    @GetMapping("/template/{bizType}")
    public void downloadTemplate(@PathVariable ExcelBizType bizType, HttpServletResponse resp) {
        ExcelTemplateUtil.writeTemplate(resp, bizType);
    }

    /** 上传并导入 */
    @PostMapping("/import/{bizType}")
    public R<ImportResultVO> importExcel(
            @PathVariable ExcelBizType bizType,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String params,
            @AuthenticationPrincipal User user) {

        // 1. 校验（类型/大小/后缀）
        ExcelFileValidator.validate(file);

        // 2. 创建任务
        String taskNo = taskService.createTask(bizType, file, user.getId(), params);

        // 3. 异步执行
        importService.doImportAsync(taskNo, file, bizType, user.getId());

        return R.ok(new ImportResultVO(taskNo, "任务已提交，请轮询进度"));
    }

    /** 查询进度 */
    @GetMapping("/task/{taskNo}/progress")
    public R<Map<String, Object>> progress(@PathVariable String taskNo) {
        return R.ok(taskService.getProgress(taskNo));
    }

    /** 下载失败明细 */
    @GetMapping("/task/{taskNo}/error-file")
    public void downloadErrorFile(@PathVariable String taskNo, HttpServletResponse resp) {
        taskService.writeErrorFile(taskNo, resp);
    }

    /** 导出 */
    @PostMapping("/export/{bizType}")
    public void export(@PathVariable ExcelBizType bizType,
                       @RequestBody(required = false) Query query,
                       HttpServletResponse resp) {
        exportService.exportSync(resp, bizType, query);
    }
}
```

### 5.9 线程池配置

```java
@Configuration
@EnableAsync
public class ExcelAsyncConfig {
    @Bean("excelExecutor")
    public ThreadPoolTaskExecutor excelExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(4);
        exec.setQueueCapacity(50);
        exec.setThreadNamePrefix("excel-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }
}
```

### 5.10 文件校验工具

```java
public class ExcelFileValidator {
    private static final Set<String> ALLOWED = Set.of("xls", "xlsx");
    private static final long MAX_SIZE = 50 * 1024 * 1024; // 50MB

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BizException("文件不能为空");
        if (file.getSize() > MAX_SIZE) throw new BizException("文件不能超过50MB");
        String ext = FileUtil.extName(file.getOriginalFilename());
        if (!ALLOWED.contains(ext.toLowerCase())) throw new BizException("仅支持 xls/xlsx");
        // 魔数校验，防伪造后缀
        try (InputStream in = file.getInputStream()) {
            byte[] head = in.readNBytes(4);
            if (!(head[0] == 0x50 && head[1] == 0x4B) && !(head[0] == (byte)0xD0))
                throw new BizException("文件格式非法");
        } catch (IOException e) {
            throw new BizException("文件读取失败");
        }
    }
}
```

---

## 六、前端交互（以 minecraft-admin-main 为例）

```vue
<script setup lang="ts">
import { ref } from 'vue';
import { uploadExcel, getProgress, downloadErrorFile } from '@/api/excel';

const bizType = ref('attraction');
const progress = ref({ total: 0, success: 0, fail: 0, percent: 0, status: 0 });
let timer: number;

async function handleUpload(file: File) {
  const { taskNo } = await uploadExcel(bizType.value, file);
  pollProgress(taskNo);
}

function pollProgress(taskNo: string) {
  timer = window.setInterval(async () => {
    const p = await getProgress(taskNo);
    progress.value = p;
    if (p.status >= 2) {       // 完成/失败
      clearInterval(timer);
      if (p.fail > 0) downloadErrorFile(taskNo);
    }
  }, 1000);
}
</script>

<template>
  <a-upload :before-upload="handleUpload" accept=".xls,.xlsx" :show-upload-list="false">
    <a-button type="primary">上传导入</a-button>
  </a-upload>

  <a-progress :percent="progress.percent" />
  <div>总计 {{ progress.total }}，成功 {{ progress.success }}，失败 {{ progress.fail }}</div>
</template>
```

---

## 七、支持的实体范围（首批落地）

| 业务类型 | 导入 | 导出 | 说明 |
|---------|------|------|------|
| attraction 景点 | ✅ | ✅ | 城市名→city_id 转换 |
| hotel 酒店 | ✅ | ✅ | 含房型子表可选 |
| food 美食 | ✅ | ✅ | 菜系字典转换 |
| product 商品 | ✅ | ✅ | 分类+库存 |
| ticket 票务 | ✅ | ✅ | 出发/到达站 |
| city 城市 | ✅ | ✅ | 关联国家 |
| points_product 积分商品 | ✅ | ✅ | 积分单价校验 |
| comment 评论 | ❌ | ✅ | 只导出 |
| orders 订单 | ❌ | ✅ | 只导出，敏感字段脱敏 |

---

## 八、关键设计要点

### 8.1 幂等与去重
- 任务创建时 `task_no` 唯一，Redis `SETNX excel:lock:{bizType}:{hash}` 防重复提交。
- 导入行按业务唯一键（如景点名+城市）判重，策略可配：**跳过 / 覆盖 / 报错**（`params` 参数传入）。

### 8.2 事务控制
- **不**对整批开启大事务（100 万行会锁死）。采用**按 500 行分批提交**，失败行记录到 `excel_task_detail`，整体状态标记「部分失败」。
- 若业务要求强一致（如财务），则整批事务 + 失败回滚，代价是内存/锁压力，需在 `params` 中显式声明 `atomic=true`。

### 8.3 大数据量导出
- 使用 `EasyExcel.write(...).doWrite(() -> cursorPage())` 的 **Supplier 模式**，配合游标分页（`id > lastId`），内存恒定在 O(批大小)。
- 导出文件 > 20MB 时转异步任务，完成后返回 OSS 预签名 URL。

### 8.4 安全
- 上传接口加 `@PreAuthorize("hasRole('ADMIN')")` 或权限点 `excel:import:attraction`。
- 导出脱敏：手机号 `138****1234`、身份证 `110***********1234`，通过 `@ExcelProperty(converter=MaskConverter.class)`。
- 文件名、Sheet 名做 XSS/路径穿越过滤（`../`、`<>`）。
- 复用 Druid wall filter 防 SQL 注入；导入值做长度/枚举/正则校验。

### 8.5 可观测
- 全链路日志：`taskNo` 贯穿 MDC，`logback` 按 taskNo 输出。
- 操作日志记录到 `operation_log` 表（复用现有日志体系）。
- 失败明细单独文件 `errors_{taskNo}.xlsx`，列 = 原行 + `错误行号` + `错误信息`，用户可修正后重新上传。

### 8.6 限流与并发
- 线程池 `maxPoolSize=4`，超载走 `CallerRunsPolicy` 或直接返回「系统繁忙」。
- 同一操作人同时只允许 1 个进行中任务（Redis 计数）。

---

## 九、目录结构（新增文件）

```
Minecraft/src/main/java/com/minecraft/
├── excel/
│   ├── annotation/        # @ExcelBizType 等
│   ├── config/            # ExcelAsyncConfig
│   ├── controller/        # ExcelController
│   ├── converter/         # MaskConverter、字典转换器
│   ├── core/              # GenericImportListener、ExcelTaskContext、ExcelFileValidator
│   ├── enums/             # ExcelBizType、TaskStatus
│   ├── model/             # 各实体 ExcelRow（AttractionExcelRow...）
│   ├── service/           # ExcelImportService、ExcelExportService、ExcelTaskService
│   └── util/              # ExcelTemplateUtil、EasyExcelUtil
└── mapper/
    └── ExcelTaskMapper.java / ExcelTaskDetailMapper.java
```

---

## 十、落地步骤建议

1. **第 1 周**：建表 + 通用核心（Listener/Context/校验/任务服务）+ 景点导入导出打通。
2. **第 2 周**：补齐酒店/美食/商品/票务/城市 5 个高频实体，前端管理端接入进度轮询。
3. **第 3 周**：异步导出 + OSS 存储 + 失败明细下载 + 脱敏 + 权限点。
4. **第 4 周**：压测（10 万行导入 < 60s，导出 50 万行 < 3min）、日志与监控接入、文档。

---

## 十一、接口清单汇总

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/excel/template/{bizType}` | 下载导入模板 |
| POST | `/api/excel/import/{bizType}` | 上传并异步导入，返回 taskNo |
| GET | `/api/excel/task/{taskNo}/progress` | 查询进度 |
| GET | `/api/excel/task/{taskNo}/error-file` | 下载失败明细 |
| POST | `/api/excel/export/{bizType}` | 同步/异步导出 |
| GET | `/api/excel/task/list` | 任务列表（管理端） |
| POST | `/api/excel/task/{taskNo}/cancel` | 取消任务 |

该模块与现有 Spring Boot 3 + MyBatis Plus 技术栈完全兼容，复用现有权限、日志、OSS 与 Redis 能力，可按业务类型逐步扩展映射类，无需改动核心框架。