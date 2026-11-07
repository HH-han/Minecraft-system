package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.dto.response.RailwayBoardVO;
import com.minecraft.dto.response.RailwayTrainVO;
import com.minecraft.entity.RailwayService;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayStopStation;
import com.minecraft.entity.RailwayTrain;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayServiceMapper;
import com.minecraft.mapper.RailwayStationMapper;
import com.minecraft.mapper.RailwayStopStationMapper;
import com.minecraft.mapper.RailwayTrainMapper;
import com.minecraft.service.RailwayInfoService;
import com.minecraft.service.RailwayScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RailwayInfoServiceImpl implements RailwayInfoService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final RailwayScheduleService scheduleService;
    private final RailwayServiceMapper serviceMapper;
    private final RailwayStopStationMapper stopStationMapper;
    private final RailwayTrainMapper trainMapper;
    private final RailwayStationMapper stationMapper;

    @Override
    public List<RailwayBoardVO> board(String stationName, LocalDate date, String type) {
        if (!StringUtils.hasText(stationName)) {
            throw new BusinessException("请选择车站");
        }
        LocalDate travelDate = date == null ? LocalDate.now() : date;
        LocalDate today = LocalDate.now();
        if (travelDate.isBefore(today)) {
            throw new BusinessException("车站大屏仅支持查询今天及未来 15 天");
        }
        if (travelDate.isAfter(today.plusDays(15))) {
            throw new BusinessException("车站大屏仅支持查询今天及未来 15 天");
        }
        Set<Long> stationIds = resolveStationIds(stationName);
        if (stationIds.isEmpty()) {
            throw new BusinessException("车站不存在：" + stationName);
        }
        boolean departure = !"ARRIVAL".equalsIgnoreCase(type);

        scheduleService.generateServices(travelDate, 1);
        List<RailwayService> services = serviceMapper.selectList(new LambdaQueryWrapper<RailwayService>()
                .eq(RailwayService::getServiceDate, travelDate)
                .eq(RailwayService::getStatus, 1));
        if (services.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> serviceIds = services.stream().map(RailwayService::getId).toList();
        List<Long> trainIds = services.stream().map(RailwayService::getTrainId).distinct().toList();
        Map<Long, RailwayTrain> trainMap = trainMapper.selectBatchIds(trainIds).stream()
                .collect(Collectors.toMap(RailwayTrain::getId, t -> t, (a, b) -> a));
        Map<Long, List<RailwayStopStation>> stopMap = stopStationMapper.selectList(
                        new LambdaQueryWrapper<RailwayStopStation>()
                                .in(RailwayStopStation::getServiceId, serviceIds)
                                .orderByAsc(RailwayStopStation::getStopSeq))
                .stream().collect(Collectors.groupingBy(RailwayStopStation::getServiceId));
        Map<Long, RailwayStation> stationMap = loadStationMap(stopMap.values().stream()
                .flatMap(List::stream)
                .map(RailwayStopStation::getStationId)
                .collect(Collectors.toSet()));

        LocalDateTime now = LocalDateTime.now();
        List<RailwayBoardVO> result = new ArrayList<>();
        for (RailwayService service : services) {
            RailwayTrain train = trainMap.get(service.getTrainId());
            List<RailwayStopStation> stops = stopMap.get(service.getId());
            if (train == null || stops == null || stops.size() < 2) {
                continue;
            }
            RailwayStopStation target = stops.stream()
                    .filter(s -> stationIds.contains(s.getStationId()))
                    .findFirst().orElse(null);
            if (target == null) {
                continue;
            }
            boolean isFirst = target.getStopSeq().equals(stops.get(0).getStopSeq());
            boolean isLast = target.getStopSeq().equals(stops.get(stops.size() - 1).getStopSeq());
            if (departure && isLast) {
                continue;
            }
            if (!departure && isFirst) {
                continue;
            }
            LocalTime plan = departure
                    ? (target.getDepartTime() != null ? target.getDepartTime() : target.getArriveTime())
                    : (target.getArriveTime() != null ? target.getArriveTime() : target.getDepartTime());
            if (plan == null) {
                continue;
            }
            int delay = delayMinutes(train.getTrainNo());
            LocalDateTime planDateTime = travelDate
                    .plusDays(target.getDayOffset() == null ? 0 : target.getDayOffset())
                    .atTime(plan);

            RailwayBoardVO vo = new RailwayBoardVO();
            vo.setServiceId(service.getId());
            vo.setTrainNo(train.getTrainNo());
            vo.setTrainType(train.getTrainType());
            vo.setStationName(stationName(stationMap, target.getStationId()));
            vo.setOriginStation(stationName(stationMap, stops.get(0).getStationId()));
            vo.setTerminalStation(stationName(stationMap, stops.get(stops.size() - 1).getStationId()));
            vo.setArriveTime(formatTime(target.getArriveTime()));
            vo.setDepartTime(formatTime(target.getDepartTime()));
            vo.setPlanTime(plan.format(TIME_FORMAT));
            vo.setExpectedTime(plan.plusMinutes(delay).format(TIME_FORMAT));
            vo.setDelayMinutes(delay);
            vo.setStatus(resolveStatus(planDateTime, now, departure, delay));
            vo.setPlatform((Math.abs(train.getTrainNo().hashCode()) % 12 + 1) + " 站台");
            result.add(vo);
        }
        result.sort(Comparator.comparing(RailwayBoardVO::getPlanTime));
        return result;
    }

    @Override
    public List<RailwayTrainVO> fares(String fromStation, String toStation, LocalDate date) {
        return scheduleService.search(fromStation, toStation, date);
    }

    private String resolveStatus(LocalDateTime planDateTime, LocalDateTime now,
                                 boolean departure, int delay) {
        if (planDateTime.isBefore(now.minusMinutes(5))) {
            return departure ? "DEPARTED" : "ARRIVED";
        }
        if (delay > 0) {
            return "DELAYED";
        }
        if (planDateTime.isBefore(now.plusMinutes(20))) {
            return departure ? "CHECKING" : "ARRIVING";
        }
        return "WAITING";
    }

    private int delayMinutes(String trainNo) {
        int hash = Math.abs(trainNo.hashCode());
        return hash % 5 == 0 ? 5 + hash % 20 : 0;
    }

    private String formatTime(LocalTime time) {
        return time == null ? "--" : time.format(TIME_FORMAT);
    }

    private String stationName(Map<Long, RailwayStation> map, Long id) {
        RailwayStation station = map.get(id);
        return station == null ? "" : station.getStationName();
    }

    private Map<Long, RailwayStation> loadStationMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        return stationMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(RailwayStation::getId, s -> s, (a, b) -> a));
    }

    private Set<Long> resolveStationIds(String keyword) {
        String kw = keyword.trim();
        List<RailwayStation> exact = stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                .eq(RailwayStation::getStationName, kw));
        if (!exact.isEmpty()) {
            return exact.stream().map(RailwayStation::getId).collect(Collectors.toSet());
        }
        List<RailwayStation> byCity = stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                .eq(RailwayStation::getCityName, kw));
        if (!byCity.isEmpty()) {
            return byCity.stream().map(RailwayStation::getId).collect(Collectors.toSet());
        }
        Set<Long> ids = new HashSet<>();
        stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                        .like(RailwayStation::getStationName, kw).last("LIMIT 20"))
                .forEach(s -> ids.add(s.getId()));
        return ids;
    }
}
