package com.minecraft.dto.response;

import lombok.Data;

/**
 * 朋友圈用户简要信息
 */
@Data
public class MomentUserVO {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}
