package com.minecraft.controller;

import com.minecraft.dto.request.MemberBenefitGrantDTO;
import com.minecraft.dto.request.MemberGrowthAdjustDTO;
import com.minecraft.dto.request.MemberLevelSetDTO;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.MemberBenefitRecord;
import com.minecraft.entity.MemberLevel;
import com.minecraft.entity.MemberPackage;
import com.minecraft.entity.MemberSignRecord;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.service.MemberAdminService;
import com.minecraft.vo.MemberGrowthRecordVO;
import com.minecraft.vo.MemberOrderVO;
import com.minecraft.vo.MemberUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员管理端接口（管理员）
 */
@Tag(name = "会员管理（管理端）")
@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class MemberAdminController {

    private final AdminAuthorizationService adminAuthorizationService;
    private final MemberAdminService memberAdminService;

    // ==================== 等级配置 ====================

    @Operation(summary = "会员等级列表")
    @GetMapping("/levels")
    public ApiResponse<List<MemberLevel>> levels() {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(memberAdminService.listLevels());
    }

    @Operation(summary = "新增会员等级")
    @PostMapping("/levels")
    public ApiResponse<MemberLevel> createLevel(@RequestBody MemberLevel level) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("创建成功", memberAdminService.saveLevel(level));
    }

    @Operation(summary = "更新会员等级")
    @PutMapping("/levels/{id}")
    public ApiResponse<MemberLevel> updateLevel(@PathVariable Long id, @RequestBody MemberLevel level) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("更新成功", memberAdminService.updateLevel(id, level));
    }

    @Operation(summary = "删除会员等级")
    @DeleteMapping("/levels/{id}")
    public ApiResponse<Void> deleteLevel(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        memberAdminService.deleteLevel(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 套餐配置 ====================

    @Operation(summary = "会员套餐列表")
    @GetMapping("/packages")
    public ApiResponse<List<MemberPackage>> packages() {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(memberAdminService.listPackages());
    }

    @Operation(summary = "新增会员套餐")
    @PostMapping("/packages")
    public ApiResponse<MemberPackage> createPackage(@RequestBody MemberPackage pkg) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("创建成功", memberAdminService.savePackage(pkg));
    }

    @Operation(summary = "更新会员套餐")
    @PutMapping("/packages/{id}")
    public ApiResponse<MemberPackage> updatePackage(@PathVariable Long id, @RequestBody MemberPackage pkg) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("更新成功", memberAdminService.updatePackage(id, pkg));
    }

    @Operation(summary = "删除会员套餐")
    @DeleteMapping("/packages/{id}")
    public ApiResponse<Void> deletePackage(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        memberAdminService.deletePackage(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 会员订单 ====================

    @Operation(summary = "会员订单分页")
    @GetMapping("/orders/page")
    public ApiResponse<PageResponse<MemberOrderVO>> orderPage(
            PageRequest request,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(memberAdminService.pageOrders(request, status, keyword));
    }

    @Operation(summary = "会员订单退款")
    @PostMapping("/orders/{id}/refund")
    public ApiResponse<Void> refundOrder(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        memberAdminService.refundOrder(id);
        return ApiResponse.success("退款成功", null);
    }

    // ==================== 会员用户 ====================

    @Operation(summary = "会员用户分页")
    @GetMapping("/users/page")
    public ApiResponse<PageResponse<MemberUserVO>> userPage(
            PageRequest request,
            @RequestParam(required = false) String keyword) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(memberAdminService.pageUsers(request, keyword));
    }

    @Operation(summary = "调整会员成长值")
    @PostMapping("/users/{userId}/growth")
    public ApiResponse<Void> adjustGrowth(@PathVariable Long userId,
                                          @Valid @RequestBody MemberGrowthAdjustDTO dto) {
        adminAuthorizationService.requireAdmin();
        memberAdminService.adjustGrowth(userId, dto);
        return ApiResponse.success("调整成功", null);
    }

    @Operation(summary = "设置会员等级")
    @PostMapping("/users/{userId}/level")
    public ApiResponse<Void> setLevel(@PathVariable Long userId,
                                      @Valid @RequestBody MemberLevelSetDTO dto) {
        adminAuthorizationService.requireAdmin();
        memberAdminService.setLevel(userId, dto.getLevelId());
        return ApiResponse.success("设置成功", null);
    }

    @Operation(summary = "发放会员权益")
    @PostMapping("/users/{userId}/benefit")
    public ApiResponse<MemberBenefitRecord> grantBenefit(@PathVariable Long userId,
                                                         @Valid @RequestBody MemberBenefitGrantDTO dto) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success("发放成功",
                memberAdminService.grantBenefit(userId, dto.getBenefitType(), dto.getContent()));
    }

    // ==================== 流水记录 ====================

    @Operation(summary = "成长值流水分页")
    @GetMapping("/growth/page")
    public ApiResponse<PageResponse<MemberGrowthRecordVO>> growthPage(
            PageRequest request,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(memberAdminService.pageGrowthRecords(request, userId, keyword));
    }

    @Operation(summary = "签到记录分页")
    @GetMapping("/sign/page")
    public ApiResponse<PageResponse<MemberSignRecord>> signPage(
            PageRequest request,
            @RequestParam(required = false) Long userId) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(memberAdminService.pageSignRecords(request, userId));
    }
}
