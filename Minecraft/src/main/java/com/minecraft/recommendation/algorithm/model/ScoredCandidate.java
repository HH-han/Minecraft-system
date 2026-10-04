package com.minecraft.recommendation.algorithm.model;

import lombok.Getter;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * 待排序候选物品：携带五个因子分量、全局基线分与人工干预标记，
 * 排序后 {@link #finalScore} 写入最终得分。
 */
@Getter
public class ScoredCandidate {

    private final long itemId;

    private final Set<String> tags;

    /** 各因子分量，缺省 0.0，取值 [0,1] */
    private final Map<ScoreComponent, Double> components = new EnumMap<>(ScoreComponent.class);

    /** 全局（匿名）基线综合分，由离线重算写入，[0,1] */
    private double baseScore;

    /** 人工置顶：置顶项固定排在最前 */
    private boolean featured;

    /** 人工加权系数 [0,1]，对非置顶但加权的物品生效 */
    private double featureWeight;

    /** 运营排除：排序前剔除 */
    private boolean excluded;

    /** 排序后的最终得分 */
    private double finalScore;

    public ScoredCandidate(long itemId, Set<String> tags) {
        this.itemId = itemId;
        this.tags = tags == null ? Set.of() : Collections.unmodifiableSet(tags);
    }

    public ScoredCandidate component(ScoreComponent component, double value) {
        components.put(component, clamp01(value));
        return this;
    }

    public double getComponent(ScoreComponent component) {
        return components.getOrDefault(component, 0.0);
    }

    public ScoredCandidate baseScore(double baseScore) {
        this.baseScore = clamp01(baseScore);
        return this;
    }

    public ScoredCandidate featured(boolean featured) {
        this.featured = featured;
        return this;
    }

    public ScoredCandidate featureWeight(double featureWeight) {
        this.featureWeight = clamp01(featureWeight);
        return this;
    }

    public ScoredCandidate excluded(boolean excluded) {
        this.excluded = excluded;
        return this;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = clamp01(finalScore);
    }

    static double clamp01(double v) {
        if (Double.isNaN(v) || v < 0.0) {
            return 0.0;
        }
        return Math.min(v, 1.0);
    }
}
