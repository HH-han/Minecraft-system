package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.MomentCommentDTO;
import com.minecraft.dto.request.MomentPublishDTO;
import com.minecraft.dto.response.MomentCommentVO;
import com.minecraft.dto.response.MomentUserVO;
import com.minecraft.dto.response.MomentVO;
import com.minecraft.entity.Friend;
import com.minecraft.entity.Moment;
import com.minecraft.entity.MomentComment;
import com.minecraft.entity.MomentLike;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.MomentCommentMapper;
import com.minecraft.mapper.MomentLikeMapper;
import com.minecraft.mapper.MomentMapper;
import com.minecraft.service.FriendService;
import com.minecraft.service.MomentService;
import com.minecraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 朋友圈服务实现
 */
@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    /** 正常状态 */
    private static final int STATUS_NORMAL = 1;
    /** 已删除状态 */
    private static final int STATUS_DELETED = 0;
    /** 私密可见 */
    private static final int VISIBILITY_PRIVATE = 2;
    /** 最多图片数量 */
    private static final int MAX_IMAGES = 9;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private MomentLikeMapper momentLikeMapper;

    @Autowired
    private MomentCommentMapper momentCommentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MomentVO publishMoment(Long userId, MomentPublishDTO dto) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        String content = dto.getContent() == null ? "" : dto.getContent().trim();
        List<String> images = dto.getImages() == null ? new ArrayList<>() : dto.getImages();
        images.removeIf(img -> img == null || img.trim().isEmpty());

        if (content.isEmpty() && images.isEmpty()) {
            throw new BusinessException("请填写内容或上传图片");
        }
        if (images.size() > MAX_IMAGES) {
            throw new BusinessException("最多只能上传9张图片");
        }

        Moment moment = new Moment();
        moment.setUserId(userId);
        moment.setContent(content);
        moment.setImages(images.isEmpty() ? null : String.join(",", images));
        moment.setLocation(dto.getLocation());
        moment.setVisibility(dto.getVisibility() != null ? dto.getVisibility() : 0);
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        moment.setStatus(STATUS_NORMAL);
        save(moment);

        return buildMomentVO(moment, userId);
    }

    @Override
    public List<MomentVO> getFeed(Long userId, Integer page, Integer size) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        List<Friend> friends = friendService.getFriendList(userId);
        for (Friend friend : friends) {
            userIds.add(friend.getFriendId());
        }

        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Moment::getUserId, userIds)
                .eq(Moment::getStatus, STATUS_NORMAL)
                // 别人不可见私密动态，自己的私密动态可见
                .and(w -> w.eq(Moment::getUserId, userId).or().ne(Moment::getVisibility, VISIBILITY_PRIVATE))
                .orderByDesc(Moment::getCreateTime);

        Page<Moment> result = page(new Page<>(pageOf(page), sizeOf(size)), wrapper);
        return toVOList(result.getRecords(), userId);
    }

    @Override
    public List<MomentVO> getUserMoments(Long currentUserId, Long targetUserId, Integer page, Integer size) {
        if (targetUserId == null) {
            throw new BusinessException("用户不存在");
        }
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moment::getUserId, targetUserId)
                .eq(Moment::getStatus, STATUS_NORMAL);
        if (!Objects.equals(currentUserId, targetUserId)) {
            wrapper.ne(Moment::getVisibility, VISIBILITY_PRIVATE);
        }
        wrapper.orderByDesc(Moment::getCreateTime);

        Page<Moment> result = page(new Page<>(pageOf(page), sizeOf(size)), wrapper);
        return toVOList(result.getRecords(), currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMoment(Long userId, Long momentId) {
        Moment moment = getById(momentId);
        if (moment == null || Objects.equals(moment.getStatus(), STATUS_DELETED)) {
            throw new BusinessException("动态不存在");
        }
        if (!Objects.equals(moment.getUserId(), userId)) {
            throw new BusinessException("只能删除自己的动态");
        }
        moment.setStatus(STATUS_DELETED);
        updateById(moment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleLike(Long userId, Long momentId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        Moment moment = getById(momentId);
        if (moment == null || !Objects.equals(moment.getStatus(), STATUS_NORMAL)) {
            throw new BusinessException("动态不存在");
        }

        LambdaQueryWrapper<MomentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentLike::getMomentId, momentId).eq(MomentLike::getUserId, userId);
        MomentLike existing = momentLikeMapper.selectOne(wrapper);

        boolean liked;
        if (existing != null) {
            momentLikeMapper.deleteById(existing.getId());
            liked = false;
        } else {
            MomentLike like = new MomentLike();
            like.setMomentId(momentId);
            like.setUserId(userId);
            momentLikeMapper.insert(like);
            liked = true;
        }

        long likeCount = momentLikeMapper.selectCount(
                new LambdaQueryWrapper<MomentLike>().eq(MomentLike::getMomentId, momentId));
        Moment update = new Moment();
        update.setId(momentId);
        update.setLikeCount((int) likeCount);
        updateById(update);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return result;
    }

    @Override
    public List<MomentUserVO> getLikes(Long momentId) {
        List<MomentLike> likes = momentLikeMapper.selectList(new LambdaQueryWrapper<MomentLike>()
                .eq(MomentLike::getMomentId, momentId)
                .orderByAsc(MomentLike::getCreateTime));
        List<MomentUserVO> result = new ArrayList<>();
        for (MomentLike like : likes) {
            MomentUserVO userVO = toUserVO(userService.getById(like.getUserId()));
            if (userVO != null) {
                result.add(userVO);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MomentCommentVO addComment(Long userId, Long momentId, MomentCommentDTO dto) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        Moment moment = getById(momentId);
        if (moment == null || !Objects.equals(moment.getStatus(), STATUS_NORMAL)) {
            throw new BusinessException("动态不存在");
        }
        String content = dto.getContent() == null ? "" : dto.getContent().trim();
        if (content.isEmpty()) {
            throw new BusinessException("评论内容不能为空");
        }

        MomentComment comment = new MomentComment();
        comment.setMomentId(momentId);
        comment.setUserId(userId);
        comment.setReplyToUserId(dto.getReplyToUserId());
        comment.setContent(content);
        comment.setStatus(STATUS_NORMAL);
        momentCommentMapper.insert(comment);

        long commentCount = momentCommentMapper.selectCount(new LambdaQueryWrapper<MomentComment>()
                .eq(MomentComment::getMomentId, momentId)
                .eq(MomentComment::getStatus, STATUS_NORMAL));
        Moment update = new Moment();
        update.setId(momentId);
        update.setCommentCount((int) commentCount);
        updateById(update);

        return buildCommentVO(comment);
    }

    @Override
    public List<MomentCommentVO> getComments(Long momentId) {
        List<MomentComment> comments = momentCommentMapper.selectList(new LambdaQueryWrapper<MomentComment>()
                .eq(MomentComment::getMomentId, momentId)
                .eq(MomentComment::getStatus, STATUS_NORMAL)
                .orderByAsc(MomentComment::getCreateTime));
        List<MomentCommentVO> result = new ArrayList<>();
        for (MomentComment comment : comments) {
            result.add(buildCommentVO(comment));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long userId, Long commentId) {
        MomentComment comment = momentCommentMapper.selectById(commentId);
        if (comment == null || Objects.equals(comment.getStatus(), STATUS_DELETED)) {
            throw new BusinessException("评论不存在");
        }
        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new BusinessException("只能删除自己的评论");
        }
        comment.setStatus(STATUS_DELETED);
        momentCommentMapper.updateById(comment);

        long commentCount = momentCommentMapper.selectCount(new LambdaQueryWrapper<MomentComment>()
                .eq(MomentComment::getMomentId, comment.getMomentId())
                .eq(MomentComment::getStatus, STATUS_NORMAL));
        Moment update = new Moment();
        update.setId(comment.getMomentId());
        update.setCommentCount((int) commentCount);
        updateById(update);
    }

    private List<MomentVO> toVOList(List<Moment> moments, Long currentUserId) {
        List<MomentVO> result = new ArrayList<>();
        if (moments == null) {
            return result;
        }
        for (Moment moment : moments) {
            result.add(buildMomentVO(moment, currentUserId));
        }
        return result;
    }

    private MomentVO buildMomentVO(Moment moment, Long currentUserId) {
        MomentVO vo = new MomentVO();
        vo.setId(moment.getId());
        vo.setUserId(moment.getUserId());

        User author = userService.getById(moment.getUserId());
        if (author != null) {
            vo.setUsername(author.getUsername());
            vo.setNickname(author.getNickname() != null ? author.getNickname() : author.getUsername());
            vo.setAvatar(author.getAvatar());
        }

        vo.setContent(moment.getContent());
        vo.setImages(parseImages(moment.getImages()));
        vo.setLocation(moment.getLocation());
        vo.setVisibility(moment.getVisibility());
        vo.setLikeCount(moment.getLikeCount() != null ? moment.getLikeCount() : 0);
        vo.setCommentCount(moment.getCommentCount() != null ? moment.getCommentCount() : 0);
        vo.setCreateTime(moment.getCreateTime());

        List<MomentLike> likes = momentLikeMapper.selectList(new LambdaQueryWrapper<MomentLike>()
                .eq(MomentLike::getMomentId, moment.getId())
                .orderByAsc(MomentLike::getCreateTime));
        List<MomentUserVO> likeUsers = new ArrayList<>();
        boolean liked = false;
        for (MomentLike like : likes) {
            if (Objects.equals(like.getUserId(), currentUserId)) {
                liked = true;
            }
            MomentUserVO userVO = toUserVO(userService.getById(like.getUserId()));
            if (userVO != null) {
                likeUsers.add(userVO);
            }
        }
        vo.setLiked(liked);
        vo.setLikes(likeUsers);
        vo.setComments(getComments(moment.getId()));
        return vo;
    }

    private MomentCommentVO buildCommentVO(MomentComment comment) {
        MomentCommentVO vo = new MomentCommentVO();
        vo.setId(comment.getId());
        vo.setMomentId(comment.getMomentId());
        vo.setUserId(comment.getUserId());

        User author = userService.getById(comment.getUserId());
        if (author != null) {
            vo.setUsername(author.getUsername());
            vo.setNickname(author.getNickname() != null ? author.getNickname() : author.getUsername());
            vo.setAvatar(author.getAvatar());
        }

        vo.setReplyToUserId(comment.getReplyToUserId());
        if (comment.getReplyToUserId() != null) {
            User replyUser = userService.getById(comment.getReplyToUserId());
            if (replyUser != null) {
                vo.setReplyToNickname(replyUser.getNickname() != null ? replyUser.getNickname() : replyUser.getUsername());
            }
        }
        vo.setContent(comment.getContent());
        vo.setCreateTime(comment.getCreateTime());
        return vo;
    }

    private MomentUserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        MomentUserVO vo = new MomentUserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        vo.setAvatar(user.getAvatar());
        return vo;
    }

    private List<String> parseImages(String images) {
        List<String> result = new ArrayList<>();
        if (images == null || images.trim().isEmpty()) {
            return result;
        }
        for (String img : images.split(",")) {
            String trimmed = img.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private int pageOf(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int sizeOf(Integer size) {
        return size == null || size < 1 ? 10 : size;
    }
}
