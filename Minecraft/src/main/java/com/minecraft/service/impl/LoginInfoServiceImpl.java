package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.LoginInfo;
import com.minecraft.mapper.LoginInfoMapper;
import com.minecraft.service.LoginInfoService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 登录信息服务实现
 */
@Service
public class LoginInfoServiceImpl extends ServiceImpl<LoginInfoMapper, LoginInfo> implements LoginInfoService {
    
    @Override
    public Page<LoginInfo> getLoginInfoPage(int page, int pageSize, String keyword) {
        Page<LoginInfo> loginInfoPage = new Page<>(page, pageSize);
        return baseMapper.selectLoginInfoPage(loginInfoPage, keyword);
    }
    
    @Override
    public boolean deleteLoginInfo(Long id) {
        return removeById(id);
    }
    
    @Override
    public boolean deleteBatchLoginInfo(String ids) {
        String[] idArray = ids.split(",");
        return removeByIds(Arrays.asList(idArray));
    }
}