package com.minecraft.pay.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.exception.BusinessException;
import com.minecraft.pay.dto.PayCreateRequest;
import com.minecraft.pay.dto.PayCreateResponse;
import com.minecraft.pay.dto.PayOrderVO;
import com.minecraft.pay.dto.PayRefundRequest;
import com.minecraft.pay.entity.PayRefund;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 聚合支付 API：所有业务模块统一通过本入口发起支付
 */
@Tag(name = "聚合支付")
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayGatewayService payGatewayService;
    private final AdminAuthorizationService adminAuthorizationService;

    @Operation(summary = "创建支付")
    @PostMapping("/create")
    public ApiResponse<PayCreateResponse> create(@Valid @RequestBody PayCreateRequest request) {
        request.setUserId(requireLogin());
        return ApiResponse.success("创建成功", payGatewayService.create(request));
    }

    @Operation(summary = "统一支付回调入口")
    @PostMapping("/notify/{channelCode}")
    public String notify(@PathVariable String channelCode,
                         @RequestBody(required = false) String rawBody,
                         @RequestHeader Map<String, String> headers) {
        return payGatewayService.handleNotify(channelCode, rawBody, headers);
    }

    @Operation(summary = "查询支付详情")
    @GetMapping("/query/{payNo}")
    public ApiResponse<PayOrderVO> query(@PathVariable String payNo) {
        return ApiResponse.success(payGatewayService.query(payNo, requireLogin()));
    }

    @Operation(summary = "模拟支付确认")
    @PostMapping("/mock/confirm/{flowNo}")
    public ApiResponse<Void> mockConfirm(@PathVariable String flowNo) {
        payGatewayService.mockConfirm(flowNo, requireLogin());
        return ApiResponse.success("支付成功", null);
    }

    @Operation(summary = "发起退款（管理员）")
    @PostMapping("/refund")
    public ApiResponse<PayRefund> refund(@Valid @RequestBody PayRefundRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("退款已发起",
                payGatewayService.refund(request.getPayNo(), request.getRefundAmount(),
                        request.getReason(), null));
    }

    private Long requireLogin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
