package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.OrderRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Order;

public interface OrderService extends IService<Order> {
    Order createOrder(Long userId, OrderRequest request);
    PageResponse<?> getOrderList(Long userId, PageRequest request);
    Order getOrderDetail(Long id);
    void cancelOrder(Long id);
    void payOrder(Long id);
}
