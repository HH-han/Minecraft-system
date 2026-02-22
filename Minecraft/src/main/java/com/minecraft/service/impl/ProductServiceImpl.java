package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Product;
import com.minecraft.mapper.ProductMapper;
import com.minecraft.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public PageResponse<?> getProductList(PageRequest request) {
        Page<Product> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Product::getName, request.getKeyword())
                    .or()
                    .like(Product::getCity, request.getKeyword());
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public Product getProductDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<Product> getRecommendProducts(String city, Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (city != null) {
            wrapper.eq(Product::getCity, city);
        }

        wrapper.orderByDesc(Product::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        return list(wrapper);
    }

    @Override
    public void addProduct(Product product) {
        save(product);
    }

    @Override
    public void updateProduct(Product product) {
        updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        removeById(id);
    }
}
