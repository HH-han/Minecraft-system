package com.minecraft.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class RailwayOrderVO {

    private Long id;

    private String orderNo;

    private Long serviceId;

    private String trainNo;

    private String trainType;

    private LocalDate serviceDate;

    private String fromStationName;

    private String toStationName;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime fromTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime toTime;

    private Integer arriveDayOffset;

    private String seatType;

    private Integer passengerCount;

    /** 总金额（分） */
    private Integer totalAmount;

    private String contactPhone;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private List<RailwayTicketVO> tickets;
}
