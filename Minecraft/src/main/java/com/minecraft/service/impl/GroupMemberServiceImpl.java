package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.GroupInvite;
import com.minecraft.entity.GroupMember;
import com.minecraft.entity.TravelGroup;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupInviteMapper;
import com.minecraft.mapper.GroupMemberMapper;
import com.minecraft.mapper.TravelGroupMapper;
import com.minecraft.service.GroupMemberService;
import com.minecraft.service.GroupNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {

    @Autowired
    private TravelGroupMapper travelGroupMapper;

    @Autowired
    private GroupInviteMapper groupInviteMapper;

    @Autowired
    private GroupNotificationService notificationService;

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Override
    @Transactional
    public void inviteMembers(Long groupId, List<Long> inviteeIds, Long inviterId) {
        TravelGroup group = travelGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }

        for (Long inviteeId : inviteeIds) {
            LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(GroupMember::getGroupId, groupId);
            memberWrapper.eq(GroupMember::getUserId, inviteeId);
            GroupMember existingMember = groupMemberMapper.selectOne(memberWrapper);
            if (existingMember != null && existingMember.getStatus() == 1) {
                continue;
            }

            GroupInvite invite = new GroupInvite();
            invite.setGroupId(groupId);
            invite.setInviterId(inviterId);
            invite.setInviteeId(inviteeId);
            invite.setType(1);
            invite.setStatus(0);
            invite.setExpireAt(LocalDateTime.now().plusDays(7));
            groupInviteMapper.insert(invite);

            notificationService.sendNotification(inviteeId, groupId, "JOIN_APPLY", 
                "群邀请", group.getName() + "邀请你加入群组", invite.getId());
        }
    }

    @Override
    @Transactional
    public void applyToJoin(Long groupId, Long userId) {
        TravelGroup group = travelGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }
        if (!group.getIsPublic()) {
            throw new BusinessException(400, "该群组不公开");
        }

        LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(GroupMember::getGroupId, groupId);
        memberWrapper.eq(GroupMember::getUserId, userId);
        GroupMember existingMember = groupMemberMapper.selectOne(memberWrapper);
        if (existingMember != null && existingMember.getStatus() == 1) {
            throw new BusinessException(400, "已经是群成员");
        }

        GroupInvite invite = new GroupInvite();
        invite.setGroupId(groupId);
        invite.setInviteeId(userId);
        invite.setType(2);
        invite.setStatus(0);
        invite.setExpireAt(LocalDateTime.now().plusDays(7));
        groupInviteMapper.insert(invite);

        notificationService.sendNotification(group.getCreatedBy(), groupId, "JOIN_APPLY",
            "入群申请", "有用户申请加入群组", invite.getId());
    }

    @Override
    @Transactional
    public void approveJoin(Long groupId, Long userId, Long approverId) {
        if (!isAdmin(groupId, approverId)) {
            throw new BusinessException(403, "没有审批权限");
        }

        GroupInvite invite = groupInviteMapper.selectByGroupAndUser(groupId, userId);
        if (invite == null) {
            throw new BusinessException(404, "申请不存在");
        }

        invite.setStatus(1);
        invite.setHandledAt(LocalDateTime.now());
        groupInviteMapper.updateById(invite);

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(3);
        member.setStatus(1);
        groupMemberMapper.insert(member);

        notificationService.sendNotification(userId, groupId, "JOIN_APPLY",
            "入群申请已通过", "你已成功加入群组", groupId);
    }

    @Override
    @Transactional
    public void rejectJoin(Long groupId, Long userId, Long rejecterId) {
        if (!isAdmin(groupId, rejecterId)) {
            throw new BusinessException(403, "没有审批权限");
        }

        GroupInvite invite = groupInviteMapper.selectByGroupAndUser(groupId, userId);
        if (invite == null) {
            throw new BusinessException(404, "申请不存在");
        }

        invite.setStatus(2);
        invite.setHandledAt(LocalDateTime.now());
        groupInviteMapper.updateById(invite);

        TravelGroup group = travelGroupMapper.selectById(groupId);
        notificationService.sendNotification(userId, groupId, "JOIN_APPLY",
            "入群申请已拒绝", "你的入群申请已被" + group.getName() + "拒绝", null);
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userId, Long operatorId) {
        if (!isAdmin(groupId, operatorId)) {
            throw new BusinessException(403, "没有权限");
        }
        if (isOwner(groupId, userId)) {
            throw new BusinessException(400, "不能移除群主");
        }

        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        wrapper.eq(GroupMember::getUserId, userId);
        GroupMember member = groupMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException(404, "成员不存在");
        }

        member.setStatus(3);
        member.setExitTime(LocalDateTime.now());
        groupMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void exitGroup(Long groupId, Long userId) {
        TravelGroup group = travelGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }
        if (group.getCreatedBy().equals(userId)) {
            throw new BusinessException(400, "群主不能退出，请先转让或解散群组");
        }

        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        wrapper.eq(GroupMember::getUserId, userId);
        GroupMember member = groupMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException(404, "不是群成员");
        }

        member.setStatus(3);
        member.setExitTime(LocalDateTime.now());
        groupMemberMapper.updateById(member);
    }

    @Override
    public void changeRole(Long groupId, Long userId, Integer role, Long operatorId) {
        if (!isOwner(groupId, operatorId)) {
            throw new BusinessException(403, "只有群主可以修改角色");
        }

        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        wrapper.eq(GroupMember::getUserId, userId);
        GroupMember member = groupMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException(404, "成员不存在");
        }

        member.setRole(role);
        groupMemberMapper.updateById(member);
    }

    @Override
    public List<GroupMember> getMembers(Long groupId) {
        return groupMemberMapper.selectByGroupId(groupId);
    }

    @Override
    public GroupMember getMember(Long groupId, Long userId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        wrapper.eq(GroupMember::getUserId, userId);
        return groupMemberMapper.selectOne(wrapper);
    }

    @Override
    public Integer getMemberRole(Long groupId, Long userId) {
        GroupMember member = getMember(groupId, userId);
        return member != null ? member.getRole() : null;
    }

    @Override
    public boolean isMember(Long groupId, Long userId) {
        GroupMember member = getMember(groupId, userId);
        return member != null && member.getStatus() == 1;
    }

    @Override
    public boolean isAdmin(Long groupId, Long userId) {
        Integer role = getMemberRole(groupId, userId);
        return role != null && role <= 2;
    }

    @Override
    public boolean isOwner(Long groupId, Long userId) {
        Integer role = getMemberRole(groupId, userId);
        return role != null && role == 1;
    }
}