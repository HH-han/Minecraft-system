package com.minecraft.service.impl;

import com.minecraft.dto.response.AnnouncementVO;
import com.minecraft.service.AnnouncementSseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公告 SSE 推送实现
 * 每个登录用户保留一个长连接，连接断开/超时自动清理
 */
@Slf4j
@Service
public class AnnouncementSseServiceImpl implements AnnouncementSseService {

    /** 30 分钟超时，前端会自动重连 */
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
        // 立即发送一条 open 事件，触发浏览器 EventSource onopen
        try {
            emitter.send(SseEmitter.event().name("open").data("connected"));
        } catch (IOException e) {
            emitters.remove(userId);
        }
        return emitter;
    }

    @Override
    public void broadcast(AnnouncementVO vo) {
        emitters.forEach((uid, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("announcement").data(vo));
            } catch (IOException e) {
                emitters.remove(uid);
                log.error("[公告] SSE 推送失败 userId={}", uid);
            }
        });
    }

    @Override
    public int connectionCount() {
        return emitters.size();
    }
}
