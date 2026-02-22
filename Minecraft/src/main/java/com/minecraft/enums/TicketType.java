package com.minecraft.enums;

public enum TicketType {
    TRAIN("火车票"),
    FLIGHT("机票"),
    BUS("汽车票"),
    TAXI("出租车");

    private final String description;

    TicketType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
