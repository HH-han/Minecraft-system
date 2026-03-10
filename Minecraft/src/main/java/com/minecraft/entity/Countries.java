package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("countries")
public class Countries {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String chineseName;

    private String image;
    
    private Integer continentId;
    
    private String capital;
    
    private BigDecimal area;
    
    private Integer population;
    
    private String currency;
    
    private String language;
    
    private String timezone;
    
    private String countryCode;
    
    private String phoneCode;
    
    private String flagEmoji;
    
    private String description;
    
    private LocalDateTime createdAt;
}
