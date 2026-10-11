package com.minecraft.enums;

/**
 * 售后类型（对应 after_sale.type）
 */
public enum AfterSaleType {
    ONLY_REFUND(1, "仅退款"),
    RETURN_REFUND(2, "退货退款"),
    EXCHANGE(3, "换货");

    private final Integer code;
    private final String description;

    AfterSaleType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AfterSaleType fromCode(Integer code) {
        for (AfterSaleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid after-sale type code: " + code);
    }

    /** 安全取描述，未知返回 null */
    public static String descriptionOf(Integer code) {
        for (AfterSaleType type : values()) {
            if (type.code.equals(code)) {
                return type.description;
            }
        }
        return null;
    }
}
