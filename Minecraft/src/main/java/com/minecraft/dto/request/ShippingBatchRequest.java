package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量发货请求（同一承运商，多订单）
 */
@Data
public class ShippingBatchRequest {

    private Long carrierId;

    private String carrierCode;

    @NotEmpty(message = "订单ID列表不能为空")
    private List<Long> orderIds;

    private String remark;
}
