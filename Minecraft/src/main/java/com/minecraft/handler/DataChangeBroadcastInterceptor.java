package com.minecraft.handler;

import com.minecraft.dto.response.DataChangeEvent;
import com.minecraft.service.RealtimeSseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Set;

/**
 * 数据变更广播拦截器
 * <p>
 * 任意「写操作」（POST/PUT/PATCH/DELETE）在响应成功后，
 * 通过 SSE 广播 {@code data-change} 事件，通知所有在线客户端自动刷新，
 * 从而解决「管理员后台改动 / 其他用户改动后，客户端需手动刷新才可见」的问题。
 * <p>
 * 说明：管理员端与用户端共用同一后端，因此管理员端的写操作同样会触发广播。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataChangeBroadcastInterceptor implements HandlerInterceptor {

    /** 数据变更事件名，与前端 sse.js 中监听的事件名保持一致 */
    private static final String EVENT_DATA_CHANGE = "data-change";

    private static final Set<String> MUTATING_METHODS =
            Set.of("POST", "PUT", "PATCH", "DELETE");

    /** 路径前缀命中即忽略（无关数据展示或高频、无意义请求） */
    private static final List<String> EXCLUDE_PREFIXES = List.of(
            "/api/auth",
            "/api/captcha",
            "/api/upload",
            "/api/realtime",
            "/api/server-monitor",
            "/api/cache",
            "/api/token",
            "/api/online-users",
            "/api/operation-logs",
            "/api/system-logs",
            "/api/login-log",
            "/api/recommendations/exposure",
            "/api/ai/trip",
            "/api/announcements/sse"
    );

    /** 路径包含即忽略（浏览量/已读/曝光类写操作，不改变列表本身） */
    private static final List<String> EXCLUDE_CONTAINS = List.of(
            "/read",
            "/exposure",
            "/view",
            "/typing",
            "/heartbeat"
    );

    private final RealtimeSseService realtimeSseService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 仅处理成功响应
        if (ex != null) {
            return;
        }
        if (!MUTATING_METHODS.contains(request.getMethod())) {
            return;
        }
        int status = response.getStatus();
        if (status < 200 || status >= 300) {
            return;
        }
        String path = request.getRequestURI();
        if (path == null || !path.startsWith("/api/") || isExcluded(path)) {
            return;
        }
        if (realtimeSseService.connectionCount() == 0) {
            return;
        }
        realtimeSseService.broadcast(EVENT_DATA_CHANGE,
                new DataChangeEvent(resolveResource(path), request.getMethod(), path, System.currentTimeMillis()));
    }

    private boolean isExcluded(String path) {
        for (String prefix : EXCLUDE_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        for (String keyword : EXCLUDE_CONTAINS) {
            if (path.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求路径中解析资源标识：
     * 去掉 {@code /api} 与前缀的 admin/public/client 段，取第一个业务段。
     * 例：/api/admin/after-sale/5 -> after-sale；/api/attraction/5 -> attraction
     */
    private String resolveResource(String path) {
        String[] segments = path.split("/");
        for (int i = 2; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.isEmpty()) {
                continue;
            }
            if ("admin".equals(segment) || "public".equals(segment) || "client".equals(segment)) {
                continue;
            }
            return segment;
        }
        return "";
    }
}
