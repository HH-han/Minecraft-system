package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 酒店预订明细（含酒店/房间信息快照）
 */
@Data
@TableName("hotel_booking_item")
public class HotelBookingItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookingId;

    private Long hotelId;

    // 酒店信息快照
    private String hotelName;

    private String province;

    private String city;

    private String address;

    private String coverImage;

    private String hotelFacilities;

    private Integer rating;

    private Integer starLevel;

    // 房间信息快照
    private Long roomId;

    private String roomName;

    private BigDecimal roomPrice;

    private String roomDesc;

    private String roomFacilities;

    // 预订信息
    private Integer quantity;

    private Integer nights;

    private BigDecimal subtotal;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
