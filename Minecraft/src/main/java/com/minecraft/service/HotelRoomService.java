package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.HotelRoom;

import java.util.List;

public interface HotelRoomService extends IService<HotelRoom> {
    List<HotelRoom> getRoomsByHotelId(Long hotelId);
    void addRoom(HotelRoom room);
    void updateRoom(HotelRoom room);
    void deleteRoom(Long id);
}