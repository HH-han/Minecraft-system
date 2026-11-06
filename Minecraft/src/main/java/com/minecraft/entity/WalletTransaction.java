package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IM 钱包流水
 */
@Data
@TableName("wallet_transaction")
public class WalletTransaction {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 业务单号 */
    private String bizNo;

    /** 类型 recharge/withdraw/transfer_out/transfer_in */
    private String type;

    /** 金额（元，恒为正数） */
    private BigDecimal amount;

    /** 变动后余额（元） */
    private BigDecimal balanceAfter;

    /** 对方用户ID（转账时） */
    private Long targetUserId;

    /** 备注 */
    private String remark;

    /** 状态 0-失败 1-成功 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
