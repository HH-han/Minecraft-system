package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.WorldCharacteristics;
import com.minecraft.mapper.WorldCharacteristicsMapper;
import com.minecraft.service.WorldCharacteristicsService;
import org.springframework.stereotype.Service;

@Service
public class WorldCharacteristicsServiceImpl extends ServiceImpl<WorldCharacteristicsMapper, WorldCharacteristics> implements WorldCharacteristicsService {
    // 可以实现自定义方法
}