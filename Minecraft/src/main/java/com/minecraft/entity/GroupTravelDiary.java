package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_travel_diary")
public class GroupTravelDiary {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private String title;
    
    private String coverUrl;
    
    private String contentJson;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}