package com.minecraft.service.impl;

import com.minecraft.service.RealtimeSseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用实时推送实现
 * <p>
 * 每个客户端（含匿名）保留一条长连接，连接断开/超时自动清理。
 * 以 UUID 作为连接标识，避免同一用户多标签页互相覆盖。
 */
@Slf4j
@Service
public class RealtimeSseServiceImpl implements RealtimeSseService {

    /** 30 分钟超时，前端会自动重连 */
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe() {
        String id = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.put(id, emitter);
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError(e -> emitters.remove(id));
        // 立即发送一条 open 事件，触发浏览器 EventSource onopen
        try {
            emitter.send(SseEmitter.event().name("open").data("connected"));
        } catch (IOException e) {
            emitters.remove(id);
        }
        return emitter;
    }

    @Override
    public void broadcast(String eventName, Object data) {
        if (emitters.isEmpty()) {
            return;
        }
        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (Exception e) {
                // 连接已断/已超时，移除即可，无需上抛（避免污染日志与系统日志表）
                emitters.remove(id);
            }
        });
    }

    @Override
    public int connectionCount() {
        return emitters.size();
    }
}
