package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class CommentRequest {
    @NotNull(message = "评论类型不能为空")
    private String itemType;

    @NotNull(message = "评论对象ID不能为空")
    private Long itemId;

    private Long parentId;

    @NotNull(message = "评论内容不能为空")
    private String content;

    private String images;

    private Integer rating;
}
