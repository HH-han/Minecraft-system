package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupTravelDiary;
import com.minecraft.service.GroupTravelDiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "旅行日记")
@RestController
@RequestMapping("/api/diary")
public class GroupTravelDiaryController {

    @Autowired
    private GroupTravelDiaryService groupTravelDiaryService;

    @Operation(summary = "生成旅行日记")
    @GetMapping("/{groupId}/generate")
    public ApiResponse<GroupTravelDiary> generateDiary(@PathVariable Long groupId) {
        GroupTravelDiary diary = groupTravelDiaryService.generateDiary(groupId);
        return ApiResponse.success("生成成功", diary);
    }

    @Operation(summary = "获取日记")
    @GetMapping("/{groupId}")
    public ApiResponse<GroupTravelDiary> getDiary(@PathVariable Long groupId) {
        GroupTravelDiary diary = groupTravelDiaryService.getDiary(groupId);
        return ApiResponse.success(diary);
    }

    @Operation(summary = "导出日记")
    @PostMapping("/{diaryId}/export")
    public ApiResponse<byte[]> exportDiary(@PathVariable Long diaryId, @RequestParam(defaultValue = "json") String format) throws IOException {
        byte[] data = groupTravelDiaryService.exportDiary(diaryId, format);
        return ApiResponse.success(data);
    }
}