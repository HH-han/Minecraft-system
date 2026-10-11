package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 旅游笔记标签关联
 */
@Data
@TableName("travel_note_tag_rel")
public class TravelNoteTagRel {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    private Long tagId;
}
