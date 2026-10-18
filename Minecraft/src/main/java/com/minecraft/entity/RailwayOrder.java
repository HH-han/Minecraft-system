package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("railway_order")
public class RailwayOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long serviceId;

    private Integer fromStopSeq;

    private Integer toStopSeq;

    private String seatType;

    private Integer passengerCount;

    /** 总金额（分） */
    private Integer totalAmount;

    private String contactPhone;

    /** PENDING / PAID / CANCELLED / REFUNDED */
    private String status;

    private LocalDateTime expireTime;

    private LocalDateTime payTime;

    private LocalDateTime cancelTime;

    private LocalDateTime createTime;
}
