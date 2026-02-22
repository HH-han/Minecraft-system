package com.minecraft.controller;

import com.minecraft.dto.request.CommentRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.service.CommentService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论管理")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary ="添加评论")
    @PostMapping("/add")
    public ApiResponse<Void> addComment(@RequestBody CommentRequest commentRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        commentService.addComment(commentRequest, userId);
        return ApiResponse.success("评论成功", null);
    }

    @Operation(summary ="删除评论")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary ="获取评论列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<CommentVO>> getCommentList(
            @RequestParam String itemType,
            @RequestParam Long itemId,
            PageRequest request) {
        return ApiResponse.success(commentService.getCommentList(itemType, itemId, request));
    }

    @Operation(summary ="获取评论")
    @GetMapping("/tree")
    public ApiResponse<List<CommentVO>> getCommentTree(@RequestParam String itemType, @RequestParam Long itemId) {
        return ApiResponse.success(commentService.getCommentTree(itemType, itemId));
    }
}
