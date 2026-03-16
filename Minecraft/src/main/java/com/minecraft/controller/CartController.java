package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Cart;
import com.minecraft.service.CartService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车管")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary ="添加到购物车")
    @PostMapping("/add")
    public ApiResponse<Void> addToCart(@RequestBody Cart cart) {
        // 优先使用前端传递的 userId，否则从 SecurityUtils 获取
        Long userId = cart.getUserId();
        if (userId == null) {
            userId = SecurityUtils.getCurrentUserId();
        }
        if (userId == null) {
            return ApiResponse.error(401, "用户未登录");
        }
        cart.setUserId(userId);
        cartService.addToCart(cart);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary ="更新购物")
    @PutMapping("/update")
    public ApiResponse<Void> updateCart(@RequestParam Long id, @RequestParam Integer quantity) {
        cartService.updateCart(id, quantity);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除购物车商")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFromCart(@PathVariable Long id) {
        cartService.deleteFromCart(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary ="清空购物")
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart(@RequestParam(required = false) Long userId) {
        // 优先使用前端传递的 userId，否则从 SecurityUtils 获取
        if (userId == null) {
            userId = SecurityUtils.getCurrentUserId();
        }
        if (userId == null) {
            return ApiResponse.error(401, "用户未登录");
        }
        cartService.clearCart(userId);
        return ApiResponse.success("清空成功", null);
    }

    @Operation(summary ="获取购物车列")
    @GetMapping("/list")
    public ApiResponse<List<Cart>> getCartList(@RequestParam(required = false) Long userId) {
        // 优先使用前端传递的 userId，否则从 SecurityUtils 获取
        if (userId == null) {
            userId = SecurityUtils.getCurrentUserId();
        }
        if (userId == null) {
            return ApiResponse.success("用户未登录", null);
        }
        return ApiResponse.success(cartService.getCartList(userId));
    }
}
