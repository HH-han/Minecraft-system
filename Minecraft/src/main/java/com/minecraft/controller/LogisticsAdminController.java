package com.minecraft.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.CarrierSaveRequest;
import com.minecraft.dto.request.FreightSaveRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.ShippingBatchRequest;
import com.minecraft.dto.request.ShippingCreateRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.LogisticsTrackDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.FreightTemplate;
import com.minecraft.entity.LogisticsCarrier;
import com.minecraft.entity.OrderShipping;
import com.minecraft.mapper.FreightTemplateMapper;
import com.minecraft.mapper.LogisticsCarrierMapper;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.service.LogisticsTrackService;
import com.minecraft.service.OrderShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流管理端接口（管理员）
 */
@Tag(name = "物流管理（管理端）")
@RestController
@RequestMapping("/api/admin/logistics")
@RequiredArgsConstructor
public class LogisticsAdminController {

    private final AdminAuthorizationService adminAuthorizationService;
    private final OrderShippingService orderShippingService;
    private final LogisticsTrackService logisticsTrackService;
    private final LogisticsCarrierMapper carrierMapper;
    private final FreightTemplateMapper freightTemplateMapper;

    // ==================== 发货单 ====================

    @Operation(summary = "发货单分页列表（仅纪念品等实物商品）")
    @GetMapping("/shipping/page")
    public ApiResponse<PageResponse<?>> shippingPage(PageRequest request) {
        adminAuthorizationService.requireAdmin();
        Page<OrderShipping> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<OrderShipping> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrderShipping::getItemType, OrderShippingService.SHIPPABLE_ITEM_TYPES)
                .orderByDesc(OrderShipping::getCreateTime);
        Page<OrderShipping> result = orderShippingService.page(page, wrapper);
        return ApiResponse.success(new PageResponse<>(
                result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize()));
    }

    @Operation(summary = "待发货订单分页列表（仅纪念品等实物商品，已支付未发货）")
    @GetMapping("/shipping/pending")
    public ApiResponse<PageResponse<?>> pendingShippingPage(PageRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(orderShippingService.listPendingOrders(request));
    }

    @Operation(summary = "创建发货单（录入收货信息与运单）")
    @PostMapping("/shipping")
    public ApiResponse<OrderShipping> createShipping(@Valid @RequestBody ShippingCreateRequest request) {
        Long adminId = adminAuthorizationService.requireAdmin();
        return ApiResponse.success("发货成功", orderShippingService.createShipping(request, adminId));
    }

    @Operation(summary = "批量发货")
    @PostMapping("/shipping/batch")
    public ApiResponse<Void> batchShipping(@Valid @RequestBody ShippingBatchRequest request) {
        Long adminId = adminAuthorizationService.requireAdmin();
        orderShippingService.batchCreateShipping(request, adminId);
        return ApiResponse.success("批量发货成功", null);
    }

    @Operation(summary = "查询发货单轨迹")
    @GetMapping("/shipping/{id}/track")
    public ApiResponse<List<LogisticsTrackDTO>> shippingTrack(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(logisticsTrackService.listByShipping(id));
    }

    // ==================== 承运商 ====================

    @Operation(summary = "承运商列表")
    @GetMapping("/carrier/list")
    public ApiResponse<List<LogisticsCarrier>> carrierList() {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(carrierMapper.selectList(
                new LambdaQueryWrapper<LogisticsCarrier>().orderByAsc(LogisticsCarrier::getSort)));
    }

    @Operation(summary = "新增/编辑承运商")
    @PostMapping("/carrier")
    public ApiResponse<LogisticsCarrier> saveCarrier(@RequestBody CarrierSaveRequest request) {
        adminAuthorizationService.requireAdmin();
        LogisticsCarrier carrier = new LogisticsCarrier();
        carrier.setId(request.getId());
        carrier.setCode(request.getCode());
        carrier.setName(request.getName());
        carrier.setLogoUrl(request.getLogoUrl());
        carrier.setApiType(request.getApiType());
        carrier.setApiKey(request.getApiKey());
        carrier.setApiSecret(request.getApiSecret());
        carrier.setApiCustomer(request.getApiCustomer());
        carrier.setPhone(request.getPhone());
        carrier.setWebsite(request.getWebsite());
        carrier.setSort(request.getSort());
        carrier.setStatus(request.getStatus());
        if (carrier.getId() == null) {
            carrierMapper.insert(carrier);
        } else {
            carrierMapper.updateById(carrier);
        }
        return ApiResponse.success("保存成功", carrier);
    }

    @Operation(summary = "删除承运商")
    @DeleteMapping("/carrier/{id}")
    public ApiResponse<Void> deleteCarrier(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        carrierMapper.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 运费模板 ====================

    @Operation(summary = "运费模板列表")
    @GetMapping("/freight/list")
    public ApiResponse<List<FreightTemplate>> freightList() {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(freightTemplateMapper.selectList(null));
    }

    @Operation(summary = "新增/编辑运费模板")
    @PostMapping("/freight")
    public ApiResponse<FreightTemplate> saveFreight(@RequestBody FreightSaveRequest request) {
        adminAuthorizationService.requireAdmin();
        FreightTemplate template = new FreightTemplate();
        template.setId(request.getId());
        template.setName(request.getName());
        template.setChargeType(request.getChargeType());
        template.setFirstUnit(request.getFirstUnit());
        template.setFirstPrice(request.getFirstPrice());
        template.setExtraUnit(request.getExtraUnit());
        template.setExtraPrice(request.getExtraPrice());
        template.setFreeRegions(request.getFreeRegions());
        template.setExcludeRegions(request.getExcludeRegions());
        template.setStatus(request.getStatus());
        if (template.getId() == null) {
            freightTemplateMapper.insert(template);
        } else {
            freightTemplateMapper.updateById(template);
        }
        return ApiResponse.success("保存成功", template);
    }

    @Operation(summary = "删除运费模板")
    @DeleteMapping("/freight/{id}")
    public ApiResponse<Void> deleteFreight(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        freightTemplateMapper.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
