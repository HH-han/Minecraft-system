package com.minecraft.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.MemberOrder;
import com.minecraft.entity.Order;
import com.minecraft.entity.RailwayOrder;
import com.minecraft.enums.RefundStatus;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.MemberOrderMapper;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.mapper.RailwayOrderMapper;
import com.minecraft.pay.adapter.PayChannelAdapter;
import com.minecraft.pay.config.PayChannelRouter;
import com.minecraft.pay.config.PayProperties;
import com.minecraft.pay.dto.NotifyResult;
import com.minecraft.pay.dto.PayCreateRequest;
import com.minecraft.pay.dto.PayCreateResponse;
import com.minecraft.pay.dto.PayOrderVO;
import com.minecraft.pay.dto.PrepayRequest;
import com.minecraft.pay.dto.PrepayResult;
import com.minecraft.pay.dto.RefundChannelRequest;
import com.minecraft.pay.dto.RefundChannelResult;
import com.minecraft.pay.entity.PayFlow;
import com.minecraft.pay.entity.PayNotifyLog;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.entity.PayRefund;
import com.minecraft.pay.enums.PayBizType;
import com.minecraft.pay.enums.PayChannel;
import com.minecraft.pay.enums.PayStatus;
import com.minecraft.pay.event.PayRefundSuccessEvent;
import com.minecraft.pay.event.PaySuccessEvent;
import com.minecraft.pay.mapper.PayFlowMapper;
import com.minecraft.pay.mapper.PayOrderMapper;
import com.minecraft.pay.mapper.PayRefundMapper;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.pay.service.PayNotifyLogService;
import com.minecraft.pay.util.PayNoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聚合支付网关实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayGatewayServiceImpl implements PayGatewayService {

    private static final int DEFAULT_EXPIRE_MINUTES = 30;

    private final PayOrderMapper payOrderMapper;
    private final PayFlowMapper payFlowMapper;
    private final PayRefundMapper payRefundMapper;
    private final PayNotifyLogService notifyLogService;
    private final PayChannelRouter channelRouter;
    private final PayProperties payProperties;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper orderMapper;
    private final MemberOrderMapper memberOrderMapper;
    private final RailwayOrderMapper railwayOrderMapper;

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public PayCreateResponse create(PayCreateRequest request) {
        if (request == null) {
            throw new BusinessException("支付参数不能为空");
        }
        PayChannel channel = PayChannel.of(request.getChannelCode());
        BigDecimal totalAmount = scale(request.getTotalAmount());
        BigDecimal pointsDeduct = scale(request.getPointsDeduct());
        BigDecimal payAmount = totalAmount.subtract(pointsDeduct);
        if (totalAmount.signum() <= 0) {
            throw new BusinessException("支付金额必须大于 0");
        }
        if (payAmount.signum() <= 0) {
            throw new BusinessException("抵扣后实付金额必须大于 0");
        }

        // 金额与归属以服务端业务单据为准，避免前端篡改
        validateBusiness(request, totalAmount);

        LocalDateTime now = LocalDateTime.now();
        PayOrder existing = findLatest(request.getBizOrderNo(), request.getBizType());
        if (existing != null && PayStatus.isSuccess(existing.getStatus())) {
            // 幂等：同一业务订单已支付成功，直接返回已有结果
            return toResponse(existing, null, null);
        }

        PayOrder payOrder;
        if (existing != null
                && PayStatus.isPayable(existing.getStatus())
                && existing.getExpireTime() != null
                && existing.getExpireTime().isAfter(now)) {
            // 复用未过期的支付主单，仅新增一次支付尝试流水
            payOrder = existing;
            payOrder.setChannelCode(channel.name());
            payOrder.setTotalAmount(totalAmount);
            payOrder.setPayAmount(payAmount);
            payOrder.setPointsDeduct(pointsDeduct);
            payOrder.setSubject(request.getSubject());
            payOrderMapper.updateById(payOrder);
        } else {
            int expireMinutes = request.getExpireMinutes() == null
                    ? DEFAULT_EXPIRE_MINUTES : request.getExpireMinutes();
            payOrder = new PayOrder();
            payOrder.setPayNo(PayNoGenerator.payNo());
            payOrder.setBizOrderNo(request.getBizOrderNo());
            payOrder.setBizType(request.getBizType());
            payOrder.setUserId(request.getUserId());
            payOrder.setTotalAmount(totalAmount);
            payOrder.setPayAmount(payAmount);
            payOrder.setPointsDeduct(pointsDeduct);
            payOrder.setChannelCode(channel.name());
            payOrder.setStatus(PayStatus.INIT.name());
            payOrder.setSubject(request.getSubject());
            payOrder.setExpireTime(now.plusMinutes(expireMinutes));
            payOrderMapper.insert(payOrder);
        }

        PayFlow flow = new PayFlow();
        flow.setFlowNo(PayNoGenerator.flowNo());
        flow.setPayNo(payOrder.getPayNo());
        flow.setChannelCode(channel.name());
        flow.setAmount(payAmount);
        flow.setStatus(PayStatus.INIT.name());
        payFlowMapper.insert(flow);

        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setPayNo(payOrder.getPayNo());
        prepayRequest.setFlowNo(flow.getFlowNo());
        prepayRequest.setUserId(payOrder.getUserId());
        prepayRequest.setAmount(payAmount);
        prepayRequest.setSubject(payOrder.getSubject());
        prepayRequest.setExpireTime(payOrder.getExpireTime());
        prepayRequest.setNotifyUrl(payProperties.getNotifyBase()
                + "/api/pay/notify/" + channel.name());

        PrepayResult prepayResult;
        try {
            PayChannelAdapter adapter = channelRouter.resolve(channel.name());
            prepayResult = adapter.prepay(prepayRequest);
        } catch (Exception e) {
            flow.setStatus(PayStatus.FAILED.name());
            flow.setChannelResponse(e.getMessage());
            payFlowMapper.updateById(flow);
            payOrder.setStatus(PayStatus.FAILED.name());
            payOrderMapper.updateById(payOrder);
            log.error("[支付] 渠道下单失败 payNo={}, channel={}", payOrder.getPayNo(), channel.name(), e);
            throw new BusinessException("发起支付失败：" + e.getMessage());
        }

        if (prepayResult == null || !prepayResult.isSuccess()) {
            flow.setStatus(PayStatus.FAILED.name());
            flow.setChannelResponse(prepayResult == null ? null : prepayResult.getRawResponse());
            payFlowMapper.updateById(flow);
            payOrder.setStatus(PayStatus.FAILED.name());
            payOrderMapper.updateById(payOrder);
            throw new BusinessException("发起支付失败："
                    + (prepayResult == null ? "渠道无响应" : prepayResult.getErrorMsg()));
        }

        flow.setChannelPrepayId(prepayResult.getPrepayId());
        flow.setChannelResponse(prepayResult.getRawResponse());
        flow.setStatus(PayStatus.PAYING.name());
        payFlowMapper.updateById(flow);

        payOrder.setStatus(PayStatus.PAYING.name());
        payOrderMapper.updateById(payOrder);

        return toResponse(payOrder, flow, prepayResult.getPayParams());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCreateResponse pay(PayCreateRequest request) {
        PayCreateResponse response = create(request);
        if (PayStatus.isSuccess(response.getStatus())) {
            return response;
        }
        if (!channelRouter.isMockMode()) {
            throw new BusinessException("请在支付渠道完成支付");
        }
        mockConfirm(response.getFlowNo(), request.getUserId());
        response.setStatus(PayStatus.SUCCESS.name());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCreateResponse createForOrder(Long orderId, Long userId, String channelCode) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (userId != null && order.getUserId() != null && !userId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权支付该订单");
        }
        String bizType = PayBizType.fromItemType(order.getItemType());
        if (!"0".equals(order.getStatus())) {
            PayOrder paid = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                    .eq(PayOrder::getBizOrderNo, order.getOrderNo())
                    .eq(PayOrder::getBizType, bizType)
                    .eq(PayOrder::getStatus, PayStatus.SUCCESS.name())
                    .last("LIMIT 1"));
            if (paid != null) {
                return toResponse(paid, null, null);
            }
            throw new BusinessException("订单当前状态不可支付");
        }
        if (order.getAmount() == null || order.getAmount().signum() <= 0) {
            throw new BusinessException("订单金额异常");
        }

        PayCreateRequest request = new PayCreateRequest();
        request.setBizOrderNo(order.getOrderNo());
        request.setBizType(bizType);
        request.setUserId(order.getUserId());
        request.setTotalAmount(order.getAmount());
        request.setPointsDeduct(BigDecimal.ZERO);
        request.setChannelCode(StringUtils.hasText(channelCode) ? channelCode : PayChannel.MOCK.name());
        request.setSubject(order.getItemName());
        request.setExpireMinutes(DEFAULT_EXPIRE_MINUTES);
        return create(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCreateResponse processOrderPayment(Long orderId, Long userId, String channelCode) {
        PayCreateResponse response = createForOrder(orderId, userId, channelCode);
        if (PayStatus.isSuccess(response.getStatus())) {
            return response;
        }
        if (!channelRouter.isMockMode()) {
            throw new BusinessException("请在支付渠道完成支付");
        }
        mockConfirm(response.getFlowNo(), userId);
        response.setStatus(PayStatus.SUCCESS.name());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mockConfirm(String flowNo, Long userId) {
        if (!StringUtils.hasText(flowNo)) {
            throw new BusinessException("支付流水号不能为空");
        }
        PayFlow flow = payFlowMapper.selectOne(new LambdaQueryWrapper<PayFlow>()
                .eq(PayFlow::getFlowNo, flowNo)
                .last("LIMIT 1"));
        if (flow == null) {
            throw new BusinessException("支付流水不存在");
        }
        PayOrder payOrder = getByPayNo(flow.getPayNo());
        if (payOrder == null) {
            throw new BusinessException("支付单不存在");
        }
        if (userId != null && payOrder.getUserId() != null && !userId.equals(payOrder.getUserId())) {
            throw new BusinessException(403, "无权操作该支付单");
        }
        if (PayStatus.isSuccess(payOrder.getStatus())) {
            return;
        }
        if (payOrder.getExpireTime() != null && payOrder.getExpireTime().isBefore(LocalDateTime.now())) {
            closePayOrder(payOrder);
            throw new BusinessException("支付已过期，请重新发起");
        }

        String channelTradeNo = "MOCK" + System.currentTimeMillis();
        flow.setStatus(PayStatus.SUCCESS.name());
        flow.setChannelResponse("{\"code\":\"SUCCESS\",\"channelTradeNo\":\"" + channelTradeNo + "\"}");
        payFlowMapper.updateById(flow);

        markPaid(payOrder, flow.getAmount(), channelTradeNo);
    }

    @Override
    public PayOrder getByPayNo(String payNo) {
        if (!StringUtils.hasText(payNo)) {
            return null;
        }
        return payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getPayNo, payNo)
                .last("LIMIT 1"));
    }

    @Override
    public PayOrderVO query(String payNo, Long userId) {
        PayOrder payOrder = getByPayNo(payNo);
        if (payOrder == null) {
            throw new BusinessException("支付单不存在");
        }
        checkOwner(payOrder, userId);
        return toVO(payOrder);
    }

    @Override
    public PayOrderVO getDetail(Long id, Long userId) {
        PayOrder payOrder = payOrderMapper.selectById(id);
        if (payOrder == null) {
            throw new BusinessException("支付单不存在");
        }
        checkOwner(payOrder, userId);
        return toVO(payOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(Long id, Long userId) {
        PayOrder payOrder = payOrderMapper.selectById(id);
        if (payOrder == null) {
            throw new BusinessException("支付单不存在");
        }
        checkOwner(payOrder, userId);
        if (!PayStatus.isPayable(payOrder.getStatus())) {
            throw new BusinessException("当前支付单不可关闭");
        }
        closePayOrder(payOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayRefund refund(String payNo, BigDecimal amount, String reason, Long afterSaleId) {
        PayOrder payOrder = getByPayNo(payNo);
        if (payOrder == null) {
            throw new BusinessException("支付单不存在");
        }
        if (!PayStatus.isRefundable(payOrder.getStatus())) {
            throw new BusinessException("当前支付单状态不可退款");
        }
        BigDecimal refundAmount = scale(amount);
        if (refundAmount.signum() <= 0) {
            throw new BusinessException("退款金额必须大于 0");
        }
        BigDecimal occupied = sumRefundAmount(payNo, true);
        BigDecimal remaining = payOrder.getPayAmount().subtract(occupied);
        if (refundAmount.compareTo(remaining) > 0) {
            throw new BusinessException("退款金额超过可退余额，可退金额：" + remaining);
        }

        PayRefund refund = new PayRefund();
        refund.setRefundNo(PayNoGenerator.refundNo());
        refund.setPayNo(payNo);
        refund.setBizOrderNo(payOrder.getBizOrderNo());
        refund.setAfterSaleId(afterSaleId);
        refund.setUserId(payOrder.getUserId());
        refund.setChannelCode(payOrder.getChannelCode());
        refund.setRefundAmount(refundAmount);
        refund.setReason(reason);
        refund.setStatus(RefundStatus.REFUNDING.getCode());
        payRefundMapper.insert(refund);

        RefundChannelRequest channelRequest = new RefundChannelRequest();
        channelRequest.setPayNo(payNo);
        channelRequest.setRefundNo(refund.getRefundNo());
        channelRequest.setAmount(refundAmount);
        channelRequest.setReason(reason);
        channelRequest.setChannelTradeNo(payOrder.getChannelTradeNo());

        try {
            PayChannelAdapter adapter = channelRouter.resolve(payOrder.getChannelCode());
            RefundChannelResult result = adapter.refund(channelRequest);
            if (result != null && result.isSuccess()) {
                markRefundSuccess(refund.getId(), result.getChannelRefundNo());
            } else {
                markRefundFailed(refund.getId(), result == null ? "渠道无响应" : result.getErrorMsg());
            }
        } catch (Exception e) {
            log.error("[退款] 渠道退款失败 payNo={}, refundNo={}", payNo, refund.getRefundNo(), e);
            markRefundFailed(refund.getId(), e.getMessage());
        }
        return payRefundMapper.selectById(refund.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundResult(Long refundId, boolean success, String channelRefundNo, String failReason) {
        if (success) {
            markRefundSuccess(refundId, channelRefundNo);
        } else {
            markRefundFailed(refundId, failReason);
            PayRefund refund = payRefundMapper.selectById(refundId);
            if (refund != null) {
                refreshPayOrderRefundStatus(refund.getPayNo());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleNotify(String channelCode, String rawBody, Map<String, String> headers) {
        PayNotifyLog notifyLog = new PayNotifyLog();
        notifyLog.setChannelCode(channelCode);
        notifyLog.setRawBody(rawBody);
        notifyLog.setHeaders(writeJson(headers));
        notifyLog.setVerifyResult(0);
        notifyLogService.create(notifyLog);

        try {
            PayChannelAdapter adapter = channelRouter.resolve(channelCode);
            NotifyResult result = adapter.verifyAndParseNotify(rawBody, headers == null ? Map.of() : headers);
            if (result == null || !result.isVerified()) {
                notifyLog.setProcessResult("ERROR");
                notifyLog.setErrorMsg("回调验签失败");
                notifyLogService.update(notifyLog);
                throw new BusinessException(400, "回调验签失败");
            }
            notifyLog.setVerifyResult(1);

            PayOrder payOrder = getByPayNo(result.getPayNo());
            if (payOrder == null) {
                notifyLog.setProcessResult("ERROR");
                notifyLog.setErrorMsg("支付单不存在：" + result.getPayNo());
                notifyLogService.update(notifyLog);
                throw new BusinessException("支付单不存在");
            }
            if (PayStatus.isSuccess(payOrder.getStatus())) {
                notifyLog.setProcessResult("IGNORED");
                notifyLogService.update(notifyLog);
                return "success";
            }
            if (!result.isSuccess()) {
                payOrder.setStatus(PayStatus.FAILED.name());
                payOrderMapper.updateById(payOrder);
                notifyLog.setProcessResult("SUCCESS");
                notifyLogService.update(notifyLog);
                return "success";
            }
            if (result.getAmount() != null
                    && result.getAmount().compareTo(payOrder.getPayAmount()) != 0) {
                notifyLog.setProcessResult("ERROR");
                notifyLog.setErrorMsg("回调金额与支付单金额不一致");
                notifyLogService.update(notifyLog);
                throw new BusinessException("回调金额校验失败");
            }

            markPaid(payOrder, payOrder.getPayAmount(), result.getChannelTradeNo());
            notifyLog.setProcessResult("SUCCESS");
            notifyLogService.update(notifyLog);
            return "success";
        } catch (BusinessException e) {
            notifyLog.setProcessResult("ERROR");
            notifyLog.setErrorMsg(e.getMessage());
            notifyLogService.update(notifyLog);
            throw e;
        } catch (Exception e) {
            notifyLog.setProcessResult("ERROR");
            notifyLog.setErrorMsg(e.getMessage());
            notifyLogService.update(notifyLog);
            log.error("[支付回调] 处理异常 channel={}", channelCode, e);
            throw new BusinessException("回调处理失败");
        }
    }

    @Override
    public PageResponse<PayOrderVO> pageOrders(PageRequest request, String payNo,
                                               String bizOrderNo, String status, String channelCode) {
        Page<PayOrder> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<PayOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(payNo)) {
            wrapper.like(PayOrder::getPayNo, payNo.trim());
        }
        if (StringUtils.hasText(bizOrderNo)) {
            wrapper.like(PayOrder::getBizOrderNo, bizOrderNo.trim());
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PayOrder::getStatus, status.trim());
        }
        if (StringUtils.hasText(channelCode)) {
            wrapper.eq(PayOrder::getChannelCode, channelCode.trim());
        }
        wrapper.orderByDesc(PayOrder::getCreatedAt);
        Page<PayOrder> result = payOrderMapper.selectPage(page, wrapper);
        List<PayOrderVO> records = result.getRecords().stream()
                .map(this::toVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResponse<PayFlow> pageFlows(PageRequest request, String payNo) {
        Page<PayFlow> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<PayFlow> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(payNo)) {
            wrapper.like(PayFlow::getPayNo, payNo.trim());
        }
        wrapper.orderByDesc(PayFlow::getCreatedAt);
        Page<PayFlow> result = payFlowMapper.selectPage(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(),
                request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResponse<PayNotifyLog> pageNotifyLogs(PageRequest request, String channelCode) {
        return notifyLogService.page(request, channelCode);
    }

    // ==================== 内部方法 ====================

    private PayOrder findLatest(String bizOrderNo, String bizType) {
        return payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getBizOrderNo, bizOrderNo)
                .eq(PayOrder::getBizType, bizType)
                .orderByDesc(PayOrder::getId)
                .last("LIMIT 1"));
    }

    /**
     * 校验业务单据存在、归属与金额一致，支付金额不再信任调用方。
     */
    private void validateBusiness(PayCreateRequest request, BigDecimal totalAmount) {
        String bizType = request.getBizType();
        if (PayBizType.isOrderType(bizType)) {
            Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                    .eq(Order::getOrderNo, request.getBizOrderNo())
                    .last("LIMIT 1"));
            if (order == null) {
                throw new BusinessException("业务订单不存在");
            }
            checkBusinessOwner(order.getUserId(), request.getUserId());
            if (order.getAmount() == null || order.getAmount().compareTo(totalAmount) != 0) {
                throw new BusinessException("支付金额与订单金额不一致");
            }
            return;
        }
        if (PayBizType.MEMBER.name().equals(bizType)) {
            MemberOrder order = memberOrderMapper.selectOne(new LambdaQueryWrapper<MemberOrder>()
                    .eq(MemberOrder::getOrderNo, request.getBizOrderNo())
                    .last("LIMIT 1"));
            if (order == null) {
                throw new BusinessException("会员订单不存在");
            }
            checkBusinessOwner(order.getUserId(), request.getUserId());
            if (order.getAmount() == null || order.getAmount().compareTo(totalAmount) != 0) {
                throw new BusinessException("支付金额与会员订单金额不一致");
            }
            return;
        }
        if (PayBizType.RAILWAY.name().equals(bizType)) {
            RailwayOrder order = railwayOrderMapper.selectOne(new LambdaQueryWrapper<RailwayOrder>()
                    .eq(RailwayOrder::getOrderNo, request.getBizOrderNo())
                    .last("LIMIT 1"));
            if (order == null) {
                throw new BusinessException("铁路订单不存在");
            }
            checkBusinessOwner(order.getUserId(), request.getUserId());
            BigDecimal orderAmount = BigDecimal.valueOf(order.getTotalAmount() == null ? 0 : order.getTotalAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (orderAmount.compareTo(totalAmount) != 0) {
                throw new BusinessException("支付金额与铁路订单金额不一致");
            }
        }
    }

    private void checkBusinessOwner(Long bizUserId, Long requestUserId) {
        if (bizUserId != null && requestUserId != null && !bizUserId.equals(requestUserId)) {
            throw new BusinessException(403, "无权支付该订单");
        }
    }

    /**
     * 幂等更新支付单为成功，并发布业务事件。
     */
    private void markPaid(PayOrder payOrder, BigDecimal amount, String channelTradeNo) {
        if (PayStatus.isSuccess(payOrder.getStatus())) {
            return;
        }
        if (amount != null && payOrder.getPayAmount() != null
                && amount.compareTo(payOrder.getPayAmount()) != 0) {
            throw new BusinessException("支付金额与支付单金额不一致");
        }
        LocalDateTime now = LocalDateTime.now();
        int updated = payOrderMapper.update(null, new LambdaUpdateWrapper<PayOrder>()
                .eq(PayOrder::getId, payOrder.getId())
                .in(PayOrder::getStatus, PayStatus.INIT.name(), PayStatus.PAYING.name())
                .set(PayOrder::getStatus, PayStatus.SUCCESS.name())
                .set(PayOrder::getPayTime, now)
                .set(PayOrder::getChannelTradeNo, channelTradeNo)
                .set(PayOrder::getUpdatedAt, now));
        if (updated == 0) {
            // 已被其他请求处理，保持幂等
            return;
        }
        eventPublisher.publishEvent(new PaySuccessEvent(payOrder.getPayNo()));
    }

    private void closePayOrder(PayOrder payOrder) {
        payOrderMapper.update(null, new LambdaUpdateWrapper<PayOrder>()
                .eq(PayOrder::getId, payOrder.getId())
                .in(PayOrder::getStatus, PayStatus.INIT.name(), PayStatus.PAYING.name())
                .set(PayOrder::getStatus, PayStatus.CLOSED.name())
                .set(PayOrder::getUpdatedAt, LocalDateTime.now()));
        payFlowMapper.update(null, new LambdaUpdateWrapper<PayFlow>()
                .eq(PayFlow::getPayNo, payOrder.getPayNo())
                .in(PayFlow::getStatus, PayStatus.INIT.name(), PayStatus.PAYING.name())
                .set(PayFlow::getStatus, PayStatus.FAILED.name()));
    }

    private void markRefundSuccess(Long refundId, String channelRefundNo) {
        PayRefund refund = payRefundMapper.selectById(refundId);
        if (refund == null || RefundStatus.SUCCESS.getCode().equals(refund.getStatus())) {
            return;
        }
        int updated = payRefundMapper.update(null, new LambdaUpdateWrapper<PayRefund>()
                .eq(PayRefund::getId, refundId)
                .in(PayRefund::getStatus, RefundStatus.PENDING.getCode(), RefundStatus.REFUNDING.getCode())
                .set(PayRefund::getStatus, RefundStatus.SUCCESS.getCode())
                .set(PayRefund::getChannelRefundNo, channelRefundNo)
                .set(PayRefund::getRefundTime, LocalDateTime.now()));
        if (updated == 0) {
            return;
        }
        refreshPayOrderRefundStatus(refund.getPayNo());
        eventPublisher.publishEvent(new PayRefundSuccessEvent(refundId));
    }

    private void markRefundFailed(Long refundId, String failReason) {
        payRefundMapper.update(null, new LambdaUpdateWrapper<PayRefund>()
                .eq(PayRefund::getId, refundId)
                .in(PayRefund::getStatus, RefundStatus.PENDING.getCode(), RefundStatus.REFUNDING.getCode())
                .set(PayRefund::getStatus, RefundStatus.FAILED.getCode())
                .set(PayRefund::getFailReason, failReason));
    }

    /**
     * 根据退款累计情况刷新支付单状态：全额退款 -> REFUNDED，部分退款 -> SUCCESS/REFUNDING。
     */
    private void refreshPayOrderRefundStatus(String payNo) {
        PayOrder payOrder = getByPayNo(payNo);
        if (payOrder == null) {
            return;
        }
        BigDecimal refunded = sumRefundAmount(payNo, false);
        BigDecimal processing = sumRefundAmount(payNo, true).subtract(refunded);
        String status;
        if (refunded.compareTo(payOrder.getPayAmount()) >= 0) {
            status = PayStatus.REFUNDED.name();
        } else if (processing.signum() > 0) {
            status = PayStatus.REFUNDING.name();
        } else {
            status = PayStatus.SUCCESS.name();
        }
        payOrderMapper.update(null, new LambdaUpdateWrapper<PayOrder>()
                .eq(PayOrder::getId, payOrder.getId())
                .set(PayOrder::getStatus, status)
                .set(PayOrder::getUpdatedAt, LocalDateTime.now()));
    }

    /**
     * 汇总退款金额。
     *
     * @param includeProcessing true 时包含退款中的金额（用于可退余额校验），false 时仅统计成功退款
     */
    private BigDecimal sumRefundAmount(String payNo, boolean includeProcessing) {
        LambdaQueryWrapper<PayRefund> wrapper = new LambdaQueryWrapper<PayRefund>()
                .eq(PayRefund::getPayNo, payNo);
        if (includeProcessing) {
            wrapper.in(PayRefund::getStatus,
                    RefundStatus.PENDING.getCode(),
                    RefundStatus.REFUNDING.getCode(),
                    RefundStatus.SUCCESS.getCode());
        } else {
            wrapper.eq(PayRefund::getStatus, RefundStatus.SUCCESS.getCode());
        }
        return payRefundMapper.selectList(wrapper).stream()
                .map(PayRefund::getRefundAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void checkOwner(PayOrder payOrder, Long userId) {
        if (userId != null && payOrder.getUserId() != null && !userId.equals(payOrder.getUserId())) {
            throw new BusinessException(403, "无权查看该支付单");
        }
    }

    private PayCreateResponse toResponse(PayOrder payOrder, PayFlow flow, Map<String, Object> payParams) {
        PayCreateResponse response = new PayCreateResponse();
        response.setPayNo(payOrder.getPayNo());
        response.setFlowNo(flow == null ? null : flow.getFlowNo());
        response.setChannelCode(payOrder.getChannelCode());
        response.setPayParams(payParams);
        response.setExpireTime(payOrder.getExpireTime());
        response.setStatus(payOrder.getStatus());
        response.setAmount(payOrder.getPayAmount());
        return response;
    }

    private PayOrderVO toVO(PayOrder payOrder) {
        PayOrderVO vo = new PayOrderVO();
        vo.setId(payOrder.getId());
        vo.setPayNo(payOrder.getPayNo());
        vo.setBizOrderNo(payOrder.getBizOrderNo());
        vo.setBizType(payOrder.getBizType());
        vo.setUserId(payOrder.getUserId());
        vo.setTotalAmount(payOrder.getTotalAmount());
        vo.setPayAmount(payOrder.getPayAmount());
        vo.setPointsDeduct(payOrder.getPointsDeduct());
        vo.setChannelCode(payOrder.getChannelCode());
        vo.setStatus(payOrder.getStatus());
        vo.setStatusDesc(statusDesc(payOrder.getStatus()));
        vo.setChannelTradeNo(payOrder.getChannelTradeNo());
        vo.setSubject(payOrder.getSubject());
        vo.setExpireTime(payOrder.getExpireTime());
        vo.setPayTime(payOrder.getPayTime());
        vo.setCreatedAt(payOrder.getCreatedAt());
        vo.setRefundedAmount(sumRefundAmount(payOrder.getPayNo(), false));
        return vo;
    }

    private String statusDesc(String status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case "INIT" -> "待支付";
            case "PAYING" -> "支付中";
            case "SUCCESS" -> "支付成功";
            case "FAILED" -> "支付失败";
            case "CLOSED" -> "已关闭";
            case "REFUNDING" -> "退款中";
            case "REFUNDED" -> "已退款";
            default -> status;
        };
    }

    private BigDecimal scale(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
