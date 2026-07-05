package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_footer")
public class OfficialwebsiteFooter {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String brandName;
    
    private String brandDescription;
    
    private String socialLinks;
    
    private String copyrightText;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}