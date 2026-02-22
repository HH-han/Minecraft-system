package com.minecraft.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.WorldCharacteristics;
import com.minecraft.service.WorldCharacteristicsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "世界特征管理")
@RestController
@RequestMapping("/api/world-characteristics")
public class WorldCharacteristicsController {

    @Autowired
    private WorldCharacteristicsService worldCharacteristicsService;

    @Operation(summary ="获取世界特征列表")
    @GetMapping("/list")
    public ApiResponse<List<WorldCharacteristics>> getList() {
        return ApiResponse.success(worldCharacteristicsService.list());
    }

    @Operation(summary ="分页获取世界特征列表")
    @GetMapping("/page")
    public ApiResponse<Page<WorldCharacteristics>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<WorldCharacteristics> page = new Page<>(current, size);
        return ApiResponse.success(worldCharacteristicsService.page(page));
    }

    @Operation(summary ="根据ID获取世界特征详情")
    @GetMapping("/detail/{id}")
    public ApiResponse<WorldCharacteristics> getDetail(@PathVariable Integer id) {
        return ApiResponse.success(worldCharacteristicsService.getById(id));
    }

    @Operation(summary ="根据国家获取世界特征")
    @GetMapping("/by-country/{country}")
    public ApiResponse<List<WorldCharacteristics>> getByCountry(@PathVariable String country) {
        List<WorldCharacteristics> list = worldCharacteristicsService.lambdaQuery()
                .eq(WorldCharacteristics::getCountry, country)
                .list();
        return ApiResponse.success(list);
    }

    @Operation(summary ="根据分类获取世界特征")
    @GetMapping("/by-category/{category}")
    public ApiResponse<List<WorldCharacteristics>> getByCategory(@PathVariable String category) {
        List<WorldCharacteristics> list = worldCharacteristicsService.lambdaQuery()
                .eq(WorldCharacteristics::getCategory, category)
                .list();
        return ApiResponse.success(list);
    }

    @Operation(summary ="添加世界特征")
    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestBody WorldCharacteristics worldCharacteristics) {
        worldCharacteristicsService.save(worldCharacteristics);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary ="更新世界特征")
    @PutMapping("/update")
    public ApiResponse<Void> update(@RequestBody WorldCharacteristics worldCharacteristics) {
        worldCharacteristicsService.updateById(worldCharacteristics);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除世界特征")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        worldCharacteristicsService.removeById(id);
        return ApiResponse.success("删除成功", null);
    }
}
