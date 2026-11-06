package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.response.ServiceItemVO;
import com.minecraft.entity.ServiceItem;

import java.util.List;

/**
 * IM「服务」配置项服务
 */
public interface ServiceItemService extends IService<ServiceItem> {

    /**
     * 获取上架中的服务项（按分组、排序）
     */
    List<ServiceItemVO> listEnabled();
}
