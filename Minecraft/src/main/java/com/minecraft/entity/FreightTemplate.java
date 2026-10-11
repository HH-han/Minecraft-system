package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 运费模板
 */
@Data
@TableName("freight_template")
public class FreightTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 1-按件 2-按重量 3-按体积 */
    private Integer chargeType;

    /** 首件/首重 */
    private BigDecimal firstUnit;

    private BigDecimal firstPrice;

    /** 续件/续重 */
    private BigDecimal extraUnit;

    private BigDecimal extraPrice;

    /** 包邮地区JSON */
    private String freeRegions;

    /** 不发货地区JSON */
    private String excludeRegions;

    /** 1-启用 0-禁用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
