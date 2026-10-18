package com.minecraft.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RailwayTimetableVO {

    private String trainNo;

    private String trainType;

    private List<RailwayStopVO> stops;
}
