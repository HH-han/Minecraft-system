package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.RailwayGroupBookingRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayGroupQuoteVO;
import com.minecraft.entity.RailwayGroupBooking;
import com.minecraft.entity.RailwayStation;
import com.minecraft.entity.RailwayTrain;
import com.minecraft.entity.RailwayTrainStop;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayGroupBookingMapper;
import com.minecraft.mapper.RailwayStationMapper;
import com.minecraft.mapper.RailwayTrainMapper;
import com.minecraft.mapper.RailwayTrainStopMapper;
import com.minecraft.service.RailwayGroupBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RailwayGroupBookingServiceImpl implements RailwayGroupBookingService {

    private static final List<String> SEAT_TYPES = List.of("SECOND_CLASS", "FIRST_CLASS", "SLEEPER");
    private static final DateTimeFormatter NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final RailwayGroupBookingMapper bookingMapper;
    private final RailwayTrainMapper trainMapper;
    private final RailwayTrainStopMapper trainStopMapper;
    private final RailwayStationMapper stationMapper;

    @Override
    public RailwayGroupQuoteVO quote(String fromStation, String toStation, LocalDate travelDate,
                                     Integer passengerCount, String seatType, String trainNo) {
        validateBase(fromStation, toStation, travelDate, passengerCount, seatType);
        Set<Long> fromIds = resolveStationIds(fromStation);
        Set<Long> toIds = resolveStationIds(toStation);

        List<RailwayTrain> trains;
        if (StringUtils.hasText(trainNo)) {
            RailwayTrain train = trainMapper.selectOne(new LambdaQueryWrapper<RailwayTrain>()
                    .eq(RailwayTrain::getTrainNo, trainNo.trim().toUpperCase())
                    .eq(RailwayTrain::getStatus, 1)
                    .last("LIMIT 1"));
            trains = train == null ? new ArrayList<>() : List.of(train);
        } else {
            trains = trainMapper.selectList(new LambdaQueryWrapper<RailwayTrain>()
                    .eq(RailwayTrain::getStatus, 1));
        }

        int bestUnitPrice = 0;
        String matchedTrainNo = null;
        for (RailwayTrain train : trains) {
            List<RailwayTrainStop> stops = trainStopMapper.selectList(
                    new LambdaQueryWrapper<RailwayTrainStop>()
                            .eq(RailwayTrainStop::getTrainId, train.getId())
                            .orderByAsc(RailwayTrainStop::getStopSeq));
            if (stops.size() < 2) {
                continue;
            }
            int fromIndex = -1;
            int toIndex = -1;
            for (int i = 0; i < stops.size(); i++) {
                if (fromIndex < 0 && fromIds.contains(stops.get(i).getStationId())) {
                    fromIndex = i;
                } else if (fromIndex >= 0 && toIds.contains(stops.get(i).getStationId())) {
                    toIndex = i;
                    break;
                }
            }
            if (fromIndex < 0 || toIndex <= fromIndex) {
                continue;
            }
            int unitPrice = 0;
            for (int i = fromIndex + 1; i <= toIndex; i++) {
                unitPrice += segmentPrice(stops.get(i), seatType);
            }
            if (unitPrice <= 0) {
                continue;
            }
            if (bestUnitPrice == 0 || unitPrice < bestUnitPrice) {
                bestUnitPrice = unitPrice;
                matchedTrainNo = train.getTrainNo();
            }
        }

        BigDecimal rate = discountRate(passengerCount);
        int total = BigDecimal.valueOf(bestUnitPrice)
                .multiply(BigDecimal.valueOf(passengerCount))
                .multiply(rate)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        RailwayGroupQuoteVO vo = new RailwayGroupQuoteVO();
        vo.setMatched(bestUnitPrice > 0);
        vo.setTrainNo(matchedTrainNo);
        vo.setUnitPriceCents(bestUnitPrice);
        vo.setDiscountRate(rate);
        vo.setPassengerCount(passengerCount);
        vo.setTotalAmountCents(total);
        vo.setMessage(bestUnitPrice > 0
                ? "已按 " + matchedTrainNo + " 团体票价预估，最终以专人核价为准"
                : "该线路暂无固定车次，提交申请后由团购专员单独报价");
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RailwayGroupBooking create(Long userId, RailwayGroupBookingRequest request) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录后再提交团购申请");
        }
        if (request.getReturnDate() != null && request.getReturnDate().isBefore(request.getTravelDate())) {
            throw new BusinessException("返程日期不能早于乘车日期");
        }
        RailwayGroupQuoteVO quote = quote(request.getFromStation(), request.getToStation(),
                request.getTravelDate(), request.getPassengerCount(), request.getSeatType(),
                request.getTrainNo());

        RailwayGroupBooking booking = new RailwayGroupBooking();
        booking.setBookingNo("T" + LocalDateTime.now().format(NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        booking.setUserId(userId);
        booking.setCompanyName(request.getCompanyName().trim());
        booking.setContactName(request.getContactName().trim());
        booking.setContactPhone(request.getContactPhone().trim());
        booking.setFromStation(request.getFromStation().trim());
        booking.setToStation(request.getToStation().trim());
        booking.setTravelDate(request.getTravelDate());
        booking.setReturnDate(request.getReturnDate());
        booking.setPassengerCount(request.getPassengerCount());
        booking.setSeatType(request.getSeatType());
        booking.setTrainNo(StringUtils.hasText(request.getTrainNo())
                ? request.getTrainNo().trim().toUpperCase() : quote.getTrainNo());
        booking.setUnitPriceCents(quote.getUnitPriceCents());
        booking.setDiscountRate(quote.getDiscountRate());
        booking.setExpectedAmountCents(quote.getTotalAmountCents());
        booking.setStatus("PENDING");
        booking.setRemark(request.getRemark());
        bookingMapper.insert(booking);
        return booking;
    }

    @Override
    public PageResponse<RailwayGroupBooking> list(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        int num = safePageNum(pageNum);
        int size = safePageSize(pageSize);
        Page<RailwayGroupBooking> page = bookingMapper.selectPage(new Page<>(num, size),
                new LambdaQueryWrapper<RailwayGroupBooking>()
                        .eq(RailwayGroupBooking::getUserId, userId)
                        .orderByDesc(RailwayGroupBooking::getCreateTime));
        return new PageResponse<>(page.getRecords(), page.getTotal(), num, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long id) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        RailwayGroupBooking booking = bookingMapper.selectById(id);
        if (booking == null || !userId.equals(booking.getUserId())) {
            throw new BusinessException("团购申请不存在");
        }
        if (!"PENDING".equals(booking.getStatus())) {
            throw new BusinessException("当前申请状态不可取消");
        }
        booking.setStatus("CANCELLED");
        bookingMapper.updateById(booking);
    }

    private void validateBase(String fromStation, String toStation, LocalDate travelDate,
                              Integer passengerCount, String seatType) {
        if (!StringUtils.hasText(fromStation) || !StringUtils.hasText(toStation)) {
            throw new BusinessException("请填写出发站与到达站");
        }
        if (fromStation.trim().equals(toStation.trim())) {
            throw new BusinessException("出发站与到达站不能相同");
        }
        if (travelDate == null) {
            throw new BusinessException("请选择乘车日期");
        }
        LocalDate today = LocalDate.now();
        if (travelDate.isBefore(today)) {
            throw new BusinessException("乘车日期不能早于今天");
        }
        if (travelDate.isAfter(today.plusDays(60))) {
            throw new BusinessException("团体购票仅支持 60 天内的日期");
        }
        if (passengerCount == null || passengerCount < 20 || passengerCount > 500) {
            throw new BusinessException("团体购票人数需在 20 至 500 人之间");
        }
        if (!SEAT_TYPES.contains(seatType)) {
            throw new BusinessException("不支持的席别");
        }
        if (resolveStationIds(fromStation).isEmpty()) {
            throw new BusinessException("出发车站不存在：" + fromStation);
        }
        if (resolveStationIds(toStation).isEmpty()) {
            throw new BusinessException("到达车站不存在：" + toStation);
        }
    }

    private int segmentPrice(RailwayTrainStop stop, String seatType) {
        Integer price = switch (seatType) {
            case "FIRST_CLASS" -> stop.getFirstClassPriceCents();
            case "SLEEPER" -> stop.getSleeperPriceCents();
            default -> stop.getSecondClassPriceCents();
        };
        return price == null ? 0 : price;
    }

    private BigDecimal discountRate(Integer passengerCount) {
        if (passengerCount >= 100) {
            return new BigDecimal("0.90");
        }
        if (passengerCount >= 50) {
            return new BigDecimal("0.92");
        }
        return new BigDecimal("0.95");
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
        return stationMapper.selectList(new LambdaQueryWrapper<RailwayStation>()
                        .like(RailwayStation::getStationName, kw).last("LIMIT 20"))
                .stream().map(RailwayStation::getId).collect(Collectors.toSet());
    }

    private int safePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int safePageSize(Integer pageSize) {
        int size = pageSize == null ? 10 : pageSize;
        return Math.max(1, Math.min(size, 50));
    }
}
