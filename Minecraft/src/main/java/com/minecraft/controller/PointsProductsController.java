package com.minecraft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.common.Result;
import com.minecraft.entity.PointsProducts;
import com.minecraft.service.PointsProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points/products")
public class PointsProductsController {
    
    @Autowired
    private PointsProductsService pointsProductsService;
    
    // 获取商品列表
    @GetMapping
    public Result getProducts() {
        QueryWrapper<PointsProducts> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 只查询上架的商品
        List<PointsProducts> products = pointsProductsService.list(wrapper);
        return Result.success(products);
    }
    
    // 获取商品详情
    @GetMapping("/{id}")
    public Result getProductById(@PathVariable Long id) {
        PointsProducts product = pointsProductsService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }
    
    // 分页获取商品列表
    @GetMapping("/page")
    public Result getProductsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PointsProducts> productPage = new Page<>(page, size);
        QueryWrapper<PointsProducts> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 只查询上架的商品
        Page<PointsProducts> result = pointsProductsService.page(productPage, wrapper);
        return Result.success(result);
    }
}
