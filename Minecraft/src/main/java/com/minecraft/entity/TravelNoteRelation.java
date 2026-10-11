package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 旅游笔记结构化关联（景点/酒店/美食/商品/行程）
 */
@Data
@TableName("travel_note_relation")
public class TravelNoteRelation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    /**
     * 1-景点 2-酒店 3-美食 4-商品 5-行程
     */
    private Integer bizType;

    private Long bizId;

    private Integer sort;
}
