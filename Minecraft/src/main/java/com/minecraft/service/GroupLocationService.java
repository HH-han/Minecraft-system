package com.minecraft.service;

import com.minecraft.dto.request.LocationUpdateDTO;
import com.minecraft.entity.GroupLocationHistory;
import com.minecraft.vo.MemberLocationVO;

import java.util.List;

public interface GroupLocationService {
    void updateLocation(LocationUpdateDTO dto, Long userId);
    List<MemberLocationVO> getGroupLocations(Long groupId);
    List<GroupLocationHistory> getUserLocationHistory(Long groupId, Long userId);
}