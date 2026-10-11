package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 旅游笔记卡片 VO
 */
@Data
public class TravelNoteVO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String title;
    private String summary;
    private String coverUrl;
    private String destination;
    private Integer travelDays;
    private BigDecimal cost;
    private Integer status;
    private Integer isTop;
    private Integer isFeatured;
    private Integer viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private Integer shareCount;
    private String rejectReason;
    private List<String> tags;
    private Boolean isLiked;
    private Boolean isCollected;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
}
