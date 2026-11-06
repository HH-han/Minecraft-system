package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.ServiceItemVO;
import com.minecraft.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * IM「服务」配置项（服务页宫格）
 */
@Tag(name = "IM服务项")
@RestController
@RequestMapping("/api/public/service-item")
public class ServiceItemController {

    @Autowired
    private ServiceItemService serviceItemService;

    @Operation(summary = "服务项列表")
    @GetMapping("/list")
    public ApiResponse<List<ServiceItemVO>> list() {
        return ApiResponse.success("成功", serviceItemService.listEnabled());
    }
}
