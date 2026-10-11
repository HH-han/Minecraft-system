package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.dto.response.LogisticsTrackDTO;
import com.minecraft.entity.LogisticsCarrier;
import com.minecraft.logistics.LogisticsProvider;
import com.minecraft.mapper.LogisticsCarrierMapper;
import com.minecraft.service.LogisticsProviderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 承运商适配器调度实现：按 carrierCode → api_type → 适配器
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsProviderServiceImpl implements LogisticsProviderService {

    private final List<LogisticsProvider> providers;
    private final LogisticsCarrierMapper carrierMapper;

    private Map<String, LogisticsProvider> providerMap = Collections.emptyMap();

    @PostConstruct
    public void init() {
        providerMap = providers.stream()
                .collect(Collectors.toMap(LogisticsProvider::apiType, Function.identity(), (a, b) -> a));
    }

    @Override
    public LogisticsProvider getProvider(String apiType) {
        return providerMap.get(apiType);
    }

    @Override
    public List<LogisticsTrackDTO> query(String carrierCode, String trackingNo, String phone) {
        LogisticsProvider provider = resolveProvider(carrierCode);
        if (provider == null) {
            log.warn("[物流] 未找到承运商适配器 carrierCode={}", carrierCode);
            return Collections.emptyList();
        }
        return provider.queryTrack(trackingNo, phone);
    }

    @Override
    public boolean subscribe(String carrierCode, String trackingNo, String callbackUrl) {
        LogisticsProvider provider = resolveProvider(carrierCode);
        return provider != null && provider.subscribe(trackingNo, callbackUrl);
    }

    @Override
    public boolean unsubscribe(String carrierCode, String trackingNo) {
        LogisticsProvider provider = resolveProvider(carrierCode);
        return provider != null && provider.unsubscribe(trackingNo);
    }

    private LogisticsProvider resolveProvider(String carrierCode) {
        if (carrierCode == null) {
            return null;
        }
        LogisticsCarrier carrier = carrierMapper.selectOne(new LambdaQueryWrapper<LogisticsCarrier>()
                .eq(LogisticsCarrier::getCode, carrierCode)
                .last("LIMIT 1"));
        return carrier == null ? null : getProvider(carrier.getApiType());
    }
}
