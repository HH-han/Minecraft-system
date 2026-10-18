package com.minecraft.pay.enums;

/**
 * 支付主单状态
 */
public enum PayStatus {
    /** 已创建，未发起渠道支付 */
    INIT,
    /** 已向渠道发起支付，等待用户完成 */
    PAYING,
    /** 支付成功 */
    SUCCESS,
    /** 支付失败（可重新发起） */
    FAILED,
    /** 已关闭/已过期 */
    CLOSED,
    /** 部分退款中 */
    REFUNDING,
    /** 已全额退款 */
    REFUNDED;

    public static boolean isSuccess(String status) {
        return SUCCESS.name().equals(status);
    }

    public static boolean isPayable(String status) {
        return INIT.name().equals(status) || PAYING.name().equals(status);
    }

    public static boolean isRefundable(String status) {
        return SUCCESS.name().equals(status) || REFUNDING.name().equals(status);
    }
}
