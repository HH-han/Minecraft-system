package com.minecraft.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 新增/编辑收藏请求
 */
@Data
public class FavoriteSaveDTO {
    /** 收藏ID，为空表示新增 */
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

    /** 标签列表 */
    private List<String> tags;

    /** 来源 */
    private String source;
}
