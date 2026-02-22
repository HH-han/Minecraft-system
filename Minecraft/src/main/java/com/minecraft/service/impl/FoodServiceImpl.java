package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Food;
import com.minecraft.mapper.FoodMapper;
import com.minecraft.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

    @Override
    public PageResponse<?> getFoodList(PageRequest request) {
        Page<Food> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Food::getName, request.getKeyword())
                    .or()
                    .like(Food::getCity, request.getKeyword());
        }

        wrapper.orderByDesc(Food::getCreateTime);

        Page<Food> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public Food getFoodDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<Food> getRecommendFoods(String city, Integer limit) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1);

        if (city != null) {
            wrapper.eq(Food::getCity, city);
        }

        wrapper.orderByDesc(Food::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        return list(wrapper);
    }

    @Override
    public void addFood(Food food) {
        save(food);
    }

    @Override
    public void updateFood(Food food) {
        updateById(food);
    }

    @Override
    public void deleteFood(Long id) {
        removeById(id);
    }
}
