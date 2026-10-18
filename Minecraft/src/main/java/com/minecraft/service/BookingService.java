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
     * 取消门票预订（状态置为已取消）
     */
    void cancelTicketBooking(Long id);

    /**
     * 删除门票预订（主表 + 明细）
     */
    void deleteTicketBooking(Long id);

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

    /**
     * 取消酒店预订（状态置为已取消）
     */
    void cancelHotelBooking(Long id);

    /**
     * 删除酒店预订（主表 + 明细）
     */
    void deleteHotelBooking(Long id);
}
