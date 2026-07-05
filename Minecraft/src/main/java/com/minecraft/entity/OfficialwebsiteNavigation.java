package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_navigation")
public class OfficialwebsiteNavigation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String title;
    
    private String url;
    
    private Integer sortOrder;
    
    private Integer isActive;
    
    private Integer parentId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}