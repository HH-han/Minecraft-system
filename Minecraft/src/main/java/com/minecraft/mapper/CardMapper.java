package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Card;
import org.apache.ibatis.annotations.Mapper;

/**
 * 旅行卡片数据访问层
 */
@Mapper
public interface CardMapper extends BaseMapper<Card> {
}
