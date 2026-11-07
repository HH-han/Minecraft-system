package com.minecraft.service;

import com.minecraft.dto.request.RailwayGroupBookingRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayGroupQuoteVO;
import com.minecraft.entity.RailwayGroupBooking;

import java.time.LocalDate;

/**
 * 团体购票服务
 */
public interface RailwayGroupBookingService {

    /**
     * 团体票价预估（根据车次经停模板匹配最低票价并应用团体折扣）
     */
    RailwayGroupQuoteVO quote(String fromStation, String toStation, LocalDate travelDate,
                              Integer passengerCount, String seatType, String trainNo);

    RailwayGroupBooking create(Long userId, RailwayGroupBookingRequest request);

    PageResponse<RailwayGroupBooking> list(Long userId, Integer pageNum, Integer pageSize);

    void cancel(Long userId, Long id);
}
