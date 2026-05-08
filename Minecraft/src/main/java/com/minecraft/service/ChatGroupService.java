package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.GroupMember;

import java.util.List;

public interface ChatGroupService extends IService<ChatGroup> {
    ChatGroup getById(Long id);
    
    List<ChatGroup> getByCreatorId(Long creatorId);
    
    List<ChatGroup> searchByName(String name);
    
    ChatGroup createGroup(ChatGroup group);
    
    boolean updateGroup(ChatGroup group);
    
    boolean deleteGroup(Long id);
    
    void addMember(Long groupId, Long userId, String role);
    
    void removeMember(Long groupId, Long userId);
    
    List<GroupMember> getMembers(Long groupId);
    
    boolean isMember(Long groupId, Long userId);
}