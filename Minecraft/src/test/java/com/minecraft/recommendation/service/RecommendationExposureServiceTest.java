package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.RecommendationExposureLog;
import com.minecraft.mapper.RecommendationExposureLogMapper;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.enums.ExposureSource;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.RecommendationItemVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationExposureServiceTest {

    @Mock
    private RecommendationConfigService configService;
    @Mock
    private RecommendationExposureAsyncWriter asyncWriter;
    @InjectMocks
    private RecommendationExposureService exposureService;

    private RecommendationItemVO vo(long itemId, int rank) {
        RecommendationItemVO vo = new RecommendationItemVO();
        vo.setItemId(itemId);
        vo.setScore(BigDecimal.valueOf(0.8 - rank * 0.01));
        vo.setRankPosition(rank);
        return vo;
    }

    @Test
    void enabled_fullSample_rowsWrittenAsync() {
        when(configService.getBoolean(RecommendationDefaults.EXPOSURE_ENABLED)).thenReturn(true);
        when(configService.getDouble(isNull(), eq(RecommendationDefaults.EXPOSURE_SAMPLE_RATE))).thenReturn(1.0);

        exposureService.captureExposure(RecommendCategory.ATTRACTION, 7L, ExposureSource.PERSONALIZED,
                "req-1", List.of(vo(1L, 1), vo(2L, 2)));

        ArgumentCaptor<List<RecommendationExposureLog>> captor = ArgumentCaptor.forClass(List.class);
        verify(asyncWriter).capture(captor.capture());
        List<RecommendationExposureLog> rows = captor.getValue();
        assertEquals(2, rows.size());
        RecommendationExposureLog first = rows.get(0);
        assertEquals(7L, first.getUserId());
        assertEquals("attraction", first.getCategory());
        assertEquals(1L, first.getItemId());
        assertEquals(1, first.getRankPosition());
        assertEquals("PERSONALIZED", first.getSource());
        assertEquals("req-1", first.getRequestId());
        assertEquals(2L, rows.get(1).getItemId());
    }

    @Test
    void disabled_nothingWritten() {
        when(configService.getBoolean(RecommendationDefaults.EXPOSURE_ENABLED)).thenReturn(false);

        exposureService.captureExposure(RecommendCategory.HOTEL, null, ExposureSource.POPULAR,
                "req-2", List.of(vo(1L, 1)));

        verify(asyncWriter, never()).capture(any());
    }

    @Test
    void sampleRateZero_nothingWritten() {
        when(configService.getBoolean(RecommendationDefaults.EXPOSURE_ENABLED)).thenReturn(true);
        when(configService.getDouble(isNull(), eq(RecommendationDefaults.EXPOSURE_SAMPLE_RATE))).thenReturn(0.0);

        exposureService.captureExposure(RecommendCategory.HOTEL, null, ExposureSource.POPULAR,
                "req-3", List.of(vo(1L, 1)));

        verify(asyncWriter, never()).capture(any());
    }

    @Test
    void emptyOrNullInput_nothingWritten() {
        exposureService.captureExposure(RecommendCategory.HOTEL, null, ExposureSource.POPULAR,
                "req-4", List.of());
        exposureService.captureExposure(null, null, ExposureSource.POPULAR, "req-5", List.of(vo(1L, 1)));
        verify(asyncWriter, never()).capture(any());
    }

    @Test
    void configThrows_swallowedAndNoPropagation() {
        when(configService.getBoolean(RecommendationDefaults.EXPOSURE_ENABLED))
                .thenThrow(new RuntimeException("redis down"));

        assertDoesNotThrow(() -> exposureService.captureExposure(
                RecommendCategory.HOTEL, 1L, ExposureSource.PERSONALIZED, "req-6", List.of(vo(1L, 1))));
    }
}
