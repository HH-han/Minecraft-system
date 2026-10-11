package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 好友关系（管理端）
 */
@Data
public class ImFriendVO {
    private Long id;
    /** 用户ID */
    private Long userId;
    private String userUsername;
    private String userNickname;
    private String userAvatar;
    /** 好友ID */
    private Long friendId;
    private String friendUsername;
    private String friendNickname;
    private String friendAvatar;
    /** 备注 */
    private String remark;
    /** 状态 0-待确认 1-已添加 */
    private Integer status;
    private LocalDateTime createTime;
}
