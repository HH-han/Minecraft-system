package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.PuzzleConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface PuzzleConfigMapper extends BaseMapper<PuzzleConfig> {
    
    Optional<PuzzleConfig> findByImageIdAndVersion(String imageId, Integer version);
    
    void deleteOldRecords(int limit);
}