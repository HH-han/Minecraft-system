package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.AttractionTicket;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttractionTicketMapper extends BaseMapper<AttractionTicket> {
    List<AttractionTicket> selectByAttractionId(Long attractionId);
}