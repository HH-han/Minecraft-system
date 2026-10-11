package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游笔记标签
 */
@Data
@TableName("travel_note_tag")
public class TravelNoteTag {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer useCount;

    private Integer isHot;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
