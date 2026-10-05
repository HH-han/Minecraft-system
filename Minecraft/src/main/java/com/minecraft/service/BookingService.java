package com.minecraft.service;

import com.minecraft.dto.request.HotelBookingRequest;
import com.minecraft.dto.request.TicketBookingRequest;
import com.minecraft.vo.HotelBookingVO;
import com.minecraft.vo.TicketBookingVO;

import java.util.List;

/**
 * 预订服务（景点门票预订 / 酒店预订）
 */
public interface BookingService {

    /**
     * 创建景点门票预订，返回预订ID
     */
    Long createTicketBooking(TicketBookingRequest request);

    /**
     * 查询景点门票预订详情（含明细项）
     */
    TicketBookingVO getTicketBooking(Long id);

    /**
     * 按联系电话查询门票预订列表（创建时间倒序）
     */
    List<TicketBookingVO> getTicketBookingsByPhone(String phone);

    /**
     * 创建酒店预订，返回预订ID
     */
    Long createHotelBooking(HotelBookingRequest request);

    /**
     * 查询酒店预订详情
     */
    HotelBookingVO getHotelBooking(Long id);

    /**
     * 按联系电话查询酒店预订列表（创建时间倒序）
     */
    List<HotelBookingVO> getHotelBookingsByPhone(String phone);
}
