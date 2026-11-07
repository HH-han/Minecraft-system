package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.RailwayBoardVO;
import com.minecraft.dto.response.RailwayTrainVO;
import com.minecraft.service.RailwayInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "铁路信息查询")
@RestController
@RequestMapping("/api/railway/info")
public class RailwayInfoController {

    private final RailwayInfoService infoService;

    public RailwayInfoController(RailwayInfoService infoService) {
        this.infoService = infoService;
    }

    @Operation(summary = "车站大屏（出发 / 到达）")
    @GetMapping("/board")
    public ApiResponse<List<RailwayBoardVO>> board(
            @RequestParam String stationName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "DEPARTURE") String type) {
        return ApiResponse.success(infoService.board(stationName, date, type));
    }

    @Operation(summary = "区间票价查询")
    @GetMapping("/fares")
    public ApiResponse<List<RailwayTrainVO>> fares(
            @RequestParam String fromStation,
            @RequestParam String toStation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(infoService.fares(fromStation, toStation, date));
    }
}
