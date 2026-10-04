package com.minecraft.recommendation.algorithm.model;

/**
 * 推荐分的五个因子分量，所有分量均归一化到 [0,1]。
 */
public enum ScoreComponent {

    /** 协同过滤（个性化） */
    COLLABORATIVE,
    /** 内容标签匹配（个性化/群体亲和代理） */
    CONTENT,
    /** 热度指数 */
    POPULARITY,
    /** 季节趋势 */
    SEASONAL,
    /** 品质（评分） */
    QUALITY
}
