package com.minecraft.dto.response;

import lombok.Data;

/**
 * 公告阅读统计 VO
 */
@Data
public class AnnouncementStatVO {
    private Long id;
    private String title;
    private Long viewCount;
    private Long readCount;
    /** 阅读率 = 已读数 / 浏览量 * 100，保留两位小数 */
    private Double readRate;
}
