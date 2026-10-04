package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.recommendation.RecommendationItemSimilarity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecommendationItemSimilarityMapper extends BaseMapper<RecommendationItemSimilarity> {

    /**
     * 批量写入相似度边（冲突时更新 similarity）。
     */
    int upsertSimilarities(@Param("list") List<RecommendationItemSimilarity> list);

    /**
     * 删除某分类的全部相似度边（重算前做整类替换）。
     */
    int deleteByCategory(@Param("category") String category);

    /**
     * 查询某物品的 Top-K 相似邻居。
     */
    List<RecommendationItemSimilarity> selectNeighbors(@Param("category") String category,
                                                        @Param("itemId") Long itemId,
                                                        @Param("limit") int limit);

    /**
     * 批量查询多个物品的全部已存储邻居（个性化单次 IN 查询，避免 N+1）。
     */
    List<RecommendationItemSimilarity> selectNeighborsByItems(@Param("category") String category,
                                                               @Param("itemIds") List<Long> itemIds);
}
