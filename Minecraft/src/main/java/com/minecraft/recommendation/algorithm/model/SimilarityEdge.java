package com.minecraft.recommendation.algorithm.model;

/**
 * 物品相似度有向边（itemId -> neighborId）。相似度对称，持久化时两个方向各写一条。
 */
public record SimilarityEdge(String category, long itemId, long neighborId, double similarity) {
}
