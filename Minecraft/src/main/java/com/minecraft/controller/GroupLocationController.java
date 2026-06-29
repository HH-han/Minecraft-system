package com.minecraft.controller;

import com.minecraft.dto.request.LocationUpdateDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupLocationHistory;
import com.minecraft.service.GroupLocationService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.MemberLocationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "位置共享")
@RestController
@RequestMapping("/api/location")
public class GroupLocationController {

    @Autowired
    private GroupLocationService groupLocationService;

    @Operation(summary = "更新当前位置")
    @PostMapping("/update")
    public ApiResponse<Void> updateLocation(@RequestBody LocationUpdateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupLocationService.updateLocation(dto, userId);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "获取群组成员的实时位置")
    @GetMapping("/group/{groupId}")
    public ApiResponse<List<MemberLocationVO>> getGroupLocations(@PathVariable Long groupId) {
        List<MemberLocationVO> locations = groupLocationService.getGroupLocations(groupId);
        return ApiResponse.success(locations);
    }

    @Operation(summary = "获取成员位置历史")
    @GetMapping("/history/{groupId}/{userId}")
    public ApiResponse<List<GroupLocationHistory>> getUserLocationHistory(@PathVariable Long groupId, @PathVariable Long userId) {
        List<GroupLocationHistory> history = groupLocationService.getUserLocationHistory(groupId, userId);
        return ApiResponse.success(history);
    }
}