package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Attractions;

public interface AttractionsService extends IService<Attractions> {
    Attractions getAttractionById(Integer id);
    void addAttraction(Attractions attraction);
    void updateAttraction(Attractions attraction);
    void deleteAttraction(Integer id);
}
