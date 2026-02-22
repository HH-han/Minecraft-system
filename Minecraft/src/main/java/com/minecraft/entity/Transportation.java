package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("transportation")
public class Transportation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer attractionId;
    
    private String type;
    
    private String routeInfo;
    
    private String fromLocation;
    
    private String duration;
    
    private BigDecimal distance;
    
    private BigDecimal cost;
    
    private String currency;
    
    private String schedule;
    
    private String tips;
    
    private Boolean isRecommended;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
}
