package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.CarouselStats;
import com.minecraft.mapper.CarouselStatsMapper;
import com.minecraft.service.CarouselStatsService;
import org.springframework.stereotype.Service;

@Service
public class CarouselStatsServiceImpl extends ServiceImpl<CarouselStatsMapper, CarouselStats> implements CarouselStatsService {
}
