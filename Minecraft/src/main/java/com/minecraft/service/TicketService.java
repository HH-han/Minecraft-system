package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Ticket;

public interface TicketService extends IService<Ticket> {
    PageResponse<?> getTicketList(PageRequest request);
    Ticket getTicketDetail(Long id);
    void addTicket(Ticket ticket);
    void updateTicket(Ticket ticket);
    void deleteTicket(Long id);
}
