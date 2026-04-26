package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.ExchangeOrder;
import com.minecraft.entity.PointsProducts;
import com.minecraft.entity.User;
import com.minecraft.mapper.ExchangeOrderMapper;
import com.minecraft.service.ExchangeService;
import com.minecraft.service.PointsProductsService;
import com.minecraft.service.UserService;
import com.minecraft.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ExchangeServiceImpl extends ServiceImpl<ExchangeOrderMapper, ExchangeOrder> implements ExchangeService {
    
    @Autowired
    private PointsProductsService pointsProductsService;
    
    @Autowired
    private UserService userService;
    
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
            
            // 6. 创建兑换订单
            ExchangeOrder order = new ExchangeOrder();
            order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
            order.setUserId(userId);
            order.setProductId(productId);
            order.setProductName(product.getName());
            order.setPointsUsed(totalPoints);
            order.setQuantity(quantity);
            order.setStatus("待处理");
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
}