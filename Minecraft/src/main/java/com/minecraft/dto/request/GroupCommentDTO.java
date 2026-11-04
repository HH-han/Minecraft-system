package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupCommentDTO {

    private Long postId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;

    /** 父评论ID，为空表示一级评论 */
    private Long parentId;

    /** 回复目标用户ID */
    private Long replyToUserId;
}
