package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("platform_stats")
public class PlatformStat {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String statLabel;

    private String statValue;

    private String statIcon;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
