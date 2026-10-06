package com.minecraft.service;

import com.minecraft.dto.response.AnnouncementVO;

/**
 * 公告 SSE 实时推送服务
 */
public interface AnnouncementSseService {

    /** 订阅公告推送 */
    org.springframework.web.servlet.mvc.method.annotation.SseEmitter subscribe(Long userId);

    /** 向所有在线用户广播新公告 */
    void broadcast(AnnouncementVO vo);

    /** 当前连接数 */
    int connectionCount();
}
