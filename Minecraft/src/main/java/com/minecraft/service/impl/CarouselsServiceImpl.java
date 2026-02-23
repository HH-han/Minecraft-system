package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.CarouselGroupItems;
import com.minecraft.entity.CarouselGroups;
import com.minecraft.entity.Carousels;
import com.minecraft.mapper.CarouselsMapper;
import com.minecraft.service.CarouselsService;
import com.minecraft.utils.ImageUtils;
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
    @Resource
    private ImageUtils imageUtils;

    @Override
    public List<Carousels> getActiveCarouselsByPosition(String position) {
        QueryWrapper<Carousels> wrapper = new QueryWrapper<>();
        wrapper.eq("position", position)
                .eq("is_active", true)
                .and(i -> i.eq("is_deleted", false)
                        .or()
                        .isNull("is_deleted"))
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
                .orderByAsc("sort_order"));
        
        // 过滤活跃的轮播图项目（如果需要）
        if (groupItems != null && !groupItems.isEmpty()) {
            groupItems = groupItems.stream()
                    .filter(item -> item.getIsActive() == null || item.getIsActive())
                    .collect(java.util.stream.Collectors.toList());
        }
        
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
                .and(i -> i.eq("is_deleted", false)
                        .or()
                        .isNull("is_deleted"));
        
        // 处理时间限制
        wrapper.and(i -> i.eq("is_always_show", true)
                .or(j -> j.le("start_time", LocalDateTime.now())
                        .ge("end_time", LocalDateTime.now())));
        
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public boolean save(Carousels carousel) {
        try {
            // 处理图片上传
            if (carousel.getImageUrl() != null && carousel.getImageUrl().startsWith("data:image")) {
                String processedImageUrl = imageUtils.processBase64Image(carousel.getImageUrl());
                carousel.setImageUrl(processedImageUrl);
            }
            
            // 处理移动端图片上传
            if (carousel.getMobileImageUrl() != null && carousel.getMobileImageUrl().startsWith("data:image")) {
                String processedMobileImageUrl = imageUtils.processBase64Image(carousel.getMobileImageUrl());
                carousel.setMobileImageUrl(processedMobileImageUrl);
            }
            
            // 处理缩略图上传
            if (carousel.getThumbnailUrl() != null && carousel.getThumbnailUrl().startsWith("data:image")) {
                String processedThumbnailUrl = imageUtils.processBase64Image(carousel.getThumbnailUrl());
                carousel.setThumbnailUrl(processedThumbnailUrl);
            }
            
            return super.save(carousel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(Carousels carousel) {
        try {
            // 处理图片上传
            if (carousel.getImageUrl() != null && carousel.getImageUrl().startsWith("data:image")) {
                String processedImageUrl = imageUtils.processBase64Image(carousel.getImageUrl());
                carousel.setImageUrl(processedImageUrl);
            }
            
            // 处理移动端图片上传
            if (carousel.getMobileImageUrl() != null && carousel.getMobileImageUrl().startsWith("data:image")) {
                String processedMobileImageUrl = imageUtils.processBase64Image(carousel.getMobileImageUrl());
                carousel.setMobileImageUrl(processedMobileImageUrl);
            }
            
            // 处理缩略图上传
            if (carousel.getThumbnailUrl() != null && carousel.getThumbnailUrl().startsWith("data:image")) {
                String processedThumbnailUrl = imageUtils.processBase64Image(carousel.getThumbnailUrl());
                carousel.setThumbnailUrl(processedThumbnailUrl);
            }
            
            return super.updateById(carousel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
