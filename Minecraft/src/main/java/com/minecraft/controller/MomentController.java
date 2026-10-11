package com.minecraft.controller;

import com.minecraft.dto.request.MomentCommentDTO;
import com.minecraft.dto.request.MomentPublishDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.MomentCommentVO;
import com.minecraft.dto.response.MomentUserVO;
import com.minecraft.dto.response.MomentVO;
import com.minecraft.service.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 朋友圈
 */
@Tag(name = "朋友圈")
@RestController
@RequestMapping("/api/public/moment")
public class MomentController {

    @Autowired
    private MomentService momentService;

    @Operation(summary = "发布朋友圈动态")
    @PostMapping("/publish")
    public ApiResponse<MomentVO> publish(@RequestBody MomentPublishDTO dto) {
        return ApiResponse.success("发布成功", momentService.publishMoment(dto.getUserId(), dto));
    }

    @Operation(summary = "获取朋友圈信息流（自己 + 好友）")
    @GetMapping("/feed")
    public ApiResponse<List<MomentVO>> getFeed(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResponse.success("成功", momentService.getFeed(userId, page, size));
    }

    @Operation(summary = "获取指定用户的动态列表")
    @GetMapping("/user")
    public ApiResponse<List<MomentVO>> getUserMoments(
            @RequestParam Long userId,
            @RequestParam Long targetUserId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResponse.success("成功", momentService.getUserMoments(userId, targetUserId, page, size));
    }

    @Operation(summary = "删除动态")
    @DeleteMapping("/{momentId}")
    public ApiResponse<Void> deleteMoment(@PathVariable Long momentId, @RequestParam Long userId) {
        momentService.deleteMoment(userId, momentId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/{momentId}/like")
    public ApiResponse<Map<String, Object>> toggleLike(@PathVariable Long momentId, @RequestParam Long userId) {
        return ApiResponse.success("成功", momentService.toggleLike(userId, momentId));
    }

    @Operation(summary = "获取动态点赞用户列表")
    @GetMapping("/{momentId}/likes")
    public ApiResponse<List<MomentUserVO>> getLikes(@PathVariable Long momentId) {
        return ApiResponse.success("成功", momentService.getLikes(momentId));
    }

    @Operation(summary = "发表评论")
    @PostMapping("/{momentId}/comment")
    public ApiResponse<MomentCommentVO> addComment(
            @PathVariable Long momentId,
            @RequestBody MomentCommentDTO dto) {
        return ApiResponse.success("评论成功", momentService.addComment(dto.getUserId(), momentId, dto));
    }

    @Operation(summary = "获取动态评论列表")
    @GetMapping("/{momentId}/comments")
    public ApiResponse<List<MomentCommentVO>> getComments(@PathVariable Long momentId) {
        return ApiResponse.success("成功", momentService.getComments(momentId));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        momentService.deleteComment(userId, commentId);
        return ApiResponse.success("删除成功", null);
    }
}
