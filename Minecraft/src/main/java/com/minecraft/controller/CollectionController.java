package com.minecraft.controller;

import com.minecraft.dto.request.CollectionRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.service.CollectionService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "收藏管理")
@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Operation(summary ="收藏")
    @PostMapping("/add")
    public ApiResponse<Void> collect(@RequestBody CollectionRequest collectionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        collectionService.collect(collectionRequest, userId);
        return ApiResponse.success("收藏成功", null);
    }

    @Operation(summary ="取消收藏")
    @PostMapping("/cancel")
    public ApiResponse<Void> uncollect(@RequestBody CollectionRequest collectionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        collectionService.uncollect(collectionRequest, userId);
        return ApiResponse.success("取消收藏成功", null);
    }

    @Operation(summary ="检查是否已收藏")
    @GetMapping("/check")
    public ApiResponse<Boolean> isCollected(
            @RequestParam String itemType,
            @RequestParam Long itemId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(collectionService.isCollected(itemType, itemId, userId));
    }
}
