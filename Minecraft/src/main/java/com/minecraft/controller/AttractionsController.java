package com.minecraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Attractions;
import com.minecraft.service.AttractionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "景点管理")
@RestController
@RequestMapping("/api/attractions")
public class AttractionsController {

    @Autowired
    private AttractionsService attractionsService;

    @Operation(summary = "获取景点列表")
    @GetMapping("/list")
    public ApiResponse<IPage<Attractions>> getAttractionsList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        IPage<Attractions> attractionsPage = attractionsService.page(new Page<>(page, size));
        return ApiResponse.success(attractionsPage);
    }

    @Operation(summary = "获取景点详情")
    @GetMapping("/{id}")
    public ApiResponse<Attractions> getAttractionsById(@PathVariable Integer id) {
        return ApiResponse.success(attractionsService.getAttractionById(id));
    }

    @Operation(summary = "添加景点")
    @PostMapping("/add")
    public ApiResponse<Void> addAttractions(@RequestBody Attractions attractions) {
        attractionsService.addAttraction(attractions);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "更新景点")
    @PutMapping("/update")
    public ApiResponse<Void> updateAttractions(@RequestBody Attractions attractions) {
        attractionsService.updateAttraction(attractions);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除景点")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAttractions(@PathVariable Integer id) {
        attractionsService.deleteAttraction(id);
        return ApiResponse.success("删除成功", null);
    }
}
