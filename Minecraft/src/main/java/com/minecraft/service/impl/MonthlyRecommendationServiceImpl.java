package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Month;
import com.minecraft.entity.MonthlyRecommendation;
import com.minecraft.mapper.MonthMapper;
import com.minecraft.mapper.MonthlyRecommendationMapper;
import com.minecraft.service.MonthlyRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonthlyRecommendationServiceImpl extends ServiceImpl<MonthlyRecommendationMapper, MonthlyRecommendation> implements MonthlyRecommendationService {
    
    @Autowired
    private MonthlyRecommendationMapper monthlyRecommendationMapper;
    
    @Autowired
    private MonthMapper monthMapper;
    
    @Override
    public List<MonthlyRecommendation> getRecommendationsByMonthName(String monthName) {
        return monthlyRecommendationMapper.getRecommendationsByMonthName(monthName);
    }
    
    @Override
    public List<String> getAllMonths() {
        List<Month> months = monthMapper.selectList(null);
        return months.stream()
                .map(Month::getMonthName)
                .collect(Collectors.toList());
    }
}
