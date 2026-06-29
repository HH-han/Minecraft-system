package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.GroupNotification;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupNotificationMapper;
import com.minecraft.service.GroupNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupNotificationServiceImpl extends ServiceImpl<GroupNotificationMapper, GroupNotification> implements GroupNotificationService {

    @Override
    public List<GroupNotification> getNotifications(Long userId) {
        return baseMapper.selectByReceiverId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        GroupNotification notification = getById(notificationId);
        if (notification == null) {
            throw new BusinessException(404, "通知不存在");
        }
        if (!notification.getReceiverId().equals(userId)) {
            throw new BusinessException(403, "没有权限");
        }

        notification.setIsRead(true);
        updateById(notification);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        baseMapper.markAllRead(userId);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return baseMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public void sendNotification(Long receiverId, Long groupId, String type, String title, String content, Long targetId) {
        if (receiverId == null) {
            return;
        }

        GroupNotification notification = new GroupNotification();
        notification.setReceiverId(receiverId);
        notification.setGroupId(groupId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTargetId(targetId);
        notification.setIsRead(false);
        save(notification);
    }
}