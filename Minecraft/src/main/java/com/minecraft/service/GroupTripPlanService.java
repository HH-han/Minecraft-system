package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.GroupTripPlanDTO;
import com.minecraft.entity.GroupTripPlan;

import java.util.List;
import java.util.Map;

public interface GroupTripPlanService extends IService<GroupTripPlan> {
    GroupTripPlan addTripItem(GroupTripPlanDTO dto, Long userId);
    void updateTripItem(Long tripId, GroupTripPlanDTO dto, Long userId);
    void deleteTripItem(Long tripId, Long userId);
    Map<Integer, List<GroupTripPlan>> getTripPlanByDay(Long groupId);
    List<GroupTripPlan> getTripPlanList(Long groupId);
}