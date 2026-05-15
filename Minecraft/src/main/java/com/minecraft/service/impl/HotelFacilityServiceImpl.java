package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.HotelFacility;
import com.minecraft.mapper.HotelFacilityMapper;
import com.minecraft.service.HotelFacilityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelFacilityServiceImpl extends ServiceImpl<HotelFacilityMapper, HotelFacility> implements HotelFacilityService {

    @Override
    public List<HotelFacility> getFacilitiesByHotelId(Long hotelId) {
        LambdaQueryWrapper<HotelFacility> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelFacility::getHotelId, hotelId)
                .eq(HotelFacility::getStatus, 1)
                .orderByAsc(HotelFacility::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void addFacility(HotelFacility facility) {
        save(facility);
    }

    @Override
    public void updateFacility(HotelFacility facility) {
        updateById(facility);
    }

    @Override
    public void deleteFacility(Long id) {
        removeById(id);
    }

    @Override
    public void batchAddFacilities(Long hotelId, List<String> facilityNames) {
        List<HotelFacility> facilities = facilityNames.stream()
                .map(name -> {
                    HotelFacility facility = new HotelFacility();
                    facility.setHotelId(hotelId);
                    facility.setFacilityName(name);
                    facility.setStatus(1);
                    return facility;
                })
                .toList();
        saveBatch(facilities);
    }
}