package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.ChatMessage;

import java.util.List;

public interface ChatService extends IService<ChatMessage> {
    void sendMessage(ChatMessage message);
    List<ChatMessage> getChatHistory(Long userId, Long friendId, Integer limit);
    void markAsRead(Long userId, Long friendId);
}
