package com.minecraft.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物流轨迹（承运商适配器出参与前端展示复用）
 */
@Data
public class LogisticsTrackDTO {

    private LocalDateTime trackTime;

    /** 承运商原始状态码 */
    private String status;

    private String context;

    private String location;

    private BigDecimal longitude;

    private BigDecimal latitude;
}
