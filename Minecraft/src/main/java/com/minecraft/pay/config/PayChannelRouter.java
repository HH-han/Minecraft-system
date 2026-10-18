package com.minecraft.pay.config;

import com.minecraft.exception.BusinessException;
import com.minecraft.pay.adapter.MockChannelAdapter;
import com.minecraft.pay.adapter.PayChannelAdapter;
import com.minecraft.pay.enums.PayChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 渠道路由器：根据配置在 Mock 与真实渠道适配器之间切换
 */
@Component
@RequiredArgsConstructor
public class PayChannelRouter {

    private final List<PayChannelAdapter> adapters;
    private final MockChannelAdapter mockAdapter;
    private final PayProperties properties;

    /**
     * 解析本次支付应使用的适配器。
     */
    public PayChannelAdapter resolve(String channelCode) {
        PayChannel channel = PayChannel.of(channelCode);
        if (properties.isMockEnabled()) {
            List<String> mockChannels = properties.getMockChannels();
            if (mockChannels != null && !mockChannels.isEmpty() && !mockChannels.contains(channel.name())) {
                throw new BusinessException("该支付渠道未开启模拟支付：" + channel.name());
            }
            return mockAdapter;
        }
        if (channel == PayChannel.MOCK) {
            throw new BusinessException("模拟支付渠道未启用，请选择真实支付渠道");
        }
        return adapters.stream()
                .filter(adapter -> channel.name().equalsIgnoreCase(adapter.channelCode()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("支付渠道未配置：" + channel.name()));
    }

    public boolean isMockMode() {
        return properties.isMockEnabled();
    }
}
