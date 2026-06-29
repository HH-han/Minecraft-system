package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_poll")
public class GroupPoll {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long creatorId;
    
    private String title;
    
    private String description;
    
    private Integer type;
    
    private String options;
    
    private LocalDateTime endTime;
    
    private Boolean isAnonymous;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}