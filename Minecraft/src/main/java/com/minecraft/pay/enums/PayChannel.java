package com.minecraft.pay.enums;

import com.minecraft.exception.BusinessException;

/**
 * 支付渠道标识
 */
public enum PayChannel {
    MOCK,
    WECHAT,
    ALIPAY,
    UNIONPAY;

    public static PayChannel of(String code) {
        String normalized = normalize(code);
        for (PayChannel channel : values()) {
            if (channel.name().equals(normalized)) {
                return channel;
            }
        }
        throw new BusinessException("不支持的支付渠道：" + code);
    }

    /**
     * 兼容前端历史传参（英文枚举名 / 中文名称）。
     */
    public static String normalize(String code) {
        if (code == null || code.isBlank()) {
            return MOCK.name();
        }
        String value = code.trim().toUpperCase();
        return switch (value) {
            case "ALIPAY", "支付宝" -> ALIPAY.name();
            case "WECHAT", "WECHATPAY", "微信", "微信支付" -> WECHAT.name();
            case "UNIONPAY", "UNION", "银联", "云闪付" -> UNIONPAY.name();
            case "MOCK", "模拟" -> MOCK.name();
            default -> value;
        };
    }
}
