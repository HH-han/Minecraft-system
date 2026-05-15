package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hotel_room")
public class HotelRoom {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long hotelId;
    
    private String name;
    
    private String description;
    
    private String facilities;
    
    private BigDecimal price;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}