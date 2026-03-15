package com.minecraft.controller;

import com.minecraft.common.PaymentMethod;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Payment;
import com.minecraft.service.PaymentService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "支付管理")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary ="创建支付")
    @PostMapping("/create")
    public ApiResponse<Payment> createPayment(
            @RequestParam Long orderId,
            @RequestParam PaymentMethod paymentMethod) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success("创建成功", paymentService.createPayment(orderId, userId, paymentMethod));
    }

    @Operation(summary ="获取支付详情")
    @GetMapping("/{id}")
    public ApiResponse<Payment> getPaymentDetail(@PathVariable Long id) {
        return ApiResponse.success(paymentService.getPaymentDetail(id));
    }

    @Operation(summary ="取消支付")
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancelPayment(@PathVariable Long id) {
        paymentService.cancelPayment(id);
        return ApiResponse.success("取消成功", null);
    }
}
