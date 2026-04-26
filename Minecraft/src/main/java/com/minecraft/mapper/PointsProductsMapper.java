package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.PointsProducts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointsProductsMapper extends BaseMapper<PointsProducts> {
    // 扣减库存
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}