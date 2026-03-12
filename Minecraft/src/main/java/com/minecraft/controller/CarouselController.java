package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Carousels;
import com.minecraft.service.CarouselsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
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
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("home_top");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取首页中部轮播图")
    @GetMapping("/home/middle")
    public ApiResponse<List<Carousels>> getHomeMiddleCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("home_middle");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取目的地轮播图")
    @GetMapping("/destination")
    public ApiResponse<List<Carousels>> getDestinationCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("destination");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取景点轮播图")
    @GetMapping("/attraction")
    public ApiResponse<List<Carousels>> getAttractionCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("attraction");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取酒店轮播图")
    @GetMapping("/hotel")
    public ApiResponse<List<Carousels>> getHotelCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("hotel");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取美食轮播图")
    @GetMapping("/food")
    public ApiResponse<List<Carousels>> getFoodCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("food");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取纪念品轮播图")
    @GetMapping("/souvenir")
    public ApiResponse<List<Carousels>> getSouvenirCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("souvenir");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取攻略群轮播图")
    @GetMapping("/strategy")
    public ApiResponse<List<Carousels>> getStrategyCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("strategy");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取社区轮播图")
    @GetMapping("/community")
    public ApiResponse<List<Carousels>> getCommunityCarousels() {
        List<Carousels> carousels = carouselsService.getActiveCarouselsByPosition("community");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取所有轮播图")
    @GetMapping("/categories")
    public ApiResponse<List<Carousels>> getAllCarousels() {
        List<Carousels> carousels = carouselsService.list();
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取轮播图详情")
    @GetMapping("/{id}")
    public ApiResponse<Carousels> getCarouselById(@PathVariable Integer id) {
        Carousels carousel = carouselsService.getById(id);
        return ApiResponse.success(carousel);
    }

    @Operation(summary = "新增轮播图")
    @PostMapping
    public Object addCarousel(@RequestBody Carousels carousel) {
        carouselsService.save(carousel);
        return ApiResponse.success("新增轮播图成功");
    }

    @Operation(summary = "更新轮播图")
    @PutMapping
    public Object updateCarousel(@RequestBody Carousels carousel) {
        carouselsService.updateById(carousel);
        return ApiResponse.success("更新轮播图成功");
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Object deleteCarousel(@PathVariable Integer id) {
        carouselsService.removeById(id);
        return ApiResponse.success("删除轮播图成功");
    }
}
