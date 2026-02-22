package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Product;
import com.minecraft.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "特产/纪念品管")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary ="获取商品列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getProductList(PageRequest request) {
        return ApiResponse.success(productService.getProductList(request));
    }

    @Operation(summary ="获取商品详情")
    @GetMapping("/{id}")
    public ApiResponse<Product> getProductDetail(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductDetail(id));
    }

    @Operation(summary ="获取推荐商品")
    @GetMapping("/recommend")
    public ApiResponse<List<Product>> getRecommendProducts(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(productService.getRecommendProducts(city, limit));
    }

    @Operation(summary ="添加商品")
    @PostMapping("/add")
    public ApiResponse<Void> addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary ="更新商品")
    @PutMapping("/update")
    public ApiResponse<Void> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除商品")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("删除成功", null);
    }
}
