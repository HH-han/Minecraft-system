package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 发布朋友圈动态请求
 */
@Data
public class MomentPublishDTO {
    /** 发布者用户ID */
    private Long userId;

    /** 文字内容 */
    @Size(max = 2000, message = "动态内容不能超过2000个字符")
    private String content;

    /** 图片URL列表 */
    @Size(max = 9, message = "图片数量不能超过9张")
    private List<String> images;

    /** 位置信息 */
    @Size(max = 200, message = "位置信息不能超过200个字符")
    private String location;

    /** 可见范围 0-公开 1-仅好友 2-私密 */
    @Min(value = 0, message = "可见范围不合法")
    @Max(value = 2, message = "可见范围不合法")
    private Integer visibility;
}
