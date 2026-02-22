package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Hotel;
import com.minecraft.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "酒店管理")
@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Operation(summary ="获取酒店列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getHotelList(PageRequest request) {
        return ApiResponse.success(hotelService.getHotelList(request));
    }

    @Operation(summary ="获取酒店详情")
    @GetMapping("/{id}")
    public ApiResponse<Hotel> getHotelDetail(@PathVariable Long id) {
        return ApiResponse.success(hotelService.getHotelDetail(id));
    }

    @Operation(summary ="获取推荐酒店")
    @GetMapping("/recommend")
    public ApiResponse<List<Hotel>> getRecommendHotels(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(hotelService.getRecommendHotels(city, limit));
    }

    @Operation(summary ="添加酒店")
    @PostMapping("/add")
    public ApiResponse<Void> addHotel(@RequestBody Hotel hotel) {
        hotelService.addHotel(hotel);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary ="更新酒店")
    @PutMapping("/update")
    public ApiResponse<Void> updateHotel(@RequestBody Hotel hotel) {
        hotelService.updateHotel(hotel);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除酒店")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ApiResponse.success("删除成功", null);
    }
}
