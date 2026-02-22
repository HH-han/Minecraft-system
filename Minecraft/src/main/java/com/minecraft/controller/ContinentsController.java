package com.minecraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Continents;
import com.minecraft.service.ContinentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "大洲管理")
@RestController
@RequestMapping("/api/continents")
public class ContinentsController {

    @Autowired
    private ContinentsService continentsService;

    @Operation(summary = "获取大洲列表")
    @GetMapping("/list")
    public ApiResponse<IPage<Continents>> getContinentsList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        IPage<Continents> continentsPage = continentsService.page(new Page<>(page, size));
        return ApiResponse.success(continentsPage);
    }

    @Operation(summary = "获取大洲详情")
    @GetMapping("/{id}")
    public ApiResponse<Continents> getContinentsById(@PathVariable Integer id) {
        return ApiResponse.success(continentsService.getContinentById(id));
    }

    @Operation(summary = "添加大洲")
    @PostMapping("/add")
    public ApiResponse<Void> addContinents(@RequestBody Continents continents) {
        continentsService.addContinent(continents);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "更新大洲")
    @PutMapping("/update")
    public ApiResponse<Void> updateContinents(@RequestBody Continents continents) {
        continentsService.updateContinent(continents);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除大洲")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteContinents(@PathVariable Integer id) {
        continentsService.deleteContinent(id);
        return ApiResponse.success("删除成功", null);
    }
}
