package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.GroupTravelDiary;

public interface GroupTravelDiaryService extends IService<GroupTravelDiary> {
    GroupTravelDiary generateDiary(Long groupId);
    GroupTravelDiary getDiary(Long groupId);
    byte[] exportDiary(Long diaryId, String format);
}