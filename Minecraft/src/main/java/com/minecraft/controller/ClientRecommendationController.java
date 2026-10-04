package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.service.ClientRecommendationService;
import com.minecraft.recommendation.vo.ClientRecommendationItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客户端智能推荐接口（匿名可访问）。
 * <p>
 * 与 {@link PublicRecommendationController} 的区别：本接口面向 App/H5 推荐页，
 * 严格受 recommendation_rule 人工干预规则控制（置顶/加权/隐藏），
 * 无规则时按综合分推荐，不做个性化与曝光归因。
 */
@Tag(name = "客户端智能推荐")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/recommendations")
public class ClientRecommendationController {

    private final ClientRecommendationService clientRecommendationService;

    @Operation(summary = "获取分类推荐列表（人工规则优先，无规则按综合分）")
    @GetMapping("/{category}")
    public ApiResponse<List<ClientRecommendationItemVO>> recommend(
            @Parameter(description = "分类：attraction/hotel/food/product")
            @PathVariable String category,
            @Parameter(description = "返回条数，1-50，默认 10")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "城市过滤（精确匹配），空=不限城市")
            @RequestParam(required = false) String city) {
        RecommendCategory cat = RecommendCategory.fromCode(category);
        return ApiResponse.success(clientRecommendationService.list(cat, limit, city));
    }
}
