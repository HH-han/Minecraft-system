package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.ChatMessage;
import com.minecraft.service.ChatService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天管理")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(summary ="发送消")
    @PostMapping("/send")
    public ApiResponse<Void> sendMessage(@RequestBody ChatMessage message) {
        Long userId = SecurityUtils.getCurrentUserId();
        message.setSenderId(userId);
        chatService.sendMessage(message);
        return ApiResponse.success("发送成", null);
    }

    @Operation(summary ="获取聊天记录")
    @GetMapping("/history")
    public ApiResponse<List<ChatMessage>> getChatHistory(
            @RequestParam Long friendId,
            @RequestParam(defaultValue = "50") Integer limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(chatService.getChatHistory(userId, friendId, limit));
    }

    @Operation(summary ="标记消息为已")
    @PostMapping("/read")
    public ApiResponse<Void> markAsRead(@RequestParam Long friendId) {
        Long userId = SecurityUtils.getCurrentUserId();
        chatService.markAsRead(userId, friendId);
        return ApiResponse.success("标记成功", null);
    }
}
