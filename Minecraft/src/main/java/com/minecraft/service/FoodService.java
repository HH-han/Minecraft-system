package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Food;

import java.util.List;

public interface FoodService extends IService<Food> {
    PageResponse<?> getFoodList(PageRequest request);
    Food getFoodDetail(Long id);
    List<Food> getRecommendFoods(String city, Integer limit);
    void addFood(Food food);
    void updateFood(Food food);
    void deleteFood(Long id);
}
