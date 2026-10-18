package com.minecraft.controller;

import com.minecraft.dto.request.RailwayGenerateRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayAdminStatsVO;
import com.minecraft.dto.response.RailwayInventoryVO;
import com.minecraft.dto.response.RailwayOrderVO;
import com.minecraft.dto.response.RailwayServiceAdminVO;
import com.minecraft.dto.response.RailwayTrainAdminVO;
import com.minecraft.dto.response.RailwayTrainStopAdminVO;
import com.minecraft.dto.response.RailwayTrendVO;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayTrain;
import com.minecraft.entity.RailwayTrainStop;
import com.minecraft.service.RailwayAdminService;
import com.minecraft.service.RailwayOrderService;
import com.minecraft.service.RailwayStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "铁路管理端")
@RestController
@RequestMapping("/api/railway/admin")
public class RailwayAdminController {

    private final RailwayAdminService adminService;
    private final RailwayOrderService orderService;
    private final RailwayStationService stationService;

    public RailwayAdminController(RailwayAdminService adminService,
                                  RailwayOrderService orderService,
                                  RailwayStationService stationService) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.stationService = stationService;
    }

    // ---------------- 车站 ----------------

    @Operation(summary = "车站列表")
    @GetMapping("/stations")
    public ApiResponse<List<RailwayStation>> stations(
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(adminService.listStations(keyword));
    }

    @Operation(summary = "全部车站字典")
    @GetMapping("/stations/all")
    public ApiResponse<List<RailwayStation>> allStations() {
        return ApiResponse.success(stationService.listAll());
    }

    @Operation(summary = "新增车站")
    @PostMapping("/stations")
    public ApiResponse<RailwayStation> addStation(@RequestBody RailwayStation station) {
        station.setId(null);
        return ApiResponse.success("新增成功", adminService.saveStation(station));
    }

    @Operation(summary = "更新车站")
    @PutMapping("/stations/{id}")
    public ApiResponse<RailwayStation> updateStation(@PathVariable Long id,
                                                     @RequestBody RailwayStation station) {
        station.setId(id);
        return ApiResponse.success("更新成功", adminService.saveStation(station));
    }

    @Operation(summary = "删除车站")
    @DeleteMapping("/stations/{id}")
    public ApiResponse<Void> deleteStation(@PathVariable Long id) {
        adminService.deleteStation(id);
        return ApiResponse.success("删除成功", null);
    }

    // ---------------- 车次 ----------------

    @Operation(summary = "车次列表")
    @GetMapping("/trains")
    public ApiResponse<List<RailwayTrainAdminVO>> trains(
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(adminService.listTrains(keyword));
    }

    @Operation(summary = "新增车次")
    @PostMapping("/trains")
    public ApiResponse<RailwayTrain> addTrain(@RequestBody RailwayTrain train) {
        train.setId(null);
        return ApiResponse.success("新增成功", adminService.saveTrain(train));
    }

    @Operation(summary = "更新车次")
    @PutMapping("/trains/{id}")
    public ApiResponse<RailwayTrain> updateTrain(@PathVariable Long id,
                                                 @RequestBody RailwayTrain train) {
        train.setId(id);
        return ApiResponse.success("更新成功", adminService.saveTrain(train));
    }

    @Operation(summary = "删除车次")
    @DeleteMapping("/trains/{id}")
    public ApiResponse<Void> deleteTrain(@PathVariable Long id) {
        adminService.deleteTrain(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "车次经停站")
    @GetMapping("/trains/{id}/stops")
    public ApiResponse<List<RailwayTrainStopAdminVO>> trainStops(@PathVariable Long id) {
        return ApiResponse.success(adminService.getTrainStops(id));
    }

    @Operation(summary = "保存车次经停站")
    @PutMapping("/trains/{id}/stops")
    public ApiResponse<Void> saveTrainStops(@PathVariable Long id,
                                            @RequestBody List<RailwayTrainStop> stops) {
        adminService.saveTrainStops(id, stops);
        return ApiResponse.success("保存成功", null);
    }

    // ---------------- 排班与库存 ----------------

    @Operation(summary = "运行车次列表")
    @GetMapping("/services")
    public ApiResponse<List<RailwayServiceAdminVO>> services(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String trainNo) {
        return ApiResponse.success(adminService.listServices(startDate, endDate, trainNo));
    }

    @Operation(summary = "批量生成排班与库存")
    @PostMapping("/services/generate")
    public ApiResponse<Integer> generateServices(
            @RequestBody(required = false) RailwayGenerateRequest request) {
        int created = adminService.generateServices(request == null ? new RailwayGenerateRequest() : request);
        return ApiResponse.success("本次生成 " + created + " 个运行车次", created);
    }

    @Operation(summary = "区间库存明细")
    @GetMapping("/inventory")
    public ApiResponse<List<RailwayInventoryVO>> inventory(@RequestParam Long serviceId) {
        return ApiResponse.success(adminService.listInventory(serviceId));
    }

    // ---------------- 订单与统计 ----------------

    @Operation(summary = "全部铁路订单")
    @GetMapping("/orders")
    public ApiResponse<PageResponse<RailwayOrderVO>> orders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(orderService.listAllOrders(pageNum, pageSize, status, keyword));
    }

    @Operation(summary = "铁路经营统计")
    @GetMapping("/stats")
    public ApiResponse<RailwayAdminStatsVO> stats() {
        return ApiResponse.success(adminService.stats());
    }

    @Operation(summary = "收入趋势")
    @GetMapping("/stats/trend")
    public ApiResponse<List<RailwayTrendVO>> revenueTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        return ApiResponse.success(adminService.revenueTrend(days));
    }
}
