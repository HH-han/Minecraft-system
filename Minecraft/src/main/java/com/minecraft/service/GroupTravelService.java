package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.GroupTravel;

public interface GroupTravelService extends IService<GroupTravel> {
    PageResponse<?> getGroupList(PageRequest request);
    GroupTravel getGroupDetail(Long id);
    void createGroup(GroupTravel group);
    void updateGroup(GroupTravel group);
    void deleteGroup(Long id);
    void joinGroup(Long groupId, Long userId);
    void leaveGroup(Long groupId, Long userId);
}
