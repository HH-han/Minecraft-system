package com.minecraft.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class RailwayTrainVO {

    private Long serviceId;

    private Long trainId;

    private String trainNo;

    private String trainType;

    private String fromStationName;

    private String toStationName;

    private Integer fromStopSeq;

    private Integer toStopSeq;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime fromTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime toTime;

    /** 历时（分钟） */
    private Integer durationMinutes;

    /** 到达日期相对发车日的偏移天数 */
    private Integer arriveDayOffset;

    private List<RailwaySeatVO> seats;
}
