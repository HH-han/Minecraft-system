package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.PointsProducts;

public interface PointsProductsService extends IService<PointsProducts> {
    // 扣减库存
    boolean deductStock(Long id, Integer quantity);
}