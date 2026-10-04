package com.minecraft.recommendation.algorithm.model;

/**
 * 物品相似邻居。
 *
 * @param itemId     邻居物品 ID
 * @param similarity 相似度（余弦，[0,1]）
 */
public record Neighbor(long itemId, double similarity) implements Comparable<Neighbor> {

    @Override
    public int compareTo(Neighbor o) {
        int c = Double.compare(o.similarity, this.similarity);
        return c != 0 ? c : Long.compare(this.itemId, o.itemId);
    }
}
