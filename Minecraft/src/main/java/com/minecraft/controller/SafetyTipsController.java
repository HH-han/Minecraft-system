package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.SafetyTips;
import com.minecraft.service.SafetyTipsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/safety-tips")
@Tag(name = "安全知识提示", description = "安全知识提示相关接口")
public class SafetyTipsController {
    @Resource
    private SafetyTipsService safetyTipsService;

    @Operation(summary = "获取所有激活的安全知识提示")
    @GetMapping
    public ApiResponse<List<SafetyTips>> getActiveSafetyTips() {
        List<SafetyTips> safetyTips = safetyTipsService.getActiveSafetyTips();
        return ApiResponse.success(safetyTips);
    }

    @Operation(summary = "根据分类获取安全知识提示")
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<SafetyTips>> getSafetyTipsByCategory(@PathVariable Long categoryId) {
        List<SafetyTips> safetyTips = safetyTipsService.getSafetyTipsByCategory(categoryId);
        return ApiResponse.success(safetyTips);
    }

    @Operation(summary = "获取安全知识提示详情")
    @GetMapping("/{id}")
    public ApiResponse<SafetyTips> getSafetyTipById(@PathVariable Long id) {
        SafetyTips safetyTip = safetyTipsService.getById(id);
        return ApiResponse.success(safetyTip);
    }

    @Operation(summary = "新增安全知识提示")
    @PostMapping
    public ApiResponse<?> addSafetyTip(@RequestBody SafetyTips safetyTip) {
        safetyTipsService.save(safetyTip);
        return ApiResponse.success("新增安全知识提示成功");
    }

    @Operation(summary = "更新安全知识提示")
    @PutMapping
    public ApiResponse<?> updateSafetyTip(@RequestBody SafetyTips safetyTip) {
        safetyTipsService.updateById(safetyTip);
        return ApiResponse.success("更新安全知识提示成功");
    }

    @Operation(summary = "删除安全知识提示")
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteSafetyTip(@PathVariable Long id) {
        safetyTipsService.removeById(id);
        return ApiResponse.success("删除安全知识提示成功");
    }
}
