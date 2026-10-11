package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.TravelNoteQueryDTO;
import com.minecraft.dto.request.TravelNoteSaveDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.*;
import com.minecraft.enums.FriendStatus;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.*;
import com.minecraft.service.TravelNoteService;
import com.minecraft.service.UserService;
import com.minecraft.utils.RedisUtil;
import com.minecraft.vo.TravelNoteDetailVO;
import com.minecraft.vo.TravelNoteReportVO;
import com.minecraft.vo.TravelNoteStatVO;
import com.minecraft.vo.TravelNoteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 旅游笔记服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TravelNoteServiceImpl extends ServiceImpl<TravelNoteMapper, TravelNote> implements TravelNoteService {

    /** Redis 待回写浏览量 Hash：field=noteId, value=增量 */
    public static final String VIEW_PENDING_KEY = "note:view:pending";

    private final TravelNoteMediaMapper mediaMapper;
    private final TravelNoteTagMapper tagMapper;
    private final TravelNoteTagRelMapper tagRelMapper;
    private final TravelNoteRelationMapper relationMapper;
    private final TravelNoteRouteMapper routeMapper;
    private final TravelNoteViewMapper viewMapper;
    private final TravelNoteLikeMapper likeMapper;
    private final TravelNoteCollectionMapper collectionMapper;
    private final TravelNoteReportMapper reportMapper;
    private final TravelNoteCommentMapper commentMapper;
    private final TravelPlanMapper travelPlanMapper;
    private final FriendMapper friendMapper;
    private final AttractionMapper attractionMapper;
    private final HotelMapper hotelMapper;
    private final FoodMapper foodMapper;
    private final ProductMapper productMapper;
    private final CitiesMapper citiesMapper;
    private final UserService userService;
    private final RedisUtil redisUtil;

    // ==================== 用户端 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveNote(Long userId, TravelNoteSaveDTO dto) {
        // 状态只允许：0-草稿 1-提交审核 5-私密
        int status = dto.getStatus() == null ? 0 : dto.getStatus();
        if (status != 0 && status != 1 && status != 5) {
            status = 0;
        }

        TravelNote note = new TravelNote();
        BeanUtils.copyProperties(dto, note);
        note.setStatus(status);

        // 封面兜底：取第一张图片
        if (!StringUtils.hasText(note.getCoverUrl()) && dto.getMediaList() != null) {
            dto.getMediaList().stream()
                    .filter(m -> m.getMediaType() != null && m.getMediaType() == 1 && StringUtils.hasText(m.getUrl()))
                    .findFirst()
                    .ifPresent(m -> note.setCoverUrl(m.getUrl()));
        }

        if (dto.getId() == null) {
            note.setUserId(userId);
            save(note);
        } else {
            TravelNote existing = getById(dto.getId());
            if (existing == null) {
                throw new BusinessException("笔记不存在");
            }
            if (!existing.getUserId().equals(userId)) {
                throw new BusinessException("无权修改此笔记");
            }
            note.setId(dto.getId());
            note.setUserId(userId);
            updateById(note);
        }
        Long noteId = note.getId();

        // 重建子表数据
        saveMedia(noteId, dto.getMediaList());
        saveRelations(noteId, dto.getRelations());
        saveRoutes(noteId, dto.getRoutes());
        bindTags(noteId, dto.getTags());
        return noteId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNote(Long id, Long userId) {
        TravelNote note = getById(id);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此笔记");
        }
        removeById(id);
    }

    @Override
    public TravelNoteDetailVO getDetail(Long id, Long viewerUserId, String ip, boolean ignoreStatus) {
        TravelNote note = getById(id);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        boolean isAuthor = viewerUserId != null && viewerUserId.equals(note.getUserId());
        if (!ignoreStatus && note.getStatus() != 2 && !isAuthor) {
            throw new BusinessException("笔记不存在或未发布");
        }

        TravelNoteDetailVO vo = new TravelNoteDetailVO();
        BeanUtils.copyProperties(note, vo);
        fillUserInfo(vo);

        vo.setTags(listTagNames(id));

        // 媒体资源
        List<TravelNoteMedia> mediaList = mediaMapper.selectList(new LambdaQueryWrapper<TravelNoteMedia>()
                .eq(TravelNoteMedia::getNoteId, id)
                .orderByAsc(TravelNoteMedia::getSort));
        vo.setMediaList(mediaList.stream().map(m -> {
            TravelNoteDetailVO.TravelNoteMediaVO mv = new TravelNoteDetailVO.TravelNoteMediaVO();
            BeanUtils.copyProperties(m, mv);
            return mv;
        }).collect(Collectors.toList()));

        // 结构化关联（回填名称/封面）
        List<TravelNoteRelation> relations = relationMapper.selectList(new LambdaQueryWrapper<TravelNoteRelation>()
                .eq(TravelNoteRelation::getNoteId, id)
                .orderByAsc(TravelNoteRelation::getSort));
        vo.setRelations(relations.stream().map(this::toRelationVO).collect(Collectors.toList()));

        // 行程轨迹
        List<TravelNoteRoute> routes = routeMapper.selectList(new LambdaQueryWrapper<TravelNoteRoute>()
                .eq(TravelNoteRoute::getNoteId, id)
                .orderByAsc(TravelNoteRoute::getDayIndex)
                .orderByAsc(TravelNoteRoute::getSort));
        vo.setRoutes(routes.stream().map(r -> {
            TravelNoteDetailVO.TravelNoteRouteVO rv = new TravelNoteDetailVO.TravelNoteRouteVO();
            BeanUtils.copyProperties(r, rv);
            if (r.getArriveTime() != null) {
                rv.setArriveTime(r.getArriveTime().toString());
            }
            return rv;
        }).collect(Collectors.toList()));

        // 城市名
        if (note.getCityId() != null) {
            Cities city = citiesMapper.selectById(note.getCityId());
            if (city != null) {
                vo.setCityName(city.getName());
            }
        }

        // 互动状态
        if (viewerUserId != null) {
            vo.setIsLiked(countLike(note.getId(), viewerUserId));
            vo.setIsCollected(countCollect(note.getId(), viewerUserId));
        } else {
            vo.setIsLiked(false);
            vo.setIsCollected(false);
        }

        recordView(note, viewerUserId, ip, isAuthor);
        return vo;
    }

    @Override
    public PageResponse<TravelNoteVO> pageNotes(TravelNoteQueryDTO query, Long userId) {
        String tab = StringUtils.hasText(query.getTab()) ? query.getTab() : "latest";

        LambdaQueryWrapper<TravelNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNote::getStatus, 2);

        if (query.getCityId() != null) {
            wrapper.eq(TravelNote::getCityId, query.getCityId());
        }
        if (Boolean.TRUE.equals(query.getFeatured())) {
            wrapper.eq(TravelNote::getIsFeatured, 1);
        }
        if (query.getTagId() != null) {
            wrapper.inSql(TravelNote::getId,
                    "SELECT note_id FROM travel_note_tag_rel WHERE tag_id = " + query.getTagId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(TravelNote::getTitle, kw)
                    .or().like(TravelNote::getSummary, kw)
                    .or().like(TravelNote::getDestination, kw));
        }

        switch (tab) {
            case "recommend" -> wrapper.last(
                    "ORDER BY is_top DESC, (view_count + like_count * 3 + collect_count * 5) DESC, publish_time DESC");
            case "follow" -> {
                List<Long> friendIds = listFriendIds(userId);
                if (friendIds.isEmpty()) {
                    return new PageResponse<>(Collections.emptyList(), 0L,
                            query.getPageNum(), query.getPageSize());
                }
                wrapper.in(TravelNote::getUserId, friendIds)
                        .orderByDesc(TravelNote::getIsTop)
                        .orderByDesc(TravelNote::getPublishTime);
            }
            case "city" -> {
                // 同城：cityId 为空的兜底等同最新；有 cityId 时已在上面按 cityId 过滤
                wrapper.orderByDesc(TravelNote::getIsTop).orderByDesc(TravelNote::getPublishTime);
            }
            default -> wrapper.orderByDesc(TravelNote::getIsTop).orderByDesc(TravelNote::getPublishTime);
        }

        Page<TravelNote> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<TravelNote> result = page(page, wrapper);

        Map<Long, List<String>> tagMap = batchTagNames(
                result.getRecords().stream().map(TravelNote::getId).collect(Collectors.toList()));

        List<TravelNoteVO> voList = result.getRecords().stream().map(item -> {
            TravelNoteVO vo = new TravelNoteVO();
            BeanUtils.copyProperties(item, vo);
            fillUserInfo(vo);
            vo.setTags(tagMap.getOrDefault(item.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public PageResponse<TravelNoteVO> myNotes(Long userId, Integer status, PageRequest request) {
        Page<TravelNote> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<TravelNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNote::getUserId, userId);
        if (status != null) {
            wrapper.eq(TravelNote::getStatus, status);
        }
        wrapper.orderByDesc(TravelNote::getUpdateTime);

        Page<TravelNote> result = page(page, wrapper);

        Map<Long, List<String>> tagMap = batchTagNames(
                result.getRecords().stream().map(TravelNote::getId).collect(Collectors.toList()));

        List<TravelNoteVO> voList = result.getRecords().stream().map(item -> {
            TravelNoteVO vo = new TravelNoteVO();
            BeanUtils.copyProperties(item, vo);
            vo.setTags(tagMap.getOrDefault(item.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyToPlan(Long noteId, Long userId) {
        TravelNote note = getById(noteId);
        if (note == null || note.getStatus() != 2) {
            throw new BusinessException("笔记不存在或未发布");
        }
        TravelPlan plan = new TravelPlan();
        plan.setUserId(userId);
        plan.setTitle("【行程】" + note.getTitle());
        plan.setDescription(note.getSummary());
        plan.setStartDate(note.getTravelDate());
        int days = (note.getTravelDays() != null && note.getTravelDays() > 0) ? note.getTravelDays() : 1;
        plan.setEndDate(note.getTravelDate() != null ? note.getTravelDate().plusDays(days - 1) : null);
        plan.setCity(note.getDestination());

        // 轨迹节点转行程内容
        List<TravelNoteRoute> routes = routeMapper.selectList(new LambdaQueryWrapper<TravelNoteRoute>()
                .eq(TravelNoteRoute::getNoteId, noteId)
                .orderByAsc(TravelNoteRoute::getDayIndex)
                .orderByAsc(TravelNoteRoute::getSort));
        StringBuilder content = new StringBuilder();
        for (TravelNoteRoute r : routes) {
            content.append("Day ").append(r.getDayIndex() == null ? "-" : r.getDayIndex())
                    .append("：").append(r.getPoiName() == null ? "" : r.getPoiName());
            if (StringUtils.hasText(r.getRemark())) {
                content.append("（").append(r.getRemark()).append("）");
            }
            content.append("\n");
        }
        plan.setContent(content.length() > 0 ? content.toString() : note.getSummary());
        plan.setStatus(1);
        travelPlanMapper.insert(plan);

        // 分享数 +1
        baseMapper.update(null, new LambdaUpdateWrapper<TravelNote>()
                .setSql("share_count = share_count + 1")
                .eq(TravelNote::getId, noteId));
        return plan.getId();
    }

    // ==================== 管理端 ====================

    @Override
    public PageResponse<TravelNoteVO> adminPage(Integer status, String keyword, PageRequest request) {
        Page<TravelNote> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<TravelNote> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TravelNote::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(TravelNote::getTitle, kw).or().like(TravelNote::getDestination, kw));
        }
        wrapper.orderByDesc(TravelNote::getCreateTime);

        Page<TravelNote> result = page(page, wrapper);

        Map<Long, List<String>> tagMap = batchTagNames(
                result.getRecords().stream().map(TravelNote::getId).collect(Collectors.toList()));

        List<TravelNoteVO> voList = result.getRecords().stream().map(item -> {
            TravelNoteVO vo = new TravelNoteVO();
            BeanUtils.copyProperties(item, vo);
            fillUserInfo(vo);
            vo.setTags(tagMap.getOrDefault(item.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public void audit(Long id, Boolean approved, String reason) {
        TravelNote note = getById(id);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        LambdaUpdateWrapper<TravelNote> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TravelNote::getId, id);
        if (Boolean.TRUE.equals(approved)) {
            wrapper.set(TravelNote::getStatus, 2)
                    .set(TravelNote::getPublishTime, LocalDateTime.now())
                    .set(TravelNote::getRejectReason, null);
        } else {
            wrapper.set(TravelNote::getStatus, 3)
                    .set(TravelNote::getRejectReason,
                            StringUtils.hasText(reason) ? reason : "内容不符合社区规范");
        }
        update(wrapper);
    }

    @Override
    public void toggleTop(Long id) {
        TravelNote note = getById(id);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        lambdaUpdate().eq(TravelNote::getId, id)
                .set(TravelNote::getIsTop, note.getIsTop() != null && note.getIsTop() == 1 ? 0 : 1)
                .update();
    }

    @Override
    public void toggleFeatured(Long id) {
        TravelNote note = getById(id);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        lambdaUpdate().eq(TravelNote::getId, id)
                .set(TravelNote::getIsFeatured, note.getIsFeatured() != null && note.getIsFeatured() == 1 ? 0 : 1)
                .update();
    }

    @Override
    public void offline(Long id) {
        lambdaUpdate().eq(TravelNote::getId, id).set(TravelNote::getStatus, 4).update();
    }

    @Override
    public TravelNoteStatVO statOverview() {
        TravelNoteStatVO vo = new TravelNoteStatVO();
        Map<String, Object> summary = baseMapper.selectStatSummary();
        vo.setTotalViews(toLong(summary.get("totalViews")));
        vo.setTotalLikes(toLong(summary.get("totalLikes")));
        vo.setTotalCollects(toLong(summary.get("totalCollects")));
        vo.setTotalComments(toLong(summary.get("totalComments")));
        vo.setTotalNotes(count());
        vo.setPublishedCount(lambdaQuery().eq(TravelNote::getStatus, 2).count());
        vo.setPendingCount(lambdaQuery().eq(TravelNote::getStatus, 1).count());
        vo.setTotalReports(reportMapper.selectCount(new LambdaQueryWrapper<TravelNoteReport>()
                .eq(TravelNoteReport::getStatus, 0)));

        vo.setHotDestinations(baseMapper.selectHotDestinations().stream().map(m -> {
            TravelNoteStatVO.HotDestination hd = new TravelNoteStatVO.HotDestination();
            hd.setDestination((String) m.get("destination"));
            hd.setNoteCount(toLong(m.get("noteCount")));
            return hd;
        }).collect(Collectors.toList()));

        vo.setTopAuthors(baseMapper.selectTopAuthors().stream().map(m -> {
            TravelNoteStatVO.TopAuthor ta = new TravelNoteStatVO.TopAuthor();
            ta.setUserId(toLong(m.get("userId")));
            ta.setNoteCount(toLong(m.get("noteCount")));
            ta.setTotalLikes(toLong(m.get("totalLikes")));
            fillAuthor(ta);
            return ta;
        }).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public PageResponse<TravelNoteReportVO> reportPage(Integer status, PageRequest request) {
        Page<TravelNoteReport> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<TravelNoteReport> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TravelNoteReport::getStatus, status);
        }
        wrapper.orderByDesc(TravelNoteReport::getCreateTime);
        Page<TravelNoteReport> result = reportMapper.selectPage(page, wrapper);

        List<TravelNoteReportVO> voList = result.getRecords().stream().map(r -> {
            TravelNoteReportVO vo = new TravelNoteReportVO();
            BeanUtils.copyProperties(r, vo);
            TravelNote note = getById(r.getNoteId());
            if (note != null) {
                vo.setNoteTitle(note.getTitle());
            }
            User reporter = userService.getUserInfo(r.getReporterId());
            if (reporter != null) {
                vo.setReporterName(displayName(reporter));
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleReport(Long reportId, Integer status, Long adminId) {
        TravelNoteReport report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }
        if (Integer.valueOf(1).equals(status)) {
            // 举报成立 → 笔记下架
            offline(report.getNoteId());
        }
        reportMapper.update(null, new LambdaUpdateWrapper<TravelNoteReport>()
                .eq(TravelNoteReport::getId, reportId)
                .set(TravelNoteReport::getStatus, status)
                .set(TravelNoteReport::getHandleBy, adminId)
                .set(TravelNoteReport::getHandleTime, LocalDateTime.now()));
    }

    // ==================== 标签 ====================

    @Override
    public PageResponse<TravelNoteTag> tagPage(String keyword, PageRequest request) {
        Page<TravelNoteTag> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<TravelNoteTag> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TravelNoteTag::getName, keyword.trim());
        }
        wrapper.orderByDesc(TravelNoteTag::getUseCount);
        Page<TravelNoteTag> result = tagMapper.selectPage(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public void saveTag(TravelNoteTag tag) {
        if (tag.getId() == null) {
            Long exists = tagMapper.selectCount(new LambdaQueryWrapper<TravelNoteTag>()
                    .eq(TravelNoteTag::getName, tag.getName()));
            if (exists > 0) {
                throw new BusinessException("标签已存在");
            }
            if (tag.getIsHot() == null) tag.setIsHot(0);
            if (tag.getStatus() == null) tag.setStatus(1);
            tagMapper.insert(tag);
        } else {
            tagMapper.updateById(tag);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        tagRelMapper.delete(new LambdaQueryWrapper<TravelNoteTagRel>()
                .eq(TravelNoteTagRel::getTagId, id));
        tagMapper.deleteById(id);
    }

    @Override
    public List<TravelNoteTag> hotTags(Integer limit) {
        int n = (limit == null || limit < 1 || limit > 50) ? 10 : limit;
        LambdaQueryWrapper<TravelNoteTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNoteTag::getStatus, 1)
                .orderByDesc(TravelNoteTag::getIsHot)
                .orderByDesc(TravelNoteTag::getUseCount)
                .last("LIMIT " + n);
        return tagMapper.selectList(wrapper);
    }

    // ==================== 私有方法 ====================

    private void saveMedia(Long noteId, List<TravelNoteSaveDTO.MediaItem> mediaList) {
        mediaMapper.delete(new LambdaQueryWrapper<TravelNoteMedia>()
                .eq(TravelNoteMedia::getNoteId, noteId));
        if (mediaList == null) {
            return;
        }
        int sort = 0;
        for (TravelNoteSaveDTO.MediaItem item : mediaList) {
            if (item == null || !StringUtils.hasText(item.getUrl())) {
                continue;
            }
            TravelNoteMedia media = new TravelNoteMedia();
            media.setNoteId(noteId);
            media.setMediaType(item.getMediaType() != null ? item.getMediaType() : 1);
            media.setUrl(item.getUrl().trim());
            media.setThumbUrl(item.getThumbUrl());
            media.setSort(item.getSort() != null ? item.getSort() : sort);
            mediaMapper.insert(media);
            sort++;
        }
    }

    private void saveRelations(Long noteId, List<TravelNoteSaveDTO.RelationItem> relations) {
        relationMapper.delete(new LambdaQueryWrapper<TravelNoteRelation>()
                .eq(TravelNoteRelation::getNoteId, noteId));
        if (relations == null) {
            return;
        }
        int sort = 0;
        for (TravelNoteSaveDTO.RelationItem item : relations) {
            if (item == null || item.getBizType() == null || item.getBizId() == null) {
                continue;
            }
            TravelNoteRelation relation = new TravelNoteRelation();
            relation.setNoteId(noteId);
            relation.setBizType(item.getBizType());
            relation.setBizId(item.getBizId());
            relation.setSort(item.getSort() != null ? item.getSort() : sort);
            relationMapper.insert(relation);
            sort++;
        }
    }

    private void saveRoutes(Long noteId, List<TravelNoteSaveDTO.RouteItem> routes) {
        routeMapper.delete(new LambdaQueryWrapper<TravelNoteRoute>()
                .eq(TravelNoteRoute::getNoteId, noteId));
        if (routes == null) {
            return;
        }
        int sort = 0;
        for (TravelNoteSaveDTO.RouteItem item : routes) {
            if (item == null || !StringUtils.hasText(item.getPoiName())) {
                continue;
            }
            TravelNoteRoute route = new TravelNoteRoute();
            BeanUtils.copyProperties(item, route);
            route.setId(null);
            route.setNoteId(noteId);
            route.setSort(item.getSort() != null ? item.getSort() : sort);
            routeMapper.insert(route);
            sort++;
        }
    }

    /** 重建笔记标签关联并维护 use_count */
    private void bindTags(Long noteId, List<String> tags) {
        List<TravelNoteTagRel> oldRels = tagRelMapper.selectList(new LambdaQueryWrapper<TravelNoteTagRel>()
                .eq(TravelNoteTagRel::getNoteId, noteId));
        if (!oldRels.isEmpty()) {
            oldRels.stream().map(TravelNoteTagRel::getTagId).distinct().forEach(tagId ->
                    tagMapper.update(null, new LambdaUpdateWrapper<TravelNoteTag>()
                            .setSql("use_count = GREATEST(use_count - 1, 0)")
                            .eq(TravelNoteTag::getId, tagId)));
            tagRelMapper.delete(new LambdaQueryWrapper<TravelNoteTagRel>()
                    .eq(TravelNoteTagRel::getNoteId, noteId));
        }
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (String raw : tags.stream().distinct().limit(10).collect(Collectors.toList())) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            String name = raw.trim();
            if (name.length() > 50) {
                name = name.substring(0, 50);
            }
            TravelNoteTag tag = tagMapper.selectOne(new LambdaQueryWrapper<TravelNoteTag>()
                    .eq(TravelNoteTag::getName, name));
            if (tag == null) {
                tag = new TravelNoteTag();
                tag.setName(name);
                tag.setUseCount(0);
                tag.setIsHot(0);
                tag.setStatus(1);
                try {
                    tagMapper.insert(tag);
                } catch (Exception e) {
                    // 并发下唯一键冲突，回查
                    tag = tagMapper.selectOne(new LambdaQueryWrapper<TravelNoteTag>()
                            .eq(TravelNoteTag::getName, name));
                    if (tag == null) {
                        continue;
                    }
                }
            }
            TravelNoteTagRel rel = new TravelNoteTagRel();
            rel.setNoteId(noteId);
            rel.setTagId(tag.getId());
            tagRelMapper.insert(rel);
            tagMapper.update(null, new LambdaUpdateWrapper<TravelNoteTag>()
                    .setSql("use_count = use_count + 1")
                    .eq(TravelNoteTag::getId, tag.getId()));
        }
    }

    private List<String> listTagNames(Long noteId) {
        List<TravelNoteTagRel> rels = tagRelMapper.selectList(new LambdaQueryWrapper<TravelNoteTagRel>()
                .eq(TravelNoteTagRel::getNoteId, noteId));
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> tagIds = rels.stream().map(TravelNoteTagRel::getTagId).collect(Collectors.toList());
        return tagMapper.selectBatchIds(tagIds).stream()
                .filter(t -> t.getStatus() == null || t.getStatus() == 1)
                .map(TravelNoteTag::getName)
                .collect(Collectors.toList());
    }

    private Map<Long, List<String>> batchTagNames(List<Long> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<TravelNoteTagRel> rels = tagRelMapper.selectList(new LambdaQueryWrapper<TravelNoteTagRel>()
                .in(TravelNoteTagRel::getNoteId, noteIds));
        if (rels.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, List<Long>> noteTagIds = rels.stream().collect(Collectors.groupingBy(
                TravelNoteTagRel::getNoteId,
                Collectors.mapping(TravelNoteTagRel::getTagId, Collectors.toList())));
        List<Long> allTagIds = rels.stream().map(TravelNoteTagRel::getTagId).distinct().collect(Collectors.toList());
        Map<Long, String> tagNameMap = tagMapper.selectBatchIds(allTagIds).stream()
                .collect(Collectors.toMap(TravelNoteTag::getId, TravelNoteTag::getName));

        Map<Long, List<String>> result = new HashMap<>();
        noteTagIds.forEach((noteId, tagIds) -> result.put(noteId, tagIds.stream()
                .map(tagNameMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())));
        return result;
    }

    private TravelNoteDetailVO.TravelNoteRelationVO toRelationVO(TravelNoteRelation relation) {
        TravelNoteDetailVO.TravelNoteRelationVO vo = new TravelNoteDetailVO.TravelNoteRelationVO();
        BeanUtils.copyProperties(relation, vo);
        switch (relation.getBizType()) {
            case 1 -> {
                Attraction attraction = attractionMapper.selectById(relation.getBizId());
                if (attraction != null) {
                    vo.setBizName(attraction.getName());
                    vo.setBizCover(attraction.getCoverImage());
                }
            }
            case 2 -> {
                Hotel hotel = hotelMapper.selectById(relation.getBizId());
                if (hotel != null) {
                    vo.setBizName(hotel.getName());
                }
            }
            case 3 -> {
                Food food = foodMapper.selectById(relation.getBizId());
                if (food != null) {
                    vo.setBizName(food.getName());
                    vo.setBizCover(food.getCoverImage());
                }
            }
            case 4 -> {
                Product product = productMapper.selectById(relation.getBizId());
                if (product != null) {
                    vo.setBizName(product.getName());
                    vo.setBizCover(product.getCoverImage());
                }
            }
            default -> vo.setBizName("行程 #" + relation.getBizId());
        }
        return vo;
    }

    private boolean countLike(Long noteId, Long userId) {
        return likeMapper.selectCount(new LambdaQueryWrapper<TravelNoteLike>()
                .eq(TravelNoteLike::getNoteId, noteId)
                .eq(TravelNoteLike::getUserId, userId)) > 0;
    }

    private boolean countCollect(Long noteId, Long userId) {
        return collectionMapper.selectCount(new LambdaQueryWrapper<TravelNoteCollection>()
                .eq(TravelNoteCollection::getNoteId, noteId)
                .eq(TravelNoteCollection::getUserId, userId)) > 0;
    }

    private void recordView(TravelNote note, Long viewerUserId, String ip, boolean isAuthor) {
        try {
            if (isAuthor) {
                return;
            }
            boolean newView = false;
            if (viewerUserId != null) {
                Long exists = viewMapper.selectCount(new LambdaQueryWrapper<TravelNoteView>()
                        .eq(TravelNoteView::getNoteId, note.getId())
                        .eq(TravelNoteView::getUserId, viewerUserId));
                if (exists == 0) {
                    TravelNoteView view = new TravelNoteView();
                    view.setNoteId(note.getId());
                    view.setUserId(viewerUserId);
                    viewMapper.insert(view);
                    newView = true;
                }
            } else if (StringUtils.hasText(ip)) {
                Long exists = viewMapper.selectCount(new LambdaQueryWrapper<TravelNoteView>()
                        .eq(TravelNoteView::getNoteId, note.getId())
                        .eq(TravelNoteView::getIp, ip));
                if (exists == 0) {
                    TravelNoteView view = new TravelNoteView();
                    view.setNoteId(note.getId());
                    view.setIp(ip);
                    viewMapper.insert(view);
                    newView = true;
                }
            }
            if (newView) {
                redisUtil.hincr(VIEW_PENDING_KEY, String.valueOf(note.getId()), 1);
            }
        } catch (Exception e) {
            log.warn("[旅游笔记] 记录浏览失败 noteId={}", note.getId(), e);
        }
    }

    private void fillUserInfo(TravelNoteVO vo) {
        if (vo.getUserId() == null) {
            vo.setUsername("未知用户");
            return;
        }
        try {
            User user = userService.getUserInfo(vo.getUserId());
            if (user != null) {
                vo.setUsername(displayName(user));
                vo.setAvatar(user.getAvatar());
            } else {
                vo.setUsername("未知用户");
            }
        } catch (Exception e) {
            vo.setUsername("未知用户");
        }
    }

    private void fillAuthor(TravelNoteStatVO.TopAuthor ta) {
        try {
            User user = userService.getUserInfo(ta.getUserId());
            if (user != null) {
                ta.setUsername(displayName(user));
                ta.setAvatar(user.getAvatar());
            }
        } catch (Exception ignored) {
        }
    }

    private String displayName(User user) {
        if (StringUtils.hasText(user.getNickname())) {
            return user.getNickname();
        }
        if (StringUtils.hasText(user.getUsername())) {
            return user.getUsername();
        }
        if (StringUtils.hasText(user.getAccount())) {
            return user.getAccount();
        }
        return "未知用户";
    }

    private List<Long> listFriendIds(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                        .eq(Friend::getUserId, userId)
                        .eq(Friend::getStatus, FriendStatus.ACCEPTED.ordinal()))
                .stream()
                .map(Friend::getFriendId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
