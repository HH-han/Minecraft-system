package com.minecraft.pay.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minecraft.entity.RailwayOrder;
import com.minecraft.mapper.RailwayOrderMapper;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.enums.PayBizType;
import com.minecraft.pay.event.PaySuccessEvent;
import com.minecraft.pay.mapper.PayOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 铁路车票订单支付成功处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RailwayPayBizListener {

    private final PayOrderMapper payOrderMapper;
    private final RailwayOrderMapper railwayOrderMapper;

    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void onPaySuccess(PaySuccessEvent event) {
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getPayNo, event.getPayNo())
                .last("LIMIT 1"));
        if (payOrder == null || !PayBizType.RAILWAY.name().equals(payOrder.getBizType())) {
            return;
        }
        int updated = railwayOrderMapper.update(null, new LambdaUpdateWrapper<RailwayOrder>()
                .eq(RailwayOrder::getOrderNo, payOrder.getBizOrderNo())
                .eq(RailwayOrder::getStatus, "PENDING")
                .set(RailwayOrder::getStatus, "PAID")
                .set(RailwayOrder::getPayTime, LocalDateTime.now()));
        log.info("[支付成功] 铁路订单状态更新 orderNo={}, updated={}",
                payOrder.getBizOrderNo(), updated);
    }
}
