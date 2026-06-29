package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupNotification;
import com.minecraft.service.GroupNotificationService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "消息通知")
@RestController
@RequestMapping("/api/notification")
public class GroupNotificationController {

    @Autowired
    private GroupNotificationService groupNotificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping("/list")
    public ApiResponse<List<GroupNotification>> getNotifications() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GroupNotification> notifications = groupNotificationService.getNotifications(userId);
        return ApiResponse.success(notifications);
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupNotificationService.markAsRead(id, userId);
        return ApiResponse.success("已标记为已读", null);
    }

    @Operation(summary = "全部已读")
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        groupNotificationService.markAllRead(userId);
        return ApiResponse.success("全部已读", null);
    }

    @Operation(summary = "获取未读数量")
    @GetMapping("/unread-count")
    public ApiResponse<Integer> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer count = groupNotificationService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }
}