package com.minecraft.controller;

import com.minecraft.dto.request.AnnouncementQueryRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.AnnouncementVO;
import com.minecraft.dto.response.HomeInitVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.service.AnnouncementCacheService;
import com.minecraft.service.AnnouncementService;
import com.minecraft.service.AnnouncementSseService;
import com.minecraft.utils.JwtUtil;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * 公告用户端接口
 */
@Tag(name = "系统公告")
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AnnouncementCacheService cacheService;
    private final AnnouncementSseService sseService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "分页查询已发布公告")
    @GetMapping
    public ApiResponse<PageResponse<AnnouncementVO>> page(AnnouncementQueryRequest request) {
        return ApiResponse.success(announcementService.pageForUser(request, SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "公告详情（浏览量+1）")
    @GetMapping("/{id}")
    public ApiResponse<AnnouncementVO> detail(@PathVariable Long id) {
        return ApiResponse.success(announcementService.getDetail(id, SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "首页展示公告（弹窗/轮播/横幅数据源）")
    @GetMapping("/active")
    public ApiResponse<List<AnnouncementVO>> active() {
        return ApiResponse.success(announcementService.getActive());
    }

    @Operation(summary = "首页合并初始化（活跃公告 + 未读数，减少请求数）")
    @GetMapping("/home-init")
    public ApiResponse<HomeInitVO> homeInit() {
        Long userId = SecurityUtils.getCurrentUserId();
        HomeInitVO vo = new HomeInitVO();
        vo.setActiveList(announcementService.getActive());
        vo.setUnreadCount(userId != null
                ? announcementService.getUnreadCount(userId)
                : 0L);
        return ApiResponse.success(vo);
    }

    @Operation(summary = "我的未读数")
    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.success(announcementService.getUnreadCount(SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "我的未读公告列表")
    @GetMapping("/my-unread")
    public ApiResponse<List<AnnouncementVO>> myUnread() {
        return ApiResponse.success(announcementService.getMyUnread(SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "标记已读（幂等）")
    @PostMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        announcementService.markRead(id, SecurityUtils.getCurrentUserId());
        return ApiResponse.success("已读成功", null);
    }

    @Operation(summary = "批量标记已读（幂等）")
    @PostMapping("/batch-read")
    public ApiResponse<Void> batchRead(@RequestBody List<Long> ids) {
        announcementService.batchMarkRead(ids, SecurityUtils.getCurrentUserId());
        return ApiResponse.success("批量已读成功", null);
    }

    @Operation(summary = "SSE 实时推送（EventSource 无法携带请求头，通过 token 参数认证）")
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(@RequestParam(required = false) String token) {
        if (token == null || token.isEmpty() || !jwtUtil.validateToken(token)) {
            SseEmitter emitter = new SseEmitter(0L);
            emitter.completeWithError(new RuntimeException("未授权"));
            return emitter;
        }
        return sseService.subscribe(jwtUtil.getUserId(token));
    }
}
