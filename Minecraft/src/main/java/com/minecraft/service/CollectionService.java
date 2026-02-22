package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.CollectionRequest;
import com.minecraft.entity.UserCollection;

public interface CollectionService extends IService<UserCollection> {
    void collect(CollectionRequest request, Long userId);
    void uncollect(CollectionRequest request, Long userId);
    Boolean isCollected(String itemType, Long itemId, Long userId);
}
