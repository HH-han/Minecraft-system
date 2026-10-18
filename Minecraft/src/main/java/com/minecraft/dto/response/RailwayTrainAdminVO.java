package com.minecraft.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RailwayTrainAdminVO {

    private Long id;

    private String trainNo;

    private String trainType;

    private Long startStationId;

    private Long endStationId;

    private String startStationName;

    private String endStationName;

    private Integer secondClassSeats;

    private Integer firstClassSeats;

    private Integer sleeperSeats;

    private Integer status;

    private List<RailwayTrainStopAdminVO> stops;
}
