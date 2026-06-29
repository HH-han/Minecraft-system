package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.GroupPostDTO;
import com.minecraft.entity.GroupPost;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupMemberMapper;
import com.minecraft.mapper.GroupPostMapper;
import com.minecraft.mapper.UserMapper;
import com.minecraft.service.GroupMemberService;
import com.minecraft.service.GroupNotificationService;
import com.minecraft.service.GroupPostService;
import com.minecraft.vo.GroupPostVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupPostServiceImpl extends ServiceImpl<GroupPostMapper, GroupPost> implements GroupPostService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupNotificationService notificationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public GroupPost createPost(GroupPostDTO dto, Long userId) {
        if (!groupMemberService.isMember(dto.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        GroupPost post = new GroupPost();
        BeanUtils.copyProperties(dto, post);
        post.setUserId(userId);
        post.setLikeCount(0);
        post.setCommentCount(0);
        save(post);

        notificationService.sendNotification(null, dto.getGroupId(), "NEW_POST",
            "新动态", "有成员发布了新动态", post.getId());

        return post;
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        GroupPost post = getById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        if (!post.getUserId().equals(userId) && !groupMemberService.isAdmin(post.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限删除");
        }
        removeById(postId);
    }

    @Override
    public List<GroupPostVO> getPostList(Long groupId, Long userId) {
        List<GroupPost> posts = baseMapper.selectByGroupId(groupId);
        return posts.stream().map(post -> convertToVO(post, userId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleLike(Long postId, Long userId) {
        GroupPost post = getById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }

        String likeKey = "group:post:likes:" + postId;
        Boolean isLiked = redisTemplate.opsForSet().isMember(likeKey, userId.toString());

        if (isLiked != null && isLiked) {
            redisTemplate.opsForSet().remove(likeKey, userId.toString());
            baseMapper.decrementLikeCount(postId);
        } else {
            redisTemplate.opsForSet().add(likeKey, userId.toString());
            baseMapper.incrementLikeCount(postId);
        }
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        String likeKey = "group:post:likes:" + postId;
        Boolean isLiked = redisTemplate.opsForSet().isMember(likeKey, userId.toString());
        return isLiked != null && isLiked;
    }

    private GroupPostVO convertToVO(GroupPost post, Long userId) {
        GroupPostVO vo = new GroupPostVO();
        BeanUtils.copyProperties(post, vo);

        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            vo.setUsername(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        vo.setIsLiked(isLiked(post.getId(), userId));

        if (post.getImages() != null && !post.getImages().isEmpty()) {
            try {
                vo.setImages(objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setImages(new ArrayList<>());
            }
        } else {
            vo.setImages(new ArrayList<>());
        }

        return vo;
    }
}