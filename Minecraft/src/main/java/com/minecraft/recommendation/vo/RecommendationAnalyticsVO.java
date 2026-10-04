package com.minecraft.recommendation.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐效果分析看板数据。
 */
@Data
public class RecommendationAnalyticsVO {

    private String category;

    private Integer windowDays;

    private Overview overview;

    private List<ScoreBucket> scoreBuckets;

    private List<TopItem> topItems;

    private JobStats jobStats;

    /** 总览指标 */
    @Data
    public static class Overview {
        private long totalExposures;
        private long uniqueUsers;
        private long exposedItems;
        private long catalogSize;
        /** 去重曝光物品 / 在架物品 */
        private double coverage;
        /** 归因轻互动（点赞/收藏/评论）/ 曝光次数 */
        private double interactionRate;
        /** 归因转化（加购/支付）/ 曝光次数 */
        private double conversionRate;
    }

    /** 综合分十分位桶 */
    @Data
    public static class ScoreBucket {
        /** 桶序号 0..10 */
        private Integer bucket;
        /** 区间下界 */
        private double rangeStart;
        /** 区间上界 */
        private double rangeEnd;
        private long count;
    }

    /** 曝光 Top 物品 */
    @Data
    public static class TopItem {
        private Long itemId;
        private String name;
        private String city;
        private long exposures;
    }

    /** 重算任务统计 */
    @Data
    public static class JobStats {
        private long total;
        private long success;
        private long failed;
        private long running;
        private double avgDurationMs;
        private String lastStatus;
        private LocalDateTime lastStartTime;
    }
}
