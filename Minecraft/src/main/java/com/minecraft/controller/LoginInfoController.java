package com.minecraft.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.LoginInfo;
import com.minecraft.service.LoginInfoService;
import org.springframework.web.bind.annotation.*;

/**
 * 登录信息控制器
 */
@RestController
@RequestMapping("/api/public/user")
public class LoginInfoController {
    
    private final LoginInfoService loginInfoService;
    
    public LoginInfoController(LoginInfoService loginInfoService) {
        this.loginInfoService = loginInfoService;
    }
    
    /**
     * 获取登录信息列表
     */
    @GetMapping("/logininfo")
    public ApiResponse<Page<LoginInfo>> getLoginInfoList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<LoginInfo> loginInfoPage = loginInfoService.getLoginInfoPage(page, pageSize, keyword);
        return ApiResponse.success(loginInfoPage);
    }
    
    /**
     * 删除登录信息
     */
    @DeleteMapping("/logininfo/{id}")
    public ApiResponse<Boolean> deleteLoginInfo(@PathVariable Long id) {
        boolean result = loginInfoService.deleteLoginInfo(id);
        return ApiResponse.success(result);
    }
    
    /**
     * 批量删除登录信息
     */
    @DeleteMapping("/logininfo/batch")
    public ApiResponse<Boolean> deleteBatchLoginInfo(@RequestParam String ids) {
        boolean result = loginInfoService.deleteBatchLoginInfo(ids);
        return ApiResponse.success(result);
    }
}