package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.AnnouncementQueryRequest;
import com.minecraft.dto.request.AnnouncementSaveRequest;
import com.minecraft.dto.response.AnnouncementStatVO;
import com.minecraft.dto.response.AnnouncementVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Announcement;
import com.minecraft.entity.AnnouncementRead;
import com.minecraft.entity.AnnouncementTarget;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AnnouncementMapper;
import com.minecraft.mapper.AnnouncementReadMapper;
import com.minecraft.mapper.AnnouncementTargetMapper;
import com.minecraft.service.AnnouncementCacheService;
import com.minecraft.service.AnnouncementService;
import com.minecraft.service.AnnouncementSseService;
import com.minecraft.utils.HtmlSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {

    private static final int STATUS_PUBLISHED = 1;
    private static final int STATUS_OFFLINE = 2;

    private final AnnouncementReadMapper readMapper;
    private final AnnouncementTargetMapper targetMapper;
    private final AnnouncementCacheService cacheService;
    private final AnnouncementSseService sseService;

    // ==================== 用户端 ====================

    @Override
    public PageResponse<AnnouncementVO> pageForUser(AnnouncementQueryRequest request, Long userId) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, STATUS_PUBLISHED)
                .ne(Announcement::getTargetAudience, 4)
                .and(w -> w.isNull(Announcement::getExpireTime)
                        .or().gt(Announcement::getExpireTime, LocalDateTime.now()))
                .eq(request.getType() != null, Announcement::getType, request.getType())
                .eq(request.getLevel() != null, Announcement::getLevel, request.getLevel())
                .eq(request.getDisplayMode() != null, Announcement::getDisplayMode, request.getDisplayMode())
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getSortWeight)
                .orderByDesc(Announcement::getPublishTime);

        Page<Announcement> page = page(new Page<Announcement>(request.getPage(), request.getSize()), wrapper);
        Set<Long> readIds = queryReadIds(
                page.getRecords().stream().map(Announcement::getId).collect(Collectors.toList()), userId);
        List<AnnouncementVO> vos = page.getRecords().stream()
                .map(a -> toVO(a, readIds.contains(a.getId()), false))
                .collect(Collectors.toList());
        return new PageResponse<>(vos, page.getTotal(), request.getPage(), request.getSize());
    }

    @Override
    public AnnouncementVO getDetail(Long id, Long userId) {
        AnnouncementVO vo = cacheService.getDetail(id, () -> {
            Announcement ann = getById(id);
            if (ann == null) {
                return null;
            }
            return toVO(ann, false, true);
        });
        if (vo == null) {
            throw new BusinessException(404, "公告不存在");
        }
        // 已下架公告仅管理端可见，用户端拒绝
        if (vo.getStatus() == null || vo.getStatus() != STATUS_PUBLISHED) {
            throw new BusinessException(404, "公告不存在或已下架");
        }
        // 浏览量缓冲累加（Redis 缓冲，每 100 次落库）
        cacheService.incrView(id);
        vo.setViewCount(vo.getViewCount() == null ? 1 : vo.getViewCount() + 1);
        if (userId != null) {
            vo.setIsRead(isRead(id, userId));
        }
        return vo;
    }

    @Override
    public List<AnnouncementVO> getActive() {
        return cacheService.getActive(this::loadActiveFromDb);
    }

    private List<AnnouncementVO> loadActiveFromDb() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, STATUS_PUBLISHED)
                .ne(Announcement::getTargetAudience, 4)
                .and(w -> w.isNull(Announcement::getExpireTime)
                        .or().gt(Announcement::getExpireTime, LocalDateTime.now()))
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getSortWeight)
                .orderByDesc(Announcement::getPublishTime)
                .last("LIMIT 20");
        return list(wrapper).stream().map(a -> toVO(a, false, false)).collect(Collectors.toList());
    }

    @Override
    public Long getUnreadCount(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return cacheService.getUnreadCount(userId, () -> readMapper.countUnreadByUser(userId));
    }

    @Override
    public List<AnnouncementVO> getMyUnread(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return readMapper.selectUnreadByUser(userId).stream()
                .map(a -> toVO(a, false, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id, Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        int inserted = readMapper.insertIgnore(id, userId);
        if (inserted > 0) {
            // 幂等：仅首次插入才累加已读数
            baseMapper.incrReadCount(id, 1);
            cacheService.evictUnread(userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkRead(List<Long> ids, Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            int inserted = readMapper.insertIgnore(id, userId);
            if (inserted > 0) {
                baseMapper.incrReadCount(id, 1);
            }
        }
        cacheService.evictUnread(userId);
    }

    // ==================== 管理端 ====================

    @Override
    public PageResponse<AnnouncementVO> pageForAdmin(AnnouncementQueryRequest request) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
                .eq(request.getStatus() != null, Announcement::getStatus, request.getStatus())
                .eq(request.getType() != null, Announcement::getType, request.getType())
                .like(request.getKeyword() != null && !request.getKeyword().isEmpty(),
                        Announcement::getTitle, request.getKeyword())
                .orderByDesc(Announcement::getCreateTime);
        Page<Announcement> page = page(new Page<Announcement>(request.getPage(), request.getSize()), wrapper);
        List<AnnouncementVO> vos = page.getRecords().stream()
                .map(a -> toVO(a, false, false))
                .collect(Collectors.toList());
        return new PageResponse<>(vos, page.getTotal(), request.getPage(), request.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAnnouncement(AnnouncementSaveRequest request, Long operatorId, String operatorName) {
        Announcement ann = new Announcement();
        applyRequest(ann, request);
        ann.setCreatorId(operatorId);
        ann.setCreatorName(operatorName);

        int status = request.getStatus() != null ? request.getStatus() : 0;
        boolean scheduled = request.getPublishTime() != null
                && request.getPublishTime().isAfter(LocalDateTime.now());
        if (status == STATUS_PUBLISHED && !scheduled) {
            // 立即发布
            ann.setStatus(STATUS_PUBLISHED);
            ann.setPublishTime(LocalDateTime.now());
        } else {
            // 草稿或定时发布（定时任务到点自动发布）
            ann.setStatus(0);
            ann.setPublishTime(request.getPublishTime());
        }
        save(ann);
        saveTargets(ann.getId(), request);

        cacheService.evictActive();
        log.info("[公告] 新建成功 id={} title={} status={}", ann.getId(), ann.getTitle(), ann.getStatus());
        return ann.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(AnnouncementSaveRequest request, Long operatorId, String operatorName) {
        Announcement ann = getById(request.getId());
        if (ann == null) {
            throw new BusinessException(404, "公告不存在");
        }
        applyRequest(ann, request);
        int reqStatus = request.getStatus() != null ? request.getStatus() : ann.getStatus();
        boolean scheduled = request.getPublishTime() != null
                && request.getPublishTime().isAfter(LocalDateTime.now());
        if (reqStatus == STATUS_PUBLISHED && (ann.getStatus() == null || ann.getStatus() != STATUS_PUBLISHED) && !scheduled) {
            // 编辑后直接发布
            ann.setStatus(STATUS_PUBLISHED);
            ann.setPublishTime(LocalDateTime.now());
        } else if (reqStatus == STATUS_PUBLISHED && scheduled) {
            ann.setStatus(0);
            ann.setPublishTime(request.getPublishTime());
        }
        updateById(ann);
        saveTargets(ann.getId(), request);

        cacheService.evictActive();
        cacheService.evictDetail(ann.getId());
        log.info("[公告] 编辑成功 id={} title={}", ann.getId(), ann.getTitle());
    }

    @Override
    public void publish(Long id) {
        Announcement ann = getById(id);
        if (ann == null) {
            throw new BusinessException(404, "公告不存在");
        }
        if (ann.getStatus() != null && ann.getStatus() == STATUS_PUBLISHED) {
            throw new BusinessException(400, "公告已是发布状态");
        }
        ann.setStatus(STATUS_PUBLISHED);
        ann.setPublishTime(LocalDateTime.now());
        updateById(ann);
        cacheService.evictActive();
        cacheService.evictDetail(id);
        // SSE 实时推送
        sseService.broadcast(toVO(ann, false, true));
        log.info("[公告] 发布成功 id={} title={}", id, ann.getTitle());
    }

    @Override
    public void offline(Long id) {
        Announcement ann = getById(id);
        if (ann == null) {
            throw new BusinessException(404, "公告不存在");
        }
        ann.setStatus(STATUS_OFFLINE);
        updateById(ann);
        cacheService.evictActive();
        cacheService.evictDetail(id);
        log.info("[公告] 下架成功 id={} title={}", id, ann.getTitle());
    }

    @Override
    public void toggleTop(Long id, Integer isTop) {
        if (isTop == null || (isTop != 0 && isTop != 1)) {
            throw new BusinessException(400, "置顶参数错误");
        }
        Announcement ann = getById(id);
        if (ann == null) {
            throw new BusinessException(404, "公告不存在");
        }
        ann.setIsTop(isTop);
        updateById(ann);
        cacheService.evictActive();
    }

    @Override
    public void deleteAnnouncement(Long id) {
        Announcement ann = getById(id);
        if (ann == null) {
            throw new BusinessException(404, "公告不存在");
        }
        removeById(id);
        targetMapper.delete(new LambdaQueryWrapper<AnnouncementTarget>()
                .eq(AnnouncementTarget::getAnnouncementId, id));
        cacheService.evictActive();
        cacheService.evictDetail(id);
        log.info("[公告] 删除成功 id={} title={}", id, ann.getTitle());
    }

    @Override
    public AnnouncementStatVO getReadStat(Long id) {
        Announcement ann = getById(id);
        if (ann == null) {
            throw new BusinessException(404, "公告不存在");
        }
        AnnouncementStatVO vo = new AnnouncementStatVO();
        vo.setId(ann.getId());
        vo.setTitle(ann.getTitle());
        vo.setViewCount(ann.getViewCount() == null ? 0 : ann.getViewCount());
        vo.setReadCount(ann.getReadCount() == null ? 0 : ann.getReadCount());
        double rate = vo.getViewCount() > 0
                ? Math.round(vo.getReadCount() * 10000.0 / vo.getViewCount()) / 100.0
                : 0.0;
        vo.setReadRate(rate);
        return vo;
    }

    // ==================== 私有方法 ====================

    private void applyRequest(Announcement ann, AnnouncementSaveRequest request) {
        ann.setTitle(request.getTitle());
        ann.setSummary(request.getSummary());
        // 富文本 XSS 过滤
        ann.setContent(HtmlSanitizer.sanitize(request.getContent()));
        ann.setType(request.getType());
        ann.setLevel(request.getLevel());
        ann.setCategoryId(request.getCategoryId());
        ann.setCoverImage(request.getCoverImage());
        ann.setDisplayMode(request.getDisplayMode());
        ann.setTargetAudience(request.getTargetAudience());
        ann.setIsTop(request.getIsTop());
        ann.setSortWeight(request.getSortWeight());
        ann.setExpireTime(request.getExpireTime());
        if (ann.getType() == null) {
            ann.setType(1);
        }
        if (ann.getLevel() == null) {
            ann.setLevel(1);
        }
        if (ann.getDisplayMode() == null) {
            ann.setDisplayMode(1);
        }
        if (ann.getTargetAudience() == null) {
            ann.setTargetAudience(1);
        }
        if (ann.getIsTop() == null) {
            ann.setIsTop(0);
        }
        if (ann.getSortWeight() == null) {
            ann.setSortWeight(0);
        }
    }

    private void saveTargets(Long announcementId, AnnouncementSaveRequest request) {
        targetMapper.delete(new LambdaQueryWrapper<AnnouncementTarget>()
                .eq(AnnouncementTarget::getAnnouncementId, announcementId));
        if (request.getTargetAudience() != null && request.getTargetAudience() == 4
                && request.getTargetUserIds() != null && !request.getTargetUserIds().isEmpty()) {
            for (Long userId : request.getTargetUserIds().stream().distinct().collect(Collectors.toList())) {
                AnnouncementTarget target = new AnnouncementTarget();
                target.setAnnouncementId(announcementId);
                target.setUserId(userId);
                targetMapper.insert(target);
            }
        }
    }

    private Set<Long> queryReadIds(List<Long> announcementIds, Long userId) {
        if (userId == null || announcementIds == null || announcementIds.isEmpty()) {
            return Collections.emptySet();
        }
        return readMapper.selectList(new LambdaQueryWrapper<AnnouncementRead>()
                        .eq(AnnouncementRead::getUserId, userId)
                        .in(AnnouncementRead::getAnnouncementId, announcementIds)
                        .select(AnnouncementRead::getAnnouncementId))
                .stream()
                .map(AnnouncementRead::getAnnouncementId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private boolean isRead(Long announcementId, Long userId) {
        return readMapper.selectCount(new LambdaQueryWrapper<AnnouncementRead>()
                .eq(AnnouncementRead::getAnnouncementId, announcementId)
                .eq(AnnouncementRead::getUserId, userId)) > 0;
    }

    private AnnouncementVO toVO(Announcement ann, boolean isRead, boolean includeContent) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(ann.getId());
        vo.setTitle(ann.getTitle());
        vo.setSummary(ann.getSummary());
        // 列表接口不返回富文本正文，减少传输体积
        vo.setContent(includeContent ? ann.getContent() : null);
        vo.setType(ann.getType());
        vo.setLevel(ann.getLevel());
        vo.setCategoryId(ann.getCategoryId());
        vo.setCoverImage(ann.getCoverImage());
        vo.setDisplayMode(ann.getDisplayMode());
        vo.setTargetAudience(ann.getTargetAudience());
        vo.setStatus(ann.getStatus());
        vo.setIsTop(ann.getIsTop());
        vo.setSortWeight(ann.getSortWeight());
        vo.setPublishTime(ann.getPublishTime());
        vo.setExpireTime(ann.getExpireTime());
        vo.setViewCount(ann.getViewCount());
        vo.setReadCount(ann.getReadCount());
        vo.setCreatorName(ann.getCreatorName());
        vo.setCreateTime(ann.getCreateTime());
        vo.setIsRead(isRead);
        return vo;
    }
}
