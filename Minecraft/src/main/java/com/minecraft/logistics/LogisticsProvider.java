package com.minecraft.logistics;

import com.minecraft.dto.response.LogisticsTrackDTO;

import java.util.List;

/**
 * 物流承运商适配器（策略模式）。
 * <p>实现类的 {@link #apiType()} 对应 logistics_carrier.api_type，
 * 由 LogisticsProviderService 据此选择实现。新增承运商只需实现本接口 + 插入配置数据。</p>
 */
public interface LogisticsProvider {

    /** 接口类型标识，对应 logistics_carrier.api_type（kuaidi100/kuaidiniao/aliyun） */
    String apiType();

    /** 查询实时轨迹 */
    List<LogisticsTrackDTO> queryTrack(String trackingNo, String phone);

    /** 订阅推送 */
    boolean subscribe(String trackingNo, String callbackUrl);

    /** 取消订阅 */
    boolean unsubscribe(String trackingNo);
}
