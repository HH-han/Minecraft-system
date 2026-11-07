package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minecraft.entity.RailwayGuide;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayGuideMapper;
import com.minecraft.service.RailwayGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RailwayGuideServiceImpl implements RailwayGuideService {

    private final RailwayGuideMapper guideMapper;

    @Override
    public List<RailwayGuide> list(String category, String keyword) {
        LambdaQueryWrapper<RailwayGuide> wrapper = new LambdaQueryWrapper<RailwayGuide>()
                .eq(RailwayGuide::getStatus, 1);
        if (StringUtils.hasText(category) && !"ALL".equalsIgnoreCase(category)) {
            wrapper.eq(RailwayGuide::getCategory, category.trim().toUpperCase());
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(RailwayGuide::getTitle, kw)
                    .or().like(RailwayGuide::getSummary, kw)
                    .or().like(RailwayGuide::getTags, kw));
        }
        wrapper.orderByAsc(RailwayGuide::getSort)
                .orderByDesc(RailwayGuide::getPublishTime)
                .last("LIMIT 60");
        List<RailwayGuide> list = guideMapper.selectList(wrapper);
        list.forEach(item -> item.setContent(null));
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RailwayGuide detail(Long id) {
        RailwayGuide exists = guideMapper.selectById(id);
        if (exists == null || !Integer.valueOf(1).equals(exists.getStatus())) {
            throw new BusinessException("指南文章不存在或已下架");
        }
        guideMapper.update(null, new LambdaUpdateWrapper<RailwayGuide>()
                .eq(RailwayGuide::getId, id)
                .setSql("views = views + 1"));
        exists.setViews((exists.getViews() == null ? 0 : exists.getViews()) + 1);
        return exists;
    }

    @Override
    public List<RailwayGuide> related(Long id, Integer limit) {
        RailwayGuide current = guideMapper.selectById(id);
        if (current == null) {
            return List.of();
        }
        int size = limit == null ? 3 : Math.max(1, Math.min(limit, 6));
        List<RailwayGuide> list = guideMapper.selectList(new LambdaQueryWrapper<RailwayGuide>()
                .eq(RailwayGuide::getStatus, 1)
                .eq(RailwayGuide::getCategory, current.getCategory())
                .ne(RailwayGuide::getId, id)
                .orderByAsc(RailwayGuide::getSort)
                .last("LIMIT " + size));
        list.forEach(item -> item.setContent(null));
        return list;
    }
}
