package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.RecommendationConfig;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationConfigMapper;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.algorithm.StrategyParameters;
import com.minecraft.recommendation.dto.ConfigUpdateParam;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.event.RecommendationConfigChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationConfigServiceTest {

    @Mock
    private RecommendationConfigMapper configMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private RecommendationConfigService service;

    private List<RecommendationConfig> seedRows() {
        return RecommendationDefaults.seedConfigs();
    }

    @BeforeEach
    void setUp() {
        service = new RecommendationConfigService();
        ReflectionTestUtils.setField(service, "baseMapper", configMapper);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    private ConfigUpdateParam param(String scope, String category, String key, String value) {
        ConfigUpdateParam p = new ConfigUpdateParam();
        p.setScope(scope);
        p.setCategory(category);
        p.setParamKey(key);
        p.setParamValue(value);
        return p;
    }

    @Test
    void snapshot_loadedFromDb_thenCachedLocally() {
        when(configMapper.selectList(any())).thenReturn(seedRows());

        ConfigSnapshot first = service.snapshot();
        // 全局键
        assertEquals("20", first.globalValue(RecommendationDefaults.CF_NEIGHBOR_K));
        // 分类键
        assertEquals("0.25", first.categoryValue("hotel", RecommendationDefaults.WEIGHT_CONTENT));
        assertEquals("HYBRID", first.categoryValue("hotel", RecommendationDefaults.STRATEGY));
        assertEquals(4, first.getCategories().size());

        service.snapshot();
        // 第二次命中进程内缓存，只查库一次
        verify(configMapper, times(1)).selectList(any());

        service.invalidateCache();
        service.snapshot();
        verify(configMapper, times(2)).selectList(any());
    }

    @Test
    void get_fallbackOrder_categoryThenGlobalThenDefault() {
        // 数据库中删除 hotel 的 content 权重行 → 回退到代码默认值
        List<RecommendationConfig> rows = new ArrayList<>(seedRows());
        rows.removeIf(r -> "CATEGORY".equals(r.getScope()) && "hotel".equals(r.getCategory())
                && RecommendationDefaults.WEIGHT_CONTENT.equals(r.getParamKey()));
        when(configMapper.selectList(any())).thenReturn(rows);

        // hotel.content 行缺失 → 默认 0.25
        assertEquals(0.25, service.getDouble(RecommendCategory.HOTEL,
                RecommendationDefaults.WEIGHT_CONTENT), 1e-12);
        // 未知键 → null
        assertEquals(null, service.get(RecommendCategory.HOTEL, "not.exist"));
    }

    @Test
    void typedGetters_andStrategyParameters() {
        when(configMapper.selectList(any())).thenReturn(seedRows());
        assertTrue(service.getBoolean(RecommendationDefaults.EXPOSURE_ENABLED));
        assertEquals(20, service.getInt(null, RecommendationDefaults.CF_NEIGHBOR_K));

        StrategyParameters params = service.strategyParameters(RecommendCategory.RESTAURANT);
        assertEquals(0.25, params.weightCollaborative(), 1e-12);
        assertEquals(20, params.neighborK());
        assertEquals(0.3, params.diversityStrength(), 1e-12);
    }

    @Test
    void updateBatch_existingRowUpdated_andCacheEvictedAndEventPublished() {
        when(configMapper.selectList(any())).thenReturn(seedRows());
        RecommendationConfig existing = new RecommendationConfig();
        existing.setScope("GLOBAL");
        existing.setCategory("");
        existing.setParamKey(RecommendationDefaults.DIVERSITY_STRENGTH);
        existing.setParamValue("0.3");
        when(configMapper.selectByKey(eq("GLOBAL"), eq(""), eq(RecommendationDefaults.DIVERSITY_STRENGTH)))
                .thenReturn(existing);

        service.snapshot();
        service.updateBatch(List.of(
                param("GLOBAL", "", RecommendationDefaults.DIVERSITY_STRENGTH, "0.5")), 7L);

        assertEquals("0.5", existing.getParamValue());
        assertEquals(7L, existing.getUpdatedBy());
        verify(configMapper).updateById(existing);
        verify(configMapper, never()).insert(any());
        // redisUtil 未注入，缓存失效不会产生 delete 调用
        verify(configMapper, never()).delete(any());

        ArgumentCaptor<RecommendationConfigChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(RecommendationConfigChangedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertFalse(eventCaptor.getValue().isScheduleAffected());
        // 缓存已失效 → 再次读快照触发查库
        when(configMapper.selectList(any())).thenReturn(seedRows());
        service.snapshot();
        verify(configMapper, times(2)).selectList(any());
    }

    @Test
    void updateBatch_newRowInserted_scheduleChangeFlagged() {
        // 行不存在 → insert
        when(configMapper.selectByKey(anyString(), anyString(), anyString())).thenReturn(null);

        service.updateBatch(List.of(
                param("GLOBAL", "", RecommendationDefaults.SCHEDULE_CRON, "0 0/30 * * * ?")), 9L);

        ArgumentCaptor<RecommendationConfig> captor = ArgumentCaptor.forClass(RecommendationConfig.class);
        verify(configMapper).insert(captor.capture());
        assertEquals("0 0/30 * * * ?", captor.getValue().getParamValue());
        assertEquals(9L, captor.getValue().getUpdatedBy());

        ArgumentCaptor<RecommendationConfigChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(RecommendationConfigChangedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getValue().isScheduleAffected());
    }

    @Test
    void updateBatch_validationFailures_allOrNothing() {
        // 任何一项非法都不允许落库
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.WEIGHT_CONTENT, "-0.1")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.CF_NEIGHBOR_K, "abc")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.CF_NEIGHBOR_K, "5.5")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.CF_NEIGHBOR_K, "999")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.EXPOSURE_ENABLED, "yes")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.STRATEGY, "MAGIC")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.SCHEDULE_CRON, "0 0 3 * *")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.SCHEDULE_CRON, "0 0 3 * * * 2024")));
        assertInvalid(List.of(param("GLOBAL", "", RecommendationDefaults.SCHEDULE_CRON, "61 0 3 * * ?")));
        assertInvalid(List.of(param("GLOBAL", "", "unknown.key", "1")));
        assertInvalid(List.of(param("CATEGORY", "xxx", RecommendationDefaults.STRATEGY, "HYBRID")));
        assertInvalid(List.of(param("CATEGORY", "attraction", RecommendationDefaults.CF_NEIGHBOR_K, "20")));
        assertInvalid(List.of(param("BOGUS", "", RecommendationDefaults.WEIGHT_CONTENT, "0.1")));
        assertInvalid(List.of());
        // 合法 + 非法混合 → 同样整体拒绝
        assertInvalid(List.of(
                param("GLOBAL", "", RecommendationDefaults.WEIGHT_CONTENT, "0.1"),
                param("GLOBAL", "", RecommendationDefaults.WEIGHT_QUALITY, "9")));

        verify(configMapper, never()).insert(any());
        verify(configMapper, never()).updateById(any());
    }

    @Test
    void validateCron_acceptsQuartzQuestionMark() {
        // 种子 cron 必须合法
        RecommendationConfigService.validateCron("0 0 3 * * ?");
        RecommendationConfigService.validateCron("0 0/15 9-18 * * MON-FRI");
        assertThrows(BusinessException.class,
                () -> RecommendationConfigService.validateCron("not a cron"));
    }

    @Test
    void resetToDefaults_global_reinsertsSeededGlobalRows() {
        service.resetToDefaults(null, 1L);
        verify(configMapper).delete(any());
        // 17 条全局默认
        verify(configMapper, times(17)).insert(any());
        ArgumentCaptor<RecommendationConfigChangedEvent> captor =
                ArgumentCaptor.forClass(RecommendationConfigChangedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertTrue(captor.getValue().isScheduleAffected());
    }

    @Test
    void resetToDefaults_category_reinsertsSixRows() {
        service.resetToDefaults(RecommendCategory.SOUVENIR, 1L);
        verify(configMapper).delete(any());
        verify(configMapper, times(6)).insert(any());
    }

    @Test
    void redisFailure_doesNotBreakReads() {
        // redisUtil 为 null（未注入）时直接走数据库，不报错
        when(configMapper.selectList(any())).thenReturn(seedRows());
        assertEquals("20", service.getGlobalString(RecommendationDefaults.CF_NEIGHBOR_K));
    }

    private void assertInvalid(List<ConfigUpdateParam> updates) {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateBatch(updates, 1L));
        assertEquals(400, ex.getCode());
    }
}
