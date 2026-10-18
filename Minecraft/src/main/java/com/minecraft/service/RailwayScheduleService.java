package com.minecraft.service;

import com.minecraft.dto.response.RailwayServiceDetailVO;
import com.minecraft.dto.response.RailwayStatusVO;
import com.minecraft.dto.response.RailwayTimetableVO;
import com.minecraft.dto.response.RailwayTrainVO;

import java.time.LocalDate;
import java.util.List;

public interface RailwayScheduleService {

    /**
     * 按日生成运行实例与区间库存
     *
     * @return 新生成的车次数量
     */
    int generateServices(LocalDate startDate, int days);

    List<RailwayTrainVO> search(String fromStation, String toStation, LocalDate date);

    RailwayServiceDetailVO getServiceDetail(Long serviceId);

    RailwayTimetableVO getTimetable(String trainNo, LocalDate date);

    RailwayStatusVO getStatus(String trainNo);
}
