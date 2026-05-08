package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.ChatMessage;
import com.minecraft.mapper.ChatMessageMapper;
import com.minecraft.service.ChatService;
import com.minecraft.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisUtil redisUtil;

    private static final String CHAT_HISTORY_KEY = "chat:history:";
    private static final String UNREAD_COUNT_KEY = "chat:unread:";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    @Override
    @Transactional
    public void sendMessage(ChatMessage message) {
        if (message.getIsRead() == null) {
            message.setIsRead(0);
        }
        if (message.getMessageType() == null) {
            message.setMessageType("text");
        }
        
        save(message);
        
        String cacheKey = CHAT_HISTORY_KEY + getChatKey(message.getSenderId(), message.getReceiverId());
        redisUtil.delete(cacheKey);
        
        String unreadKey = UNREAD_COUNT_KEY + message.getReceiverId() + ":" + message.getSenderId();
        Long count = (Long) redisUtil.get(unreadKey);
        redisUtil.set(unreadKey, (count != null ? count : 0) + 1);
        
        messagingTemplate.convertAndSendToUser(message.getReceiverId().toString(), "/queue/messages", message);
    }

    @Override
    public List<ChatMessage> getChatHistory(Long userId, Long friendId, Integer limit) {
        String cacheKey = CHAT_HISTORY_KEY + getChatKey(userId, friendId);
        
        @SuppressWarnings("unchecked")
        List<ChatMessage> cachedHistory = (List<ChatMessage>) redisUtil.get(cacheKey);
        if (cachedHistory != null) {
            return cachedHistory;
        }
        
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatMessage::getSenderId, userId).eq(ChatMessage::getReceiverId, friendId))
                .or()
                .eq(ChatMessage::getSenderId, friendId)
                .eq(ChatMessage::getReceiverId, userId);
        wrapper.orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + (limit != null ? limit : 50));
        
        List<ChatMessage> history = list(wrapper);
        redisUtil.set(cacheKey, history, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        return history;
    }

    @Override
    @Transactional
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
        
        String cacheKey = CHAT_HISTORY_KEY + getChatKey(userId, friendId);
        redisUtil.delete(cacheKey);
        
        String unreadKey = UNREAD_COUNT_KEY + userId + ":" + friendId;
        redisUtil.delete(unreadKey);
    }

    @Override
    public Long getUnreadCount(Long userId, Long friendId) {
        String unreadKey = UNREAD_COUNT_KEY + userId + ":" + friendId;
        Long count = (Long) redisUtil.get(unreadKey);
        return count != null ? count : 0L;
    }

    @Override
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        ChatMessage message = getById(messageId);
        if (message != null && message.getReceiverId().equals(userId) && message.getIsRead() == 0) {
            message.setIsRead(1);
            updateById(message);
            
            String cacheKey = CHAT_HISTORY_KEY + getChatKey(userId, message.getSenderId());
            redisUtil.delete(cacheKey);
            
            String unreadKey = UNREAD_COUNT_KEY + userId + ":" + message.getSenderId();
            Long count = (Long) redisUtil.get(unreadKey);
            if (count != null && count > 0) {
                redisUtil.set(unreadKey, count - 1);
            }
        }
    }

    private String getChatKey(Long userId1, Long userId2) {
        return userId1 < userId2 ? userId1 + ":" + userId2 : userId2 + ":" + userId1;
    }
}