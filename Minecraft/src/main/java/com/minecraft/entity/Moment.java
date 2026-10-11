package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 朋友圈动态
 */
@Data
@TableName("moment")
public class Moment {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布者用户ID */
    private Long userId;

    /** 文字内容 */
    private String content;

    /** 图片URL列表，逗号分隔 */
    private String images;

    /** 位置信息 */
    private String location;

    /** 可见范围 0-公开 1-仅好友 2-私密 */
    private Integer visibility;

    /** 点赞数 */
    private Integer likeCount;

    /** 评论数 */
    private Integer commentCount;

    /** 状态 0-已删除 1-正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
