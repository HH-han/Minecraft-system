package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Hotel;
import com.minecraft.vo.AttractionVO;
import com.minecraft.vo.HotelDetailVO;
import com.minecraft.vo.HotelListVO;

import java.util.List;

public interface HotelService extends IService<Hotel> {
    PageResponse<HotelListVO> getHotelList(PageRequest request);
    HotelDetailVO getHotelDetail(Long id);
    List<Hotel> getRecommendHotels(String city, Integer limit);
    void addHotel(Hotel hotel);
    void updateHotel(Hotel hotel);
    void deleteHotel(Long id);
}
