package com.minecraft.task;

import com.minecraft.service.RailwayOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 铁路订单超时任务：每分钟取消超过 15 分钟未支付的订单并归还区间库存
 */
@Component
public class RailwayOrderTask {

    private static final Logger log = LoggerFactory.getLogger(RailwayOrderTask.class);

    private final RailwayOrderService orderService;

    public RailwayOrderTask(RailwayOrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(initialDelay = 30_000, fixedDelay = 60_000)
    public void cancelExpiredOrders() {
        try {
            int count = orderService.cancelExpiredOrders();
            if (count > 0) {
                log.info("铁路订单超时任务：已取消 {} 笔未支付订单", count);
            }
        } catch (Exception e) {
            log.error("铁路订单超时任务执行失败", e);
        }
    }
}
