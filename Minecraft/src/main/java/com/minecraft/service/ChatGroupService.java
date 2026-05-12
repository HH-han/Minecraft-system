package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.CreateGroupRequest;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.GroupMember;

import java.util.List;

public interface ChatGroupService extends IService<ChatGroup> {
    ChatGroup getById(Long id);

    List<ChatGroup> getByCreatorId(Long creatorId);
    
    List<ChatGroup> getByUserId(Long userId);

    List<ChatGroup> searchByName(String name);

    ChatGroup createGroup(ChatGroup group);

    ChatGroup createGroupWithMembers(CreateGroupRequest request);

    boolean updateGroup(ChatGroup group);

    boolean deleteGroup(Long id);

    void addMember(Long groupId, Long userId, String role);

    void removeMember(Long groupId, Long userId);

    List<GroupMember> getMembers(Long groupId);

    boolean isMember(Long groupId, Long userId);

void inviteMembers(Long groupId, List<Long> userIds);

    boolean updateGroupWithFile(ChatGroup group, org.springframework.web.multipart.MultipartFile avatar);
}
