package com.minecraft.enums;

/**
 * 售后单状态（对应 after_sale.status）
 */
public enum AfterSaleStatus {
    PENDING(0, "待审核"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "已驳回"),
    WAIT_BUYER_SEND(3, "待买家退货"),
    WAIT_SELLER_RECEIVE(4, "待商家收货"),
    REFUNDING(5, "退款中"),
    COMPLETED(6, "已完成"),
    CLOSED(7, "已关闭");

    private final Integer code;
    private final String description;

    AfterSaleStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AfterSaleStatus fromCode(Integer code) {
        for (AfterSaleStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid after-sale status code: " + code);
    }

    /** 安全取描述，未知返回 null */
    public static String descriptionOf(Integer code) {
        for (AfterSaleStatus status : values()) {
            if (status.code.equals(code)) {
                return status.description;
            }
        }
        return null;
    }
}
