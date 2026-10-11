package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.FavoriteSaveDTO;
import com.minecraft.dto.response.FavoriteStatVO;
import com.minecraft.dto.response.FavoriteVO;
import com.minecraft.entity.Favorite;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.FavoriteMapper;
import com.minecraft.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 收藏服务实现
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    /** 正常状态 */
    private static final int STATUS_NORMAL = 1;
    /** 已删除状态 */
    private static final int STATUS_DELETED = 0;
    /** 允许的收藏类型 */
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "note", "link", "image", "video", "file", "chat", "voice", "location", "miniapp");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteVO save(Long userId, FavoriteSaveDTO dto) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        String type = normalizeType(dto.getType());
        String title = trimToNull(dto.getTitle());
        String content = trimToNull(dto.getContent());
        String cover = trimToNull(dto.getCover());
        String url = trimToNull(dto.getUrl());
        String fileName = trimToNull(dto.getFileName());
        List<String> tags = normalizeTags(dto.getTags());

        if (title == null && content == null && cover == null && url == null && fileName == null) {
            throw new BusinessException("收藏内容不能为空");
        }

        Favorite entity;
        if (dto.getId() != null) {
            entity = getById(dto.getId());
            if (entity == null || Objects.equals(entity.getStatus(), STATUS_DELETED)) {
                throw new BusinessException("收藏不存在");
            }
            if (!Objects.equals(entity.getUserId(), userId)) {
                throw new BusinessException("无权操作该收藏");
            }
        } else {
            entity = new Favorite();
            entity.setUserId(userId);
            entity.setStatus(STATUS_NORMAL);
        }

        entity.setType(type);
        entity.setTitle(title);
        entity.setContent(content);
        entity.setCover(cover);
        entity.setUrl(url);
        entity.setFileName(fileName);
        entity.setFileSize(dto.getFileSize());
        entity.setTags(tags.isEmpty() ? null : String.join(",", tags));
        entity.setSource(trimToNull(dto.getSource()));

        if (dto.getId() != null) {
            updateById(entity);
        } else {
            save(entity);
        }
        return toVO(entity);
    }

    @Override
    public List<FavoriteVO> list(Long userId, String type, String tag, String keyword, Integer page, Integer size) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getStatus, STATUS_NORMAL);

        Set<String> types = parseTypes(type);
        if (!types.isEmpty()) {
            wrapper.in(Favorite::getType, types);
        }

        String kw = trimToNull(keyword);
        if (kw != null) {
            wrapper.and(w -> w.like(Favorite::getTitle, kw)
                    .or().like(Favorite::getContent, kw)
                    .or().like(Favorite::getTags, kw)
                    .or().like(Favorite::getFileName, kw));
        }

        String tg = trimToNull(tag);
        if (tg != null) {
            wrapper.like(Favorite::getTags, tg);
        }

        wrapper.orderByDesc(Favorite::getUpdateTime).orderByDesc(Favorite::getId);

        Page<Favorite> result = page(new Page<>(pageOf(page), sizeOf(size)), wrapper);
        return toVOList(result.getRecords());
    }

    @Override
    public List<FavoriteVO> recent(Long userId, Integer limit) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        int lim = (limit == null || limit < 1) ? 20 : Math.min(limit, 100);
        List<Favorite> list = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getStatus, STATUS_NORMAL)
                .orderByDesc(Favorite::getUpdateTime)
                .last("LIMIT " + lim)
                .list();
        return toVOList(list);
    }

    @Override
    public FavoriteVO detail(Long userId, Long id) {
        Favorite favorite = getOwnedFavorite(userId, id);
        return toVO(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Favorite favorite = getOwnedFavorite(userId, id);
        favorite.setStatus(STATUS_DELETED);
        updateById(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(Long userId, String type) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        Favorite update = new Favorite();
        update.setStatus(STATUS_DELETED);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getStatus, STATUS_NORMAL);
        Set<String> types = parseTypes(type);
        if (!types.isEmpty()) {
            wrapper.in(Favorite::getType, types);
        }
        update(update, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void touch(Long userId, Long id) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        Favorite favorite = getById(id);
        if (favorite == null || !Objects.equals(favorite.getUserId(), userId)
                || Objects.equals(favorite.getStatus(), STATUS_DELETED)) {
            return;
        }
        // 仅更新 update_time（由 MyMetaObjectHandler 自动填充）
        Favorite update = new Favorite();
        update.setId(id);
        updateById(update);
    }

    @Override
    public FavoriteStatVO stats(Long userId) {
        FavoriteStatVO stat = new FavoriteStatVO();
        if (userId == null) {
            return stat;
        }
        List<Favorite> list = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getStatus, STATUS_NORMAL)
                .list();
        stat.setTotal((long) list.size());
        for (Favorite favorite : list) {
            String type = favorite.getType() == null ? "note" : favorite.getType();
            stat.getTypes().merge(type, 1L, Long::sum);
        }
        return stat;
    }

    @Override
    public List<String> tags(Long userId) {
        List<String> result = new ArrayList<>();
        if (userId == null) {
            return result;
        }
        List<Favorite> list = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getStatus, STATUS_NORMAL)
                .select(Favorite::getTags)
                .list();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Favorite favorite : list) {
            for (String tag : splitTags(favorite.getTags())) {
                set.add(tag);
            }
        }
        result.addAll(set);
        return result;
    }

    private Favorite getOwnedFavorite(Long userId, Long id) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (id == null) {
            throw new BusinessException("收藏不存在");
        }
        Favorite favorite = getById(id);
        if (favorite == null || Objects.equals(favorite.getStatus(), STATUS_DELETED)) {
            throw new BusinessException("收藏不存在");
        }
        if (!Objects.equals(favorite.getUserId(), userId)) {
            throw new BusinessException("无权操作该收藏");
        }
        return favorite;
    }

    private List<FavoriteVO> toVOList(List<Favorite> favorites) {
        List<FavoriteVO> result = new ArrayList<>();
        if (favorites == null) {
            return result;
        }
        for (Favorite favorite : favorites) {
            result.add(toVO(favorite));
        }
        return result;
    }

    private FavoriteVO toVO(Favorite favorite) {
        FavoriteVO vo = new FavoriteVO();
        vo.setId(favorite.getId());
        vo.setUserId(favorite.getUserId());
        vo.setType(favorite.getType());
        vo.setTitle(favorite.getTitle());
        vo.setContent(favorite.getContent());
        vo.setCover(favorite.getCover());
        vo.setUrl(favorite.getUrl());
        vo.setFileName(favorite.getFileName());
        vo.setFileSize(favorite.getFileSize());
        vo.setTags(splitTags(favorite.getTags()));
        vo.setSource(favorite.getSource());
        vo.setCreateTime(favorite.getCreateTime());
        vo.setUpdateTime(favorite.getUpdateTime());
        return vo;
    }

    private String normalizeType(String type) {
        String value = trimToNull(type);
        if (value == null || !ALLOWED_TYPES.contains(value)) {
            return "note";
        }
        return value;
    }

    private Set<String> parseTypes(String type) {
        Set<String> result = new LinkedHashSet<>();
        String value = trimToNull(type);
        if (value == null) {
            return result;
        }
        for (String item : value.split(",")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty() && !"all".equals(trimmed) && ALLOWED_TYPES.contains(trimmed)) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private List<String> normalizeTags(List<String> tags) {
        List<String> result = new ArrayList<>();
        if (tags == null) {
            return result;
        }
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String tag : tags) {
            String trimmed = trimToNull(tag);
            if (trimmed != null) {
                set.add(trimmed);
            }
        }
        result.addAll(set);
        return result;
    }

    private List<String> splitTags(String tags) {
        List<String> result = new ArrayList<>();
        String value = trimToNull(tags);
        if (value == null) {
            return result;
        }
        for (String tag : value.split(",")) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int pageOf(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int sizeOf(Integer size) {
        return size == null || size < 1 ? 20 : size;
    }
}
