package com.minecraft.enums;

public enum CallStatus {
    INITIATED("已发起"),
    RINGING("响铃中"),
    ACCEPTED("已接受"),
    CONNECTED("通话中"),
    ENDED("已结束"),
    REJECTED("已拒绝"),
    TIMEOUT("超时");
    
    private final String description;
    
    CallStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}