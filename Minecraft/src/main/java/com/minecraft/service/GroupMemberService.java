package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.GroupMember;

import java.util.List;

public interface GroupMemberService extends IService<GroupMember> {
    void inviteMembers(Long groupId, List<Long> inviteeIds, Long inviterId);
    void applyToJoin(Long groupId, Long userId);
    void approveJoin(Long groupId, Long userId, Long approverId);
    void rejectJoin(Long groupId, Long userId, Long rejecterId);
    void removeMember(Long groupId, Long userId, Long operatorId);
    void exitGroup(Long groupId, Long userId);
    void changeRole(Long groupId, Long userId, String role, Long operatorId);
    List<GroupMember> getMembers(Long groupId);
    GroupMember getMember(Long groupId, Long userId);
    String getMemberRole(Long groupId, Long userId);
    boolean isMember(Long groupId, Long userId);
    boolean isAdmin(Long groupId, Long userId);
    boolean isOwner(Long groupId, Long userId);
}