package com.minecraft.service;

import com.minecraft.dto.response.LogisticsTrackDTO;
import com.minecraft.logistics.LogisticsProvider;

import java.util.List;

/**
 * 承运商适配器调度服务
 */
public interface LogisticsProviderService {

    /** 按 api_type 获取适配器 */
    LogisticsProvider getProvider(String apiType);

    /** 查询轨迹：carrierCode 定位承运商 → api_type → 适配器 */
    List<LogisticsTrackDTO> query(String carrierCode, String trackingNo, String phone);

    /** 订阅推送 */
    boolean subscribe(String carrierCode, String trackingNo, String callbackUrl);

    /** 取消订阅 */
    boolean unsubscribe(String carrierCode, String trackingNo);
}
