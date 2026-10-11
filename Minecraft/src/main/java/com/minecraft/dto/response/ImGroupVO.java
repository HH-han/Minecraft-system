package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天群组（管理端）
 */
@Data
public class ImGroupVO {
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private String creatorName;
    private Integer maxMembers;
    private Integer memberCount;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
