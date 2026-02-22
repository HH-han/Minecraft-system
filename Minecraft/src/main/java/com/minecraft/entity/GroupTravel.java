package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("group_travel")
public class GroupTravel {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long creatorId;
    
    private String name;
    
    private String description;
    
    private String destination;
    
    private LocalDateTime departureTime;
    
    private LocalDateTime returnTime;
    
    private Integer maxMembers;
    
    private Integer currentMembers;
    
    private BigDecimal price;
    
    private String images;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
