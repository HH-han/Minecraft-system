package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.TravelNoteCommentDTO;
import com.minecraft.dto.request.TravelNoteQueryDTO;
import com.minecraft.dto.request.TravelNoteSaveDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelNoteTag;
import com.minecraft.service.TravelNoteInteractionService;
import com.minecraft.service.TravelNoteService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.TravelNoteCommentVO;
import com.minecraft.vo.TravelNoteDetailVO;
import com.minecraft.vo.TravelNoteReportVO;
import com.minecraft.vo.TravelNoteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 旅游笔记用户端接口
 */
@Tag(name = "旅游笔记")
@RestController
@RequestMapping("/api/travel-note")
@RequiredArgsConstructor
public class TravelNoteController {

    private final TravelNoteService travelNoteService;
    private final TravelNoteInteractionService interactionService;

    @Operation(summary = "创建笔记（status=0草稿/1提交审核/5私密）")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody TravelNoteSaveDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        dto.setId(null);
        return ApiResponse.success("保存成功", travelNoteService.saveNote(userId, dto));
    }

    @Operation(summary = "编辑笔记")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody TravelNoteSaveDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        dto.setId(id);
        travelNoteService.saveNote(userId, dto);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除笔记")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        travelNoteService.deleteNote(id, userId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "笔记详情")
    @GetMapping("/{id}")
    public ApiResponse<TravelNoteDetailVO> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(travelNoteService.getDetail(id, userId, resolveIp(request), false));
    }

    @Operation(summary = "笔记广场分页（tab=recommend/latest/follow/city）")
    @GetMapping("/page")
    public ApiResponse<PageResponse<TravelNoteVO>> page(TravelNoteQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(travelNoteService.pageNotes(query, userId));
    }

    @Operation(summary = "我的笔记（status 可选：0草稿1待审2已发布3驳回4下架5私密）")
    @GetMapping("/mine")
    public ApiResponse<PageResponse<TravelNoteVO>> mine(Integer status, PageRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        return ApiResponse.success(travelNoteService.myNotes(userId, status, request));
    }

    @Operation(summary = "热门标签")
    @GetMapping("/tags/hot")
    public ApiResponse<List<TravelNoteTag>> hotTags(@RequestParam(required = false) Integer limit) {
        return ApiResponse.success(travelNoteService.hotTags(limit));
    }

    @Operation(summary = "点赞")
    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        interactionService.like(id, userId);
        return ApiResponse.success("点赞成功", null);
    }

    @Operation(summary = "取消点赞")
    @PostMapping("/{id}/unlike")
    public ApiResponse<Void> unlike(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        interactionService.unlike(id, userId);
        return ApiResponse.success("已取消点赞", null);
    }

    @Operation(summary = "收藏")
    @PostMapping("/{id}/collect")
    public ApiResponse<Void> collect(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        interactionService.collect(id, userId);
        return ApiResponse.success("收藏成功", null);
    }

    @Operation(summary = "取消收藏")
    @PostMapping("/{id}/uncollect")
    public ApiResponse<Void> uncollect(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        interactionService.uncollect(id, userId);
        return ApiResponse.success("已取消收藏", null);
    }

    @Operation(summary = "发表评论")
    @PostMapping("/{id}/comment")
    public ApiResponse<Long> comment(@PathVariable Long id, @Valid @RequestBody TravelNoteCommentDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        dto.setNoteId(id);
        return ApiResponse.success("评论成功", interactionService.comment(dto, userId));
    }

    @Operation(summary = "评论分页")
    @GetMapping("/{id}/comment/page")
    public ApiResponse<PageResponse<TravelNoteCommentVO>> comments(@PathVariable Long id, PageRequest request) {
        return ApiResponse.success(interactionService.comments(id, request));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        interactionService.deleteComment(commentId, userId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "举报笔记")
    @PostMapping("/{id}/report")
    public ApiResponse<Void> report(@PathVariable Long id, @RequestBody(required = false) ReportBody body) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        interactionService.report(id, userId, body == null ? null : body.getReason());
        return ApiResponse.success("举报成功，感谢您的反馈", null);
    }

    @Operation(summary = "我的举报记录")
    @GetMapping("/report/mine")
    public ApiResponse<PageResponse<TravelNoteReportVO>> myReports(PageRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        return ApiResponse.success(travelNoteService.myReports(userId, request));
    }

    @Operation(summary = "一键复制为出行计划")
    @PostMapping("/{id}/copy-to-plan")
    public ApiResponse<Long> copyToPlan(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        return ApiResponse.success("已复制到我的出行计划", travelNoteService.copyToPlan(id, userId));
    }

    /** 解析客户端IP（兼容代理） */
    private String resolveIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            int idx = ip.indexOf(',');
            return idx > 0 ? ip.substring(0, idx).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    @lombok.Data
    public static class ReportBody {
        private String reason;
    }
}
