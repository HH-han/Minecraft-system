package com.minecraft.dto.response;

import lombok.Data;

@Data
public class RailwaySeatVO {

    /** SECOND_CLASS / FIRST_CLASS / SLEEPER */
    private String seatType;

    private Integer leftSeats;

    private Integer totalSeats;

    /** 票价（分） */
    private Integer priceCents;
}
