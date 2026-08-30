package com.minecraft.enums;

/**
 * 操作日志状态枚举，对应 operation_logs.status 字段（0-失败，1-成功）。
 */
public enum LogStatus {
    SUCCESS(1, "成功"),
    FAIL(0, "失败");

    private final Integer code;
    private final String message;

    LogStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
