package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 申请售后请求
 */
@Data
public class AfterSaleApplyRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 1-仅退款 2-退货退款 3-换货 */
    @NotNull(message = "售后类型不能为空")
    private Integer type;

    private Integer reasonType;

    private String reason;

    /** 凭证图片URL列表（复用上传服务） */
    private List<String> evidenceUrls;

    private BigDecimal refundAmount;

    /** 售后数量，缺省取订单数量 */
    private Integer quantity;
}
