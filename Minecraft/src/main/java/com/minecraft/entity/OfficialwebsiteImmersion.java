package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_immersion")
public class OfficialwebsiteImmersion {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String quoteText;
    
    private String author;
    
    private Integer isActive;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}