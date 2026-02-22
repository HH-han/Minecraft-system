package com.minecraft.enums;

public enum Season {
    SPRING("春季"),
    SUMMER("夏季"),
    AUTUMN("秋季"),
    WINTER("冬季"),
    ALL("全年");

    private final String description;

    Season(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
