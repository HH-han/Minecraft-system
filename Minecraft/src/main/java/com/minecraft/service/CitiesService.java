package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Cities;

public interface CitiesService extends IService<Cities> {
    Cities getCityById(Integer id);
    void addCity(Cities city);
    void updateCity(Cities city);
    void deleteCity(Integer id);
}
