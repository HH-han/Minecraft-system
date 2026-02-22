package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Countries;

public interface CountriesService extends IService<Countries> {
    Countries getCountryById(Integer id);
    void addCountry(Countries country);
    void updateCountry(Countries country);
    void deleteCountry(Integer id);
}
