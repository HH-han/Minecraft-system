package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Carousels;

import java.util.List;

public interface CarouselsService extends IService<Carousels> {
    List<Carousels> getActiveCarouselsByPosition(String position);
    List<Carousels> getCarouselsByGroupCode(String groupCode);
}
