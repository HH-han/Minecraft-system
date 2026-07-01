package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.GroupPollDTO;
import com.minecraft.dto.request.GroupPollVoteDTO;
import com.minecraft.entity.GroupPoll;
import com.minecraft.vo.GroupPollResultVO;
import com.minecraft.vo.GroupPollVO;

import java.util.List;

public interface GroupPollService extends IService<GroupPoll> {
    GroupPoll createPoll(GroupPollDTO dto, Long userId);
    void vote(GroupPollVoteDTO dto, Long userId);
    GroupPollResultVO getResult(Long pollId);
    void closePoll(Long pollId, Long userId);
    List<GroupPoll> getPollList(Long groupId);
    List<GroupPollVO> getPollVOList(Long groupId, Long userId);
}