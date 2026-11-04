package com.minecraft.excel.enums;

import lombok.Getter;

/**
 * 业务唯一键重复时的处理策略。
 */
@Getter
public enum DuplicateStrategy {

    SKIP("skip", "跳过已存在数据"),
    OVERWRITE("overwrite", "覆盖已存在数据"),
    ERROR("error", "遇到重复数据即报错");

    private final String code;
    private final String desc;

    DuplicateStrategy(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DuplicateStrategy of(Object value) {
        if (value == null) {
            return SKIP;
        }
        String v = value.toString().trim();
        for (DuplicateStrategy strategy : values()) {
            if (strategy.code.equalsIgnoreCase(v) || strategy.name().equalsIgnoreCase(v)) {
                return strategy;
            }
        }
        return SKIP;
    }
}
