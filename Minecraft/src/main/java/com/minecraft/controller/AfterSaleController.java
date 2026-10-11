package com.minecraft.controller;

import com.minecraft.dto.request.AfterSaleApplyRequest;
import com.minecraft.dto.request.AfterSaleMessageRequest;
import com.minecraft.dto.request.AfterSaleReturnRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.AfterSaleVO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.service.AfterSaleService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 售后用户端接口
 */
@Tag(name = "售后服务")
@RestController
@RequestMapping("/api/after-sale")
@RequiredArgsConstructor
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    @Operation(summary = "申请售后")
    @PostMapping("/apply")
    public ApiResponse<AfterSaleVO> apply(@Valid @RequestBody AfterSaleApplyRequest request) {
        return ApiResponse.success("申请成功", afterSaleService.apply(SecurityUtils.getCurrentUserId(), request));
    }

    @Operation(summary = "我的售后列表")
    @GetMapping("/page")
    public ApiResponse<PageResponse<?>> page(PageRequest request) {
        return ApiResponse.success(afterSaleService.pageByUser(SecurityUtils.getCurrentUserId(), request));
    }

    @Operation(summary = "售后详情")
    @GetMapping("/{id}")
    public ApiResponse<AfterSaleVO> detail(@PathVariable Long id) {
        return ApiResponse.success(afterSaleService.detail(id, SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "填写退货物流")
    @PostMapping("/{id}/return")
    public ApiResponse<Void> returnGoods(@PathVariable Long id, @Valid @RequestBody AfterSaleReturnRequest request) {
        afterSaleService.returnGoods(id, SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.success("提交成功", null);
    }

    @Operation(summary = "撤销申请")
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        afterSaleService.cancel(id, SecurityUtils.getCurrentUserId());
        return ApiResponse.success("已撤销", null);
    }

    @Operation(summary = "协商留言")
    @PostMapping("/{id}/message")
    public ApiResponse<Void> message(@PathVariable Long id, @RequestBody AfterSaleMessageRequest request) {
        afterSaleService.message(id, SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.success("发送成功", null);
    }
}
