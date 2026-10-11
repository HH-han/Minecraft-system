package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.TravelNoteAuditDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelNoteTag;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.service.TravelNoteService;
import com.minecraft.vo.TravelNoteDetailVO;
import com.minecraft.vo.TravelNoteReportVO;
import com.minecraft.vo.TravelNoteStatVO;
import com.minecraft.vo.TravelNoteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 旅游笔记管理端接口（管理员）
 */
@Tag(name = "旅游笔记管理")
@RestController
@RequestMapping("/api/admin/travel-note")
@RequiredArgsConstructor
public class TravelNoteAdminController {

    private final TravelNoteService travelNoteService;
    private final AdminAuthorizationService adminAuthorizationService;

    @Operation(summary = "全量分页（含草稿/待审/驳回/下架）")
    @GetMapping("/page")
    public ApiResponse<PageResponse<TravelNoteVO>> page(Integer status, String keyword, PageRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(travelNoteService.adminPage(status, keyword, request));
    }

    @Operation(summary = "笔记详情（管理端，含未发布）")
    @GetMapping("/{id}")
    public ApiResponse<TravelNoteDetailVO> detail(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(travelNoteService.getDetail(id, null, null, true));
    }

    @Operation(summary = "审核：通过→已发布，驳回→已驳回")
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id, @Valid @RequestBody TravelNoteAuditDTO dto) {
        adminAuthorizationService.requireAdmin();
        travelNoteService.audit(id, dto.getApproved(), dto.getReason());
        return ApiResponse.success(Boolean.TRUE.equals(dto.getApproved()) ? "审核通过" : "已驳回", null);
    }

    @Operation(summary = "置顶/取消置顶")
    @PostMapping("/{id}/top")
    public ApiResponse<Void> top(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        travelNoteService.toggleTop(id);
        return ApiResponse.success("操作成功", null);
    }

    @Operation(summary = "加精/取消加精")
    @PostMapping("/{id}/feature")
    public ApiResponse<Void> feature(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        travelNoteService.toggleFeatured(id);
        return ApiResponse.success("操作成功", null);
    }

    @Operation(summary = "下架")
    @PostMapping("/{id}/offline")
    public ApiResponse<Void> offline(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        travelNoteService.offline(id);
        return ApiResponse.success("下架成功", null);
    }

    @Operation(summary = "数据看板")
    @GetMapping("/stat/overview")
    public ApiResponse<TravelNoteStatVO> statOverview() {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(travelNoteService.statOverview());
    }

    // ==================== 举报处理 ====================

    @Operation(summary = "举报分页")
    @GetMapping("/report/page")
    public ApiResponse<PageResponse<TravelNoteReportVO>> reportPage(Integer status, PageRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(travelNoteService.reportPage(status, request));
    }

    @Operation(summary = "处理举报（1-处理并下架笔记 2-驳回举报）")
    @PostMapping("/report/{id}/handle")
    public ApiResponse<Void> handleReport(@PathVariable Long id, @RequestParam Integer status) {
        Long adminId = adminAuthorizationService.requireAdmin();
        travelNoteService.handleReport(id, status, adminId);
        return ApiResponse.success("处理成功", null);
    }

    // ==================== 标签管理 ====================

    @Operation(summary = "标签分页")
    @GetMapping("/tag/page")
    public ApiResponse<PageResponse<TravelNoteTag>> tagPage(String keyword, PageRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(travelNoteService.tagPage(keyword, request));
    }

    @Operation(summary = "新建标签")
    @PostMapping("/tag")
    public ApiResponse<Void> createTag(@RequestBody TravelNoteTag tag) {
        adminAuthorizationService.requireAdmin();
        travelNoteService.saveTag(tag);
        return ApiResponse.success("创建成功", null);
    }

    @Operation(summary = "更新标签")
    @PutMapping("/tag/{id}")
    public ApiResponse<Void> updateTag(@PathVariable Long id, @RequestBody TravelNoteTag tag) {
        adminAuthorizationService.requireAdmin();
        tag.setId(id);
        travelNoteService.saveTag(tag);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/tag/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        travelNoteService.deleteTag(id);
        return ApiResponse.success("删除成功", null);
    }
}
