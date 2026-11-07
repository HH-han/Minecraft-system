package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 铁路积分兑换记录
 */
@Data
@TableName("railway_member_redemption")
public class RailwayMemberRedemption {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String redeemNo;

    private Long userId;

    private Long giftId;

    private String giftName;

    private Integer pointsCost;

    private String redeemCode;

    /** UNUSED / USED */
    private String status;

    private LocalDateTime createTime;
}
