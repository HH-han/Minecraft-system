package com.minecraft.controller;

import com.minecraft.annotation.OperationLog;
import com.minecraft.dto.request.AfterSaleAuditRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.AfterSaleVO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.enums.OperationType;
import com.minecraft.pay.entity.PayRefund;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.service.AfterSaleService;
import com.minecraft.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 售后管理端接口（管理员）
 */
@Tag(name = "售后管理（管理端）")
@RestController
@RequestMapping("/api/admin/after-sale")
@RequiredArgsConstructor
public class AfterSaleAdminController {

    private final AdminAuthorizationService adminAuthorizationService;
    private final AfterSaleService afterSaleService;
    private final RefundService refundService;

    @Operation(summary = "售后列表")
    @GetMapping("/page")
    public ApiResponse<PageResponse<?>> page(PageRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(afterSaleService.pageForAdmin(request));
    }

    @Operation(summary = "售后详情")
    @GetMapping("/{id}")
    public ApiResponse<AfterSaleVO> detail(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(afterSaleService.detail(id, null));
    }

    @Operation(summary = "审核")
    @OperationLog(module = "AFTER_SALE", action = "售后审核", type = OperationType.UPDATE,
            bizId = "#id",
            detail = "'审核结果：' + (#request.approved ? '通过' : '驳回')"
                    + " + (#request.auditRemark != null ? '，备注：' + #request.auditRemark : '')")
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id, @Valid @RequestBody AfterSaleAuditRequest request) {
        Long adminId = adminAuthorizationService.requireAdmin();
        afterSaleService.audit(id, request, adminId);
        return ApiResponse.success("审核完成", null);
    }

    @Operation(summary = "确认收货")
    @OperationLog(module = "AFTER_SALE", action = "确认收货", type = OperationType.UPDATE, bizId = "#id")
    @PostMapping("/{id}/receive")
    public ApiResponse<Void> receive(@PathVariable Long id) {
        Long adminId = adminAuthorizationService.requireAdmin();
        afterSaleService.receive(id, adminId);
        return ApiResponse.success("已确认收货", null);
    }

    @Operation(summary = "发起退款")
    @OperationLog(module = "AFTER_SALE", action = "发起退款", type = OperationType.ADD, bizId = "#id")
    @PostMapping("/{id}/refund")
    public ApiResponse<PayRefund> refund(@PathVariable Long id) {
        Long adminId = adminAuthorizationService.requireAdmin();
        return ApiResponse.success("退款已发起", refundService.createRefund(id, adminId));
    }

    @Operation(summary = "关闭售后")
    @OperationLog(module = "AFTER_SALE", action = "关闭售后", type = OperationType.UPDATE,
            bizId = "#id", detail = "(#reason != null ? '关闭原因：' + #reason : '关闭售后')")
    @PostMapping("/{id}/close")
    public ApiResponse<Void> close(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Long adminId = adminAuthorizationService.requireAdmin();
        afterSaleService.close(id, reason, adminId);
        return ApiResponse.success("已关闭", null);
    }
}
