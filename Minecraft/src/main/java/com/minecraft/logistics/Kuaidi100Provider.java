package com.minecraft.logistics;

import com.minecraft.dto.response.LogisticsTrackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 快递100 适配器（骨架，待接入）。
 * <p>待实现：实时查询（/poll/query.do）、订阅推送（/poll 接口）、签名校验。</p>
 */
@Slf4j
@Component
public class Kuaidi100Provider implements LogisticsProvider {

    private static final String API_TYPE = "kuaidi100";

    @Override
    public String apiType() {
        return API_TYPE;
    }

    @Override
    public List<LogisticsTrackDTO> queryTrack(String trackingNo, String phone) {
        // TODO 调用快递100 实时查询接口，解析 data 为轨迹列表
        log.debug("[快递100] queryTrack 骨架调用 trackingNo={}", trackingNo);
        return Collections.emptyList();
    }

    @Override
    public boolean subscribe(String trackingNo, String callbackUrl) {
        // TODO 调用快递100 订阅接口
        return false;
    }

    @Override
    public boolean unsubscribe(String trackingNo) {
        // TODO 调用快递100 取消订阅接口
        return false;
    }
}
