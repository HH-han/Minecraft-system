package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.GroupCreateDTO;
import com.minecraft.dto.request.GroupUpdateDTO;
import com.minecraft.entity.TravelGroup;
import com.minecraft.vo.TravelGroupVO;

import java.util.List;

public interface TravelGroupService extends IService<TravelGroup> {
    Long createGroup(GroupCreateDTO dto, Long userId);
    void updateGroup(GroupUpdateDTO dto);
    void dissolveGroup(Long groupId, Long userId);
    TravelGroupVO getGroupDetail(Long groupId, Long userId);
    List<TravelGroupVO> getMyGroups(Long userId);
    List<TravelGroup> searchPublicGroups(String keyword);
    void transferOwner(Long groupId, Long newOwnerId, Long currentUserId);
}