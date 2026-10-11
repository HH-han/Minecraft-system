package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 朋友圈评论视图对象
 */
@Data
public class MomentCommentVO {
    private Long id;
    private Long momentId;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    /** 回复目标用户ID */
    private Long replyToUserId;
    /** 回复目标用户昵称 */
    private String replyToNickname;
    private String content;
    private LocalDateTime createTime;
}
