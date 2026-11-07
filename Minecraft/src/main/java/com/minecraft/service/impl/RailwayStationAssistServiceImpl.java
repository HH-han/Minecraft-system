package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.RailwayStationServiceApplyRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.RailwayServiceRequest;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayStationServiceItem;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayServiceRequestMapper;
import com.minecraft.mapper.RailwayStationMapper;
import com.minecraft.mapper.RailwayStationServiceItemMapper;
import com.minecraft.service.RailwayStationAssistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RailwayStationAssistServiceImpl implements RailwayStationAssistService {

    private static final DateTimeFormatter NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final int MAX_ACTIVE_REQUESTS = 3;

    private final RailwayStationServiceItemMapper serviceItemMapper;
    private final RailwayServiceRequestMapper requestMapper;
    private final RailwayStationMapper stationMapper;

    @Override
    public List<RailwayStationServiceItem> listServices() {
        return serviceItemMapper.selectList(new LambdaQueryWrapper<RailwayStationServiceItem>()
                .eq(RailwayStationServiceItem::getStatus, 1)
                .orderByAsc(RailwayStationServiceItem::getSort)
                .orderByAsc(RailwayStationServiceItem::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RailwayServiceRequest apply(Long userId, RailwayStationServiceApplyRequest request) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录后再预约站车服务");
        }
        RailwayStationServiceItem service = serviceItemMapper.selectById(request.getServiceId());
        if (service == null || !Integer.valueOf(1).equals(service.getStatus())) {
            throw new BusinessException("服务项目不存在或已停用");
        }
        String stationName = request.getStationName().trim();
        Long stationCount = stationMapper.selectCount(new LambdaQueryWrapper<RailwayStation>()
                .and(w -> w.eq(RailwayStation::getStationName, stationName)
                        .or().eq(RailwayStation::getCityName, stationName)));
        if (stationCount == null || stationCount == 0) {
            throw new BusinessException("车站不存在：" + stationName);
        }
        LocalDate today = LocalDate.now();
        if (request.getServiceDate().isBefore(today)) {
            throw new BusinessException("服务日期不能早于今天");
        }
        if (request.getServiceDate().isAfter(today.plusDays(30))) {
            throw new BusinessException("仅支持预约 30 天内的站车服务");
        }

        Long duplicate = requestMapper.selectCount(new LambdaQueryWrapper<RailwayServiceRequest>()
                .eq(RailwayServiceRequest::getUserId, userId)
                .eq(RailwayServiceRequest::getServiceId, service.getId())
                .eq(RailwayServiceRequest::getStationName, stationName)
                .eq(RailwayServiceRequest::getServiceDate, request.getServiceDate())
                .in(RailwayServiceRequest::getStatus, "PENDING", "ACCEPTED"));
        if (duplicate != null && duplicate > 0) {
            throw new BusinessException("该车站当日已提交相同服务预约，请勿重复提交");
        }
        Long active = requestMapper.selectCount(new LambdaQueryWrapper<RailwayServiceRequest>()
                .eq(RailwayServiceRequest::getUserId, userId)
                .in(RailwayServiceRequest::getStatus, "PENDING", "ACCEPTED"));
        if (active != null && active >= MAX_ACTIVE_REQUESTS) {
            throw new BusinessException("最多同时存在 " + MAX_ACTIVE_REQUESTS + " 个进行中的预约，请先取消或等待处理完成");
        }

        RailwayServiceRequest entity = new RailwayServiceRequest();
        entity.setRequestNo("S" + LocalDateTime.now().format(NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        entity.setUserId(userId);
        entity.setServiceId(service.getId());
        entity.setServiceName(service.getName());
        entity.setStationName(stationName);
        entity.setServiceDate(request.getServiceDate());
        entity.setPassengerCount(request.getPassengerCount());
        entity.setContactName(request.getContactName().trim());
        entity.setContactPhone(request.getContactPhone().trim());
        entity.setRemark(StringUtils.hasText(request.getRemark()) ? request.getRemark().trim() : null);
        entity.setStatus("PENDING");
        requestMapper.insert(entity);
        return entity;
    }

    @Override
    public PageResponse<RailwayServiceRequest> listRequests(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        int num = safePageNum(pageNum);
        int size = safePageSize(pageSize);
        Page<RailwayServiceRequest> page = requestMapper.selectPage(new Page<>(num, size),
                new LambdaQueryWrapper<RailwayServiceRequest>()
                        .eq(RailwayServiceRequest::getUserId, userId)
                        .orderByDesc(RailwayServiceRequest::getCreateTime));
        return new PageResponse<>(page.getRecords(), page.getTotal(), num, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long id) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        RailwayServiceRequest request = requestMapper.selectById(id);
        if (request == null || !userId.equals(request.getUserId())) {
            throw new BusinessException("预约记录不存在");
        }
        if (!"PENDING".equals(request.getStatus()) && !"ACCEPTED".equals(request.getStatus())) {
            throw new BusinessException("当前预约状态不可取消");
        }
        request.setStatus("CANCELLED");
        requestMapper.updateById(request);
    }

    private int safePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int safePageSize(Integer pageSize) {
        int size = pageSize == null ? 10 : pageSize;
        return Math.max(1, Math.min(size, 50));
    }
}
