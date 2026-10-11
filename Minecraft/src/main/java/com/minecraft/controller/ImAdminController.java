package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.FavoriteVO;
import com.minecraft.dto.response.ImCallRecordVO;
import com.minecraft.dto.response.ImFriendVO;
import com.minecraft.dto.response.ImGroupMemberVO;
import com.minecraft.dto.response.ImGroupMessageVO;
import com.minecraft.dto.response.ImGroupVO;
import com.minecraft.dto.response.ImSingleMessageVO;
import com.minecraft.dto.response.MomentCommentVO;
import com.minecraft.dto.response.MomentUserVO;
import com.minecraft.dto.response.MomentVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.Emoji;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.service.ImAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 即时通讯管理后台接口（好友 / 群组 / 消息 / 通话 / 表情）。
 * 每个入口强制管理员鉴权。
 */
@Tag(name = "即时通讯-管理后台")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/im")
public class ImAdminController {

    private final AdminAuthorizationService authorizationService;
    private final ImAdminService imAdminService;

    // =========================================================
    // 好友关系
    // =========================================================

    @Operation(summary = "分页查询好友关系/好友申请")
    @GetMapping("/friends")
    public ApiResponse<PageResponse<ImFriendVO>> pageFriends(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageFriends(page, size, keyword, status));
    }

    @Operation(summary = "删除好友关系（双向）")
    @DeleteMapping("/friends/{id}")
    public ApiResponse<Void> deleteFriend(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.deleteFriendRelation(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "通过好友申请")
    @PostMapping("/friends/{id}/approve")
    public ApiResponse<Void> approveFriendRequest(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.approveFriendRequest(id);
        return ApiResponse.success("已通过", null);
    }

    @Operation(summary = "拒绝好友申请")
    @PostMapping("/friends/{id}/reject")
    public ApiResponse<Void> rejectFriendRequest(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.rejectFriendRequest(id);
        return ApiResponse.success("已拒绝", null);
    }

    // =========================================================
    // 聊天群组
    // =========================================================

    @Operation(summary = "分页查询聊天群组")
    @GetMapping("/groups")
    public ApiResponse<PageResponse<ImGroupVO>> pageGroups(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageGroups(page, size, keyword));
    }

    @Operation(summary = "查询群成员列表")
    @GetMapping("/groups/{id}/members")
    public ApiResponse<List<ImGroupMemberVO>> listGroupMembers(@PathVariable Long id) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.listGroupMembers(id));
    }

    @Operation(summary = "更新群组信息")
    @PutMapping("/groups/{id}")
    public ApiResponse<Void> updateGroup(@PathVariable Long id, @RequestBody ChatGroup group) {
        authorizationService.requireAdmin();
        imAdminService.updateGroup(id, group);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "解散群组（同时清理成员与群消息）")
    @DeleteMapping("/groups/{id}")
    public ApiResponse<Void> deleteGroup(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.deleteGroup(id);
        return ApiResponse.success("解散成功", null);
    }

    @Operation(summary = "移除群成员")
    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public ApiResponse<Void> removeGroupMember(@PathVariable Long groupId, @PathVariable Long userId) {
        authorizationService.requireAdmin();
        imAdminService.removeGroupMember(groupId, userId);
        return ApiResponse.success("移除成功", null);
    }

    // =========================================================
    // 聊天消息
    // =========================================================

    @Operation(summary = "分页查询单聊消息")
    @GetMapping("/messages/single")
    public ApiResponse<PageResponse<ImSingleMessageVO>> pageSingleMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String messageType) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageSingleMessages(page, size, userId, keyword, messageType));
    }

    @Operation(summary = "分页查询群聊消息")
    @GetMapping("/messages/group")
    public ApiResponse<PageResponse<ImGroupMessageVO>> pageGroupMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) String keyword) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageGroupMessages(page, size, groupId, keyword));
    }

    // =========================================================
    // 通话记录
    // =========================================================

    @Operation(summary = "分页查询通话记录")
    @GetMapping("/calls")
    public ApiResponse<PageResponse<ImCallRecordVO>> pageCallRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String callType) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageCallRecords(page, size, keyword, callType));
    }

    @Operation(summary = "删除通话记录")
    @DeleteMapping("/calls/{id}")
    public ApiResponse<Void> deleteCallRecord(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.deleteCallRecord(id);
        return ApiResponse.success("删除成功", null);
    }

    // =========================================================
    // 系统表情
    // =========================================================

    @Operation(summary = "分页查询系统表情")
    @GetMapping("/emojis")
    public ApiResponse<PageResponse<Emoji>> pageEmojis(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer isActive) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageEmojis(page, size, keyword, category, isActive));
    }

    @Operation(summary = "新增系统表情")
    @PostMapping("/emojis")
    public ApiResponse<Long> createEmoji(@RequestBody Emoji emoji) {
        authorizationService.requireAdmin();
        return ApiResponse.success("创建成功", imAdminService.createEmoji(emoji));
    }

    @Operation(summary = "更新系统表情")
    @PutMapping("/emojis/{id}")
    public ApiResponse<Void> updateEmoji(@PathVariable Long id, @RequestBody Emoji emoji) {
        authorizationService.requireAdmin();
        imAdminService.updateEmoji(id, emoji);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除系统表情")
    @DeleteMapping("/emojis/{id}")
    public ApiResponse<Void> deleteEmoji(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.deleteEmoji(id);
        return ApiResponse.success("删除成功", null);
    }

    // =========================================================
    // 用户所在群
    // =========================================================

    @Operation(summary = "查询用户所在的群组列表")
    @GetMapping("/users/{userId}/groups")
    public ApiResponse<List<ImGroupVO>> listUserGroups(@PathVariable Long userId) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.listUserGroups(userId));
    }

    // =========================================================
    // 朋友圈
    // =========================================================

    @Operation(summary = "分页查询朋友圈动态")
    @GetMapping("/moments")
    public ApiResponse<PageResponse<MomentVO>> pageMoments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer visibility) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageMoments(page, size, userId, keyword, visibility));
    }

    @Operation(summary = "查询朋友圈动态评论列表")
    @GetMapping("/moments/{id}/comments")
    public ApiResponse<List<MomentCommentVO>> listMomentComments(@PathVariable Long id) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.listMomentComments(id));
    }

    @Operation(summary = "查询朋友圈动态点赞用户列表")
    @GetMapping("/moments/{id}/likes")
    public ApiResponse<List<MomentUserVO>> listMomentLikes(@PathVariable Long id) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.listMomentLikes(id));
    }

    @Operation(summary = "删除朋友圈动态（逻辑删除）")
    @DeleteMapping("/moments/{id}")
    public ApiResponse<Void> deleteMoment(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.deleteMoment(id);
        return ApiResponse.success("删除成功", null);
    }

    // =========================================================
    // 收藏
    // =========================================================

    @Operation(summary = "分页查询收藏")
    @GetMapping("/favorites")
    public ApiResponse<PageResponse<FavoriteVO>> pageFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword) {
        authorizationService.requireAdmin();
        return ApiResponse.success(imAdminService.pageFavorites(page, size, userId, type, tag, keyword));
    }

    @Operation(summary = "删除收藏（逻辑删除）")
    @DeleteMapping("/favorites/{id}")
    public ApiResponse<Void> deleteFavorite(@PathVariable Long id) {
        authorizationService.requireAdmin();
        imAdminService.deleteFavorite(id);
        return ApiResponse.success("删除成功", null);
    }
}
