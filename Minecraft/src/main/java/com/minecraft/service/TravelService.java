package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelPlan;

public interface TravelService extends IService<TravelPlan> {
    PageResponse<?> getTravelPlanList(Long userId, PageRequest request);
    TravelPlan getTravelPlanDetail(Long id);
    void createTravelPlan(TravelPlan travelPlan);
    void updateTravelPlan(TravelPlan travelPlan);
    void deleteTravelPlan(Long id);
}
