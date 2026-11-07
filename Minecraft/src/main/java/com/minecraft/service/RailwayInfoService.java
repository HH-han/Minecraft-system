package com.minecraft.service;

import com.minecraft.dto.response.RailwayBoardVO;
import com.minecraft.dto.response.RailwayTrainVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 铁路信息查询（车站大屏、票价）
 */
public interface RailwayInfoService {

    /**
     * 车站大屏
     *
     * @param type DEPARTURE 出发 / ARRIVAL 到达
     */
    List<RailwayBoardVO> board(String stationName, LocalDate date, String type);

    /**
     * 区间票价查询（复用余票查询结果）
     */
    List<RailwayTrainVO> fares(String fromStation, String toStation, LocalDate date);
}
