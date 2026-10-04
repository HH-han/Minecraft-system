package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.RecommendationExposureLog;
import com.minecraft.mapper.RecommendationExposureLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 曝光日志异步落库：独立 Bean，避免同类调用导致 {@link Async} 代理失效。
 * 任何异常都只记录日志，绝不影响推荐主链路。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationExposureAsyncWriter {

    private final RecommendationExposureLogMapper exposureLogMapper;

    @Async
    public void capture(List<RecommendationExposureLog> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        try {
            exposureLogMapper.batchInsert(rows);
        } catch (Exception e) {
            log.warn("推荐曝光日志批量写入失败 size={}, error={}", rows.size(), e.getMessage());
        }
    }
}
