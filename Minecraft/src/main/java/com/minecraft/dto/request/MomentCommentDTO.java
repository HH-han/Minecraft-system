package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 朋友圈评论请求
 */
@Data
public class MomentCommentDTO {
    /** 评论者用户ID */
    private Long userId;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;

    /** 回复目标用户ID，为空表示评论动态本身 */
    private Long replyToUserId;
}
