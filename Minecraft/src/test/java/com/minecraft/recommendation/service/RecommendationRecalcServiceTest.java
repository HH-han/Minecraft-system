package com.minecraft.recommendation.service;

import com.minecraft.entity.Attraction;
import com.minecraft.entity.recommendation.AttractionRecommendation;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.entity.recommendation.RecommendationItemSimilarity;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AttractionMapper;
import com.minecraft.mapper.AttractionsRecommendationMapper;
import com.minecraft.mapper.FoodMapper;
import com.minecraft.mapper.HotelMapper;
import com.minecraft.mapper.ProductMapper;
import com.minecraft.mapper.RecommendationAggregationMapper;
import com.minecraft.mapper.RecommendationItemSimilarityMapper;
import com.minecraft.mapper.RecommendationJobLogMapper;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.algorithm.StrategyParameters;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.dto.RecalcResult;
import com.minecraft.recommendation.dto.UserItemInteraction;
import com.minecraft.recommendation.enums.JobStatus;
import com.minecraft.recommendation.enums.JobTriggerType;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.enums.RecommendStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationRecalcServiceTest {

    @Mock private RecommendationConfigService configService;
    @Mock private RecommendationAggregationMapper aggregationMapper;
    @Mock private RecommendationItemSimilarityMapper similarityMapper;
    @Mock private RecommendationJobLogMapper jobLogMapper;
    @Mock private AttractionMapper attractionMapper;
    @Mock private HotelMapper hotelMapper;
    @Mock private FoodMapper foodMapper;
    @Mock private ProductMapper productMapper;
    @Mock private AttractionsRecommendationMapper attractionRecMapper;
    @Mock private com.minecraft.mapper.HotelsRecommendationMapper hotelRecMapper;
    @Mock private com.minecraft.mapper.RestaurantsRecommendationMapper restaurantRecMapper;
    @Mock private com.minecraft.mapper.SouvenirsRecommendationMapper souvenirRecMapper;

    private RecommendationRecalcService service;
    private long jobLogSeq = 100L;

    @BeforeEach
    void setUp() {
        RecommendationMapperRegistry registry = new RecommendationMapperRegistry(
                attractionRecMapper, hotelRecMapper, restaurantRecMapper, souvenirRecMapper);

        service = new RecommendationRecalcService();
        ReflectionTestUtils.setField(service, "configService", configService);
        ReflectionTestUtils.setField(service, "aggregationMapper", aggregationMapper);
        ReflectionTestUtils.setField(service, "similarityMapper", similarityMapper);
        ReflectionTestUtils.setField(service, "jobLogMapper", jobLogMapper);
        ReflectionTestUtils.setField(service, "registry", registry);
        ReflectionTestUtils.setField(service, "attractionMapper", attractionMapper);
        ReflectionTestUtils.setField(service, "hotelMapper", hotelMapper);
        ReflectionTestUtils.setField(service, "foodMapper", foodMapper);
        ReflectionTestUtils.setField(service, "productMapper", productMapper);
        // redisTemplate 保持 null，跳过缓存清理

        when(configService.getInt(eq(null), eq(RecommendationDefaults.CF_NEIGHBOR_K))).thenReturn(20);
        when(configService.getInt(eq(null), eq(RecommendationDefaults.BEHAVIOR_RECENCY_DAYS))).thenReturn(90);
        when(configService.getDouble(eq(null), eq(RecommendationDefaults.POPULARITY_LOG_BASE))).thenReturn(10.0);
        when(configService.getDouble(eq(null), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_LIKE))).thenReturn(1.0);
        when(configService.getDouble(eq(null), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_COLLECT))).thenReturn(3.0);
        when(configService.getDouble(eq(null), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_COMMENT))).thenReturn(2.0);
        when(configService.getDouble(eq(null), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_CART))).thenReturn(4.0);
        when(configService.getDouble(eq(null), eq(RecommendationDefaults.BEHAVIOR_WEIGHT_ORDER))).thenReturn(6.0);
        when(configService.strategyParameters(any())).thenReturn(StrategyParameters.of(
                RecommendStrategy.HYBRID, 0.25, 0.25, 0.20, 0.10, 0.20, 20, 0.3, 0.15));

        when(attractionRecMapper.upsertItems(anyList())).thenAnswer(i -> ((List<?>) i.getArgument(0)).size());
        when(hotelRecMapper.upsertItems(anyList())).thenAnswer(i -> ((List<?>) i.getArgument(0)).size());
        when(restaurantRecMapper.upsertItems(anyList())).thenAnswer(i -> ((List<?>) i.getArgument(0)).size());
        when(souvenirRecMapper.upsertItems(anyList())).thenAnswer(i -> ((List<?>) i.getArgument(0)).size());
        when(similarityMapper.upsertSimilarities(anyList())).thenAnswer(i -> ((List<?>) i.getArgument(0)).size());

        doAnswer(invocation -> {
            RecommendationJobLog log = invocation.getArgument(0);
            log.setId(++jobLogSeq);
            return 1;
        }).when(jobLogMapper).insert(any(RecommendationJobLog.class));
        when(jobLogMapper.updateById(any())).thenReturn(1);
        when(similarityMapper.deleteByCategory(anyString())).thenReturn(0);

        // 聚合查询默认空
        when(aggregationMapper.selectLikes(anyString(), any())).thenReturn(List.of());
        when(aggregationMapper.selectCollections(anyString(), any())).thenReturn(List.of());
        when(aggregationMapper.selectComments(anyString(), any())).thenReturn(List.of());
        when(aggregationMapper.selectCartItems(anyString(), any())).thenReturn(List.of());
        when(aggregationMapper.selectPaidOrders(anyString(), any())).thenReturn(List.of());
        when(aggregationMapper.countCartAll(anyString())).thenReturn(List.of());
        when(aggregationMapper.countPaidOrdersAll(anyString())).thenReturn(List.of());
    }

    private Attraction attraction(long id, int rating, int likes, int collects, int comments, String tags) {
        Attraction a = new Attraction();
        a.setId(id);
        a.setName("景点" + id);
        a.setStatus(1);
        a.setRating(rating);
        a.setLikeCount(likes);
        a.setCollectCount(collects);
        a.setCommentCount(comments);
        a.setTags(tags);
        a.setSeason("四季皆宜");
        a.setCity("三亚");
        return a;
    }

    private UserItemInteraction interaction(long userId, long itemId, LocalDateTime time) {
        UserItemInteraction i = new UserItemInteraction();
        i.setUserId(userId);
        i.setItemId(itemId);
        i.setActionTime(time);
        return i;
    }

    @Test
    void recalculate_success_assemblesScoresReplacesEdgesWritesSuccessLog() {
        when(attractionMapper.selectList(any())).thenReturn(List.of(
                attraction(1L, 5, 100, 50, 20, "亲子,海滨"),
                attraction(2L, 3, 10, 5, 2, "历史,古镇")));
        // 两个用户都对 1、2 有点赞共现 → 双向相似度边
        LocalDateTime recent = LocalDateTime.now().minusDays(1);
        when(aggregationMapper.selectLikes(eq("attraction"), any())).thenReturn(List.of(
                interaction(1L, 1L, recent), interaction(1L, 2L, recent),
                interaction(2L, 1L, recent), interaction(2L, 2L, recent)));

        RecalcResult result = service.recalculate(RecommendCategory.ATTRACTION,
                JobTriggerType.MANUAL, 7L);

        assertEquals(JobStatus.SUCCESS.name(), result.getStatus());
        assertEquals(2, result.getItemCount());
        assertEquals(2, result.getEdgeCount());
        assertTrue(result.getDurationMs() >= 0L);

        // 相似度边整类替换 + 双向写入
        verify(similarityMapper).deleteByCategory("attraction");
        ArgumentCaptor<List<RecommendationItemSimilarity>> edgeCaptor = ArgumentCaptor.forClass(List.class);
        verify(similarityMapper).upsertSimilarities(edgeCaptor.capture());
        List<RecommendationItemSimilarity> edges = edgeCaptor.getValue();
        assertEquals(2, edges.size());
        assertEquals("attraction", edges.get(0).getCategory());

        // upsert 负载：两行李算法列在 [0,1]，人工列必须为空（不覆盖）
        ArgumentCaptor<List<AttractionRecommendation>> rowCaptor = ArgumentCaptor.forClass(List.class);
        verify(attractionRecMapper).upsertItems(rowCaptor.capture());
        List<AttractionRecommendation> rows = rowCaptor.getValue();
        assertEquals(2, rows.size());
        for (BaseRecommendationItem row : rows) {
            assertNull(row.getIsFeatured(), "upsert 不得写人工列 is_featured");
            assertNull(row.getStatus(), "upsert 不得写人工列 status");
            assertNull(row.getFeatureWeight(), "upsert 不得写人工列 feature_weight");
            assertScoreRange(row);
        }
        // 高热度高评分的物品 1 综合分更高
        AttractionRecommendation r1 = rows.stream().filter(r -> r.getItemId() == 1L).findFirst().orElseThrow();
        AttractionRecommendation r2 = rows.stream().filter(r -> r.getItemId() == 2L).findFirst().orElseThrow();
        assertTrue(r1.getRecommendationScore().doubleValue() >= r2.getRecommendationScore().doubleValue());
        // 四季皆宜 → 季节分 1
        assertEquals(1.0, r1.getSeasonalScore().doubleValue(), 1e-9);

        // 任务日志 RUNNING -> SUCCESS，operator 透传
        ArgumentCaptor<RecommendationJobLog> logCaptor = ArgumentCaptor.forClass(RecommendationJobLog.class);
        verify(jobLogMapper).insert(logCaptor.capture());
        assertEquals(JobStatus.RUNNING.name(), logCaptor.getValue().getStatus());
        assertEquals(7L, logCaptor.getValue().getOperatorId());
        verify(jobLogMapper).updateById(argThatStatus(JobStatus.SUCCESS, 2));
    }

    @Test
    void recalculate_emptyCatalog_stillSuccessAndReplacesEdges() {
        when(attractionMapper.selectList(any())).thenReturn(List.of());

        RecalcResult result = service.recalculate(RecommendCategory.ATTRACTION,
                JobTriggerType.SYSTEM, null);

        assertEquals(JobStatus.SUCCESS.name(), result.getStatus());
        assertEquals(0, result.getItemCount());
        verify(similarityMapper).deleteByCategory("attraction");
        verify(attractionRecMapper, never()).upsertItems(anyList());
        verify(jobLogMapper).updateById(argThatStatus(JobStatus.SUCCESS, 0));
    }

    @Test
    void recalculate_manualFailure_writesFailedLogAndRethrows500() {
        when(attractionMapper.selectList(any())).thenThrow(new RuntimeException("db down"));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.recalculate(RecommendCategory.ATTRACTION, JobTriggerType.MANUAL, 7L));
        assertEquals(500, ex.getCode());
        verify(jobLogMapper).updateById(argThatStatus(JobStatus.FAILED, 0));
        verify(attractionRecMapper, never()).upsertItems(anyList());
    }

    @Test
    void recalculate_systemFailure_returnsFailedWithoutThrowing() {
        when(attractionMapper.selectList(any())).thenThrow(new RuntimeException("db down"));

        RecalcResult result = service.recalculate(RecommendCategory.ATTRACTION,
                JobTriggerType.SYSTEM, null);
        assertEquals(JobStatus.FAILED.name(), result.getStatus());
        assertTrue(result.getErrorMessage().contains("db down"));
    }

    @Test
    void recalculate_null_runsAllFourCategoriesFixedOrder() {
        when(attractionMapper.selectList(any())).thenReturn(List.of());
        when(hotelMapper.selectList(any())).thenReturn(List.of());
        when(foodMapper.selectList(any())).thenReturn(List.of());
        when(productMapper.selectList(any())).thenReturn(List.of());

        RecalcResult result = service.recalculate(null, JobTriggerType.MANUAL, 1L);

        assertEquals(RecommendationRecalcService.ALL, result.getCategory());
        assertEquals(JobStatus.SUCCESS.name(), result.getStatus());
        // ALL + 4 分类 = 5 条日志
        verify(jobLogMapper, times(5)).insert(any(RecommendationJobLog.class));
        verify(jobLogMapper, times(5)).updateById(any());
        verify(similarityMapper).deleteByCategory("attraction");
        verify(similarityMapper).deleteByCategory("hotel");
        verify(similarityMapper).deleteByCategory("food");
        verify(similarityMapper).deleteByCategory("product");
    }

    @Test
    void recalculateAll_manualCategoryFailure_failsAndStops() {
        when(attractionMapper.selectList(any())).thenReturn(List.of());
        when(foodMapper.selectList(any())).thenThrow(new RuntimeException("boom"));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.recalculateAll(JobTriggerType.MANUAL, 1L));
        assertEquals(500, ex.getCode());
        // 顺序 attraction → hotel → food(异常) 停止；product 不再执行
        verify(similarityMapper).deleteByCategory("attraction");
        verify(similarityMapper).deleteByCategory("hotel");
        verify(similarityMapper, never()).deleteByCategory("product");
    }

    @Test
    void concurrentInvocation_rejectedWith409() throws InterruptedException {
        java.util.concurrent.CountDownLatch insideQuery = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
        when(attractionMapper.selectList(any())).thenAnswer(inv -> {
            insideQuery.countDown();
            release.await();
            return List.of(attraction(1L, 5, 1, 1, 1, "亲子"));
        });

        Thread worker = new Thread(() -> service.recalculate(RecommendCategory.ATTRACTION,
                JobTriggerType.SYSTEM, null));
        worker.start();
        insideQuery.await();
        try {
            BusinessException ex = assertThrows(BusinessException.class, () ->
                    service.recalculate(RecommendCategory.ATTRACTION, JobTriggerType.MANUAL, 1L));
            assertEquals(409, ex.getCode());
        } finally {
            release.countDown();
            worker.join();
        }
    }

    private static void assertScoreRange(BaseRecommendationItem row) {
        assertBetween0And1(row.getRecommendationScore());
        assertBetween0And1(row.getUserPreferenceMatching());
        assertBetween0And1(row.getPopularityIndex());
        assertBetween0And1(row.getCollaborativeScore());
        assertBetween0And1(row.getContentScore());
        assertBetween0And1(row.getSeasonalScore());
        assertBetween0And1(row.getQualityScore());
    }

    private static void assertBetween0And1(BigDecimal v) {
        assertTrue(v.doubleValue() >= 0.0 && v.doubleValue() <= 1.0,
                "score out of range: " + v);
    }

    private static RecommendationJobLog argThatStatus(JobStatus status, int itemCount) {
        return org.mockito.ArgumentMatchers.argThat(log ->
                log != null
                        && status.name().equals(log.getStatus())
                        && log.getItemCount() != null
                        && log.getItemCount() == itemCount
                        && log.getDurationMs() != null
                        && log.getEndTime() != null);
    }
}
