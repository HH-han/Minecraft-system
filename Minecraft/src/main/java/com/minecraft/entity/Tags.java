package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tags")
public class Tags {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String chineseName;
    
    private String category;
    
    private String icon;
    
    private String color;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
}
