package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.UserCollection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollectionMapper extends BaseMapper<UserCollection> {
}
