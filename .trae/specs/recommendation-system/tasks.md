# Smart Recommendation System - Implementation Plan

> Mapping of Acceptance Criteria to work: AC-1→T1; AC-4/AC-12→T3; AC-5→T4; AC-2→T5; AC-3/AC-6(exposure)→T6; AC-6(analytics)→T7; AC-7→T8; AC-8→T9; AC-9/AC-13→T10; AC-10→T11; AC-11→T2/T12; AC-1(compatibility seed)→T1.
>
> Backend package root: `com.minecraft.recommendation` (subpackages `config`, `entity`, `mapper`, `enums`, `dto`, `algorithm`, `service`, `controller`; entities/mappers that must sit in standard packages go under `com.minecraft.entity` / `com.minecraft.mapper` per convention).
>
> SQL: `Minecraft/src/main/resources/static/sql/recommendation_system.sql` (idempotent `CREATE TABLE IF NOT EXISTS` + upsert seed).

## Task 1: Database migration — recommendation schema and seed defaults
- **Status**: `completed`
- **Priority**: high
- **Depends On**: None
- **Completion Evidence**:
  - TR-1.1: Created `Minecraft/src/main/resources/static/sql/recommendation_system.sql`; executed twice against local MySQL 8.0 (db_minecraft) — both exit code 0.
  - TR-1.2: information_schema confirms 8 tables; 41 seed config rows (17 GLOBAL + 6×4 CATEGORY); FKs fk_attr_rec_item→attraction, fk_hotel_rec_item→hotel, fk_rest_rec_item→food, fk_souv_rec_item→product.
  - TR-1.3: Parent-row delete blocked with ERROR 1451 (23000) FK constraint fails; test child row removed afterwards.
- **Description**:
  - Create `recommendation_system.sql` for MySQL 8, re-runnable.
  - Four item tables: `attractions_recommendations` (FK `attraction(id)`), `hotels_recommendations` (FK `hotel(id)`), `restaurants_recommendations` (FK `food(id)`), `souvenirs_recommendations` (FK `product(id)`). Each: `id BIGINT AI PK`, `item_id BIGINT NOT NULL`, `recommendation_score DECIMAL(10,4)`, `user_preference_matching DECIMAL(8,4)` (latest aggregate/content affinity contribution), `popularity_index DECIMAL(8,4)`, `collaborative_score`, `content_score`, `seasonal_score`, `quality_score` DECIMAL(8,4), `is_featured TINYINT(1) DEFAULT 0`, `feature_weight DECIMAL(8,4) DEFAULT 0`, `status TINYINT DEFAULT 1` (1 active, 0 excluded), `created_at`/`updated_at` TIMESTAMP, `update_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`, `UNIQUE KEY uk_item_id(item_id)`, indexes on scores/status, FK `ON DELETE RESTRICT ON UPDATE RESTRICT`.
  - Supporting: `recommendation_config` (id; scope ENUM('GLOBAL','CATEGORY'); category VARCHAR(32) NULL; param_key VARCHAR(64); param_value VARCHAR(225); value_type VARCHAR(16); description; updated_by BIGINT NULL; created_at/updated_at; UNIQUE(scope,category,param_key)); `recommendation_item_similarity` (id, category VARCHAR(32), item_id, neighbor_id, similarity DECIMAL(8,4), updated_at, UNIQUE(category,item_id,neighbor_id), indexes); `recommendation_job_log` (id, category VARCHAR(32) NULL, trigger_type VARCHAR(16) ('SYSTEM'/'MANUAL'), operator_id BIGINT NULL, status VARCHAR(16) ('RUNNING'/'SUCCESS'/'FAILED'), item_count INT, duration_ms BIGINT, error_message TEXT, start_time/end_time); `recommendation_exposure_log` (id, user_id BIGINT NULL, category VARCHAR(32), item_id BIGINT, score DECIMAL(10,4), rank_position INT, source VARCHAR(16), request_id VARCHAR(48), created_at; indexes (created_at),(category,created_at),(user_id,created_at)) — no FK (high-write log).
  - Seed default config rows (scope/scope+category) for keys defined in `RecommendationDefaults`: per-category factor weights (collaborative 0.25/content 0.25/popularity 0.2/seasonal 0.1/quality 0.2), strategy=HYBRID; global behavior action weights (like 1/collect 3/comment 2/cart 4/order 6; paid/completed order bonus), cf.neighbor.k=20, behavior.recency.days=90, popularity.log.base=10, diversity.strength=0.3, featured.boost=0.15, cold.start=POPULAR, exposure.enabled=true, exposure.sample.rate=1.0, exposure.attribution.hours=72, retention.days=30, schedule.enabled=false, schedule.cron='0 0 3 * * ?'.
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `rule` TR-1.1: Script runs twice against a db_minecraft database without error; evidence = execution output.
  - `rule` TR-1.2: Each of four tables contains required columns/unique key/FK (verified via information_schema or SHOW CREATE TABLE screenshots/output); supporting tables and seeded config row count exist; evidence = verification query output.
  - `rule` TR-1.3: Deleting/updating a referenced item is restricted by FK; evidence = query result.
