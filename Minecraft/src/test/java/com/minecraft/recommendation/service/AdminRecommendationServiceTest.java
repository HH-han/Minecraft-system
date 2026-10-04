package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.AttractionRecommendation;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.entity.recommendation.RecommendationConfig;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationJobLogMapper;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.dto.ConfigUpdateParam;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.dto.RecalcResult;
import com.minecraft.recommendation.dto.RecommendationFeatureParam;
import com.minecraft.recommendation.enums.JobTriggerType;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.AdminRecommendationItemVO;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO;
import com.minecraft.recommendation.vo.RecommendationScheduleStatusVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminRecommendationServiceTest {

    @Mock
    private RecommendationConfigService configService;
    @Mock
    private RecommendationRecalcService recalcService;
    @Mock
    private RecommendationAnalyticsService analyticsService;
    @Mock
    private RecommendationScheduleManager scheduleManager;
    @Mock
    private RecommendationMapperRegistry registry;
    @Mock
    private RecommendationItemCatalog catalog;
    @Mock
    private RecommendationJobLogMapper jobLogMapper;

    @InjectMocks
    private AdminRecommendationService service;

    private BaseRecommendationItem recRow(long itemId, double score, boolean featured) {
        AttractionRecommendation r = new AttractionRecommendation();
        r.setItemId(itemId);
        r.setRecommendationScore(BigDecimal.valueOf(score));
        r.setPopularityIndex(BigDecimal.valueOf(0.5));
        r.setUserPreferenceMatching(BigDecimal.valueOf(0.6));
        r.setCollaborativeScore(BigDecimal.ZERO);
        r.setContentScore(BigDecimal.valueOf(0.4));
        r.setSeasonalScore(BigDecimal.valueOf(0.5));
        r.setQualityScore(BigDecimal.valueOf(0.8));
        r.setIsFeatured(featured);
        r.setFeatureWeight(BigDecimal.ZERO);
        r.setStatus(1);
        r.setUpdateTimestamp(LocalDateTime.now());
        return r;
    }

    @Test
    void listConfig_delegates() {
        RecommendationConfig cfg = new RecommendationConfig();
        cfg.setParamKey("strategy");
        when(configService.listAll()).thenReturn(List.of(cfg));

        assertEquals(1, service.listConfig().size());
    }

    @Test
    void updateConfig_passesOperator() {
        List<ConfigUpdateParam> updates = List.of(new ConfigUpdateParam());
        service.updateConfig(updates, 9L);
        verify(configService).updateBatch(updates, 9L);
    }

    @Test
    void resetConfig_nullResetsGlobal() {
        service.resetConfig(null, 9L);
        verify(configService).resetToDefaults(isNull(), eq(9L));
    }

    @Test
    void resetConfig_invalidCategory_throws400() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resetConfig("nope", 9L));
        assertEquals(400, ex.getCode());
    }

    @Test
    void listItems_mapsRowsWithDetails() {
        when(registry.selectScoredPage(eq(RecommendCategory.ATTRACTION), eq(0), eq(20)))
                .thenReturn(List.of(recRow(1L, 0.9, true), recRow(2L, 0.7, false)));
        ItemDetail d = new ItemDetail();
        d.setItemId(1L);
        d.setName("湖景");
        d.setCity("北京");
        when(catalog.load(eq(RecommendCategory.ATTRACTION), any())).thenReturn(Map.of(1L, d));

        List<AdminRecommendationItemVO> result = service.listItems("attraction", null, null);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getItemId());
        assertTrue(result.get(0).getIsFeatured());
        assertEquals("湖景", result.get(0).getName());
        assertEquals("北京", result.get(0).getCity());
        assertEquals(0.9, result.get(0).getRecommendationScore().doubleValue(), 1e-9);
        // 缺失详情的行不报错
        assertEquals(2L, result.get(1).getItemId());
    }

    @Test
    void featureItem_success() {
        when(registry.updateManualFields(eq(RecommendCategory.ATTRACTION), eq(1L),
                eq(Boolean.TRUE), any(), isNull())).thenReturn(recRow(1L, 0.9, true));
        ItemDetail d = new ItemDetail();
        d.setItemId(1L);
        d.setName("湖景");
        when(catalog.load(eq(RecommendCategory.ATTRACTION), any())).thenReturn(Map.of(1L, d));

        RecommendationFeatureParam param = new RecommendationFeatureParam();
        param.setFeatured(true);
        param.setFeatureWeight(BigDecimal.valueOf(0.5));

        AdminRecommendationItemVO vo = service.featureItem("attraction", 1L, param);

        assertEquals(1L, vo.getItemId());
        assertEquals("湖景", vo.getName());
    }

    @Test
    void featureItem_rowMissing_throws404() {
        when(registry.updateManualFields(any(), any(), any(), any(), any())).thenReturn(null);

        RecommendationFeatureParam param = new RecommendationFeatureParam();
        param.setFeatured(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.featureItem("hotel", 99L, param));
        assertEquals(404, ex.getCode());
    }

    @Test
    void featureItem_invalidParams_throws400() {
        RecommendationFeatureParam badStatus = new RecommendationFeatureParam();
        badStatus.setStatus(9);
        assertEquals(400, assertThrows(BusinessException.class,
                () -> service.featureItem("hotel", 1L, badStatus)).getCode());

        RecommendationFeatureParam badWeight = new RecommendationFeatureParam();
        badWeight.setFeatureWeight(BigDecimal.valueOf(1.5));
        assertEquals(400, assertThrows(BusinessException.class,
                () -> service.featureItem("hotel", 1L, badWeight)).getCode());

        assertEquals(400, assertThrows(BusinessException.class,
                () -> service.featureItem("hotel", 1L, null)).getCode());

        assertEquals(400, assertThrows(BusinessException.class,
                () -> service.featureItem("hotel", null, new RecommendationFeatureParam())).getCode());
    }

    @Test
    void triggerRecalc_nullCategory_runsAllManual() {
        RecalcResult result = new RecalcResult();
        result.setCategory("ALL");
        when(recalcService.recalculate(isNull(), eq(JobTriggerType.MANUAL), eq(9L)))
                .thenReturn(result);

        RecalcResult returned = service.triggerRecalc(null, 9L);
        assertEquals("ALL", returned.getCategory());
    }

    @Test
    void triggerRecalc_specificCategory() {
        service.triggerRecalc("food", 9L);
        verify(recalcService).recalculate(eq(RecommendCategory.RESTAURANT),
                eq(JobTriggerType.MANUAL), eq(9L));
    }

    @Test
    void listJobs_returnsMapperRows() {
        RecommendationJobLog logRow = new RecommendationJobLog();
        logRow.setStatus("SUCCESS");
        when(jobLogMapper.selectList(any())).thenReturn(List.of(logRow));

        List<RecommendationJobLog> logs = service.listJobs(null, 5);

        assertEquals(1, logs.size());
        assertEquals("SUCCESS", logs.get(0).getStatus());
    }

    @Test
    void analytics_delegates() {
        RecommendationAnalyticsVO vo = new RecommendationAnalyticsVO();
        when(analyticsService.dashboard(eq("hotel"), eq(30))).thenReturn(vo);
        assertEquals(vo, service.analytics("hotel", 30));
    }

    @Test
    void scheduleStatus_mapsManagerState() {
        when(scheduleManager.isScheduled()).thenReturn(true);
        when(scheduleManager.getActiveCron()).thenReturn("0 0 3 * * ?");
        LocalDateTime next = LocalDateTime.now().plusHours(5);
        when(scheduleManager.nextRunTime()).thenReturn(next);

        RecommendationScheduleStatusVO vo = service.scheduleStatus();

        assertTrue(vo.isScheduled());
        assertEquals("0 0 3 * * ?", vo.getCron());
        assertEquals(next, vo.getNextRunTime());
    }

    @Test
    void scheduleStatus_notScheduled() {
        when(scheduleManager.isScheduled()).thenReturn(false);
        RecommendationScheduleStatusVO vo = service.scheduleStatus();
        assertFalse(vo.isScheduled());
    }
}
