package com.minecraft.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class RailwayStopVO {

    private Integer stopSeq;

    private Long stationId;

    private String stationName;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arriveTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime departTime;

    private Integer stopOver;

    private Integer dayOffset;
}
