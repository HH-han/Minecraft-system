package com.minecraft.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理端调整成长值请求
 */
@Data
public class MemberGrowthAdjustDTO {

    @NotNull(message = "变化值不能为空")
    private Integer changeValue;

    private String remark;
}
