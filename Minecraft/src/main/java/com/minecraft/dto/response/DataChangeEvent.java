package com.minecraft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用数据变更事件
 * <p>
 * 任意写操作（新增/修改/删除）成功后，由 {@code DataChangeBroadcastInterceptor}
 * 通过 SSE 广播给所有在线客户端，客户端据此自动重新拉取当前页面数据。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataChangeEvent {

    /** 资源标识，取自请求路径（如 attraction、announcements、after-sale） */
    private String resource;

    /** 请求方法：POST / PUT / PATCH / DELETE */
    private String method;

    /** 请求路径 */
    private String path;

    /** 事件时间戳（毫秒） */
    private long at;
}
