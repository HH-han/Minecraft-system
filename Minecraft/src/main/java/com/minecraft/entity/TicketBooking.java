package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ticket_booking")
public class TicketBooking {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String specialRequest;

    private LocalDate visitDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
