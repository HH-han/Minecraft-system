package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityPostVO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String bio;
    private String title;
    private String content;
    private String images;
    private String videos;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    /** 状态：0-待审 1-通过 2-驳回 */
    private Integer status;
    private Boolean isLiked;
    private Boolean isCollected;
    private LocalDateTime createTime;
}
