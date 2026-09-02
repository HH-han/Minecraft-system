package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 卡片标签关联实体类，对应数据库 card_tags 表（联合主键：card_id + tag_id）
 */
@Data
@TableName("card_tags")
public class CardTag {
    /**
     * 卡片ID
     */
    private Integer cardId;

    /**
     * 标签ID
     */
    private Integer tagId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
