package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.recommendation.dto.RecommendationRuleParam;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.recommendation.service.RecommendationRuleService;
import com.minecraft.recommendation.vo.AdminRecommendationRuleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 推荐人工干预规则管理后台接口（每个入口强制管理员鉴权）。
 */
@Tag(name = "智能推荐-干预规则")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/recommendations/rules")
public class AdminRecommendationRuleController {

    private final AdminAuthorizationService authorizationService;
    private final RecommendationRuleService ruleService;

    @Operation(summary = "查看某分类的干预规则列表（附带物品名称/城市）")
    @GetMapping("/{category}")
    public ApiResponse<List<AdminRecommendationRuleVO>> list(
            @PathVariable String category,
            @Parameter(description = "状态过滤：1启用 0停用，空=全部")
            @RequestParam(required = false) Integer status) {
        authorizationService.requireAdmin();
        return ApiResponse.success(ruleService.listForAdmin(category, status));
    }

    @Operation(summary = "新建干预规则（同分类同物品仅允许一条）")
    @PostMapping
    public ApiResponse<AdminRecommendationRuleVO> create(@RequestBody RecommendationRuleParam param) {
        Long operatorId = authorizationService.requireAdmin();
        return ApiResponse.success("规则创建成功（操作人 " + operatorId + "）",
                ruleService.create(param, operatorId));
    }

    @Operation(summary = "更新干预规则")
    @PutMapping("/{id}")
    public ApiResponse<AdminRecommendationRuleVO> update(
            @PathVariable Long id,
            @RequestBody RecommendationRuleParam param) {
        Long operatorId = authorizationService.requireAdmin();
        return ApiResponse.success("规则更新成功（操作人 " + operatorId + "）",
                ruleService.update(id, param, operatorId));
    }

    @Operation(summary = "删除干预规则")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authorizationService.requireAdmin();
        ruleService.delete(id);
        return ApiResponse.success("规则已删除", null);
    }
}
