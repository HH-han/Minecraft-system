package com.minecraft.logistics;

import com.minecraft.dto.response.LogisticsTrackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 阿里云物流 适配器（骨架，待接入）。
 * <p>待实现：轨迹查询、电子面单、订阅回传。</p>
 */
@Slf4j
@Component
public class AliyunLogisticsProvider implements LogisticsProvider {

    private static final String API_TYPE = "aliyun";

    @Override
    public String apiType() {
        return API_TYPE;
    }

    @Override
    public List<LogisticsTrackDTO> queryTrack(String trackingNo, String phone) {
        // TODO 调用阿里云物流轨迹查询接口
        log.debug("[阿里云物流] queryTrack 骨架调用 trackingNo={}", trackingNo);
        return Collections.emptyList();
    }

    @Override
    public boolean subscribe(String trackingNo, String callbackUrl) {
        // TODO 调用阿里云物流订阅接口
        return false;
    }

    @Override
    public boolean unsubscribe(String trackingNo) {
        // TODO 调用阿里云物流取消订阅接口
        return false;
    }
}
