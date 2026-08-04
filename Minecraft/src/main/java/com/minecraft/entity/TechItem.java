package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tech_items")
public class TechItem {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer categoryId;

    private String techName;

    private String description;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
