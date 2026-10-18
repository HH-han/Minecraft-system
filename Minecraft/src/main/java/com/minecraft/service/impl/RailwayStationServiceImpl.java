package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.entity.RailwayStation;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayStationMapper;
import com.minecraft.service.RailwayStationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RailwayStationServiceImpl implements RailwayStationService {

    private final RailwayStationMapper stationMapper;

    public RailwayStationServiceImpl(RailwayStationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    @Override
    public List<RailwayStation> search(String keyword) {
        LambdaQueryWrapper<RailwayStation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(RailwayStation::getStationName, kw)
                    .or().like(RailwayStation::getCityName, kw)
                    .or().like(RailwayStation::getPinyin, kw)
                    .or().like(RailwayStation::getInitial, kw.toUpperCase()));
        }
        wrapper.orderByAsc(RailwayStation::getId).last("LIMIT 20");
        return stationMapper.selectList(wrapper);
    }

    @Override
    public List<RailwayStation> listAll() {
        return stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                .orderByAsc(RailwayStation::getId));
    }

    @Override
    public RailwayStation saveStation(RailwayStation station) {
        if (station == null || !StringUtils.hasText(station.getStationName())) {
            throw new BusinessException("车站名称不能为空");
        }
        if (!StringUtils.hasText(station.getCityName())) {
            station.setCityName(station.getStationName());
        }
        if (station.getId() == null) {
            stationMapper.insert(station);
        } else {
            stationMapper.updateById(station);
        }
        return station;
    }

    @Override
    public void deleteStation(Long id) {
        stationMapper.deleteById(id);
    }
}
