package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.UserEmoji;
import com.minecraft.service.UserEmojiService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "用户表情包管理")
@RestController
@RequestMapping("/api/emoji")
public class UserEmojiController {

    @Autowired
    private UserEmojiService userEmojiService;

    @Operation(summary = "上传表情包")
    @PostMapping("/upload")
    public ApiResponse<UserEmoji> uploadEmoji(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "emojiName", required = false) String emojiName) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            UserEmoji emoji = userEmojiService.uploadEmoji(userId, file, emojiName);
            return ApiResponse.success("上传成功", emoji);
        } catch (Exception e) {
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传表情包(Base64)")
    @PostMapping("/upload/base64")
    public ApiResponse<UserEmoji> uploadEmojiBase64(
            @RequestBody EmojiBase64Request request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            UserEmoji emoji = userEmojiService.uploadEmojiBase64(userId, request.getBase64Image(), request.getEmojiName());
            return ApiResponse.success("上传成功", emoji);
        } catch (Exception e) {
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }
    
    public static class EmojiBase64Request {
        private String base64Image;
        private String emojiName;
        
        public String getBase64Image() {
            return base64Image;
        }
        
        public void setBase64Image(String base64Image) {
            this.base64Image = base64Image;
        }
        
        public String getEmojiName() {
            return emojiName;
        }
        
        public void setEmojiName(String emojiName) {
            this.emojiName = emojiName;
        }
    }

    @Operation(summary = "获取用户表情包列表")
    @GetMapping("/list")
    public ApiResponse<List<UserEmoji>> getEmojis() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<UserEmoji> emojis = userEmojiService.getEmojisByUserId(userId);
        return ApiResponse.success(emojis);
    }

    @Operation(summary = "删除表情包")
    @DeleteMapping("/{emojiId}")
    public ApiResponse<Void> deleteEmoji(@PathVariable Long emojiId) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean success = userEmojiService.deleteEmoji(userId, emojiId);
        if (success) {
            return ApiResponse.success("删除成功", null);
        } else {
            return ApiResponse.error("删除失败，表情包不存在");
        }
    }

    @Operation(summary = "更新表情包名称")
    @PutMapping("/{emojiId}")
    public ApiResponse<Void> updateEmojiName(
            @PathVariable Long emojiId,
            @RequestParam String emojiName) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean success = userEmojiService.updateEmojiName(userId, emojiId, emojiName);
        if (success) {
            return ApiResponse.success("更新成功", null);
        } else {
            return ApiResponse.error("更新失败，表情包不存在");
        }
    }

    @Operation(summary = "获取表情包数量")
    @GetMapping("/count")
    public ApiResponse<Integer> getEmojiCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer count = userEmojiService.getEmojiCount(userId);
        return ApiResponse.success(count);
    }
}