package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_album")
public class GroupAlbum {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long userId;
    
    private String imageUrl;
    
    private String thumbnailUrl;
    
    private String description;
    
    private String location;
    
    private LocalDateTime takenAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableLogic
    private Integer deleted;
}