package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RefundOrderVO;
import com.minecraft.entity.AfterSale;
import com.minecraft.entity.Payment;
import com.minecraft.entity.RefundOrder;
import com.minecraft.enums.AfterSaleStatus;
import com.minecraft.enums.PaymentStatus;
import com.minecraft.enums.RefundStatus;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AfterSaleMapper;
import com.minecraft.mapper.PaymentMapper;
import com.minecraft.mapper.RefundOrderMapper;
import com.minecraft.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 退款服务实现（当前支付为模拟网关，退款为模拟/人工入账）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl extends ServiceImpl<RefundOrderMapper, RefundOrder> implements RefundService {

    private final AfterSaleMapper afterSaleMapper;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundOrder createRefund(Long afterSaleId, Long adminId) {
        AfterSale as = afterSaleMapper.selectById(afterSaleId);
        if (as == null) {
            throw new BusinessException("售后单不存在");
        }
        // 幂等：同一售后单已有成功/进行中的退款单则直接返回
        RefundOrder exist = getOne(new LambdaQueryWrapper<RefundOrder>()
                .eq(RefundOrder::getAfterSaleId, afterSaleId)
                .ne(RefundOrder::getStatus, RefundStatus.FAILED.getCode())
                .last("LIMIT 1"));
        if (exist != null) {
            return exist;
        }

        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, as.getOrderId())
                .eq(Payment::getStatus, PaymentStatus.SUCCESS.getCode().toString())
                .last("LIMIT 1"));

        RefundOrder refund = new RefundOrder();
        refund.setRefundNo(generateRefundNo());
        refund.setAfterSaleId(afterSaleId);
        refund.setOrderId(as.getOrderId());
        refund.setOrderNo(as.getOrderNo());
        refund.setUserId(as.getUserId());
        if (payment != null) {
            refund.setPayId(payment.getId());
            refund.setPaymentNo(payment.getPaymentNo());
            refund.setChannel(payment.getPaymentMethod());
        }
        refund.setAmount(as.getActualAmount() != null ? as.getActualAmount() : as.getRefundAmount());
        refund.setStatus(RefundStatus.REFUNDING.getCode());
        save(refund);

        if (payment != null) {
            payment.setStatus(PaymentStatus.REFUNDING.getCode().toString());
            paymentMapper.updateById(payment);
        }
        // TODO 调用真实渠道退款接口（当前为模拟网关，仅记录退款单，待模拟/人工回调）
        log.info("[退款] 生成退款单 refundNo={}, amount={}（模拟渠道）", refund.getRefundNo(), refund.getAmount());
        return refund;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyResult(Long refundId, boolean success, String channelRefundNo, String failReason) {
        RefundOrder refund = getById(refundId);
        if (refund == null) {
            throw new BusinessException("退款单不存在");
        }
        refund.setChannelRefundNo(channelRefundNo);
        if (success) {
            refund.setStatus(RefundStatus.SUCCESS.getCode());
            refund.setRefundTime(LocalDateTime.now());
        } else {
            refund.setStatus(RefundStatus.FAILED.getCode());
            refund.setFailReason(failReason);
        }
        updateById(refund);

        // 同步原支付单状态
        if (refund.getPayId() != null) {
            Payment payment = paymentMapper.selectById(refund.getPayId());
            if (payment != null) {
                payment.setStatus(success
                        ? PaymentStatus.REFUNDED.getCode().toString()
                        : PaymentStatus.SUCCESS.getCode().toString());
                paymentMapper.updateById(payment);
            }
        }

        if (success && refund.getAfterSaleId() != null) {
            AfterSale as = afterSaleMapper.selectById(refund.getAfterSaleId());
            if (as != null) {
                as.setStatus(AfterSaleStatus.COMPLETED.getCode());
                as.setRefundNo(refund.getRefundNo());
                as.setRefundTime(refund.getRefundTime());
                afterSaleMapper.updateById(as);
                // TODO 同步 orders.after_sale_status = 2
            }
        } else if (!success) {
            // TODO 失败重试（3 次），仍失败转人工
        }
    }

    @Override
    public PageResponse<?> page(PageRequest request) {
        Page<RefundOrder> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<RefundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RefundOrder::getCreateTime);
        Page<RefundOrder> result = page(page, wrapper);
        List<RefundOrderVO> records = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    private RefundOrderVO toVO(RefundOrder refund) {
        RefundOrderVO vo = new RefundOrderVO();
        vo.setId(refund.getId());
        vo.setRefundNo(refund.getRefundNo());
        vo.setOrderNo(refund.getOrderNo());
        vo.setChannel(refund.getChannel());
        vo.setAmount(refund.getAmount());
        vo.setStatus(refund.getStatus());
        vo.setStatusDesc(RefundStatus.descriptionOf(refund.getStatus()));
        vo.setChannelRefundNo(refund.getChannelRefundNo());
        vo.setFailReason(refund.getFailReason());
        vo.setRefundTime(refund.getRefundTime());
        vo.setCreateTime(refund.getCreateTime());
        return vo;
    }

    private String generateRefundNo() {
        // TODO 建议改为 "RF" + yyyyMMdd + 4位序列
        return "RF" + System.currentTimeMillis();
    }
}
