package com.minecraft.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minecraft.entity.Announcement;
import com.minecraft.mapper.AnnouncementMapper;
import com.minecraft.service.AnnouncementCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公告定时任务（幂等，可重复执行）
 * - 到点自动发布：草稿且发布时间已到 → 已发布
 * - 过期自动下架：已发布且已过期 → 已下架
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementScheduleTask {

    private final AnnouncementMapper announcementMapper;
    private final AnnouncementCacheService cacheService;

    /** 每分钟扫描：到点自动发布 */
    @Scheduled(cron = "0 * * * * ?")
    public void autoPublishScheduled() {
        int rows = announcementMapper.update(null,
                new LambdaUpdateWrapper<Announcement>()
                        .set(Announcement::getStatus, 1)
                        .eq(Announcement::getStatus, 0)
                        .isNotNull(Announcement::getPublishTime)
                        .le(Announcement::getPublishTime, LocalDateTime.now()));
        if (rows > 0) {
            cacheService.evictActive();
            log.info("[公告] 定时任务自动发布 {} 条", rows);
        }
    }

    /** 每分钟扫描：过期公告自动下架 */
    @Scheduled(cron = "0 * * * * ?")
    public void autoOfflineExpired() {
        int rows = announcementMapper.update(null,
                new LambdaUpdateWrapper<Announcement>()
                        .set(Announcement::getStatus, 2)
                        .eq(Announcement::getStatus, 1)
                        .isNotNull(Announcement::getExpireTime)
                        .lt(Announcement::getExpireTime, LocalDateTime.now()));
        if (rows > 0) {
            cacheService.evictActive();
            log.info("[公告] 定时任务自动下架过期公告 {} 条", rows);
        }
    }
}
