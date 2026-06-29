package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.*;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupAlbumMapper;
import com.minecraft.mapper.GroupPostMapper;
import com.minecraft.mapper.GroupTravelDiaryMapper;
import com.minecraft.mapper.GroupTripPlanMapper;
import com.minecraft.service.GroupMemberService;
import com.minecraft.service.GroupTravelDiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupTravelDiaryServiceImpl extends ServiceImpl<GroupTravelDiaryMapper, GroupTravelDiary> implements GroupTravelDiaryService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupTripPlanMapper tripPlanMapper;

    @Autowired
    private GroupPostMapper postMapper;

    @Autowired
    private GroupAlbumMapper albumMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public GroupTravelDiary generateDiary(Long groupId) {
        GroupTravelDiary existingDiary = baseMapper.selectByGroupId(groupId);
        if (existingDiary != null) {
            existingDiary.setStatus(1);
            updateById(existingDiary);
            return existingDiary;
        }

        List<GroupTripPlan> tripPlans = tripPlanMapper.selectByGroupIdOrdered(groupId);
        List<GroupPost> posts = postMapper.selectByGroupId(groupId);
        List<GroupAlbum> albums = albumMapper.selectByGroupIdOrdered(groupId);

        Map<Integer, List<GroupTripPlan>> plansByDay = tripPlans.stream()
            .collect(Collectors.groupingBy(GroupTripPlan::getDayIndex));

        Map<LocalDateTime, List<GroupPost>> postsByDate = posts.stream()
            .collect(Collectors.groupingBy(p -> p.getCreatedAt().toLocalDate().atStartOfDay()));

        Map<LocalDateTime, List<GroupAlbum>> albumsByDate = albums.stream()
            .collect(Collectors.groupingBy(a -> a.getCreatedAt().toLocalDate().atStartOfDay()));

        ObjectNode diaryContent = objectMapper.createObjectNode();
        ArrayNode daysArray = objectMapper.createArrayNode();

        Set<Integer> allDays = new TreeSet<>(plansByDay.keySet());
        for (Integer dayIndex : allDays) {
            ObjectNode dayNode = objectMapper.createObjectNode();
            dayNode.put("dayIndex", dayIndex);

            List<GroupTripPlan> dayPlans = plansByDay.get(dayIndex);
            if (!dayPlans.isEmpty()) {
                ArrayNode tripItemsArray = objectMapper.createArrayNode();
                for (GroupTripPlan plan : dayPlans) {
                    ObjectNode itemNode = objectMapper.createObjectNode();
                    itemNode.put("activity", plan.getActivity());
                    itemNode.put("location", plan.getLocation());
                    itemNode.put("transport", plan.getTransport());
                    itemNode.put("timeSlot", plan.getTimeSlot());
                    tripItemsArray.add(itemNode);
                }
                dayNode.set("tripItems", tripItemsArray);

                String title = dayPlans.get(0).getActivity();
                if (title.length() > 20) {
                    title = title.substring(0, 20) + "...";
                }
                dayNode.put("title", "Day " + dayIndex + "：" + title);
            } else {
                dayNode.put("title", "Day " + dayIndex);
            }

            daysArray.add(dayNode);
        }

        diaryContent.set("days", daysArray);
        
        ObjectNode statistics = objectMapper.createObjectNode();
        statistics.put("totalDays", allDays.size());
        statistics.put("totalPosts", posts.size());
        statistics.put("totalPhotos", albums.size());
        diaryContent.set("statistics", statistics);

        GroupTravelDiary diary = new GroupTravelDiary();
        diary.setGroupId(groupId);
        diary.setTitle("旅行日记");
        diary.setContentJson(diaryContent.toString());
        diary.setStatus(1);

        if (!albums.isEmpty()) {
            diary.setCoverUrl(albums.get(0).getImageUrl());
        }

        save(diary);

        return diary;
    }

    @Override
    public GroupTravelDiary getDiary(Long groupId) {
        GroupTravelDiary diary = baseMapper.selectByGroupId(groupId);
        if (diary == null) {
            return generateDiary(groupId);
        }
        return diary;
    }

    @Override
    public byte[] exportDiary(Long diaryId, String format) {
        GroupTravelDiary diary = getById(diaryId);
        if (diary == null) {
            throw new BusinessException(404, "日记不存在");
        }

        String content = diary.getContentJson();
        return content.getBytes();
    }
}