- **Notes**: Use utf8mb4/InnoDB/RIGHT naming matching existing dump. Keep `update_timestamp` exactly as named in the request in addition to `updated_at`.

## Task 2: Backend foundation — enums, entities, mappers, defaults
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T1
- **Completion Evidence**:
  - TR-2.1: `.\mvnw.cmd -q compile -DskipTests` exit 0 (no warnings/errors).
  - Created enums (RecommendCategory with code/itemTable/recommendationTable mapping + 400 on unknown; RecommendStrategy; JobTriggerType; JobStatus; ExposureSource), 9 entities under `com.minecraft.entity.recommendation` (BaseRecommendationItem + 4 concrete @TableName + config/similarity/joblog/exposure), common `RecommendationItemMapper<T>` + 4 mappers + config/similarity/joblog/exposure/aggregation mappers, 7 XML files under `resources/mapper/`, `RecommendationDefaults` (17 GLOBAL + 6 CATEGORY specs, seedConfigs), `RecommendationSchedulingConfig` (ThreadPoolTaskScheduler bean), `RecommendationMapperRegistry` (category→mapper/entity factory, type-safe upsert).
  - Aggregation XML columns verified against actual entities: like_record/collection (user_id,item_id,item_type,create_time), comment (+rating,status=1), cart/orders (same + orders status IN ('1','3')).
  - TR-2.2/TR-2.3 deferred to T11 unit tests (enum unit test + mapper XML parsing via test context).
- **Description**:
  - `enums/RecommendCategory` (ATTRACTION→attraction/`attraction` table; HOTEL→hotel/`hotel`; RESTAURANT→food/`food`; SOUVENIR→product/`product`; valueOf-or-throw 400 helper), `RecommendStrategy` (HYBRID/POPULAR/CONTENT/COLLABORATIVE), `JobTriggerType`, `JobStatus`, `ExposureSource` (PERSONALIZED/POPULAR/FEATURED/COLD_START).
  - Entities (MyBatis-Plus, Lombok `@Data`): abstract `BaseRecommendationItem` (no `@TableName`, shared columns) + four concrete entities with `@TableName` plural table names; `RecommendationConfig`, `RecommendationItemSimilarity`, `RecommendationJobLog`, `RecommendationExposureLog`.
  - Mappers: four `BaseMapper<...>` interfaces + XML with `upsertItems` (`INSERT ... ON DUPLICATE KEY UPDATE` excluding manual columns and never overwriting `is_featured/feature_weight/status`), `selectScoredPage` (join-free paged select ordered by score), manual update statements; `RecommendationConfigMapper` (XML select by scope/category), `RecommendationItemSimilarityMapper` (batch upsert + delete-by-category + select neighbors), `RecommendationJobLogMapper` (insert/update/page), `RecommendationExposureLogMapper` (batch insert async, aggregate queries, purge).
  - Add aggregation SQL to item/behavior mappers (or dedicated `RecommendationAggregationMapper`) returning per-item counts in one grouped query each: likes/collects/comments by item_type; cart adds and paid/completed orders by item_type/status with optional recency columns (current-month, attribution-window use via parameters).
  - `algorithm/RecommendationDefaults`: typed key constants + default map used for seading/fallback/reset.
  - Enable scheduling via `@EnableScheduling` on a new `config/RecommendationSchedulingConfig` (task executor/`ThreadPoolTaskScheduler` bean).
