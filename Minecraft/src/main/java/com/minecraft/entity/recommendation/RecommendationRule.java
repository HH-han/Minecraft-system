package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐人工干预规则：支持置顶（PIN）/加权（BOOST）/隐藏（HIDE），
 * 同一分类下同一物品仅允许一条规则（uk_category_item）。
 */
@Data
@TableName("recommendation_rule")
public class RecommendationRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类编码 attraction/hotel/food/product */
    private String category;

    /** 被干预的物品 ID */
    private Long itemId;

    /** 规则类型 PIN / BOOST / HIDE */
    private String ruleType;

    /** 置顶位序，越小越靠前（仅 PIN 生效） */
    private Integer sortOrder;

    /** 加权系数 [0,1]：最终分 = 综合分 + boost*(1-综合分)（仅 BOOST 生效） */
    private BigDecimal boostWeight;

    /** 生效开始时间，空=立即生效 */
    private LocalDateTime startTime;

    /** 生效结束时间，空=长期有效 */
    private LocalDateTime endTime;

    /** 1-启用 0-停用 */
    private Integer status;

    /** 规则备注 */
    private String remark;

    /** 最后操作人（管理员用户 ID） */
    private Long operatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
