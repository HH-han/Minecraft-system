package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.response.FriendInfoDTO;
import com.minecraft.dto.response.FriendRequestDTO;
import com.minecraft.entity.Friend;

import java.util.List;

public interface FriendService extends IService<Friend> {
    void addFriend(Long userId, Long friendId);
    void acceptFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    void updateRemark(Long userId, Long friendId, String remark);
    List<Friend> getFriendList(Long userId);
    List<FriendInfoDTO> getFriendInfoList(Long userId);
    List<Friend> getFriendRequestList(Long userId);
    List<FriendRequestDTO> getFriendRequestInfoList(Long userId);
    boolean isFriend(Long userId, Long friendId);
}
