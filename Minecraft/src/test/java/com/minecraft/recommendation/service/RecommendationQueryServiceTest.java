package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.AttractionRecommendation;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationAggregationMapper;
import com.minecraft.mapper.RecommendationItemSimilarityMapper;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.algorithm.StrategyParameters;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.dto.UserItemInteraction;
import com.minecraft.recommendation.enums.ExposureSource;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.enums.RecommendStrategy;
import com.minecraft.recommendation.vo.RecommendationResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationQueryServiceTest {

    @Mock
    private RecommendationMapperRegistry registry;
    @Mock
    private RecommendationItemCatalog catalog;
    @Mock
    private RecommendationAggregationMapper aggregationMapper;
    @Mock
    private RecommendationItemSimilarityMapper similarityMapper;
    @Mock
    private RecommendationConfigService configService;
    @Mock
    private RecommendationExposureService exposureService;

    @InjectMocks
    private RecommendationQueryService service;

    private final Map<Long, ItemDetail> sourceItems = new HashMap<>();

    @BeforeEach
    void setUp() {
        when(configService.strategyParameters(any())).thenReturn(StrategyParameters.of(
                RecommendStrategy.HYBRID, 0.25, 0.25, 0.20, 0.10, 0.20, 20, 0.3, 0.15));
        when(configService.getInt(isNull(), eq(RecommendationDefaults.BEHAVIOR_RECENCY_DAYS))).thenReturn(90);
        when(configService.getDouble(isNull(), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_LIKE))).thenReturn(1.0);
        when(configService.getDouble(isNull(), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_COLLECT))).thenReturn(3.0);
        when(configService.getDouble(isNull(), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_COMMENT))).thenReturn(2.0);
        when(configService.getDouble(isNull(), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_CART))).thenReturn(4.0);
        when(configService.getDouble(isNull(), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_ORDER))).thenReturn(6.0);

        // catalog.load 始终以“主数据中存在且被请求”为准返回
        when(catalog.load(any(), any())).thenAnswer(inv -> {
            Collection<Long> ids = inv.getArgument(1);
            Map<Long, ItemDetail> result = new HashMap<>();
            for (Long id : ids) {
                ItemDetail d = sourceItems.get(id);
                if (d != null) {
                    result.put(id, d);
                }
            }
            return result;
        });
    }

    private BaseRecommendationItem row(long itemId, double score) {
        return row(itemId, score, false, 0.0);
    }

    private BaseRecommendationItem row(long itemId, double score, boolean featured, double featureWeight) {
        AttractionRecommendation r = new AttractionRecommendation();
        r.setItemId(itemId);
        r.setRecommendationScore(BigDecimal.valueOf(score));
        r.setPopularityIndex(BigDecimal.valueOf(0.5));
        r.setQualityScore(BigDecimal.valueOf(0.8));
        r.setCollaborativeScore(BigDecimal.ZERO);
        r.setContentScore(BigDecimal.ZERO);
        r.setSeasonalScore(BigDecimal.valueOf(0.5));
        r.setIsFeatured(featured);
        r.setFeatureWeight(BigDecimal.valueOf(featureWeight));
        return r;
    }

    private ItemDetail item(long id, String name, List<String> tags, String city) {
        ItemDetail d = new ItemDetail();
        d.setItemId(id);
        d.setName(name);
        d.setCity(city);
        d.setTags(tags);
        d.setStatus(1);
        d.setRating(5);
        sourceItems.put(id, d);
        return d;
    }

    @Test
    void anonymousList_rankedByGlobalScore_andExposureCaptured() {
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt()))
                .thenReturn(new ArrayList<>(List.of(row(1L, 0.80), row(2L, 0.60))));
        item(1L, "湖景", List.of("lake"), "北京");
        item(2L, "山景", List.of("mountain"), "北京");

        RecommendationResultVO result = service.list(RecommendCategory.ATTRACTION, null, 10, null, null);

        assertEquals("POPULAR", result.getSource());
        assertEquals("HYBRID", result.getStrategy());
        assertEquals(2, result.getItems().size());
        assertEquals(1L, result.getItems().get(0).getItemId());
        assertEquals(1, result.getItems().get(0).getRankPosition());
        assertEquals("湖景", result.getItems().get(0).getName());
        verify(aggregationMapper, never()).selectUserInteractions(any(), anyLong(), any());
        verify(exposureService).captureExposure(eq(RecommendCategory.ATTRACTION), isNull(),
                eq(ExposureSource.POPULAR), any(), any());
    }

    @Test
    void anonymousList_cityFilterApplied() {
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt()))
                .thenReturn(new ArrayList<>(List.of(row(1L, 0.80), row(2L, 0.60))));
        item(1L, "北京景点", List.of("lake"), "北京");
        item(2L, "上海景点", List.of("lake"), "上海");

        RecommendationResultVO result = service.list(RecommendCategory.ATTRACTION, null, 10, "上海", null);

        assertEquals(1, result.getItems().size());
        assertEquals(2L, result.getItems().get(0).getItemId());
    }

    @Test
    void loggedInUser_withoutInteractions_coldStart() {
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt()))
                .thenReturn(new ArrayList<>(List.of(row(1L, 0.80))));
        item(1L, "湖景", List.of("lake"), "北京");
        when(aggregationMapper.selectUserInteractions(any(), eq(7L), any()))
                .thenReturn(List.of());

        RecommendationResultVO result = service.list(RecommendCategory.ATTRACTION, 7L, 10, null, null);

        assertEquals("COLD_START", result.getSource());
        verify(exposureService).captureExposure(eq(RecommendCategory.ATTRACTION), eq(7L),
                eq(ExposureSource.COLD_START), any(), any());
    }

    @Test
    void loggedInUser_withCoInteraction_personalized() {
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt()))
                .thenReturn(new ArrayList<>(List.of(row(1L, 0.50), row(2L, 0.90))));
        item(1L, "湖景A", List.of("lake"), "北京");
        item(2L, "山景B", List.of("mountain"), "北京");
        item(99L, "用户互动过的湖景", List.of("lake"), "北京");

        UserItemInteraction interaction = new UserItemInteraction();
        interaction.setItemId(99L);
        interaction.setActionType("ORDER");
        interaction.setActionTime(LocalDateTime.now());
        when(aggregationMapper.selectUserInteractions(eq("attraction"), eq(7L), any()))
                .thenReturn(List.of(interaction));
        when(similarityMapper.selectNeighborsByItems(eq("attraction"), any()))
                .thenReturn(List.of(edge(1L, 99L, 0.9)));

        RecommendationResultVO result = service.list(RecommendCategory.ATTRACTION, 7L, 10, null, null);

        assertEquals("PERSONALIZED", result.getSource());
        // item1 全局分低但有协同 0.9 + 内容余弦 1，个性化后应反超 item2
        assertEquals(1L, result.getItems().get(0).getItemId());
        verify(similarityMapper).selectNeighborsByItems(eq("attraction"), any());
    }

    @Test
    void limit_clampedToMax100() {
        List<BaseRecommendationItem> rows = new ArrayList<>();
        for (long i = 1; i <= 150; i++) {
            rows.add(row(i, 1.0 - i / 1000.0));
            item(i, "景点" + i, List.of("tag" + (i % 5)), "北京");
        }
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt())).thenReturn(rows);

        RecommendationResultVO result = service.list(RecommendCategory.ATTRACTION, null, 999, null, null);

        assertEquals(100, result.getItems().size());
    }

    @Test
    void limit_nullDefaultsTo10() {
        List<BaseRecommendationItem> rows = new ArrayList<>();
        for (long i = 1; i <= 20; i++) {
            rows.add(row(i, 1.0 - i / 1000.0));
            item(i, "景点" + i, List.of(), "北京");
        }
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt())).thenReturn(rows);

        RecommendationResultVO result = service.list(RecommendCategory.ATTRACTION, null, null, null, null);

        assertEquals(10, result.getItems().size());
    }

    @Test
    void related_bySimilarityEdges() {
        when(catalog.exists(RecommendCategory.HOTEL, 5L)).thenReturn(true);
        when(similarityMapper.selectNeighbors(eq("hotel"), eq(5L), anyInt()))
                .thenReturn(List.of(edge(5L, 6L, 0.8), edge(5L, 7L, 0.6)));
        item(6L, "相似酒店A", List.of("pool"), "北京");
        item(7L, "相似酒店B", List.of("gym"), "北京");

        RecommendationResultVO result = service.related(RecommendCategory.HOTEL, 5L, 10);

        assertEquals("RELATED", result.getSource());
        assertEquals(2, result.getItems().size());
        assertEquals(6L, result.getItems().get(0).getItemId());
        assertEquals(1, result.getItems().get(0).getRankPosition());
        assertEquals("相似酒店A", result.getItems().get(0).getName());
        // 相关推荐不记曝光
        verify(exposureService, never()).captureExposure(any(), any(), any(), any(), any());
    }

    @Test
    void related_inactiveEdge_fallsBackToTagOverlap() {
        // seed 自身详情（标签 lake），用于 Jaccard
        item(1L, "种子", List.of("lake"), "北京");
        when(catalog.exists(RecommendCategory.ATTRACTION, 1L)).thenReturn(true);
        when(similarityMapper.selectNeighbors(eq("attraction"), eq(1L), anyInt()))
                .thenReturn(List.of(edge(1L, 2L, 0.9)));
        // 2 已下架 -> 边结果被剔除
        ItemDetail inactive = item(2L, "下架", List.of("lake"), "北京");
        inactive.setStatus(0);
        // 兜底候选
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt()))
                .thenReturn(new ArrayList<>(List.of(row(3L, 0.7), row(4L, 0.9))));
        item(3L, "同标签景点", List.of("lake"), "北京");
        item(4L, "不同标签景点", List.of("desert"), "北京");

        RecommendationResultVO result = service.related(RecommendCategory.ATTRACTION, 1L, 10);

        assertEquals(2, result.getItems().size());
        assertEquals(3L, result.getItems().get(0).getItemId());
        assertEquals(4L, result.getItems().get(1).getItemId());
    }

    @Test
    void related_itemMissing_throws404() {
        when(catalog.exists(RecommendCategory.RESTAURANT, 404L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.related(RecommendCategory.RESTAURANT, 404L, 10));
        assertEquals(404, ex.getCode());
    }

    @Test
    void emptyCatalog_returnsEmptyResult() {
        when(registry.selectTopScored(eq(RecommendCategory.ATTRACTION), anyInt()))
                .thenReturn(List.of());

        RecommendationResultVO result = service.list(RecommendCategory.ATTRACTION, null, 10, null, null);

        assertTrue(result.getItems().isEmpty());
        assertEquals("POPULAR", result.getSource());
    }

    private com.minecraft.entity.recommendation.RecommendationItemSimilarity edge(long itemId,
                                                                                    long neighborId,
                                                                                    double sim) {
        com.minecraft.entity.recommendation.RecommendationItemSimilarity e =
                new com.minecraft.entity.recommendation.RecommendationItemSimilarity();
        e.setItemId(itemId);
        e.setNeighborId(neighborId);
        e.setSimilarity(BigDecimal.valueOf(sim));
        return e;
    }
}
