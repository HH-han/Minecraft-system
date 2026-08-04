package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("features")
public class Feature {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String icon;

    private String title;

    private String description;

    private String bgColor;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
