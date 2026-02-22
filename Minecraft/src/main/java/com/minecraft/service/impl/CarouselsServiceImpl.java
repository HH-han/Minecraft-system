package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.CarouselGroupItems;
import com.minecraft.entity.CarouselGroups;
import com.minecraft.entity.Carousels;
import com.minecraft.mapper.CarouselsMapper;
import com.minecraft.service.CarouselsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarouselsServiceImpl extends ServiceImpl<CarouselsMapper, Carousels> implements CarouselsService {
    @Resource
    private CarouselGroupsServiceImpl carouselGroupsService;
    @Resource
    private CarouselGroupItemsServiceImpl carouselGroupItemsService;

    @Override
    public List<Carousels> getActiveCarouselsByPosition(String position) {
        QueryWrapper<Carousels> wrapper = new QueryWrapper<>();
        wrapper.eq("position", position)
                .eq("is_active", true)
                .eq("is_deleted", false)
                .orderByAsc("sort_order");
        
        // 处理时间限制
        wrapper.and(i -> i.eq("is_always_show", true)
                .or(j -> j.le("start_time", LocalDateTime.now())
                        .ge("end_time", LocalDateTime.now())));
        
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Carousels> getCarouselsByGroupCode(String groupCode) {
        // 先获取分组信息
        CarouselGroups group = carouselGroupsService.getOne(new QueryWrapper<CarouselGroups>()
                .eq("group_code", groupCode)
                .eq("is_active", true));
        
        if (group == null) {
            return List.of();
        }
        
        // 获取分组关联的轮播图
        List<CarouselGroupItems> groupItems = carouselGroupItemsService.list(new QueryWrapper<CarouselGroupItems>()
                .eq("group_id", group.getId())
                .eq("is_active", true)
                .orderByAsc("sort_order"));
        
        if (groupItems.isEmpty()) {
            return List.of();
        }
        
        // 提取轮播图ID
        List<Integer> carouselIds = groupItems.stream()
                .map(CarouselGroupItems::getCarouselId)
                .toList();
        
        // 查询轮播图信息
        QueryWrapper<Carousels> wrapper = new QueryWrapper<>();
        wrapper.in("id", carouselIds)
                .eq("is_active", true)
                .eq("is_deleted", false);
        
        // 处理时间限制
        wrapper.and(i -> i.eq("is_always_show", true)
                .or(j -> j.le("start_time", LocalDateTime.now())
                        .ge("end_time", LocalDateTime.now())));
        
        return baseMapper.selectList(wrapper);
    }
}
