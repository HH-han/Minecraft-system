package com.minecraft.controller;

import com.minecraft.dto.request.GroupCreateDTO;
import com.minecraft.dto.request.GroupUpdateDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.TravelGroup;
import com.minecraft.service.TravelGroupService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.TravelGroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "旅行群组管理")
@RestController
@RequestMapping("/api/group")
public class TravelGroupController {

    @Autowired
    private TravelGroupService travelGroupService;

    @Operation(summary = "创建群组")
    @PostMapping("/create")
    public ApiResponse<Long> createGroup(@RequestBody GroupCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        Long groupId = travelGroupService.createGroup(dto, userId);
        return ApiResponse.success("创建成功", groupId);
    }

    @Operation(summary = "更新群组信息")
    @PutMapping("/{groupId}")
    public ApiResponse<Void> updateGroup(@PathVariable Long groupId, @RequestBody GroupUpdateDTO dto) {
        dto.setId(groupId);
        travelGroupService.updateGroup(dto);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "解散群组")
    @DeleteMapping("/{groupId}")
    public ApiResponse<Void> dissolveGroup(@PathVariable Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        travelGroupService.dissolveGroup(groupId, userId);
        return ApiResponse.success("解散成功", null);
    }

    @Operation(summary = "获取群组详情")
    @GetMapping("/{groupId}")
    public ApiResponse<TravelGroupVO> getGroupDetail(@PathVariable Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        TravelGroupVO vo = travelGroupService.getGroupDetail(groupId, userId);
        return ApiResponse.success(vo);
    }

    @Operation(summary = "获取我的群组列表")
    @GetMapping("/list")
    public ApiResponse<List<TravelGroupVO>> getMyGroups() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TravelGroupVO> groups = travelGroupService.getMyGroups(userId);
        return ApiResponse.success(groups);
    }

    @Operation(summary = "搜索公开群组")
    @GetMapping("/search")
    public ApiResponse<List<TravelGroup>> searchPublicGroups(@RequestParam String keyword) {
        List<TravelGroup> groups = travelGroupService.searchPublicGroups(keyword);
        return ApiResponse.success(groups);
    }

    @Operation(summary = "转让群主")
    @PostMapping("/{groupId}/transfer")
    public ApiResponse<Void> transferOwner(@PathVariable Long groupId, @RequestParam Long newOwnerId) {
        Long userId = SecurityUtils.getCurrentUserId();
        travelGroupService.transferOwner(groupId, newOwnerId, userId);
        return ApiResponse.success("转让成功", null);
    }
}