package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.recommendation.RecommendationConfig;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.recommendation.dto.ConfigUpdateParam;
import com.minecraft.recommendation.dto.RecalcResult;
import com.minecraft.recommendation.dto.RecommendationFeatureParam;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.recommendation.service.AdminRecommendationService;
import com.minecraft.recommendation.vo.AdminRecommendationItemVO;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO;
import com.minecraft.recommendation.vo.RecommendationScheduleStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
 * 智能推荐管理后台接口（每个入口强制管理员鉴权）。
 */
@Tag(name = "智能推荐-管理后台")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/recommendations")
public class AdminRecommendationController {

    private final AdminAuthorizationService authorizationService;
    private final AdminRecommendationService adminService;

    // ---------------- 算法参数 ----------------

    @Operation(summary = "查看全部推荐参数")
    @GetMapping("/config")
    public ApiResponse<List<RecommendationConfig>> listConfig() {
        authorizationService.requireAdmin();
        return ApiResponse.success(adminService.listConfig());
    }

    @Operation(summary = "批量更新推荐参数（整体校验，任一非法全部不落库）")
    @PutMapping("/config")
    public ApiResponse<Void> updateConfig(@RequestBody List<ConfigUpdateParam> updates) {
        Long operatorId = authorizationService.requireAdmin();
        adminService.updateConfig(updates, operatorId);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "恢复参数默认值（category 为空时重置全局参数）")
    @PostMapping("/config/reset")
    public ApiResponse<Void> resetConfig(
            @Parameter(description = "分类编码，空=全局")
            @RequestParam(required = false) String category) {
        Long operatorId = authorizationService.requireAdmin();
        adminService.resetConfig(category, operatorId);
        return ApiResponse.success("已恢复默认值", null);
    }

    // ---------------- 物品管理 / 人工干预 ----------------

    @Operation(summary = "分页查看推荐物品行")
    @GetMapping("/items/{category}")
    public ApiResponse<List<AdminRecommendationItemVO>> listItems(
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        authorizationService.requireAdmin();
        return ApiResponse.success(adminService.listItems(category, page, pageSize));
    }

    @Operation(summary = "人工干预：置顶/加权/上下线")
    @PutMapping("/items/{category}/{itemId}/feature")
    public ApiResponse<AdminRecommendationItemVO> featureItem(
            @PathVariable String category,
            @PathVariable Long itemId,
            @RequestBody RecommendationFeatureParam param) {
        Long operatorId = authorizationService.requireAdmin();
        return ApiResponse.success("操作成功（操作人 " + operatorId + "）",
                adminService.featureItem(category, itemId, param));
    }

    // ---------------- 重算 / 任务日志 ----------------

    @Operation(summary = "触发推荐重算（category 为空时重算全部分类，同步返回结果）")
    @PostMapping("/recalc")
    public ApiResponse<RecalcResult> recalc(
            @Parameter(description = "分类编码，空=全量")
            @RequestParam(required = false) String category) {
        Long operatorId = authorizationService.requireAdmin();
        return ApiResponse.success(adminService.triggerRecalc(category, operatorId));
    }

    @Operation(summary = "查看最近重算任务日志")
    @GetMapping("/jobs")
    public ApiResponse<List<RecommendationJobLog>> listJobs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        authorizationService.requireAdmin();
        return ApiResponse.success(adminService.listJobs(category, limit));
    }

    // ---------------- 分析看板 / 调度 ----------------

    @Operation(summary = "推荐效果分析看板")
    @GetMapping("/analytics")
    public ApiResponse<RecommendationAnalyticsVO> analytics(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "7") Integer windowDays) {
        authorizationService.requireAdmin();
        return ApiResponse.success(adminService.analytics(category, windowDays));
    }

    @Operation(summary = "查看定时重算任务状态")
    @GetMapping("/schedule")
    public ApiResponse<RecommendationScheduleStatusVO> scheduleStatus() {
        authorizationService.requireAdmin();
        return ApiResponse.success(adminService.scheduleStatus());
    }
}
