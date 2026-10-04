package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.service.RecommendationQueryService;
import com.minecraft.recommendation.vo.RecommendationResultVO;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能推荐公共接口（匿名可访问；携带登录态时返回个性化结果）。
 */
@Tag(name = "智能推荐")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class PublicRecommendationController {

    private final RecommendationQueryService recommendationQueryService;

    @Operation(summary = "获取分类推荐列表（匿名热门 / 登录个性化）")
    @GetMapping("/{category}")
    public ApiResponse<RecommendationResultVO> list(
            @Parameter(description = "分类：attraction/hotel/food/product")
            @PathVariable String category,
            @Parameter(description = "返回条数，1-100，默认 10")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "城市过滤（精确匹配）")
            @RequestParam(required = false) String city,
            @Parameter(description = "季节偏好：spring/summer/autumn/winter")
            @RequestParam(required = false) String season) {
        RecommendCategory cat = RecommendCategory.fromCode(category);
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(recommendationQueryService.list(cat, userId, limit, city, season));
    }

    @Operation(summary = "获取相关推荐（物品协同过滤 + 标签兜底）")
    @GetMapping("/{category}/{itemId}/related")
    public ApiResponse<RecommendationResultVO> related(
            @Parameter(description = "分类：attraction/hotel/food/product")
            @PathVariable String category,
            @Parameter(description = "物品 ID")
            @PathVariable Long itemId,
            @Parameter(description = "返回条数，1-100，默认 10")
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        RecommendCategory cat = RecommendCategory.fromCode(category);
        return ApiResponse.success(recommendationQueryService.related(cat, itemId, limit));
    }
}
