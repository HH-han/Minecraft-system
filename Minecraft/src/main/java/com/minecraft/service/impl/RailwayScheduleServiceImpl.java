package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.dto.response.RailwaySeatVO;
import com.minecraft.dto.response.RailwayServiceDetailVO;
import com.minecraft.dto.response.RailwayStatusVO;
import com.minecraft.dto.response.RailwayStopVO;
import com.minecraft.dto.response.RailwayTimetableVO;
import com.minecraft.dto.response.RailwayTrainVO;
import com.minecraft.entity.RailwaySegmentInventory;
import com.minecraft.entity.RailwayService;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayStopStation;
import com.minecraft.entity.RailwayTrain;
import com.minecraft.entity.RailwayTrainStop;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwaySegmentInventoryMapper;
import com.minecraft.mapper.RailwayServiceMapper;
import com.minecraft.mapper.RailwayStationMapper;
import com.minecraft.mapper.RailwayStopStationMapper;
import com.minecraft.mapper.RailwayTrainMapper;
import com.minecraft.mapper.RailwayTrainStopMapper;
import com.minecraft.service.RailwayScheduleService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RailwayScheduleServiceImpl implements RailwayScheduleService {

    private static final List<String> SEAT_ORDER = List.of("SECOND_CLASS", "FIRST_CLASS", "SLEEPER");

    private final RailwayStationMapper stationMapper;
    private final RailwayTrainMapper trainMapper;
    private final RailwayTrainStopMapper trainStopMapper;
    private final RailwayServiceMapper serviceMapper;
    private final RailwayStopStationMapper stopStationMapper;
    private final RailwaySegmentInventoryMapper inventoryMapper;

    public RailwayScheduleServiceImpl(RailwayStationMapper stationMapper,
                                      RailwayTrainMapper trainMapper,
                                      RailwayTrainStopMapper trainStopMapper,
                                      RailwayServiceMapper serviceMapper,
                                      RailwayStopStationMapper stopStationMapper,
                                      RailwaySegmentInventoryMapper inventoryMapper) {
        this.stationMapper = stationMapper;
        this.trainMapper = trainMapper;
        this.trainStopMapper = trainStopMapper;
        this.serviceMapper = serviceMapper;
        this.stopStationMapper = stopStationMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateServices(LocalDate startDate, int days) {
        LocalDate start = startDate == null ? LocalDate.now() : startDate;
        int safeDays = Math.max(1, Math.min(days, 30));
        List<RailwayTrain> trains = trainMapper.selectList(new LambdaQueryWrapper<RailwayTrain>()
                .eq(RailwayTrain::getStatus, 1));
        int created = 0;
        for (int i = 0; i < safeDays; i++) {
            LocalDate date = start.plusDays(i);
            for (RailwayTrain train : trains) {
                if (ensureService(train, date)) {
                    created++;
                }
            }
        }
        return created;
    }

    /**
     * 确保某车次某日存在运行实例（不存在则按经停模板生成）
     *
     * @return 是否新生成
     */
    private boolean ensureService(RailwayTrain train, LocalDate date) {
        RailwayService existing = findService(train.getId(), date);
        if (existing != null) {
            return false;
        }
        List<RailwayTrainStop> template = trainStopMapper.selectList(new LambdaQueryWrapper<RailwayTrainStop>()
                .eq(RailwayTrainStop::getTrainId, train.getId())
                .orderByAsc(RailwayTrainStop::getStopSeq));
        if (template.size() < 2) {
            return false;
        }
        RailwayService service = new RailwayService();
        service.setTrainId(train.getId());
        service.setServiceDate(date);
        service.setStatus(1);
        try {
            serviceMapper.insert(service);
        } catch (DuplicateKeyException e) {
            // 并发下已被其他请求生成
            return false;
        }
        for (RailwayTrainStop stop : template) {
            RailwayStopStation copy = new RailwayStopStation();
            copy.setServiceId(service.getId());
            copy.setStationId(stop.getStationId());
            copy.setStopSeq(stop.getStopSeq());
            copy.setArriveTime(stop.getArriveTime());
            copy.setDepartTime(stop.getDepartTime());
            copy.setStopOver(stop.getStopOver());
            copy.setDayOffset(stop.getDayOffset());
            stopStationMapper.insert(copy);
        }
        for (int i = 0; i < template.size() - 1; i++) {
            RailwayTrainStop current = template.get(i);
            RailwayTrainStop next = template.get(i + 1);
            addInventory(service.getId(), current.getStopSeq(), next.getStopSeq(),
                    "SECOND_CLASS", train.getSecondClassSeats(), next.getSecondClassPriceCents());
            addInventory(service.getId(), current.getStopSeq(), next.getStopSeq(),
                    "FIRST_CLASS", train.getFirstClassSeats(), next.getFirstClassPriceCents());
            addInventory(service.getId(), current.getStopSeq(), next.getStopSeq(),
                    "SLEEPER", train.getSleeperSeats(), next.getSleeperPriceCents());
        }
        return true;
    }

    private void addInventory(Long serviceId, Integer fromSeq, Integer toSeq,
                              String seatType, Integer capacity, Integer priceCents) {
        int seats = capacity == null ? 0 : capacity;
        int price = priceCents == null ? 0 : priceCents;
        if (seats <= 0 || price <= 0) {
            return;
        }
        RailwaySegmentInventory inventory = new RailwaySegmentInventory();
        inventory.setServiceId(serviceId);
        inventory.setFromStopSeq(fromSeq);
        inventory.setToStopSeq(toSeq);
        inventory.setSeatType(seatType);
        inventory.setTotalSeats(seats);
        inventory.setLeftSeats(seats);
        inventory.setPriceCents(price);
        inventoryMapper.insert(inventory);
    }

    private RailwayService findService(Long trainId, LocalDate date) {
        return serviceMapper.selectOne(new LambdaQueryWrapper<RailwayService>()
                .eq(RailwayService::getTrainId, trainId)
                .eq(RailwayService::getServiceDate, date)
                .last("LIMIT 1"));
    }

    @Override
    public List<RailwayTrainVO> search(String fromStation, String toStation, LocalDate date) {
        if (!StringUtils.hasText(fromStation) || !StringUtils.hasText(toStation)) {
            throw new BusinessException("请填写出发地和到达地");
        }
        LocalDate travelDate = date == null ? LocalDate.now() : date;
        LocalDate today = LocalDate.now();
        if (travelDate.isBefore(today)) {
            throw new BusinessException("不能查询历史日期的车票");
        }
        if (travelDate.isAfter(today.plusDays(15))) {
            throw new BusinessException("仅支持查询未来 15 天内的车票");
        }
        Set<Long> fromIds = resolveStationIds(fromStation);
        Set<Long> toIds = resolveStationIds(toStation);
        if (fromIds.isEmpty()) {
            throw new BusinessException("出发车站不存在：" + fromStation);
        }
        if (toIds.isEmpty()) {
            throw new BusinessException("到达车站不存在：" + toStation);
        }
        if (fromIds.equals(toIds)) {
            return new ArrayList<>();
        }

        // 懒生成当日运行实例，保证首次查询即可出票
        generateServices(travelDate, 1);

        List<RailwayService> services = serviceMapper.selectList(new LambdaQueryWrapper<RailwayService>()
                .eq(RailwayService::getServiceDate, travelDate)
                .eq(RailwayService::getStatus, 1));
        if (services.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, RailwayService> serviceMap = services.stream()
                .collect(Collectors.toMap(RailwayService::getId, s -> s, (a, b) -> a));
        List<Long> serviceIds = new ArrayList<>(serviceMap.keySet());
        List<Long> trainIds = services.stream().map(RailwayService::getTrainId).distinct().toList();

        Map<Long, RailwayTrain> trainMap = trainMapper.selectBatchIds(trainIds).stream()
                .collect(Collectors.toMap(RailwayTrain::getId, t -> t, (a, b) -> a));
        Map<Long, List<RailwayStopStation>> stopMap = stopStationMapper.selectList(
                        new LambdaQueryWrapper<RailwayStopStation>()
                                .in(RailwayStopStation::getServiceId, serviceIds)
                                .orderByAsc(RailwayStopStation::getStopSeq))
                .stream().collect(Collectors.groupingBy(RailwayStopStation::getServiceId));
        Map<Long, List<RailwaySegmentInventory>> inventoryMap = inventoryMapper.selectList(
                        new LambdaQueryWrapper<RailwaySegmentInventory>()
                                .in(RailwaySegmentInventory::getServiceId, serviceIds))
                .stream().collect(Collectors.groupingBy(RailwaySegmentInventory::getServiceId));

        Set<Long> stationIds = new HashSet<>(fromIds);
        stationIds.addAll(toIds);
        stopMap.values().forEach(list -> list.forEach(s -> stationIds.add(s.getStationId())));
        Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);

        List<RailwayTrainVO> result = new ArrayList<>();
        for (RailwayService service : services) {
            RailwayTrain train = trainMap.get(service.getTrainId());
            List<RailwayStopStation> stops = stopMap.get(service.getId());
            if (train == null || stops == null || stops.size() < 2) {
                continue;
            }
            RailwayStopStation fromStop = stops.stream()
                    .filter(s -> fromIds.contains(s.getStationId()))
                    .findFirst().orElse(null);
            if (fromStop == null) {
                continue;
            }
            RailwayStopStation toStop = stops.stream()
                    .filter(s -> toIds.contains(s.getStationId()) && s.getStopSeq() > fromStop.getStopSeq())
                    .findFirst().orElse(null);
            if (toStop == null) {
                continue;
            }
            int fromMinutes = toMinutes(fromStop.getDepartTime(), fromStop.getArriveTime())
                    + safeInt(fromStop.getDayOffset()) * 1440;
            int toMinutes = toMinutes(toStop.getArriveTime(), toStop.getDepartTime())
                    + safeInt(toStop.getDayOffset()) * 1440;
            int duration = toMinutes - fromMinutes;
            if (duration <= 0) {
                continue;
            }
            List<RailwaySeatVO> seats = aggregateSeats(inventoryMap.get(service.getId()),
                    fromStop.getStopSeq(), toStop.getStopSeq());
            if (seats.isEmpty()) {
                continue;
            }
            RailwayTrainVO vo = new RailwayTrainVO();
            vo.setServiceId(service.getId());
            vo.setTrainId(train.getId());
            vo.setTrainNo(train.getTrainNo());
            vo.setTrainType(train.getTrainType());
            vo.setFromStationName(stationName(stationMap, fromStop.getStationId()));
            vo.setToStationName(stationName(stationMap, toStop.getStationId()));
            vo.setFromStopSeq(fromStop.getStopSeq());
            vo.setToStopSeq(toStop.getStopSeq());
            vo.setFromTime(fromStop.getDepartTime() != null ? fromStop.getDepartTime() : fromStop.getArriveTime());
            vo.setToTime(toStop.getArriveTime() != null ? toStop.getArriveTime() : toStop.getDepartTime());
            vo.setDurationMinutes(duration);
            vo.setArriveDayOffset(safeInt(toStop.getDayOffset()) - safeInt(fromStop.getDayOffset()));
            vo.setSeats(seats);
            result.add(vo);
        }
        result.sort(Comparator.comparing(RailwayTrainVO::getFromTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return result;
    }

    @Override
    public RailwayServiceDetailVO getServiceDetail(Long serviceId) {
        RailwayService service = serviceMapper.selectById(serviceId);
        if (service == null) {
            throw new BusinessException("车次运行信息不存在");
        }
        RailwayTrain train = trainMapper.selectById(service.getTrainId());
        if (train == null) {
            throw new BusinessException("车次信息不存在");
        }
        List<RailwayStopStation> stops = stopStationMapper.selectList(
                new LambdaQueryWrapper<RailwayStopStation>()
                        .eq(RailwayStopStation::getServiceId, serviceId)
                        .orderByAsc(RailwayStopStation::getStopSeq));
        Set<Long> stationIds = stops.stream().map(RailwayStopStation::getStationId).collect(Collectors.toSet());
        Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);
        List<RailwayStopVO> stopVos = stops.stream()
                .map(s -> toStopVO(s, stationMap))
                .collect(Collectors.toList());
        List<RailwaySegmentInventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<RailwaySegmentInventory>()
                        .eq(RailwaySegmentInventory::getServiceId, serviceId));

        RailwayServiceDetailVO detail = new RailwayServiceDetailVO();
        detail.setServiceId(serviceId);
        detail.setTrainId(train.getId());
        detail.setTrainNo(train.getTrainNo());
        detail.setTrainType(train.getTrainType());
        detail.setServiceDate(service.getServiceDate());
        detail.setStops(stopVos);
        if (!stops.isEmpty()) {
            detail.setSeats(aggregateSeats(inventories,
                    stops.get(0).getStopSeq(), stops.get(stops.size() - 1).getStopSeq()));
        } else {
            detail.setSeats(new ArrayList<>());
        }
        return detail;
    }

    @Override
    public RailwayTimetableVO getTimetable(String trainNo, LocalDate date) {
        if (!StringUtils.hasText(trainNo)) {
            throw new BusinessException("请输入车次号");
        }
        RailwayTrain train = trainMapper.selectOne(new LambdaQueryWrapper<RailwayTrain>()
                .eq(RailwayTrain::getTrainNo, trainNo.trim().toUpperCase())
                .last("LIMIT 1"));
        if (train == null) {
            throw new BusinessException("车次不存在：" + trainNo);
        }
        List<RailwayStopVO> stopVos;
        Set<Long> stationIds;
        if (date != null) {
            RailwayService service = findService(train.getId(), date);
            if (service != null) {
                List<RailwayStopStation> stops = stopStationMapper.selectList(
                        new LambdaQueryWrapper<RailwayStopStation>()
                                .eq(RailwayStopStation::getServiceId, service.getId())
                                .orderByAsc(RailwayStopStation::getStopSeq));
                stationIds = stops.stream().map(RailwayStopStation::getStationId).collect(Collectors.toSet());
                Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);
                stopVos = stops.stream().map(s -> toStopVO(s, stationMap)).collect(Collectors.toList());
            } else {
                stopVos = templateStops(train.getId());
            }
        } else {
            stopVos = templateStops(train.getId());
        }
        RailwayTimetableVO vo = new RailwayTimetableVO();
        vo.setTrainNo(train.getTrainNo());
        vo.setTrainType(train.getTrainType());
        vo.setStops(stopVos);
        return vo;
    }

    private List<RailwayStopVO> templateStops(Long trainId) {
        List<RailwayTrainStop> stops = trainStopMapper.selectList(new LambdaQueryWrapper<RailwayTrainStop>()
                .eq(RailwayTrainStop::getTrainId, trainId)
                .orderByAsc(RailwayTrainStop::getStopSeq));
        Set<Long> stationIds = stops.stream().map(RailwayTrainStop::getStationId).collect(Collectors.toSet());
        Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);
        List<RailwayStopVO> result = new ArrayList<>();
        for (RailwayTrainStop stop : stops) {
            RailwayStopVO vo = new RailwayStopVO();
            vo.setStopSeq(stop.getStopSeq());
            vo.setStationId(stop.getStationId());
            vo.setStationName(stationName(stationMap, stop.getStationId()));
            vo.setArriveTime(stop.getArriveTime());
            vo.setDepartTime(stop.getDepartTime());
            vo.setStopOver(stop.getStopOver());
            vo.setDayOffset(stop.getDayOffset());
            result.add(vo);
        }
        return result;
    }

    @Override
    public RailwayStatusVO getStatus(String trainNo) {
        if (!StringUtils.hasText(trainNo)) {
            throw new BusinessException("请输入车次号");
        }
        String no = trainNo.trim().toUpperCase();
        RailwayTrain train = trainMapper.selectOne(new LambdaQueryWrapper<RailwayTrain>()
                .eq(RailwayTrain::getTrainNo, no).last("LIMIT 1"));
        if (train == null) {
            throw new BusinessException("车次不存在：" + trainNo);
        }
        int hash = Math.abs(no.hashCode());
        int delay = hash % 5 == 0 ? 5 + hash % 20 : 0;
        RailwayStatusVO vo = new RailwayStatusVO();
        vo.setTrainNo(no);
        vo.setDelayMinutes(delay);
        vo.setStatus(delay == 0 ? "ON_TIME" : "DELAYED");
        vo.setMessage(delay == 0 ? "列车正点运行" : "列车预计晚点 " + delay + " 分钟");
        return vo;
    }

    private List<RailwaySeatVO> aggregateSeats(List<RailwaySegmentInventory> inventories,
                                               int fromSeq, int toSeq) {
        if (inventories == null || inventories.isEmpty() || toSeq <= fromSeq) {
            return new ArrayList<>();
        }
        int expected = toSeq - fromSeq;
        Map<String, List<RailwaySegmentInventory>> grouped = inventories.stream()
                .filter(i -> i.getFromStopSeq() >= fromSeq && i.getToStopSeq() <= toSeq)
                .collect(Collectors.groupingBy(RailwaySegmentInventory::getSeatType));
        List<RailwaySeatVO> result = new ArrayList<>();
        for (String seatType : SEAT_ORDER) {
            List<RailwaySegmentInventory> segments = grouped.get(seatType);
            if (segments == null || segments.size() < expected) {
                continue;
            }
            int left = segments.stream().mapToInt(i -> safeInt(i.getLeftSeats())).min().orElse(0);
            int total = segments.stream().mapToInt(i -> safeInt(i.getTotalSeats())).min().orElse(0);
            int price = segments.stream().mapToInt(i -> safeInt(i.getPriceCents())).sum();
            RailwaySeatVO seat = new RailwaySeatVO();
            seat.setSeatType(seatType);
            seat.setLeftSeats(left);
            seat.setTotalSeats(total);
            seat.setPriceCents(price);
            result.add(seat);
        }
        return result;
    }

    private Set<Long> resolveStationIds(String keyword) {
        String kw = keyword.trim();
        List<RailwayStation> byName = stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                .eq(RailwayStation::getStationName, kw));
        if (!byName.isEmpty()) {
            return byName.stream().map(RailwayStation::getId).collect(Collectors.toSet());
        }
        List<RailwayStation> byCity = stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                .eq(RailwayStation::getCityName, kw));
        if (!byCity.isEmpty()) {
            return byCity.stream().map(RailwayStation::getId).collect(Collectors.toSet());
        }
        List<RailwayStation> fuzzy = stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                .and(w -> w.like(RailwayStation::getStationName, kw)
                        .or().like(RailwayStation::getCityName, kw)
                        .or().like(RailwayStation::getPinyin, kw))
                .last("LIMIT 20"));
        return fuzzy.stream().map(RailwayStation::getId).collect(Collectors.toSet());
    }

    private Map<Long, RailwayStation> loadStationMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        return stationMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(RailwayStation::getId, s -> s, (a, b) -> a, LinkedHashMap::new));
    }

    private RailwayStopVO toStopVO(RailwayStopStation stop, Map<Long, RailwayStation> stationMap) {
        RailwayStopVO vo = new RailwayStopVO();
        vo.setStopSeq(stop.getStopSeq());
        vo.setStationId(stop.getStationId());
        vo.setStationName(stationName(stationMap, stop.getStationId()));
        vo.setArriveTime(stop.getArriveTime());
        vo.setDepartTime(stop.getDepartTime());
        vo.setStopOver(stop.getStopOver());
        vo.setDayOffset(stop.getDayOffset());
        return vo;
    }

    private String stationName(Map<Long, RailwayStation> map, Long id) {
        RailwayStation station = map.get(id);
        return station == null ? "" : station.getStationName();
    }

    private int toMinutes(LocalTime primary, LocalTime fallback) {
        LocalTime time = primary != null ? primary : fallback;
        return time == null ? 0 : time.getHour() * 60 + time.getMinute();
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
