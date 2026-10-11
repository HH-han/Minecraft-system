package com.minecraft.task;

import com.minecraft.service.OrderShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 超时自动确认收货（幂等，可重复执行）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoConfirmReceiveTask {

    /** 自动确认收货天数（TODO 改为读取系统参数） */
    private static final int AUTO_CONFIRM_DAYS = 10;

    private final OrderShippingService orderShippingService;

    /** 每日 03:00 扫描超时未签收运单，自动确认收货 */
    @Scheduled(cron = "0 0 3 * * ?")
    public void autoConfirmReceive() {
        int count = orderShippingService.autoConfirmReceive(AUTO_CONFIRM_DAYS);
        if (count > 0) {
            log.info("[物流] 自动确认收货 {} 单", count);
        }
    }
}
