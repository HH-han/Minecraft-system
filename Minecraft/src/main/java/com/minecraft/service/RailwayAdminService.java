package com.minecraft.service;

import com.minecraft.dto.request.RailwayGenerateRequest;
import com.minecraft.dto.response.RailwayAdminStatsVO;
import com.minecraft.dto.response.RailwayInventoryVO;
import com.minecraft.dto.response.RailwayServiceAdminVO;
import com.minecraft.dto.response.RailwayTrainAdminVO;
import com.minecraft.dto.response.RailwayTrainStopAdminVO;
import com.minecraft.dto.response.RailwayTrendVO;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayTrain;
import com.minecraft.entity.RailwayTrainStop;

import java.time.LocalDate;
import java.util.List;

public interface RailwayAdminService {

    List<RailwayStation> listStations(String keyword);

    RailwayStation saveStation(RailwayStation station);

    void deleteStation(Long id);

    List<RailwayTrainAdminVO> listTrains(String keyword);

    RailwayTrain saveTrain(RailwayTrain train);

    void deleteTrain(Long id);

    List<RailwayTrainStopAdminVO> getTrainStops(Long trainId);

    void saveTrainStops(Long trainId, List<RailwayTrainStop> stops);

    List<RailwayServiceAdminVO> listServices(LocalDate startDate, LocalDate endDate, String trainNo);

    int generateServices(RailwayGenerateRequest request);

    List<RailwayInventoryVO> listInventory(Long serviceId);

    RailwayAdminStatsVO stats();

    List<RailwayTrendVO> revenueTrend(int days);
}
