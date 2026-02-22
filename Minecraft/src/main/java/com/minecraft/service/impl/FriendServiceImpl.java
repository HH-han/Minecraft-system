package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Friend;
import com.minecraft.enums.FriendStatus;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.FriendMapper;
import com.minecraft.service.FriendService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BusinessException("不能添加自己为好友");
        }

        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getUserId, userId)
                .eq(Friend::getFriendId, friendId);
        if (getOne(wrapper) != null) {
            throw new BusinessException("已经是好友或已发送好友请求");
        }

        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus(FriendStatus.PENDING.ordinal());
        save(friend);
    }

    @Override
    public void acceptFriend(Long userId, Long friendId) {
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getUserId, friendId)
                .eq(Friend::getFriendId, userId);
        Friend friend = getOne(wrapper);
        if (friend == null) {
            throw new BusinessException("好友请求不存在");
        }
        friend.setStatus(FriendStatus.ACCEPTED.ordinal());
        updateById(friend);

        Friend reverseFriend = new Friend();
        reverseFriend.setUserId(userId);
        reverseFriend.setFriendId(friendId);
        reverseFriend.setStatus(FriendStatus.ACCEPTED.ordinal());
        save(reverseFriend);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Friend::getUserId, userId).eq(Friend::getFriendId, friendId))
                .or()
                .eq(Friend::getUserId, friendId)
                .eq(Friend::getFriendId, userId);
        remove(wrapper);
    }

    @Override
    public List<Friend> getFriendList(Long userId) {
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getUserId, userId)
                .eq(Friend::getStatus, FriendStatus.ACCEPTED.ordinal());
        return list(wrapper);
    }

    @Override
    public List<Friend> getFriendRequestList(Long userId) {
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getFriendId, userId)
                .eq(Friend::getStatus, FriendStatus.PENDING.ordinal());
        return list(wrapper);
    }
}
