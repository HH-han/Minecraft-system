# Smart Recommendation System - Product Requirements Document

## Overview
- **Summary**: A hybrid recommendation system for the four bookable categories of the tourism platform — attractions (`attraction`), hotels (`hotel`), restaurants/food (`food`), and souvenirs (`product`) — consisting of four precomputed recommendation tables, a configurable hybrid ranking engine (collaborative filtering + content-based + popularity + seasonal + quality), public REST APIs with personalized/anonymous modes, admin-only management APIs, a scheduled recalculation job, performance analytics, and a management console inside the existing vben admin app (`web-antdv-next`).
- **Purpose**: The current "recommendations" (`/attraction/recommend`, `/food/recommend`, static `recommend` table, manually curated `home_recommendations`) are hard-coded sorts by rating/likes or manually edited rows. There is no personalization, no behavioral learning, no tunable strategy, and no performance measurement. The new system increases discovery relevance while keeping full operator control (feature/boost/exclude, parameters, scheduling).
- **Target Users**:
  - End users of the public APIs (web/uni-app clients) — personalized or anonymous recommendations.
  - Platform administrators — tune algorithm parameters, feature/exclude items, trigger/inspect recalculation, schedule jobs, view analytics.

## Goals
- Precompute and persist per-item recommendation scores with decomposed, inspectable components for all four categories.
- Produce personalized rankings for logged-in users (item-based collaborative filtering + content/tag affinity) with a high-quality anonymous/cold-start fallback (popularity + seasonality + quality + manual features).
- Make every meaningful algorithm parameter adjustable per category at runtime, without redeploy, with validation and caching.
- Give operators manual control: feature/pin an item, apply a weight boost, or exclude an item.
- Support on-demand and scheduled recalculation with run history and concurrency protection.
- Measure recommendation performance: exposures, attributed interactions/conversions, coverage, score distribution, top converting items.
- Ship an admin console consistent with existing `web-antdv-next` views.
- Reach at least 80% line coverage of the new backend recommendation package with unit + slice/integration-style tests.

## Non-Goals
- Modifying the user-facing clients (`minecraft-ui` Vue web app, `minecraft-App` uni-app). New public APIs are provided; wiring their pages is a follow-up.
- A new click/view/dwell-time event pipeline or SDK. Analytics derive from (a) exposures recorded by the API itself and (b) the existing behavior tables.
- A distributed scheduler (Quartz/XXL-Job). Single-instance Spring `TaskScheduler` is sufficient for current deployment.
- Real-time stream processing; recommendations update via batch recalculation plus bounded on-demand personalization.
- Changing existing endpoints/tables (`recommend`, `home_recommendations`, `/attraction/recommend`, etc.) or their behavior.
- Cross-category recommendation (e.g., "booked hotel → suggest attractions") in this iteration; each category ranks within itself (a future mixed/home endpoint can compose them).

## Background & Context
- Backend: Spring Boot 3.0.2, Java 17, MyBatis-Plus 3.5.3.2, MySQL 8 (`db_minecraft`), Redis (Lettuce), JWT auth (jjwt), Lombok, Hutool, FastJSON. Tests: JUnit 5 + Mockito (`spring-boot-starter-test`), existing tests are Mockito-only mapper mocks.
- Response envelope: `com.minecraft.dto.response.ApiResponse<T>` (`code/message/data`); paged envelope `PageResponse<T>`; request `PageRequest(pageNum,pageSize,keyword,sortBy,sortOrder)`.
- Persistence conventions: entities `@TableName`/`@TableId(IdType.AUTO)`, mappers extend `BaseMapper<T>` with optional XML in `src/main/resources/mapper/`, services extend `ServiceImpl<Mapper, Entity>`, controllers use `@RestController` + `@RequestMapping("/api/...")` + Swagger `@Tag`/`@Operation`.
- Active item tables and their behavior keys:
  - `attraction` (id BIGINT; city, province, season, tags, cuisine-like `tags`, rating int, like_count/collect_count/comment_count, status) — **not** the separate world-atlas table `attractions` (INT id, FK cities). Public listing pages use `/api/attraction` → entity `Attraction` → table `attraction`.
  - `hotel` (id BIGINT; city, star_level, facilities, rating, counts, status)
  - `food` (id BIGINT; city, cuisine_type, tags, price, rating, counts, status)
  - `product` (id BIGINT; city, type, tags, price, stock, rating, counts, status)
