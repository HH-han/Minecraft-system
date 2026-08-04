package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.*;
import com.minecraft.service.WebsiteIntroductionService;
import com.minecraft.vo.FeatureVO;
import com.minecraft.vo.MilestoneVO;
import com.minecraft.vo.TechCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "网站介绍页管理")
@RestController
@RequestMapping("/api/website-introduction")
public class WebsiteIntroductionController {

    @Autowired
    private WebsiteIntroductionService websiteIntroductionService;

    // ==================== 核心功能 (features) ====================

    @Operation(summary = "获取核心功能列表(含子项)")
    @GetMapping("/features")
    public ApiResponse<List<FeatureVO>> getFeatureList() {
        return ApiResponse.success(websiteIntroductionService.getFeatureList());
    }

    @Operation(summary = "获取核心功能详情")
    @GetMapping("/features/{id}")
    public ApiResponse<Feature> getFeatureDetail(@PathVariable Integer id) {
        return ApiResponse.success(websiteIntroductionService.getFeatureDetail(id));
    }

    @Operation(summary = "保存核心功能")
    @PostMapping("/features")
    public ApiResponse<Void> saveFeature(@RequestBody Feature feature) {
        websiteIntroductionService.saveFeature(feature);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除核心功能")
    @DeleteMapping("/features/{id}")
    public ApiResponse<Void> deleteFeature(@PathVariable Integer id) {
        websiteIntroductionService.deleteFeature(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 功能子项 (feature_items) ====================

    @Operation(summary = "根据功能ID获取子项列表")
    @GetMapping("/feature-items")
    public ApiResponse<List<FeatureItem>> getFeatureItems(@RequestParam Integer featureId) {
        return ApiResponse.success(websiteIntroductionService.getFeatureItemsByFeatureId(featureId));
    }

    @Operation(summary = "保存功能子项")
    @PostMapping("/feature-items")
    public ApiResponse<Void> saveFeatureItem(@RequestBody FeatureItem featureItem) {
        websiteIntroductionService.saveFeatureItem(featureItem);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除功能子项")
    @DeleteMapping("/feature-items/{id}")
    public ApiResponse<Void> deleteFeatureItem(@PathVariable Integer id) {
        websiteIntroductionService.deleteFeatureItem(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 技术分类 (tech_categories) ====================

    @Operation(summary = "获取技术分类列表(含技术项)")
    @GetMapping("/tech-categories")
    public ApiResponse<List<TechCategoryVO>> getTechCategoryList() {
        return ApiResponse.success(websiteIntroductionService.getTechCategoryList());
    }

    @Operation(summary = "保存技术分类")
    @PostMapping("/tech-categories")
    public ApiResponse<Void> saveTechCategory(@RequestBody TechCategory techCategory) {
        websiteIntroductionService.saveTechCategory(techCategory);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除技术分类")
    @DeleteMapping("/tech-categories/{id}")
    public ApiResponse<Void> deleteTechCategory(@PathVariable Integer id) {
        websiteIntroductionService.deleteTechCategory(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 技术项 (tech_items) ====================

    @Operation(summary = "根据分类ID获取技术项列表")
    @GetMapping("/tech-items")
    public ApiResponse<List<TechItem>> getTechItems(@RequestParam Integer categoryId) {
        return ApiResponse.success(websiteIntroductionService.getTechItemsByCategoryId(categoryId));
    }

    @Operation(summary = "保存技术项")
    @PostMapping("/tech-items")
    public ApiResponse<Void> saveTechItem(@RequestBody TechItem techItem) {
        websiteIntroductionService.saveTechItem(techItem);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除技术项")
    @DeleteMapping("/tech-items/{id}")
    public ApiResponse<Void> deleteTechItem(@PathVariable Integer id) {
        websiteIntroductionService.deleteTechItem(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 发展历程 (milestones) ====================

    @Operation(summary = "获取发展历程列表")
    @GetMapping("/milestones")
    public ApiResponse<List<Milestone>> getMilestoneList() {
        return ApiResponse.success(websiteIntroductionService.getMilestoneList());
    }

    @Operation(summary = "获取发展历程列表(带格式化日期)")
    @GetMapping("/milestones/formatted")
    public ApiResponse<List<MilestoneVO>> getMilestoneListFormatted() {
        return ApiResponse.success(websiteIntroductionService.getMilestoneListFormatted());
    }

    @Operation(summary = "保存发展历程")
    @PostMapping("/milestones")
    public ApiResponse<Void> saveMilestone(@RequestBody Milestone milestone) {
        websiteIntroductionService.saveMilestone(milestone);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除发展历程")
    @DeleteMapping("/milestones/{id}")
    public ApiResponse<Void> deleteMilestone(@PathVariable Integer id) {
        websiteIntroductionService.deleteMilestone(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 平台数据统计 (platform_stats) ====================

    @Operation(summary = "获取平台数据统计列表")
    @GetMapping("/platform-stats")
    public ApiResponse<List<PlatformStat>> getPlatformStatList() {
        return ApiResponse.success(websiteIntroductionService.getPlatformStatList());
    }

    @Operation(summary = "保存平台数据统计")
    @PostMapping("/platform-stats")
    public ApiResponse<Void> savePlatformStat(@RequestBody PlatformStat platformStat) {
        websiteIntroductionService.savePlatformStat(platformStat);
        return ApiResponse.success("保存成功", null);
    }

    @Operation(summary = "删除平台数据统计")
    @DeleteMapping("/platform-stats/{id}")
    public ApiResponse<Void> deletePlatformStat(@PathVariable Integer id) {
        websiteIntroductionService.deletePlatformStat(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 聚合数据 ====================

    @Operation(summary = "获取网站介绍页完整数据")
    @GetMapping("/data")
    public ApiResponse<Map<String, Object>> getIntroductionData() {
        return ApiResponse.success(websiteIntroductionService.getIntroductionData());
    }
}
