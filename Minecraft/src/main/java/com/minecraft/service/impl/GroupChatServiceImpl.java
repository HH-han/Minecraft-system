package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.GroupChatMessage;
import com.minecraft.mapper.GroupChatMessageMapper;
import com.minecraft.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupChatServiceImpl extends ServiceImpl<GroupChatMessageMapper, GroupChatMessage> implements GroupChatService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(GroupChatMessage message) {
        save(message);
        messagingTemplate.convertAndSend("/topic/group/" + message.getGroupId(), message);
    }

    @Override
    public List<GroupChatMessage> getGroupHistory(Long groupId, Integer limit) {
        return baseMapper.selectByGroupId(groupId, limit != null ? limit : 50);
    }
}