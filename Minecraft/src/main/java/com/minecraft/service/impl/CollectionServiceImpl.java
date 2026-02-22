package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.CollectionRequest;
import com.minecraft.entity.UserCollection;
import com.minecraft.mapper.CollectionMapper;
import com.minecraft.service.CollectionService;
import org.springframework.stereotype.Service;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, UserCollection> implements CollectionService {

    @Override
    public void collect(CollectionRequest request, Long userId) {
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getItemType, request.getItemType())
                .eq(UserCollection::getItemId, request.getItemId());
        
        if (getOne(wrapper) != null) {
            throw new RuntimeException("已经收藏过了");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setItemType(request.getItemType());
        collection.setItemId(request.getItemId());
        save(collection);
    }

    @Override
    public void uncollect(CollectionRequest request, Long userId) {
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getItemType, request.getItemType())
                .eq(UserCollection::getItemId, request.getItemId());
        remove(wrapper);
    }

    @Override
    public Boolean isCollected(String itemType, Long itemId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getItemType, itemType)
                .eq(UserCollection::getItemId, itemId);
        return getOne(wrapper) != null;
    }
}