- Existing implicit behavior signals (item_type values `attraction`/`hotel`/`food`/`product`): `like_record`, `collection`, `comment` (with optional rating), `cart` (item_type/item_id, create_time), `orders` (item_type/item_id, status, create_time). `user_activity_log` exists but is empty/generic and is not used.
- Authorization: `JwtAuthenticationFilter` populates `SecurityContext` with the numeric user id; `SecurityConfig` is stateless but currently ends with `anyRequest().permitAll()`. Admin identity is `user.permissions = '0'` (see `UserServiceImpl.adminLogin`). Admin endpoints must therefore enforce admin rights in code (service-level check), matching the project's existing pattern, and public GET endpoints remain reachable without login.
- No `@EnableScheduling`/`@Scheduled` usage exists yet; scheduling must be introduced.
- Admin frontend: `minecraft-admin-main/apps/web-antdv-next` (vben 5.7, Vue 3, antdv-next, Tailwind v4). Business routes live in `src/router/routes/modules/*.ts` with lucide icons and `$t` titles; API modules in `src/api/management/**` use `requestClient` (`#/api/request`, Bearer token, unwraps `data` on `code===200`); views live in `src/views/<group>/*.vue` using `Page` from `@vben/common-ui`, antdv-next components and `@vben/icons` lucide exports; locale JSON in `src/locales/langs/{zh-CN,en-US}/`. No chart library is installed — metrics must use antdv-next `Statistic`/`Progress`/`Table`/`Tag` and CSS bars.

## Functional Requirements
- **FR-1 (Storage)**: Provide a SQL migration creating four recommendation tables (`attractions_recommendations`, `hotels_recommendations`, `restaurants_recommendations`, `souvenirs_recommendations`) plus supporting tables: parameter config, item-similarity, job run log, exposure log. Each of the four tables has, at minimum: `item_id`, `recommendation_score`, `user_preference_matching`, `popularity_index`, `update_timestamp`, plus decomposed components (collaborative/content/seasonal/quality scores), manual control columns (`is_featured`, `feature_weight`, `status`), audit timestamps, a unique key on `item_id`, and a foreign key to the corresponding item table.
- **FR-2 (Category mapping)**: System maps categories to item tables/behavior types unambiguously: attraction→`attraction`(`attraction`), hotel→`hotel`(`hotel`), restaurant→`food`(`food`), souvenir→`product`(`product`). Unknown category values are rejected everywhere with a 400 error.
- **FR-3 (Batch recalculation)**: An admin-triggerable/schedulable job (per category or all four) computes and upserts item rows: popularity index (weighted, log-compressed interactions incl. likes/collects/comments/cart-adds/orders, recency-aware), quality (rating), seasonality (explicit season field where present + per-month interaction history), content tag vectors, and item-based collaborative-filtering similarities (cosine over weighted user–item interactions, top-K neighbors). Manual columns (featured/weight/status) must survive re-runs. Every run writes a job-log row (trigger source, timing, counts, status/error). Overlapping runs for the same category must be rejected/skipped safely.
- **FR-4 (Hybrid online ranking)**: Public per-category GET endpoint returns ranked items with item card fields. Logged-in users with recent behavior receive personalized ranking (item-CF neighbor expansion + content/tag affinity blended with popularity/seasonal/quality); anonymous and cold-start users receive the global blended ranking; featured items receive a configured boost and may be pinned to the top; excluded items are filtered; a light diversity re-order avoids repeated same-tag runs; already strongly-consumed items can be de-emphasized but pinned/featured rules always win.
- **FR-5 (Related items)**: A per-item "related" endpoint returns similar items using precomputed item similarity with content-tag fallback.
- **FR-6 (Configurable strategy)**: All weights/knobs (factor weights per category; per-action behavior weights; CF neighbor K; recency window; popularity compression base; diversity strength; featured boost; strategy selector hybrid/popularity/content/collaborative; cold-start mode; exposure logging toggle/sample rate/retention days; schedule cron/enabled) are stored per scope (global/category), seeded with defaults, editable via admin API, validated, cached, and take effect on subsequent reads/recalculation without restart.
- **FR-7 (Admin item control)**: Admin can list paginated scored items per category (with search), toggle featured, set feature weight/pinned position, and set active/excluded status.
- **FR-8 (Analytics)**: Exposures served by the public endpoint are recorded asynchronously (user id optional, category, item, score, rank, source, timestamp). Analytics endpoints return: per-category/overall exposures, unique users, attributed interactions (like/collect/cart/order occurring after an exposure of that item, within a configurable window), proxy CTR/conversion rates, catalog coverage, score distribution buckets, top converting items, and job run statistics. Exposure data is retained for a configurable number of days and purged by the scheduled maintenance.
- **FR-9 (Scheduling)**: Administrators can enable/disable automatic recalculation and set a cron expression; the backend dynamically (re)schedules a single clean-up/recalc task, reports next estimated run time, and records scheduled runs like manual ones.
- **FR-10 (Security)**: Public recommendation GET endpoints are accessible anonymously and become personalized automatically when a valid Bearer token is present. Every admin endpoint requires an authenticated user whose `permissions='0'`; otherwise the API returns 403. Inputs are validated; failures use the existing `ApiResponse` error shape via `GlobalExceptionHandler`.
- **FR-11 (Admin console)**: A new "Recommendation" menu group in `web-antdv-next` provides: (a) overview/analytics dashboard, (b) parameter management with per-category tabs, (c) item control table (feature/boost/exclude + manual recalc trigger), (d) schedule settings + run history. Pages follow existing component/i18n/layout conventions and call only the new admin APIs.
- **FR-12 (Observability & errors)**: New code uses structured SLF4J logging at sensible levels (job start/end/timing, parameter changes, triggers, failures), throws `BusinessException` for domain errors, and never leaks stack traces to clients.

