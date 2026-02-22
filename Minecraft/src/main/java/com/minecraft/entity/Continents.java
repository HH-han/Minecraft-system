package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("continents")
public class Continents {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String chineseName;
    
    private BigDecimal area;
    
    private Integer population;
    
    private String description;
    
    private LocalDateTime createdAt;
}
