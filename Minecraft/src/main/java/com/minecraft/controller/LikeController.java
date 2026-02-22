package com.minecraft.controller;

import com.minecraft.dto.request.LikeRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.service.LikeService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "点赞管理")
@RestController
@RequestMapping("/api/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Operation(summary ="点赞")
    @PostMapping("/add")
    public ApiResponse<Void> like(@RequestBody LikeRequest likeRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        likeService.like(likeRequest, userId);
        return ApiResponse.success("点赞成功", null);
    }

    @Operation(summary ="取消点赞")
    @PostMapping("/cancel")
    public ApiResponse<Void> unlike(@RequestBody LikeRequest likeRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        likeService.unlike(likeRequest, userId);
        return ApiResponse.success("取消点赞成功", null);
    }

    @Operation(summary ="检查是否已点赞")
    @GetMapping("/check")
    public ApiResponse<Boolean> isLiked(
            @RequestParam String itemType,
            @RequestParam Long itemId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(likeService.isLiked(itemType, itemId, userId));
    }
}
