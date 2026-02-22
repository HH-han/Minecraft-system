package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.ChatMessage;
import com.minecraft.mapper.ChatMessageMapper;
import com.minecraft.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    @Override
    public void sendMessage(ChatMessage message) {
        save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(Long userId, Long friendId, Integer limit) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatMessage::getSenderId, userId).eq(ChatMessage::getReceiverId, friendId))
                .or()
                .eq(ChatMessage::getSenderId, friendId)
                .eq(ChatMessage::getReceiverId, userId);
        wrapper.orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + (limit != null ? limit : 50));
        return list(wrapper);
    }

    @Override
    public void markAsRead(Long userId, Long friendId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSenderId, friendId)
                .eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getIsRead, 0);
        
        List<ChatMessage> messages = list(wrapper);
        messages.forEach(msg -> {
            msg.setIsRead(1);
            updateById(msg);
        });
    }
}
