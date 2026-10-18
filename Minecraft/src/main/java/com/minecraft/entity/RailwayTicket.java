package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("railway_ticket")
public class RailwayTicket {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long passengerId;

    private String passengerName;

    private String idCardNo;

    private String seatNo;

    private Integer priceCents;

    /** ACTIVE / REFUNDED */
    private String status;

    private LocalDateTime createTime;
}
