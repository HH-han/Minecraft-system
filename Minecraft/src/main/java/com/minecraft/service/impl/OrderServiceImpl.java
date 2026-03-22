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

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    public Order createOrder(Long userId, OrderRequest request) {
        Order order = new Order();
        order.setUserId(userId);
        order.setItemType(request.getItemType());
        order.setItemId(request.getItemId());
        order.setItemName(request.getItemName());
        order.setImage(request.getImage());
        order.setAmount(request.getAmount());
        order.setQuantity(request.getQuantity());
        order.setRemark(request.getRemark());
        order.setStatus(OrderStatus.PENDING.getCode().toString());
        
        // 生成订单编号：日期 + 4位自增数
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        
        // 设置创建时间
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        save(order);
        return order;
    }
    
    // 生成唯一订单编号
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 从数据库中获取当天的最大订单编号
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Order::getOrderNo, dateStr)
               .orderByDesc(Order::getOrderNo)
               .last("LIMIT 1");
        
        Order lastOrder = getOne(wrapper);
        int counter = 1;
        
        if (lastOrder != null) {
            String lastOrderNo = lastOrder.getOrderNo();
            if (lastOrderNo.startsWith(dateStr)) {
                // 提取计数器部分并加 1
                String counterStr = lastOrderNo.substring(dateStr.length());
                try {
                    counter = Integer.parseInt(counterStr) + 1;
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用默认值 1
                    counter = 1;
                }
            }
        }
        
        // 格式化为 4 位数字
        String counterStr = String.format("%04d", counter);
        return dateStr + counterStr;
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

    @Override
    public void payOrder(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(OrderStatus.PAID.getCode().toString());
        order.setUpdateTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        removeById(id);
    }
}
