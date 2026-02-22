package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reviews")
public class Reviews {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer attractionId;
    
    private Long userId;
    
    private BigDecimal rating;
    
    private String title;
    
    private String content;
    
    private LocalDate visitDate;
    
    private String travelWith;
    
    private Integer photosCount;
    
    private Integer likesCount;
    
    private Integer helpfulCount;
    
    private Boolean isVerified;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
