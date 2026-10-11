package com.minecraft.dto.request;

import lombok.Data;

/**
 * 朋友圈评论请求
 */
@Data
public class MomentCommentDTO {
    /** 评论者用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 回复目标用户ID，为空表示评论动态本身 */
    private Long replyToUserId;
}
