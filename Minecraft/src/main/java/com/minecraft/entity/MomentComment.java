package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 朋友圈评论
 */
@Data
@TableName("moment_comment")
public class MomentComment {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 动态ID */
    private Long momentId;

    /** 评论者用户ID */
    private Long userId;

    /** 回复目标用户ID，为空表示评论动态本身 */
    private Long replyToUserId;

    /** 评论内容 */
    private String content;

    /** 状态 0-已删除 1-正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
