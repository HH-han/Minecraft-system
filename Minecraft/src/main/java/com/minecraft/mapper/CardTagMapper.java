package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.CardTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卡片标签关联数据访问层
 */
@Mapper
public interface CardTagMapper extends BaseMapper<CardTag> {
}
