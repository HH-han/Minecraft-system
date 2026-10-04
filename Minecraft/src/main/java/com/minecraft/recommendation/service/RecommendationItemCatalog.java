package com.minecraft.recommendation.service;

import com.minecraft.entity.Attraction;
import com.minecraft.entity.Food;
import com.minecraft.entity.Hotel;
import com.minecraft.entity.Product;
import com.minecraft.mapper.AttractionMapper;
import com.minecraft.mapper.FoodMapper;
import com.minecraft.mapper.HotelMapper;
import com.minecraft.mapper.ProductMapper;
import com.minecraft.recommendation.algorithm.FeatureTokenizer;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.enums.RecommendCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐内部使用的源物品投影目录：屏蔽四类源表差异。
 */
@Service
@RequiredArgsConstructor
public class RecommendationItemCatalog {

    private final AttractionMapper attractionMapper;
    private final HotelMapper hotelMapper;
    private final FoodMapper foodMapper;
    private final ProductMapper productMapper;

    /**
     * 批量加载物品投影，返回 itemId -> detail（保留入参顺序语义无关，使用 LinkedHashMap）。
     */
    public Map<Long, ItemDetail> load(RecommendCategory category, Collection<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return new LinkedHashMap<>();
        }
        List<Long> ids = itemIds.stream().distinct().toList();
        return switch (category) {
            case ATTRACTION -> attractionMapper.selectBatchIds(ids).stream()
                    .collect(LinkedHashMap::new, (m, a) -> m.put(a.getId(), toDetail(a)), Map::putAll);
            case HOTEL -> hotelMapper.selectBatchIds(ids).stream()
                    .collect(LinkedHashMap::new, (m, h) -> m.put(h.getId(), toDetail(h)), Map::putAll);
            case RESTAURANT -> foodMapper.selectBatchIds(ids).stream()
                    .collect(LinkedHashMap::new, (m, f) -> m.put(f.getId(), toDetail(f)), Map::putAll);
            case SOUVENIR -> productMapper.selectBatchIds(ids).stream()
                    .collect(LinkedHashMap::new, (m, p) -> m.put(p.getId(), toDetail(p)), Map::putAll);
        };
    }

    /**
     * 统计某分类在架物品总数（分析覆盖率用）。
     */
    public long countActive(RecommendCategory category) {
        return switch (category) {
            case ATTRACTION -> attractionMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Attraction>()
                            .eq(Attraction::getStatus, 1));
            case HOTEL -> hotelMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Hotel>()
                            .eq(Hotel::getStatus, 1));
            case RESTAURANT -> foodMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Food>()
                            .eq(Food::getStatus, 1));
            case SOUVENIR -> productMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>()
                            .eq(Product::getStatus, 1));
        };
    }

    /**
     * 单个物品是否存在（用于相关推荐 404 判断）。
     */
    public boolean exists(RecommendCategory category, Long itemId) {
        return switch (category) {
            case ATTRACTION -> attractionMapper.selectById(itemId) != null;
            case HOTEL -> hotelMapper.selectById(itemId) != null;
            case RESTAURANT -> foodMapper.selectById(itemId) != null;
            case SOUVENIR -> productMapper.selectById(itemId) != null;
        };
    }

    private ItemDetail toDetail(Attraction a) {
        ItemDetail d = base(a.getId(), a.getName(), a.getCoverImage(), a.getCity(),
                a.getProvince(), a.getPrice(), a.getRating(), a.getStatus());
        d.setTags(tags(a.getTags()));
        d.setSeason(a.getSeason());
        return d;
    }

    private ItemDetail toDetail(Hotel h) {
        ItemDetail d = base(h.getId(), h.getName(), h.getCoverImage(), h.getCity(),
                h.getProvince(), h.getPrice(), h.getRating(), h.getStatus());
        // 酒店无标签字段，设施作为内容标签
        d.setTags(tags(h.getFacilities()));
        d.setSubType(h.getStarLevel() == null ? null : String.valueOf(h.getStarLevel()));
        return d;
    }

    private ItemDetail toDetail(Food f) {
        ItemDetail d = base(f.getId(), f.getName(), f.getCoverImage(), f.getCity(),
                f.getProvince(), f.getPrice(), f.getRating(), f.getStatus());
        d.setTags(tags(f.getTags()));
        d.setSubType(f.getCuisineType());
        return d;
    }

    private ItemDetail toDetail(Product p) {
        ItemDetail d = base(p.getId(), p.getName(), p.getCoverImage(), p.getCity(),
                p.getProvince(), p.getPrice(), p.getRating(), p.getStatus());
        d.setTags(tags(p.getTags()));
        d.setSubType(p.getType());
        return d;
    }

    private ItemDetail base(Long id, String name, String cover, String city, String province,
                            java.math.BigDecimal price, Integer rating, Integer status) {
        ItemDetail d = new ItemDetail();
        d.setItemId(id);
        d.setName(name);
        d.setCoverImage(cover);
        d.setCity(city);
        d.setProvince(province);
        d.setPrice(price);
        d.setRating(rating);
        d.setStatus(status);
        d.setTags(List.of());
        return d;
    }

    private List<String> tags(String raw) {
        return new ArrayList<>(FeatureTokenizer.tokenize(raw));
    }
}
