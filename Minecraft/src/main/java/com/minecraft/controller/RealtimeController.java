package com.minecraft.controller;

import com.minecraft.service.RealtimeSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 通用实时推送接口
 * <p>
 * 客户端（含未登录游客）建立一条 SSE 长连接，
 * 同时接收 {@code data-change}（数据变更）与 {@code announcement}（系统公告）事件。
 */
@Tag(name = "实时推送")
@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
public class RealtimeController {

    private final RealtimeSseService realtimeSseService;

    @Operation(summary = "通用实时推送（数据变更 + 公告，匿名允许）")
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse() {
        return realtimeSseService.subscribe();
    }
}
