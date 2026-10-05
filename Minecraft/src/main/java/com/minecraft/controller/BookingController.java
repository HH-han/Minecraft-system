package com.minecraft.controller;

import com.minecraft.dto.request.HotelBookingRequest;
import com.minecraft.dto.request.TicketBookingRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.service.BookingService;
import com.minecraft.vo.HotelBookingVO;
import com.minecraft.vo.TicketBookingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预订管理")
@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "创建景点门票预订")
    @PostMapping("/ticket")
    public ApiResponse<Long> createTicketBooking(@Valid @RequestBody TicketBookingRequest request) {
        return ApiResponse.success("预订成功", bookingService.createTicketBooking(request));
    }

    @Operation(summary = "查询景点门票预订详情")
    @GetMapping("/ticket/{id}")
    public ApiResponse<TicketBookingVO> getTicketBooking(@PathVariable Long id) {
        return ApiResponse.success(bookingService.getTicketBooking(id));
    }

    @Operation(summary = "创建酒店预订")
    @PostMapping("/hotel")
    public ApiResponse<Long> createHotelBooking(@Valid @RequestBody HotelBookingRequest request) {
        return ApiResponse.success("预订成功", bookingService.createHotelBooking(request));
    }

    @Operation(summary = "查询酒店预订详情")
    @GetMapping("/hotel/{id}")
    public ApiResponse<HotelBookingVO> getHotelBooking(@PathVariable Long id) {
        return ApiResponse.success(bookingService.getHotelBooking(id));
    }
}
