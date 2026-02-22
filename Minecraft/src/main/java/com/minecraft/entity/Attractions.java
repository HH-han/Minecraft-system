package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("attractions")
public class Attractions {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String chineseName;
    
    private Integer cityId;
    
    private String type;
    
    private BigDecimal rating;
    
    private String priceLevel;
    
    private String visitingHours;
    
    private String bestSeason;
    
    private String visitDuration;
    
    private String description;
    
    private String history;
    
    private String tips;
    
    private String address;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private String imageUrl;
    
    private String officialWebsite;
    
    private String yearBuilt;
    
    private String architect;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
