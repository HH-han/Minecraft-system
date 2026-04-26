package com.minecraft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.common.Result;
import com.minecraft.entity.ExchangeOrder;
import com.minecraft.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {
    
    @Autowired
    private ExchangeService exchangeService;
    
    // 兑换商品
    @PostMapping
    public Result exchangeProduct(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam String receiver) {
        // 从上下文获取用户ID
        Long userId = 1L; // 这里应该从认证上下文获取，暂时硬编码
        
        boolean success = exchangeService.exchangeProduct(userId, productId, quantity, address, phone, receiver);
        if (success) {
            return Result.success("兑换成功");
        } else {
            return Result.error("兑换失败");
        }
    }
    
    // 获取用户的兑换订单列表
    @GetMapping("/orders")
    public Result getExchangeOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // 从上下文获取用户ID
        Long userId = 1L; // 这里应该从认证上下文获取，暂时硬编码
        
        Page<ExchangeOrder> orderPage = new Page<>(page, size);
        QueryWrapper<ExchangeOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        Page<ExchangeOrder> result = exchangeService.page(orderPage, wrapper);
        return Result.success(result);
    }
    
    // 获取订单详情
    @GetMapping("/orders/{id}")
    public Result getOrderById(@PathVariable Long id) {
        ExchangeOrder order = exchangeService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
}
