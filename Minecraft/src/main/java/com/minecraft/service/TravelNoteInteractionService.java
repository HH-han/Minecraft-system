package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.TravelNoteCommentDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelNoteLike;
import com.minecraft.vo.TravelNoteCommentVO;

/**
 * 旅游笔记互动服务（点赞/收藏/评论/举报）
 */
public interface TravelNoteInteractionService extends IService<TravelNoteLike> {

    void like(Long noteId, Long userId);

    void unlike(Long noteId, Long userId);

    void collect(Long noteId, Long userId);

    void uncollect(Long noteId, Long userId);

    /** 发表评论，返回评论ID */
    Long comment(TravelNoteCommentDTO dto, Long userId);

    /** 一级评论分页（附二级回复） */
    PageResponse<TravelNoteCommentVO> comments(Long noteId, PageRequest request);

    void deleteComment(Long commentId, Long userId);

    void report(Long noteId, Long userId, String reason);
}
