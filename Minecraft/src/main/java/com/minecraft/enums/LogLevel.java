package com.minecraft.enums;

/**
 * 系统日志级别枚举，对应 system_log.log_level 字段。
 */
public enum LogLevel {
    DEBUG("调试"),
    INFO("信息"),
    WARN("警告"),
    ERROR("错误");

    private final String description;

    LogLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
