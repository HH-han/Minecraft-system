package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attraction_tags")
public class AttractionTags {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer attractionId;
    
    private Integer tagId;
    
    private LocalDateTime createdAt;
}
