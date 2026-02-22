package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Payment;

public interface PaymentService extends IService<Payment> {
    Payment createPayment(Long orderId, Long userId, String paymentMethod);
    Payment getPaymentDetail(Long id);
    void cancelPayment(Long id);
}
