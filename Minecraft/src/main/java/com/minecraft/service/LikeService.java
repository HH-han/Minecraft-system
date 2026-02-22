package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.LikeRequest;
import com.minecraft.entity.Like;

public interface LikeService extends IService<Like> {
    void like(LikeRequest request, Long userId);
    void unlike(LikeRequest request, Long userId);
    Boolean isLiked(String itemType, Long itemId, Long userId);
}
