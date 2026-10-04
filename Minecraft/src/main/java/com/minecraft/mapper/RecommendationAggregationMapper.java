package com.minecraft.mapper;

import com.minecraft.recommendation.dto.ItemCountResult;
import com.minecraft.recommendation.dto.UserItemInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐系统专用聚合查询 Mapper：
 * 统一从现有行为表（like_record / collection / comment / cart / orders）
 * 以集合式 SQL 提取用户-物品行为与物品计数，避免全表载入。
 */
@Mapper
public interface RecommendationAggregationMapper {

    // ---------------- 用户-物品行为（recency 窗口内，用于协同过滤/画像） ----------------

    List<UserItemInteraction> selectLikes(@Param("itemType") String itemType,
                                          @Param("since") LocalDateTime since);

    List<UserItemInteraction> selectCollections(@Param("itemType") String itemType,
                                                @Param("since") LocalDateTime since);

    List<UserItemInteraction> selectComments(@Param("itemType") String itemType,
                                             @Param("since") LocalDateTime since);

    List<UserItemInteraction> selectCartItems(@Param("itemType") String itemType,
                                              @Param("since") LocalDateTime since);

    List<UserItemInteraction> selectPaidOrders(@Param("itemType") String itemType,
                                               @Param("since") LocalDateTime since);

    /**
     * 查询单个用户在窗口内的全部行为（UNION 五张行为表，查询时个性化专用）。
     * actionType: LIKE/COLLECT/COMMENT/CART/ORDER
     */
    List<UserItemInteraction> selectUserInteractions(@Param("itemType") String itemType,
                                                     @Param("userId") Long userId,
                                                     @Param("since") LocalDateTime since);

    // ---------------- 全量物品计数（用于热度，购物车/订单无冗余计数列） ----------------

    List<ItemCountResult> countCartAll(@Param("itemType") String itemType);

    List<ItemCountResult> countPaidOrdersAll(@Param("itemType") String itemType);
}
