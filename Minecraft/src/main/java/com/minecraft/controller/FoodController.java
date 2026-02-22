package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Food;
import com.minecraft.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "美食管理")
@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Operation(summary ="获取美食列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getFoodList(PageRequest request) {
        return ApiResponse.success(foodService.getFoodList(request));
    }

    @Operation(summary ="获取美食详情")
    @GetMapping("/{id}")
    public ApiResponse<Food> getFoodDetail(@PathVariable Long id) {
        return ApiResponse.success(foodService.getFoodDetail(id));
    }

    @Operation(summary ="获取推荐美食")
    @GetMapping("/recommend")
    public ApiResponse<List<Food>> getRecommendFoods(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(foodService.getRecommendFoods(city, limit));
    }

    @Operation(summary ="添加美食")
    @PostMapping("/add")
    public ApiResponse<Void> addFood(@RequestBody Food food) {
        foodService.addFood(food);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary ="更新美食")
    @PutMapping("/update")
    public ApiResponse<Void> updateFood(@RequestBody Food food) {
        foodService.updateFood(food);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除美食")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ApiResponse.success("删除成功", null);
    }
}
