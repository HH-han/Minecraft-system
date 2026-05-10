package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.fastjson2.JSONObject;
import com.minecraft.entity.GroupChatMessage;
import com.minecraft.entity.GroupMember;
import com.minecraft.entity.User;
import com.minecraft.handler.WebSocketHandler;
import com.minecraft.mapper.GroupChatMessageMapper;
import com.minecraft.mapper.GroupMemberMapper;
import com.minecraft.mapper.UserMapper;
import com.minecraft.service.GroupChatService;
import com.minecraft.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GroupChatServiceImpl extends ServiceImpl<GroupChatMessageMapper, GroupChatMessage> implements GroupChatService {

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String GROUP_CHAT_HISTORY_KEY = "group:chat:history:";
    private static final String GROUP_UNREAD_COUNT_KEY = "group:unread:";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    @Override
    @Transactional
    public void sendMessage(Long groupId, Long senderId, String content, String messageType) {
        GroupChatMessage message = new GroupChatMessage();
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "text");

        User sender = userMapper.selectById(senderId);
        if (sender != null) {
            message.setSenderName(sender.getUsername());
            message.setSenderAvatar(sender.getAvatar());
        }

        save(message);

        String cacheKey = GROUP_CHAT_HISTORY_KEY + groupId;
        redisUtil.delete(cacheKey);

        LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(GroupMember::getGroupId, groupId);
        List<GroupMember> members = groupMemberMapper.selectList(memberWrapper);

        String senderName = sender != null ? sender.getUsername() : "";
        String senderAvatar = sender != null ? sender.getAvatar() : "";

        for (GroupMember member : members) {
            if (!member.getUserId().equals(senderId)) {
                String unreadKey = GROUP_UNREAD_COUNT_KEY + member.getUserId() + ":" + groupId;
                Integer count = (Integer) redisUtil.get(unreadKey);
                redisUtil.set(unreadKey, (count != null ? count : 0) + 1);

                JSONObject wsMessage = new JSONObject();
                wsMessage.put("type", "GROUP_MESSAGE");
                wsMessage.put("groupId", groupId);
                wsMessage.put("senderId", senderId);
                wsMessage.put("senderName", senderName);
                wsMessage.put("senderAvatar", senderAvatar);
                wsMessage.put("content", content);
                wsMessage.put("messageType", message.getMessageType());
                wsMessage.put("timestamp", System.currentTimeMillis());

                WebSocketHandler.sendMessage(member.getUserId(), wsMessage);
            }
        }
    }

    @Override
    public List<GroupChatMessage> getChatHistory(Long groupId, Integer limit) {
        String cacheKey = GROUP_CHAT_HISTORY_KEY + groupId;

        @SuppressWarnings("unchecked")
        List<GroupChatMessage> cachedHistory = (List<GroupChatMessage>) redisUtil.get(cacheKey);
        if (cachedHistory != null) {
            return cachedHistory;
        }

        LambdaQueryWrapper<GroupChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupChatMessage::getGroupId, groupId);
        wrapper.orderByDesc(GroupChatMessage::getCreateTime);
        wrapper.last("LIMIT " + (limit != null ? limit : 50));

        List<GroupChatMessage> history = list(wrapper);

        for (GroupChatMessage message : history) {
            if (message.getSenderName() == null || message.getSenderAvatar() == null) {
                User sender = userMapper.selectById(message.getSenderId());
                if (sender != null) {
                    message.setSenderName(sender.getUsername());
                    message.setSenderAvatar(sender.getAvatar());
                }
            }
        }

        redisUtil.set(cacheKey, history, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return history;
    }

    @Override
    public Long getUnreadCount(Long userId, Long groupId) {
        String unreadKey = GROUP_UNREAD_COUNT_KEY + userId + ":" + groupId;
        Integer count = (Integer) redisUtil.get(unreadKey);
        return count != null ? count.longValue() : 0L;
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long groupId) {
        String cacheKey = GROUP_CHAT_HISTORY_KEY + groupId;
        redisUtil.delete(cacheKey);

        String unreadKey = GROUP_UNREAD_COUNT_KEY + userId + ":" + groupId;
        redisUtil.delete(unreadKey);
    }
}
