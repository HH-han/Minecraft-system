package com.minecraft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.common.Result;
import com.minecraft.entity.PointsProducts;
import com.minecraft.service.PointsProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/points/products")
public class PointsProductsController {
    
    @Autowired
    private PointsProductsService pointsProductsService;
    
    // 上传商品图片
    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = pointsProductsService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
    
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
    
    // 新增商品
    @PostMapping
    public Result addProduct(@RequestBody PointsProducts product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return Result.error("商品名称不能为空");
        }
        if (product.getPointsPrice() == null || product.getPointsPrice() < 0) {
            return Result.error("兑换积分不能为空且不能为负数");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            return Result.error("库存不能为空且不能为负数");
        }
        if (product.getStatus() == null) {
            product.setStatus(0); // 默认下架状态
        }
        boolean saved = pointsProductsService.save(product);
        if (!saved) {
            return Result.error("新增商品失败");
        }
        return Result.success(product);
    }
    
    // 修改商品
    @PutMapping("/{id}")
    public Result updateProduct(@PathVariable Long id, @RequestBody PointsProducts product) {
        PointsProducts existing = pointsProductsService.getById(id);
        if (existing == null) {
            return Result.error("商品不存在");
        }
        product.setId(id);
        boolean updated = pointsProductsService.updateById(product);
        if (!updated) {
            return Result.error("修改商品失败");
        }
        return Result.success();
    }
    
    // 删除商品
    @DeleteMapping("/{id}")
    public Result deleteProduct(@PathVariable Long id) {
        PointsProducts existing = pointsProductsService.getById(id);
        if (existing == null) {
            return Result.error("商品不存在");
        }
        boolean removed = pointsProductsService.removeById(id);
        if (!removed) {
            return Result.error("删除商品失败");
        }
        return Result.success();
    }
}
