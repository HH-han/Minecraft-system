package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RailwayGenerateRequest {

    /** 起始日期，缺省为今天 */
    private LocalDate startDate;

    /** 生成天数，缺省 15 天 */
    @Min(value = 1, message = "生成天数不能小于1天")
    @Max(value = 90, message = "生成天数不能超过90天")
    private Integer days = 15;
}
