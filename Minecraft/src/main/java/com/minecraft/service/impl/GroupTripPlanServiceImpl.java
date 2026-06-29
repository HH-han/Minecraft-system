package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.GroupTripPlanDTO;
import com.minecraft.entity.GroupTripPlan;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupTripPlanMapper;
import com.minecraft.service.GroupMemberService;
import com.minecraft.service.GroupTripPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupTripPlanServiceImpl extends ServiceImpl<GroupTripPlanMapper, GroupTripPlan> implements GroupTripPlanService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Override
    @Transactional
    public GroupTripPlan addTripItem(GroupTripPlanDTO dto, Long userId) {
        if (!groupMemberService.isMember(dto.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        GroupTripPlan tripPlan = new GroupTripPlan();
        BeanUtils.copyProperties(dto, tripPlan);
        tripPlan.setCreatedBy(userId);
        save(tripPlan);

        return tripPlan;
    }

    @Override
    @Transactional
    public void updateTripItem(Long tripId, GroupTripPlanDTO dto, Long userId) {
        GroupTripPlan tripPlan = getById(tripId);
        if (tripPlan == null) {
            throw new BusinessException(404, "行程不存在");
        }
        if (!tripPlan.getCreatedBy().equals(userId) && !groupMemberService.isAdmin(tripPlan.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限修改");
        }

        BeanUtils.copyProperties(dto, tripPlan);
        updateById(tripPlan);
    }

    @Override
    @Transactional
    public void deleteTripItem(Long tripId, Long userId) {
        GroupTripPlan tripPlan = getById(tripId);
        if (tripPlan == null) {
            throw new BusinessException(404, "行程不存在");
        }
        if (!tripPlan.getCreatedBy().equals(userId) && !groupMemberService.isAdmin(tripPlan.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限删除");
        }

        removeById(tripId);
    }

    @Override
    public Map<Integer, List<GroupTripPlan>> getTripPlanByDay(Long groupId) {
        List<GroupTripPlan> plans = baseMapper.selectByGroupIdOrdered(groupId);
        return plans.stream()
            .collect(Collectors.groupingBy(GroupTripPlan::getDayIndex, LinkedHashMap::new, Collectors.toList()));
    }

    @Override
    public List<GroupTripPlan> getTripPlanList(Long groupId) {
        return baseMapper.selectByGroupIdOrdered(groupId);
    }
}