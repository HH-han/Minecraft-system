package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Continents;
import com.minecraft.mapper.ContinentsMapper;
import com.minecraft.service.ContinentsService;
import org.springframework.stereotype.Service;

@Service
public class ContinentsServiceImpl extends ServiceImpl<ContinentsMapper, Continents> implements ContinentsService {

    @Override
    public Continents getContinentById(Integer id) {
        return getById(id);
    }

    @Override
    public void addContinent(Continents continent) {
        save(continent);
    }

    @Override
    public void updateContinent(Continents continent) {
        updateById(continent);
    }

    @Override
    public void deleteContinent(Integer id) {
        removeById(id);
    }
}
