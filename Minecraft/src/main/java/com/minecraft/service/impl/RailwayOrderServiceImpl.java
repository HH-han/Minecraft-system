package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.RailwayOrderCreateRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayAdminStatsVO;
import com.minecraft.dto.response.RailwayOrderVO;
import com.minecraft.dto.response.RailwayTicketVO;
import com.minecraft.entity.RailwayOrder;
import com.minecraft.entity.RailwayPassenger;
import com.minecraft.entity.RailwaySegmentInventory;
import com.minecraft.entity.RailwayService;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayStopStation;
import com.minecraft.entity.RailwayTicket;
import com.minecraft.entity.RailwayTrain;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayOrderMapper;
import com.minecraft.mapper.RailwayPassengerMapper;
import com.minecraft.mapper.RailwaySegmentInventoryMapper;
import com.minecraft.mapper.RailwayServiceMapper;
import com.minecraft.mapper.RailwayStationMapper;
import com.minecraft.mapper.RailwayStopStationMapper;
import com.minecraft.mapper.RailwayTicketMapper;
import com.minecraft.mapper.RailwayTrainMapper;
import com.minecraft.pay.dto.PayCreateRequest;
import com.minecraft.pay.dto.PayCreateResponse;
import com.minecraft.pay.enums.PayBizType;
import com.minecraft.pay.enums.PayChannel;
import com.minecraft.pay.enums.PayStatus;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.service.RailwayOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class RailwayOrderServiceImpl implements RailwayOrderService {

    private static final List<String> SEAT_TYPES = List.of("SECOND_CLASS", "FIRST_CLASS", "SLEEPER");
    private static final DateTimeFormatter ORDER_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final RailwayOrderMapper orderMapper;
    private final RailwayTicketMapper ticketMapper;
    private final RailwayPassengerMapper passengerMapper;
    private final RailwayServiceMapper serviceMapper;
    private final RailwayTrainMapper trainMapper;
    private final RailwayStopStationMapper stopStationMapper;
    private final RailwayStationMapper stationMapper;
    private final RailwaySegmentInventoryMapper inventoryMapper;
    private final PayGatewayService payGatewayService;

    public RailwayOrderServiceImpl(RailwayOrderMapper orderMapper,
                                   RailwayTicketMapper ticketMapper,
                                   RailwayPassengerMapper passengerMapper,
                                   RailwayServiceMapper serviceMapper,
                                   RailwayTrainMapper trainMapper,
                                   RailwayStopStationMapper stopStationMapper,
                                   RailwayStationMapper stationMapper,
                                   RailwaySegmentInventoryMapper inventoryMapper,
                                   PayGatewayService payGatewayService) {
        this.orderMapper = orderMapper;
        this.ticketMapper = ticketMapper;
        this.passengerMapper = passengerMapper;
        this.serviceMapper = serviceMapper;
        this.trainMapper = trainMapper;
        this.stopStationMapper = stopStationMapper;
        this.stationMapper = stationMapper;
        this.inventoryMapper = inventoryMapper;
        this.payGatewayService = payGatewayService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RailwayOrderVO createOrder(Long userId, RailwayOrderCreateRequest request) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录后再购票");
        }
        if (!SEAT_TYPES.contains(request.getSeatType())) {
            throw new BusinessException("不支持的席别");
        }
        RailwayService service = serviceMapper.selectById(request.getServiceId());
        if (service == null || !Integer.valueOf(1).equals(service.getStatus())) {
            throw new BusinessException("车次运行信息不存在或已停运");
        }
        if (service.getServiceDate().isBefore(LocalDate.now())) {
            throw new BusinessException("该日期车次已停止售票");
        }
        List<RailwayStopStation> stops = stopStationMapper.selectList(
                new LambdaQueryWrapper<RailwayStopStation>()
                        .eq(RailwayStopStation::getServiceId, service.getId())
                        .orderByAsc(RailwayStopStation::getStopSeq));
        RailwayStopStation fromStop = stops.stream()
                .filter(s -> s.getStopSeq().equals(request.getFromStopSeq())).findFirst().orElse(null);
        RailwayStopStation toStop = stops.stream()
                .filter(s -> s.getStopSeq().equals(request.getToStopSeq())).findFirst().orElse(null);
        if (fromStop == null || toStop == null || fromStop.getStopSeq() >= toStop.getStopSeq()) {
            throw new BusinessException("上车站或下车站不正确");
        }
        Set<Long> passengerIds = new LinkedHashSet<>(request.getPassengerIds());
        List<RailwayPassenger> passengers = passengerMapper.selectBatchIds(passengerIds);
        if (passengers.size() != passengerIds.size()) {
            throw new BusinessException("乘车人信息不存在");
        }
        if (passengers.size() > 5) {
            throw new BusinessException("单笔订单最多购买 5 张车票");
        }
        RailwayTrain train = trainMapper.selectById(service.getTrainId());
        if (train == null) {
            throw new BusinessException("车次信息不存在");
        }
        for (RailwayPassenger passenger : passengers) {
            if (!userId.equals(passenger.getUserId())) {
                throw new BusinessException("乘车人信息不属于当前用户");
            }
            int active = ticketMapper.countActiveTicket(passenger.getIdCardNo(), train.getId(),
                    service.getServiceDate());
            if (active > 0) {
                throw new BusinessException("乘车人 " + passenger.getName() + " 已购买当日 " + train.getTrainNo() + " 车票");
            }
        }

        int expectedSegments = toStop.getStopSeq() - fromStop.getStopSeq();
        List<RailwaySegmentInventory> segments = inventoryMapper.selectList(
                new LambdaQueryWrapper<RailwaySegmentInventory>()
                        .eq(RailwaySegmentInventory::getServiceId, service.getId())
                        .eq(RailwaySegmentInventory::getSeatType, request.getSeatType())
                        .ge(RailwaySegmentInventory::getFromStopSeq, fromStop.getStopSeq())
                        .le(RailwaySegmentInventory::getToStopSeq, toStop.getStopSeq())
                        .orderByAsc(RailwaySegmentInventory::getFromStopSeq));
        if (segments.size() < expectedSegments) {
            throw new BusinessException("该席别在该区间暂无库存");
        }
        int unitPrice = segments.stream()
                .mapToInt(s -> s.getPriceCents() == null ? 0 : s.getPriceCents())
                .sum();

        // 逐段扣减库存：任意一段库存不足即整单回滚
        for (int i = 0; i < passengers.size(); i++) {
            for (int seq = fromStop.getStopSeq(); seq < toStop.getStopSeq(); seq++) {
                int updated = inventoryMapper.decrement(service.getId(), seq, seq + 1,
                        request.getSeatType(), 1);
                if (updated == 0) {
                    throw new BusinessException("余票不足，请重新查询后再试");
                }
            }
        }

        RailwayOrder order = new RailwayOrder();
        order.setOrderNo("R" + LocalDateTime.now().format(ORDER_NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        order.setUserId(userId);
        order.setServiceId(service.getId());
        order.setFromStopSeq(fromStop.getStopSeq());
        order.setToStopSeq(toStop.getStopSeq());
        order.setSeatType(request.getSeatType());
        order.setPassengerCount(passengers.size());
        order.setTotalAmount(unitPrice * passengers.size());
        order.setContactPhone(StringUtils.hasText(request.getContactPhone())
                ? request.getContactPhone() : passengers.get(0).getPhone());
        order.setStatus("PENDING");
        order.setExpireTime(LocalDateTime.now().plusMinutes(15));
        orderMapper.insert(order);

        int index = 0;
        for (RailwayPassenger passenger : passengers) {
            RailwayTicket ticket = new RailwayTicket();
            ticket.setOrderId(order.getId());
            ticket.setPassengerId(passenger.getId());
            ticket.setPassengerName(passenger.getName());
            ticket.setIdCardNo(passenger.getIdCardNo());
            ticket.setSeatNo(generateSeatNo(passenger.getIdCardNo(), index++));
            ticket.setPriceCents(unitPrice);
            ticket.setStatus("ACTIVE");
            ticketMapper.insert(ticket);
        }
        return toOrderVO(order);
    }

    @Override
    public PageResponse<RailwayOrderVO> listOrders(Long userId, Integer pageNum, Integer pageSize, String status) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Page<RailwayOrder> page = new Page<>(safePageNum(pageNum), safePageSize(pageSize));
        LambdaQueryWrapper<RailwayOrder> wrapper = new LambdaQueryWrapper<RailwayOrder>()
                .eq(RailwayOrder::getUserId, userId)
                .orderByDesc(RailwayOrder::getCreateTime);
        if (StringUtils.hasText(status)) {
            wrapper.eq(RailwayOrder::getStatus, status);
        }
        Page<RailwayOrder> result = orderMapper.selectPage(page, wrapper);
        List<RailwayOrderVO> records = result.getRecords().stream()
                .map(this::toOrderVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), safePageNum(pageNum), safePageSize(pageSize));
    }

    @Override
    public PageResponse<RailwayOrderVO> listAllOrders(Integer pageNum, Integer pageSize,
                                                      String status, String keyword) {
        Page<RailwayOrder> page = new Page<>(safePageNum(pageNum), safePageSize(pageSize));
        LambdaQueryWrapper<RailwayOrder> wrapper = new LambdaQueryWrapper<RailwayOrder>()
                .orderByDesc(RailwayOrder::getCreateTime);
        if (StringUtils.hasText(status)) {
            wrapper.eq(RailwayOrder::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(RailwayOrder::getOrderNo, kw)
                    .or().like(RailwayOrder::getContactPhone, kw));
        }
        Page<RailwayOrder> result = orderMapper.selectPage(page, wrapper);
        List<RailwayOrderVO> records = result.getRecords().stream()
                .map(this::toOrderVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), safePageNum(pageNum), safePageSize(pageSize));
    }

    @Override
    public RailwayOrderVO getOrder(Long userId, Long orderId) {
        return toOrderVO(getOwnedOrder(userId, orderId));
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public void payOrder(Long userId, Long orderId) {
        RailwayOrder order = getOwnedOrder(userId, orderId);
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不可支付");
        }
        if (order.getExpireTime() != null && order.getExpireTime().isBefore(LocalDateTime.now())) {
            cancelInternal(order);
            throw new BusinessException("订单已超时取消，请重新下单");
        }
        BigDecimal amount = BigDecimal.valueOf(order.getTotalAmount() == null ? 0 : order.getTotalAmount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (amount.signum() <= 0) {
            throw new BusinessException("订单金额异常");
        }

        PayCreateRequest request = new PayCreateRequest();
        request.setBizOrderNo(order.getOrderNo());
        request.setBizType(PayBizType.RAILWAY.name());
        request.setUserId(userId);
        request.setTotalAmount(amount);
        request.setChannelCode(PayChannel.MOCK.name());
        request.setSubject("铁路车票：" + order.getOrderNo());
        PayCreateResponse response = payGatewayService.pay(request);
        if (!PayStatus.isSuccess(response.getStatus())) {
            throw new BusinessException("支付未完成");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        RailwayOrder order = getOwnedOrder(userId, orderId);
        if ("CANCELLED".equals(order.getStatus()) || "REFUNDED".equals(order.getStatus())) {
            throw new BusinessException("订单已取消");
        }
        cancelInternal(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelExpiredOrders() {
        List<RailwayOrder> expired = orderMapper.selectList(new LambdaQueryWrapper<RailwayOrder>()
                .eq(RailwayOrder::getStatus, "PENDING")
                .lt(RailwayOrder::getExpireTime, LocalDateTime.now())
                .last("LIMIT 100"));
        for (RailwayOrder order : expired) {
            cancelInternal(order);
        }
        return expired.size();
    }

    @Override
    public RailwayAdminStatsVO stats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        RailwayAdminStatsVO vo = new RailwayAdminStatsVO();
        vo.setTodayOrders(orderMapper.selectCount(new LambdaQueryWrapper<RailwayOrder>()
                .ge(RailwayOrder::getCreateTime, todayStart)));
        vo.setPendingOrders(orderMapper.selectCount(new LambdaQueryWrapper<RailwayOrder>()
                .eq(RailwayOrder::getStatus, "PENDING")));
        vo.setPaidOrders(orderMapper.selectCount(new LambdaQueryWrapper<RailwayOrder>()
                .eq(RailwayOrder::getStatus, "PAID")));
        vo.setTotalOrders(orderMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setTodayRevenue(sumAmount(new QueryWrapper<RailwayOrder>()
                .select("IFNULL(SUM(total_amount), 0) AS amount")
                .eq("status", "PAID")
                .ge("pay_time", todayStart)));
        vo.setTotalRevenue(sumAmount(new QueryWrapper<RailwayOrder>()
                .select("IFNULL(SUM(total_amount), 0) AS amount")
                .eq("status", "PAID")));
        return vo;
    }

    private long sumAmount(QueryWrapper<RailwayOrder> wrapper) {
        List<Map<String, Object>> maps = orderMapper.selectMaps(wrapper);
        if (maps == null || maps.isEmpty()) {
            return 0L;
        }
        Object value = maps.get(0).get("amount");
        return value == null ? 0L : Long.parseLong(String.valueOf(value));
    }

    private RailwayOrder getOwnedOrder(Long userId, Long orderId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        RailwayOrder order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    /**
     * 取消订单并归还所有相邻区间库存
     */
    private void cancelInternal(RailwayOrder order) {
        List<RailwayTicket> tickets = ticketMapper.selectList(new LambdaQueryWrapper<RailwayTicket>()
                .eq(RailwayTicket::getOrderId, order.getId()));
        int count = tickets.size();
        for (int seq = order.getFromStopSeq(); seq < order.getToStopSeq(); seq++) {
            inventoryMapper.increment(order.getServiceId(), seq, seq + 1, order.getSeatType(), count);
        }
        for (RailwayTicket ticket : tickets) {
            if (!"REFUNDED".equals(ticket.getStatus())) {
                ticket.setStatus("REFUNDED");
                ticketMapper.updateById(ticket);
            }
        }
        boolean paid = "PAID".equals(order.getStatus());
        order.setStatus(paid ? "REFUNDED" : "CANCELLED");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private RailwayOrderVO toOrderVO(RailwayOrder order) {
        RailwayOrderVO vo = new RailwayOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setServiceId(order.getServiceId());
        vo.setSeatType(order.getSeatType());
        vo.setPassengerCount(order.getPassengerCount());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setContactPhone(order.getContactPhone());
        vo.setStatus(order.getStatus());
        vo.setExpireTime(order.getExpireTime());
        vo.setPayTime(order.getPayTime());
        vo.setCreateTime(order.getCreateTime());

        RailwayService service = serviceMapper.selectById(order.getServiceId());
        if (service != null) {
            vo.setServiceDate(service.getServiceDate());
            RailwayTrain train = trainMapper.selectById(service.getTrainId());
            if (train != null) {
                vo.setTrainNo(train.getTrainNo());
                vo.setTrainType(train.getTrainType());
            }
            List<RailwayStopStation> stops = stopStationMapper.selectList(
                    new LambdaQueryWrapper<RailwayStopStation>()
                            .eq(RailwayStopStation::getServiceId, service.getId())
                            .orderByAsc(RailwayStopStation::getStopSeq));
            RailwayStopStation fromStop = stops.stream()
                    .filter(s -> s.getStopSeq().equals(order.getFromStopSeq())).findFirst().orElse(null);
            RailwayStopStation toStop = stops.stream()
                    .filter(s -> s.getStopSeq().equals(order.getToStopSeq())).findFirst().orElse(null);
            Map<Long, RailwayStation> stationMap = loadStationMap(stops.stream()
                    .map(RailwayStopStation::getStationId).collect(Collectors.toSet()));
            if (fromStop != null) {
                vo.setFromStationName(stationName(stationMap, fromStop.getStationId()));
                vo.setFromTime(fromStop.getDepartTime() != null ? fromStop.getDepartTime() : fromStop.getArriveTime());
            }
            if (toStop != null) {
                vo.setToStationName(stationName(stationMap, toStop.getStationId()));
                vo.setToTime(toStop.getArriveTime() != null ? toStop.getArriveTime() : toStop.getDepartTime());
                vo.setArriveDayOffset((toStop.getDayOffset() == null ? 0 : toStop.getDayOffset())
                        - (fromStop != null && fromStop.getDayOffset() != null ? fromStop.getDayOffset() : 0));
            }
        }
        List<RailwayTicket> tickets = ticketMapper.selectList(new LambdaQueryWrapper<RailwayTicket>()
                .eq(RailwayTicket::getOrderId, order.getId())
                .orderByAsc(RailwayTicket::getId));
        vo.setTickets(tickets.stream().map(t -> {
            RailwayTicketVO ticketVO = new RailwayTicketVO();
            ticketVO.setId(t.getId());
            ticketVO.setPassengerName(t.getPassengerName());
            ticketVO.setIdCardNo(t.getIdCardNo());
            ticketVO.setSeatNo(t.getSeatNo());
            ticketVO.setPriceCents(t.getPriceCents());
            ticketVO.setStatus(t.getStatus());
            return ticketVO;
        }).collect(Collectors.toList()));
        return vo;
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

    private String generateSeatNo(String idCardNo, int index) {
        int hash = Math.abs(idCardNo.hashCode()) + index;
        int car = hash % 8 + 1;
        int row = hash % 18 + 1;
        char letter = "ABCDEF".charAt(hash % 6);
        return String.format("%02d车%02d%s", car, row, letter);
    }

    private int safePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int safePageSize(Integer pageSize) {
        int size = pageSize == null ? 10 : pageSize;
        return Math.max(1, Math.min(size, 50));
    }
}
