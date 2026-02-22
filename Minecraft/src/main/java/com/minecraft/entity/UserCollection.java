package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("collection")
public class UserCollection {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String itemType;
    
    private Long itemId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}