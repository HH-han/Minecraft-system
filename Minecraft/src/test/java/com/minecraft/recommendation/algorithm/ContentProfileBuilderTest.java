package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ItemFeatures;
import com.minecraft.recommendation.algorithm.model.UserBehavior;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentProfileBuilderTest {

    private static final double EPS = 1e-12;

    private ItemFeatures item(long id, String tags, String city, String subType) {
        return ItemFeatures.builder()
                .itemId(id).category("attraction")
                .tags(FeatureTokenizer.tokenize(tags))
                .city(city).subType(subType)
                .active(true).build();
    }

    @Test
    void itemVector_namespacedTokens() {
        Map<String, Double> v = ContentProfileBuilder.itemVector(item(1L, "亲子,海滨", "三亚", "自然"));
        assertEquals(1.0, v.get("tag:亲子"), EPS);
        assertEquals(1.0, v.get("tag:海滨"), EPS);
        assertEquals(1.0, v.get("city:三亚"), EPS);
        assertEquals(1.0, v.get("type:自然"), EPS);
    }

    @Test
    void userVector_accumulatesWeightedTags() {
        ItemFeatures a = item(1L, "亲子", "三亚", "");
        ItemFeatures b = item(2L, "亲子,美食", "海口", "");
        LocalDateTime now = LocalDateTime.now();
        List<UserBehavior> behaviors = List.of(
                new UserBehavior(1L, 3.0, now),
                new UserBehavior(2L, 1.0, now));
        Map<String, Double> uv = ContentProfileBuilder.userVector(behaviors, Map.of(1L, a, 2L, b));
        // 亲子被两个物品累积（3+1），美食仅 1
        assertEquals(4.0, uv.get("tag:亲子"), EPS);
        assertEquals(1.0, uv.get("tag:美食"), EPS);
    }

    @Test
    void userVector_emptyOrMissingItems_safe() {
        assertTrue(ContentProfileBuilder.userVector(null, Map.of()).isEmpty());
        List<UserBehavior> behaviors = List.of(new UserBehavior(99L, 1.0, LocalDateTime.now()));
        assertTrue(ContentProfileBuilder.userVector(behaviors, Map.of()).isEmpty());
    }

    @Test
    void contentScore_tagAffinityOrdersMatches() {
        ItemFeatures liked = item(1L, "亲子,海滨", "三亚", "");
        ItemFeatures match = item(2L, "亲子,海滨", "三亚", "");
        ItemFeatures unrelated = item(3L, "历史,古镇", "西安", "");
        Map<String, Double> uv = ContentProfileBuilder.userVector(
                List.of(new UserBehavior(1L, 3.0, LocalDateTime.now())), Map.of(1L, liked));

        double scoreMatch = ContentProfileBuilder.contentScore(uv, match);
        double scoreOther = ContentProfileBuilder.contentScore(uv, unrelated);
        assertEquals(1.0, scoreMatch, EPS);
        assertEquals(0.0, scoreOther, EPS);
    }

    @Test
    void contentScore_coldUser_isZero() {
        assertEquals(0.0, ContentProfileBuilder.contentScore(Map.of(), item(2L, "亲子", "三亚", "")), EPS);
        assertEquals(0.0, ContentProfileBuilder.contentScore(null, item(2L, "亲子", "三亚", "")), EPS);
    }

    @Test
    void populationAffinity_matchesPopularTags() {
        ItemFeatures item = item(5L, "亲子", "三亚", "");
        // 物品向量 {tag:亲子, city:三亚} 与目录流行度向量完全同向（等权）→ 1
        Map<String, Double> aligned = Map.of("tag:亲子", 10.0, "city:三亚", 10.0);
        assertEquals(1.0, ContentProfileBuilder.populationAffinity(item, aligned), EPS);
        // 目录中该物品的标签都冷门 → 亲和度低
        Map<String, Double> dominated = Map.of("tag:历史", 50.0, "city:西安", 40.0,
                "tag:亲子", 1.0, "city:三亚", 1.0);
        assertTrue(ContentProfileBuilder.populationAffinity(item, dominated) < 0.2);
        assertEquals(0.0, ContentProfileBuilder.populationAffinity(item, Map.of()), EPS);
    }
}
