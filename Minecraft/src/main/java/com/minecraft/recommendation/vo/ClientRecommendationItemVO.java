package com.minecraft.recommendation.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户端推荐列表物品（直接面向 App/H5 推荐页）。
 */
@Data
public class ClientRecommendationItemVO {

    private Long itemId;

    private String name;

    /** 封面图完整地址 */
    private String image;

    private String city;

    private String province;

    private BigDecimal price;

    private Integer rating;

    /** 标签集合（酒店取设施） */
    private List<String> tags;

    /** 子类型：酒店星级 / 美食品类 / 文创类型，景点为空 */
    private String subType;

    /** 综合推荐分（0-1） */
    private BigDecimal score;

    /** 命中的人工规则类型 PIN/BOOST/HIDE，无规则为空 */
    private String ruleType;

    /** 是否人工置顶（PIN） */
    private Boolean featured;

    /** 展示排名（从 1 开始） */
    private Integer rankPosition;
}
