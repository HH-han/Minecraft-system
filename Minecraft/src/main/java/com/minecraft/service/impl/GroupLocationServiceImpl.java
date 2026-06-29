package com.minecraft.service.impl;

import com.minecraft.dto.request.LocationUpdateDTO;
import com.minecraft.entity.GroupLocationHistory;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupLocationHistoryMapper;
import com.minecraft.mapper.UserMapper;
import com.minecraft.service.GroupLocationService;
import com.minecraft.service.GroupMemberService;
import com.minecraft.vo.MemberLocationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GroupLocationServiceImpl implements GroupLocationService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupLocationHistoryMapper locationHistoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void updateLocation(LocationUpdateDTO dto, Long userId) {
        if (!groupMemberService.isMember(dto.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        GroupLocationHistory history = new GroupLocationHistory();
        history.setGroupId(dto.getGroupId());
        history.setUserId(userId);
        history.setLatitude(dto.getLatitude());
        history.setLongitude(dto.getLongitude());
        history.setAccuracy(dto.getAccuracy());
        locationHistoryMapper.insert(history);

        String redisKey = "group:location:" + dto.getGroupId() + ":" + userId;
        redisTemplate.opsForValue().set(redisKey, history, 30, TimeUnit.SECONDS);
    }

    @Override
    public List<MemberLocationVO> getGroupLocations(Long groupId) {
        List<GroupLocationHistory> histories = locationHistoryMapper.selectByGroupId(groupId);
        
        Map<Long, GroupLocationHistory> latestLocations = histories.stream()
            .collect(Collectors.toMap(
                GroupLocationHistory::getUserId,
                h -> h,
                (existing, replacement) -> existing.getRecordedAt().isAfter(replacement.getRecordedAt()) ? existing : replacement
            ));

        List<MemberLocationVO> result = new ArrayList<>();
        for (Map.Entry<Long, GroupLocationHistory> entry : latestLocations.entrySet()) {
            GroupLocationHistory history = entry.getValue();
            MemberLocationVO vo = new MemberLocationVO();
            
            vo.setUserId(history.getUserId());
            vo.setLatitude(history.getLatitude());
            vo.setLongitude(history.getLongitude());
            vo.setAccuracy(history.getAccuracy());
            vo.setUpdatedAt(history.getRecordedAt());

            User user = userMapper.selectById(history.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<GroupLocationHistory> getUserLocationHistory(Long groupId, Long userId) {
        if (!groupMemberService.isMember(groupId, userId)) {
            throw new BusinessException(403, "不是群成员");
        }
        return locationHistoryMapper.selectByGroupAndUser(groupId, userId);
    }
}