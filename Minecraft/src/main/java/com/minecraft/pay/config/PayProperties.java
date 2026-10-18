package com.minecraft.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付模块配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "pay")
public class PayProperties {

    /** 模拟支付开关：true 时所有渠道统一路由到 MockChannelAdapter */
    private boolean mockEnabled = true;

    /** 模拟支付允许的渠道，为空表示全部渠道均可模拟 */
    private List<String> mockChannels = new ArrayList<>();

    /** 支付回调基地址 */
    private String notifyBase = "http://localhost:8080";

    private Wechat wechat = new Wechat();

    private Alipay alipay = new Alipay();

    @Data
    public static class Wechat {
        private String appId;
        private String mchId;
        private String apiV3Key;
        private String notifyUrl;
    }

    @Data
    public static class Alipay {
        private String appId;
        private String privateKey;
        private String alipayPublicKey;
        private String notifyUrl;
    }
}
