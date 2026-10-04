package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.recommendation.dto.InterventionRuleParam;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.recommendation.service.InterventionRuleService;
import com.minecraft.recommendation.vo.AdminInterventionRuleVO;
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
 * 业务级人工干预规则管理后台接口（每个入口强制管理员鉴权）。
 */
@Tag(name = "智能推荐-业务干预规则")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/intervention/rules")
public class AdminInterventionRuleController {

    private final AdminAuthorizationService authorizationService;
    private final InterventionRuleService ruleService;

    @Operation(summary = "业务干预规则列表（范围/状态/类型可选过滤）")
    @GetMapping
    public ApiResponse<List<AdminInterventionRuleVO>> list(
            @Parameter(description = "作用范围：attraction/hotel/food/product/ALL，空=全部")
            @RequestParam(required = false) String scopeCategory,
            @Parameter(description = "状态过滤：1启用 0停用，空=全部")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "规则类型过滤：RANK/TIME/SORT/HIDE/PIN/BOOST")
            @RequestParam(required = false) String ruleType) {
        authorizationService.requireAdmin();
        return ApiResponse.success(ruleService.listForAdmin(scopeCategory, status, ruleType));
    }

    @Operation(summary = "新建业务干预规则（规则编码全局唯一）")
    @PostMapping
    public ApiResponse<AdminInterventionRuleVO> create(@RequestBody InterventionRuleParam param) {
        Long operatorId = authorizationService.requireAdmin();
        return ApiResponse.success("规则创建成功（操作人 " + operatorId + "）",
                ruleService.create(param, operatorId));
    }

    @Operation(summary = "更新业务干预规则")
    @PutMapping("/{id}")
    public ApiResponse<AdminInterventionRuleVO> update(
            @PathVariable Long id,
            @RequestBody InterventionRuleParam param) {
        Long operatorId = authorizationService.requireAdmin();
        return ApiResponse.success("规则更新成功（操作人 " + operatorId + "）",
                ruleService.update(id, param, operatorId));
    }

    @Operation(summary = "删除业务干预规则")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authorizationService.requireAdmin();
        ruleService.delete(id);
        return ApiResponse.success("规则已删除", null);
    }
}
