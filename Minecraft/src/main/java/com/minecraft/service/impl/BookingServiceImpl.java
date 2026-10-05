package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.dto.request.HotelBookingRequest;
import com.minecraft.dto.request.TicketBookingRequest;
import com.minecraft.entity.Attraction;
import com.minecraft.entity.AttractionTicket;
import com.minecraft.entity.Hotel;
import com.minecraft.entity.HotelBooking;
import com.minecraft.entity.HotelBookingItem;
import com.minecraft.entity.HotelRoom;
import com.minecraft.entity.TicketBooking;
import com.minecraft.entity.TicketBookingItem;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AttractionMapper;
import com.minecraft.mapper.AttractionTicketMapper;
import com.minecraft.mapper.HotelBookingItemMapper;
import com.minecraft.mapper.HotelBookingMapper;
import com.minecraft.mapper.HotelMapper;
import com.minecraft.mapper.HotelRoomMapper;
import com.minecraft.mapper.TicketBookingItemMapper;
import com.minecraft.mapper.TicketBookingMapper;
import com.minecraft.service.BookingService;
import com.minecraft.vo.HotelBookingVO;
import com.minecraft.vo.TicketBookingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private TicketBookingMapper ticketBookingMapper;

    @Autowired
    private TicketBookingItemMapper ticketBookingItemMapper;

    @Autowired
    private HotelBookingMapper hotelBookingMapper;

    @Autowired
    private HotelBookingItemMapper hotelBookingItemMapper;

    @Autowired
    private AttractionTicketMapper attractionTicketMapper;

    @Autowired
    private AttractionMapper attractionMapper;

    @Autowired
    private HotelRoomMapper hotelRoomMapper;

    @Autowired
    private HotelMapper hotelMapper;

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
        return toTicketBookingVO(booking);
    }

    @Override
    public List<TicketBookingVO> getTicketBookingsByPhone(String phone) {
        LambdaQueryWrapper<TicketBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketBooking::getContactPhone, phone)
                .orderByDesc(TicketBooking::getCreatedAt);
        return ticketBookingMapper.selectList(wrapper).stream()
                .map(this::toTicketBookingVO)
                .toList();
    }

    private TicketBookingVO toTicketBookingVO(TicketBooking booking) {
        TicketBookingVO vo = new TicketBookingVO();
        vo.setId(booking.getId());
        vo.setContactName(booking.getContactName());
        vo.setContactPhone(booking.getContactPhone());
        vo.setContactEmail(booking.getContactEmail());
        vo.setSpecialRequest(booking.getSpecialRequest());
        vo.setVisitDate(booking.getVisitDate());
        vo.setCreatedAt(booking.getCreatedAt());

        LambdaQueryWrapper<TicketBookingItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketBookingItem::getBookingId, booking.getId());
        List<TicketBookingItem> items = ticketBookingItemMapper.selectList(wrapper);

        // 批量反查景点信息：ticketId -> attraction_ticket.attractionId -> attraction.name
        Map<Long, Long> ticketAttractionMap = new HashMap<>();
        Map<Long, String> attractionNameMap = new HashMap<>();
        List<Long> ticketIds = items.stream()
                .map(TicketBookingItem::getTicketId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (!ticketIds.isEmpty()) {
            List<AttractionTicket> tickets = attractionTicketMapper.selectBatchIds(ticketIds);
            for (AttractionTicket ticket : tickets) {
                ticketAttractionMap.put(ticket.getId(), ticket.getAttractionId());
            }
            Set<Long> attractionIds = tickets.stream()
                    .map(AttractionTicket::getAttractionId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!attractionIds.isEmpty()) {
                for (Attraction attraction : attractionMapper.selectBatchIds(attractionIds)) {
                    attractionNameMap.put(attraction.getId(), attraction.getName());
                }
            }
        }

        vo.setItems(items.stream().map(item -> {
            TicketBookingVO.TicketBookingItemVO itemVO = new TicketBookingVO.TicketBookingItemVO();
            itemVO.setId(item.getId());
            itemVO.setTicketId(item.getTicketId());
            itemVO.setTicketName(item.getTicketName());
            itemVO.setTicketPrice(item.getTicketPrice());
            itemVO.setDescription(item.getDescription());
            itemVO.setQuantity(item.getQuantity());
            Long attractionId = ticketAttractionMap.get(item.getTicketId());
            itemVO.setAttractionId(attractionId);
            itemVO.setAttractionName(attractionId != null ? attractionNameMap.get(attractionId) : null);
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
        // 房间所属酒店必须存在（hotel_booking_item.hotel_id 外键约束）
        Hotel hotel = hotelMapper.selectById(room.getHotelId());
        if (hotel == null) {
            throw new BusinessException("房间所属酒店不存在");
        }

        int nights = (int) ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        int quantity = request.getQuantity() != null ? request.getQuantity() : 1;
        BigDecimal subtotal = room.getPrice()
                .multiply(BigDecimal.valueOf(quantity))
                .multiply(BigDecimal.valueOf(nights));

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

        // 写入明细表：酒店信息快照 + 房间信息快照 + 预订信息
        HotelBookingItem item = new HotelBookingItem();
        item.setBookingId(booking.getId());
        item.setHotelId(hotel.getId());
        item.setHotelName(hotel.getName());
        item.setProvince(hotel.getProvince());
        item.setCity(hotel.getCity());
        item.setAddress(hotel.getAddress());
        item.setCoverImage(hotel.getCoverImage());
        item.setHotelFacilities(hotel.getFacilities());
        item.setRating(hotel.getRating());
        item.setStarLevel(hotel.getStarLevel());
        item.setRoomId(room.getId());
        item.setRoomName(room.getName());
        item.setRoomPrice(room.getPrice());
        item.setRoomDesc(room.getDescription());
        item.setRoomFacilities(room.getFacilities());
        item.setQuantity(quantity);
        item.setNights(nights);
        item.setSubtotal(subtotal);
        hotelBookingItemMapper.insert(item);

        return booking.getId();
    }

    @Override
    public HotelBookingVO getHotelBooking(Long id) {
        HotelBooking booking = hotelBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订记录不存在");
        }
        return toHotelBookingVO(booking);
    }

    @Override
    public List<HotelBookingVO> getHotelBookingsByPhone(String phone) {
        LambdaQueryWrapper<HotelBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelBooking::getContactPhone, phone)
                .orderByDesc(HotelBooking::getCreatedAt);
        return hotelBookingMapper.selectList(wrapper).stream()
                .map(this::toHotelBookingVO)
                .toList();
    }

    private HotelBookingVO toHotelBookingVO(HotelBooking booking) {
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

        LambdaQueryWrapper<HotelBookingItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelBookingItem::getBookingId, booking.getId());
        vo.setItems(hotelBookingItemMapper.selectList(wrapper).stream().map(item -> {
            HotelBookingVO.HotelBookingItemVO itemVO = new HotelBookingVO.HotelBookingItemVO();
            itemVO.setId(item.getId());
            itemVO.setHotelId(item.getHotelId());
            itemVO.setHotelName(item.getHotelName());
            itemVO.setProvince(item.getProvince());
            itemVO.setCity(item.getCity());
            itemVO.setAddress(item.getAddress());
            itemVO.setCoverImage(item.getCoverImage());
            itemVO.setHotelFacilities(item.getHotelFacilities());
            itemVO.setRating(item.getRating());
            itemVO.setStarLevel(item.getStarLevel());
            itemVO.setRoomId(item.getRoomId());
            itemVO.setRoomName(item.getRoomName());
            itemVO.setRoomPrice(item.getRoomPrice());
            itemVO.setRoomDesc(item.getRoomDesc());
            itemVO.setRoomFacilities(item.getRoomFacilities());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setNights(item.getNights());
            itemVO.setSubtotal(item.getSubtotal());
            return itemVO;
        }).toList());

        return vo;
    }
}
