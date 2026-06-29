package com.minecraft.controller;

import com.minecraft.dto.request.AITripGenerateDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.AITripRecommendation;
import com.minecraft.service.AITripRecommendationService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI行程推荐")
@RestController
@RequestMapping("/api/ai/trip")
public class AITripRecommendationController {

    @Autowired
    private AITripRecommendationService aiTripRecommendationService;

    @Operation(summary = "生成AI行程方案")
    @PostMapping("/generate")
    public ApiResponse<AITripRecommendation> generateTrip(@RequestBody AITripGenerateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        AITripRecommendation recommendation = aiTripRecommendationService.generateTrip(dto, userId);
        return ApiResponse.success("生成成功", recommendation);
    }

    @Operation(summary = "采用方案到群组行程")
    @PostMapping("/adopt/{id}")
    public ApiResponse<Void> adoptTrip(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        aiTripRecommendationService.adoptTrip(id, userId);
        return ApiResponse.success("已采用", null);
    }

    @Operation(summary = "获取历史推荐记录")
    @GetMapping("/history")
    public ApiResponse<List<AITripRecommendation>> getHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<AITripRecommendation> history = aiTripRecommendationService.getHistory(userId);
        return ApiResponse.success(history);
    }
}