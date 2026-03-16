package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Cart;
import com.minecraft.mapper.CartMapper;
import com.minecraft.service.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Override
    public void addToCart(Cart cart) {
        if (cart.getUserId() == null) {
            throw new RuntimeException("用户未登录");
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, cart.getUserId())
                .eq(Cart::getItemType, cart.getItemType())
                .eq(Cart::getItemId, cart.getItemId());
        
        Cart existingCart = getOne(wrapper);
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
            updateById(existingCart);
        } else {
            save(cart);
        }
    }

    @Override
    public void updateCart(Long id, Integer quantity) {
        Cart cart = getById(id);
        if (cart == null) {
            throw new RuntimeException("购物车商品不存在");
        }
        cart.setQuantity(quantity);
        updateById(cart);
    }

    @Override
    public void deleteFromCart(Long id) {
        removeById(id);
    }

    @Override
    public void clearCart(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        remove(wrapper);
    }

    @Override
    public List<Cart> getCartList(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return list(wrapper);
    }
}
