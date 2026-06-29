package com.minecraft.dto.request;

import lombok.Data;

@Data
public class GroupCommentDTO {
    private Long postId;
    private String content;
    private Long parentId;
    private Long replyToUserId;
}