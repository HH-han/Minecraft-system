package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String itemType;
    private Long itemId;
    private Long parentId;
    private String content;
    private List<String> images;
    private Integer rating;
    private Integer likeCount;
    private Boolean isLiked;
    private LocalDateTime createTime;
    private List<CommentVO> replies;
}
