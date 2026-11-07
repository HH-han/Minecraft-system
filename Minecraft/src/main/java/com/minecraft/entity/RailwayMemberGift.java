package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 铁路积分兑换礼品
 */
@Data
@TableName("railway_member_gift")
public class RailwayMemberGift {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String giftCode;

    private String name;

    private String description;

    private Integer pointsCost;

    private Integer stock;

    private String icon;

    private Integer sort;

    /** 1 上架 0 下架 */
    private Integer status;

    private LocalDateTime createTime;
}
