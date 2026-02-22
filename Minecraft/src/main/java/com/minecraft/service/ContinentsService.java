package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Continents;

public interface ContinentsService extends IService<Continents> {
    Continents getContinentById(Integer id);
    void addContinent(Continents continent);
    void updateContinent(Continents continent);
    void deleteContinent(Integer id);
}
