package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.CommunityPost;
import com.minecraft.service.CommunityService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.CommunityPostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "社区管理")
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Operation(summary ="获取帖子列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<CommunityPostVO>> getPostList(PageRequest request) {
        return ApiResponse.success(communityService.getPostList(request));
    }

    @Operation(summary ="获取我的帖子列表")
    @GetMapping("/my")
    public ApiResponse<PageResponse<CommunityPostVO>> getMyPosts(PageRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        return ApiResponse.success(communityService.getMyPosts(userId, request));
    }

    @Operation(summary ="获取帖子详情")
    @GetMapping("/{id}")
    public ApiResponse<CommunityPostVO> getPostDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(communityService.getPostDetail(id, userId));
    }

    @Operation(summary ="创建帖子")
    @PostMapping("/create")
    public ApiResponse<Void> createPost(@RequestBody CommunityPost post) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        post.setUserId(userId);
        communityService.createPost(post);
        return ApiResponse.success("发布成功", null);
    }

    @Operation(summary ="更新帖子")
    @PutMapping("/update")
    public ApiResponse<Void> updatePost(@RequestBody CommunityPost post) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        // 验证帖子所有权
        CommunityPost existingPost = communityService.getById(post.getId());
        if (existingPost == null) {
            return ApiResponse.error("帖子不存在");
        }
        if (!existingPost.getUserId().equals(currentUserId)) {
            return ApiResponse.error("无权修改此帖子");
        }
        
        communityService.updatePost(post);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除帖子")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        // 验证帖子所有权
        CommunityPost existingPost = communityService.getById(id);
        if (existingPost == null) {
            return ApiResponse.error("帖子不存在");
        }
        if (!existingPost.getUserId().equals(currentUserId)) {
            return ApiResponse.error("无权删除此帖子");
        }
        
        communityService.deletePost(id);
        return ApiResponse.success("删除成功", null);
    }
}
