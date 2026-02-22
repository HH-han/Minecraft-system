package com.minecraft.enums;

public enum TravelType {
    INDEPENDENT("自由行"),
    GROUP_TOUR("跟团游"),
    SELF_DRIVING("自驾游"),
    CRUISE("邮轮游");

    private final String description;

    TravelType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
