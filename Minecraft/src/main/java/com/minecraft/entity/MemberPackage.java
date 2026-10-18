package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员套餐
 */
@Data
@TableName("member_package")
public class MemberPackage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 1 月卡 2 季卡 3 年卡 */
    private Integer type;

    /** 有效天数 */
    private Integer durationDays;

    private BigDecimal price;

    private BigDecimal originalPrice;

    /** 赠送积分 */
    private Integer givePoints;

    /** 赠送成长值 */
    private Integer giveGrowth;

    private String description;

    private Integer sort;

    /** 1 启用 0 停用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
