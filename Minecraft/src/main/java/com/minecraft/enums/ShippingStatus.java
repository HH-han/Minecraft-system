package com.minecraft.enums;

/**
 * 发货单状态（对应 order_shipping.status）
 */
public enum ShippingStatus {
    PENDING(0, "待发货"),
    SHIPPED(1, "已发货"),
    IN_TRANSIT(2, "运输中"),
    DELIVERING(3, "派送中"),
    RECEIVED(4, "已签收"),
    REJECTED(5, "拒收"),
    RETURNED(6, "退回"),
    EXCEPTION(7, "异常");

    private final Integer code;
    private final String description;

    ShippingStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShippingStatus fromCode(Integer code) {
        for (ShippingStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid shipping status code: " + code);
    }

    /** 安全取描述，未知返回 null */
    public static String descriptionOf(Integer code) {
        for (ShippingStatus status : values()) {
            if (status.code.equals(code)) {
                return status.description;
            }
        }
        return null;
    }
}
