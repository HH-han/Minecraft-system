package com.minecraft.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包流水 VO
 */
@Data
@Schema(description = "钱包流水")
public class WalletTransactionVO {

    @Schema(description = "流水ID")
    private Long id;

    @Schema(description = "业务单号")
    private String bizNo;

    @Schema(description = "类型 recharge/withdraw/transfer_out/transfer_in")
    private String type;

    @Schema(description = "金额（元，恒为正数）")
    private BigDecimal amount;

    @Schema(description = "变动后余额（元）")
    private BigDecimal balanceAfter;

    @Schema(description = "对方用户ID（转账时）")
    private Long targetUserId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态 0-失败 1-成功")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
