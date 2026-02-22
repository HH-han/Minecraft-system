package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelPlan;
import com.minecraft.mapper.TravelPlanMapper;
import com.minecraft.service.TravelService;
import org.springframework.stereotype.Service;

@Service
public class TravelServiceImpl extends ServiceImpl<TravelPlanMapper, TravelPlan> implements TravelService {

    @Override
    public PageResponse<?> getTravelPlanList(Long userId, PageRequest request) {
        Page<TravelPlan> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<TravelPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelPlan::getUserId, userId);

        Page<TravelPlan> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public TravelPlan getTravelPlanDetail(Long id) {
        return getById(id);
    }

    @Override
    public void createTravelPlan(TravelPlan travelPlan) {
        save(travelPlan);
    }

    @Override
    public void updateTravelPlan(TravelPlan travelPlan) {
        updateById(travelPlan);
    }

    @Override
    public void deleteTravelPlan(Long id) {
        removeById(id);
    }
}
