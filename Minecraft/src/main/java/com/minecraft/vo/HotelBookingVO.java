package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 酒店预订详情（含明细项）
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

    private List<HotelBookingItemVO> items;

    /**
     * 酒店预订明细（酒店/房间信息快照）
     */
    @Data
    public static class HotelBookingItemVO {
        private Long id;
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
    }
}
