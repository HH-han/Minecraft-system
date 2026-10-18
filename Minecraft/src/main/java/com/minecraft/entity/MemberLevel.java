package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员等级配置
 */
@Data
@TableName("member_level")
public class MemberLevel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 等级序号 0-4 */
    private Integer level;

    /** 等级名称 */
    private String name;

    /** 等级图标 */
    private String icon;

    /** 成长值下限 */
    private Integer growthMin;

    /** 成长值上限，0 表示无上限 */
    private Integer growthMax;

    /** 折扣率，0.88 表示 88 折 */
    private BigDecimal discount;

    /** 积分倍率 */
    private BigDecimal pointsRate;

    /** 权益描述（JSON 数组） */
    private String benefits;

    private Integer sort;

    /** 1 启用 0 停用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
