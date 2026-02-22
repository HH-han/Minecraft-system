package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Attraction;
import com.minecraft.mapper.AttractionMapper;
import com.minecraft.service.AttractionService;
import com.minecraft.service.LikeService;
import com.minecraft.vo.AttractionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements AttractionService {

    @Autowired
    private LikeService likeService;

    @Override
    public PageResponse<AttractionVO> getAttractionList(PageRequest request) {
        Page<Attraction> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Attraction::getName, request.getKeyword())
                    .or()
                    .like(Attraction::getCity, request.getKeyword());
        }

        if (request.getSortBy() != null) {
            if ("likeCount".equals(request.getSortBy())) {
                wrapper.orderByDesc(Attraction::getLikeCount);
            } else if ("collectCount".equals(request.getSortBy())) {
                wrapper.orderByDesc(Attraction::getCollectCount);
            } else {
                wrapper.orderByDesc(Attraction::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(Attraction::getCreateTime);
        }

        Page<Attraction> result = page(page, wrapper);

        List<AttractionVO> voList = result.getRecords().stream().map(item -> {
            AttractionVO vo = new AttractionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public AttractionVO getAttractionDetail(Long id, Long userId) {
        Attraction attraction = getById(id);
        AttractionVO vo = new AttractionVO();
        BeanUtils.copyProperties(attraction, vo);

        if (userId != null) {
            vo.setIsLiked(likeService.isLiked("attraction", id, userId));
        }

        return vo;
    }

    @Override
    public List<AttractionVO> getRecommendAttractions(String city, String season, Integer limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, 1);

        if (city != null) {
            wrapper.eq(Attraction::getCity, city);
        }

        if (season != null) {
            wrapper.like(Attraction::getSeason, season);
        }

        wrapper.orderByDesc(Attraction::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        List<Attraction> list = list(wrapper);
        return list.stream().map(item -> {
            AttractionVO vo = new AttractionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AttractionVO> getHotAttractions(Integer limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, 1)
                .orderByDesc(Attraction::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        List<Attraction> list = list(wrapper);
        return list.stream().map(item -> {
            AttractionVO vo = new AttractionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
