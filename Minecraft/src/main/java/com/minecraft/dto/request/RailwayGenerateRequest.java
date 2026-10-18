package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RailwayGenerateRequest {

    /** 起始日期，缺省为今天 */
    private LocalDate startDate;

    /** 生成天数，缺省 15 天 */
    private Integer days = 15;
}
