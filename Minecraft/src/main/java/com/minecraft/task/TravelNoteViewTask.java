package com.minecraft.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minecraft.entity.TravelNote;
import com.minecraft.mapper.TravelNoteMapper;
import com.minecraft.service.impl.TravelNoteServiceImpl;
import com.minecraft.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 旅游笔记浏览量回写定时任务
 * Redis Hash note:view:pending（field=noteId, value=增量）→ 每5分钟回写数据库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TravelNoteViewTask {

    private final RedisUtil redisUtil;
    private final TravelNoteMapper travelNoteMapper;

    @Scheduled(cron = "0 */5 * * * ?")
    public void flushViewCounts() {
        Map<Object, Object> entries = redisUtil.hgetall(TravelNoteServiceImpl.VIEW_PENDING_KEY);
        if (entries.isEmpty()) {
            return;
        }
        long total = 0;
        int notes = 0;
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String noteId = String.valueOf(entry.getKey());
            long delta;
            Object value = entry.getValue();
            if (value instanceof Number number) {
                delta = number.longValue();
            } else {
                try {
                    delta = Long.parseLong(String.valueOf(value).trim());
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            if (delta <= 0) {
                continue;
            }
            try {
                travelNoteMapper.update(null, new LambdaUpdateWrapper<TravelNote>()
                        .setSql("view_count = view_count + " + delta)
                        .eq(TravelNote::getId, Long.parseLong(noteId)));
                total += delta;
                notes++;
            } catch (NumberFormatException e) {
                log.warn("[旅游笔记] 非法浏览量缓存键: {}", noteId);
            }
        }
        redisUtil.hdel(TravelNoteServiceImpl.VIEW_PENDING_KEY, entries.keySet().toArray());
        if (notes > 0) {
            log.info("[旅游笔记] 浏览量回写完成: {} 篇笔记, 共 {} 次", notes, total);
        }
    }
}
