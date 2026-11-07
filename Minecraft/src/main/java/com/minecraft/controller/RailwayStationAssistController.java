package com.minecraft.controller;

import com.minecraft.dto.request.RailwayStationServiceApplyRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.RailwayServiceRequest;
import com.minecraft.entity.RailwayStationServiceItem;
import com.minecraft.exception.BusinessException;
import com.minecraft.service.RailwayStationAssistService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "站车服务")
@RestController
@RequestMapping("/api/railway/station-services")
public class RailwayStationAssistController {

    private final RailwayStationAssistService stationAssistService;

    public RailwayStationAssistController(RailwayStationAssistService stationAssistService) {
        this.stationAssistService = stationAssistService;
    }

    @Operation(summary = "站车服务项目列表")
    @GetMapping
    public ApiResponse<List<RailwayStationServiceItem>> services() {
        return ApiResponse.success(stationAssistService.listServices());
    }

    @Operation(summary = "提交站车服务预约")
    @PostMapping("/requests")
    public ApiResponse<RailwayServiceRequest> apply(
            @Valid @RequestBody RailwayStationServiceApplyRequest request) {
        return ApiResponse.success("预约已提交", stationAssistService.apply(requireLogin(), request));
    }

    @Operation(summary = "我的服务预约")
    @GetMapping("/requests")
    public ApiResponse<PageResponse<RailwayServiceRequest>> requests(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(stationAssistService.listRequests(requireLogin(), pageNum, pageSize));
    }

    @Operation(summary = "取消服务预约")
    @PostMapping("/requests/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        stationAssistService.cancel(requireLogin(), id);
        return ApiResponse.success("预约已取消", null);
    }

    private Long requireLogin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
