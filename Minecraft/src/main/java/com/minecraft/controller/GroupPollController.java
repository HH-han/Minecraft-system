package com.minecraft.controller;

import com.minecraft.dto.request.GroupPollDTO;
import com.minecraft.dto.request.GroupPollVoteDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupPoll;
import com.minecraft.service.GroupPollService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.GroupPollResultVO;
import com.minecraft.vo.GroupPollVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "投票功能")
@RestController
@RequestMapping("/api/poll")
public class GroupPollController {

    @Autowired
    private GroupPollService groupPollService;

    @Operation(summary = "发起投票")
    @PostMapping("/create")
    public ApiResponse<GroupPoll> createPoll(@RequestBody GroupPollDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        GroupPoll poll = groupPollService.createPoll(dto, userId);
        return ApiResponse.success("创建成功", poll);
    }

    @Operation(summary = "投票")
    @PostMapping("/{pollId}/vote")
    public ApiResponse<Void> vote(@PathVariable Long pollId, @RequestBody GroupPollVoteDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        dto.setPollId(pollId);
        groupPollService.vote(dto, userId);
        return ApiResponse.success("投票成功", null);
    }

    @Operation(summary = "获取投票结果")
    @GetMapping("/{pollId}/result")
    public ApiResponse<GroupPollResultVO> getResult(@PathVariable Long pollId) {
        GroupPollResultVO result = groupPollService.getResult(pollId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "结束投票")
    @PutMapping("/{pollId}/close")
    public ApiResponse<Void> closePoll(@PathVariable Long pollId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupPollService.closePoll(pollId, userId);
        return ApiResponse.success("已结束", null);
    }

    @Operation(summary = "获取群组投票列表")
    @GetMapping("/group/{groupId}")
    public ApiResponse<List<GroupPollVO>> getPollList(@PathVariable Long groupId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GroupPollVO> polls = groupPollService.getPollVOList(groupId, userId);
        return ApiResponse.success(polls);
    }
}