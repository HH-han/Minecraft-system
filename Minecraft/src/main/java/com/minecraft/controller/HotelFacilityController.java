package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.HotelFacility;
import com.minecraft.service.HotelFacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "酒店设施管理")
@RestController
@RequestMapping("/api/hotelFacility")
public class HotelFacilityController {

    @Autowired
    private HotelFacilityService hotelFacilityService;

    @Operation(summary = "获取酒店设施列表")
    @GetMapping("/list")
    public ApiResponse<List<HotelFacility>> getFacilitiesByHotelId(@RequestParam Long hotelId) {
        return ApiResponse.success(hotelFacilityService.getFacilitiesByHotelId(hotelId));
    }

    @Operation(summary = "添加酒店设施")
    @PostMapping("/add")
    public ApiResponse<Long> addFacility(@RequestBody Map<String, Object> request) {
        HotelFacility facility = new HotelFacility();
        facility.setHotelId(Long.parseLong(request.get("hotelId").toString()));
        facility.setFacilityName(request.get("facility").toString());
        facility.setStatus(1);
        hotelFacilityService.addFacility(facility);
        return ApiResponse.success("添加成功", facility.getId());
    }

    @Operation(summary = "更新酒店设施")
    @PutMapping("/update")
    public ApiResponse<Void> updateFacility(@RequestBody HotelFacility facility) {
        hotelFacilityService.updateFacility(facility);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除酒店设施")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFacility(@PathVariable Long id) {
        hotelFacilityService.deleteFacility(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "批量添加酒店设施")
    @PostMapping("/batchAdd")
    public ApiResponse<Void> batchAddFacilities(@RequestBody Map<String, Object> request) {
        Long hotelId = Long.parseLong(request.get("hotelId").toString());
        @SuppressWarnings("unchecked")
        List<String> facilities = (List<String>) request.get("facilities");
        hotelFacilityService.batchAddFacilities(hotelId, facilities);
        return ApiResponse.success("批量添加成功", null);
    }
}