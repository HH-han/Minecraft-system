package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Friend;

import java.util.List;

public interface FriendService extends IService<Friend> {
    void addFriend(Long userId, Long friendId);
    void acceptFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    List<Friend> getFriendList(Long userId);
    List<Friend> getFriendRequestList(Long userId);
}
