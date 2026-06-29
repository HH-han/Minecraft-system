package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.GroupCreateDTO;
import com.minecraft.dto.request.GroupUpdateDTO;
import com.minecraft.entity.GroupMember;
import com.minecraft.entity.TravelGroup;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupMemberMapper;
import com.minecraft.mapper.TravelGroupMapper;
import com.minecraft.mapper.UserMapper;
import com.minecraft.service.TravelGroupService;
import com.minecraft.vo.TravelGroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelGroupServiceImpl extends ServiceImpl<TravelGroupMapper, TravelGroup> implements TravelGroupService {

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Long createGroup(GroupCreateDTO dto, Long userId) {
        TravelGroup group = new TravelGroup();
        BeanUtils.copyProperties(dto, group);
        group.setCreatedBy(userId);
        group.setStatus(1);
        group.setDeleted(0);
        if (group.getMaxMembers() == null) {
            group.setMaxMembers(20);
        }
        if (group.getIsPublic() == null) {
            group.setIsPublic(false);
        }
        save(group);

        GroupMember member = new GroupMember();
        member.setGroupId(group.getId());
        member.setUserId(userId);
        member.setRole("owner");
        member.setStatus(1);
        groupMemberMapper.insert(member);

        return group.getId();
    }

    @Override
    public void updateGroup(GroupUpdateDTO dto) {
        TravelGroup group = getById(dto.getId());
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }
        BeanUtils.copyProperties(dto, group);
        updateById(group);
    }

    @Override
    @Transactional
    public void dissolveGroup(Long groupId, Long userId) {
        TravelGroup group = getById(groupId);
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }
        if (!group.getCreatedBy().equals(userId)) {
            throw new BusinessException(403, "只有群主可以解散群组");
        }
        group.setStatus(3);
        updateById(group);

        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        List<GroupMember> members = groupMemberMapper.selectList(wrapper);
        members.forEach(member -> {
            member.setStatus(3);
            member.setExitTime(java.time.LocalDateTime.now());
            groupMemberMapper.updateById(member);
        });
    }

    @Override
    public TravelGroupVO getGroupDetail(Long groupId, Long userId) {
        TravelGroup group = getById(groupId);
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }

        TravelGroupVO vo = new TravelGroupVO();
        BeanUtils.copyProperties(group, vo);

        User creator = userMapper.selectById(group.getCreatedBy());
        if (creator != null) {
            vo.setCreatorName(creator.getNickname());
            vo.setCreatorAvatar(creator.getAvatar());
        }

        LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(GroupMember::getGroupId, groupId);
        memberWrapper.eq(GroupMember::getStatus, 1);
        List<GroupMember> members = groupMemberMapper.selectList(memberWrapper);
        vo.setMembers(members);
        vo.setCurrentMembers(members.size());

        LambdaQueryWrapper<GroupMember> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(GroupMember::getGroupId, groupId);
        userRoleWrapper.eq(GroupMember::getUserId, userId);
        GroupMember userMember = groupMemberMapper.selectOne(userRoleWrapper);
        if (userMember != null) {
            vo.setUserRole(userMember.getRole());
        }

        return vo;
    }

    @Override
    public List<TravelGroupVO> getMyGroups(Long userId) {
        List<TravelGroup> groups = baseMapper.selectByUserId(userId);
        return groups.stream().map(group -> {
            TravelGroupVO vo = new TravelGroupVO();
            BeanUtils.copyProperties(group, vo);

            LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GroupMember::getGroupId, group.getId());
            wrapper.eq(GroupMember::getStatus, 1);
            Long memberCount = groupMemberMapper.selectCount(wrapper);
            vo.setCurrentMembers(memberCount.intValue());

            User creator = userMapper.selectById(group.getCreatedBy());
            if (creator != null) {
                vo.setCreatorName(creator.getNickname());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TravelGroup> searchPublicGroups(String keyword) {
        return baseMapper.searchPublicGroups(keyword);
    }

    @Override
    @Transactional
    public void transferOwner(Long groupId, Long newOwnerId, Long currentUserId) {
        TravelGroup group = getById(groupId);
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }
        if (!group.getCreatedBy().equals(currentUserId)) {
            throw new BusinessException(403, "只有群主可以转让");
        }

        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        wrapper.eq(GroupMember::getUserId, newOwnerId);
        GroupMember newOwner = groupMemberMapper.selectOne(wrapper);
        if (newOwner == null) {
            throw new BusinessException(400, "新群主不是群成员");
        }

        group.setCreatedBy(newOwnerId);
        updateById(group);

        newOwner.setRole(1);
        groupMemberMapper.updateById(newOwner);

        GroupMember oldOwner = new GroupMember();
        oldOwner.setGroupId(groupId);
        oldOwner.setUserId(currentUserId);
        oldOwner.setRole(2);
        groupMemberMapper.update(oldOwner, wrapper);
    }
}