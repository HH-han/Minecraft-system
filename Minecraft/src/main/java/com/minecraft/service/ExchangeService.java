package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.ExchangeOrder;
import com.minecraft.entity.PointsRecord;

public interface ExchangeService extends IService<ExchangeOrder> {
    // 兑换商品
    boolean exchangeProduct(Long userId, Long productId, Integer quantity, String address, String phone, String receiver);
}