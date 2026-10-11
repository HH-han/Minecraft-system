package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.TravelNoteCommentDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelNote;
import com.minecraft.entity.TravelNoteCollection;
import com.minecraft.entity.TravelNoteComment;
import com.minecraft.entity.TravelNoteLike;
import com.minecraft.entity.TravelNoteReport;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.TravelNoteCollectionMapper;
import com.minecraft.mapper.TravelNoteCommentMapper;
import com.minecraft.mapper.TravelNoteLikeMapper;
import com.minecraft.mapper.TravelNoteReportMapper;
import com.minecraft.service.TravelNoteInteractionService;
import com.minecraft.service.TravelNoteService;
import com.minecraft.service.UserService;
import com.minecraft.vo.TravelNoteCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 旅游笔记互动服务实现（点赞/收藏/评论/举报）
 */
@Service
@RequiredArgsConstructor
public class TravelNoteInteractionServiceImpl extends ServiceImpl<TravelNoteLikeMapper, TravelNoteLike>
        implements TravelNoteInteractionService {

    private final TravelNoteCollectionMapper collectionMapper;
    private final TravelNoteCommentMapper commentMapper;
    private final TravelNoteReportMapper reportMapper;
    private final UserService userService;
    private final TravelNoteService travelNoteService;

    // ==================== 点赞 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Long noteId, Long userId) {
        checkNoteExists(noteId);
        Long exists = baseMapper.selectCount(new LambdaQueryWrapper<TravelNoteLike>()
                .eq(TravelNoteLike::getNoteId, noteId)
                .eq(TravelNoteLike::getUserId, userId));
        if (exists > 0) {
            throw new BusinessException("已经点赞过了");
        }
        TravelNoteLike like = new TravelNoteLike();
        like.setNoteId(noteId);
        like.setUserId(userId);
        save(like);
        travelNoteService.lambdaUpdate()
                .eq(TravelNote::getId, noteId)
                .setSql("like_count = like_count + 1")
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(Long noteId, Long userId) {
        boolean removed = baseMapper.delete(new LambdaQueryWrapper<TravelNoteLike>()
                .eq(TravelNoteLike::getNoteId, noteId)
                .eq(TravelNoteLike::getUserId, userId)) > 0;
        if (removed) {
            travelNoteService.lambdaUpdate()
                    .eq(TravelNote::getId, noteId)
                    .setSql("like_count = GREATEST(like_count - 1, 0)")
                    .update();
        }
    }

    // ==================== 收藏 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collect(Long noteId, Long userId) {
        checkNoteExists(noteId);
        Long exists = collectionMapper.selectCount(new LambdaQueryWrapper<TravelNoteCollection>()
                .eq(TravelNoteCollection::getNoteId, noteId)
                .eq(TravelNoteCollection::getUserId, userId));
        if (exists > 0) {
            throw new BusinessException("已经收藏过了");
        }
        TravelNoteCollection collection = new TravelNoteCollection();
        collection.setNoteId(noteId);
        collection.setUserId(userId);
        collectionMapper.insert(collection);
        travelNoteService.lambdaUpdate()
                .eq(TravelNote::getId, noteId)
                .setSql("collect_count = collect_count + 1")
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uncollect(Long noteId, Long userId) {
        boolean removed = collectionMapper.delete(new LambdaQueryWrapper<TravelNoteCollection>()
                .eq(TravelNoteCollection::getNoteId, noteId)
                .eq(TravelNoteCollection::getUserId, userId)) > 0;
        if (removed) {
            travelNoteService.lambdaUpdate()
                    .eq(TravelNote::getId, noteId)
                    .setSql("collect_count = GREATEST(collect_count - 1, 0)")
                    .update();
        }
    }

    // ==================== 评论 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long comment(TravelNoteCommentDTO dto, Long userId) {
        checkNoteExists(dto.getNoteId());
        TravelNoteComment comment = new TravelNoteComment();
        comment.setNoteId(dto.getNoteId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);

        Long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
        comment.setParentId(parentId);
        if (parentId > 0) {
            TravelNoteComment parent = commentMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("回复的评论不存在");
            }
            comment.setReplyUid(dto.getReplyUid() != null ? dto.getReplyUid() : parent.getUserId());
        }
        commentMapper.insert(comment);

        travelNoteService.lambdaUpdate()
                .eq(TravelNote::getId, dto.getNoteId())
                .setSql("comment_count = comment_count + 1")
                .update();
        return comment.getId();
    }

    @Override
    public PageResponse<TravelNoteCommentVO> comments(Long noteId, PageRequest request) {
        Page<TravelNoteComment> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<TravelNoteComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNoteComment::getNoteId, noteId)
                .eq(TravelNoteComment::getParentId, 0L)
                .eq(TravelNoteComment::getStatus, 1)
                .orderByDesc(TravelNoteComment::getCreateTime);
        Page<TravelNoteComment> result = commentMapper.selectPage(page, wrapper);

        List<TravelNoteComment> roots = result.getRecords();
        if (roots.isEmpty()) {
            return new PageResponse<>(Collections.emptyList(), result.getTotal(),
                    request.getPageNum(), request.getPageSize());
        }

        List<Long> rootIds = roots.stream().map(TravelNoteComment::getId).collect(Collectors.toList());
        List<TravelNoteComment> replies = commentMapper.selectList(new LambdaQueryWrapper<TravelNoteComment>()
                .in(TravelNoteComment::getParentId, rootIds)
                .eq(TravelNoteComment::getStatus, 1)
                .orderByAsc(TravelNoteComment::getCreateTime));

        Map<Long, String> nameMap = batchUserNames(roots, replies);

        Map<Long, List<TravelNoteComment>> repliesByRoot = replies.stream()
                .collect(Collectors.groupingBy(TravelNoteComment::getParentId));

        List<TravelNoteCommentVO> voList = roots.stream().map(root -> {
            TravelNoteCommentVO vo = toCommentVO(root, nameMap);
            List<TravelNoteCommentVO> replyVos = repliesByRoot
                    .getOrDefault(root.getId(), Collections.emptyList())
                    .stream()
                    .map(r -> toCommentVO(r, nameMap))
                    .collect(Collectors.toList());
            vo.setReplies(replyVos);
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        TravelNoteComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }
        // 一并删除二级回复
        List<TravelNoteComment> replies = commentMapper.selectList(new LambdaQueryWrapper<TravelNoteComment>()
                .eq(TravelNoteComment::getParentId, commentId));
        int removed = 1 + replies.size();
        commentMapper.deleteById(commentId);
        if (!replies.isEmpty()) {
            commentMapper.delete(new LambdaQueryWrapper<TravelNoteComment>()
                    .eq(TravelNoteComment::getParentId, commentId));
        }
        travelNoteService.lambdaUpdate()
                .eq(TravelNote::getId, comment.getNoteId())
                .setSql("comment_count = GREATEST(comment_count - " + removed + ", 0)")
                .update();
    }

    // ==================== 举报 ====================

    @Override
    public void report(Long noteId, Long userId, String reason) {
        checkNoteExists(noteId);
        TravelNoteReport report = new TravelNoteReport();
        report.setNoteId(noteId);
        report.setReporterId(userId);
        report.setReason(StringUtils.hasText(reason) ? reason : "违规内容");
        report.setStatus(0);
        reportMapper.insert(report);
    }

    // ==================== 私有方法 ====================

    private void checkNoteExists(Long noteId) {
        TravelNote note = travelNoteService.getById(noteId);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
    }

    private TravelNoteCommentVO toCommentVO(TravelNoteComment comment, Map<Long, String> nameMap) {
        TravelNoteCommentVO vo = new TravelNoteCommentVO();
        BeanUtils.copyProperties(comment, vo);
        vo.setUsername(nameMap.getOrDefault(comment.getUserId(), "未知用户"));
        if (comment.getReplyUid() != null) {
            vo.setReplyUsername(nameMap.get(comment.getReplyUid()));
        }
        vo.setReplies(new ArrayList<>());
        return vo;
    }

    private Map<Long, String> batchUserNames(List<TravelNoteComment> roots, List<TravelNoteComment> replies) {
        List<TravelNoteComment> all = new ArrayList<>(roots);
        all.addAll(replies);
        return all.stream()
                .map(TravelNoteComment::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(uid -> uid, uid -> {
                    User user = userService.getUserInfo(uid);
                    if (user == null) {
                        return "未知用户";
                    }
                    if (StringUtils.hasText(user.getNickname())) {
                        return user.getNickname();
                    }
                    if (StringUtils.hasText(user.getUsername())) {
                        return user.getUsername();
                    }
                    return StringUtils.hasText(user.getAccount()) ? user.getAccount() : "未知用户";
                }));
    }
}
