package com.minecraft.pay.dto;

import lombok.Data;

/**
 * 渠道查单结果
 */
@Data
public class QueryChannelResult {

    /** INIT / PAYING / SUCCESS / FAILED / CLOSED */
    private String status;

    private String channelTradeNo;

    private String rawResponse;
}
