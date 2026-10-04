package com.minecraft.recommendation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.mapper.RecommendationAnalyticsMapper;
import com.minecraft.mapper.RecommendationExposureLogMapper;
import com.minecraft.mapper.RecommendationJobLogMapper;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.dto.ItemExposureResult;
import com.minecraft.recommendation.dto.ScoreBucketResult;
import com.minecraft.recommendation.enums.JobStatus;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO.JobStats;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO.Overview;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO.ScoreBucket;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO.TopItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 推荐效果分析服务：曝光/去重/覆盖率、曝光归因互动率与转化率、分数分布、
 * 曝光 Top 物品、重算任务统计，以及过期曝光日志分批清理。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationAnalyticsService {

    private static final int DEFAULT_WINDOW_DAYS = 7;
    private static final int MAX_WINDOW_DAYS = 90;
    private static final int TOP_N = 10;
    private static final int PURGE_BATCH = 1000;
    private static final int PURGE_MAX_BATCHES = 200;

    private final RecommendationAnalyticsMapper analyticsMapper;
    private final RecommendationJobLogMapper jobLogMapper;
    private final RecommendationExposureLogMapper exposureLogMapper;
    private final RecommendationConfigService configService;
    private final RecommendationItemCatalog catalog;

    /**
     * 看板数据。
     *
     * @param categoryCode 分类编码，null/blank 表示全部分类
     * @param windowDays   统计窗口天数（1-90，默认 7）
     */
    public RecommendationAnalyticsVO dashboard(String categoryCode, Integer windowDays) {
        RecommendCategory category = resolveCategory(categoryCode);
        int days = normalizeWindow(windowDays);
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        int attributionHours = configService.getInt(null,
                RecommendationDefaults.EXPOSURE_ATTRIBUTION_HOURS);
        String code = category == null ? null : category.code();

        long exposures = analyticsMapper.countExposures(code, since);
        long users = analyticsMapper.countExposureUsers(code, since);
        long exposedItems = analyticsMapper.countExposureItems(code, since);
        long catalogSize = totalCatalogSize(category);

        long interactions = sumAcrossCategories(category,
                c -> analyticsMapper.countAttributedInteractions(c, since, attributionHours));
        long conversions = sumAcrossCategories(category,
                c -> analyticsMapper.countAttributedConversions(c, since, attributionHours));

        RecommendationAnalyticsVO vo = new RecommendationAnalyticsVO();
        vo.setCategory(code == null ? "ALL" : code);
        vo.setWindowDays(days);

        Overview overview = new Overview();
        overview.setTotalExposures(exposures);
        overview.setUniqueUsers(users);
        overview.setExposedItems(exposedItems);
        overview.setCatalogSize(catalogSize);
        overview.setCoverage(ratio(exposedItems, catalogSize));
        overview.setInteractionRate(ratio(interactions, exposures));
        overview.setConversionRate(ratio(conversions, exposures));
        vo.setOverview(overview);

        vo.setScoreBuckets(scoreBuckets(code, since));
        vo.setTopItems(topItems(category, code, since));
        vo.setJobStats(jobStats(code, since));
        return vo;
    }

    /**
     * 按保留期配置分批清理曝光日志，返回累计删除行数。
     */
    public int purgeExpired() {
        int retentionDays = configService.getInt(null, RecommendationDefaults.RETENTION_DAYS);
        LocalDateTime before = LocalDateTime.now().minusDays(retentionDays);
        int total = 0;
        for (int i = 0; i < PURGE_MAX_BATCHES; i++) {
            int deleted = exposureLogMapper.purgeBefore(before, PURGE_BATCH);
            total += deleted;
            if (deleted < PURGE_BATCH) {
                break;
            }
        }
        if (total > 0) {
            log.info("推荐曝光日志清理完成 retentionDays={} deleted={}", retentionDays, total);
        }
        return total;
    }

    // ---------------- 装配 ----------------

    private List<ScoreBucket> scoreBuckets(String code, LocalDateTime since) {
        List<ScoreBucketResult> rows = analyticsMapper.selectScoreBuckets(code, since);
        ScoreBucket[] buckets = new ScoreBucket[11];
        for (int i = 0; i <= 10; i++) {
            ScoreBucket b = new ScoreBucket();
            b.setBucket(i);
            b.setRangeStart(i / 10.0);
            b.setRangeEnd(i == 10 ? 1.0 : (i + 1) / 10.0);
            b.setCount(0L);
            buckets[i] = b;
        }
        for (ScoreBucketResult row : rows) {
            if (row.getBucket() != null && row.getBucket() >= 0 && row.getBucket() <= 10) {
                buckets[row.getBucket()].setCount(row.getCnt() == null ? 0L : row.getCnt());
            }
        }
        return Arrays.asList(buckets);
    }

    private List<TopItem> topItems(RecommendCategory category, String code, LocalDateTime since) {
        List<ItemExposureResult> rows = analyticsMapper.selectTopItems(code, since, TOP_N);
        List<TopItem> items = new ArrayList<>(rows.size());
        // 全部分类时物品类型不可判定，仅返回 ID 与次数
        Map<Long, ItemDetail> details = category == null ? Map.of()
                : catalog.load(category, rows.stream().map(ItemExposureResult::getItemId).toList());
        for (ItemExposureResult row : rows) {
            TopItem item = new TopItem();
            item.setItemId(row.getItemId());
            item.setExposures(row.getExposures() == null ? 0L : row.getExposures());
            ItemDetail detail = details.get(row.getItemId());
            if (detail != null) {
                item.setName(detail.getName());
                item.setCity(detail.getCity());
            }
            items.add(item);
        }
        return items;
    }

    private JobStats jobStats(String code, LocalDateTime since) {
        // 全部分类视图只统计 ALL 汇总日志；分类视图统计该分类日志，避免重复计数
        QueryWrapper<RecommendationJobLog> wrapper = new QueryWrapper<RecommendationJobLog>()
                .eq("category", code == null ? "ALL" : code)
                .ge("start_time", since)
                .orderByDesc("start_time");
        List<RecommendationJobLog> logs = jobLogMapper.selectList(wrapper);

        JobStats stats = new JobStats();
        stats.setTotal(logs.size());
        long success = 0;
        long failed = 0;
        long running = 0;
        long durationSum = 0;
        long durationCount = 0;
        for (RecommendationJobLog logRow : logs) {
            if (JobStatus.SUCCESS.name().equals(logRow.getStatus())) {
                success++;
                if (logRow.getDurationMs() != null && logRow.getDurationMs() > 0) {
                    durationSum += logRow.getDurationMs();
                    durationCount++;
                }
            } else if (JobStatus.FAILED.name().equals(logRow.getStatus())) {
                failed++;
            } else if (JobStatus.RUNNING.name().equals(logRow.getStatus())) {
                running++;
            }
        }
        stats.setSuccess(success);
        stats.setFailed(failed);
        stats.setRunning(running);
        stats.setAvgDurationMs(durationCount == 0 ? 0.0 : (double) durationSum / durationCount);
        if (!logs.isEmpty()) {
            RecommendationJobLog latest = logs.get(0);
            stats.setLastStatus(latest.getStatus());
            stats.setLastStartTime(latest.getStartTime());
        }
        return stats;
    }

    private long totalCatalogSize(RecommendCategory category) {
        if (category != null) {
            return catalog.countActive(category);
        }
        long sum = 0;
        for (RecommendCategory c : RecommendCategory.values()) {
            sum += catalog.countActive(c);
        }
        return sum;
    }

    /**
     * 单分类直接统计；全部分类时累加四个分类（曝光表 category 取值即 itemTable 编码）。
     */
    private long sumAcrossCategories(RecommendCategory category, CategoryCounter counter) {
        if (category != null) {
            return counter.count(category.code());
        }
        long sum = 0;
        for (RecommendCategory c : RecommendCategory.values()) {
            sum += counter.count(c.code());
        }
        return sum;
    }

    private RecommendCategory resolveCategory(String categoryCode) {
        if (categoryCode == null || categoryCode.isBlank()) {
            return null;
        }
        return RecommendCategory.fromCode(categoryCode.trim());
    }

    private int normalizeWindow(Integer windowDays) {
        if (windowDays == null || windowDays <= 0) {
            return DEFAULT_WINDOW_DAYS;
        }
        return Math.min(windowDays, MAX_WINDOW_DAYS);
    }

    private static double ratio(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return Math.min(1.0, (double) numerator / denominator);
    }

    @FunctionalInterface
    private interface CategoryCounter {
        long count(String categoryCode);
    }
}
