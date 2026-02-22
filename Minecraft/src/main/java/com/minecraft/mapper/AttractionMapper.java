package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {
}
