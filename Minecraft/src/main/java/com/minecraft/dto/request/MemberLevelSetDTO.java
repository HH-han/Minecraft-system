package com.minecraft.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理端设置会员等级请求
 */
@Data
public class MemberLevelSetDTO {

    @NotNull(message = "等级不能为空")
    private Long levelId;
}
