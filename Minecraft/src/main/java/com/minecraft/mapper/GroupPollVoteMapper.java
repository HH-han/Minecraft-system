package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupPollVote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupPollVoteMapper extends BaseMapper<GroupPollVote> {
    
    List<GroupPollVote> selectByPollId(@Param("pollId") Long pollId);
    
    List<GroupPollVote> selectByPollIdAndOptionId(@Param("pollId") Long pollId, @Param("optionId") Integer optionId);
    
    GroupPollVote selectByPollAndUser(@Param("pollId") Long pollId, @Param("userId") Long userId);
}