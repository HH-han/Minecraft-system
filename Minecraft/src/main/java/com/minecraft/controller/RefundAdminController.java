package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 退款管理端接口（管理员）
 */
@Tag(name = "退款管理（管理端）")
@RestController
@RequestMapping("/api/admin/refund")
@RequiredArgsConstructor
public class RefundAdminController {

    private final AdminAuthorizationService adminAuthorizationService;
    private final RefundService refundService;

    @Operation(summary = "退款单列表")
    @GetMapping("/page")
    public ApiResponse<PageResponse<?>> page(PageRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(refundService.page(request));
    }

    @Operation(summary = "模拟渠道回调（标记退款成功/失败）")
    @PostMapping("/{id}/notify")
    public ApiResponse<Void> notify(@PathVariable Long id,
                                    @RequestParam boolean success,
                                    @RequestParam(required = false) String channelRefundNo,
                                    @RequestParam(required = false) String failReason) {
        adminAuthorizationService.requireAdmin();
        refundService.notifyResult(id, success, channelRefundNo, failReason);
        return ApiResponse.success("已更新", null);
    }
}
