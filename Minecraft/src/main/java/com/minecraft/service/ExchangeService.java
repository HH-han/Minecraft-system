package com.minecraft.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.ExchangeOrder;
import com.minecraft.entity.PointsRecord;

public interface ExchangeService extends IService<ExchangeOrder> {
    // 兑换商品
    boolean exchangeProduct(Long userId, Long productId, Integer quantity, String address, String phone, String receiver);

    // 我的兑换订单（附带关联物流订单的物流状态）
    Page<ExchangeOrder> listUserOrders(Long userId, Integer page, Integer size);
}