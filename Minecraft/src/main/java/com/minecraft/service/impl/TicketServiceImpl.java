package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Ticket;
import com.minecraft.mapper.TicketMapper;
import com.minecraft.service.TicketService;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    @Override
    public PageResponse<?> getTicketList(PageRequest request) {
        Page<Ticket> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Ticket::getDepartureCity, request.getKeyword())
                    .or()
                    .like(Ticket::getArrivalCity, request.getKeyword());
        }

        wrapper.orderByDesc(Ticket::getCreateTime);

        Page<Ticket> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public Ticket getTicketDetail(Long id) {
        return getById(id);
    }

    @Override
    public void addTicket(Ticket ticket) {
        save(ticket);
    }

    @Override
    public void updateTicket(Ticket ticket) {
        updateById(ticket);
    }

    @Override
    public void deleteTicket(Long id) {
        removeById(id);
    }
}
