package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tech_categories")
public class TechCategory {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String categoryName;

    private String themeClass;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
