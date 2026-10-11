package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游笔记收藏记录
 */
@Data
@TableName("travel_note_collection")
public class TravelNoteCollection {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    private Long userId;

    private Long folderId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
