package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 四张分类推荐结果表 Mapper 的公共契约。
 */
public interface RecommendationItemMapper<T extends BaseRecommendationItem> extends BaseMapper<T> {

    /**
     * 批量写入推荐分；唯一键 item_id 冲突时只更新算法列，
     * 不覆盖 is_featured / feature_weight / status 人工列。
     */
    int upsertItems(@Param("list") List<T> list);
}
