package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.HotelFacility;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotelFacilityMapper extends BaseMapper<HotelFacility> {
    List<HotelFacility> selectByHotelId(Long hotelId);
}