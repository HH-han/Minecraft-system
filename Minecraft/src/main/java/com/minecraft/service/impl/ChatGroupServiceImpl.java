package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.CreateGroupRequest;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.GroupMember;
import com.minecraft.mapper.ChatGroupMapper;
import com.minecraft.mapper.GroupMemberMapper;
import com.minecraft.service.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroup> implements ChatGroupService {

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Override
    public ChatGroup getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<ChatGroup> getByCreatorId(Long creatorId) {
        return baseMapper.selectByCreatorId(creatorId);
    }
    
    @Override
    public List<ChatGroup> getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<ChatGroup> searchByName(String name) {
        return baseMapper.searchByName(name);
    }

    @Override
    @Transactional
    public ChatGroup createGroup(ChatGroup group) {
        if (group.getMaxMembers() == null) {
            group.setMaxMembers(200);
        }
        group.setMemberCount(0);
        baseMapper.insert(group);

        GroupMember member = new GroupMember();
        member.setGroupId(group.getId());
        member.setUserId(group.getCreatorId());
        member.setRole("admin");
        groupMemberMapper.insert(member);

        group.setMemberCount(1);
        baseMapper.updateById(group);

        return group;
    }

    @Override
    @Transactional
    public ChatGroup createGroupWithMembers(CreateGroupRequest request) {
        ChatGroup group = new ChatGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatorId(request.getCreatorId());
        group.setMaxMembers(200);
        group.setMemberCount(0);

        baseMapper.insert(group);

        List<Long> memberIds = request.getMemberIds();
        if (memberIds != null && !memberIds.isEmpty()) {
            for (Long userId : memberIds) {
                GroupMember member = new GroupMember();
                member.setGroupId(group.getId());
                member.setUserId(userId);
                member.setRole(userId.equals(request.getCreatorId()) ? "admin" : "member");
                groupMemberMapper.insert(member);
            }
            group.setMemberCount(memberIds.size());
        } else {
            GroupMember member = new GroupMember();
            member.setGroupId(group.getId());
            member.setUserId(request.getCreatorId());
            member.setRole("admin");
            groupMemberMapper.insert(member);
            group.setMemberCount(1);
        }

        baseMapper.updateById(group);
        return group;
    }

    @Override
    public boolean updateGroup(ChatGroup group) {
        return baseMapper.updateById(group) > 0;
    }

    @Override
    @Transactional
    public boolean deleteGroup(Long id) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, id);
        groupMemberMapper.delete(wrapper);

        return baseMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public void addMember(Long groupId, Long userId, String role) {
        if (isMember(groupId, userId)) {
            return;
        }

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(role != null ? role : "member");
        groupMemberMapper.insert(member);

        ChatGroup group = baseMapper.selectById(groupId);
        if (group != null) {
            group.setMemberCount(group.getMemberCount() + 1);
            baseMapper.updateById(group);
        }
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId)
               .eq(GroupMember::getUserId, userId);
        groupMemberMapper.delete(wrapper);

        ChatGroup group = baseMapper.selectById(groupId);
        if (group != null) {
            group.setMemberCount(Math.max(0, group.getMemberCount() - 1));
            baseMapper.updateById(group);
        }
    }

    @Override
    public List<GroupMember> getMembers(Long groupId) {
        return groupMemberMapper.selectByGroupId(groupId);
    }

    @Override
    public boolean isMember(Long groupId, Long userId) {
        return groupMemberMapper.countByGroupAndUser(groupId, userId) > 0;
    }
}
