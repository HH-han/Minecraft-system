package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.HotelRoom;
import com.minecraft.service.HotelRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "酒店房型管理")
@RestController
@RequestMapping("/api/hotelRoom")
public class HotelRoomController {

    @Autowired
    private HotelRoomService hotelRoomService;

    @Operation(summary = "获取酒店房型列表")
    @GetMapping("/list")
    public ApiResponse<List<HotelRoom>> getRoomsByHotelId(@RequestParam Long hotelId) {
        return ApiResponse.success(hotelRoomService.getRoomsByHotelId(hotelId));
    }

    @Operation(summary = "添加酒店房型")
    @PostMapping("/add")
    public ApiResponse<Long> addRoom(@RequestBody HotelRoom room) {
        room.setStatus(1);
        hotelRoomService.addRoom(room);
        return ApiResponse.success("添加成功", room.getId());
    }

    @Operation(summary = "更新酒店房型")
    @PutMapping("/update")
    public ApiResponse<Void> updateRoom(@RequestBody HotelRoom room) {
        hotelRoomService.updateRoom(room);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除酒店房型")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRoom(@PathVariable Long id) {
        hotelRoomService.deleteRoom(id);
        return ApiResponse.success("删除成功", null);
    }
}