package com.minecraft.recommendation.dto;

import lombok.Data;

/**
 * 物品曝光次数聚合行。
 */
@Data
public class ItemExposureResult {

    private Long itemId;

    private Long exposures;
}
