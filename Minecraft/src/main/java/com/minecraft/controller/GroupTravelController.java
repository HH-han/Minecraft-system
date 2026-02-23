package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.GroupTravel;
import com.minecraft.service.GroupTravelService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "跟团游管理")
@RestController
@RequestMapping("/api/group")
public class GroupTravelController {

    @Autowired
    private GroupTravelService groupTravelService;

    @Operation(summary ="获取旅行团列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getGroupList(PageRequest request) {
        return ApiResponse.success(groupTravelService.getGroupList(request));
    }

    @Operation(summary ="获取旅行团详情")
    @GetMapping("/{id}")
    public ApiResponse<GroupTravel> getGroupDetail(@PathVariable Long id) {
        return ApiResponse.success(groupTravelService.getGroupDetail(id));
    }

    @Operation(summary ="创建旅行团")
    @PostMapping("/create")
    public ApiResponse<Void> createGroup(@RequestBody GroupTravel group) {
        groupTravelService.createGroup(group);
        return ApiResponse.success("创建成功", null);
    }

    @Operation(summary ="加入旅行团")
    @PostMapping("/join")
    public ApiResponse<Void> joinGroup(@RequestParam Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupTravelService.joinGroup(groupId, userId);
        return ApiResponse.success("加入成功", null);
    }

    @Operation(summary ="退出旅行团")
    @PostMapping("/leave")
    public ApiResponse<Void> leaveGroup(@RequestParam Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupTravelService.leaveGroup(groupId, userId);
        return ApiResponse.success("退出成功", null);
    }
}
