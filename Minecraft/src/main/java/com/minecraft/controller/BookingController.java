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

import java.util.List;

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

    @Operation(summary = "按联系电话查询门票预订列表")
    @GetMapping("/ticket/list")
    public ApiResponse<List<TicketBookingVO>> getTicketBookingsByPhone(@RequestParam String phone) {
        return ApiResponse.success(bookingService.getTicketBookingsByPhone(phone));
    }

    @Operation(summary = "取消门票预订")
    @PutMapping("/ticket/{id}/cancel")
    public ApiResponse<Void> cancelTicketBooking(@PathVariable Long id) {
        bookingService.cancelTicketBooking(id);
        return ApiResponse.success("取消成功", null);
    }

    @Operation(summary = "完成门票预订（支付成功后置为已完成）")
    @PutMapping("/ticket/{id}/complete")
    public ApiResponse<Void> completeTicketBooking(@PathVariable Long id) {
        bookingService.completeTicketBooking(id);
        return ApiResponse.success("操作成功", null);
    }

    @Operation(summary = "删除门票预订")
    @DeleteMapping("/ticket/{id}")
    public ApiResponse<Void> deleteTicketBooking(@PathVariable Long id) {
        bookingService.deleteTicketBooking(id);
        return ApiResponse.success("删除成功", null);
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

    @Operation(summary = "按联系电话查询酒店预订列表")
    @GetMapping("/hotel/list")
    public ApiResponse<List<HotelBookingVO>> getHotelBookingsByPhone(@RequestParam String phone) {
        return ApiResponse.success(bookingService.getHotelBookingsByPhone(phone));
    }

    @Operation(summary = "取消酒店预订")
    @PutMapping("/hotel/{id}/cancel")
    public ApiResponse<Void> cancelHotelBooking(@PathVariable Long id) {
        bookingService.cancelHotelBooking(id);
        return ApiResponse.success("取消成功", null);
    }

    @Operation(summary = "完成酒店预订（支付成功后置为已完成）")
    @PutMapping("/hotel/{id}/complete")
    public ApiResponse<Void> completeHotelBooking(@PathVariable Long id) {
        bookingService.completeHotelBooking(id);
        return ApiResponse.success("操作成功", null);
    }

    @Operation(summary = "删除酒店预订")
    @DeleteMapping("/hotel/{id}")
    public ApiResponse<Void> deleteHotelBooking(@PathVariable Long id) {
        bookingService.deleteHotelBooking(id);
        return ApiResponse.success("删除成功", null);
    }
}
