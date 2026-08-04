package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Feature;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeatureMapper extends BaseMapper<Feature> {
}
