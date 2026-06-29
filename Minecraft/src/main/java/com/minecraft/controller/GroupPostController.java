package com.minecraft.controller;

import com.minecraft.dto.request.GroupPostDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupPost;
import com.minecraft.service.GroupPostService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.GroupPostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "群组动态")
@RestController
@RequestMapping("/api/group/{groupId}/post")
public class GroupPostController {

    @Autowired
    private GroupPostService groupPostService;

    @Operation(summary = "发布动态")
    @PostMapping
    public ApiResponse<GroupPost> createPost(@PathVariable Long groupId, @RequestBody GroupPostDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        dto.setGroupId(groupId);
        GroupPost post = groupPostService.createPost(dto, userId);
        return ApiResponse.success("发布成功", post);
    }

    @Operation(summary = "删除动态")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupPostService.deletePost(postId, userId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取动态列表")
    @GetMapping("/list")
    public ApiResponse<List<GroupPostVO>> getPostList(@PathVariable Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GroupPostVO> posts = groupPostService.getPostList(groupId, userId);
        return ApiResponse.success(posts);
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/{postId}/like")
    public ApiResponse<Void> toggleLike(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupPostService.toggleLike(postId, userId);
        return ApiResponse.success("操作成功", null);
    }

    @Operation(summary = "检查是否已点赞")
    @GetMapping("/{postId}/liked")
    public ApiResponse<Boolean> isLiked(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Boolean liked = groupPostService.isLiked(postId, userId);
        return ApiResponse.success(liked);
    }
}