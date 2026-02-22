package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Month;
import com.minecraft.mapper.MonthMapper;
import com.minecraft.service.MonthService;
import org.springframework.stereotype.Service;

@Service
public class MonthServiceImpl extends ServiceImpl<MonthMapper, Month> implements MonthService {
}
