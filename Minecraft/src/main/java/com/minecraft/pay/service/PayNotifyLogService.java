package com.minecraft.pay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.pay.entity.PayNotifyLog;
import com.minecraft.pay.mapper.PayNotifyLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 回调日志服务：独立事务写入，确保业务处理回滚时日志仍然保留
 */
@Service
@RequiredArgsConstructor
public class PayNotifyLogService {

    private final PayNotifyLogMapper payNotifyLogMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void create(PayNotifyLog log) {
        payNotifyLogMapper.insert(log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void update(PayNotifyLog log) {
        payNotifyLogMapper.updateById(log);
    }

    public PageResponse<PayNotifyLog> page(PageRequest request, String channelCode) {
        Page<PayNotifyLog> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<PayNotifyLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(channelCode)) {
            wrapper.eq(PayNotifyLog::getChannelCode, channelCode.trim());
        }
        wrapper.orderByDesc(PayNotifyLog::getCreatedAt);
        Page<PayNotifyLog> result = payNotifyLogMapper.selectPage(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(),
                request.getPageNum(), request.getPageSize());
    }
}
