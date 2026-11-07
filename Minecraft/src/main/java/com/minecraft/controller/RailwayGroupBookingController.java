package com.minecraft.controller;

import com.minecraft.dto.request.RailwayGroupBookingRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayGroupQuoteVO;
import com.minecraft.entity.RailwayGroupBooking;
import com.minecraft.exception.BusinessException;
import com.minecraft.service.RailwayGroupBookingService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "铁路团购服务")
@RestController
@RequestMapping("/api/railway/group")
public class RailwayGroupBookingController {

    private final RailwayGroupBookingService groupBookingService;

    public RailwayGroupBookingController(RailwayGroupBookingService groupBookingService) {
        this.groupBookingService = groupBookingService;
    }

    @Operation(summary = "团体票价预估")
    @GetMapping("/quote")
    public ApiResponse<RailwayGroupQuoteVO> quote(
            @RequestParam String fromStation,
            @RequestParam String toStation,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
            @RequestParam Integer passengerCount,
            @RequestParam(defaultValue = "SECOND_CLASS") String seatType,
            @RequestParam(required = false) String trainNo) {
        return ApiResponse.success(groupBookingService.quote(fromStation, toStation, travelDate,
                passengerCount, seatType, trainNo));
    }

    @Operation(summary = "提交团购申请")
    @PostMapping("/bookings")
    public ApiResponse<RailwayGroupBooking> create(
            @Valid @RequestBody RailwayGroupBookingRequest request) {
        return ApiResponse.success("团购申请已提交", groupBookingService.create(requireLogin(), request));
    }

    @Operation(summary = "我的团购申请")
    @GetMapping("/bookings")
    public ApiResponse<PageResponse<RailwayGroupBooking>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(groupBookingService.list(requireLogin(), pageNum, pageSize));
    }

    @Operation(summary = "取消团购申请")
    @PostMapping("/bookings/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        groupBookingService.cancel(requireLogin(), id);
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
