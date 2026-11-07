package com.minecraft.dto.response;

import lombok.Data;

/**
 * 车站大屏（到发信息）
 */
@Data
public class RailwayBoardVO {

    private Long serviceId;

    private String trainNo;

    private String trainType;

    private String stationName;

    /** 始发站 */
    private String originStation;

    /** 终到站 */
    private String terminalStation;

    /** 到达时间（到达大屏） */
    private String arriveTime;

    /** 出发时间（出发大屏） */
    private String departTime;

    /** 计划时间（按大屏类型取到达或出发） */
    private String planTime;

    /** 预计时间（含晚点） */
    private String expectedTime;

    private Integer delayMinutes;

    /** WAITING / CHECKING / DEPARTED / DELAYED */
    private String status;

    private String platform;
}
