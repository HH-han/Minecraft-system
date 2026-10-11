package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.OrderRequest;
import com.minecraft.entity.ExchangeOrder;
import com.minecraft.entity.Order;
import com.minecraft.entity.PointsProducts;
import com.minecraft.entity.User;
import com.minecraft.mapper.ExchangeOrderMapper;
import com.minecraft.service.ExchangeService;
import com.minecraft.service.OrderService;
import com.minecraft.service.PointsProductsService;
import com.minecraft.service.UserService;
import com.minecraft.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ExchangeServiceImpl extends ServiceImpl<ExchangeOrderMapper, ExchangeOrder> implements ExchangeService {

    /** 积分兑换在 orders 中的商品类型标识 */
    private static final String ITEM_TYPE_POINTS = "points";

    /** 兑换单状态（与物流订单生命周期对应） */
    private static final String STATUS_PENDING_SHIP = "待发货";
    
    @Autowired
    private PointsProductsService pointsProductsService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    @Transactional
    public boolean exchangeProduct(Long userId, Long productId, Integer quantity, String address, String phone, String receiver) {
        // 生成分布式锁，防止并发操作
        String lockKey = "exchange:lock:" + userId + ":" + productId;
        try {
            // 尝试获取锁，设置5秒过期
            Boolean locked = redisUtil.setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
            if (locked == null || !locked) {
                // 锁获取失败，说明有其他操作正在进行
                return false;
            }
            
            // 1. 获取商品信息
            PointsProducts product = pointsProductsService.getById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            
            if (product.getStatus() != 1) {
                throw new RuntimeException("商品已下架");
            }
            
            if (product.getStock() < quantity) {
                throw new RuntimeException("商品库存不足");
            }
            
            // 2. 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 3. 计算所需积分
            int totalPoints = product.getPointsPrice() * quantity;
            if (user.getPoints() < totalPoints) {
                throw new RuntimeException("积分不足");
            }
            
            // 4. 扣减用户积分
            boolean deductPointsSuccess = userService.deductPoints(userId, totalPoints, "兑换商品：" + product.getName());
            if (!deductPointsSuccess) {
                throw new RuntimeException("扣减积分失败");
            }
            
            // 5. 扣减商品库存
            boolean deductStockSuccess = pointsProductsService.deductStock(productId, quantity);
            if (!deductStockSuccess) {
                throw new RuntimeException("扣减库存失败");
            }
            
            // 6. 生成物流订单：复用 orders 表统一入口，积分已扣减即视为已支付，进入物流发货流程
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setItemType(ITEM_TYPE_POINTS);
            orderRequest.setItemId(productId);
            orderRequest.setItemName(product.getName());
            orderRequest.setImage(product.getImageUrl());
            orderRequest.setAmount(BigDecimal.ZERO);
            orderRequest.setQuantity(quantity);
            orderRequest.setRemark("积分兑换：" + product.getName());
            orderRequest.setReceiverName(receiver);
            orderRequest.setReceiverPhone(phone);
            orderRequest.setReceiverAddress(address);
            Order logisticsOrder = orderService.createOrder(userId, orderRequest);
            orderService.payOrder(logisticsOrder.getId());

            // 7. 创建兑换订单并回填关联的物流订单ID
            ExchangeOrder order = new ExchangeOrder();
            order.setOrderNo(logisticsOrder.getOrderNo());
            order.setUserId(userId);
            order.setProductId(productId);
            order.setOrderId(logisticsOrder.getId());
            order.setProductName(product.getName());
            order.setPointsUsed(totalPoints);
            order.setQuantity(quantity);
            order.setStatus(STATUS_PENDING_SHIP);
            order.setAddress(address);
            order.setPhone(phone);
            order.setReceiver(receiver);
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            boolean saveOrderSuccess = save(order);
            if (!saveOrderSuccess) {
                throw new RuntimeException("创建订单失败");
            }
            
            return true;
        } catch (Exception e) {
            // 事务会自动回滚
            throw e;
        } finally {
            // 释放锁
            redisUtil.delete(lockKey);
        }
    }

    @Override
    public Page<ExchangeOrder> listUserOrders(Long userId, Integer page, Integer size) {
        Page<ExchangeOrder> orderPage = new Page<>(page, size);
        LambdaQueryWrapper<ExchangeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExchangeOrder::getUserId, userId).orderByDesc(ExchangeOrder::getCreateTime);
        Page<ExchangeOrder> result = page(orderPage, wrapper);

        // 批量加载关联的物流订单，回填物流状态
        List<Long> orderIds = result.getRecords().stream()
                .map(ExchangeOrder::getOrderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, Order> orderMap = orderIds.isEmpty()
                ? Collections.emptyMap()
                : orderService.listByIds(orderIds).stream()
                        .collect(Collectors.toMap(Order::getId, o -> o));

        for (ExchangeOrder exchangeOrder : result.getRecords()) {
            Order order = exchangeOrder.getOrderId() == null ? null : orderMap.get(exchangeOrder.getOrderId());
            if (order != null) {
                exchangeOrder.setShippingStatus(order.getShippingStatus());
                exchangeOrder.setStatusDesc(shippingStatusDesc(order.getShippingStatus()));
                exchangeOrder.setCarrierCode(order.getCarrierCode());
                exchangeOrder.setTrackingNo(order.getTrackingNo());
            } else {
                exchangeOrder.setStatusDesc(exchangeOrder.getStatus());
            }
        }
        return result;
    }

    /** 物流状态描述（对应 orders.shipping_status） */
    private String shippingStatusDesc(Integer status) {
        if (status == null) {
            return "待发货";
        }
        return switch (status) {
            case 0 -> "待发货";
            case 1 -> "部分发货";
            case 2 -> "已发货";
            case 3 -> "已签收";
            default -> "未知";
        };
    }
}