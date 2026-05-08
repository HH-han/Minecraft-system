package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.GroupChatMessage;

import java.util.List;

public interface GroupChatService extends IService<GroupChatMessage> {
    void sendMessage(GroupChatMessage message);
    
    List<GroupChatMessage> getGroupHistory(Long groupId, Integer limit);
}