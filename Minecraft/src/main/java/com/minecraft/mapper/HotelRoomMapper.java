package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.HotelRoom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotelRoomMapper extends BaseMapper<HotelRoom> {
    List<HotelRoom> selectByHotelId(Long hotelId);
}