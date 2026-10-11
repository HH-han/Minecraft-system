package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.FavoriteVO;
import com.minecraft.dto.response.ImCallRecordVO;
import com.minecraft.dto.response.ImFriendVO;
import com.minecraft.dto.response.ImGroupMemberVO;
import com.minecraft.dto.response.ImGroupMessageVO;
import com.minecraft.dto.response.ImGroupVO;
import com.minecraft.dto.response.ImSingleMessageVO;
import com.minecraft.dto.response.MomentCommentVO;
import com.minecraft.dto.response.MomentUserVO;
import com.minecraft.dto.response.MomentVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.CallRecord;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.ChatMessage;
import com.minecraft.entity.Emoji;
import com.minecraft.entity.Favorite;
import com.minecraft.entity.Friend;
import com.minecraft.entity.GroupChatMessage;
import com.minecraft.entity.GroupMember;
import com.minecraft.entity.Moment;
import com.minecraft.entity.MomentComment;
import com.minecraft.entity.MomentLike;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.CallRecordMapper;
import com.minecraft.mapper.ChatGroupMapper;
import com.minecraft.mapper.ChatMessageMapper;
import com.minecraft.mapper.EmojiMapper;
import com.minecraft.mapper.FavoriteMapper;
import com.minecraft.mapper.FriendMapper;
import com.minecraft.mapper.GroupChatMessageMapper;
import com.minecraft.mapper.GroupMemberMapper;
import com.minecraft.mapper.MomentCommentMapper;
import com.minecraft.mapper.MomentLikeMapper;
import com.minecraft.mapper.MomentMapper;
import com.minecraft.service.FriendService;
import com.minecraft.service.ImAdminService;
import com.minecraft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 即时通讯管理端服务实现，直接使用各 BaseMapper 做分页查询与数据装配。
 */
@Service
@RequiredArgsConstructor
public class ImAdminServiceImpl implements ImAdminService {

    private final FriendMapper friendMapper;
    private final ChatGroupMapper chatGroupMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final GroupChatMessageMapper groupChatMessageMapper;
    private final CallRecordMapper callRecordMapper;
    private final EmojiMapper emojiMapper;
    private final MomentMapper momentMapper;
    private final MomentCommentMapper momentCommentMapper;
    private final MomentLikeMapper momentLikeMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserService userService;
    private final FriendService friendService;

    private static final int MAX_PAGE_SIZE = 200;
    /** 正常状态（朋友圈/收藏通用） */
    private static final int STATUS_NORMAL = 1;
    /** 已删除状态 */
    private static final int STATUS_DELETED = 0;

    // =========================================================
    // 好友关系
    // =========================================================

