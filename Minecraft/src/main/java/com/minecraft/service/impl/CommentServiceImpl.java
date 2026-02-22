package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.CommentRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Comment;
import com.minecraft.mapper.CommentMapper;
import com.minecraft.service.CommentService;
import com.minecraft.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public void addComment(CommentRequest request, Long userId) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setItemType(request.getItemType());
        comment.setItemId(request.getItemId());
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());
        comment.setImages(request.getImages());
        comment.setRating(request.getRating());
        save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        removeById(id);
    }

    @Override
    public PageResponse<CommentVO> getCommentList(String itemType, Long itemId, PageRequest request) {
        Page<Comment> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getItemType, itemType)
                .eq(Comment::getItemId, itemId)
                .isNull(Comment::getParentId)
                .orderByDesc(Comment::getCreateTime);

        Page<Comment> result = page(page, wrapper);

        List<CommentVO> voList = result.getRecords().stream().map(item -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<CommentVO> getCommentTree(String itemType, Long itemId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getItemType, itemType)
                .eq(Comment::getItemId, itemId)
                .orderByDesc(Comment::getCreateTime);

        List<Comment> allComments = list(wrapper);
        List<CommentVO> rootComments = new ArrayList<>();

        for (Comment comment : allComments) {
            if (comment.getParentId() == null) {
                CommentVO vo = new CommentVO();
                BeanUtils.copyProperties(comment, vo);
                vo.setReplies(new ArrayList<>());
                buildCommentTree(vo, allComments);
                rootComments.add(vo);
            }
        }

        return rootComments;
    }

    private void buildCommentTree(CommentVO parent, List<Comment> allComments) {
        for (Comment comment : allComments) {
            if (parent.getId().equals(comment.getParentId())) {
                CommentVO child = new CommentVO();
                BeanUtils.copyProperties(comment, child);
                child.setReplies(new ArrayList<>());
                parent.getReplies().add(child);
                buildCommentTree(child, allComments);
            }
        }
    }
}
