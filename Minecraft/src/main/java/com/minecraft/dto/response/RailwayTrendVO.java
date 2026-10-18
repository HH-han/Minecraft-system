package com.minecraft.dto.response;

import lombok.Data;

@Data
public class RailwayTrendVO {

    private String date;

    /** 收入（分） */
    private Long amount;

    private Long orders;
}
