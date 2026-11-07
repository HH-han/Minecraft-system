package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商旅出差申请
 */
@Data
@TableName("railway_business_trip")
public class RailwayBusinessTrip {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tripNo;

    private Long userId;

    private String fromStation;

    private String toStation;

    private LocalDate departDate;

    private LocalDate returnDate;

    private String purpose;

    private Integer passengerCount;

    /** 预估票款（分） */
    private Integer estimatedAmountCents;

    /** APPROVED / REJECTED / CANCELLED */
    private String status;

    private String auditRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
