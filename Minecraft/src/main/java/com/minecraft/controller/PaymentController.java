package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.exception.BusinessException;
import com.minecraft.pay.dto.PayCreateResponse;
import com.minecraft.pay.dto.PayOrderVO;
import com.minecraft.pay.enums.PayChannel;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商城/预订支付兼容接口：内部统一委托给聚合支付网关。
 * 新业务请直接使用 /api/pay/*。
 */
@Tag(name = "支付管理（兼容接口）")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PayGatewayService payGatewayService;

    @Operation(summary = "创建支付")
    @PostMapping("/create")
    public ApiResponse<PayCreateResponse> createPayment(
            @RequestParam Long orderId,
            @RequestParam(required = false) String paymentMethod) {
        return ApiResponse.success("创建成功", payGatewayService.createForOrder(
                orderId, requireLogin(), PayChannel.normalize(paymentMethod)));
    }

    @Operation(summary = "处理支付（模拟渠道自动确认）")
    @PostMapping("/process")
    public ApiResponse<PayCreateResponse> processPayment(
            @RequestParam Long orderId,
            @RequestParam(required = false) String paymentMethod) {
        return ApiResponse.success("支付处理成功", payGatewayService.processOrderPayment(
                orderId, requireLogin(), PayChannel.normalize(paymentMethod)));
    }

    @Operation(summary = "获取支付详情")
    @GetMapping("/{id}")
    public ApiResponse<PayOrderVO> getPaymentDetail(@PathVariable Long id) {
        return ApiResponse.success(payGatewayService.getDetail(id, requireLogin()));
    }

    @Operation(summary = "取消支付")
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancelPayment(@PathVariable Long id) {
        payGatewayService.close(id, requireLogin());
        return ApiResponse.success("取消成功", null);
    }

    private Long requireLogin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
