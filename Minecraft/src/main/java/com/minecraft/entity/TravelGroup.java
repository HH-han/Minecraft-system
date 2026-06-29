package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("travel_group")
public class TravelGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String coverUrl;
    
    private String destination;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String description;
    
    private Integer maxMembers;
    
    private Boolean isPublic;
    
    private Integer status;
    
    private Long createdBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}