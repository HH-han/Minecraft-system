package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.CommentRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Comment;
import com.minecraft.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    void addComment(CommentRequest request, Long userId);
    void deleteComment(Long id);
    PageResponse<CommentVO> getCommentList(String itemType, Long itemId, PageRequest request);
    List<CommentVO> getCommentTree(String itemType, Long itemId);
}
