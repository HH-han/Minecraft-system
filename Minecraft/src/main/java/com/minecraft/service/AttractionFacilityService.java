package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.AttractionFacility;

import java.util.List;

public interface AttractionFacilityService extends IService<AttractionFacility> {
    List<AttractionFacility> getFacilitiesByAttractionId(Long attractionId);
    void addFacility(AttractionFacility facility);
    void updateFacility(AttractionFacility facility);
    void deleteFacility(Long id);
    void batchAddFacilities(Long attractionId, List<String> facilityNames);
}