package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.LogisticsTrackDTO;
import com.minecraft.dto.response.OrderShippingVO;
import com.minecraft.service.LogisticsTrackService;
import com.minecraft.service.OrderShippingService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流用户端接口
 */
@Tag(name = "物流查询")
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class OrderShippingController {

    private final OrderShippingService orderShippingService;
    private final LogisticsTrackService logisticsTrackService;

    @Operation(summary = "查询订单下所有包裹")
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<OrderShippingVO>> listByOrder(@PathVariable Long orderId) {
        return ApiResponse.success(orderShippingService.listByOrder(orderId));
    }

    @Operation(summary = "查询运单轨迹")
    @GetMapping("/track/{trackingNo}")
    public ApiResponse<List<LogisticsTrackDTO>> track(@PathVariable String trackingNo,
                                                      @RequestParam(required = false) String phone) {
        return ApiResponse.success(logisticsTrackService.queryTrack(trackingNo, phone));
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{shippingId}")
    public ApiResponse<Void> confirm(@PathVariable Long shippingId) {
        orderShippingService.confirmReceive(shippingId, SecurityUtils.getCurrentUserId());
        return ApiResponse.success("确认成功", null);
    }

    @Operation(summary = "订阅物流推送")
    @PostMapping("/subscribe/{shippingId}")
    public ApiResponse<Void> subscribe(@PathVariable Long shippingId) {
        orderShippingService.subscribe(shippingId, SecurityUtils.getCurrentUserId());
        return ApiResponse.success("订阅成功", null);
    }

    @Operation(summary = "物流轨迹回调（第三方推送）")
    @PostMapping("/callback/{carrierCode}")
    public ApiResponse<Void> callback(@PathVariable String carrierCode,
                                      @RequestBody(required = false) String body) {
        // TODO 验签 → 解析轨迹 → 落库(logistics_track) → WebSocket/SSE 推送
        // 注意：SecurityConfig 对该路径放行，必须在业务代码内自行验签
        return ApiResponse.success("ok", null);
    }
}
