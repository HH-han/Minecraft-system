package com.minecraft.enums;

/**
 * 退款单状态（对应 refund_order.status）
 */
public enum RefundStatus {
    PENDING(0, "待退款"),
    REFUNDING(1, "退款中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败");

    private final Integer code;
    private final String description;

    RefundStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RefundStatus fromCode(Integer code) {
        for (RefundStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid refund status code: " + code);
    }

    /** 安全取描述，未知返回 null */
    public static String descriptionOf(Integer code) {
        for (RefundStatus status : values()) {
            if (status.code.equals(code)) {
                return status.description;
            }
        }
        return null;
    }
}
