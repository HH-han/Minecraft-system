package com.minecraft.controller;

import com.minecraft.dto.request.AnnouncementQueryRequest;
import com.minecraft.dto.request.AnnouncementSaveRequest;
import com.minecraft.dto.response.AnnouncementStatVO;
import com.minecraft.dto.response.AnnouncementVO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.User;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.service.AnnouncementService;
import com.minecraft.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公告管理端接口（管理员，permissions='0'）
 */
@Tag(name = "公告管理")
@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
public class AnnouncementAdminController {

    private final AnnouncementService announcementService;
    private final AdminAuthorizationService adminAuthorizationService;
    private final UserService userService;

    @Operation(summary = "分页查询公告（含草稿/已下架）")
    @GetMapping
    public ApiResponse<PageResponse<AnnouncementVO>> page(AnnouncementQueryRequest request) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(announcementService.pageForAdmin(request));
    }

    @Operation(summary = "新建公告（status=1 立即发布，指定未来 publishTime 为定时发布）")
    @PostMapping
    public ApiResponse<Long> save(@Valid @RequestBody AnnouncementSaveRequest request) {
        Long adminId = adminAuthorizationService.requireAdmin();
        return ApiResponse.success("创建成功",
                announcementService.saveAnnouncement(request, adminId, resolveAdminName(adminId)));
    }

    @Operation(summary = "修改公告")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody AnnouncementSaveRequest request) {
        Long adminId = adminAuthorizationService.requireAdmin();
        request.setId(id);
        announcementService.updateAnnouncement(request, adminId, resolveAdminName(adminId));
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除公告（逻辑删除）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        announcementService.deleteAnnouncement(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "发布公告")
    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        announcementService.publish(id);
        return ApiResponse.success("发布成功", null);
    }

    @Operation(summary = "下架公告")
    @PostMapping("/{id}/offline")
    public ApiResponse<Void> offline(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        announcementService.offline(id);
        return ApiResponse.success("下架成功", null);
    }

    @Operation(summary = "置顶/取消置顶")
    @PostMapping("/{id}/top")
    public ApiResponse<Void> top(@PathVariable Long id, @RequestParam Integer isTop) {
        adminAuthorizationService.requireAdmin();
        announcementService.toggleTop(id, isTop);
        return ApiResponse.success(isTop == 1 ? "置顶成功" : "已取消置顶", null);
    }

    @Operation(summary = "阅读统计（浏览量/已读数/阅读率）")
    @GetMapping("/{id}/read-stat")
    public ApiResponse<AnnouncementStatVO> readStat(@PathVariable Long id) {
        adminAuthorizationService.requireAdmin();
        return ApiResponse.success(announcementService.getReadStat(id));
    }

    private String resolveAdminName(Long adminId) {
        User admin = userService.getById(adminId);
        if (admin == null) {
            return "admin";
        }
        return admin.getNickname() != null && !admin.getNickname().isEmpty()
                ? admin.getNickname()
                : admin.getUsername();
    }
}
