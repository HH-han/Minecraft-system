package com.minecraft.controller;

import com.minecraft.dto.request.CreateGroupRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.GroupMember;
import com.minecraft.service.ChatGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "群组管理")
@RestController
@RequestMapping("/api/public/chat-group")
public class ChatGroupController {

    @Autowired
    private ChatGroupService chatGroupService;

    @Operation(summary = "根据ID查询群组")
    @GetMapping("/{id}")
    public ApiResponse<ChatGroup> getGroupById(@PathVariable Long id) {
        ChatGroup group = chatGroupService.getById(id);
        if (group == null) {
            return ApiResponse.error(404, "群组不存在");
        }
        return ApiResponse.success("成功", group);
    }

    @Operation(summary = "根据创建者ID查询群组")
    @GetMapping("/creator/{creatorId}")
    public ApiResponse<List<ChatGroup>> getGroupsByCreatorId(@PathVariable Long creatorId) {
        List<ChatGroup> groups = chatGroupService.getByCreatorId(creatorId);
        return ApiResponse.success("成功", groups);
    }
    
    @Operation(summary = "根据用户ID查询所在的所有群组")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ChatGroup>> getGroupsByUserId(@PathVariable Long userId) {
        List<ChatGroup> groups = chatGroupService.getByUserId(userId);
        return ApiResponse.success("成功", groups);
    }

    @Operation(summary = "根据名称搜索群组")
    @GetMapping("/search")
    public ApiResponse<List<ChatGroup>> searchGroups(@RequestParam String name) {
        List<ChatGroup> groups = chatGroupService.searchByName(name);
        return ApiResponse.success("成功", groups);
    }

    @Operation(summary = "创建群组")
    @PostMapping
    public ApiResponse<ChatGroup> createGroup(@RequestBody ChatGroup group) {
        if (group.getName() == null || group.getName().isEmpty()) {
            return ApiResponse.error(400, "群名称不能为空");
        }
        if (group.getCreatorId() == null) {
            return ApiResponse.error(400, "创建者ID不能为空");
        }

        ChatGroup createdGroup = chatGroupService.createGroup(group);
        return ApiResponse.success("群组创建成功", createdGroup);
    }

    @Operation(summary = "创建群组并添加成员")
    @PostMapping("/with-members")
    public ApiResponse<ChatGroup> createGroupWithMembers(@RequestBody CreateGroupRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            return ApiResponse.error(400, "群名称不能为空");
        }
        if (request.getCreatorId() == null) {
            return ApiResponse.error(400, "创建者ID不能为空");
        }

        ChatGroup createdGroup = chatGroupService.createGroupWithMembers(request);
        return ApiResponse.success("群组创建成功", createdGroup);
    }

    @Operation(summary = "更新群组信息")
    @PutMapping
    public ApiResponse<Void> updateGroup(@RequestBody ChatGroup group) {
        if (group.getId() == null) {
            return ApiResponse.error(400, "群组ID不能为空");
        }

        boolean updated = chatGroupService.updateGroup(group);
        if (!updated) {
            return ApiResponse.error(404, "群组不存在");
        }

        return ApiResponse.success("群组更新成功", null);
    }

    @Operation(summary = "删除群组")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGroup(@PathVariable Long id) {
        boolean deleted = chatGroupService.deleteGroup(id);
        if (!deleted) {
            return ApiResponse.error(404, "群组不存在");
        }

        return ApiResponse.success("群组删除成功", null);
    }

    @Operation(summary = "添加群成员")
    @PostMapping("/{groupId}/members")
    public ApiResponse<Void> addMember(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "member") String role) {

        chatGroupService.addMember(groupId, userId, role);
        return ApiResponse.success("成员添加成功", null);
    }

    @Operation(summary = "移除群成员")
    @DeleteMapping("/{groupId}/members/{userId}")
    public ApiResponse<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {

        chatGroupService.removeMember(groupId, userId);
        return ApiResponse.success("成员移除成功", null);
    }

    @Operation(summary = "获取群成员列表")
    @GetMapping("/{groupId}/members")
    public ApiResponse<List<GroupMember>> getMembers(@PathVariable Long groupId) {
        List<GroupMember> members = chatGroupService.getMembers(groupId);
        return ApiResponse.success("成功", members);
    }

    @Operation(summary = "邀请好友加入群聊")
    @PostMapping("/{groupId}/invite")
    public ApiResponse<Void> inviteFriends(
            @PathVariable Long groupId,
            @RequestBody List<Long> friendIds) {
        
        if (friendIds == null || friendIds.isEmpty()) {
            return ApiResponse.error(400, "好友ID列表不能为空");
        }

        chatGroupService.inviteMembers(groupId, friendIds);
        return ApiResponse.success("邀请成功", null);
    }
}
