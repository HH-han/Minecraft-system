package com.minecraft.recommendation.algorithm.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * 推荐算法的物品特征输入（不可变）。
 * <p>
 * 由重算服务从源物品表 + 行为聚合结果组装，算法层不直接访问数据库。
 */
@Getter
@Builder
public class ItemFeatures {

    /** 源物品 ID */
    private final long itemId;

    /** 分类编码（attraction/hotel/food/product） */
    private final String category;

    /** 标签 token 集合（已分词、小写归一） */
    private final Set<String> tags;

    /** 城市（作为内容 token 之一） */
    private final String city;

    /** 子类型：酒店星级 / 菜系 / 商品类型 */
    private final String subType;

    /** 评分 0-5；0 表示无评分 */
    private final int rating;

    /** 价格 */
    private final double price;

    /** 适宜季节 token（spring/summer/autumn/winter/all，由 SeasonalAnalyzer 解析） */
    private final Set<String> seasons;

    /**
     * 全窗口行为计数：action key(like/collect/comment/cart/order) -> 次数。
     * 冗余计数列 + 聚合查询合并而来；重算流水线中会被补充购物车/订单计数，故为可变 Map。
     */
    private final Map<String, Double> counts;

    /** 当月行为计数：action key -> 次数（用于季节趋势的行为侧信号），可变 Map */
    private final Map<String, Double> monthlyCounts;

    /** 源物品是否在架（status） */
    private final boolean active;

    /**
     * 全窗口加权前的总行为次数（counts 求和由调用方在组装时写入，便于季节比例计算）。
     */
    public double totalCount() {
        return counts.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    /** 当月总行为次数。 */
    public double monthlyTotalCount() {
        return monthlyCounts.values().stream().mapToDouble(Double::doubleValue).sum();
    }
}
