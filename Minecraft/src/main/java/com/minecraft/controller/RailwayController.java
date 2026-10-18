package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.RailwayServiceDetailVO;
import com.minecraft.dto.response.RailwayStatusVO;
import com.minecraft.dto.response.RailwayTimetableVO;
import com.minecraft.dto.response.RailwayTrainVO;
import com.minecraft.entity.RailwayStation;
import com.minecraft.service.RailwayScheduleService;
import com.minecraft.service.RailwayStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "铁路票务")
@RestController
@RequestMapping("/api/railway")
public class RailwayController {

    private final RailwayStationService stationService;
    private final RailwayScheduleService scheduleService;

    public RailwayController(RailwayStationService stationService,
                             RailwayScheduleService scheduleService) {
        this.stationService = stationService;
        this.scheduleService = scheduleService;
    }

    @Operation(summary = "车站联想查询")
    @GetMapping("/stations")
    public ApiResponse<List<RailwayStation>> stations(
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(stationService.search(keyword));
    }

    @Operation(summary = "车票查询")
    @GetMapping("/tickets/search")
    public ApiResponse<List<RailwayTrainVO>> search(
            @RequestParam String fromStation,
            @RequestParam String toStation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(scheduleService.search(fromStation, toStation, date));
    }

    @Operation(summary = "运行车次详情（经停站与全程余票）")
    @GetMapping("/services/{id}")
    public ApiResponse<RailwayServiceDetailVO> serviceDetail(@PathVariable Long id) {
        return ApiResponse.success(scheduleService.getServiceDetail(id));
    }

    @Operation(summary = "时刻表查询")
    @GetMapping("/timetable")
    public ApiResponse<RailwayTimetableVO> timetable(
            @RequestParam String trainNo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(scheduleService.getTimetable(trainNo, date));
    }

    @Operation(summary = "正晚点查询")
    @GetMapping("/status")
    public ApiResponse<RailwayStatusVO> status(@RequestParam String trainNo) {
        return ApiResponse.success(scheduleService.getStatus(trainNo));
    }
}
