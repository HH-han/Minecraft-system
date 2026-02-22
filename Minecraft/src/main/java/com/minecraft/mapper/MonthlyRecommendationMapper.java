package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.MonthlyRecommendation;

import java.util.List;

public interface MonthlyRecommendationMapper extends BaseMapper<MonthlyRecommendation> {
    // 根据月份名称获取推荐内容
    List<MonthlyRecommendation> getRecommendationsByMonthName(String monthName);
}
