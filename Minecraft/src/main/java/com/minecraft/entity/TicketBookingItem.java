package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("ticket_booking_item")
public class TicketBookingItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookingId;

    private Long ticketId;

    private String ticketName;

    private BigDecimal ticketPrice;

    private String description;

    private Integer quantity;
}
