package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Attractions;
import com.minecraft.mapper.AttractionsMapper;
import com.minecraft.service.AttractionsService;
import org.springframework.stereotype.Service;

@Service
public class AttractionsServiceImpl extends ServiceImpl<AttractionsMapper, Attractions> implements AttractionsService {

    @Override
    public Attractions getAttractionById(Integer id) {
        return getById(id);
    }

    @Override
    public void addAttraction(Attractions attraction) {
        save(attraction);
    }

    @Override
    public void updateAttraction(Attractions attraction) {
        updateById(attraction);
    }

    @Override
    public void deleteAttraction(Integer id) {
        removeById(id);
    }
}
