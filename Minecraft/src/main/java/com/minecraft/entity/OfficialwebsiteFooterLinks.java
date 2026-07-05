package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_footer_links")
public class OfficialwebsiteFooterLinks {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String category;
    
    private String title;
    
    private String url;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}