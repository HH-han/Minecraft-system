package com.minecraft.excel.enums;

import lombok.Getter;

@Getter
public enum ExcelTaskStatus {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "成功"),
    PARTIAL_FAIL(3, "部分失败"),
    FAILED(4, "失败"),
    CANCELLED(5, "已取消");

    private final int code;
    private final String desc;

    ExcelTaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ExcelTaskStatus of(Integer code) {
        if (code == null) {
            return PENDING;
        }
        for (ExcelTaskStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
}
