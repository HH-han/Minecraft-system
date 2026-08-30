package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.OperationLogQueryDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OperationLog;
import com.minecraft.mapper.OperationLogMapper;
import com.minecraft.service.OperationLogService;
import com.minecraft.vo.OperationLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现，支持条件分页查询与异步落库。
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public PageResponse<OperationLogVO> getOperationLogList(OperationLogQueryDTO queryDTO) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        // 关键字搜索（用户名、描述、URI）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            String keyword = queryDTO.getKeyword().trim();
            wrapper.and(w -> w.like(OperationLog::getUsername, keyword)
                    .or()
                    .like(OperationLog::getOperationDesc, keyword)
                    .or()
                    .like(OperationLog::getRequestUri, keyword));
        }

        // 用户ID过滤
        if (queryDTO.getUserId() != null) {
            wrapper.eq(OperationLog::getUserId, queryDTO.getUserId());
        }

        // 操作类型过滤
        if (queryDTO.getOperationType() != null && !queryDTO.getOperationType().trim().isEmpty()) {
            wrapper.eq(OperationLog::getOperationType, queryDTO.getOperationType());
        }

        // 状态过滤
        if (queryDTO.getStatus() != null) {
            wrapper.eq(OperationLog::getStatus, queryDTO.getStatus());
        }

        // 时间范围过滤
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(OperationLog::getCreatedAt, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(OperationLog::getCreatedAt, queryDTO.getEndTime());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(OperationLog::getCreatedAt);

        Page<OperationLog> resultPage = page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);

        // 转换为VO
        List<OperationLogVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResponse<>(voList, resultPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public OperationLogVO getOperationLogDetail(Long id) {
        OperationLog log = getById(id);
        return log == null ? null : convertToVO(log);
    }

    @Override
    public List<OperationLogVO> getLogsByUserId(Integer userId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getUserId, userId)
                .orderByDesc(OperationLog::getCreatedAt);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationLog> getRecentLogsByUserId(Integer userId, Integer limit) {
        return baseMapper.selectRecentLogsByUserId(userId, limit);
    }

    @Override
    public Long countByTimeRange(String startTime, String endTime) {
        return baseMapper.countByTimeRange(startTime, endTime);
    }

    @Override
    @Transactional
    public void saveOperationLog(OperationLog operationLog) {
        if (operationLog.getCreatedAt() == null) {
            operationLog.setCreatedAt(LocalDateTime.now());
        }
        save(operationLog);
    }

    @Override
    @Async("taskExecutor")
    public void saveOperationLogAsync(OperationLog operationLog) {
        try {
            saveOperationLog(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteOperationLog(Long id) {
        // 直接调用 mapper.deleteById，与 OfficialwebsiteServiceImpl 保持一致，
        // 同时避免 removeById 在无 Spring 上下文的单元测试中依赖 TableInfo 的问题
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public boolean deleteLogsBeforeTime(LocalDateTime time) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(OperationLog::getCreatedAt, time);
        return remove(wrapper);
    }

    /**
     * Entity转VO
     */
    private OperationLogVO convertToVO(OperationLog entity) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(entity, vo);
        // 实体字段 ipAddress 对应文档 VO 中的 userIp
        vo.setUserIp(entity.getIpAddress());
        return vo;
    }
}
