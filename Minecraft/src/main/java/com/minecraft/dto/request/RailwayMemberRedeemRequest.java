package com.minecraft.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 铁路积分兑换
 */
@Data
public class RailwayMemberRedeemRequest {

    @NotNull(message = "请选择兑换礼品")
    private Long giftId;
}
