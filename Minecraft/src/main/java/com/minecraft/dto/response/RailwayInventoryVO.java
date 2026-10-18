package com.minecraft.dto.response;

import lombok.Data;

@Data
public class RailwayInventoryVO {

    private Integer fromStopSeq;

    private Integer toStopSeq;

    private String fromStationName;

    private String toStationName;

    private String seatType;

    private Integer totalSeats;

    private Integer leftSeats;

    private Integer priceCents;
}
