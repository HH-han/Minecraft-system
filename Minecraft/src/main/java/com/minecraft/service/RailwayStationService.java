package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.RailwayStation;

import java.util.List;

public interface RailwayStationService extends IService<RailwayStation> {

    List<RailwayStation> search(String keyword);

    List<RailwayStation> listAll();

    RailwayStation saveStation(RailwayStation station);

    void deleteStation(Long id);
}
