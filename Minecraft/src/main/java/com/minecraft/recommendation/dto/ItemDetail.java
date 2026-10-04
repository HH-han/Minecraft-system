package com.minecraft.recommendation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 源物品的轻量投影（推荐展示 + 特征构建通用）。
 */
@Data
public class ItemDetail {

    private Long itemId;

    private String name;

    private String coverImage;

    private String city;

    private String province;

    private BigDecimal price;

    /** 0-5，0 为未知 */
    private Integer rating;

    /** 已分词标签 */
    private List<String> tags;

    /** 原始季节文本（仅景点可能有） */
    private String season;

    /** 子类型（星级/菜系/商品类型） */
    private String subType;

    private Integer status;
}