## Non-Functional Requirements
- **NFR-1 (Complexity)**: The online ranking path is O(n log n) or better per category (single linear scoring pass + sort/heap top-K over the candidate set; CF expansion bounded by K neighbors); the batch job uses set-based SQL aggregation for counts rather than loading raw interaction rows and documents its complexity.
- **NFR-2 (Performance)**: Anonymous/global lists and configuration are cached in Redis with short TTLs and invalidated on writes/recalculation; exposure persistence is async and non-blocking to response time; a default public request (limit 10) adds no perceptible overhead versus existing list endpoints under current data volumes.
- **NFR-3 (Correctness)**: All score components are normalized to [0,1]; weights are normalized at read time so they need not sum to 1; popularity uses log compression to prevent a single dominant item; stored final score equals the documented formula within rounding.
- **NFR-4 (Compatibility)**: No existing API contract, table, or frontend behavior changes; new tables only add FKs to existing InnoDB item tables with `ON DELETE RESTRICT ON UPDATE RESTRICT`; code matches existing package/layout/naming conventions and Java 17/Spring Boot 3 idioms.
- **NFR-5 (Testability)**: New backend code is organized so the algorithm is pure/deterministic and unit-testable without a database; ≥80% line coverage for the new recommendation packages is enforced via JaCoCo; tests run with Mockito/JUnit5 and do not require a live MySQL.
- **NFR-6 (Reliability)**: Recalculation is idempotent (upsert by item_id, manual columns preserved), concurrency-guarded per category, and failures produce a FAILED job-log row plus a 500-shaped response for manual triggers without corrupting previous scores.
- **NFR-7 (Maintainability)**: Magic numbers (weights, defaults, SQL-free constants) live in one config/defaults layer; Chinese `@Operation`/`@Tag`/comments follow the surrounding code; i18n keys provided in zh-CN and en-US.

## Constraints
- **Technical**: Java 17, Spring Boot 3.0.2, MyBatis-Plus 3.5.3.2, MySQL 8 dialect (`INSERT ... ON DUPLICATE KEY UPDATE` acceptable in mapper XML), Redis available, JWT auth as implemented; no new heavyweight infra (no MQ, no new scheduler framework, no new JS chart dependency).
- **Business**: Manual curation always overrides the algorithm for featured/excluded items; changes must be reversible (re-toggle, reset-to-default config).
- **Dependencies**: Existing item tables and behavior tables must remain the sources of truth; admin login/token flow unchanged.

