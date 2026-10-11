package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游笔记点赞记录
 */
@Data
@TableName("travel_note_like")
public class TravelNoteLike {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
