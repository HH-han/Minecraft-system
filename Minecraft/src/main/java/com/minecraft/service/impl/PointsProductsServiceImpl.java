package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.PointsProducts;
import com.minecraft.mapper.PointsProductsMapper;
import com.minecraft.service.PointsProductsService;
import org.springframework.stereotype.Service;

@Service
public class PointsProductsServiceImpl extends ServiceImpl<PointsProductsMapper, PointsProducts> implements PointsProductsService {
    @Override
    public boolean deductStock(Long id, Integer quantity) {
        int result = baseMapper.deductStock(id, quantity);
        return result > 0;
    }
}