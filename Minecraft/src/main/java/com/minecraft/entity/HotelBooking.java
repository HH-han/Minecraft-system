package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hotel_booking")
public class HotelBooking {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String specialRequest;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer guests;

    private Long roomId;

    private String roomName;

    private BigDecimal roomPrice;

    private String roomDesc;

    private String roomFacilities;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
