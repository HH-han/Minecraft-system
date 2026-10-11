package com.minecraft.task;

import com.minecraft.service.LogisticsTrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 物流轨迹定时同步（幂等，可重复执行）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogisticsSyncTask {

    private final LogisticsTrackService logisticsTrackService;

    /** 每 30 分钟同步未签收运单轨迹 */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void syncTracks() {
        int count = logisticsTrackService.syncTracks();
        if (count > 0) {
            log.info("[物流] 定时同步轨迹处理 {} 单", count);
        }
    }
}
