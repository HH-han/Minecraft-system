package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Countries;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CountriesMapper extends BaseMapper<Countries> {
}
