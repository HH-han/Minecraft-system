package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.service.AttractionService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.AttractionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "景点管理")
@RestController
@RequestMapping("/api/attraction")
public class AttractionController {

    @Autowired
    private AttractionService attractionService;

    @Operation(summary ="获取景点列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<AttractionVO>> getAttractionList(PageRequest request) {
        return ApiResponse.success(attractionService.getAttractionList(request));
    }

    @Operation(summary ="获取景点详情")
    @GetMapping("/{id}")
    public ApiResponse<AttractionVO> getAttractionDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(attractionService.getAttractionDetail(id, userId));
    }

    @Operation(summary ="获取推荐景点")
    @GetMapping("/recommend")
    public ApiResponse<List<AttractionVO>> getRecommendAttractions(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String season,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(attractionService.getRecommendAttractions(city, season, limit));
    }

    @Operation(summary ="获取热门景点")
    @GetMapping("/hot")
    public ApiResponse<List<AttractionVO>> getHotAttractions(@RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(attractionService.getHotAttractions(limit));
    }
}
