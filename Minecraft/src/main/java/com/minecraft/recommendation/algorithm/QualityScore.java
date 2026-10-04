package com.minecraft.recommendation.algorithm;

/**
 * 品质分：源评分（0-5）线性归一到 [0,1]；无评分（0）给中性 0.5，避免冷物品被一票否决。
 */
public final class QualityScore {

    private QualityScore() {
    }

    public static double of(int rating) {
        if (rating <= 0) {
            return 0.5;
        }
        return Normalizers.clamp01(rating / 5.0);
    }
}
