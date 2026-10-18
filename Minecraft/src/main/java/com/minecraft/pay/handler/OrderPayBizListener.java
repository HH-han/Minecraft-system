package com.minecraft.pay.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minecraft.entity.HotelBooking;
import com.minecraft.entity.Order;
import com.minecraft.entity.TicketBooking;
import com.minecraft.entity.User;
import com.minecraft.enums.OrderStatus;
import com.minecraft.mapper.HotelBookingMapper;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.mapper.TicketBookingMapper;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.enums.PayBizType;
import com.minecraft.pay.event.PaySuccessEvent;
import com.minecraft.pay.mapper.PayOrderMapper;
import com.minecraft.service.MemberService;
import com.minecraft.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 商城/酒店/票务等 orders 表业务支付成功处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPayBizListener {

    private final PayOrderMapper payOrderMapper;
    private final OrderMapper orderMapper;
    private final HotelBookingMapper hotelBookingMapper;
    private final TicketBookingMapper ticketBookingMapper;
    private final UserService userService;
    private final MemberService memberService;

    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void onPaySuccess(PaySuccessEvent event) {
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getPayNo, event.getPayNo())
                .last("LIMIT 1"));
        if (payOrder == null || !PayBizType.isOrderType(payOrder.getBizType())) {
            return;
        }
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, payOrder.getBizOrderNo())
                .last("LIMIT 1"));
        if (order == null) {
            log.warn("[支付成功] 未找到业务订单 payNo={}, bizOrderNo={}",
                    payOrder.getPayNo(), payOrder.getBizOrderNo());
            return;
        }

        // 条件更新保证只推进一次，天然幂等
        int updated = orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, OrderStatus.PENDING.getCode().toString())
                .set(Order::getStatus, OrderStatus.PAID.getCode().toString())
                .set(Order::getPayNo, payOrder.getPayNo())
                .set(Order::getUpdateTime, LocalDateTime.now()));
        if (updated == 0) {
            return;
        }

        // 支付奖励：1 元 = 1 积分（会员倍率加速）、1 元 = 1 成长值
        int basePoints = payOrder.getPayAmount() == null ? 0 : payOrder.getPayAmount().intValue();
        if (basePoints > 0 && order.getUserId() != null) {
            BigDecimal pointsRate = memberService.getPointsRate(order.getUserId());
            int points = new BigDecimal(basePoints)
                    .multiply(pointsRate)
                    .setScale(0, RoundingMode.DOWN)
                    .intValue();
            if (points > 0) {
                userService.addPoints(order.getUserId(), points, "支付订单获得积分");
            }
            memberService.addGrowth(order.getUserId(), basePoints, MemberService.SOURCE_CONSUME,
                    order.getOrderNo(), "消费获得成长值");
        }

        // 酒店/票务预订在支付成功后自动置为已完成，无需前端二次调用
        if ("hotel".equalsIgnoreCase(order.getItemType())) {
            hotelBookingMapper.update(null, new LambdaUpdateWrapper<HotelBooking>()
                    .eq(HotelBooking::getId, order.getItemId())
                    .eq(HotelBooking::getStatus, 0)
                    .set(HotelBooking::getStatus, 1)
                    .set(HotelBooking::getUpdatedAt, LocalDateTime.now()));
        } else if ("ticket".equalsIgnoreCase(order.getItemType())) {
            ticketBookingMapper.update(null, new LambdaUpdateWrapper<TicketBooking>()
                    .eq(TicketBooking::getId, order.getItemId())
                    .eq(TicketBooking::getStatus, 0)
                    .set(TicketBooking::getStatus, 1)
                    .set(TicketBooking::getUpdatedAt, LocalDateTime.now()));
        }

        log.info("[支付成功] 业务订单已更新 orderNo={}, payNo={}, amount={}",
                order.getOrderNo(), payOrder.getPayNo(), payOrder.getPayAmount());
    }
}
