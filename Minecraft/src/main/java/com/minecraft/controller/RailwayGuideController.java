package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.RailwayGuide;
import com.minecraft.service.RailwayGuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "铁路出行指南")
@RestController
@RequestMapping("/api/railway/guides")
public class RailwayGuideController {

    private final RailwayGuideService guideService;

    public RailwayGuideController(RailwayGuideService guideService) {
        this.guideService = guideService;
    }

    @Operation(summary = "指南列表（分类 / 关键词）")
    @GetMapping
    public ApiResponse<List<RailwayGuide>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(guideService.list(category, keyword));
    }

    @Operation(summary = "指南详情（含阅读量累加）")
    @GetMapping("/{id}")
    public ApiResponse<RailwayGuide> detail(@PathVariable Long id) {
        return ApiResponse.success(guideService.detail(id));
    }

    @Operation(summary = "相关指南")
    @GetMapping("/{id}/related")
    public ApiResponse<List<RailwayGuide>> related(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") Integer limit) {
        return ApiResponse.success(guideService.related(id, limit));
    }
}
