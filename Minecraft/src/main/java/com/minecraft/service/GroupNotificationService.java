package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.GroupNotification;

import java.util.List;

public interface GroupNotificationService extends IService<GroupNotification> {
    List<GroupNotification> getNotifications(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllRead(Long userId);
    Integer getUnreadCount(Long userId);
    void sendNotification(Long receiverId, Long groupId, String type, String title, String content, Long targetId);
}