package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.ImCallRecordVO;
import com.minecraft.dto.response.ImFriendVO;
import com.minecraft.dto.response.ImGroupMemberVO;
import com.minecraft.dto.response.ImGroupMessageVO;
import com.minecraft.dto.response.ImGroupVO;
import com.minecraft.dto.response.ImSingleMessageVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.CallRecord;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.ChatMessage;
import com.minecraft.entity.Emoji;
import com.minecraft.entity.Friend;
import com.minecraft.entity.GroupChatMessage;
import com.minecraft.entity.GroupMember;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.CallRecordMapper;
import com.minecraft.mapper.ChatGroupMapper;
import com.minecraft.mapper.ChatMessageMapper;
import com.minecraft.mapper.EmojiMapper;
import com.minecraft.mapper.FriendMapper;
import com.minecraft.mapper.GroupChatMessageMapper;
import com.minecraft.mapper.GroupMemberMapper;
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
    private final UserService userService;
    private final FriendService friendService;

    private static final int MAX_PAGE_SIZE = 200;

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
