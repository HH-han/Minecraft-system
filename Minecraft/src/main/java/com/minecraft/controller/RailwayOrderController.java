package com.minecraft.controller;

import com.minecraft.dto.request.RailwayOrderCreateRequest;
import com.minecraft.dto.request.RailwayPassengerRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayOrderVO;
import com.minecraft.entity.RailwayPassenger;
import com.minecraft.service.RailwayOrderService;
import com.minecraft.service.RailwayPassengerService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "铁路订单")
@RestController
@RequestMapping("/api/railway")
public class RailwayOrderController {

    private final RailwayOrderService orderService;
    private final RailwayPassengerService passengerService;

    public RailwayOrderController(RailwayOrderService orderService,
                                  RailwayPassengerService passengerService) {
        this.orderService = orderService;
        this.passengerService = passengerService;
    }

    @Operation(summary = "提交订单（占座）")
    @PostMapping("/orders")
    public ApiResponse<RailwayOrderVO> createOrder(
            @Valid @RequestBody RailwayOrderCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success("下单成功", orderService.createOrder(userId, request));
    }

    @Operation(summary = "我的车票订单")
    @GetMapping("/orders")
    public ApiResponse<PageResponse<RailwayOrderVO>> myOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(orderService.listOrders(userId, pageNum, pageSize, status));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/orders/{id}")
    public ApiResponse<RailwayOrderVO> orderDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(orderService.getOrder(userId, id));
    }

    @Operation(summary = "模拟支付")
    @PostMapping("/orders/{id}/pay")
    public ApiResponse<Void> payOrder(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.payOrder(userId, id);
        return ApiResponse.success("支付成功", null);
    }

    @Operation(summary = "取消 / 退票")
    @PostMapping("/orders/{id}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancelOrder(userId, id);
        return ApiResponse.success("操作成功", null);
    }

    @Operation(summary = "我的乘车人")
    @GetMapping("/passengers")
    public ApiResponse<List<RailwayPassenger>> passengers() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(passengerService.listByUser(userId));
    }

    @Operation(summary = "新增乘车人")
    @PostMapping("/passengers")
    public ApiResponse<RailwayPassenger> addPassenger(
            @Valid @RequestBody RailwayPassengerRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success("添加成功", passengerService.addPassenger(userId, request));
    }

    @Operation(summary = "删除乘车人")
    @DeleteMapping("/passengers/{id}")
    public ApiResponse<Void> deletePassenger(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        passengerService.deletePassenger(userId, id);
        return ApiResponse.success("删除成功", null);
    }
}
