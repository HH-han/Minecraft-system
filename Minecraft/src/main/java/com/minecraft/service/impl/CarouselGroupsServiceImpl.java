package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.CarouselGroups;
import com.minecraft.mapper.CarouselGroupsMapper;
import com.minecraft.service.CarouselGroupsService;
import org.springframework.stereotype.Service;

@Service
public class CarouselGroupsServiceImpl extends ServiceImpl<CarouselGroupsMapper, CarouselGroups> implements CarouselGroupsService {
}
