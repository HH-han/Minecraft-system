package com.minecraft.enums;

public enum PaymentStatus {
    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    CANCELLED(3, "已取消"),
    REFUNDING(4, "退款中"),
    REFUNDED(5, "已退款");

    private final Integer code;
    private final String description;

    PaymentStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
