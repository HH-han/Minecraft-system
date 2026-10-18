package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RailwayServiceDetailVO {

    private Long serviceId;

    private Long trainId;

    private String trainNo;

    private String trainType;

    private LocalDate serviceDate;

    private List<RailwayStopVO> stops;

    /** 全程各席别余票与票价 */
    private List<RailwaySeatVO> seats;
}
