package com.minecraft.recommendation.config;

import com.minecraft.entity.recommendation.AttractionRecommendation;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.entity.recommendation.HotelRecommendation;
import com.minecraft.entity.recommendation.RestaurantRecommendation;
import com.minecraft.entity.recommendation.SouvenirRecommendation;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minecraft.mapper.AttractionsRecommendationMapper;
import com.minecraft.mapper.HotelsRecommendationMapper;
import com.minecraft.mapper.RecommendationItemMapper;
import com.minecraft.mapper.RestaurantsRecommendationMapper;
import com.minecraft.mapper.SouvenirsRecommendationMapper;
import com.minecraft.recommendation.enums.RecommendCategory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 推荐分类 -> 结果表 Mapper / 实体工厂 的注册表。
 * <p>
 * 屏蔽四张结构相同表的泛型差异，重算编排服务只面向 {@link BaseRecommendationItem} 编程。
 */
@Component
public class RecommendationMapperRegistry {

    private final Map<RecommendCategory, RecommendationItemMapper<? extends BaseRecommendationItem>> mappers =
            new EnumMap<>(RecommendCategory.class);

    private final Map<RecommendCategory, Supplier<BaseRecommendationItem>> factories =
            new EnumMap<>(RecommendCategory.class);

    public RecommendationMapperRegistry(AttractionsRecommendationMapper attractionMapper,
                                        HotelsRecommendationMapper hotelMapper,
                                        RestaurantsRecommendationMapper restaurantMapper,
                                        SouvenirsRecommendationMapper souvenirMapper) {
        mappers.put(RecommendCategory.ATTRACTION, attractionMapper);
        mappers.put(RecommendCategory.HOTEL, hotelMapper);
        mappers.put(RecommendCategory.RESTAURANT, restaurantMapper);
        mappers.put(RecommendCategory.SOUVENIR, souvenirMapper);

        factories.put(RecommendCategory.ATTRACTION, AttractionRecommendation::new);
        factories.put(RecommendCategory.HOTEL, HotelRecommendation::new);
        factories.put(RecommendCategory.RESTAURANT, RestaurantRecommendation::new);
        factories.put(RecommendCategory.SOUVENIR, SouvenirRecommendation::new);
    }

    /**
     * 创建分类对应的推荐结果实体实例。
     */
    public BaseRecommendationItem newInstance(RecommendCategory category) {
        return factories.get(category).get();
    }

    /**
     * 批量写入（upsert）某分类的推荐分，冲突时保留人工列。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int upsert(RecommendCategory category, List<? extends BaseRecommendationItem> items) {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return ((RecommendationItemMapper) mappers.get(category)).upsertItems(items);
    }

    /**
     * 获取原始 Mapper（MyBatis-Plus BaseMapper 查询/分页等场景）。
     */
    public RecommendationItemMapper<? extends BaseRecommendationItem> mapper(RecommendCategory category) {
        return mappers.get(category);
    }

    /**
     * 查询在架（status=1）的 Top N 推荐行：置顶优先，其次综合分降序。
     * limit 由调用方保证为安全整数（1..100）。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<BaseRecommendationItem> selectTopScored(RecommendCategory category, int limit) {
        QueryWrapper<BaseRecommendationItem> wrapper = new QueryWrapper<BaseRecommendationItem>()
                .eq("status", 1)
                .orderByDesc("is_featured")
                .orderByDesc("recommendation_score")
                .last("LIMIT " + limit);
        return (List<BaseRecommendationItem>) ((RecommendationItemMapper) mappers.get(category)).selectList(wrapper);
    }

    /**
     * 按物品 ID 查询单条在架推荐行。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public BaseRecommendationItem selectScoredByItemId(RecommendCategory category, Long itemId) {
        QueryWrapper<BaseRecommendationItem> wrapper = new QueryWrapper<BaseRecommendationItem>()
                .eq("item_id", itemId)
                .eq("status", 1)
                .last("LIMIT 1");
        List<BaseRecommendationItem> list = (List<BaseRecommendationItem>)
                ((RecommendationItemMapper) mappers.get(category)).selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 管理后台分页查询推荐行（置顶优先 + 综合分降序）。offset/size 由服务层保证为非负整数。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<BaseRecommendationItem> selectScoredPage(RecommendCategory category,
                                                          int offset, int size) {
        QueryWrapper<BaseRecommendationItem> wrapper = new QueryWrapper<BaseRecommendationItem>()
                .orderByDesc("is_featured")
                .orderByDesc("recommendation_score")
                .last("LIMIT " + Math.max(0, offset) + "," + Math.max(1, size));
        return (List<BaseRecommendationItem>) ((RecommendationItemMapper) mappers.get(category)).selectList(wrapper);
    }

    /**
     * 人工干预字段更新（置顶/加权/状态），按 item_id 定位；行不存在返回 null。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public BaseRecommendationItem updateManualFields(RecommendCategory category, Long itemId,
                                                      Boolean featured, java.math.BigDecimal featureWeight,
                                                      Integer status) {
        RecommendationItemMapper mapper = (RecommendationItemMapper) mappers.get(category);
        QueryWrapper<BaseRecommendationItem> wrapper = new QueryWrapper<BaseRecommendationItem>()
                .eq("item_id", itemId).last("LIMIT 1");
        List<BaseRecommendationItem> rows = (List<BaseRecommendationItem>) mapper.selectList(wrapper);
        if (rows.isEmpty()) {
            return null;
        }
        BaseRecommendationItem row = rows.get(0);
        if (featured != null) {
            row.setIsFeatured(featured);
        }
        if (featureWeight != null) {
            row.setFeatureWeight(featureWeight);
        }
        if (status != null) {
            row.setStatus(status);
        }
        mapper.updateById(row);
        return row;
    }

    /**
     * 按物品 ID 集合批量查询在架推荐行。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<BaseRecommendationItem> selectScoredByItemIds(RecommendCategory category,
                                                               List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<BaseRecommendationItem> wrapper = new QueryWrapper<BaseRecommendationItem>()
                .eq("status", 1)
                .in("item_id", itemIds);
        return (List<BaseRecommendationItem>) ((RecommendationItemMapper) mappers.get(category)).selectList(wrapper);
    }
}
