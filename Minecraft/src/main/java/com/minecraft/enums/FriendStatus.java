package com.minecraft.enums;

public enum FriendStatus {
    PENDING("待确认"),
    ACCEPTED("已添加"),
    BLOCKED("已拉黑");

    private final String description;

    FriendStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
