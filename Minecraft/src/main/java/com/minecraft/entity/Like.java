package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("like_record")
public class Like {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String itemType;
    
    private Long itemId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
