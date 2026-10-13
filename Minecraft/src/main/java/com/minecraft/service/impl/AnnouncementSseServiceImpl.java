package com.minecraft.service.impl;

import com.minecraft.dto.response.AnnouncementVO;
import com.minecraft.service.AnnouncementSseService;
import com.minecraft.service.RealtimeSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 公告 SSE 推送实现
 * <p>
 * 复用 {@link RealtimeSseService} 的统一长连接（/api/realtime/sse），
 * 公告以 {@code announcement} 事件名广播，避免同一客户端维护多条 SSE 连接。
 */
@Service
@RequiredArgsConstructor
public class AnnouncementSseServiceImpl implements AnnouncementSseService {

    /** 公告事件名，与前端 sse.js 中监听的事件名保持一致 */
    private static final String EVENT_ANNOUNCEMENT = "announcement";

    private final RealtimeSseService realtimeSseService;

    @Override
    public SseEmitter subscribe(Long userId) {
        return realtimeSseService.subscribe();
    }

    @Override
    public void broadcast(AnnouncementVO vo) {
        realtimeSseService.broadcast(EVENT_ANNOUNCEMENT, vo);
    }

    @Override
    public int connectionCount() {
        return realtimeSseService.connectionCount();
    }
}
