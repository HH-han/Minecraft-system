package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.common.PaymentMethod;
import com.minecraft.entity.Order;
import com.minecraft.entity.Payment;
import com.minecraft.enums.PaymentStatus;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.mapper.PaymentMapper;
import com.minecraft.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Autowired
    private OrderMapper orderMapper;
    private static final Random random = new Random();

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Payment createPayment(Long orderId, Long userId, PaymentMethod paymentMethod) {
        // 调用 processPayment 方法，确保支付成功后更新订单状态
        return processPayment(orderId, userId, paymentMethod);
    }

    @Override
    public Payment getPaymentDetail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public void cancelPayment(Long id) {
        Payment payment = getById(id);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        payment.setStatus(PaymentStatus.CANCELLED.getCode().toString());
        payment.setUpdateTime(LocalDateTime.now());
        updateById(payment);
    }

    /**
     * 处理支付请求，模拟支付网关调用
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param paymentMethod 支付方式
     * @return 支付结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Payment processPayment(Long orderId, Long userId, PaymentMethod paymentMethod) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setUserId(userId);
            payment.setPaymentMethod(paymentMethod.getChineseName());
            payment.setAmount(order.getAmount().multiply(new java.math.BigDecimal(order.getQuantity())));
            payment.setStatus(PaymentStatus.PENDING.getCode().toString());
            payment.setPaymentNo(generatePaymentNo());
            payment.setCreateTime(LocalDateTime.now());
            save(payment);
            
            // 模拟支付网关调用，80%的成功率
            boolean paymentSuccess = simulatePaymentGateway();
            
            if (paymentSuccess) {
                payment.setStatus(PaymentStatus.SUCCESS.getCode().toString());
                log.info("支付成功，订单ID：{}，支付编号：{}", orderId, payment.getPaymentNo());
                
                // 更新订单状态为已支付
                if (order != null) {
                    order.setStatus(com.minecraft.enums.OrderStatus.PAID.getCode().toString());
                    order.setUpdateTime(LocalDateTime.now());
                    orderMapper.updateById(order);
                    log.info("订单状态更新成功，订单ID：{}", orderId);
                }
            } else {
                payment.setStatus(PaymentStatus.FAILED.getCode().toString());
                log.info("支付失败，订单ID：{}，支付编号：{}", orderId, payment.getPaymentNo());
            }
            
            payment.setUpdateTime(LocalDateTime.now());
            updateById(payment);
            return payment;
        } catch (Exception e) {
            log.error("支付处理失败，订单ID：{}，支付方式：{}", orderId, paymentMethod.getChineseName(), e);
            throw new RuntimeException("支付处理失败：" + e.getMessage());
        }
    }

    /**
     * 验证支付状态转换是否合法
     * @param current 当前支付状态
     * @param newStatus 新的支付状态
     * @return 如果状态转换合法返回true，否则返回false
     */
    @Override
    public boolean isValidStatusTransition(String current, PaymentStatus newStatus) {
        if (current == null) {
            return newStatus == PaymentStatus.PENDING;
        }
        
        PaymentStatus currentStatus;
        try {
            currentStatus = PaymentStatus.fromCode(Integer.parseInt(current));
        } catch (NumberFormatException e) {
            return false;
        }
        
        return switch (currentStatus) {
            case PENDING -> newStatus == PaymentStatus.SUCCESS || newStatus == PaymentStatus.FAILED || newStatus == PaymentStatus.CANCELLED;
            case SUCCESS -> newStatus == PaymentStatus.REFUNDING;
            case FAILED -> newStatus == PaymentStatus.PENDING;
            case CANCELLED -> false;
            case REFUNDING -> newStatus == PaymentStatus.REFUNDED;
            case REFUNDED -> false;
            default -> false;
        };
    }

    /**
     * 更新支付状态
     * @param id 支付记录ID
     * @param status 新的支付状态
     */
    @Override
    @Transactional
    public void updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = getById(id);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        
        if (!isValidStatusTransition(payment.getStatus(), status)) {
            throw new RuntimeException("非法状态转换");
        }
        
        payment.setStatus(status.getCode().toString());
        payment.setUpdateTime(LocalDateTime.now());
        updateById(payment);
    }

    /**
     * 生成唯一的支付编号
     * @return 生成的支付编号
     */
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + random.nextInt(9999);
    }

    /**
     * 模拟支付网关调用，返回支付是否成功
     * @return 支付成功返回true，否则返回false
     */
    private boolean simulatePaymentGateway() {
        return random.nextDouble() < 0.8; // 80%成功率
    }
}
