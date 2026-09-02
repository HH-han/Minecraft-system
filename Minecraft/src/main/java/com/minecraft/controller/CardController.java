package com.minecraft.controller;

import com.minecraft.dto.request.CardRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Card;
import com.minecraft.service.CardService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.CardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 旅行卡片管理
 */
@Tag(name = "旅行卡片管理")
@RestController
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @Operation(summary = "获取卡片列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<CardVO>> getCardList(
            PageRequest request,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String tag) {
        return ApiResponse.success(cardService.getCardList(request, location, tag));
    }

    @Operation(summary = "获取我的卡片列表")
    @GetMapping("/my")
    public ApiResponse<PageResponse<CardVO>> getMyCards(PageRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        return ApiResponse.success(cardService.getMyCards(userId, request));
    }

    @Operation(summary = "获取卡片详情")
    @GetMapping("/{id}")
    public ApiResponse<CardVO> getCardDetail(@PathVariable Integer id) {
        return ApiResponse.success(cardService.getCardDetail(id));
    }

    @Operation(summary = "创建卡片")
    @PostMapping("/add")
    public ApiResponse<Card> addCard(@Valid @RequestBody CardRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        Card card = cardService.createCard(request, userId);
        return ApiResponse.success("发布成功", card);
    }

    @Operation(summary = "更新卡片")
    @PutMapping("/update")
    public ApiResponse<Void> updateCard(@Valid @RequestBody CardRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        cardService.updateCard(request, userId);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除卡片")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCard(@PathVariable Integer id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        cardService.deleteCard(id, userId);
        return ApiResponse.success("删除成功", null);
    }
}
