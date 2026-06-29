package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.AITripGenerateDTO;
import com.minecraft.entity.AITripRecommendation;

import java.util.List;

public interface AITripRecommendationService extends IService<AITripRecommendation> {
    AITripRecommendation generateTrip(AITripGenerateDTO dto, Long userId);
    void adoptTrip(Long recommendationId, Long userId);
    List<AITripRecommendation> getHistory(Long userId);
}