package com.minecraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Cities;
import com.minecraft.service.CitiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "城市管理")
@RestController
@RequestMapping("/api/cities")
public class CitiesController {

    @Autowired
    private CitiesService citiesService;

    @Operation(summary = "获取城市列表")
    @GetMapping("/list")
    public ApiResponse<IPage<Cities>> getCitiesList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        IPage<Cities> citiesPage = citiesService.page(new Page<>(page, size));
        return ApiResponse.success(citiesPage);
    }

    @Operation(summary = "获取城市详情")
    @GetMapping("/{id}")
    public ApiResponse<Cities> getCitiesById(@PathVariable Integer id) {
        return ApiResponse.success(citiesService.getCityById(id));
    }

    @Operation(summary = "添加城市")
    @PostMapping("/add")
    public ApiResponse<Void> addCities(@RequestBody Cities cities) {
        citiesService.addCity(cities);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "更新城市")
    @PutMapping("/update")
    public ApiResponse<Void> updateCities(@RequestBody Cities cities) {
        citiesService.updateCity(cities);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除城市")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCities(@PathVariable Integer id) {
        citiesService.deleteCity(id);
        return ApiResponse.success("删除成功", null);
    }
}
