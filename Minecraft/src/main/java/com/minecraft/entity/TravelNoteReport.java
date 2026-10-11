package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游笔记举报记录
 */
@Data
@TableName("travel_note_report")
public class TravelNoteReport {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    private Long reporterId;

    private String reason;

    /**
     * 0-待处理 1-已处理 2-驳回
     */
    private Integer status;

    private Long handleBy;

    private LocalDateTime handleTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
