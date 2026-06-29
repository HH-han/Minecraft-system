package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.GroupPollDTO;
import com.minecraft.dto.request.GroupPollVoteDTO;
import com.minecraft.entity.GroupPoll;
import com.minecraft.entity.GroupPollVote;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupPollMapper;
import com.minecraft.mapper.GroupPollVoteMapper;
import com.minecraft.service.GroupMemberService;
import com.minecraft.service.GroupPollService;
import com.minecraft.vo.GroupPollResultVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupPollServiceImpl extends ServiceImpl<GroupPollMapper, GroupPoll> implements GroupPollService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupPollVoteMapper pollVoteMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public GroupPoll createPoll(GroupPollDTO dto, Long userId) {
        if (!groupMemberService.isMember(dto.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        GroupPoll poll = new GroupPoll();
        BeanUtils.copyProperties(dto, poll);
        poll.setCreatorId(userId);
        poll.setStatus(1);
        if (dto.getIsAnonymous() == null) {
            poll.setIsAnonymous(false);
        }
        save(poll);

        return poll;
    }

    @Override
    @Transactional
    public void vote(GroupPollVoteDTO dto, Long userId) {
        GroupPoll poll = getById(dto.getPollId());
        if (poll == null) {
            throw new BusinessException(404, "投票不存在");
        }
        if (!groupMemberService.isMember(poll.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }
        if (poll.getStatus() != 1) {
            throw new BusinessException(400, "投票已结束");
        }

        GroupPollVote existingVote = pollVoteMapper.selectByPollAndUser(dto.getPollId(), userId);
        if (existingVote != null) {
            throw new BusinessException(400, "已参与投票");
        }

        GroupPollVote vote = new GroupPollVote();
        vote.setPollId(dto.getPollId());
        vote.setUserId(userId);
        vote.setOptionId(dto.getOptionId());
        vote.setScore(dto.getScore());
        pollVoteMapper.insert(vote);
    }

    @Override
    public GroupPollResultVO getResult(Long pollId) {
        GroupPoll poll = getById(pollId);
        if (poll == null) {
            throw new BusinessException(404, "投票不存在");
        }

        List<GroupPollVote> votes = pollVoteMapper.selectByPollId(pollId);
        
        Map<Integer, Integer> optionVotes = new HashMap<>();
        int totalVotes = votes.size();

        for (GroupPollVote vote : votes) {
            optionVotes.merge(vote.getOptionId(), 1, Integer::sum);
        }

        Map<Integer, Double> optionPercentages = new HashMap<>();
        if (totalVotes > 0) {
            for (Map.Entry<Integer, Integer> entry : optionVotes.entrySet()) {
                optionPercentages.put(entry.getKey(), (entry.getValue() * 100.0) / totalVotes);
            }
        }

        GroupPollResultVO result = new GroupPollResultVO();
        result.setPollId(pollId);
        result.setTotalVotes(totalVotes);
        result.setOptionVotes(optionVotes);
        result.setOptionPercentages(optionPercentages);

        return result;
    }

    @Override
    @Transactional
    public void closePoll(Long pollId, Long userId) {
        GroupPoll poll = getById(pollId);
        if (poll == null) {
            throw new BusinessException(404, "投票不存在");
        }
        if (!poll.getCreatorId().equals(userId) && !groupMemberService.isAdmin(poll.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限结束投票");
        }

        poll.setStatus(2);
        updateById(poll);
    }

    @Override
    public List<GroupPoll> getPollList(Long groupId) {
        return baseMapper.selectByGroupId(groupId);
    }
}