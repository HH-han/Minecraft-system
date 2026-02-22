package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelPlan;
import com.minecraft.service.TravelService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "出行计划管理")
@RestController
@RequestMapping("/api/travel")
public class TravelController {

    @Autowired
    private TravelService travelService;

    @Operation(summary ="获取出行计划列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getTravelPlanList(PageRequest pageRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(travelService.getTravelPlanList(userId, pageRequest));
    }

    @Operation(summary ="获取出行计划详情")
    @GetMapping("/{id}")
    public ApiResponse<TravelPlan> getTravelPlanDetail(@PathVariable Long id) {
        return ApiResponse.success(travelService.getTravelPlanDetail(id));
    }

    @Operation(summary ="创建出行计划")
    @PostMapping("/create")
    public ApiResponse<Void> createTravelPlan(@RequestBody TravelPlan travelPlan) {
        travelService.createTravelPlan(travelPlan);
        return ApiResponse.success("创建成功", null);
    }

    @Operation(summary ="更新出行计划")
    @PutMapping("/update")
    public ApiResponse<Void> updateTravelPlan(@RequestBody TravelPlan travelPlan) {
        travelService.updateTravelPlan(travelPlan);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除出行计划")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTravelPlan(@PathVariable Long id) {
        travelService.deleteTravelPlan(id);
        return ApiResponse.success("删除成功", null);
    }
}
