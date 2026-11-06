package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.response.ServiceItemVO;
import com.minecraft.entity.ServiceItem;
import com.minecraft.mapper.ServiceItemMapper;
import com.minecraft.service.ServiceItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IM「服务」配置项服务实现
 */
@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem>
        implements ServiceItemService {

    @Override
    public List<ServiceItemVO> listEnabled() {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getStatus, 1)
                .orderByAsc(ServiceItem::getCategory)
                .orderByAsc(ServiceItem::getSort);
        return list(wrapper).stream().map(item -> {
            ServiceItemVO vo = new ServiceItemVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
