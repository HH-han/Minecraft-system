package com.minecraft.recommendation.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户-物品行为记录（协同过滤与用户兴趣画像输入）。
 */
@Data
public class UserItemInteraction {

    private Long userId;

    private Long itemId;

    /** 行为发生时间（用于时间衰减） */
    private LocalDateTime actionTime;

    /** 评论评分（1-5），其他行为为空 */
    private Integer rating;

    /** 行为类型 LIKE/COLLECT/COMMENT/CART/ORDER（仅单用户 UNION 查询返回） */
    private String actionType;
}
