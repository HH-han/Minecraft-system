package com.minecraft.recommendation.config;

/**
 * 推荐系统 Redis 键定义。
 */
public final class RecommendationCacheKeys {

    private RecommendationCacheKeys() {
    }

    /** 参数快照 */
    public static final String CONFIG = "recommendation:config";

    /** 匿名全局推荐列表前缀：recommendation:list:{category}:{city}:{limit} */
    public static final String GLOBAL_LIST_PREFIX = "recommendation:list:";

    public static String globalList(String category, String city, int limit) {
        return GLOBAL_LIST_PREFIX + category + ":" + (city == null ? "_" : city) + ":" + limit;
    }
}
