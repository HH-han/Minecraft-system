package com.minecraft.enums;

public enum MessageType {
    TEXT("文本"),
    IMAGE("图片"),
    VIDEO("视频"),
    FILE("文件");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
