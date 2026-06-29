package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupBillSplit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupBillSplitMapper extends BaseMapper<GroupBillSplit> {
    
    List<GroupBillSplit> selectByBillId(@Param("billId") Long billId);
    
    List<GroupBillSplit> selectByGroupId(@Param("groupId") Long groupId);
    
    List<GroupBillSplit> selectUnpaidByUserId(@Param("userId") Long userId);
}