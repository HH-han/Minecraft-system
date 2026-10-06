package com.minecraft.service;

import com.minecraft.dto.response.AnnouncementVO;

import java.util.List;
import java.util.function.Supplier;

/**
 * 公告缓存服务（Redis）
 * 键设计：
 *  - announcement:active            首页活跃公告，TTL 10min
 *  - announcement:unread:{userId}   用户未读数，TTL 5min
 *  - announcement:detail:{id}       公告详情，TTL 30min
 *  - announcement:view:{id}         浏览量缓冲，每 100 次落库一次
 */
public interface AnnouncementCacheService {

    /** 获取首页活跃公告（缓存未命中回源 DB） */
    List<AnnouncementVO> getActive(Supplier<List<AnnouncementVO>> loader);

    /** 获取公告详情（缓存未命中回源 DB） */
    AnnouncementVO getDetail(Long id, Supplier<AnnouncementVO> loader);

    /** 获取用户未读数 */
    Long getUnreadCount(Long userId, Supplier<Long> loader);

    /** 失效活跃公告缓存 */
    void evictActive();

    /** 失效公告详情缓存 */
    void evictDetail(Long id);

    /** 失效用户未读数缓存 */
    void evictUnread(Long userId);

    /** 浏览量缓冲：每 100 次落库一次 */
    void incrView(Long id);
}
