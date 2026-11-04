package com.minecraft.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    @Size(max = 20, message = "收藏类型不能超过20个字符")
    private String type;

    /** 标题 */
    @Size(max = 200, message = "标题不能超过200个字符")
    private String title;

    /** 文字内容/描述 */
    @Size(max = 2000, message = "内容不能超过2000个字符")
    private String content;

    /** 缩略图/封面地址 */
    @Size(max = 500, message = "封面地址不能超过500个字符")
    private String cover;

    /** 链接/文件/媒体地址 */
    @Size(max = 500, message = "地址不能超过500个字符")
    private String url;

    /** 文件名 */
    @Size(max = 255, message = "文件名不能超过255个字符")
    private String fileName;

    /** 文件大小（字节） */
    @Min(value = 0, message = "文件大小不能为负数")
    private Long fileSize;

    /** 标签列表 */
    @Size(max = 20, message = "标签数量不能超过20个")
    private List<String> tags;

    /** 来源 */
    @Size(max = 50, message = "来源不能超过50个字符")
    private String source;
}
