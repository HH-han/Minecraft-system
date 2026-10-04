package com.minecraft.recommendation.algorithm.model;

import java.util.List;

/**
 * 单个用户的行为历史（协同过滤输入）。
 *
 * @param userId    用户 ID
 * @param behaviors recency 窗口内的加权行为列表
 */
public record UserHistory(long userId, List<UserBehavior> behaviors) {
}
