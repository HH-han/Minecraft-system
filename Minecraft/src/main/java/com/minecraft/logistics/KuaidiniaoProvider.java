package com.minecraft.logistics;

import com.minecraft.dto.response.LogisticsTrackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 快递鸟 适配器（骨架，待接入）。
 * <p>待实现：即时查询（/Ebusiness/EbusinessOrderHandle.aspx，RequestType=1002）、订阅推送。</p>
 */
@Slf4j
@Component
public class KuaidiniaoProvider implements LogisticsProvider {

    private static final String API_TYPE = "kuaidiniao";

    @Override
    public String apiType() {
        return API_TYPE;
    }

    @Override
    public List<LogisticsTrackDTO> queryTrack(String trackingNo, String phone) {
        // TODO 调用快递鸟即时查询接口
        log.debug("[快递鸟] queryTrack 骨架调用 trackingNo={}", trackingNo);
        return Collections.emptyList();
    }

    @Override
    public boolean subscribe(String trackingNo, String callbackUrl) {
        // TODO 调用快递鸟订阅接口
        return false;
    }

    @Override
    public boolean unsubscribe(String trackingNo) {
        // TODO 调用快递鸟取消订阅接口
        return false;
    }
}
