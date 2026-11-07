package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商旅企业账户
 */
@Data
@TableName("railway_business_account")
public class RailwayBusinessAccount {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String companyName;

    /** 统一社会信用代码 */
    private String taxNo;

    private String contactName;

    private String contactPhone;

    /** 月度差旅预算（分） */
    private Integer monthlyBudgetCents;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
