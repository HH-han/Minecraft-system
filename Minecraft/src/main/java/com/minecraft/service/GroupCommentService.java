package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.GroupCommentDTO;
import com.minecraft.entity.GroupComment;
import com.minecraft.vo.GroupCommentVO;

import java.util.List;

public interface GroupCommentService extends IService<GroupComment> {
    GroupComment createComment(GroupCommentDTO dto, Long userId);
    void deleteComment(Long commentId, Long userId);
    List<GroupCommentVO> getCommentList(Long postId);
}