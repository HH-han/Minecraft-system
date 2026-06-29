package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("group_location_history")
public class GroupLocationHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long userId;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private Integer accuracy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime recordedAt;
}