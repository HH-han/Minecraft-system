package com.minecraft.controller;

import com.minecraft.dto.request.GroupCommentDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupComment;
import com.minecraft.service.GroupCommentService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.GroupCommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "动态评论")
@RestController
@RequestMapping("/api/group/post")
public class GroupCommentController {

    @Autowired
    private GroupCommentService groupCommentService;

    @Operation(summary = "发表评论")
    @PostMapping("/{postId}/comment")
    public ApiResponse<GroupComment> createComment(@PathVariable Long postId, @RequestBody GroupCommentDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        dto.setPostId(postId);
        GroupComment comment = groupCommentService.createComment(dto, userId);
        return ApiResponse.success("评论成功", comment);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupCommentService.deleteComment(commentId, userId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/{postId}/comment/list")
    public ApiResponse<List<GroupCommentVO>> getCommentList(@PathVariable Long postId) {
        List<GroupCommentVO> comments = groupCommentService.getCommentList(postId);
        return ApiResponse.success(comments);
    }
}