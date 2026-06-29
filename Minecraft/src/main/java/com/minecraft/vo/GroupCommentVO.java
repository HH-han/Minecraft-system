package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupCommentVO {
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private List<GroupCommentVO> replies;
    private LocalDateTime createdAt;
}