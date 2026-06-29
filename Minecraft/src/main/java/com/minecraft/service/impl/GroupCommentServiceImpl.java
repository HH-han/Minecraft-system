package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.GroupCommentDTO;
import com.minecraft.entity.GroupComment;
import com.minecraft.entity.GroupPost;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupCommentMapper;
import com.minecraft.mapper.GroupPostMapper;
import com.minecraft.mapper.UserMapper;
import com.minecraft.service.GroupCommentService;
import com.minecraft.service.GroupMemberService;
import com.minecraft.vo.GroupCommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupCommentServiceImpl extends ServiceImpl<GroupCommentMapper, GroupComment> implements GroupCommentService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupPostMapper groupPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public GroupComment createComment(GroupCommentDTO dto, Long userId) {
        GroupPost post = groupPostMapper.selectById(dto.getPostId());
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        if (!groupMemberService.isMember(post.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        GroupComment comment = new GroupComment();
        BeanUtils.copyProperties(dto, comment);
        comment.setUserId(userId);
        if (dto.getParentId() == null) {
            comment.setParentId(0L);
        }
        save(comment);

        groupPostMapper.incrementCommentCount(dto.getPostId());

        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        GroupComment comment = getById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }

        GroupPost post = groupPostMapper.selectById(comment.getPostId());
        if (!comment.getUserId().equals(userId) && !groupMemberService.isAdmin(post.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限删除");
        }

        removeById(commentId);
    }

    @Override
    public List<GroupCommentVO> getCommentList(Long postId) {
        List<GroupComment> comments = baseMapper.selectByPostId(postId);
        
        Map<Long, GroupCommentVO> commentMap = new HashMap<>();
        List<GroupCommentVO> rootComments = new ArrayList<>();

        for (GroupComment comment : comments) {
            GroupCommentVO vo = convertToVO(comment);
            commentMap.put(comment.getId(), vo);

            if (comment.getParentId() == 0) {
                rootComments.add(vo);
            } else {
                GroupCommentVO parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>());
                    }
                    parent.getReplies().add(vo);
                }
            }
        }

        rootComments.sort(Comparator.comparing(GroupCommentVO::getCreatedAt).reversed());
        rootComments.forEach(vo -> {
            if (vo.getReplies() != null) {
                vo.getReplies().sort(Comparator.comparing(GroupCommentVO::getCreatedAt));
            }
        });

        return rootComments;
    }

    private GroupCommentVO convertToVO(GroupComment comment) {
        GroupCommentVO vo = new GroupCommentVO();
        BeanUtils.copyProperties(comment, vo);

        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUsername(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        if (comment.getReplyToUserId() != null) {
            User replyToUser = userMapper.selectById(comment.getReplyToUserId());
            if (replyToUser != null) {
                vo.setReplyToUsername(replyToUser.getNickname());
            }
        }

        return vo;
    }
}