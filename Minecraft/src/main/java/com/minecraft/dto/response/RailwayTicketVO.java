package com.minecraft.dto.response;

import lombok.Data;

@Data
public class RailwayTicketVO {

    private Long id;

    private String passengerName;

    private String idCardNo;

    private String seatNo;

    private Integer priceCents;

    private String status;
}
