package com.minecraft.pay.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.pay.dto.PayOrderVO;
import com.minecraft.pay.dto.PayRefundRequest;
import com.minecraft.pay.entity.PayFlow;
import com.minecraft.pay.entity.PayNotifyLog;
import com.minecraft.pay.entity.PayRefund;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 支付管理（管理端）
 */
@Tag(name = "支付管理（管理端）")
@RestController
@RequestMapping("/api/admin/pay")
@RequiredArgsConstructor
public class PayAdminController {

    private final PayGatewayService payGatewayService;
    private final AdminAuthorizationService adminAuthorizationService;

    @Operation(summary = "支付主单分页")
    @GetMapping("/orders")
    public ApiResponse<PageResponse<PayOrderVO>> pageOrders(
            PageRequest request,
            @RequestParam(required = false) String payNo,
            @RequestParam(required = false) String bizOrderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String channelCode) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(payGatewayService.pageOrders(
                request, payNo, bizOrderNo, status, channelCode));
    }

    @Operation(summary = "支付流水分页")
    @GetMapping("/flows")
    public ApiResponse<PageResponse<PayFlow>> pageFlows(
            PageRequest request,
            @RequestParam(required = false) String payNo) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(payGatewayService.pageFlows(request, payNo));
    }

    @Operation(summary = "支付回调日志分页")
    @GetMapping("/notify-logs")
    public ApiResponse<PageResponse<PayNotifyLog>> pageNotifyLogs(
            PageRequest request,
            @RequestParam(required = false) String channelCode) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(payGatewayService.pageNotifyLogs(request, channelCode));
    }

    @Operation(summary = "按支付单发起退款")
    @PostMapping("/{payNo}/refund")
    public ApiResponse<PayRefund> refund(@PathVariable String payNo,
                                         @RequestParam BigDecimal refundAmount,
                                         @RequestParam(required = false) String reason) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("退款已发起",
                payGatewayService.refund(payNo, refundAmount, reason, null));
    }

    @Operation(summary = "发起售后退款（管理员）")
    @PostMapping("/refund")
    public ApiResponse<PayRefund> refundByBody(@Valid @RequestBody PayRefundRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("退款已发起",
                payGatewayService.refund(request.getPayNo(), request.getRefundAmount(),
                        request.getReason(), null));
    }
}
