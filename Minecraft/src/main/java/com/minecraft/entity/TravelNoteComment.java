package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游笔记评论（支持二级回复）
 */
@Data
@TableName("travel_note_comment")
public class TravelNoteComment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    private Long userId;

    /**
     * 父评论ID（0-一级评论）
     */
    private Long parentId;

    /**
     * 被回复人ID
     */
    private Long replyUid;

    private String content;

    private Integer likeCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
