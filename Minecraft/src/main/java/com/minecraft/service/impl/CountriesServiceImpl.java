package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Countries;
import com.minecraft.mapper.CountriesMapper;
import com.minecraft.service.CountriesService;
import org.springframework.stereotype.Service;

@Service
public class CountriesServiceImpl extends ServiceImpl<CountriesMapper, Countries> implements CountriesService {

    @Override
    public Countries getCountryById(Integer id) {
        return getById(id);
    }

    @Override
    public void addCountry(Countries country) {
        save(country);
    }

    @Override
    public void updateCountry(Countries country) {
        updateById(country);
    }

    @Override
    public void deleteCountry(Integer id) {
        removeById(id);
    }
}
