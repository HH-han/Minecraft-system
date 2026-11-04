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
    /** 状态：0-待审 1-通过 2-驳回 */
    private Integer status;
    private LocalDateTime createTime;
}
