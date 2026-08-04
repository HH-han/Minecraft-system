package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OperationLog;
import com.minecraft.mapper.OperationLogMapper;
import com.minecraft.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志服务实现，参照 FoodServiceImpl 的分页查询风格。
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public PageResponse<OperationLog> getOperationLogList(PageRequest request) {
        Page<OperationLog> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().trim();
            wrapper.like(OperationLog::getOperationType, keyword)
                    .or()
                    .like(OperationLog::getOperationDesc, keyword);
        }

        wrapper.orderByDesc(OperationLog::getCreatedAt);

        Page<OperationLog> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public OperationLog getOperationLogDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<OperationLog> getLogsByUserId(Integer userId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getUserId, userId)
                .orderByDesc(OperationLog::getCreatedAt);
        return list(wrapper);
    }

    @Override
    @Transactional
    public void saveOperationLog(OperationLog operationLog) {
        save(operationLog);
    }

    @Override
    @Transactional
    public void deleteOperationLog(Long id) {
        // 直接调用 mapper.deleteById，与 OfficialwebsiteServiceImpl 保持一致，
        // 同时避免 removeById 在无 Spring 上下文的单元测试中依赖 TableInfo 的问题
        baseMapper.deleteById(id);
    }
}
