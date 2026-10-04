package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationAnalyticsMapper;
import com.minecraft.mapper.RecommendationExposureLogMapper;
import com.minecraft.mapper.RecommendationJobLogMapper;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.dto.ItemExposureResult;
import com.minecraft.recommendation.dto.ScoreBucketResult;
import com.minecraft.recommendation.enums.JobStatus;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationAnalyticsServiceTest {

    @Mock
    private RecommendationAnalyticsMapper analyticsMapper;
    @Mock
    private RecommendationJobLogMapper jobLogMapper;
    @Mock
    private RecommendationExposureLogMapper exposureLogMapper;
    @Mock
    private RecommendationConfigService configService;
    @Mock
    private RecommendationItemCatalog catalog;

    @InjectMocks
    private RecommendationAnalyticsService service;

    private ScoreBucketResult bucket(int b, long cnt) {
        ScoreBucketResult r = new ScoreBucketResult();
        r.setBucket(b);
        r.setCnt(cnt);
        return r;
    }

    private ItemExposureResult exposed(long id, long cnt) {
        ItemExposureResult r = new ItemExposureResult();
        r.setItemId(id);
        r.setExposures(cnt);
        return r;
    }

    private RecommendationJobLog job(String status, long durationMs, LocalDateTime start) {
        RecommendationJobLog log = new RecommendationJobLog();
        log.setCategory("attraction");
        log.setStatus(status);
        log.setDurationMs(durationMs);
        log.setStartTime(start);
        return log;
    }

    @Test
    void dashboard_singleCategory_fullMetrics() {
        when(configService.getInt(isNull(), eq(RecommendationDefaults.EXPOSURE_ATTRIBUTION_HOURS)))
                .thenReturn(72);
        when(analyticsMapper.countExposures(eq("attraction"), any())).thenReturn(100L);
        when(analyticsMapper.countExposureUsers(eq("attraction"), any())).thenReturn(20L);
        when(analyticsMapper.countExposureItems(eq("attraction"), any())).thenReturn(8L);
        when(analyticsMapper.countAttributedInteractions(eq("attraction"), any(), eq(72)))
                .thenReturn(10L);
        when(analyticsMapper.countAttributedConversions(eq("attraction"), any(), eq(72)))
                .thenReturn(4L);
        when(analyticsMapper.selectScoreBuckets(eq("attraction"), any()))
                .thenReturn(List.of(bucket(3, 50L), bucket(7, 50L)));
        when(analyticsMapper.selectTopItems(eq("attraction"), any(), anyInt()))
                .thenReturn(List.of(exposed(1L, 60L), exposed(2L, 40L)));
        when(catalog.countActive(RecommendCategory.ATTRACTION)).thenReturn(10L);

        ItemDetail d1 = new ItemDetail();
        d1.setItemId(1L);
        d1.setName("湖景");
        d1.setCity("北京");
        when(catalog.load(eq(RecommendCategory.ATTRACTION), any()))
                .thenReturn(java.util.Map.of(1L, d1));

        when(jobLogMapper.selectList(any())).thenReturn(List.of(
                job(JobStatus.SUCCESS.name(), 1000L, LocalDateTime.now()),
                job(JobStatus.SUCCESS.name(), 3000L, LocalDateTime.now().minusHours(1)),
                job(JobStatus.FAILED.name(), 0L, LocalDateTime.now().minusHours(2))));

        RecommendationAnalyticsVO vo = service.dashboard("attraction", 7);

        assertEquals("attraction", vo.getCategory());
        assertEquals(7, vo.getWindowDays());
        assertEquals(100L, vo.getOverview().getTotalExposures());
        assertEquals(20L, vo.getOverview().getUniqueUsers());
        assertEquals(0.8, vo.getOverview().getCoverage(), 1e-9);
        assertEquals(0.10, vo.getOverview().getInteractionRate(), 1e-9);
        assertEquals(0.04, vo.getOverview().getConversionRate(), 1e-9);

        assertEquals(11, vo.getScoreBuckets().size());
        assertEquals(50L, vo.getScoreBuckets().get(3).getCount());
        assertEquals(0L, vo.getScoreBuckets().get(0).getCount());
        assertEquals(1.0, vo.getScoreBuckets().get(10).getRangeEnd(), 1e-9);

        assertEquals(2, vo.getTopItems().size());
        assertEquals(1L, vo.getTopItems().get(0).getItemId());
        assertEquals(60L, vo.getTopItems().get(0).getExposures());
        assertEquals("湖景", vo.getTopItems().get(0).getName());

        assertEquals(3L, vo.getJobStats().getTotal());
        assertEquals(2L, vo.getJobStats().getSuccess());
        assertEquals(1L, vo.getJobStats().getFailed());
        assertEquals(2000.0, vo.getJobStats().getAvgDurationMs(), 1e-9);
        assertEquals(JobStatus.SUCCESS.name(), vo.getJobStats().getLastStatus());
    }

    @Test
    void dashboard_allCategory_sumsFourCategories() {
        when(configService.getInt(isNull(), eq(RecommendationDefaults.EXPOSURE_ATTRIBUTION_HOURS)))
                .thenReturn(72);
        when(analyticsMapper.countExposures(isNull(), any())).thenReturn(400L);
        when(analyticsMapper.countExposureUsers(isNull(), any())).thenReturn(80L);
        when(analyticsMapper.countExposureItems(isNull(), any())).thenReturn(40L);
        when(analyticsMapper.countAttributedInteractions(any(), any(), anyInt())).thenReturn(5L);
        when(analyticsMapper.countAttributedConversions(any(), any(), anyInt())).thenReturn(2L);
        when(analyticsMapper.selectScoreBuckets(isNull(), any())).thenReturn(List.of());
        when(analyticsMapper.selectTopItems(isNull(), any(), anyInt())).thenReturn(List.of());
        when(catalog.countActive(any())).thenReturn(10L);
        when(jobLogMapper.selectList(any())).thenReturn(List.of());

        RecommendationAnalyticsVO vo = service.dashboard(null, null);

        assertEquals("ALL", vo.getCategory());
        assertEquals(7, vo.getWindowDays());
        assertEquals(40L, vo.getOverview().getCatalogSize());
        // 归因指标对四个分类各调用一次并累加：互动 5*4=20，转化 2*4=8
        verify(analyticsMapper, times(4)).countAttributedInteractions(any(), any(), eq(72));
        verify(analyticsMapper, times(4)).countAttributedConversions(any(), any(), eq(72));
        assertEquals(0.05, vo.getOverview().getInteractionRate(), 1e-9);
        assertEquals(0.02, vo.getOverview().getConversionRate(), 1e-9);
        assertEquals(1.0, vo.getOverview().getCoverage(), 1e-9);
        // ALL 视图不做名称装配
        assertEquals(0, vo.getTopItems().size());
    }

    @Test
    void dashboard_invalidCategory_throws400() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.dashboard("not-a-category", 7));
        assertEquals(400, ex.getCode());
    }

    @Test
    void dashboard_windowClampedTo90() {
        when(configService.getInt(isNull(), anyString())).thenReturn(72);
        when(jobLogMapper.selectList(any())).thenReturn(List.of());

        RecommendationAnalyticsVO vo = service.dashboard("hotel", 999);

        assertEquals(90, vo.getWindowDays());
    }

    @Test
    void dashboard_zeroExposures_ratesZero() {
        when(configService.getInt(isNull(), anyString())).thenReturn(72);
        when(jobLogMapper.selectList(any())).thenReturn(List.of());

        RecommendationAnalyticsVO vo = service.dashboard("hotel", 7);

        assertEquals(0.0, vo.getOverview().getCoverage(), 0.0);
        assertEquals(0.0, vo.getOverview().getInteractionRate(), 0.0);
        assertEquals(0.0, vo.getOverview().getConversionRate(), 0.0);
    }

    @Test
    void purgeExpired_batchesUntilShort() {
        when(configService.getInt(isNull(), eq(RecommendationDefaults.RETENTION_DAYS))).thenReturn(30);
        when(exposureLogMapper.purgeBefore(any(), anyInt()))
                .thenReturn(1000)
                .thenReturn(500);

        int deleted = service.purgeExpired();

        assertEquals(1500, deleted);
        verify(exposureLogMapper, times(2)).purgeBefore(any(), eq(1000));
    }

    @Test
    void purgeExpired_nothingToDelete_singleCall() {
        when(configService.getInt(isNull(), eq(RecommendationDefaults.RETENTION_DAYS))).thenReturn(30);
        when(exposureLogMapper.purgeBefore(any(), anyInt())).thenReturn(0);

        int deleted = service.purgeExpired();

        assertEquals(0, deleted);
        verify(exposureLogMapper, times(1)).purgeBefore(any(), anyInt());
    }
}
