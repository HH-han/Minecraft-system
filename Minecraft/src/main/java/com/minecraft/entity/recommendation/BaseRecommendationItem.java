package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐结果表公共字段。四张分类推荐结果表结构一致，仅表名不同。
 */
@Data
public abstract class BaseRecommendationItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 源物品表 ID */
    private Long itemId;

    /** 综合推荐分（全局/匿名口径，0-1） */
    private BigDecimal recommendationScore;

    /** 用户偏好匹配分（内容标签群体亲和度，0-1） */
    private BigDecimal userPreferenceMatching;

    /** 热度指数（对数压缩 + 归一化，0-1） */
    private BigDecimal popularityIndex;

    /** 协同过滤分（物品共现相似度聚合，0-1） */
    private BigDecimal collaborativeScore;

    /** 内容质量/标签丰富度分（0-1） */
    private BigDecimal contentScore;

    /** 季节趋势分（0-1） */
    private BigDecimal seasonalScore;

    /** 品质分（评分归一化，0-1） */
    private BigDecimal qualityScore;

    /** 是否人工置顶推荐 */
    private Boolean isFeatured;

    /** 人工加权系数（置顶/boost 强度） */
    private BigDecimal featureWeight;

    /** 状态 0-排除 1-正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 推荐分更新时间戳（ON UPDATE CURRENT_TIMESTAMP） */
    private LocalDateTime updateTimestamp;
}
