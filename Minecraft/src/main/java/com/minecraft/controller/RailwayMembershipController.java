package com.minecraft.controller;

import com.minecraft.dto.request.RailwayMemberRedeemRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayMemberOverviewVO;
import com.minecraft.entity.RailwayMemberGift;
import com.minecraft.entity.RailwayMemberRedemption;
import com.minecraft.entity.RailwayPointLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.service.RailwayMembershipService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "铁路会员服务")
@RestController
@RequestMapping("/api/railway/member")
public class RailwayMembershipController {

    private final RailwayMembershipService membershipService;

    public RailwayMembershipController(RailwayMembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Operation(summary = "会员中心概览（等级 / 积分 / 购票统计）")
    @GetMapping("/overview")
    public ApiResponse<RailwayMemberOverviewVO> overview() {
        return ApiResponse.success(membershipService.overview(requireLogin()));
    }

    @Operation(summary = "积分流水")
    @GetMapping("/points")
    public ApiResponse<PageResponse<RailwayPointLog>> points(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(membershipService.pageLogs(requireLogin(), pageNum, pageSize));
    }

    @Operation(summary = "积分礼品列表")
    @GetMapping("/gifts")
    public ApiResponse<List<RailwayMemberGift>> gifts() {
        return ApiResponse.success(membershipService.listGifts());
    }

    @Operation(summary = "积分兑换礼品")
    @PostMapping("/redeem")
    public ApiResponse<RailwayMemberRedemption> redeem(
            @Valid @RequestBody RailwayMemberRedeemRequest request) {
        return ApiResponse.success("兑换成功",
                membershipService.redeem(requireLogin(), request.getGiftId()));
    }

    private Long requireLogin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
