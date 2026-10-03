package com.minecraft.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 积分商品兑换请求
 */
@Data
public class ExchangeRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 兑换数量
     */
    private Integer quantity = 1;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 收件人
     */
    private String receiver;
}
