package com.minecraft.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 会员开通/续费请求
 */
@Data
public class MemberOpenDTO {

    @NotNull(message = "套餐不能为空")
    private Long packageId;

    /** 1 支付宝 2 微信 3 余额 */
    @NotNull(message = "支付方式不能为空")
    private Integer payType;
}
