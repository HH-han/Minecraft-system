package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 旅游笔记评论 VO
 */
@Data
public class TravelNoteCommentVO {
    private Long id;
    private Long noteId;
    private Long userId;
    private String username;
    private String avatar;
    private Long parentId;
    private Long replyUid;
    private String replyUsername;
    private String content;
    private Integer likeCount;
    private LocalDateTime createTime;
    private List<TravelNoteCommentVO> replies;
}
