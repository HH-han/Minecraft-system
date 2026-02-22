package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.LikeRequest;
import com.minecraft.entity.Like;
import com.minecraft.mapper.LikeMapper;
import com.minecraft.service.LikeService;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Override
    public void like(LikeRequest request, Long userId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getItemType, request.getItemType())
                .eq(Like::getItemId, request.getItemId());
        
        if (getOne(wrapper) != null) {
            throw new RuntimeException("已经点赞过了");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setItemType(request.getItemType());
        like.setItemId(request.getItemId());
        save(like);
    }

    @Override
    public void unlike(LikeRequest request, Long userId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getItemType, request.getItemType())
                .eq(Like::getItemId, request.getItemId());
        remove(wrapper);
    }

    @Override
    public Boolean isLiked(String itemType, Long itemId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getItemType, itemType)
                .eq(Like::getItemId, itemId);
        return getOne(wrapper) != null;
    }
}
