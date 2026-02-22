package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.MonthlyRecommendation;

import java.util.List;

public interface MonthlyRecommendationService extends IService<MonthlyRecommendation> {
    // 根据月份名称获取推荐内容
    List<MonthlyRecommendation> getRecommendationsByMonthName(String monthName);
    
    // 获取所有月份
    List<String> getAllMonths();
}
