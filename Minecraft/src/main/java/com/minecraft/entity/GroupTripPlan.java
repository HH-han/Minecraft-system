package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("group_trip_plan")
public class GroupTripPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Integer dayIndex;
    
    private LocalDate date;
    
    private String timeSlot;
    
    private String activity;
    
    private String location;
    
    private String transport;
    
    private String notes;
    
    private Long createdBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}