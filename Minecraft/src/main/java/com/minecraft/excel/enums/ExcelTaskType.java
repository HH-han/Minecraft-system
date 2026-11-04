package com.minecraft.excel.enums;

import lombok.Getter;

@Getter
public enum ExcelTaskType {

    IMPORT(1, "导入"),
    EXPORT(2, "导出");

    private final int code;
    private final String desc;

    ExcelTaskType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
