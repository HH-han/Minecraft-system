package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.HomeRecommendations;
import com.minecraft.service.HomeRecommendationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "首页推荐管理")
@RestController
@RequestMapping("/api/home-recommendations")
public class HomeRecommendationsController {

    @Autowired
    private HomeRecommendationsService homeRecommendationsService;

    @Operation(summary = "获取首页推荐列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<HomeRecommendations>> getHomeRecommendationsList(PageRequest request) {
        return ApiResponse.success(homeRecommendationsService.getHomeRecommendationsList(request));
    }

    @Operation(summary = "获取首页推荐详情")
    @GetMapping("/{id}")
    public ApiResponse<HomeRecommendations> getHomeRecommendationById(@PathVariable Long id) {
        return ApiResponse.success(homeRecommendationsService.getHomeRecommendationById(id));
    }

    @Operation(summary = "获取热门推荐")
    @GetMapping("/hot")
    public ApiResponse<List<HomeRecommendations>> getHotRecommendations(@RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(homeRecommendationsService.getHotRecommendations(limit));
    }

    @Operation(summary = "获取新晋推荐")
    @GetMapping("/new")
    public ApiResponse<List<HomeRecommendations>> getNewRecommendations(@RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(homeRecommendationsService.getNewRecommendations(limit));
    }

    @Operation(summary = "按分类获取推荐")
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<HomeRecommendations>> getRecommendationsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(homeRecommendationsService.getRecommendationsByCategory(categoryId, limit));
    }

    @Operation(summary = "按城市获取推荐")
    @GetMapping("/city/{city}")
    public ApiResponse<List<HomeRecommendations>> getRecommendationsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(homeRecommendationsService.getRecommendationsByCity(city, limit));
    }

    @Operation(summary = "新增首页推荐")
    @PostMapping("/add")
    public ApiResponse<Void> addHomeRecommendation(@RequestBody HomeRecommendations homeRecommendations) {
        homeRecommendationsService.addHomeRecommendation(homeRecommendations);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "修改首页推荐")
    @PutMapping("/update")
    public ApiResponse<Void> updateHomeRecommendation(@RequestBody HomeRecommendations homeRecommendations) {
        homeRecommendationsService.updateHomeRecommendation(homeRecommendations);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除首页推荐")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteHomeRecommendation(@PathVariable Long id) {
        homeRecommendationsService.deleteHomeRecommendation(id);
        return ApiResponse.success("删除成功", null);
    }
}