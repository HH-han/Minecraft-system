package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 旅游笔记评论请求
 */
@Data
public class TravelNoteCommentDTO {

    @NotNull(message = "笔记ID不能为空")
    private Long noteId;

    /** 父评论ID，一级评论传0或不传 */
    private Long parentId = 0L;

    /** 被回复人ID（二级回复时必传） */
    private Long replyUid;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论最长1000字")
    private String content;
}
