package com.minecraft.controller;

import com.minecraft.dto.request.OrderRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Order;
import com.minecraft.service.OrderService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary ="创建订单")
    @PostMapping("/create")
    public ApiResponse<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success("创建成功", orderService.createOrder(userId, orderRequest));
    }

    @Operation(summary ="获取订单列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getOrderList(PageRequest pageRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(orderService.getOrderList(userId, pageRequest));
    }

    @Operation(summary ="获取订单详情")
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderDetail(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderDetail(id));
    }

    @Operation(summary ="取消订单")
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ApiResponse.success("取消成功", null);
    }
}
