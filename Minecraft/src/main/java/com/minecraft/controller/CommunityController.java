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

    @Operation(summary ="获取帖子详情")
    @GetMapping("/{id}")
    public ApiResponse<CommunityPostVO> getPostDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(communityService.getPostDetail(id, userId));
    }

    @Operation(summary ="创建帖子")
    @PostMapping("/create")
    public ApiResponse<Void> createPost(@RequestBody CommunityPost post) {
        communityService.createPost(post);
        return ApiResponse.success("发布成功", null);
    }

    @Operation(summary ="更新帖子")
    @PutMapping("/update")
    public ApiResponse<Void> updatePost(@RequestBody CommunityPost post) {
        communityService.updatePost(post);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除帖子")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ApiResponse.success("删除成功", null);
    }
}
