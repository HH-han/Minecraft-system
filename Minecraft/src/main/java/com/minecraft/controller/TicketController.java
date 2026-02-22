package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Ticket;
import com.minecraft.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "票务管理")
@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Operation(summary ="获取票务列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getTicketList(PageRequest request) {
        return ApiResponse.success(ticketService.getTicketList(request));
    }

    @Operation(summary ="获取票务详情")
    @GetMapping("/{id}")
    public ApiResponse<Ticket> getTicketDetail(@PathVariable Long id) {
        return ApiResponse.success(ticketService.getTicketDetail(id));
    }

    @Operation(summary ="添加票务")
    @PostMapping("/add")
    public ApiResponse<Void> addTicket(@RequestBody Ticket ticket) {
        ticketService.addTicket(ticket);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary ="更新票务")
    @PutMapping("/update")
    public ApiResponse<Void> updateTicket(@RequestBody Ticket ticket) {
        ticketService.updateTicket(ticket);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除票务")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ApiResponse.success("删除成功", null);
    }
}
