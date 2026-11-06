package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IM 钱包账户
 */
@Data
@TableName("wallet_account")
public class WalletAccount {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 可用余额（元） */
    private BigDecimal balance;

    /** 冻结金额（元） */
    private BigDecimal frozenAmount;

    /** 累计充值（元） */
    private BigDecimal totalRecharge;

    /** 累计支出（元） */
    private BigDecimal totalExpense;

    /** 支付密码（BCrypt） */
    private String payPassword;

    /** 状态 0-冻结 1-正常 */
    private Integer status;

    /** 乐观锁版本号 */
    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
