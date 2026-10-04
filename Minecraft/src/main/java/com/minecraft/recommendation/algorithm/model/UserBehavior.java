package com.minecraft.recommendation.algorithm.model;

import java.time.LocalDateTime;

/**
 * 单个用户对单个物品的行为（已乘行为权重与时间衰减）。
 *
 * @param itemId  物品 ID
 * @param weight  行为权重（action.weight × recency 衰减，非负）
 * @param time    行为发生时间
 */
public record UserBehavior(long itemId, double weight, LocalDateTime time) {
}
