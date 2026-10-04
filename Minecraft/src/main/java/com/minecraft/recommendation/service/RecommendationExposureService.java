package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.RecommendationExposureLog;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.enums.ExposureSource;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.RecommendationItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐曝光采集：开关 + 采样率控制，组装日志行后异步落库。
 * 采样使用 {@link Math#random()}，采样率 0/1 两个端点确定（便于测试）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationExposureService {

    private final RecommendationConfigService configService;
    private final RecommendationExposureAsyncWriter asyncWriter;

    /**
     * 按配置决定是否记录本次曝光；命中采样时构造日志并异步写入。
     */
    public void captureExposure(RecommendCategory category, Long userId, ExposureSource source,
                                String requestId, List<RecommendationItemVO> items) {
        if (category == null || source == null || items == null || items.isEmpty()) {
            return;
        }
        try {
            if (!configService.getBoolean(RecommendationDefaults.EXPOSURE_ENABLED)) {
                return;
            }
            double sampleRate = configService.getDouble(null, RecommendationDefaults.EXPOSURE_SAMPLE_RATE);
            if (sampleRate <= 0.0 || Math.random() > sampleRate) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            List<RecommendationExposureLog> rows = new ArrayList<>(items.size());
            for (RecommendationItemVO item : items) {
                RecommendationExposureLog row = new RecommendationExposureLog();
                row.setUserId(userId);
                row.setCategory(category.code());
                row.setItemId(item.getItemId());
                row.setScore(item.getScore() == null ? BigDecimal.ZERO : item.getScore());
                row.setRankPosition(item.getRankPosition());
                row.setSource(source.name());
                row.setRequestId(requestId);
                row.setCreatedAt(now);
                rows.add(row);
            }
            asyncWriter.capture(rows);
        } catch (Exception e) {
            // 曝光采集永远不阻断推荐接口
            log.warn("推荐曝光采集失败 category={}, requestId={}, error={}",
                    category.code(), requestId, e.getMessage());
        }
    }
}
