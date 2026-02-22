package com.minecraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Countries;
import com.minecraft.service.CountriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "国家管理")
@RestController
@RequestMapping("/api/countries")
public class CountriesController {

    @Autowired
    private CountriesService countriesService;

    @Operation(summary = "获取国家列表")
    @GetMapping("/list")
    public ApiResponse<IPage<Countries>> getCountriesList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        IPage<Countries> countriesPage = countriesService.page(new Page<>(page, size));
        return ApiResponse.success(countriesPage);
    }

    @Operation(summary = "获取国家详情")
    @GetMapping("/{id}")
    public ApiResponse<Countries> getCountriesById(@PathVariable Integer id) {
        return ApiResponse.success(countriesService.getCountryById(id));
    }

    @Operation(summary = "添加国家")
    @PostMapping("/add")
    public ApiResponse<Void> addCountries(@RequestBody Countries countries) {
        countriesService.addCountry(countries);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "更新国家")
    @PutMapping("/update")
    public ApiResponse<Void> updateCountries(@RequestBody Countries countries) {
        countriesService.updateCountry(countries);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除国家")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCountries(@PathVariable Integer id) {
        countriesService.deleteCountry(id);
        return ApiResponse.success("删除成功", null);
    }
}
