package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.HotelRoom;
import com.minecraft.mapper.HotelRoomMapper;
import com.minecraft.service.HotelRoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelRoomServiceImpl extends ServiceImpl<HotelRoomMapper, HotelRoom> implements HotelRoomService {

    @Override
    public List<HotelRoom> getRoomsByHotelId(Long hotelId) {
        LambdaQueryWrapper<HotelRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelRoom::getHotelId, hotelId)
                .eq(HotelRoom::getStatus, 1)
                .orderByAsc(HotelRoom::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void addRoom(HotelRoom room) {
        save(room);
    }

    @Override
    public void updateRoom(HotelRoom room) {
        updateById(room);
    }

    @Override
    public void deleteRoom(Long id) {
        removeById(id);
    }
}