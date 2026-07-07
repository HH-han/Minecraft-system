package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.PuzzleConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface PuzzleConfigMapper extends BaseMapper<PuzzleConfig> {
    
    @Select("SELECT * FROM captcha_puzzle_config WHERE image_id = #{imageId} AND version = #{version} LIMIT 1")
    Optional<PuzzleConfig> findByImageIdAndVersion(@Param("imageId") String imageId, @Param("version") Integer version);
}