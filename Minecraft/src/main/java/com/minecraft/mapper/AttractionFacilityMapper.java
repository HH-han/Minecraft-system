package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.AttractionFacility;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttractionFacilityMapper extends BaseMapper<AttractionFacility> {
    List<AttractionFacility> selectByAttractionId(Long attractionId);
}