- **Acceptance Criteria Addressed**: AC-1, AC-11
- **Test Requirements**:
  - `rule` TR-2.1: Project compiles (`mvn -q -DskipTests compile`); evidence = build output.
  - `rule` TR-2.2: Category enum resolves four codes/tables and throws BusinessException(400) for unknown; evidence = unit test.
  - `rule` TR-2.3: Mapper XML statements are syntactically valid (mybatis-spring parses them during a mapper-only unit test or Spring context smoke test with mocked DataSource); evidence = test run.
- **Notes**: Follow `FoodMapper`/`Food` style; map underscore columns via existing camel-case config.

## Task 3: Hybrid algorithm core (pure, deterministic)
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T2
- **Completion Evidence**:
  - Implemented in `com.minecraft.recommendation.algorithm`: Normalizers (min-max/log compression/cosine/jaccard/exp decay/clamp), FeatureTokenizer (中英文分隔符), SeasonalAnalyzer (四季/春夏秋冬 + word-boundary english incl. fall/all fix + 行为侧月度比例), ContentProfileBuilder (namespaced item vectors, weighted user affinity, populationAffinity vs full population vector), ItemSimilarityCalculator (weighted user-item matrix, cosine co-interaction, symmetric Top-K edges, cutoff, predict weighted-neighbor CF score), PopularityBuilder (weighted counts→log→min-max), QualityScore, StrategyParameters (auto-normalized weights, presets, K/boost clamps), HybridRanker (global/personalized paths, fallback, featured pin/boost, O(n log n) sort + bounded MMR diversity, deterministic tie-breaks, limit clamp 100).
  - 62 unit tests across 9 test classes, all green (`mvn test`: Tests run: 62, Failures: 0, Errors: 0).
  - Covers TR-3.1 ([0,1] bounds/empty/single/zero), TR-3.2 (weight sensitivity + presets), TR-3.3 (5-run determinism), TR-3.4 (cold non-empty/excluded/featured), TR-3.5 (K/symmetry/cutoff), TR-3.6 scenarios (strong-tag user, dominant-item log compression mid .32 vs linear .01, diversity reorder).
