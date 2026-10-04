package com.minecraft.mapper;

import com.minecraft.recommendation.dto.ItemExposureResult;
import com.minecraft.recommendation.dto.ScoreBucketResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐效果分析 Mapper：曝光统计、分数分布、曝光归因互动。
 */
@Mapper
public interface RecommendationAnalyticsMapper {

    /** 窗口内曝光总次数 */
    long countExposures(@Param("category") String category,
                        @Param("since") LocalDateTime since);

    /** 窗口内去重曝光用户数（匿名曝光不计入） */
    long countExposureUsers(@Param("category") String category,
                            @Param("since") LocalDateTime since);

    /** 窗口内去重曝光物品数 */
    long countExposureItems(@Param("category") String category,
                            @Param("since") LocalDateTime since);

    /** 综合十分位分数桶分布：bucket in [0,10] */
    List<ScoreBucketResult> selectScoreBuckets(@Param("category") String category,
                                               @Param("since") LocalDateTime since);

    /** 窗口内曝光次数 Top N 物品 */
    List<ItemExposureResult> selectTopItems(@Param("category") String category,
                                            @Param("since") LocalDateTime since,
                                            @Param("limit") int limit);

    /**
     * 窗口内发生、且在曝光后 attributionHours 小时内产生的轻互动数（点赞/收藏/评论）。
     */
    long countAttributedInteractions(@Param("itemType") String itemType,
                                     @Param("since") LocalDateTime since,
                                     @Param("attributionHours") int attributionHours);

    /**
     * 窗口内发生、且在曝光后 attributionHours 小时内产生的转化数（加购/支付订单）。
     */
    long countAttributedConversions(@Param("itemType") String itemType,
                                    @Param("since") LocalDateTime since,
                                    @Param("attributionHours") int attributionHours);
}
