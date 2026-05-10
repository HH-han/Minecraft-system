package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.Emoji;
import com.minecraft.service.EmojiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "系统表情管理")
@RestController
@RequestMapping("/api/emojis")
public class EmojiController {

    @Autowired
    private EmojiService emojiService;

    @Operation(summary = "获取所有表情")
    @GetMapping
    public ApiResponse<List<Emoji>> getAllEmojis() {
        List<Emoji> emojis = emojiService.getAllEmojis();
        return ApiResponse.success(emojis);
    }

    @Operation(summary = "获取表情分类列表")
    @GetMapping("/categories")
    public ApiResponse<List<String>> getCategories() {
        List<String> categories = emojiService.getAllCategories();
        return ApiResponse.success(categories);
    }

    @Operation(summary = "按分类获取表情")
    @GetMapping("/category/{category}")
    public ApiResponse<List<Emoji>> getEmojisByCategory(@PathVariable String category) {
        List<Emoji> emojis = emojiService.getEmojisByCategory(category);
        return ApiResponse.success(emojis);
    }

    @Operation(summary = "获取分组表情")
    @GetMapping("/grouped")
    public ApiResponse<Map<String, List<Emoji>>> getGroupedEmojis() {
        Map<String, List<Emoji>> grouped = emojiService.getEmojisGroupedByCategory();
        return ApiResponse.success(grouped);
    }

    @Operation(summary = "获取单个表情")
    @GetMapping("/code/{emojiCode}")
    public ApiResponse<Emoji> getEmojiByCode(@PathVariable String emojiCode) {
        Emoji emoji = emojiService.getEmojiByCode(emojiCode);
        if (emoji != null) {
            return ApiResponse.success(emoji);
        }
        return ApiResponse.error("表情不存在");
    }
}