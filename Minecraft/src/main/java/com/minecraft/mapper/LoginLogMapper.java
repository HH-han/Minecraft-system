package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.entity.LoginLog;
import org.apache.ibatis.annotations.Param;

public interface LoginLogMapper extends BaseMapper<LoginLog> {
    IPage<LoginLog> selectLoginLogPage(Page<LoginLog> page, 
                                      @Param("keyword") String keyword, 
                                      @Param("status") String status, 
                                      @Param("startTime") String startTime, 
                                      @Param("endTime") String endTime);
}