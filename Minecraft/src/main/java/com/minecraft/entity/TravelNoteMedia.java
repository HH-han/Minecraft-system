package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游笔记媒体资源
 */
@Data
@TableName("travel_note_media")
public class TravelNoteMedia {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    /**
     * 1-图片 2-视频
     */
    private Integer mediaType;

    private String url;

    private String thumbUrl;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
