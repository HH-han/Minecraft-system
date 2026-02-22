package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Order;
import com.minecraft.entity.Payment;
import com.minecraft.enums.PaymentStatus;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.mapper.PaymentMapper;
import com.minecraft.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Payment createPayment(Long orderId, Long userId, String paymentMethod) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(order.getAmount().multiply(new java.math.BigDecimal(order.getQuantity())));
        payment.setStatus(PaymentStatus.PENDING.getCode().toString());
        save(payment);
        return payment;
    }

    @Override
    public Payment getPaymentDetail(Long id) {
        return getById(id);
    }

    @Override
    public void cancelPayment(Long id) {
        Payment payment = getById(id);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        payment.setStatus(PaymentStatus.CANCELLED.getCode().toString());
        updateById(payment);
    }
}
