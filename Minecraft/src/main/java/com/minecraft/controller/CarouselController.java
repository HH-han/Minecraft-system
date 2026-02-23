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
        List<Carousels> carousels = carouselsService.getCarouselsByGroupCode("home_top");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取首页中部轮播图")
    @GetMapping("/home/middle")
    public ApiResponse<List<Carousels>> getHomeMiddleCarousels() {
        List<Carousels> carousels = carouselsService.getCarouselsByGroupCode("home_middle");
        return ApiResponse.success(carousels);
    }

    @Operation(summary = "获取所有轮播图")
    @GetMapping
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
    public ApiResponse<Void> addCarousel(@RequestBody Carousels carousel) {
        carouselsService.save(carousel);
        return ApiResponse.success("新增轮播图成功");
    }

    @Operation(summary = "更新轮播图")
    @PutMapping
    public ApiResponse<Void> updateCarousel(@RequestBody Carousels carousel) {
        carouselsService.updateById(carousel);
        return ApiResponse.success("更新轮播图成功");
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCarousel(@PathVariable Integer id) {
        carouselsService.removeById(id);
        return ApiResponse.success("删除轮播图成功");
    }
}
