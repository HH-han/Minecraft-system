package com.minecraft.dto.response;

import lombok.Data;

@Data
public class RailwayStatusVO {

    private String trainNo;

    /** ON_TIME / DELAYED */
    private String status;

    private Integer delayMinutes;

    private String message;
}
