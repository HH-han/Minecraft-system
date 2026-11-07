package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 铁路会员积分流水
 */
@Data
@TableName("railway_point_log")
public class RailwayPointLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 积分变化：正数获得，负数消耗 */
    private Integer changeValue;

    /** 变化后积分余额 */
    private Integer afterValue;

    /** ORDER_EARN / REDEEM */
    private String bizType;

    private String bizNo;

    private String remark;

    private LocalDateTime createTime;
}
