package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_post")
public class GroupPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long userId;
    
    private String content;
    
    private String images;
    
    private String location;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}