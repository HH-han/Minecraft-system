package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.HomeRecommendations;
import com.minecraft.mapper.HomeRecommendationsMapper;
import com.minecraft.service.HomeRecommendationsService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeRecommendationsServiceImpl extends ServiceImpl<HomeRecommendationsMapper, HomeRecommendations> implements HomeRecommendationsService {

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public PageResponse<HomeRecommendations> getHomeRecommendationsList(PageRequest request) {
        Page<HomeRecommendations> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<HomeRecommendations> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(HomeRecommendations::getStatus, 1);

        if (request.getKeyword() != null) {
            wrapper.like(HomeRecommendations::getName, request.getKeyword())
                    .or()
                    .like(HomeRecommendations::getSummary, request.getKeyword())
                    .or()
                    .like(HomeRecommendations::getAddress, request.getKeyword());
        }

        if (request.getSortBy() != null) {
            if ("weight".equals(request.getSortBy())) {
                wrapper.orderByDesc(HomeRecommendations::getWeight)
                        .orderByDesc(HomeRecommendations::getCreatedAt);
            } else if ("rating".equals(request.getSortBy())) {
                wrapper.orderByDesc(HomeRecommendations::getAverageRating)
                        .orderByDesc(HomeRecommendations::getRatingCount);
            } else if ("price".equals(request.getSortBy())) {
                wrapper.orderByAsc(HomeRecommendations::getMinPrice);
            } else {
                wrapper.orderByDesc(HomeRecommendations::getCreatedAt);
            }
        } else {
            wrapper.orderByDesc(HomeRecommendations::getWeight)
                    .orderByDesc(HomeRecommendations::getCreatedAt);
        }

        Page<HomeRecommendations> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public HomeRecommendations getHomeRecommendationById(Long id) {
        HomeRecommendations recommendation = getById(id);
        if (recommendation != null) {
            // 增加浏览次数
            recommendation.setViewCount(recommendation.getViewCount() + 1);
            updateById(recommendation);
        }
        return recommendation;
    }

    @Override
    public List<HomeRecommendations> getHotRecommendations(Integer limit) {
        LambdaQueryWrapper<HomeRecommendations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeRecommendations::getStatus, 1)
                .eq(HomeRecommendations::getIsHot, true)
                .orderByDesc(HomeRecommendations::getWeight)
                .orderByDesc(HomeRecommendations::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));
        return list(wrapper);
    }

    @Override
    public List<HomeRecommendations> getNewRecommendations(Integer limit) {
        LambdaQueryWrapper<HomeRecommendations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeRecommendations::getStatus, 1)
                .eq(HomeRecommendations::getIsNew, true)
                .orderByDesc(HomeRecommendations::getCreatedAt)
                .last("LIMIT " + (limit != null ? limit : 10));
        return list(wrapper);
    }

    @Override
    public List<HomeRecommendations> getRecommendationsByCategory(Integer categoryId, Integer limit) {
        LambdaQueryWrapper<HomeRecommendations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeRecommendations::getStatus, 1)
                .eq(HomeRecommendations::getCategoryId, categoryId)
                .orderByDesc(HomeRecommendations::getWeight)
                .orderByDesc(HomeRecommendations::getCreatedAt)
                .last("LIMIT " + (limit != null ? limit : 10));
        return list(wrapper);
    }

    @Override
    public List<HomeRecommendations> getRecommendationsByCity(String city, Integer limit) {
        LambdaQueryWrapper<HomeRecommendations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeRecommendations::getStatus, 1)
                .eq(HomeRecommendations::getCity, city)
                .orderByDesc(HomeRecommendations::getWeight)
                .orderByDesc(HomeRecommendations::getCreatedAt)
                .last("LIMIT " + (limit != null ? limit : 10));
        return list(wrapper);
    }

    @Override
    public void addHomeRecommendation(HomeRecommendations homeRecommendations) {
        save(homeRecommendations);
    }

    @Override
    public void updateHomeRecommendation(HomeRecommendations homeRecommendations) {
        updateById(homeRecommendations);
    }

    @Override
    public void deleteHomeRecommendation(Long id) {
        removeById(id);
    }

    @Override
    public boolean save(HomeRecommendations homeRecommendations) {
        try {
            // 处理封面图片上传
            if (homeRecommendations.getCoverImageUrl() != null && homeRecommendations.getCoverImageUrl().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(homeRecommendations.getCoverImageUrl());
                homeRecommendations.setCoverImageUrl(processedImage);
            }

            // 处理Banner图片上传
            if (homeRecommendations.getBannerImageUrl() != null && homeRecommendations.getBannerImageUrl().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(homeRecommendations.getBannerImageUrl());
                homeRecommendations.setBannerImageUrl(processedImage);
            }

            return super.save(homeRecommendations);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateById(HomeRecommendations homeRecommendations) {
        try {
            // 处理封面图片上传
            if (homeRecommendations.getCoverImageUrl() != null && homeRecommendations.getCoverImageUrl().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(homeRecommendations.getCoverImageUrl());
                homeRecommendations.setCoverImageUrl(processedImage);
            }

            // 处理Banner图片上传
            if (homeRecommendations.getBannerImageUrl() != null && homeRecommendations.getBannerImageUrl().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(homeRecommendations.getBannerImageUrl());
                homeRecommendations.setBannerImageUrl(processedImage);
            }

            return super.updateById(homeRecommendations);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}