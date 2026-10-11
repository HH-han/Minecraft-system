package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏项（微信风格「收藏」）
 */
@Data
@TableName("favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 类型 note/link/image/video/file/chat/voice/location/miniapp */
    private String type;

    /** 标题 */
    private String title;

    /** 文字内容/描述 */
    private String content;

    /** 缩略图/封面地址 */
    private String cover;

    /** 链接/文件/媒体地址 */
    private String url;

    /** 文件名 */
    private String fileName;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 标签，逗号分隔 */
    private String tags;

    /** 来源 */
    private String source;

    /** 状态 0-已删除 1-正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
