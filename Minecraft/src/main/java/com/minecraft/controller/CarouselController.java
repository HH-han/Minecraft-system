package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Carousels;
import com.minecraft.service.CarouselsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/carousel")
@Tag(name = "轮播图管理", description = "轮播图相关接口")
public class CarouselController {
    @Resource
    private CarouselsService carouselsService;

    @Operation(summary = "获取指定位置的轮播图")
    @GetMapping("/position")
    public ApiResponse<List<Carousels>> getCarouselsByPosition(@RequestParam String position) {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition(position);
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取指定分组的轮播图")
    @GetMapping("/group")
    public ApiResponse<List<Carousels>> getCarouselsByGroup(@RequestParam String groupCode) {
        List<Carousels> carousels = carouselsService.getCarouselsByGroupCode(groupCode);
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取首页顶部轮播图")
    @GetMapping("/home/top")
    public ApiResponse<List<Carousels>> getHomeTopCarousels() {
        List<Carousels> carousels = carouselsService.getCarouselsByGroupCode("home_top");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取首页中部轮播图")
    @GetMapping("/home/middle")
    public ApiResponse<List<Carousels>> getHomeMiddleCarousels() {
        List<Carousels> carousels = carouselsService.getCarouselsByGroupCode("home_middle");
        return ApiResponse.success(carousels);
    }
}
