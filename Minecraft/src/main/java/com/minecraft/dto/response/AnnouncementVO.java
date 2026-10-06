package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告 VO（列表不含 content，详情含 content）
 */
@Data
public class AnnouncementVO {
    private Long id;
    private String title;
    private String summary;
    /** 仅详情接口返回 */
    private String content;
    private Integer type;
    private Integer level;
    private Long categoryId;
    private String coverImage;
    private Integer displayMode;
    private Integer targetAudience;
    private Integer status;
    private Integer isTop;
    private Integer sortWeight;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private Long viewCount;
    private Long readCount;
    private String creatorName;
    private LocalDateTime createTime;
    /** 当前用户是否已读（未登录恒为 false） */
    private Boolean isRead;
}
