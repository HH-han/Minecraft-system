package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_destinations")
public class OfficialwebsiteDestinations {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String category;
    
    private String description;
    
    private String imageUrl;
    
    private String duration;
    
    private BigDecimal price;
    
    private String priceCurrency;
    
    private Integer isFeatured;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}