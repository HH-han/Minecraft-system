package com.minecraft.pay.service;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.pay.dto.PayCreateRequest;
import com.minecraft.pay.dto.PayCreateResponse;
import com.minecraft.pay.dto.PayOrderVO;
import com.minecraft.pay.entity.PayFlow;
import com.minecraft.pay.entity.PayNotifyLog;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.entity.PayRefund;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 聚合支付网关：系统内唯一的支付出口
 */
public interface PayGatewayService {

    /** 创建支付（不自动确认，返回前端调起支付所需参数） */
    PayCreateResponse create(PayCreateRequest request);

    /** 创建支付并在模拟渠道下自动确认（供会员/铁路等同步支付场景使用） */
    PayCreateResponse pay(PayCreateRequest request);

    /** 按商城/酒店/票务订单创建支付 */
    PayCreateResponse createForOrder(Long orderId, Long userId, String channelCode);

    /** 按订单创建并完成模拟支付（兼容旧 /api/payment/process） */
    PayCreateResponse processOrderPayment(Long orderId, Long userId, String channelCode);

    /** 模拟支付确认 */
    void mockConfirm(String flowNo, Long userId);

    /** 根据支付单号查询 */
    PayOrder getByPayNo(String payNo);

    /** 查询支付详情（用户端，校验归属） */
    PayOrderVO query(String payNo, Long userId);

    /** 按主键查询支付详情（用户端，校验归属） */
    PayOrderVO getDetail(Long id, Long userId);

    /** 关闭未支付的支付单 */
    void close(Long id, Long userId);

    /** 发起退款（全额/部分） */
    PayRefund refund(String payNo, BigDecimal amount, String reason, Long afterSaleId);

    /** 人工确认退款结果 */
    void handleRefundResult(Long refundId, boolean success, String channelRefundNo, String failReason);

    /** 统一回调处理入口 */
    String handleNotify(String channelCode, String rawBody, Map<String, String> headers);

    /** 管理端：支付主单分页 */
    PageResponse<PayOrderVO> pageOrders(PageRequest request, String payNo, String bizOrderNo,
                                        String status, String channelCode);

    /** 管理端：支付流水分页 */
    PageResponse<PayFlow> pageFlows(PageRequest request, String payNo);

    /** 管理端：回调日志分页 */
    PageResponse<PayNotifyLog> pageNotifyLogs(PageRequest request, String channelCode);
}
