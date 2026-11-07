package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 团体购票申请
 */
@Data
@TableName("railway_group_booking")
public class RailwayGroupBooking {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bookingNo;

    private Long userId;

    private String companyName;

    private String contactName;

    private String contactPhone;

    private String fromStation;

    private String toStation;

    private LocalDate travelDate;

    private LocalDate returnDate;

    private Integer passengerCount;

    private String seatType;

    private String trainNo;

    /** 预估单人票价（分） */
    private Integer unitPriceCents;

    /** 团体折扣率 */
    private BigDecimal discountRate;

    /** 预估总额（分） */
    private Integer expectedAmountCents;

    /** PENDING / CONFIRMED / CANCELLED */
    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
