package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_trip_recommendation")
public class AITripRecommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long userId;
    
    private String destination;
    
    private Integer days;
    
    private String preferences;
    
    private String recommendationJson;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}