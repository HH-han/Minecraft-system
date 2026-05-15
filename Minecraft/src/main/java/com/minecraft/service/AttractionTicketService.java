package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.AttractionTicket;

import java.util.List;

public interface AttractionTicketService extends IService<AttractionTicket> {
    List<AttractionTicket> getTicketsByAttractionId(Long attractionId);
    void addTicket(AttractionTicket ticket);
    void updateTicket(AttractionTicket ticket);
    void deleteTicket(Long id);
}