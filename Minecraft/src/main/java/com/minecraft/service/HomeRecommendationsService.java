package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.HomeRecommendations;

import java.util.List;

public interface HomeRecommendationsService extends IService<HomeRecommendations> {
    PageResponse<HomeRecommendations> getHomeRecommendationsList(PageRequest request);
    HomeRecommendations getHomeRecommendationById(Long id);
    List<HomeRecommendations> getHotRecommendations(Integer limit);
    List<HomeRecommendations> getNewRecommendations(Integer limit);
    List<HomeRecommendations> getRecommendationsByCategory(Integer categoryId, Integer limit);
    List<HomeRecommendations> getRecommendationsByCity(String city, Integer limit);
    void addHomeRecommendation(HomeRecommendations homeRecommendations);
    void updateHomeRecommendation(HomeRecommendations homeRecommendations);
    void deleteHomeRecommendation(Long id);
}