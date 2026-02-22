package com.minecraft.enums;

public enum ProductType {
    SOUVENIR("纪念品"),
    SPECIALTY("特产"),
    OTHER("其他");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
