package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cities")
public class Cities {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String chineseName;
    
    private Integer countryId;
    
    private Boolean isCapital;
    
    private BigDecimal area;
    
    private Integer population;
    
    private String timezone;
    
    private String description;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private String famousFor;
    
    private String bestSeason;
    
    private LocalDateTime createdAt;
}
