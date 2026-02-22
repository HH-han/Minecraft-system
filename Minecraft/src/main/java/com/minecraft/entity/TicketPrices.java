package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ticket_prices")
public class TicketPrices {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer attractionId;
    
    private String ticketType;
    
    private BigDecimal price;
    
    private String currency;
    
    private String season;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String description;
    
    private String purchaseUrl;
    
    private String discountInfo;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
