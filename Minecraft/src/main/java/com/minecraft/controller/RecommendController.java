package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Recommend;
import com.minecraft.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "推荐管理")
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @Operation(summary = "获取推荐列表")
    @GetMapping("/list")
    public ApiResponse<List<Recommend>> getRecommendList() {
        return ApiResponse.success(recommendService.getRecommendList());
    }

    @Operation(summary = "获取所有推荐列表（管理用）")
    @GetMapping("/list/all")
    public ApiResponse<List<Recommend>> getAllRecommendList() {
        return ApiResponse.success(recommendService.getRecommendListByState(null));
    }

    @Operation(summary = "根据状态获取推荐列表")
    @GetMapping("/list/state")
    public ApiResponse<List<Recommend>> getRecommendListByState(@RequestParam String state) {
        return ApiResponse.success(recommendService.getRecommendListByState(state));
    }

    @Operation(summary = "获取推荐详情")
    @GetMapping("/{id}")
    public ApiResponse<Recommend> getRecommendById(@PathVariable Integer id) {
        return ApiResponse.success(recommendService.getRecommendById(id));
    }

    @Operation(summary = "新增推荐")
    @PostMapping("/add")
    public ApiResponse<Void> addRecommend(@RequestBody Recommend recommend) {
        recommendService.addRecommend(recommend);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "修改推荐")
    @PutMapping("/update")
    public ApiResponse<Void> updateRecommend(@RequestBody Recommend recommend) {
        recommendService.updateRecommend(recommend);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除推荐")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecommend(@PathVariable Integer id) {
        recommendService.deleteRecommend(id);
        return ApiResponse.success("删除成功", null);
    }
}