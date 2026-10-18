package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.dto.request.RailwayGenerateRequest;
import com.minecraft.dto.response.RailwayAdminStatsVO;
import com.minecraft.dto.response.RailwayInventoryVO;
import com.minecraft.dto.response.RailwayServiceAdminVO;
import com.minecraft.dto.response.RailwayTrainAdminVO;
import com.minecraft.dto.response.RailwayTrainStopAdminVO;
import com.minecraft.entity.RailwayOrder;
import com.minecraft.entity.RailwaySegmentInventory;
import com.minecraft.entity.RailwayService;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayStopStation;
import com.minecraft.entity.RailwayTrain;
import com.minecraft.entity.RailwayTrainStop;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayOrderMapper;
import com.minecraft.mapper.RailwaySegmentInventoryMapper;
import com.minecraft.mapper.RailwayServiceMapper;
import com.minecraft.mapper.RailwayStationMapper;
import com.minecraft.mapper.RailwayStopStationMapper;
import com.minecraft.mapper.RailwayTrainMapper;
import com.minecraft.mapper.RailwayTrainStopMapper;
import com.minecraft.service.RailwayAdminService;
import com.minecraft.service.RailwayOrderService;
import com.minecraft.service.RailwayScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RailwayAdminServiceImpl implements RailwayAdminService {

    private final RailwayStationMapper stationMapper;
    private final RailwayTrainMapper trainMapper;
    private final RailwayTrainStopMapper trainStopMapper;
    private final RailwayServiceMapper serviceMapper;
    private final RailwayStopStationMapper stopStationMapper;
    private final RailwaySegmentInventoryMapper inventoryMapper;
    private final RailwayOrderMapper orderMapper;
    private final RailwayScheduleService scheduleService;
    private final RailwayOrderService orderService;

    public RailwayAdminServiceImpl(RailwayStationMapper stationMapper,
                                   RailwayTrainMapper trainMapper,
                                   RailwayTrainStopMapper trainStopMapper,
                                   RailwayServiceMapper serviceMapper,
                                   RailwayStopStationMapper stopStationMapper,
                                   RailwaySegmentInventoryMapper inventoryMapper,
                                   RailwayOrderMapper orderMapper,
                                   RailwayScheduleService scheduleService,
                                   RailwayOrderService orderService) {
        this.stationMapper = stationMapper;
        this.trainMapper = trainMapper;
        this.trainStopMapper = trainStopMapper;
        this.serviceMapper = serviceMapper;
        this.stopStationMapper = stopStationMapper;
        this.inventoryMapper = inventoryMapper;
        this.orderMapper = orderMapper;
        this.scheduleService = scheduleService;
        this.orderService = orderService;
    }

    @Override
    public List<RailwayStation> listStations(String keyword) {
        LambdaQueryWrapper<RailwayStation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(RailwayStation::getStationName, kw)
                    .or().like(RailwayStation::getCityName, kw)
                    .or().like(RailwayStation::getStationCode, kw));
        }
        wrapper.orderByAsc(RailwayStation::getId);
        return stationMapper.selectList(wrapper);
    }

    @Override
    public RailwayStation saveStation(RailwayStation station) {
        if (station == null || !StringUtils.hasText(station.getStationName())) {
            throw new BusinessException("车站名称不能为空");
        }
        if (!StringUtils.hasText(station.getCityName())) {
            station.setCityName(station.getStationName());
        }
        if (!StringUtils.hasText(station.getStationCode())) {
            throw new BusinessException("电报码不能为空");
        }
        RailwayStation exists = stationMapper.selectOne(new LambdaQueryWrapper<RailwayStation>()
                .eq(RailwayStation::getStationCode, station.getStationCode().trim()).last("LIMIT 1"));
        if (exists != null && !exists.getId().equals(station.getId())) {
            throw new BusinessException("电报码已存在");
        }
        if (station.getId() == null) {
            stationMapper.insert(station);
        } else {
            stationMapper.updateById(station);
        }
        return station;
    }

    @Override
    public void deleteStation(Long id) {
        Long used = trainStopMapper.selectCount(new LambdaQueryWrapper<RailwayTrainStop>()
                .eq(RailwayTrainStop::getStationId, id));
        if (used != null && used > 0) {
            throw new BusinessException("该车站已被车次经停使用，无法删除");
        }
        stationMapper.deleteById(id);
    }

    @Override
    public List<RailwayTrainAdminVO> listTrains(String keyword) {
        LambdaQueryWrapper<RailwayTrain> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(RailwayTrain::getTrainNo, keyword.trim().toUpperCase());
        }
        wrapper.orderByAsc(RailwayTrain::getTrainNo);
        List<RailwayTrain> trains = trainMapper.selectList(wrapper);
        Set<Long> stationIds = trains.stream()
                .flatMap(t -> java.util.stream.Stream.of(t.getStartStationId(), t.getEndStationId()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);
        return trains.stream().map(t -> {
            RailwayTrainAdminVO vo = new RailwayTrainAdminVO();
            vo.setId(t.getId());
            vo.setTrainNo(t.getTrainNo());
            vo.setTrainType(t.getTrainType());
            vo.setStartStationId(t.getStartStationId());
            vo.setEndStationId(t.getEndStationId());
            vo.setStartStationName(stationName(stationMap, t.getStartStationId()));
            vo.setEndStationName(stationName(stationMap, t.getEndStationId()));
            vo.setSecondClassSeats(t.getSecondClassSeats());
            vo.setFirstClassSeats(t.getFirstClassSeats());
            vo.setSleeperSeats(t.getSleeperSeats());
            vo.setStatus(t.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public RailwayTrain saveTrain(RailwayTrain train) {
        if (train == null || !StringUtils.hasText(train.getTrainNo())) {
            throw new BusinessException("车次号不能为空");
        }
        if (!StringUtils.hasText(train.getTrainType())) {
            throw new BusinessException("车次类型不能为空");
        }
        train.setTrainNo(train.getTrainNo().trim().toUpperCase());
        RailwayTrain exists = trainMapper.selectOne(new LambdaQueryWrapper<RailwayTrain>()
                .eq(RailwayTrain::getTrainNo, train.getTrainNo()).last("LIMIT 1"));
        if (exists != null && !exists.getId().equals(train.getId())) {
            throw new BusinessException("车次号已存在");
        }
        if (train.getSecondClassSeats() == null) train.setSecondClassSeats(0);
        if (train.getFirstClassSeats() == null) train.setFirstClassSeats(0);
        if (train.getSleeperSeats() == null) train.setSleeperSeats(0);
        if (train.getStatus() == null) train.setStatus(1);
        if (train.getId() == null) {
            trainMapper.insert(train);
        } else {
            trainMapper.updateById(train);
        }
        return train;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTrain(Long id) {
        List<RailwayService> services = serviceMapper.selectList(new LambdaQueryWrapper<RailwayService>()
                .eq(RailwayService::getTrainId, id));
        List<Long> serviceIds = services.stream().map(RailwayService::getId).collect(Collectors.toList());
        if (!serviceIds.isEmpty()) {
            Long activeOrders = orderMapper.selectCount(new LambdaQueryWrapper<RailwayOrder>()
                    .in(RailwayOrder::getServiceId, serviceIds)
                    .in(RailwayOrder::getStatus, List.of("PENDING", "PAID")));
            if (activeOrders != null && activeOrders > 0) {
                throw new BusinessException("该车次存在未完成订单，无法删除");
            }
            stopStationMapper.delete(new LambdaQueryWrapper<RailwayStopStation>()
                    .in(RailwayStopStation::getServiceId, serviceIds));
            inventoryMapper.delete(new LambdaQueryWrapper<RailwaySegmentInventory>()
                    .in(RailwaySegmentInventory::getServiceId, serviceIds));
            serviceMapper.deleteBatchIds(serviceIds);
        }
        trainStopMapper.delete(new LambdaQueryWrapper<RailwayTrainStop>()
                .eq(RailwayTrainStop::getTrainId, id));
        trainMapper.deleteById(id);
    }

    @Override
    public List<RailwayTrainStopAdminVO> getTrainStops(Long trainId) {
        List<RailwayTrainStop> stops = trainStopMapper.selectList(new LambdaQueryWrapper<RailwayTrainStop>()
                .eq(RailwayTrainStop::getTrainId, trainId)
                .orderByAsc(RailwayTrainStop::getStopSeq));
        Set<Long> stationIds = stops.stream().map(RailwayTrainStop::getStationId).collect(Collectors.toSet());
        Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);
        return stops.stream().map(stop -> {
            RailwayTrainStopAdminVO vo = new RailwayTrainStopAdminVO();
            vo.setId(stop.getId());
            vo.setStationId(stop.getStationId());
            vo.setStationName(stationName(stationMap, stop.getStationId()));
            vo.setStopSeq(stop.getStopSeq());
            vo.setArriveTime(stop.getArriveTime());
            vo.setDepartTime(stop.getDepartTime());
            vo.setStopOver(stop.getStopOver());
            vo.setDayOffset(stop.getDayOffset());
            vo.setSecondClassPriceCents(stop.getSecondClassPriceCents());
            vo.setFirstClassPriceCents(stop.getFirstClassPriceCents());
            vo.setSleeperPriceCents(stop.getSleeperPriceCents());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTrainStops(Long trainId, List<RailwayTrainStop> stops) {
        RailwayTrain train = trainMapper.selectById(trainId);
        if (train == null) {
            throw new BusinessException("车次不存在");
        }
        if (stops == null || stops.size() < 2) {
            throw new BusinessException("至少需要配置两个经停站");
        }
        // 存在未完成订单时禁止改动经停（避免库存与订单不一致）
        List<Long> serviceIds = serviceMapper.selectList(new LambdaQueryWrapper<RailwayService>()
                        .eq(RailwayService::getTrainId, trainId))
                .stream().map(RailwayService::getId).collect(Collectors.toList());
        if (!serviceIds.isEmpty()) {
            Long activeOrders = orderMapper.selectCount(new LambdaQueryWrapper<RailwayOrder>()
                    .in(RailwayOrder::getServiceId, serviceIds)
                    .in(RailwayOrder::getStatus, List.of("PENDING", "PAID")));
            if (activeOrders != null && activeOrders > 0) {
                throw new BusinessException("该车次存在未完成订单，暂不能修改经停站");
            }
        }
        LocalDate today = LocalDate.now();
        List<RailwayService> futureServices = serviceIds.isEmpty() ? new ArrayList<>()
                : serviceMapper.selectList(new LambdaQueryWrapper<RailwayService>()
                .eq(RailwayService::getTrainId, trainId)
                .ge(RailwayService::getServiceDate, today));
        List<Long> futureIds = futureServices.stream().map(RailwayService::getId).collect(Collectors.toList());
        if (!futureIds.isEmpty()) {
            stopStationMapper.delete(new LambdaQueryWrapper<RailwayStopStation>()
                    .in(RailwayStopStation::getServiceId, futureIds));
            inventoryMapper.delete(new LambdaQueryWrapper<RailwaySegmentInventory>()
                    .in(RailwaySegmentInventory::getServiceId, futureIds));
            serviceMapper.deleteBatchIds(futureIds);
        }

        trainStopMapper.delete(new LambdaQueryWrapper<RailwayTrainStop>()
                .eq(RailwayTrainStop::getTrainId, trainId));
        int seq = 1;
        for (RailwayTrainStop stop : stops) {
            if (stop.getStationId() == null) {
                throw new BusinessException("经停站不能为空");
            }
            stop.setId(null);
            stop.setTrainId(trainId);
            stop.setStopSeq(seq++);
            if (stop.getStopOver() == null) stop.setStopOver(0);
            if (stop.getDayOffset() == null) stop.setDayOffset(0);
            if (stop.getSecondClassPriceCents() == null) stop.setSecondClassPriceCents(0);
            if (stop.getFirstClassPriceCents() == null) stop.setFirstClassPriceCents(0);
            if (stop.getSleeperPriceCents() == null) stop.setSleeperPriceCents(0);
            trainStopMapper.insert(stop);
        }
        // 同步车次起终站
        train.setStartStationId(stops.get(0).getStationId());
        train.setEndStationId(stops.get(stops.size() - 1).getStationId());
        trainMapper.updateById(train);
    }

    @Override
    public List<RailwayServiceAdminVO> listServices(LocalDate startDate, LocalDate endDate, String trainNo) {
        LocalDate start = startDate == null ? LocalDate.now() : startDate;
        LocalDate end = endDate == null ? start.plusDays(15) : endDate;
        List<Long> trainIds = null;
        if (StringUtils.hasText(trainNo)) {
            trainIds = trainMapper.selectList(new LambdaQueryWrapper<RailwayTrain>()
                            .like(RailwayTrain::getTrainNo, trainNo.trim().toUpperCase()))
                    .stream().map(RailwayTrain::getId).collect(Collectors.toList());
            if (trainIds.isEmpty()) {
                return new ArrayList<>();
            }
        }
        LambdaQueryWrapper<RailwayService> wrapper = new LambdaQueryWrapper<RailwayService>()
                .between(RailwayService::getServiceDate, start, end)
                .orderByAsc(RailwayService::getServiceDate)
                .orderByAsc(RailwayService::getTrainId);
        if (trainIds != null) {
            wrapper.in(RailwayService::getTrainId, trainIds);
        }
        List<RailwayService> services = serviceMapper.selectList(wrapper);
        if (services.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> allTrainIds = services.stream().map(RailwayService::getTrainId).collect(Collectors.toSet());
        Map<Long, RailwayTrain> trainMap = trainMapper.selectBatchIds(allTrainIds).stream()
                .collect(Collectors.toMap(RailwayTrain::getId, t -> t, (a, b) -> a));
        Set<Long> stationIds = trainMap.values().stream()
                .flatMap(t -> java.util.stream.Stream.of(t.getStartStationId(), t.getEndStationId()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);
        List<Long> serviceIds = services.stream().map(RailwayService::getId).collect(Collectors.toList());
        Map<Long, List<RailwaySegmentInventory>> inventoryMap = inventoryMapper.selectList(
                        new LambdaQueryWrapper<RailwaySegmentInventory>()
                                .in(RailwaySegmentInventory::getServiceId, serviceIds))
                .stream().collect(Collectors.groupingBy(RailwaySegmentInventory::getServiceId));

        return services.stream().map(service -> {
            RailwayTrain train = trainMap.get(service.getTrainId());
            RailwayServiceAdminVO vo = new RailwayServiceAdminVO();
            vo.setId(service.getId());
            vo.setTrainId(service.getTrainId());
            vo.setServiceDate(service.getServiceDate());
            vo.setStatus(service.getStatus());
            if (train != null) {
                vo.setTrainNo(train.getTrainNo());
                vo.setTrainType(train.getTrainType());
                vo.setStartStationName(stationName(stationMap, train.getStartStationId()));
                vo.setEndStationName(stationName(stationMap, train.getEndStationId()));
                List<RailwaySegmentInventory> inventories = inventoryMap.getOrDefault(service.getId(),
                        new ArrayList<>());
                vo.setSecondClassLeft(minLeft(inventories, "SECOND_CLASS"));
                vo.setFirstClassLeft(minLeft(inventories, "FIRST_CLASS"));
                vo.setSleeperLeft(minLeft(inventories, "SLEEPER"));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public int generateServices(RailwayGenerateRequest request) {
        LocalDate start = request == null || request.getStartDate() == null
                ? LocalDate.now() : request.getStartDate();
        int days = request == null || request.getDays() == null ? 15 : request.getDays();
        return scheduleService.generateServices(start, days);
    }

    @Override
    public List<RailwayInventoryVO> listInventory(Long serviceId) {
        RailwayService service = serviceMapper.selectById(serviceId);
        if (service == null) {
            throw new BusinessException("运行车次不存在");
        }
        List<RailwayStopStation> stops = stopStationMapper.selectList(
                new LambdaQueryWrapper<RailwayStopStation>()
                        .eq(RailwayStopStation::getServiceId, serviceId)
                        .orderByAsc(RailwayStopStation::getStopSeq));
        Map<Integer, String> seqNameMap = new HashMap<>();
        Set<Long> stationIds = stops.stream().map(RailwayStopStation::getStationId).collect(Collectors.toSet());
        Map<Long, RailwayStation> stationMap = loadStationMap(stationIds);
        for (RailwayStopStation stop : stops) {
            seqNameMap.put(stop.getStopSeq(), stationName(stationMap, stop.getStationId()));
        }
        List<RailwaySegmentInventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<RailwaySegmentInventory>()
                        .eq(RailwaySegmentInventory::getServiceId, serviceId)
                        .orderByAsc(RailwaySegmentInventory::getFromStopSeq)
                        .orderByAsc(RailwaySegmentInventory::getId));
        return inventories.stream().map(item -> {
            RailwayInventoryVO vo = new RailwayInventoryVO();
            vo.setFromStopSeq(item.getFromStopSeq());
            vo.setToStopSeq(item.getToStopSeq());
            vo.setFromStationName(seqNameMap.getOrDefault(item.getFromStopSeq(), ""));
            vo.setToStationName(seqNameMap.getOrDefault(item.getToStopSeq(), ""));
            vo.setSeatType(item.getSeatType());
            vo.setTotalSeats(item.getTotalSeats());
            vo.setLeftSeats(item.getLeftSeats());
            vo.setPriceCents(item.getPriceCents());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public RailwayAdminStatsVO stats() {
        return orderService.stats();
    }

    private Map<Long, RailwayStation> loadStationMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        return stationMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(RailwayStation::getId, s -> s, (a, b) -> a));
    }

    private String stationName(Map<Long, RailwayStation> map, Long id) {
        RailwayStation station = map.get(id);
        return station == null ? "" : station.getStationName();
    }

    private Integer minLeft(List<RailwaySegmentInventory> inventories, String seatType) {
        return inventories.stream()
                .filter(i -> seatType.equals(i.getSeatType()))
                .map(RailwaySegmentInventory::getLeftSeats)
                .filter(v -> v != null)
                .min(Integer::compareTo)
                .orElse(0);
    }
}
