package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {
    void addToCart(Cart cart);
    void updateCart(Long id, Integer quantity);
    void deleteFromCart(Long id);
    void clearCart(Long userId);
    List<Cart> getCartList(Long userId);
}