    @Override
    public PageResponse<ImFriendVO> pageFriends(Integer page, Integer size, String keyword, Integer status) {
        Page<Friend> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Friend::getStatus, status);
        }
        String kw = trim(keyword);
        if (kw != null) {
            if (kw.matches("\\d+")) {
                Long uid = Long.valueOf(kw);
                wrapper.and(w -> w.eq(Friend::getUserId, uid).or().eq(Friend::getFriendId, uid));
            } else {
                wrapper.like(Friend::getRemark, kw);
            }
        }
        wrapper.orderByDesc(Friend::getCreateTime);

        Page<Friend> result = friendMapper.selectPage(pageParam, wrapper);
        Map<Long, User> userCache = new HashMap<>();
        List<ImFriendVO> vos = new ArrayList<>();
        for (Friend friend : result.getRecords()) {
            ImFriendVO vo = new ImFriendVO();
            vo.setId(friend.getId());
            vo.setUserId(friend.getUserId());
            vo.setFriendId(friend.getFriendId());
            vo.setRemark(friend.getRemark());
            vo.setStatus(friend.getStatus());
            vo.setCreateTime(friend.getCreateTime());

            User u = user(friend.getUserId(), userCache);
            if (u != null) {
                vo.setUserUsername(u.getUsername());
                vo.setUserNickname(u.getNickname());
                vo.setUserAvatar(u.getAvatar());
            }
            User f = user(friend.getFriendId(), userCache);
            if (f != null) {
                vo.setFriendUsername(f.getUsername());
                vo.setFriendNickname(f.getNickname());
                vo.setFriendAvatar(f.getAvatar());
            }
            vos.add(vo);
        }
        return new PageResponse<>(vos, result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    @Override
    public void deleteFriendRelation(Long id) {
        Friend friend = friendMapper.selectById(id);
        if (friend == null) {
            throw new BusinessException(404, "好友关系不存在");
        }
        // 删除双向关系
        friendService.deleteFriend(friend.getUserId(), friend.getFriendId());
    }

    @Override
    public void approveFriendRequest(Long id) {
        Friend friend = friendMapper.selectById(id);
        if (friend == null) {
            throw new BusinessException(404, "好友申请不存在");
        }
        friendService.acceptFriend(friend.getFriendId(), friend.getUserId());
    }

    @Override
    public void rejectFriendRequest(Long id) {
        Friend friend = friendMapper.selectById(id);
        if (friend == null) {
            throw new BusinessException(404, "好友申请不存在");
        }
        friendService.deleteFriend(friend.getUserId(), friend.getFriendId());
    }

    // =========================================================
    // 聊天群组
    // =========================================================

    @Override
    public PageResponse<ImGroupVO> pageGroups(Integer page, Integer size, String keyword) {
        Page<ChatGroup> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<ChatGroup> wrapper = new LambdaQueryWrapper<>();
        String kw = trim(keyword);
        if (kw != null) {
            wrapper.and(w -> w.like(ChatGroup::getName, kw).or().like(ChatGroup::getDescription, kw));
        }
        wrapper.orderByDesc(ChatGroup::getCreateTime);

        Page<ChatGroup> result = chatGroupMapper.selectPage(pageParam, wrapper);
        Map<Long, User> userCache = new HashMap<>();
        List<ImGroupVO> vos = new ArrayList<>();
        for (ChatGroup group : result.getRecords()) {
            ImGroupVO vo = new ImGroupVO();
            vo.setId(group.getId());
            vo.setName(group.getName());
            vo.setDescription(group.getDescription());
            vo.setCreatorId(group.getCreatorId());
            vo.setMaxMembers(group.getMaxMembers());
            vo.setMemberCount(group.getMemberCount());
            vo.setAvatar(group.getAvatar());
            vo.setCreateTime(group.getCreateTime());
            vo.setUpdateTime(group.getUpdateTime());

            User creator = user(group.getCreatorId(), userCache);
            if (creator != null) {
                vo.setCreatorName(displayName(creator));
            }
            vos.add(vo);
        }
        return new PageResponse<>(vos, result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    @Override
    public List<ImGroupMemberVO> listGroupMembers(Long groupId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId)
                .orderByAsc(GroupMember::getJoinTime);
        List<GroupMember> members = groupMemberMapper.selectList(wrapper);

        Map<Long, User> userCache = new HashMap<>();
        List<ImGroupMemberVO> vos = new ArrayList<>();
        for (GroupMember member : members) {
            ImGroupMemberVO vo = new ImGroupMemberVO();
            vo.setId(member.getId());
            vo.setGroupId(member.getGroupId());
            vo.setUserId(member.getUserId());
            vo.setRole(member.getRole());
            vo.setStatus(member.getStatus());
            vo.setGroupNickname(member.getNickname());
            vo.setJoinTime(member.getJoinTime());
            vo.setExitTime(member.getExitTime());

            User u = user(member.getUserId(), userCache);
            if (u != null) {
                vo.setUsername(u.getUsername());
                vo.setNickname(u.getNickname());
                vo.setAvatar(u.getAvatar());
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public void updateGroup(Long id, ChatGroup group) {
        ChatGroup exist = chatGroupMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(404, "群组不存在");
        }
        group.setId(id);
        // 避免误改创建者
        group.setCreatorId(null);
        chatGroupMapper.updateById(group);
    }

    @Override
    public void deleteGroup(Long id) {
        // 删除群成员
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, id);
        groupMemberMapper.delete(wrapper);
        // 删除群聊消息
        LambdaQueryWrapper<GroupChatMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(GroupChatMessage::getGroupId, id);
        groupChatMessageMapper.delete(msgWrapper);
        chatGroupMapper.deleteById(id);
    }

    @Override
    public void removeGroupMember(Long groupId, Long userId) {
        ChatGroup group = chatGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException(404, "群组不存在");
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId);
        groupMemberMapper.delete(wrapper);
        group.setMemberCount(Math.max(0, (group.getMemberCount() == null ? 0 : group.getMemberCount()) - 1));
        chatGroupMapper.updateById(group);
    }

    // =========================================================
    // 聊天消息
    // =========================================================

    @Override
    public PageResponse<ImSingleMessageVO> pageSingleMessages(Integer page, Integer size, Long userId, String keyword, String messageType) {
        Page<ChatMessage> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.and(w -> w.eq(ChatMessage::getSenderId, userId).or().eq(ChatMessage::getReceiverId, userId));
        }
        String kw = trim(keyword);
        if (kw != null) {
            wrapper.like(ChatMessage::getContent, kw);
        }
        if (StringUtils.hasText(messageType)) {
            wrapper.eq(ChatMessage::getMessageType, messageType);
        }
        wrapper.orderByDesc(ChatMessage::getCreateTime);

        Page<ChatMessage> result = chatMessageMapper.selectPage(pageParam, wrapper);
        Map<Long, User> userCache = new HashMap<>();
        List<ImSingleMessageVO> vos = new ArrayList<>();
        for (ChatMessage msg : result.getRecords()) {
            ImSingleMessageVO vo = new ImSingleMessageVO();
            vo.setId(msg.getId());
            vo.setSenderId(msg.getSenderId());
            vo.setReceiverId(msg.getReceiverId());
            vo.setContent(msg.getContent());
            vo.setMessageType(msg.getMessageType());
            vo.setIsRead(msg.getIsRead());
            vo.setCreateTime(msg.getCreateTime());

            User sender = user(msg.getSenderId(), userCache);
            if (sender != null) {
                vo.setSenderName(displayName(sender));
                vo.setSenderAvatar(sender.getAvatar());
            }
            User receiver = user(msg.getReceiverId(), userCache);
            if (receiver != null) {
                vo.setReceiverName(displayName(receiver));
                vo.setReceiverAvatar(receiver.getAvatar());
            }
            vos.add(vo);
        }
        return new PageResponse<>(vos, result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    @Override
    public PageResponse<ImGroupMessageVO> pageGroupMessages(Integer page, Integer size, Long groupId, String keyword) {
        Page<GroupChatMessage> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<GroupChatMessage> wrapper = new LambdaQueryWrapper<>();
        if (groupId != null) {
            wrapper.eq(GroupChatMessage::getGroupId, groupId);
        }
        String kw = trim(keyword);
        if (kw != null) {
            wrapper.like(GroupChatMessage::getContent, kw);
        }
        wrapper.orderByDesc(GroupChatMessage::getCreateTime);

        Page<GroupChatMessage> result = groupChatMessageMapper.selectPage(pageParam, wrapper);
        Map<Long, User> userCache = new HashMap<>();
        Map<Long, String> groupNameCache = new HashMap<>();
        List<ImGroupMessageVO> vos = new ArrayList<>();
        for (GroupChatMessage msg : result.getRecords()) {
            ImGroupMessageVO vo = new ImGroupMessageVO();
            vo.setId(msg.getId());
            vo.setGroupId(msg.getGroupId());
            vo.setSenderId(msg.getSenderId());
            vo.setContent(msg.getContent());
            vo.setMessageType(msg.getMessageType());
            vo.setCreateTime(msg.getCreateTime());
            vo.setSenderName(msg.getSenderName());
            vo.setSenderAvatar(msg.getSenderAvatar());

            if (vo.getSenderName() == null || vo.getSenderAvatar() == null) {
                User sender = user(msg.getSenderId(), userCache);
                if (sender != null) {
                    if (vo.getSenderName() == null) {
                        vo.setSenderName(displayName(sender));
                    }
                    if (vo.getSenderAvatar() == null) {
                        vo.setSenderAvatar(sender.getAvatar());
                    }
                }
            }
            vo.setGroupName(groupName(msg.getGroupId(), groupNameCache));
            vos.add(vo);
        }
        return new PageResponse<>(vos, result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    // =========================================================
    // 通话记录
    // =========================================================

    @Override
    public PageResponse<ImCallRecordVO> pageCallRecords(Integer page, Integer size, String keyword, String callType) {
        Page<CallRecord> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<CallRecord> wrapper = new LambdaQueryWrapper<>();
        String kw = trim(keyword);
        if (kw != null) {
            if (kw.matches("\\d+")) {
                Long uid = Long.valueOf(kw);
                wrapper.and(w -> w.eq(CallRecord::getCallerId, uid).or().eq(CallRecord::getReceiverId, uid));
            } else {
                wrapper.like(CallRecord::getCallId, kw);
            }
        }
        if (StringUtils.hasText(callType)) {
            wrapper.eq(CallRecord::getCallType, callType);
        }
        wrapper.orderByDesc(CallRecord::getStartTime);

        Page<CallRecord> result = callRecordMapper.selectPage(pageParam, wrapper);
        Map<Long, User> userCache = new HashMap<>();
        List<ImCallRecordVO> vos = new ArrayList<>();
        for (CallRecord record : result.getRecords()) {
            ImCallRecordVO vo = new ImCallRecordVO();
            vo.setId(record.getId());
            vo.setCallId(record.getCallId());
            vo.setCallerId(record.getCallerId());
            vo.setReceiverId(record.getReceiverId());
            vo.setCallType(record.getCallType());
            vo.setStatus(record.getStatus());
            vo.setDuration(record.getDuration());
            vo.setStartTime(record.getStartTime());
            vo.setEndTime(record.getEndTime());
            vo.setCreatedAt(record.getCreatedAt());

            User caller = user(record.getCallerId(), userCache);
            if (caller != null) {
                vo.setCallerName(displayName(caller));
            }
            User receiver = user(record.getReceiverId(), userCache);
            if (receiver != null) {
                vo.setReceiverName(displayName(receiver));
            }
            vos.add(vo);
        }
        return new PageResponse<>(vos, result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    @Override
    public void deleteCallRecord(Long id) {
        callRecordMapper.deleteById(id);
    }

    // =========================================================
    // 系统表情
    // =========================================================

    @Override
    public PageResponse<Emoji> pageEmojis(Integer page, Integer size, String keyword, String category, Integer isActive) {
        Page<Emoji> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<Emoji> wrapper = new LambdaQueryWrapper<>();
        String kw = trim(keyword);
        if (kw != null) {
            wrapper.and(w -> w.like(Emoji::getEmojiName, kw).or().like(Emoji::getEmojiCode, kw));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Emoji::getCategory, category);
        }
        if (isActive != null) {
            wrapper.eq(Emoji::getIsActive, isActive);
        }
        wrapper.orderByAsc(Emoji::getCategory).orderByAsc(Emoji::getSortOrder);

        Page<Emoji> result = emojiMapper.selectPage(pageParam, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    @Override
    public Long createEmoji(Emoji emoji) {
        if (!StringUtils.hasText(emoji.getEmojiCode())) {
            throw new BusinessException(400, "表情编码不能为空");
        }
        if (!StringUtils.hasText(emoji.getEmojiName())) {
            throw new BusinessException(400, "表情名称不能为空");
        }
        if (emoji.getIsActive() == null) {
            emoji.setIsActive(1);
        }
        if (emoji.getSortOrder() == null) {
            emoji.setSortOrder(0);
        }
        emoji.setId(null);
        emojiMapper.insert(emoji);
        return emoji.getId();
    }

    @Override
    public void updateEmoji(Long id, Emoji emoji) {
        Emoji exist = emojiMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(404, "表情不存在");
        }
        emoji.setId(id);
        emojiMapper.updateById(emoji);
    }

    @Override
    public void deleteEmoji(Long id) {
        emojiMapper.deleteById(id);
    }

    // =========================================================
    // 用户所在群
    // =========================================================

    @Override
    public List<ImGroupVO> listUserGroups(Long userId) {
        List<ImGroupVO> vos = new ArrayList<>();
        if (userId == null) {
            return vos;
        }
        LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(GroupMember::getUserId, userId);
        List<GroupMember> memberships = groupMemberMapper.selectList(memberWrapper);
        if (memberships.isEmpty()) {
            return vos;
        }
        List<Long> groupIds = new ArrayList<>();
        Map<Long, String> roleByGroup = new HashMap<>();
        for (GroupMember member : memberships) {
            groupIds.add(member.getGroupId());
            roleByGroup.put(member.getGroupId(), member.getRole());
        }
        List<ChatGroup> groups = chatGroupMapper.selectBatchIds(groupIds);
        Map<Long, User> userCache = new HashMap<>();
        for (ChatGroup group : groups) {
            ImGroupVO vo = new ImGroupVO();
            vo.setId(group.getId());
            vo.setName(group.getName());
            vo.setDescription(group.getDescription());
            vo.setCreatorId(group.getCreatorId());
            vo.setMaxMembers(group.getMaxMembers());
            vo.setMemberCount(group.getMemberCount());
            vo.setAvatar(group.getAvatar());
            vo.setCreateTime(group.getCreateTime());
            vo.setUpdateTime(group.getUpdateTime());
            vo.setRole(roleByGroup.get(group.getId()));
            User creator = user(group.getCreatorId(), userCache);
            if (creator != null) {
                vo.setCreatorName(displayName(creator));
            }
            vos.add(vo);
        }
        return vos;
    }

    // =========================================================
    // 朋友圈
    // =========================================================

    @Override
    public PageResponse<MomentVO> pageMoments(Integer page, Integer size, Long userId, String keyword, Integer visibility) {
        Page<Moment> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moment::getStatus, STATUS_NORMAL);
        if (userId != null) {
            wrapper.eq(Moment::getUserId, userId);
        }
        if (visibility != null) {
            wrapper.eq(Moment::getVisibility, visibility);
        }
        String kw = trim(keyword);
        if (kw != null) {
            wrapper.like(Moment::getContent, kw);
        }
        wrapper.orderByDesc(Moment::getCreateTime);

        Page<Moment> result = momentMapper.selectPage(pageParam, wrapper);
        Map<Long, User> userCache = new HashMap<>();
        List<MomentVO> vos = new ArrayList<>();
        for (Moment moment : result.getRecords()) {
            vos.add(buildMomentVO(moment, userCache));
        }
        return new PageResponse<>(vos, result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    @Override
    public List<MomentCommentVO> listMomentComments(Long momentId) {
        List<MomentComment> comments = momentCommentMapper.selectList(new LambdaQueryWrapper<MomentComment>()
                .eq(MomentComment::getMomentId, momentId)
                .eq(MomentComment::getStatus, STATUS_NORMAL)
                .orderByAsc(MomentComment::getCreateTime));
        List<MomentCommentVO> result = new ArrayList<>();
        for (MomentComment comment : comments) {
            MomentCommentVO vo = new MomentCommentVO();
            vo.setId(comment.getId());
            vo.setMomentId(comment.getMomentId());
            vo.setUserId(comment.getUserId());

            User author = userService.getById(comment.getUserId());
            if (author != null) {
                vo.setUsername(author.getUsername());
                vo.setNickname(displayName(author));
                vo.setAvatar(author.getAvatar());
            }
            vo.setReplyToUserId(comment.getReplyToUserId());
            if (comment.getReplyToUserId() != null) {
                User replyUser = userService.getById(comment.getReplyToUserId());
                if (replyUser != null) {
                    vo.setReplyToNickname(displayName(replyUser));
                }
            }
            vo.setContent(comment.getContent());
            vo.setCreateTime(comment.getCreateTime());
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<MomentUserVO> listMomentLikes(Long momentId) {
        List<MomentLike> likes = momentLikeMapper.selectList(new LambdaQueryWrapper<MomentLike>()
                .eq(MomentLike::getMomentId, momentId)
                .orderByAsc(MomentLike::getCreateTime));
        List<MomentUserVO> result = new ArrayList<>();
        for (MomentLike like : likes) {
            User user = userService.getById(like.getUserId());
            if (user != null) {
                MomentUserVO vo = new MomentUserVO();
                vo.setUserId(user.getId());
                vo.setUsername(user.getUsername());
                vo.setNickname(displayName(user));
                vo.setAvatar(user.getAvatar());
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public void deleteMoment(Long momentId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new BusinessException(404, "动态不存在");
        }
        Moment update = new Moment();
        update.setId(momentId);
        update.setStatus(STATUS_DELETED);
        momentMapper.updateById(update);
    }

    // =========================================================
    // 收藏
    // =========================================================

    @Override
    public PageResponse<FavoriteVO> pageFavorites(Integer page, Integer size, Long userId, String type, String tag, String keyword) {
        Page<Favorite> pageParam = new Page<>(normalizePage(page), normalizeSize(size));
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getStatus, STATUS_NORMAL);
        if (userId != null) {
            wrapper.eq(Favorite::getUserId, userId);
        }
        String typeValue = trim(type);
        if (typeValue != null) {
            wrapper.eq(Favorite::getType, typeValue);
        }
        String tagValue = trim(tag);
        if (tagValue != null) {
            wrapper.like(Favorite::getTags, tagValue);
        }
        String kw = trim(keyword);
        if (kw != null) {
            wrapper.and(w -> w.like(Favorite::getTitle, kw)
                    .or().like(Favorite::getContent, kw)
                    .or().like(Favorite::getTags, kw)
                    .or().like(Favorite::getFileName, kw));
        }
        wrapper.orderByDesc(Favorite::getUpdateTime).orderByDesc(Favorite::getId);

        Page<Favorite> result = favoriteMapper.selectPage(pageParam, wrapper);
        List<FavoriteVO> vos = new ArrayList<>();
        for (Favorite favorite : result.getRecords()) {
            vos.add(buildFavoriteVO(favorite));
        }
        return new PageResponse<>(vos, result.getTotal(), normalizePage(page), normalizeSize(size));
    }

    @Override
    public void deleteFavorite(Long id) {
        Favorite favorite = favoriteMapper.selectById(id);
        if (favorite == null) {
            throw new BusinessException(404, "收藏不存在");
        }
        Favorite update = new Favorite();
        update.setId(id);
        update.setStatus(STATUS_DELETED);
        favoriteMapper.updateById(update);
    }

    private MomentVO buildMomentVO(Moment moment, Map<Long, User> userCache) {
        MomentVO vo = new MomentVO();
        vo.setId(moment.getId());
        vo.setUserId(moment.getUserId());

        User author = user(moment.getUserId(), userCache);
        if (author != null) {
            vo.setUsername(author.getUsername());
            vo.setNickname(displayName(author));
            vo.setAvatar(author.getAvatar());
        }
        vo.setContent(moment.getContent());
        vo.setImages(splitToList(moment.getImages()));
        vo.setLocation(moment.getLocation());
        vo.setVisibility(moment.getVisibility());
        vo.setLikeCount(moment.getLikeCount() != null ? moment.getLikeCount() : 0);
        vo.setCommentCount(moment.getCommentCount() != null ? moment.getCommentCount() : 0);
        vo.setCreateTime(moment.getCreateTime());
        return vo;
    }

    private FavoriteVO buildFavoriteVO(Favorite favorite) {
        FavoriteVO vo = new FavoriteVO();
        vo.setId(favorite.getId());
        vo.setUserId(favorite.getUserId());
        vo.setType(favorite.getType());
        vo.setTitle(favorite.getTitle());
        vo.setContent(favorite.getContent());
        vo.setCover(favorite.getCover());
        vo.setUrl(favorite.getUrl());
        vo.setFileName(favorite.getFileName());
        vo.setFileSize(favorite.getFileSize());
        vo.setTags(splitToList(favorite.getTags()));
        vo.setSource(favorite.getSource());
        vo.setCreateTime(favorite.getCreateTime());
        vo.setUpdateTime(favorite.getUpdateTime());
        return vo;
    }

    // =========================================================
    // 工具方法
    // =========================================================

    private int normalizePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<String> splitToList(String value) {
        List<String> result = new ArrayList<>();
        if (value == null) {
            return result;
        }
        for (String item : value.split(",")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private User user(Long id, Map<Long, User> cache) {
        if (id == null) {
            return null;
        }
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        User u = userService.getById(id);
        cache.put(id, u);
        return u;
    }

    private String groupName(Long groupId, Map<Long, String> cache) {
        if (groupId == null) {
            return null;
        }
        if (cache.containsKey(groupId)) {
            return cache.get(groupId);
        }
        ChatGroup group = chatGroupMapper.selectById(groupId);
        String name = group != null ? group.getName() : null;
        cache.put(groupId, name);
        return name;
    }

    private String displayName(User user) {
        if (user == null) {
            return null;
        }
        return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
    }
}
