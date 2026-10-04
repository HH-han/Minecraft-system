package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.recommendation.RecommendationExposureLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RecommendationExposureLogMapper extends BaseMapper<RecommendationExposureLog> {

    /**
     * 异步批量写入曝光日志。
     */
    int batchInsert(@Param("list") List<RecommendationExposureLog> list);

    /**
     * 分批清理过期曝光日志，返回删除行数。
     */
    int purgeBefore(@Param("before") LocalDateTime before, @Param("limit") int limit);
}
