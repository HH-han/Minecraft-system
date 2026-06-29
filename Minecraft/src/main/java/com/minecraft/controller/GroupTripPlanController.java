package com.minecraft.controller;

import com.minecraft.dto.request.GroupTripPlanDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupTripPlan;
import com.minecraft.service.GroupTripPlanService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "行程计划")
@RestController
@RequestMapping("/api/group/{groupId}/trip")
public class GroupTripPlanController {

    @Autowired
    private GroupTripPlanService groupTripPlanService;

    @Operation(summary = "添加行程项")
    @PostMapping
    public ApiResponse<GroupTripPlan> addTripItem(@PathVariable Long groupId, @RequestBody GroupTripPlanDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        dto.setGroupId(groupId);
        GroupTripPlan plan = groupTripPlanService.addTripItem(dto, userId);
        return ApiResponse.success("添加成功", plan);
    }

    @Operation(summary = "更新行程项")
    @PutMapping("/{tripId}")
    public ApiResponse<Void> updateTripItem(@PathVariable Long tripId, @RequestBody GroupTripPlanDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupTripPlanService.updateTripItem(tripId, dto, userId);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除行程项")
    @DeleteMapping("/{tripId}")
    public ApiResponse<Void> deleteTripItem(@PathVariable Long tripId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupTripPlanService.deleteTripItem(tripId, userId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取完整行程（按天分组）")
    @GetMapping("/list")
    public ApiResponse<Map<Integer, List<GroupTripPlan>>> getTripPlanByDay(@PathVariable Long groupId) {
        Map<Integer, List<GroupTripPlan>> plan = groupTripPlanService.getTripPlanByDay(groupId);
        return ApiResponse.success(plan);
    }

    @Operation(summary = "获取行程列表")
    @GetMapping("/all")
    public ApiResponse<List<GroupTripPlan>> getTripPlanList(@PathVariable Long groupId) {
        List<GroupTripPlan> plans = groupTripPlanService.getTripPlanList(groupId);
        return ApiResponse.success(plans);
    }
}