package com.minecraft.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class RailwayTrainStopAdminVO {

    private Long id;

    private Long stationId;

    private String stationName;

    private Integer stopSeq;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arriveTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime departTime;

    private Integer stopOver;

    private Integer dayOffset;

    private Integer secondClassPriceCents;

    private Integer firstClassPriceCents;

    private Integer sleeperPriceCents;
}
