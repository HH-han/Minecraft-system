package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建发货单请求
 */
@Data
public class ShippingCreateRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private Long carrierId;

    private String carrierCode;

    private String trackingNo;

    @NotNull(message = "收件人不能为空")
    private String receiverName;

    @NotNull(message = "收件电话不能为空")
    private String receiverPhone;

    @NotNull(message = "收件地址不能为空")
    private String receiverAddress;

    /** 本包裹数量，缺省取订单数量 */
    private Integer quantity;

    private BigDecimal packageWeight;

    private BigDecimal freight;

    private LocalDateTime expectTime;

    private String remark;
}
