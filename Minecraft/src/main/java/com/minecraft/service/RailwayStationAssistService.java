package com.minecraft.service;

import com.minecraft.dto.request.RailwayStationServiceApplyRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.RailwayServiceRequest;
import com.minecraft.entity.RailwayStationServiceItem;

import java.util.List;

/**
 * 站车服务预约
 */
public interface RailwayStationAssistService {

    List<RailwayStationServiceItem> listServices();

    RailwayServiceRequest apply(Long userId, RailwayStationServiceApplyRequest request);

    PageResponse<RailwayServiceRequest> listRequests(Long userId, Integer pageNum, Integer pageSize);

    void cancel(Long userId, Long id);
}
