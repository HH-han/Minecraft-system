package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    @NotNull(message = "商品类型不能为空")
    private String itemType;

    @NotNull(message = "商品ID不能为空")
    private Long itemId;

    @NotNull(message = "商品名称不能为空")
    private String itemName;

    private String image;

    @NotNull(message = "商品金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    private String remark;
}
