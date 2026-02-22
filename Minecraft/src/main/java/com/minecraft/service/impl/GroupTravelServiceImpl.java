package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.GroupTravel;
import com.minecraft.mapper.GroupTravelMapper;
import com.minecraft.service.GroupTravelService;
import org.springframework.stereotype.Service;

@Service
public class GroupTravelServiceImpl extends ServiceImpl<GroupTravelMapper, GroupTravel> implements GroupTravelService {

    @Override
    public PageResponse<?> getGroupList(PageRequest request) {
        Page<GroupTravel> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<GroupTravel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupTravel::getStatus, 1)
                .orderByDesc(GroupTravel::getCreateTime);

        Page<GroupTravel> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public GroupTravel getGroupDetail(Long id) {
        return getById(id);
    }

    @Override
    public void createGroup(GroupTravel group) {
        save(group);
    }

    @Override
    public void joinGroup(Long groupId, Long userId) {
        GroupTravel group = getById(groupId);
        if (group.getCurrentMembers() >= group.getMaxMembers()) {
            throw new RuntimeException("团队已满员");
        }
        group.setCurrentMembers(group.getCurrentMembers() + 1);
        updateById(group);
    }

    @Override
    public void leaveGroup(Long groupId, Long userId) {
        GroupTravel group = getById(groupId);
        group.setCurrentMembers(group.getCurrentMembers() - 1);
        updateById(group);
    }
}
