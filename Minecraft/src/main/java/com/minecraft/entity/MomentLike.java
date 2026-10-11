package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 朋友圈点赞
 */
@Data
@TableName("moment_like")
public class MomentLike {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 动态ID */
    private Long momentId;

    /** 点赞用户ID */
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
