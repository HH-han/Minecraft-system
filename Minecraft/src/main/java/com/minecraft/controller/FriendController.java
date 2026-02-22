package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Friend;
import com.minecraft.service.FriendService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "好友管理")
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Operation(summary ="添加好友")
    @PostMapping("/add")
    public ApiResponse<Void> addFriend(@RequestParam Long friendId) {
        Long userId = SecurityUtils.getCurrentUserId();
        friendService.addFriend(userId, friendId);
        return ApiResponse.success("好友请求已发", null);
    }

    @Operation(summary ="接受好友请求")
    @PostMapping("/accept")
    public ApiResponse<Void> acceptFriend(@RequestParam Long friendId) {
        Long userId = SecurityUtils.getCurrentUserId();
        friendService.acceptFriend(userId, friendId);
        return ApiResponse.success("已添加好", null);
    }

    @Operation(summary ="删除好友")
    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteFriend(@RequestParam Long friendId) {
        Long userId = SecurityUtils.getCurrentUserId();
        friendService.deleteFriend(userId, friendId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary ="获取好友列表")
    @GetMapping("/list")
    public ApiResponse<List<Friend>> getFriendList() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(friendService.getFriendList(userId));
    }

    @Operation(summary ="获取好友请求列表")
    @GetMapping("/requests")
    public ApiResponse<List<Friend>> getFriendRequestList() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(friendService.getFriendRequestList(userId));
    }
}
