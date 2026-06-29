package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupPostVO {
    private Long id;
    private Long groupId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private List<String> images;
    private String location;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;
}