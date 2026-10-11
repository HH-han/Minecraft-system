package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 群成员（管理端）
 */
@Data
public class ImGroupMemberVO {
    private Long id;
    private Long groupId;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    /** 角色：owner-群主 admin-管理员 member-成员 */
    private String role;
    /** 1-正常 2-禁言 3-已退出 */
    private Integer status;
    /** 群内昵称 */
    private String groupNickname;
    private LocalDateTime joinTime;
    private LocalDateTime exitTime;
}
