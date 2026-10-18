package com.minecraft.pay.enums;

/**
 * 支付业务类型
 */
public enum PayBizType {
    MALL,
    HOTEL,
    TICKET,
    POINTS,
    GROUP,
    MEMBER,
    RAILWAY;

    /**
     * 将 orders.item_type 映射为支付业务类型。
     */
    public static String fromItemType(String itemType) {
        if (itemType == null) {
            return MALL.name();
        }
        return switch (itemType.toLowerCase()) {
            case "hotel" -> HOTEL.name();
            case "ticket" -> TICKET.name();
            case "points" -> POINTS.name();
            case "group" -> GROUP.name();
            default -> MALL.name();
        };
    }

    /**
     * 是否为 orders 表承载的业务类型。
     */
    public static boolean isOrderType(String bizType) {
        return MALL.name().equals(bizType)
                || HOTEL.name().equals(bizType)
                || TICKET.name().equals(bizType)
                || POINTS.name().equals(bizType)
                || GROUP.name().equals(bizType);
    }
}
