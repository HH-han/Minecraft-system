package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.AnnouncementQueryRequest;
import com.minecraft.dto.request.AnnouncementSaveRequest;
import com.minecraft.dto.response.AnnouncementStatVO;
import com.minecraft.dto.response.AnnouncementVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Announcement;

import java.util.List;

/**
 * 系统公告服务：覆盖公告全生命周期 草稿 → 发布 → 展示 → 已读 → 过期 → 下架
 */
public interface AnnouncementService extends IService<Announcement> {

    // ==================== 用户端 ====================

    /** 分页查询已发布公告（未过期），匿名可访问 */
    PageResponse<AnnouncementVO> pageForUser(AnnouncementQueryRequest request, Long userId);

    /** 公告详情（浏览量缓冲累加） */
    AnnouncementVO getDetail(Long id, Long userId);

    /** 首页展示公告（缓存） */
    List<AnnouncementVO> getActive();

    /** 用户未读数（缓存） */
    Long getUnreadCount(Long userId);

    /** 我的未读公告列表 */
    List<AnnouncementVO> getMyUnread(Long userId);

    /** 标记已读（幂等） */
    void markRead(Long id, Long userId);

    /** 批量标记已读（幂等） */
    void batchMarkRead(List<Long> ids, Long userId);

    // ==================== 管理端 ====================

    /** 管理端分页查询（含草稿/已下架） */
    PageResponse<AnnouncementVO> pageForAdmin(AnnouncementQueryRequest request);

    /** 新建公告（含定时发布） */
    Long saveAnnouncement(AnnouncementSaveRequest request, Long operatorId, String operatorName);

    /** 编辑公告 */
    void updateAnnouncement(AnnouncementSaveRequest request, Long operatorId, String operatorName);

    /** 发布（立即） */
    void publish(Long id);

    /** 下架 */
    void offline(Long id);

    /** 置顶/取消置顶 */
    void toggleTop(Long id, Integer isTop);

    /** 逻辑删除 */
    void deleteAnnouncement(Long id);

    /** 阅读统计 */
    AnnouncementStatVO getReadStat(Long id);
}
