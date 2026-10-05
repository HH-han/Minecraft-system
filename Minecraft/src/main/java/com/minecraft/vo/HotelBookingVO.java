package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 酒店预订详情
 */
@Data
public class HotelBookingVO {
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
    private LocalDateTime createdAt;
}