## Assumptions
- Deployment runs a single backend instance (or one instance runs scheduled jobs); Redis and MySQL are reachable in dev/prod as configured.
- Data volumes stay modest (catalog items in the thousands, interactions in the millions); the chosen in-memory similarity computation is adequate at this scale; set-based aggregation keeps DB load bounded.
- `orders.status = '1'` (paid) or '3' (completed) count as conversion-grade signals; cart rows and unpaid orders count as weaker intent signals.
- Administrators use the `web-antdv-next` console; the older `minecraft-admin` app is untouched.
- Public clients currently do not send a user id parameter; personalization relies solely on the JWT; no trust in client-supplied user ids.

## Acceptance Criteria

### AC-1: Database migration exists and is valid
- **Type**: `rule`
- **Given**: a MySQL 8 database initialized from `db_minecraft.sql`
- **When**: the new recommendation migration script is executed
- **Then**: the four recommendation tables exist with `item_id`, `recommendation_score`, `user_preference_matching`, `popularity_index`, `update_timestamp` (+ component/manual columns), unique key on `item_id`, and FK constraints to `attraction(id)`, `hotel(id)`, `food(id)`, `product(id)`; supporting config/similarity/job-log/exposure-log tables and seeded default config rows exist; script is idempotent (re-runnable)
- **Pass Condition**: script runs twice without error, `SHOW CREATE TABLE` shows required columns and FKs, and default config is present for global + four categories
- **Evidence**: migration file under `Minecraft/src/main/resources/static/sql/`, successful execution output, table definitions

### AC-2: Recalculation populates and refreshes scores safely
- **Type**: `rule`
- **Given**: seeded catalog/behavior data
- **When**: an admin triggers recalculation for one category and for all categories
- **Then**: every active item has an upserted row with normalized component scores and a final score; re-running preserves manual `is_featured/feature_weight/status` changes; a job-log row records SUCCESS with item counts and duration; a concurrent trigger for the same category is rejected/skipped without data corruption
- **Pass Condition**: unit/orchestration tests with mocked mappers verify upsert calls, preservation logic, job-log writes, and concurrency guard; manual run via API returns a job record
- **Evidence**: test results, service code, job-log rows

### AC-3: Public API ranking behaves per identity and controls
- **Type**: `rule`
- **Given**: recalculated data with a featured item, an excluded item, and a user with behavior history
- **When**: `GET /api/recommendations/{category}` is called anonymously, then with that user's token
- **Then**: anonymous returns global blended order; authenticated response differs per user signals; featured item is boosted/pinned; excluded item never appears; an invalid category returns 400; related-items endpoint returns ranked neighbors
- **Pass Condition**: controller/service tests cover all branches and assertions above
- **Evidence**: test results and endpoint responses

### AC-4: Hybrid algorithm factors, weights, and bounds are correct
- **Type**: `rule`
- **Given**: known synthetic items/behaviors and a fixed configuration
- **When**: the scoring engine ranks items
- **Then**: result is a deterministic blend of collaborative, content, popularity, seasonal, quality factors using configured weights; every component is within [0,1]; log compression prevents one item from monopolizing; changing a weight changes the blended ranking in the expected direction; the online path performs one scoring pass plus a sort (documented O(n log n))
- **Pass Condition**: pure unit tests assert numeric bounds, determinism, weight sensitivity, cold-start fallback, and related-item similarity
- **Evidence**: algorithm test class results and documented complexity

### AC-5: Parameter management is validated and live
- **Type**: `rule`
- **Given**: an admin token
- **When**: parameters are fetched, updated with valid values, and updated with invalid values (negative weight, non-numeric, unknown key, bad cron)
- **Then**: valid updates persist, invalidate caches, and affect the next computation; invalid updates are rejected with 400 and a clear message; reset-to-defaults is available
- **Pass Condition**: service/controller tests for get/update/validate/reset and cache invalidation
- **Evidence**: test results

