package com.minecraft.pay.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.enums.PayBizType;
import com.minecraft.pay.event.PaySuccessEvent;
import com.minecraft.pay.mapper.PayOrderMapper;
import com.minecraft.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员开通订单支付成功处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberPayBizListener {

    private final PayOrderMapper payOrderMapper;
    private final MemberService memberService;

    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void onPaySuccess(PaySuccessEvent event) {
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getPayNo, event.getPayNo())
                .last("LIMIT 1"));
        if (payOrder == null || !PayBizType.MEMBER.name().equals(payOrder.getBizType())) {
            return;
        }
        memberService.markPaid(payOrder.getBizOrderNo());
        log.info("[支付成功] 会员订单已开通 orderNo={}, payNo={}",
                payOrder.getBizOrderNo(), payOrder.getPayNo());
    }
}
