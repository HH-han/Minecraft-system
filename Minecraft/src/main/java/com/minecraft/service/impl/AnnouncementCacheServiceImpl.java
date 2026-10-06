package com.minecraft.service.impl;

import com.minecraft.dto.response.AnnouncementVO;
import com.minecraft.mapper.AnnouncementMapper;
import com.minecraft.service.AnnouncementCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * 公告缓存服务实现
 * Redis 不可用时自动降级直查 DB，保证可用性（缓存降级策略）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementCacheServiceImpl implements AnnouncementCacheService {

    private static final String ACTIVE_KEY = "announcement:active";
    private static final String UNREAD_KEY = "announcement:unread:";
    private static final String DETAIL_KEY = "announcement:detail:";
    private static final String VIEW_KEY = "announcement:view:";

    private static final long ACTIVE_TTL_MINUTES = 10;
    private static final long UNREAD_TTL_MINUTES = 5;
    private static final long DETAIL_TTL_MINUTES = 30;
    private static final int VIEW_FLUSH_THRESHOLD = 100;

    private final RedisTemplate<String, Object> redisTemplate;
    private final AnnouncementMapper announcementMapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<AnnouncementVO> getActive(Supplier<List<AnnouncementVO>> loader) {
        try {
            Object cached = redisTemplate.opsForValue().get(ACTIVE_KEY);
            if (cached != null) {
                return (List<AnnouncementVO>) cached;
            }
        } catch (Exception e) {
            log.warn("[公告] Redis 不可用，活跃公告降级查 DB: {}", e.getMessage());
        }
        List<AnnouncementVO> list = loader.get();
        // TTL 加随机抖动，防止缓存雪崩
        long ttl = ACTIVE_TTL_MINUTES + ThreadLocalRandom.current().nextLong(0, 3);
        try {
            redisTemplate.opsForValue().set(ACTIVE_KEY, list, Duration.ofMinutes(ttl));
        } catch (Exception ignored) {
        }
        return list;
    }

    @Override
    public AnnouncementVO getDetail(Long id, Supplier<AnnouncementVO> loader) {
        String key = DETAIL_KEY + id;
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof AnnouncementVO) {
                return (AnnouncementVO) cached;
            }
        } catch (Exception e) {
            log.warn("[公告] Redis 不可用，详情降级查 DB: {}", e.getMessage());
        }
        AnnouncementVO vo = loader.get();
        if (vo != null) {
            try {
                redisTemplate.opsForValue().set(key, vo, Duration.ofMinutes(DETAIL_TTL_MINUTES));
            } catch (Exception ignored) {
            }
        }
        return vo;
    }

    @Override
    public Long getUnreadCount(Long userId, Supplier<Long> loader) {
        String key = UNREAD_KEY + userId;
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return Long.parseLong(cached.toString());
            }
        } catch (Exception e) {
            log.warn("[公告] Redis 不可用，未读数降级查 DB: {}", e.getMessage());
        }
        Long count = loader.get();
        try {
            redisTemplate.opsForValue().set(key, count, Duration.ofMinutes(UNREAD_TTL_MINUTES));
        } catch (Exception ignored) {
        }
        return count;
    }

    @Override
    public void evictActive() {
        try {
            redisTemplate.delete(ACTIVE_KEY);
        } catch (Exception e) {
            log.warn("[公告] 失效活跃缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public void evictDetail(Long id) {
        try {
            redisTemplate.delete(DETAIL_KEY + id);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void evictUnread(Long userId) {
        try {
            redisTemplate.delete(UNREAD_KEY + userId);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void incrView(Long id) {
        String key = VIEW_KEY + id;
        try {
            Long cnt = redisTemplate.opsForValue().increment(key);
            if (cnt != null && cnt % VIEW_FLUSH_THRESHOLD == 0) {
                announcementMapper.incrViewCount(id, VIEW_FLUSH_THRESHOLD);
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            // Redis 不可用时直接落库，保底计数
            log.warn("[公告] 浏览量缓冲失败，直接落库: {}", e.getMessage());
            announcementMapper.incrViewCount(id, 1);
        }
    }
}
