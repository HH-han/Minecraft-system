package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 根据用户ID查询最近的操作日志
     */
    @Select("SELECT * FROM operation_logs WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<OperationLog> selectRecentLogsByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /**
     * 统计指定时间范围内的操作次数
     */
    @Select("SELECT COUNT(*) FROM operation_logs WHERE created_at BETWEEN #{startTime} AND #{endTime}")
    Long countByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
