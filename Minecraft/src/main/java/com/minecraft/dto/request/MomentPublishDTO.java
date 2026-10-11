package com.minecraft.dto.request;

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
    private String content;

    /** 图片URL列表 */
    private List<String> images;

    /** 位置信息 */
    private String location;

    /** 可见范围 0-公开 1-仅好友 2-私密 */
    private Integer visibility;
}
