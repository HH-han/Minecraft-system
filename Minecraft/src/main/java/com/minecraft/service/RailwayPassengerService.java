package com.minecraft.service;

import com.minecraft.dto.request.RailwayPassengerRequest;
import com.minecraft.entity.RailwayPassenger;

import java.util.List;

public interface RailwayPassengerService {

    List<RailwayPassenger> listByUser(Long userId);

    RailwayPassenger addPassenger(Long userId, RailwayPassengerRequest request);

    void deletePassenger(Long userId, Long id);
}
