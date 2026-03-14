package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.entity.LoginInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 登录信息Mapper
 */
public interface LoginInfoMapper extends BaseMapper<LoginInfo> {
    
    /**
     * 分页查询登录信息
     */
    Page<LoginInfo> selectLoginInfoPage(Page<LoginInfo> page, @Param("keyword") String keyword);
}