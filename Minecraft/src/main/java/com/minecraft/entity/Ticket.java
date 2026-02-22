package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ticket")
public class Ticket {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String type;
    
    private String departureCity;
    
    private String arrivalCity;
    
    private String departureTime;
    
    private String arrivalTime;
    
    private String carrier;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private String seatClass;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
