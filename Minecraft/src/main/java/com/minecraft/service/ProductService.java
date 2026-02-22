package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {
    PageResponse<?> getProductList(PageRequest request);
    Product getProductDetail(Long id);
    List<Product> getRecommendProducts(String city, Integer limit);
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Long id);
}
