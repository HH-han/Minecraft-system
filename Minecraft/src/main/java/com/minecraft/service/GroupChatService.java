package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.GroupChatMessage;

import java.util.List;

public interface GroupChatService extends IService<GroupChatMessage> {
    void sendMessage(Long groupId, Long senderId, String content, String messageType);
    List<GroupChatMessage> getChatHistory(Long groupId, Integer limit);
    Long getUnreadCount(Long userId, Long groupId);
    void markAsRead(Long userId, Long groupId);
}
