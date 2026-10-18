package com.minecraft.pay.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minecraft.pay.entity.PayFlow;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.enums.PayStatus;
import com.minecraft.pay.mapper.PayFlowMapper;
import com.minecraft.pay.mapper.PayOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付超时关单任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayOrderTask {

    private final PayOrderMapper payOrderMapper;
    private final PayFlowMapper payFlowMapper;

    @Scheduled(fixedDelay = 60_000)
    @Transactional(rollbackFor = Exception.class)
    public void closeExpiredOrders() {
        List<PayOrder> expired = payOrderMapper.selectList(new LambdaQueryWrapper<PayOrder>()
                .in(PayOrder::getStatus, PayStatus.INIT.name(), PayStatus.PAYING.name())
                .lt(PayOrder::getExpireTime, LocalDateTime.now())
                .last("LIMIT 100"));
        for (PayOrder payOrder : expired) {
            payOrderMapper.update(null, new LambdaUpdateWrapper<PayOrder>()
                    .eq(PayOrder::getId, payOrder.getId())
                    .in(PayOrder::getStatus, PayStatus.INIT.name(), PayStatus.PAYING.name())
                    .set(PayOrder::getStatus, PayStatus.CLOSED.name())
                    .set(PayOrder::getUpdatedAt, LocalDateTime.now()));
            payFlowMapper.update(null, new LambdaUpdateWrapper<PayFlow>()
                    .eq(PayFlow::getPayNo, payOrder.getPayNo())
                    .in(PayFlow::getStatus, PayStatus.INIT.name(), PayStatus.PAYING.name())
                    .set(PayFlow::getStatus, PayStatus.FAILED.name()));
        }
        if (!expired.isEmpty()) {
            log.info("[支付] 已关闭 {} 笔超时支付单", expired.size());
        }
    }
}
