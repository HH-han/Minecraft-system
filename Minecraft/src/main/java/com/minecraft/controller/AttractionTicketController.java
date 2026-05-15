package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.AttractionTicket;
import com.minecraft.service.AttractionTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "景点门票管理")
@RestController
@RequestMapping("/api/attractionTicket")
public class AttractionTicketController {

    @Autowired
    private AttractionTicketService attractionTicketService;

    @Operation(summary = "获取景点门票列表")
    @GetMapping("/list")
    public ApiResponse<List<AttractionTicket>> getTicketsByAttractionId(@RequestParam Long attractionId) {
        return ApiResponse.success(attractionTicketService.getTicketsByAttractionId(attractionId));
    }

    @Operation(summary = "添加景点门票")
    @PostMapping("/add")
    public ApiResponse<Long> addTicket(@RequestBody AttractionTicket ticket) {
        ticket.setStatus(1);
        attractionTicketService.addTicket(ticket);
        return ApiResponse.success("添加成功", ticket.getId());
    }

    @Operation(summary = "更新景点门票")
    @PutMapping("/update")
    public ApiResponse<Void> updateTicket(@RequestBody AttractionTicket ticket) {
        attractionTicketService.updateTicket(ticket);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary = "删除景点门票")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTicket(@PathVariable Long id) {
        attractionTicketService.deleteTicket(id);
        return ApiResponse.success("删除成功", null);
    }
}