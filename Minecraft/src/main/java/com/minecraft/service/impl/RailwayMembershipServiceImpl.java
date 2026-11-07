package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayMemberOverviewVO;
import com.minecraft.entity.RailwayMemberGift;
import com.minecraft.entity.RailwayMemberRedemption;
import com.minecraft.entity.RailwayOrder;
import com.minecraft.entity.RailwayPointLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayMemberGiftMapper;
import com.minecraft.mapper.RailwayMemberRedemptionMapper;
import com.minecraft.mapper.RailwayOrderMapper;
import com.minecraft.mapper.RailwayPointLogMapper;
import com.minecraft.service.MemberService;
import com.minecraft.service.RailwayMembershipService;
import com.minecraft.vo.MemberInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class RailwayMembershipServiceImpl implements RailwayMembershipService {

    private static final DateTimeFormatter NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final RailwayPointLogMapper pointLogMapper;
    private final RailwayMemberGiftMapper giftMapper;
    private final RailwayMemberRedemptionMapper redemptionMapper;
    private final RailwayOrderMapper orderMapper;
    private final MemberService memberService;

    @Override
    public RailwayMemberOverviewVO overview(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        MemberInfoVO member = memberService.getInfo(userId);

        RailwayMemberOverviewVO vo = new RailwayMemberOverviewVO();
        vo.setMember(member);
        vo.setPoints(currentBalance(userId));
        vo.setTotalEarned(totalEarned(userId));
        vo.setPaidOrders(orderMapper.selectCount(new LambdaQueryWrapper<RailwayOrder>()
                .eq(RailwayOrder::getUserId, userId)
                .eq(RailwayOrder::getStatus, "PAID")));
        vo.setTotalSpentCents(sumPaidAmount(userId));
        vo.setRecentLogs(pointLogMapper.selectList(new LambdaQueryWrapper<RailwayPointLog>()
                .eq(RailwayPointLog::getUserId, userId)
                .orderByDesc(RailwayPointLog::getId)
                .last("LIMIT 10")));
        return vo;
    }

    @Override
    public PageResponse<RailwayPointLog> pageLogs(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        int num = safePageNum(pageNum);
        int size = safePageSize(pageSize);
        Page<RailwayPointLog> page = pointLogMapper.selectPage(new Page<>(num, size),
                new LambdaQueryWrapper<RailwayPointLog>()
                        .eq(RailwayPointLog::getUserId, userId)
                        .orderByDesc(RailwayPointLog::getId));
        return new PageResponse<>(page.getRecords(), page.getTotal(), num, size);
    }

    @Override
    public List<RailwayMemberGift> listGifts() {
        return giftMapper.selectList(new LambdaQueryWrapper<RailwayMemberGift>()
                .eq(RailwayMemberGift::getStatus, 1)
                .orderByAsc(RailwayMemberGift::getSort)
                .orderByAsc(RailwayMemberGift::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RailwayMemberRedemption redeem(Long userId, Long giftId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        RailwayMemberGift gift = giftMapper.selectById(giftId);
        if (gift == null || !Integer.valueOf(1).equals(gift.getStatus())) {
            throw new BusinessException("该礼品已下架");
        }
        int balance = currentBalanceForUpdate(userId);
        int cost = gift.getPointsCost() == null ? 0 : gift.getPointsCost();
        if (balance < cost) {
            throw new BusinessException("积分不足，当前可用 " + balance + " 积分");
        }
        int updated = giftMapper.update(null, new LambdaUpdateWrapper<RailwayMemberGift>()
                .eq(RailwayMemberGift::getId, giftId)
                .gt(RailwayMemberGift::getStock, 0)
                .setSql("stock = stock - 1"));
        if (updated == 0) {
            throw new BusinessException("礼品库存不足");
        }

        String redeemNo = "X" + LocalDateTime.now().format(NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        RailwayPointLog pointLog = new RailwayPointLog();
        pointLog.setUserId(userId);
        pointLog.setChangeValue(-cost);
        pointLog.setAfterValue(balance - cost);
        pointLog.setBizType("REDEEM");
        pointLog.setBizNo(redeemNo);
        pointLog.setRemark("积分兑换：" + gift.getName());
        pointLogMapper.insert(pointLog);

        RailwayMemberRedemption redemption = new RailwayMemberRedemption();
        redemption.setRedeemNo(redeemNo);
        redemption.setUserId(userId);
        redemption.setGiftId(gift.getId());
        redemption.setGiftName(gift.getName());
        redemption.setPointsCost(cost);
        redemption.setRedeemCode(redeemNo.substring(redeemNo.length() - 8));
        redemption.setStatus("UNUSED");
        redemptionMapper.insert(redemption);
        return redemption;
    }

    /**
     * 在支付回调事务内执行，不单独开启事务：
     * 避免积分发放异常时将外层支付事务标记为 rollback-only。
     */
    @Override
    public void awardOrderPoints(String orderNo) {
        RailwayOrder order = orderMapper.selectOne(new LambdaQueryWrapper<RailwayOrder>()
                .eq(RailwayOrder::getOrderNo, orderNo)
                .last("LIMIT 1"));
        if (order == null || !"PAID".equals(order.getStatus()) || order.getUserId() == null) {
            return;
        }
        Long exists = pointLogMapper.selectCount(new LambdaQueryWrapper<RailwayPointLog>()
                .eq(RailwayPointLog::getUserId, order.getUserId())
                .eq(RailwayPointLog::getBizType, "ORDER_EARN")
                .eq(RailwayPointLog::getBizNo, orderNo));
        if (exists != null && exists > 0) {
            return;
        }

        int amountYuan = Math.max(1, (order.getTotalAmount() == null ? 0 : order.getTotalAmount()) / 100);
        BigDecimal rate = memberService.getPointsRate(order.getUserId());
        int earn = Math.max(1, BigDecimal.valueOf(amountYuan)
                .multiply(rate)
                .setScale(0, RoundingMode.DOWN)
                .intValue());

        RailwayPointLog pointLog = new RailwayPointLog();
        pointLog.setUserId(order.getUserId());
        pointLog.setChangeValue(earn);
        pointLog.setAfterValue(currentBalance(order.getUserId()) + earn);
        pointLog.setBizType("ORDER_EARN");
        pointLog.setBizNo(orderNo);
        pointLog.setRemark("铁路购票消费，金额 ¥" + amountYuan);
        try {
            pointLogMapper.insert(pointLog);
        } catch (DuplicateKeyException e) {
            // 支付回调重试时唯一索引兜底，避免重复发放
            return;
        }

        int growth = Math.max(1, amountYuan / 10);
        memberService.addGrowth(order.getUserId(), growth, MemberService.SOURCE_CONSUME,
                orderNo, "铁路购票消费");
        log.info("[铁路会员] 订单 {} 发放积分 {}、成长值 {}", orderNo, earn, growth);
    }

    private int currentBalance(Long userId) {
        RailwayPointLog last = pointLogMapper.selectOne(new LambdaQueryWrapper<RailwayPointLog>()
                .eq(RailwayPointLog::getUserId, userId)
                .orderByDesc(RailwayPointLog::getId)
                .last("LIMIT 1"));
        return last == null || last.getAfterValue() == null ? 0 : last.getAfterValue();
    }

    private int currentBalanceForUpdate(Long userId) {
        RailwayPointLog last = pointLogMapper.selectOne(new LambdaQueryWrapper<RailwayPointLog>()
                .eq(RailwayPointLog::getUserId, userId)
                .orderByDesc(RailwayPointLog::getId)
                .last("LIMIT 1 FOR UPDATE"));
        return last == null || last.getAfterValue() == null ? 0 : last.getAfterValue();
    }

    private int totalEarned(Long userId) {
        List<Map<String, Object>> maps = pointLogMapper.selectMaps(new QueryWrapper<RailwayPointLog>()
                .select("IFNULL(SUM(change_value), 0) AS total")
                .eq("user_id", userId)
                .gt("change_value", 0));
        if (maps == null || maps.isEmpty() || maps.get(0).get("total") == null) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(maps.get(0).get("total")));
    }

    private long sumPaidAmount(Long userId) {
        List<Map<String, Object>> maps = orderMapper.selectMaps(new QueryWrapper<RailwayOrder>()
                .select("IFNULL(SUM(total_amount), 0) AS amount")
                .eq("user_id", userId)
                .eq("status", "PAID"));
        if (maps == null || maps.isEmpty() || maps.get(0).get("amount") == null) {
            return 0L;
        }
        return Long.parseLong(String.valueOf(maps.get(0).get("amount")));
    }

    private int safePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int safePageSize(Integer pageSize) {
        int size = pageSize == null ? 10 : pageSize;
        return Math.max(1, Math.min(size, 50));
    }
}
