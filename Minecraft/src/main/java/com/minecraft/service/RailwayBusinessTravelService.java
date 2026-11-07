package com.minecraft.service;

import com.minecraft.dto.request.RailwayBusinessAccountRequest;
import com.minecraft.dto.request.RailwayBusinessTripRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayBusinessSummaryVO;
import com.minecraft.entity.RailwayBusinessAccount;
import com.minecraft.entity.RailwayBusinessTrip;

/**
 * 商旅服务
 */
public interface RailwayBusinessTravelService {

    RailwayBusinessAccount getAccount(Long userId);

    RailwayBusinessAccount saveAccount(Long userId, RailwayBusinessAccountRequest request);

    RailwayBusinessSummaryVO summary(Long userId);

    RailwayBusinessTrip createTrip(Long userId, RailwayBusinessTripRequest request);

    PageResponse<RailwayBusinessTrip> listTrips(Long userId, Integer pageNum, Integer pageSize);

    void cancelTrip(Long userId, Long id);
}
