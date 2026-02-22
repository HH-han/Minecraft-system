package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Cities;
import com.minecraft.mapper.CitiesMapper;
import com.minecraft.service.CitiesService;
import org.springframework.stereotype.Service;

@Service
public class CitiesServiceImpl extends ServiceImpl<CitiesMapper, Cities> implements CitiesService {

    @Override
    public Cities getCityById(Integer id) {
        return getById(id);
    }

    @Override
    public void addCity(Cities city) {
        save(city);
    }

    @Override
    public void updateCity(Cities city) {
        updateById(city);
    }

    @Override
    public void deleteCity(Integer id) {
        removeById(id);
    }
}
