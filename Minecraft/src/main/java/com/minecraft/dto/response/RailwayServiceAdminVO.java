package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RailwayServiceAdminVO {

    private Long id;

    private Long trainId;

    private String trainNo;

    private String trainType;

    private LocalDate serviceDate;

    private Integer status;

    private String startStationName;

    private String endStationName;

    private Integer secondClassLeft;

    private Integer firstClassLeft;

    private Integer sleeperLeft;
}
