package com.minecraft.controller;

import com.minecraft.dto.request.GroupInviteDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupMember;
import com.minecraft.service.GroupMemberService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "群组成员管理")
@RestController
@RequestMapping("/api/group/{groupId}/member")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    @Operation(summary = "邀请成员")
    @PostMapping("/invite")
    public ApiResponse<Void> inviteMembers(@PathVariable Long groupId, @RequestBody GroupInviteDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        dto.setGroupId(groupId);
        groupMemberService.inviteMembers(groupId, dto.getInviteeIds(), userId);
        return ApiResponse.success("邀请成功", null);
    }

    @Operation(summary = "申请加入群组")
    @PostMapping("/apply")
    public ApiResponse<Void> applyToJoin(@PathVariable Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupMemberService.applyToJoin(groupId, userId);
        return ApiResponse.success("申请成功", null);
    }

    @Operation(summary = "审批入群申请")
    @PutMapping("/{userId}/approve")
    public ApiResponse<Void> approveJoin(@PathVariable Long groupId, @PathVariable Long userId) {
        Long approverId = SecurityUtils.getCurrentUserId();
        groupMemberService.approveJoin(groupId, userId, approverId);
        return ApiResponse.success("审批通过", null);
    }

    @Operation(summary = "拒绝入群申请")
    @PutMapping("/{userId}/reject")
    public ApiResponse<Void> rejectJoin(@PathVariable Long groupId, @PathVariable Long userId) {
        Long rejecterId = SecurityUtils.getCurrentUserId();
        groupMemberService.rejectJoin(groupId, userId, rejecterId);
        return ApiResponse.success("已拒绝", null);
    }

    @Operation(summary = "移除成员")
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        groupMemberService.removeMember(groupId, userId, operatorId);
        return ApiResponse.success("移除成功", null);
    }

    @Operation(summary = "退出群组")
    @PostMapping("/exit")
    public ApiResponse<Void> exitGroup(@PathVariable Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupMemberService.exitGroup(groupId, userId);
        return ApiResponse.success("退出成功", null);
    }

    @Operation(summary = "修改成员角色")
    @PutMapping("/{userId}/role")
    public ApiResponse<Void> changeRole(@PathVariable Long groupId, @PathVariable Long userId, @RequestParam String role) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        groupMemberService.changeRole(groupId, userId, role, operatorId);
        return ApiResponse.success("角色修改成功", null);
    }

    @Operation(summary = "获取成员列表")
    @GetMapping("/list")
    public ApiResponse<List<GroupMember>> getMembers(@PathVariable Long groupId) {
        List<GroupMember> members = groupMemberService.getMembers(groupId);
        return ApiResponse.success(members);
    }

    @Operation(summary = "获取成员信息")
    @GetMapping("/{userId}")
    public ApiResponse<GroupMember> getMember(@PathVariable Long groupId, @PathVariable Long userId) {
        GroupMember member = groupMemberService.getMember(groupId, userId);
        return ApiResponse.success(member);
    }
}