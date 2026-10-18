package com.minecraft.pay.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraft.pay.dto.NotifyResult;
import com.minecraft.pay.dto.PrepayRequest;
import com.minecraft.pay.dto.PrepayResult;
import com.minecraft.pay.dto.QueryChannelResult;
import com.minecraft.pay.dto.RefundChannelRequest;
import com.minecraft.pay.dto.RefundChannelResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 模拟支付渠道：开发/测试环境零依赖使用。
 * 支付、退款、查单均直接返回成功，但走与真实渠道完全相同的状态机与数据表。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MockChannelAdapter implements PayChannelAdapter {

    private final ObjectMapper objectMapper;

    @Override
    public String channelCode() {
        return "MOCK";
    }

    @Override
    public PrepayResult prepay(PrepayRequest request) {
        String prepayId = "mock_prepay_" + request.getFlowNo();
        Map<String, Object> params = new HashMap<>();
        params.put("prepayId", prepayId);
        params.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
        params.put("appId", "mock_app");
        params.put("mock", true);

        PrepayResult result = new PrepayResult();
        result.setSuccess(true);
        result.setPrepayId(prepayId);
        result.setPayParams(params);
        result.setRawResponse("{\"code\":\"SUCCESS\",\"prepayId\":\"" + prepayId + "\"}");
        return result;
    }

    @Override
    public NotifyResult verifyAndParseNotify(String rawBody, Map<String, String> headers) {
        NotifyResult result = new NotifyResult();
        result.setVerified(true);
        result.setSuccess(true);
        result.setRawBody(rawBody);
        try {
            JsonNode node = objectMapper.readTree(rawBody);
            if (node.hasNonNull("payNo")) {
                result.setPayNo(node.get("payNo").asText());
            }
            if (node.hasNonNull("amount")) {
                result.setAmount(new BigDecimal(node.get("amount").asText()));
            }
            if (node.hasNonNull("channelTradeNo")) {
                result.setChannelTradeNo(node.get("channelTradeNo").asText());
            }
            if (node.hasNonNull("success")) {
                result.setSuccess(node.get("success").asBoolean());
            }
        } catch (Exception e) {
            log.warn("[Mock支付] 回调报文解析失败：{}", e.getMessage());
        }
        return result;
    }

    @Override
    public RefundChannelResult refund(RefundChannelRequest request) {
        RefundChannelResult result = new RefundChannelResult();
        result.setSuccess(true);
        result.setChannelRefundNo("mock_refund_" + request.getRefundNo());
        result.setRawResponse("{\"code\":\"SUCCESS\",\"refundNo\":\"" + request.getRefundNo() + "\"}");
        return result;
    }

    @Override
    public QueryChannelResult query(String payNo, String channelTradeNo) {
        QueryChannelResult result = new QueryChannelResult();
        result.setStatus("SUCCESS");
        result.setChannelTradeNo(channelTradeNo);
        result.setRawResponse("{\"code\":\"SUCCESS\"}");
        return result;
    }
}
