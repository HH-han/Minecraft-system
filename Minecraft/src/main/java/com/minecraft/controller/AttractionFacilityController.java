package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.AttractionFacility;
import com.minecraft.service.AttractionFacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "景点设施管理")
@RestController
@RequestMapping("/api/attractionFacility")
public class AttractionFacilityController {

    @Autowired
    private AttractionFacilityService attractionFacilityService;

    @Operation(summary = "获取景点设施列表")
    @GetMapping("/list")
    public ApiResponse<List<AttractionFacility>> getFacilitiesByAttractionId(@RequestParam Long attractionId) {
        return ApiResponse.success(attractionFacilityService.getFacilitiesByAttractionId(attractionId));
    }

    @Operation(summary = "添加景点设施")
    @PostMapping("/add")
    public ApiResponse<Long> addFacility(@RequestBody Map<String, Object> request) {
        AttractionFacility facility = new AttractionFacility();
        facility.setAttractionId(Long.parseLong(request.get("attractionId").toString()));
        facility.setFacilityName(request.get("facility").toString());
        facility.setStatus(1);
        attractionFacilityService.addFacility(facility);
        return ApiResponse.success("添加成功", facility.getId());
    }

    @Operation(summary = "更新景点设施")
    @PutMapping("/update")
    public ApiResponse<Void> updateFacility(@RequestBody AttractionFacility facility) {
        attractionFacilityService.updateFacility(facility);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除景点设施")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFacility(@PathVariable Long id) {
        attractionFacilityService.deleteFacility(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "批量添加景点设施")
    @PostMapping("/batchAdd")
    public ApiResponse<Void> batchAddFacilities(@RequestBody Map<String, Object> request) {
        Long attractionId = Long.parseLong(request.get("attractionId").toString());
        @SuppressWarnings("unchecked")
        List<String> facilities = (List<String>) request.get("facilities");
        attractionFacilityService.batchAddFacilities(attractionId, facilities);
        return ApiResponse.success("批量添加成功", null);
    }
}