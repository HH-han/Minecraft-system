package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.CommunityPost;
import com.minecraft.vo.CommunityPostVO;

public interface CommunityService extends IService<CommunityPost> {
    PageResponse<CommunityPostVO> getPostList(PageRequest request);
    CommunityPostVO getPostDetail(Long id, Long userId);
    void createPost(CommunityPost post);
    void updatePost(CommunityPost post);
    void deletePost(Long id);
}
