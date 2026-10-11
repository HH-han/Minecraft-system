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
    /** 当前查询用户在群内的角色（仅「用户所在群」接口返回） */
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
