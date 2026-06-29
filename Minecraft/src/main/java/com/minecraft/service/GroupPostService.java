package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.GroupPostDTO;
import com.minecraft.entity.GroupPost;
import com.minecraft.vo.GroupPostVO;

import java.util.List;

public interface GroupPostService extends IService<GroupPost> {
    GroupPost createPost(GroupPostDTO dto, Long userId);
    void deletePost(Long postId, Long userId);
    List<GroupPostVO> getPostList(Long groupId, Long userId);
    void toggleLike(Long postId, Long userId);
    boolean isLiked(Long postId, Long userId);
}