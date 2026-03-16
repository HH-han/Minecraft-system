package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.OrderRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Order;
import com.minecraft.enums.OrderStatus;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    // 用于生成订单编号的计数器
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    @Override
    public Order createOrder(Long userId, OrderRequest request) {
        Order order = new Order();
        order.setUserId(userId);
        order.setItemType(request.getItemType());
        order.setItemId(request.getItemId());
        order.setQuantity(request.getQuantity());
        order.setRemark(request.getRemark());
        order.setStatus(OrderStatus.PENDING.getCode().toString());
        
        // 生成订单编号：日期 + 4位自增数
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String counterStr = String.format("%04d", orderCounter.getAndIncrement());
        if (orderCounter.get() > 9999) {
            orderCounter.set(1);
        }
        String orderNo = dateStr + counterStr;
        order.setOrderNo(orderNo);
        
        // 设置创建时间
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        save(order);
        return order;
    }

    @Override
    public PageResponse<?> getOrderList(Long userId, PageRequest request) {
        Page<Order> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);

        Page<Order> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public Order getOrderDetail(Long id) {
        return getById(id);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(OrderStatus.CANCELLED.getCode().toString());
        order.setUpdateTime(LocalDateTime.now());
        updateById(order);
    }
}
