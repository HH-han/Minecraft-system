package com.minecraft.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.MonthlyRecommendation;
import com.minecraft.service.MonthlyRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "月份旅行推荐")
@RestController
@RequestMapping("/api/monthly-recommendations")
public class MonthlyRecommendationController {

    @Autowired
    private MonthlyRecommendationService monthlyRecommendationService;

    @Operation(summary = "获取所有月份")
    @GetMapping("/months")
    public ApiResponse<List<String>> getAllMonths() {
        return ApiResponse.success(monthlyRecommendationService.getAllMonths());
    }

    @Operation(summary = "根据月份获取推荐内容")
    @GetMapping("/by-month/{monthName}")
    public ApiResponse<List<MonthlyRecommendation>> getByMonthName(@PathVariable String monthName) {
        List<MonthlyRecommendation> recommendations = monthlyRecommendationService.getRecommendationsByMonthName(monthName);
        return ApiResponse.success(recommendations);
    }

    @Operation(summary = "获取推荐内容列表")
    @GetMapping("/list")
    public ApiResponse<List<MonthlyRecommendation>> getList() {
        return ApiResponse.success(monthlyRecommendationService.list());
    }

    @Operation(summary = "分页获取推荐内容列表")
    @GetMapping("/page")
    public ApiResponse<Page<MonthlyRecommendation>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<MonthlyRecommendation> page = new Page<>(current, size);
        return ApiResponse.success(monthlyRecommendationService.page(page));
    }

    @Operation(summary = "根据ID获取推荐内容详情")
    @GetMapping("/detail/{id}")
    public ApiResponse<MonthlyRecommendation> getDetail(@PathVariable Integer id) {
        return ApiResponse.success(monthlyRecommendationService.getById(id));
    }

    @Operation(summary = "添加推荐内容")
    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestBody MonthlyRecommendation recommendation) {
        monthlyRecommendationService.save(recommendation);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "更新推荐内容")
    @PutMapping("/update")
    public ApiResponse<Void> update(@RequestBody MonthlyRecommendation recommendation) {
        monthlyRecommendationService.updateById(recommendation);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除推荐内容")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        monthlyRecommendationService.removeById(id);
        return ApiResponse.success("删除成功", null);
    }
}
