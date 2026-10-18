package com.minecraft.pay.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.entity.AfterSale;
import com.minecraft.entity.Order;
import com.minecraft.enums.AfterSaleStatus;
import com.minecraft.enums.OrderStatus;
import com.minecraft.enums.RefundStatus;
import com.minecraft.mapper.AfterSaleMapper;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.entity.PayRefund;
import com.minecraft.pay.enums.PayBizType;
import com.minecraft.pay.event.PayRefundSuccessEvent;
import com.minecraft.pay.mapper.PayOrderMapper;
import com.minecraft.pay.mapper.PayRefundMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 退款成功处理：同步售后单、业务订单与退款状态
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRefundBizListener {

    private final PayRefundMapper payRefundMapper;
    private final PayOrderMapper payOrderMapper;
    private final AfterSaleMapper afterSaleMapper;
    private final OrderMapper orderMapper;

    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void onRefundSuccess(PayRefundSuccessEvent event) {
        PayRefund refund = payRefundMapper.selectById(event.getRefundId());
        if (refund == null) {
            return;
        }
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getPayNo, refund.getPayNo())
                .last("LIMIT 1"));
        if (payOrder == null) {
            return;
        }

        // 售后单闭环
        if (refund.getAfterSaleId() != null) {
            AfterSale afterSale = afterSaleMapper.selectById(refund.getAfterSaleId());
            if (afterSale != null) {
                afterSale.setStatus(AfterSaleStatus.COMPLETED.getCode());
                afterSale.setRefundNo(refund.getRefundNo());
                afterSale.setRefundTime(refund.getRefundTime());
                afterSaleMapper.updateById(afterSale);
            }
        }

        if (!PayBizType.isOrderType(payOrder.getBizType())) {
            return;
        }
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, payOrder.getBizOrderNo())
                .last("LIMIT 1"));
        if (order == null) {
            return;
        }
        order.setAfterSaleStatus(2);
        if (isFullRefund(refund.getPayNo(), payOrder.getPayAmount())) {
            order.setStatus(OrderStatus.REFUNDED.getCode().toString());
        }
        orderMapper.updateById(order);

        log.info("[退款成功] 业务订单已同步 orderNo={}, refundNo={}, fullRefund={}",
                order.getOrderNo(), refund.getRefundNo(), isFullRefund(refund.getPayNo(), payOrder.getPayAmount()));
    }

    private boolean isFullRefund(String payNo, BigDecimal payAmount) {
        if (payAmount == null) {
            return false;
        }
        List<PayRefund> refunds = payRefundMapper.selectList(new LambdaQueryWrapper<PayRefund>()
                .eq(PayRefund::getPayNo, payNo)
                .eq(PayRefund::getStatus, RefundStatus.SUCCESS.getCode()));
        BigDecimal refunded = refunds.stream()
                .map(PayRefund::getRefundAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return refunded.compareTo(payAmount) >= 0;
    }
}