### AC-6: Analytics and exposure tracking work
- **Type**: `rule`
- **Given**: exposure logging enabled and interactions recorded after exposures
- **When**: a public recommendation request is served and analytics endpoints are queried
- **Then**: an exposure row is persisted asynchronously with rank/source; analytics return exposures, unique users, attributed conversions, proxy CTR/conversion, coverage and distribution; retention purge deletes rows older than configured days
- **Pass Condition**: tests verify async capture path, attribution SQL logic (mocked), overview assembly, and purge call
- **Evidence**: test results, DTO/service code

### AC-7: Scheduling can be controlled
- **Type**: `rule`
- **Given**: scheduling config (enabled flag + cron)
- **When**: admin updates cron and toggles enabled
- **Then**: the dynamic task is (re)scheduled or cancelled, next-run time is reported, and a scheduled run executes the same service path as a manual run and logs identically; an invalid cron returns 400
- **Pass Condition**: tests with a controllable `TaskScheduler` verify schedule/cancel/reschedule and invalid-cron rejection
- **Evidence**: test results

### AC-8: Admin endpoints are authorized
- **Type**: `rule`
- **Given**: no token, a non-admin token, and an admin token
- **When**: calling any admin recommendation endpoint (config update, item feature, recalc trigger, schedule update)
- **Then**: anonymous/non-admin calls receive 403; admin calls succeed; public GET endpoints remain accessible anonymously
- **Pass Condition**: authorization tests cover the three identities for a write endpoint and the public read endpoint
- **Evidence**: test results

### AC-9: Admin console delivers the four management surfaces
- **Type**: `rule`
- **Given**: the `web-antdv-next` app
- **When**: an administrator opens the Recommendation menu
- **Then**: routes/menu entries exist for dashboard, parameters, item control, and schedule/history; each view loads data from the new APIs and performs its core action (save params, toggle feature/exclude, trigger recalc, save schedule); zh-CN/en-US labels exist; the app builds without new errors beyond the known typecheck baseline
- **Pass Condition**: files exist under router/api/views/locales, views are wired to endpoints, and production build (or agreed lint/typecheck command) completes
- **Evidence**: file paths, route registration, successful build output

### AC-10: Test coverage threshold
- **Type**: `rule`
- **Given**: JaCoCo configured for the new recommendation packages
- **When**: `mvn verify`/`mvn test` runs
- **Then**: line coverage of the new recommendation packages is ≥80% and all new tests pass without external services
- **Pass Condition**: JaCoCo report shows ≥0.80 line coverage for the configured packages; build is green
- **Evidence**: surefire + JaCoCo report outputs

### AC-11: Backward compatibility and conventions
- **Type**: `rule`
- **Given**: the change set
- **When**: existing controllers/services/tables are inspected and the app compiles
- **Then**: no existing endpoint signature or table is altered; new code follows ApiResponse/MyBatis-Plus/Swagger/Lombok/package conventions; logging and BusinessException usage follow existing patterns
- **Pass Condition**: diff review + successful compilation
- **Evidence**: review checklist, compile output

### AC-12: Recommendation quality under realistic data
- **Type**: `rubric`
- **Dimension**: Relevance and robustness of rankings (personalization lift, cold-start quality, diversity, absence of degenerate ordering)
- **Scale**: 1-5
- **Anchors**: 1 = pure popularity sort in disguise, cold users get empty/identical lists; 3 = hybrid works but factors are coarse or diversity absent; 5 = personalized/anonymous modes both sensible, weights tunable with visible effect, popularity compressed, tags/categories diverse, pinned items stable
- **Pass Threshold**: >= 4
- **Evidence**: algorithm unit tests with constructed scenarios, manual endpoint inspection notes

### AC-13: Admin console UX consistency
- **Type**: `rubric`
- **Dimension**: Visual/interaction consistency with existing management pages (Page layout, antdv-next components, loading/empty/error states, confirmations, i18n)
- **Scale**: 1-5
- **Anchors**: 1 = custom markup alien to the admin; 3 = functional but inconsistent in spacing/feedback; 5 = indistinguishable in quality from existing pages like food/hotel management
- **Pass Threshold**: >= 4
- **Evidence**: browser/screenshot inspection of the four views

## Open Questions
- None (scope clarifications resolved: admin-console-only frontend scope; console in `web-antdv-next`; hybrid with anonymous fallback; reuse existing behavior tables).
