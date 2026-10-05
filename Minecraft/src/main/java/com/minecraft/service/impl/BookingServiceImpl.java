package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.dto.request.HotelBookingRequest;
import com.minecraft.dto.request.TicketBookingRequest;
import com.minecraft.entity.AttractionTicket;
import com.minecraft.entity.HotelBooking;
import com.minecraft.entity.HotelRoom;
import com.minecraft.entity.TicketBooking;
import com.minecraft.entity.TicketBookingItem;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AttractionTicketMapper;
import com.minecraft.mapper.HotelBookingMapper;
import com.minecraft.mapper.HotelRoomMapper;
import com.minecraft.mapper.TicketBookingItemMapper;
import com.minecraft.mapper.TicketBookingMapper;
import com.minecraft.service.BookingService;
import com.minecraft.vo.HotelBookingVO;
import com.minecraft.vo.TicketBookingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private TicketBookingMapper ticketBookingMapper;

    @Autowired
    private TicketBookingItemMapper ticketBookingItemMapper;

    @Autowired
    private HotelBookingMapper hotelBookingMapper;

    @Autowired
    private AttractionTicketMapper attractionTicketMapper;

    @Autowired
    private HotelRoomMapper hotelRoomMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTicketBooking(TicketBookingRequest request) {
        if (request.getVisitDate().isBefore(LocalDate.now())) {
            throw new BusinessException("游玩日期不能早于今天");
        }

        // 从数据库快照门票信息（名称/单价/说明），不信任前端传参
        TicketBooking booking = new TicketBooking();
        booking.setContactName(request.getContactName());
        booking.setContactPhone(request.getContactPhone());
        booking.setContactEmail(request.getContactEmail());
        booking.setSpecialRequest(request.getSpecialRequest());
        booking.setVisitDate(request.getVisitDate());
        ticketBookingMapper.insert(booking);

        for (TicketBookingRequest.TicketItem itemReq : request.getItems()) {
            AttractionTicket ticket = attractionTicketMapper.selectById(itemReq.getTicketId());
            if (ticket == null || ticket.getStatus() == null || ticket.getStatus() != 1) {
                throw new BusinessException("门票不存在或已下架: " + itemReq.getTicketId());
            }

            TicketBookingItem item = new TicketBookingItem();
            item.setBookingId(booking.getId());
            item.setTicketId(ticket.getId());
            item.setTicketName(ticket.getName());
            item.setTicketPrice(ticket.getPrice());
            item.setDescription(ticket.getDescription());
            item.setQuantity(itemReq.getQuantity());
            ticketBookingItemMapper.insert(item);
        }

        return booking.getId();
    }

    @Override
    public TicketBookingVO getTicketBooking(Long id) {
        TicketBooking booking = ticketBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订记录不存在");
        }

        TicketBookingVO vo = new TicketBookingVO();
        vo.setId(booking.getId());
        vo.setContactName(booking.getContactName());
        vo.setContactPhone(booking.getContactPhone());
        vo.setContactEmail(booking.getContactEmail());
        vo.setSpecialRequest(booking.getSpecialRequest());
        vo.setVisitDate(booking.getVisitDate());
        vo.setCreatedAt(booking.getCreatedAt());

        LambdaQueryWrapper<TicketBookingItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketBookingItem::getBookingId, id);
        List<TicketBookingItem> items = ticketBookingItemMapper.selectList(wrapper);

        vo.setItems(items.stream().map(item -> {
            TicketBookingVO.TicketBookingItemVO itemVO = new TicketBookingVO.TicketBookingItemVO();
            itemVO.setId(item.getId());
            itemVO.setTicketId(item.getTicketId());
            itemVO.setTicketName(item.getTicketName());
            itemVO.setTicketPrice(item.getTicketPrice());
            itemVO.setDescription(item.getDescription());
            itemVO.setQuantity(item.getQuantity());
            return itemVO;
        }).toList());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHotelBooking(HotelBookingRequest request) {
        LocalDate today = LocalDate.now();
        if (request.getCheckInDate().isBefore(today)) {
            throw new BusinessException("入住日期不能早于今天");
        }
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BusinessException("退房日期必须晚于入住日期");
        }

        // 从数据库快照房间信息（名称/单价/描述/设施），不信任前端传参
        HotelRoom room = hotelRoomMapper.selectById(request.getRoomId());
        if (room == null || room.getStatus() == null || room.getStatus() != 1) {
            throw new BusinessException("房间不存在或已下架");
        }

        HotelBooking booking = new HotelBooking();
        booking.setContactName(request.getContactName());
        booking.setContactPhone(request.getContactPhone());
        booking.setContactEmail(request.getContactEmail());
        booking.setSpecialRequest(request.getSpecialRequest());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setGuests(request.getGuests());
        booking.setRoomId(room.getId());
        booking.setRoomName(room.getName());
        booking.setRoomPrice(room.getPrice());
        booking.setRoomDesc(room.getDescription());
        booking.setRoomFacilities(room.getFacilities());
        hotelBookingMapper.insert(booking);

        return booking.getId();
    }

    @Override
    public HotelBookingVO getHotelBooking(Long id) {
        HotelBooking booking = hotelBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订记录不存在");
        }

        HotelBookingVO vo = new HotelBookingVO();
        vo.setId(booking.getId());
        vo.setContactName(booking.getContactName());
        vo.setContactPhone(booking.getContactPhone());
        vo.setContactEmail(booking.getContactEmail());
        vo.setSpecialRequest(booking.getSpecialRequest());
        vo.setCheckInDate(booking.getCheckInDate());
        vo.setCheckOutDate(booking.getCheckOutDate());
        vo.setGuests(booking.getGuests());
        vo.setRoomId(booking.getRoomId());
        vo.setRoomName(booking.getRoomName());
        vo.setRoomPrice(booking.getRoomPrice());
        vo.setRoomDesc(booking.getRoomDesc());
        vo.setRoomFacilities(booking.getRoomFacilities());
        vo.setCreatedAt(booking.getCreatedAt());
        return vo;
    }
}
