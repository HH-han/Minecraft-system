package com.minecraft.pay.adapter;

import com.minecraft.pay.dto.NotifyResult;
import com.minecraft.pay.dto.PrepayRequest;
import com.minecraft.pay.dto.PrepayResult;
import com.minecraft.pay.dto.QueryChannelResult;
import com.minecraft.pay.dto.RefundChannelRequest;
import com.minecraft.pay.dto.RefundChannelResult;

import java.util.Map;

/**
 * 支付渠道适配器：新增渠道只需实现本接口并注册 Bean，业务代码无需改动
 */
public interface PayChannelAdapter {

    /** 渠道标识，需与 pay_order.channel_code 一致 */
    String channelCode();

    /** 渠道下单，返回预支付标识与前端调起参数 */
    PrepayResult prepay(PrepayRequest request);

    /** 验签并解析异步回调 */
    NotifyResult verifyAndParseNotify(String rawBody, Map<String, String> headers);

    /** 渠道退款 */
    RefundChannelResult refund(RefundChannelRequest request);

    /** 渠道查单 */
    QueryChannelResult query(String payNo, String channelTradeNo);
}
