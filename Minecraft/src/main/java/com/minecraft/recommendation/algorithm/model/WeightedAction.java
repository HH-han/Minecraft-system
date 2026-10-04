package com.minecraft.recommendation.algorithm.model;

import java.time.LocalDateTime;

/**
 * 用户-物品加权行为明细（Item-CF 构图输入）。
 *
 * @param userId 用户 ID
 * @param itemId 物品 ID
 * @param weight 行为权重（含时间衰减，非负）
 * @param time   行为发生时间
 */
public record WeightedAction(long userId, long itemId, double weight, LocalDateTime time) {
}
