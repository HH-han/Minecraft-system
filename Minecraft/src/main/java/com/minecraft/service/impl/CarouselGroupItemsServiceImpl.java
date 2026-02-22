package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.CarouselGroupItems;
import com.minecraft.mapper.CarouselGroupItemsMapper;
import com.minecraft.service.CarouselGroupItemsService;
import org.springframework.stereotype.Service;

@Service
public class CarouselGroupItemsServiceImpl extends ServiceImpl<CarouselGroupItemsMapper, CarouselGroupItems> implements CarouselGroupItemsService {
}
