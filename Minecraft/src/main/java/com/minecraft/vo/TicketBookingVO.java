package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点门票预订详情（含明细项）
 */
@Data
public class TicketBookingVO {
    private Long id;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String specialRequest;
    private LocalDate visitDate;
    /** 订单状态：0-待支付 1-已支付 2-已取消 3-已完成 */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TicketBookingItemVO> items;

    /**
     * 门票预订明细
     */
    @Data
    public static class TicketBookingItemVO {
        private Long id;
        private Long ticketId;
        private String ticketName;
        private BigDecimal ticketPrice;
        private String description;
        private Integer quantity;

        /** 关联景点ID（通过 ticket -> attraction_ticket.attraction_id 反查） */
        private Long attractionId;

        /** 关联景点名称 */
        private String attractionName;
    }
}
