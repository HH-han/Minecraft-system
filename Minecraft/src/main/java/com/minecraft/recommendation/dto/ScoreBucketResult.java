package com.minecraft.recommendation.dto;

import lombok.Data;

/**
 * 曝光综合分十分位桶统计行。
 */
@Data
public class ScoreBucketResult {

    /** 桶序号 [0,10]：floor(score * 10) */
    private Integer bucket;

    private Long cnt;
}
