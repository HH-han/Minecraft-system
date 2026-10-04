package com.minecraft.recommendation.enums;

/**
 * 单次推荐结果的来源口径，用于曝光归因分析。
 */
public enum ExposureSource {

    /** 登录用户个性化推荐 */
    PERSONALIZED,
    /** 匿名/全局热门推荐 */
    POPULAR,
    /** 人工置顶推荐 */
    FEATURED,
    /** 冷启动兜底 */
    COLD_START
}
