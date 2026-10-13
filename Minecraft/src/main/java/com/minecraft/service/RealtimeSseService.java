package com.minecraft.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 通用实时推送服务
 * <p>
 * 单条长连接同时承载「数据变更」与「系统公告」两类事件，
 * 支持匿名连接（公共页面无需登录也能实时刷新）。
 */
public interface RealtimeSseService {

    /** 订阅通用实时推送（匿名允许） */
    SseEmitter subscribe();

    /** 向所有在线连接广播一个命名事件 */
    void broadcast(String eventName, Object data);

    /** 当前连接数 */
    int connectionCount();
}
