package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员权益发放记录
 */
@Data
@TableName("member_benefit_record")
public class MemberBenefitRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** BIRTHDAY / COUPON / ... */
    private String benefitType;

    private Long levelId;

    /** 发放内容（JSON） */
    private String content;

    /** 1 已发放 0 未发放 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
