package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.RecommendationExposureLog;
import com.minecraft.mapper.RecommendationExposureLogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationExposureAsyncWriterTest {

    @Mock
    private RecommendationExposureLogMapper exposureLogMapper;
    @InjectMocks
    private RecommendationExposureAsyncWriter writer;

    @Test
    void capture_batchInserted() {
        List<RecommendationExposureLog> rows = List.of(new RecommendationExposureLog());
        writer.capture(rows);
        verify(exposureLogMapper).batchInsert(rows);
    }

    @Test
    void capture_emptySkipped() {
        writer.capture(List.of());
        writer.capture(null);
        verify(exposureLogMapper, never()).batchInsert(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void capture_dbFailure_swallowed() {
        List<RecommendationExposureLog> rows = List.of(new RecommendationExposureLog());
        when(exposureLogMapper.batchInsert(rows)).thenThrow(new RuntimeException("db error"));
        assertDoesNotThrow(() -> writer.capture(rows));
    }
}
