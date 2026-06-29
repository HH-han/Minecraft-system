package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupBillMapper extends BaseMapper<GroupBill> {
    
    List<GroupBill> selectByGroupId(@Param("groupId") Long groupId);
    
    List<GroupBill> selectUnsettledByGroupId(@Param("groupId") Long groupId);
}