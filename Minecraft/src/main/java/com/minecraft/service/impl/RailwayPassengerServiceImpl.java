package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.dto.request.RailwayPassengerRequest;
import com.minecraft.entity.RailwayPassenger;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayPassengerMapper;
import com.minecraft.service.RailwayPassengerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RailwayPassengerServiceImpl implements RailwayPassengerService {

    private final RailwayPassengerMapper passengerMapper;

    public RailwayPassengerServiceImpl(RailwayPassengerMapper passengerMapper) {
        this.passengerMapper = passengerMapper;
    }

    @Override
    public List<RailwayPassenger> listByUser(Long userId) {
        return passengerMapper.selectList(new LambdaQueryWrapper<RailwayPassenger>()
                .eq(RailwayPassenger::getUserId, userId)
                .orderByDesc(RailwayPassenger::getId));
    }

    @Override
    public RailwayPassenger addPassenger(Long userId, RailwayPassengerRequest request) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        String idCardNo = request.getIdCardNo() == null ? "" : request.getIdCardNo().trim().toUpperCase();
        if (!idCardNo.matches("^\\d{15}$|^\\d{17}[\\dX]$")) {
            throw new BusinessException("证件号码格式不正确");
        }
        Long exists = passengerMapper.selectCount(new LambdaQueryWrapper<RailwayPassenger>()
                .eq(RailwayPassenger::getUserId, userId)
                .eq(RailwayPassenger::getIdCardNo, idCardNo));
        if (exists != null && exists > 0) {
            throw new BusinessException("该乘车人已存在");
        }
        RailwayPassenger passenger = new RailwayPassenger();
        passenger.setUserId(userId);
        passenger.setName(request.getName().trim());
        passenger.setIdCardNo(idCardNo);
        passenger.setPassengerType(StringUtils.hasText(request.getPassengerType())
                ? request.getPassengerType() : "ADULT");
        passenger.setPhone(request.getPhone());
        passenger.setVerifyStatus(1);
        passengerMapper.insert(passenger);
        return passenger;
    }

    @Override
    public void deletePassenger(Long userId, Long id) {
        RailwayPassenger passenger = passengerMapper.selectById(id);
        if (passenger == null || !passenger.getUserId().equals(userId)) {
            throw new BusinessException("乘车人不存在");
        }
        passengerMapper.deleteById(id);
    }
}
