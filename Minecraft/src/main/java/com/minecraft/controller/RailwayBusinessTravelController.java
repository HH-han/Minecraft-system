package com.minecraft.controller;

import com.minecraft.dto.request.RailwayBusinessAccountRequest;
import com.minecraft.dto.request.RailwayBusinessTripRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayBusinessSummaryVO;
import com.minecraft.entity.RailwayBusinessAccount;
import com.minecraft.entity.RailwayBusinessTrip;
import com.minecraft.exception.BusinessException;
import com.minecraft.service.RailwayBusinessTravelService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "铁路商旅服务")
@RestController
@RequestMapping("/api/railway/business")
public class RailwayBusinessTravelController {

    private final RailwayBusinessTravelService businessTravelService;

    public RailwayBusinessTravelController(RailwayBusinessTravelService businessTravelService) {
        this.businessTravelService = businessTravelService;
    }

    @Operation(summary = "我的商旅企业账户")
    @GetMapping("/account")
    public ApiResponse<RailwayBusinessAccount> account() {
        return ApiResponse.success(businessTravelService.getAccount(requireLogin()));
    }

    @Operation(summary = "开通 / 修改商旅企业账户")
    @PutMapping("/account")
    public ApiResponse<RailwayBusinessAccount> saveAccount(
            @Valid @RequestBody RailwayBusinessAccountRequest request) {
        return ApiResponse.success("企业账户已保存",
                businessTravelService.saveAccount(requireLogin(), request));
    }

    @Operation(summary = "月度预算使用概览")
    @GetMapping("/summary")
    public ApiResponse<RailwayBusinessSummaryVO> summary() {
        return ApiResponse.success(businessTravelService.summary(requireLogin()));
    }

    @Operation(summary = "提交出差申请")
    @PostMapping("/trips")
    public ApiResponse<RailwayBusinessTrip> createTrip(
            @Valid @RequestBody RailwayBusinessTripRequest request) {
        return ApiResponse.success("出差申请已提交",
                businessTravelService.createTrip(requireLogin(), request));
    }

    @Operation(summary = "我的出差申请")
    @GetMapping("/trips")
    public ApiResponse<PageResponse<RailwayBusinessTrip>> trips(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(businessTravelService.listTrips(requireLogin(), pageNum, pageSize));
    }

    @Operation(summary = "取消出差申请")
    @PostMapping("/trips/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        businessTravelService.cancelTrip(requireLogin(), id);
        return ApiResponse.success("申请已取消", null);
    }

    private Long requireLogin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
