package com.minecraft.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.GroupChatMessage;
import com.minecraft.handler.WebSocketHandler;
import com.minecraft.mapper.GroupChatMessageMapper;
import com.minecraft.service.GroupChatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupChatServiceImpl extends ServiceImpl<GroupChatMessageMapper, GroupChatMessage> implements GroupChatService {

    @Override
    public void sendMessage(GroupChatMessage message) {
        save(message);
        
        JSONObject wsMessage = new JSONObject();
        wsMessage.put("type", "GROUP_MESSAGE");
        wsMessage.put("groupId", message.getGroupId());
        wsMessage.put("senderId", message.getSenderId());
        wsMessage.put("content", message.getContent());
        wsMessage.put("messageType", message.getMessageType());
        wsMessage.put("timestamp", System.currentTimeMillis());
        
        WebSocketHandler.sendGroupMessage(message.getGroupId(), wsMessage);
    }

    @Override
    public List<GroupChatMessage> getGroupHistory(Long groupId, Integer limit) {
        return baseMapper.selectByGroupId(groupId, limit != null ? limit : 50);
    }
}