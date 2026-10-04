package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ItemFeatures;
import com.minecraft.recommendation.algorithm.model.UserBehavior;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容画像构建器。
 * <p>
 * 物品向量 = 标签 + 城市 + 子类型 token（各权重 1）；
 * 用户兴趣向量 = 历史交互物品向量按行为权重（含时间衰减）累加。
 * 内容匹配分 = 两稀疏向量余弦（{@link Normalizers#cosine}）。
 */
public final class ContentProfileBuilder {

    private ContentProfileBuilder() {
    }

    /**
     * 构建物品内容 token 向量。
     */
    public static Map<String, Double> itemVector(ItemFeatures item) {
        Map<String, Double> vector = new HashMap<>();
        if (item.getTags() != null) {
            item.getTags().forEach(t -> vector.merge(token("tag", t), 1.0, Double::sum));
        }
        if (item.getCity() != null && !item.getCity().isBlank()) {
            vector.put(token("city", item.getCity().trim().toLowerCase()), 1.0);
        }
        if (item.getSubType() != null && !item.getSubType().isBlank()) {
            vector.put(token("type", item.getSubType().trim().toLowerCase()), 1.0);
        }
        return vector;
    }

    /**
     * 基于用户历史构建兴趣向量。itemIndex 缺失物品的行为被跳过。
     */
    public static Map<String, Double> userVector(List<UserBehavior> behaviors,
                                                 Map<Long, ItemFeatures> itemIndex) {
        Map<String, Double> vector = new HashMap<>();
        if (behaviors == null || itemIndex == null) {
            return vector;
        }
        for (UserBehavior behavior : behaviors) {
            ItemFeatures item = itemIndex.get(behavior.itemId());
            if (item == null || behavior.weight() <= 0.0) {
                continue;
            }
            for (String token : itemVector(item).keySet()) {
                vector.merge(token, behavior.weight(), Double::sum);
            }
        }
        return vector;
    }

    /**
     * 用户对物品的内容匹配分 [0,1]。
     */
    public static double contentScore(Map<String, Double> userVector, ItemFeatures item) {
        if (userVector == null || userVector.isEmpty()) {
            return 0.0;
        }
        return Normalizers.cosine(userVector, itemVector(item));
    }

    /**
     * 群体亲和代理：离线重算时无特定用户，使用「token 流行度向量」
     * （每个 token 按物品自身以外的全局出现次数加权）与物品向量的余弦，
     * 作为 user_preference_matching 列的内容侧口径。
     */
    public static double populationAffinity(ItemFeatures item, Map<String, Double> tokenPopularity) {
        if (tokenPopularity == null || tokenPopularity.isEmpty()) {
            return 0.0;
        }
        Map<String, Double> vector = itemVector(item);
        if (vector.isEmpty()) {
            return 0.0;
        }
        // 直接与全量 token 流行度向量比较：物品 token 在目录中越冷门，
        // 非重叠热门 token 越多，余弦越低（稀疏重叠由 cosine 处理）。
        return Normalizers.cosine(vector, tokenPopularity);
    }

    private static String token(String namespace, String raw) {
        return namespace + ":" + raw;
    }
}
