package com.minecraft.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.LoginInfo;

/**
 * 登录信息服务
 */
public interface LoginInfoService extends IService<LoginInfo> {
    
    /**
     * 分页查询登录信息
     */
    Page<LoginInfo> getLoginInfoPage(int page, int pageSize, String keyword);
    
    /**
     * 删除登录信息
     */
    boolean deleteLoginInfo(Long id);
    
    /**
     * 批量删除登录信息
     */
    boolean deleteBatchLoginInfo(String ids);
}