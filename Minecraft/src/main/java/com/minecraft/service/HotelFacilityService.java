package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.HotelFacility;

import java.util.List;

public interface HotelFacilityService extends IService<HotelFacility> {
    List<HotelFacility> getFacilitiesByHotelId(Long hotelId);
    void addFacility(HotelFacility facility);
    void updateFacility(HotelFacility facility);
    void deleteFacility(Long id);
    void batchAddFacilities(Long hotelId, List<String> facilityNames);
}