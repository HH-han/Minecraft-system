package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feature_items")
public class FeatureItem {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer featureId;

    private String itemName;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
