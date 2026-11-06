package com.minecraft.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钱包信息 VO
 */
@Data
@Schema(description = "钱包信息")
public class WalletVO {

    @Schema(description = "钱包ID")
    private Long id;

    @Schema(description = "可用余额（元）")
    private BigDecimal balance;

    @Schema(description = "冻结金额（元）")
    private BigDecimal frozenAmount;

    @Schema(description = "累计充值（元）")
    private BigDecimal totalRecharge;

    @Schema(description = "累计支出（元）")
    private BigDecimal totalExpense;

    @Schema(description = "是否已设置支付密码")
    private Boolean hasPayPassword;

    @Schema(description = "状态 0-冻结 1-正常")
    private Integer status;
}
