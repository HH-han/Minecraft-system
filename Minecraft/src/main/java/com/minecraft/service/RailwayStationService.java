package com.minecraft.service;

import com.minecraft.entity.RailwayStation;

import java.util.List;

public interface RailwayStationService {

    List<RailwayStation> search(String keyword);

    List<RailwayStation> listAll();

    RailwayStation saveStation(RailwayStation station);

    void deleteStation(Long id);
}