- **Description**:
  - In package `com.minecraft.recommendation.algorithm`: `ItemFeatures` (id, category, tags token set, city, type/cuisine/star, rating, price, season text, raw counts, monthly counts), `UserBehavior` (itemId, action weight, timestamp), `ScoredCandidate` (id, component map, final score, tags).
  - `Normalizers`: min-max with empty/equal-edge handling; log compression `ln(1+x)/ln(base)`; vector cosine; zero-vector handling.
  - `SeasonalAnalyzer`: month/keyword parsing for `season`/`best_season` text (四季皆宜→1.0 baseline, season match to current month bands) blended with current-month interaction ratio for categories without season text.
  - `ContentProfileBuilder`: item tag/category token vectors; user affinity vector (time-decayed, action-weighted tag accumulation); content match = cosine.
  - `ItemSimilarityCalculator`: user–item weighted interaction matrix (recency-decayed actions), item-item cosine co-interaction; emits top-K neighbor edges; complexity documented O(E + Σ_u m_u² + n·k log k).
  - `HybridRanker`: immutable `StrategyParameters` (weights normalized at construction, k, diversity strength, featured boost, strategy); computes for each candidate collaborative score (neighbor expansion over user's strong items), content score, stored popularity/seasonal/quality; cold-start/anonymous path uses stored global score (popularity+seasonal+quality+featured); strategy presets override weights; featured pin/boost + exclusion input; greedy tag-diversity re-order; returns ranked list; one linear scoring pass + sort (O(n log n), heap option for limit k).
  - Popularity builder used by recalc: weighted action counts (denormalized item counts + cart/order aggregates) with log compression and min-max normalization.
- **Acceptance Criteria Addressed**: AC-4, AC-12
- **Test Requirements**:
  - `rule` TR-3.1: All components/finals within [0,1] on randomized and edge-case (empty, single-item, zero-interaction) inputs; evidence = unit tests.
  - `rule` TR-3.2: Weight sensitivity: increasing a factor's weight moves items strong on that factor upward; strategy presets COLLABORATIVE vs POPULAR produce expected order; evidence = tests.
  - `rule` TR-3.3: Determinism: same inputs → identical output across repeated runs; evidence = test.
  - `rule` TR-3.4: Cold/anonymous path never returns empty while active items exist; excluded items removed; featured pinned first; evidence = test.
  - `rule` TR-3.5: ItemSimilarityCalculator respects K, symmetric affinity, ignores interactions outside recency window; evidence = test.
  - `rubric` TR-3.6: Recommendation quality dimension; scale 1-5; anchors per AC-12; threshold >= 4; evidence = scenario-based tests (user with strong tag history vs new user; dominant-item log compression; diversity ordering).

## Task 4: Configuration service — typed parameters, validation, cache, reset
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T2
- **Completion Evidence**:
  - `RecommendationConfigService` extends ServiceImpl: two-level (GLOBAL/CATEGORY) snapshot with in-memory L1 (60s) + Redis (`recommendation:config`, 300s, optional bean + try/catch 降级), category→global→code-default fallback, typed getters, `strategyParameters(category)` normalized.
  - 全量校验后落库（all-or-nothing @Transactional）：scope/category/key registry, NUMBER 按键范围（权重/多样性/boost/采样率 0..1，k 1..200 且整数，天数区间，log base >1，行为权重 0..100），BOOLEAN，STRATEGY 枚举，6 段 Cron（Spring CronExpression，'?'→'*' 兼容 Quartz）；updateBatch/resetToDefaults 记录 updated_by、失效缓存、发布 RecommendationConfigChangedEvent（scheduleAffected 标记）。
  - 新增 RecommendationConfigMapper.selectByKey + XML。
  - 10 个 Mockito 单测全部通过（含缓存命中/失效、回退顺序、13 种非法输入不落库、重置 17 全局/6 分类行、事件标记）。
- **Description**:
  - `RecommendationConfigService`: load all rows into a typed snapshot; `get(scope,category,key)` with default fallback GLOBAL→default; `StrategyParameters forCategory(...)`; Redis cache key `recommendation:config` with short TTL; invalidation on writes.
  - Validation: numeric ≥ 0 (weights), ranges (0..1 where applicable, k 1..200, days 1..730, sample rate 0..1, cron via Spring/CronExpression or Quartz cron parser compatible with 6/7-field), known key registry with type metadata; unknown key rejected; batch update all-or-nothing semantics with per-field errors.
  - Admin methods: list (global + per category view), update batch, reset scope/category to defaults (operator id recorded).
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `rule` TR-4.1: Valid updates persist, evict cache, and subsequent reads return new values; invalid (negative, non-numeric, unknown key, bad cron) → BusinessException(400) and no partial persistence; evidence = Mockito tests.
  - `rule` TR-4.2: Reset restores seed defaults; category falls back to global/default when key absent; evidence = tests.
  - `rule` TR-4.3: Factor weights need not sum to 1 (normalized in StrategyParameters); evidence = test.

## Task 5: Recalculation orchestration service
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T3, T4
- **Completion Evidence**:
  - `RecommendationRecalcService`：分类锁+ALL 锁 tryLock 快速失败 409；单分类流水线 = 在架物品加载（Attraction/Hotel.facilities/Food/Product 适配器）→ 集合式行为聚合（recency 窗口加权行为 + 全量 cart/order grouped counts + 当月计数）→ 热度(log+minmax)/品质/季节/内容完整度/群体亲和/协同中心度分量 → Item-CF 边整类替换（deleteByCategory + 500 批量 upsert）→ 五因子综合分 → 500 批量 upsert 保留人工列 → 匿名列表缓存键清理；RUNNING/SUCCESS/FAILED job_log（itemCount/duration/error 截断）；MANUAL 失败抛 500，SYSTEM 失败记录后继续；recalculateAll 固定四分类顺序 + ALL 汇总日志。
  - similarity mapper 新增 deleteByCategory；新增 RecalcResult、RecommendationCacheKeys。
  - 7 个 Mockito 单测全绿（成功装配/分值区间/人工列不覆盖、空目录、MANUAL/SYSTEM 失败分流、null→全量汇总、手动失败即停、并发 409 CountDownLatch 验证）。
- **Description**:
  - `RecommendationRecalcService.recalculate(categoryNullable, triggerType, operatorId)`: guard concurrency (per-category lock + ALL lock; fail fast with BusinessException 409/“任务正在运行”); create RUNNING log; load active items + behavior aggregates via set-based mapper queries; build ItemFeatures; compute components (popularity/quality/seasonal); build item similarity edges (replace category edges); compute stored global `recommendation_score` and aggregate `user_preference_matching` (mean content affinity over a sample of active users or tag-popularity affinity proxy, documented); batch upsert preserving manual columns; finish SUCCESS/FAILED log with counts/duration/error; clear query caches.
  - `@Async` capable executor reuse; expose synchronous service method (controller decides).
  - Logging at start/end (category, counts, ms) and error with stack trace in logs only.
- **Acceptance Criteria Addressed**: AC-2, AC-11
- **Test Requirements**:
  - `rule` TR-5.1: With mocked mappers, verifies feature assembly, upsert payload bounds/columns, similarity replace, cache eviction, job log SUCCESS fields; evidence = test.
  - `rule` TR-5.2: Manual columns preserved — preseed rows with is_featured/feature_weight/status survive a recalc (upsert SQL audit + integration-style test with mocked upsert capture); evidence = test.
  - `rule` TR-5.3: Concurrent second invocation for same category rejected while first RUNNING; failure path writes FAILED log and rethrows as BusinessException(500) for manual triggers; evidence = tests.
  - `rule` TR-5.4: `recalculate(null,...)` runs all four categories in fixed order and returns aggregate summary; evidence = test.

## Task 6: Public query API + personalized serving + exposure capture
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T5
- **Completion Evidence**:
  - 新增 `RecommendationItemCatalog`（四类源表轻量投影，Hotel 设施→标签）、`ItemDetail`、`RecommendationItemVO`/`RecommendationResultVO`；registry 增加在架 Top/分页/单查等原始 QueryWrapper 助手；similarity mapper 增加 `selectNeighborsByItems`（单次 IN，避免 N+1）；aggregation mapper 增加单用户五表 UNION 行为查询 `selectUserInteractions`。
  - `RecommendationQueryService`：匿名全局（Redis 120s 缓存，键含分类/城市/条数）vs 登录个性化（recency 窗口行为 + 行为权重×指数衰减 + Item-CF predict + 内容画像余弦，五因子排序，无信号自动回退 COLD_START）；城市过滤、显式季节覆盖、置顶/加权/MMR 多样性复用 HybridRanker；limit 1..100 钳制、候选池≤200；相关推荐 = 相似度边→下架剔除→标签 Jaccard 兜底（不存在物品 404）。
  - 曝光链路：`RecommendationExposureAsyncWriter`（独立 Bean @Async，异常吞掉）+ `RecommendationExposureService`（开关/采样率/requestId/排名/分数组装，永不阻断）。
  - `PublicRecommendationController` `/api/recommendations/{category}` 与 `/{category}/{itemId}/related`，中文 @Tag/@Operation，匿名可读，SecurityUtils 解析登录态。
  - 22 个单测全绿（Query 10 + Exposure 5 + Writer 3 + Controller 4）：匿名/城市/冷启动/共现反超/limit 边界/相关边+标签兜底/404/空目录/曝光开关采样/异常吞咽。
- **Description**:
  - `RecommendationQueryService.list(category, userId, limit, city, season)`: load scored rows + item details (via existing item mappers/services minimal projections — id,name,city/cover image/price/rating/tags/status); anonymous/COLD path vs personalized (load recent user behavior across like/collect/comment/cart/orders within recency window; build user vectors; HybridRanker); city filter; exclude status=0 and items with missing item rows; pinned featured items first; diversity; Redis cache only for anonymous global list (per category, short TTL); personalized not cached; return source/strategy metadata.
  - `related(category, itemId, limit)`: similarity neighbors with content-tag fallback; same projection.
  - `ExposureService`: request id; build exposure rows (rank, score, source, userId nullable); async batched insert honoring enabled/sample-rate; never blocks or fails the request (try/catch + debug log).
  - `PublicRecommendationController` `/api/recommendations`: `GET /{category}` and `GET /{category}/{itemId}/related`; `@Tag/@Operation` Chinese annotations; `ApiResponse` envelope; 400 on bad category; permitAll-compatible (no auth annotation).
  - VOs: `RecommendationItemVO`, `RecommendationResultVO` (source, strategy, generatedAt, items).
- **Acceptance Criteria Addressed**: AC-3, AC-6
- **Test Requirements**:
  - `rule` TR-6.1: Anonymous vs user-with-behavior produce different orders; cold user = global; featured pinned; excluded filtered; invalid category 400; evidence = controller/service tests.
  - `rule` TR-6.2: Related returns neighbors then content fallback for an item with no edges; unknown item → 404; evidence = tests.
  - `rule` TR-3.6 linkage TR-6.3: Exposure rows are emitted asynchronously with correct rank/source and failures swallowed; evidence = test (executor captured synchronously via Mockito).
  - `rule` TR-6.4: City filter and limit boundary (>100 clamp, default 10) enforced; evidence = test.

## Task 7: Analytics service + endpoints data
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T6
- **Completion Evidence**:
  - 新增 `RecommendationAnalyticsMapper` + XML：窗口内曝光次数/去重用户/去重物品、score×10 十分位桶（LEAST 上限 10）、曝光 Top N、曝光归因 SQL（行为表 JOIN 曝光日志，user/item 精确匹配 + 行为发生在曝光后 attributionHours 内；互动=点赞/收藏/评论(评论 status=1)，转化=加购/订单(status IN 1,3)；匿名曝光因 user_id NULL 自然排除）。
  - `RecommendationAnalyticsService`：dashboard(category 可空=ALL，窗口默认 7 天/上限 90) 装配覆盖率/互动率/转化率（除零归零，比率上限 1）、补零的 11 桶分数分布、Top10 物品（单分类装配名称城市，ALL 仅 ID）、任务统计（ALL 视图只统计 ALL 汇总日志避免重复计数；成功/失败/运行中数、平均耗时、最近状态与时间）；`purgeExpired()` 按 retention.days 分批 1000、上限 200 批。
  - catalog 增加 countActive；7 个单测全绿（单分类全指标、ALL 四分类累加、非法分类 400、窗口钳制、零曝光除零、清理多批/零行）。
- **Description**:
  - `RecommendationAnalyticsService`: overall + per-category overview (exposures, unique users, attributed likes/collects/cart/orders within attribution window after first exposure, proxy CTR = attributed interactions/exposures, conversion = attributed orders/exposures, coverage = scored active items / active catalog, score distribution buckets, top-N converting items); job run stats (last run, success/failure counts, avg duration).
  - Exposure purge `purge(retentionDays)` deleting in batches; exposed to scheduler.
  - Mapper XML aggregate queries with parameterized window/category; attribution excludes self-exposure rows with null user (counts only identifiable users) — document.
  - VOs: `AnalyticsOverviewVO`, `CategoryAnalyticsVO`, `TopItemVO`, `ScoreDistributionVO`.
- **Acceptance Criteria Addressed**: AC-6
- **Test Requirements**:
  - `rule` TR-7.1: Aggregation assembly maps mapper rows into VOs correctly (zero-row case included); evidence = test.
  - `rule` TR-7.2: Attribution window boundary (interaction just inside/outside hours) enforced by query parameters; purge called with configured days and batched; evidence = tests.
  - `rule` TR-7.3: Coverage math correct with mocked catalog/scored counts; evidence = test.

## Task 8: Dynamic scheduling
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T5, T7
- **Completion Evidence**:
  - `RecommendationScheduleManager`：@PostConstruct 注册；@EventListener 仅在 scheduleAffected 时 reschedule；启用/停用/换 Cron 动态替换 ScheduledFuture（cancel(false)）；CronTrigger + SimpleTriggerContext 计算 nextRunTime；Cron 六段校验（'?'→'*'），非法/读配置失败均安全不外抛；任务体 = recalc(all, SYSTEM) + purgeExpired，各自 try/catch 隔离。
  - 12 个单测全绿（启用注册/停用不注册/非法 Cron/配置读取失败/重排取消旧 future/任务体执行顺序/重算异常仍清理/清理异常不外抛/事件响应分流/future done 状态/nextRunTime 时间）。
- **Description**:
  - `RecommendationScheduleManager`: holds `ScheduledFuture<?>`; on startup and on config change schedules/cancels a cron task via `ThreadPoolTaskScheduler` + Spring `CronTrigger`; task runs recalc(all, SYSTEM) then purge; reports next execution time; validates 6-field cron; `schedule.enabled`/`schedule.cron` reactive after admin update (called by config service).
  - `GET /admin/recommendations/schedule` returns {enabled,cron,nextRunTime,lastRunSummary}; `PUT` updates config keys and reschedules immediately.
- **Acceptance Criteria Addressed**: AC-7
- **Test Requirements**:
  - `rule` TR-8.1: With mocked TaskScheduler, enable→scheduled, disable→cancelled, cron change→cancelled+rescheduled; invalid cron → 400 with no state change; evidence = tests.
  - `rule` TR-8.2: Triggered runnable invokes recalc(all) then purge and never throws to scheduler (errors logged); evidence = test.

## Task 9: Admin authorization + admin REST controller
- **Status**: `completed`
- **Priority**: high
- **Depends On**: T4, T5, T7, T8
- **Completion Evidence**:
  - `AdminAuthorizationService.requireAdmin()` 唯一鉴权入口（null 主体 401/用户不存在 401/permissions≠'0' → 403，通过返回 userId）。
  - `AdminRecommendationService` + `AdminRecommendationController` `/api/admin/recommendations`：config 列表/批量更新/重置（事务）、items 分页（registry.selectScoredPage + 源物品名称装配）、`PUT items/{category}/{itemId}/feature`（置顶/加权/状态校验 400，无推荐行 404）、`POST /recalc`（MANUAL 同步返回 RecalcResult，FAILED→500 由服务抛出）、jobs 最近日志、analytics 看板、schedule 状态；每个控制器入口首行强制 requireAdmin；DTO `RecommendationFeatureParam`，中文 @Tag/@Operation。
  - registry 增加 updateManualFields/selectScoredPage；新增 `AdminRecommendationItemVO`/`RecommendationScheduleStatusVO`。
  - 26 个单测全绿（鉴权 4 + 管理服务 14 + 控制器 8）：401/403 分流、参数服务透传、干预字段校验矩阵、404、重算分类解析、作业/分析/调度装配、未授权不触达业务。
- **Description**:
  - `AdminAuthorizationService.requireAdmin()`: resolve `SecurityUtils.getCurrentUserId()`; null → BusinessException(401), load `UserService.getById`; `permissions != '0'` → BusinessException(403). Single chokepoint invoked first in every admin endpoint.
  - `AdminRecommendationController` `/api/admin/recommendations`: config list/update/reset; scored items paged list (category, keyword, page); feature/boost/exclude update (`PUT /items/feature`); `POST /recalculate` (body {category?} → manual trigger, returns job log); jobs paged list; analytics overview/category/top-items; schedule get/put.
  - Request DTOs with bean validation (`@NotNull`, ranges); Swagger annotations; ApiResponse envelope; clear Chinese messages.
- **Acceptance Criteria Addressed**: AC-5, AC-8, AC-2(trigger)
- **Test Requirements**:
  - `rule` TR-9.1: No-token→401, non-admin→403, admin passes for representative write endpoints (MockMvc standalone or direct calls with SecurityContext set); public GET remains unguarded; evidence = tests.
  - `rule` TR-9.2: Bean validation failures map to 400 via GlobalExceptionHandler; feature update validates category/item existence (missing → 404); evidence = tests.
  - `rule` TR-9.3: Manual recalc endpoint returns job log DTO and maps FAILED job to 500; evidence = test.

## Task 10: Admin console — API module, routes, i18n, four views
- **Status**: `pending`
- **Priority**: high
- **Depends On**: T9
- **Description**:
  - `apps/web-antdv-next/src/api/management/recommendation/index.ts`: get/update/reset config, paged items, setFeature, recalc, jobs, analytics endpoints, schedule get/put (requestClient style matching existing modules; typed light interfaces).
  - New route module `router/routes/modules/recommendation.ts`: group `path:/recommendation` icon `lucide:sparkles`, order ~950; children: dashboard (`lucide:layout-dashboard`), params (`lucide:sliders-horizontal`), items (`lucide:list-checks`), schedule (`lucide:calendar-clock`).
  - Locale files `locales/langs/{zh-CN,en-US}/recommendation.json` registered in `locales/index.ts`.
  - Views under `views/recommendation_management/`:
    - `dashboard.vue`: four category `Card`s (Statistic: coverage %, item count, avg score, last run status/at), Progress/Table for score distribution and top converting items, exposure/CTR/conversion metrics, recent job runs table; auto/manual refresh.
    - `params.vue`: category Select/Tabs; factor weights as InputNumber + Slider with sum hint; strategy Select; advanced section (behavior action weights, K, recency, log base, diversity, featured boost, exposure settings); Save (batch), Reset (Popconfirm); loading/error states.
    - `items.vue`: category tabs; server-paged Table (item name, city, final + component scores, featured Switch, featureWeight InputNumber, status Switch exclude, updateTimestamp); "recalc this category" and "recalc all" buttons with confirm; keyword search.
    - `schedule.vue`: enable Switch, cron Input with human hint + next-run display, Save; jobs Table (trigger, operator, status Tag, counts, duration, start/end, error expandable).
  - Follow food.vue patterns: `Page`, antdv-next imports, `@vben/icons` lucide components, `message`/`Modal` feedback, `$t`. No new npm deps; no `i-lucide-*` classes.
- **Acceptance Criteria Addressed**: AC-9, AC-13
- **Test Requirements**:
  - `rule` TR-10.1: All files exist; route/menu registered; locale keys resolve in both languages; api functions hit the documented paths; evidence = inspection/build.
  - `rule` TR-10.2: App production build (`pnpm --filter @vben/web-antdv-next build` or repo-standard build command) succeeds without NEW type/lint errors beyond the documented ~609-error typecheck baseline; evidence = build output / baseline comparison.
  - `rubric` TR-10.3: UX consistency dimension; scale 1-5; anchors per AC-13; threshold >= 4; evidence = browser inspection/screenshots of each view against food/hotel pages.

## Task 11: Test coverage gate — JaCoCo, slice tests, ≥80%
- **Status**: `pending`
- **Priority**: high
- **Depends On**: T3..T9
- **Description**:
  - Add `jacoco-maven-plugin` to `Minecraft/pom.xml` (prepare-agent + report + check rule scoped to `com.minecraft.recommendation.*` and new controllers/config classes line ≥0.80; do not enforce project-wide baseline).
  - Add missing tests until threshold: algorithm edge cases; all service branches; controller slice tests (standalone MockMvc or direct invocation with mocked services); scheduler; config validation; authorization; analytics; exposure; recalc failure/concurrency. No live MySQL/Redis required.
  - Run `mvn test` (scope new tests + existing unaffected) and record results.
- **Acceptance Criteria Addressed**: AC-10, AC-4
- **Test Requirements**:
  - `rule` TR-11.1: `mvn -q test` green for new test classes; evidence = surefire output.
  - `rule` TR-11.2: JaCoCo report ≥80% line for configured packages; evidence = target/site/jacoco/index.html summary.
  - `rule` TR-11.3: No test depends on external services (no `@SpringBootTest` requiring DB); evidence = test source review.

## Task 12: Integration verification, compatibility check, optimization pass
- **Status**: `pending`
- **Priority**: medium
- **Depends On**: T10, T11
- **Description**:
  - Full backend compile + targeted run; verify no existing endpoint changed (diff audit vs `/api/attraction`,`/hotel`,`/food`,`/product`,`/recommend`,`/home-recommendations`).
  - Manual smoke checklist against running backend (if env available): run migration → trigger recalc per category → fetch public lists anonymously/with token → update config → feature/exclude item → schedule toggle → analytics.
  - Verify Redis keys/TTLs, async exposure non-blocking, query path logs; check N+1 queries on item projection joins (batch fetch).
  - Confirm SQL FK compatibility with seeded dataset; document migration execution (command) in response to reviewer; no doc files created unless asked.
- **Acceptance Criteria Addressed**: AC-11, NFR-1/2/4
- **Test Requirements**:
  - `rule` TR-12.1: Compile + app startup succeeds with new beans (mapper XML loading); evidence = startup log excerpt.
  - `rule` TR-12.2: No modifications to existing controller method signatures/tables; evidence = git diff audit list.
  - `rubric` TR-12.3: Performance/quality pass; scale 1-5; anchors 1 = N+1/cache missing/blocking writes, 5 = batched queries, cached anonymous lists, async exposure, documented complexity; threshold >= 4; evidence = code inspection + smoke timings.
