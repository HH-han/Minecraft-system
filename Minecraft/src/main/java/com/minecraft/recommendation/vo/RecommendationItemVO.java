package com.minecraft.recommendation.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推荐物品展示对象。
 */
@Data
public class RecommendationItemVO {

    private Long itemId;

    private String name;

    private String coverImage;

    private String city;

    private String province;

    private BigDecimal price;

    private Integer rating;

    private List<String> tags;

    /** 本次排序得分 [0,1] */
    private BigDecimal score;

    /** 展示排名（从 1 开始） */
    private Integer rankPosition;

    /** 是否人工置顶 */
    private Boolean featured;
}
