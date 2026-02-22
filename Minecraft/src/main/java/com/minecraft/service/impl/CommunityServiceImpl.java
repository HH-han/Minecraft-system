package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.CommunityPost;
import com.minecraft.mapper.CommunityPostMapper;
import com.minecraft.service.CollectionService;
import com.minecraft.service.CommunityService;
import com.minecraft.service.LikeService;
import com.minecraft.vo.CommunityPostVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityService {

    @Autowired
    private LikeService likeService;

    @Autowired
    private CollectionService collectionService;

    @Override
    public PageResponse<CommunityPostVO> getPostList(PageRequest request) {
        Page<CommunityPost> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityPost::getStatus, 1)
                .orderByDesc(CommunityPost::getCreateTime);

        Page<CommunityPost> result = page(page, wrapper);

        List<CommunityPostVO> voList = result.getRecords().stream().map(item -> {
            CommunityPostVO vo = new CommunityPostVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public CommunityPostVO getPostDetail(Long id, Long userId) {
        CommunityPost post = getById(id);
        CommunityPostVO vo = new CommunityPostVO();
        BeanUtils.copyProperties(post, vo);

        if (userId != null) {
            vo.setIsLiked(likeService.isLiked("post", id, userId));
            vo.setIsCollected(collectionService.isCollected("post", id, userId));
        }

        return vo;
    }

    @Override
    public void createPost(CommunityPost post) {
        save(post);
    }

    @Override
    public void updatePost(CommunityPost post) {
        updateById(post);
    }

    @Override
    public void deletePost(Long id) {
        removeById(id);
    }
}
