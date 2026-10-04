package com.minecraft.recommendation.dto;

import lombok.Data;

/**
 * 按物品聚合的计数结果。
 */
@Data
public class ItemCountResult {

    private Long itemId;

    private Long cnt;
}
