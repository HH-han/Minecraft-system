package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.AttractionFacility;
import com.minecraft.mapper.AttractionFacilityMapper;
import com.minecraft.service.AttractionFacilityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttractionFacilityServiceImpl extends ServiceImpl<AttractionFacilityMapper, AttractionFacility> implements AttractionFacilityService {

    @Override
    public List<AttractionFacility> getFacilitiesByAttractionId(Long attractionId) {
        LambdaQueryWrapper<AttractionFacility> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionFacility::getAttractionId, attractionId)
                .eq(AttractionFacility::getStatus, 1)
                .orderByAsc(AttractionFacility::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void addFacility(AttractionFacility facility) {
        save(facility);
    }

    @Override
    public void updateFacility(AttractionFacility facility) {
        updateById(facility);
    }

    @Override
    public void deleteFacility(Long id) {
        removeById(id);
    }

    @Override
    public void batchAddFacilities(Long attractionId, List<String> facilityNames) {
        List<AttractionFacility> facilities = facilityNames.stream()
                .map(name -> {
                    AttractionFacility facility = new AttractionFacility();
                    facility.setAttractionId(attractionId);
                    facility.setFacilityName(name);
                    facility.setStatus(1);
                    return facility;
                })
                .toList();
        saveBatch(facilities);
    }
}