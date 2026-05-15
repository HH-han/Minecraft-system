package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.AttractionTicket;
import com.minecraft.mapper.AttractionTicketMapper;
import com.minecraft.service.AttractionTicketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttractionTicketServiceImpl extends ServiceImpl<AttractionTicketMapper, AttractionTicket> implements AttractionTicketService {

    @Override
    public List<AttractionTicket> getTicketsByAttractionId(Long attractionId) {
        LambdaQueryWrapper<AttractionTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTicket::getAttractionId, attractionId)
                .eq(AttractionTicket::getStatus, 1)
                .orderByAsc(AttractionTicket::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void addTicket(AttractionTicket ticket) {
        save(ticket);
    }

    @Override
    public void updateTicket(AttractionTicket ticket) {
        updateById(ticket);
    }

    @Override
    public void deleteTicket(Long id) {
        removeById(id);
    }
}