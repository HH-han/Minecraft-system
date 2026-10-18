package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RefundOrderVO;
import com.minecraft.entity.AfterSale;
import com.minecraft.enums.AfterSaleStatus;
import com.minecraft.enums.RefundStatus;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AfterSaleMapper;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.entity.PayRefund;
import com.minecraft.pay.enums.PayStatus;
import com.minecraft.pay.mapper.PayOrderMapper;
import com.minecraft.pay.mapper.PayRefundMapper;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 退款服务实现：统一委托聚合支付网关，支持全额/部分退款
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl extends ServiceImpl<PayRefundMapper, PayRefund> implements RefundService {

    private final AfterSaleMapper afterSaleMapper;
    private final PayOrderMapper payOrderMapper;
    private final PayGatewayService payGatewayService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayRefund createRefund(Long afterSaleId, Long adminId) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException("售后单不存在");
        }

        // 幂等：同一售后单已有成功/退款中的退款单则直接返回
        PayRefund exist = getOne(new LambdaQueryWrapper<PayRefund>()
                .eq(PayRefund::getAfterSaleId, afterSaleId)
                .ne(PayRefund::getStatus, RefundStatus.FAILED.getCode())
                .last("LIMIT 1"));
        if (exist != null) {
            return exist;
        }

        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getBizOrderNo, afterSale.getOrderNo())
                .in(PayOrder::getStatus, PayStatus.SUCCESS.name(), PayStatus.REFUNDING.name())
                .orderByDesc(PayOrder::getId)
                .last("LIMIT 1"));
        if (payOrder == null) {
            throw new BusinessException("未找到该订单的有效支付记录，无法退款");
        }

        java.math.BigDecimal amount = afterSale.getActualAmount() != null
                ? afterSale.getActualAmount() : afterSale.getRefundAmount();
        String reason = afterSale.getReason() != null ? afterSale.getReason() : "售后退款";

        PayRefund refund = payGatewayService.refund(payOrder.getPayNo(), amount, reason, afterSaleId);

        if (RefundStatus.REFUNDING.getCode().equals(refund.getStatus())
                || RefundStatus.PENDING.getCode().equals(refund.getStatus())) {
            afterSale.setStatus(AfterSaleStatus.REFUNDING.getCode());
            afterSaleMapper.updateById(afterSale);
        }
        log.info("[售后] 发起退款 afterSaleId={}, refundNo={}, amount={}, adminId={}",
                afterSaleId, refund.getRefundNo(), amount, adminId);
        return refund;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyResult(Long refundId, boolean success, String channelRefundNo, String failReason) {
        payGatewayService.handleRefundResult(refundId, success, channelRefundNo, failReason);
    }

    @Override
    public PageResponse<?> page(PageRequest request) {
        Page<PayRefund> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<PayRefund> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PayRefund::getCreatedAt);
        Page<PayRefund> result = page(page, wrapper);
        List<RefundOrderVO> records = result.getRecords().stream()
                .map(this::toVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    private RefundOrderVO toVO(PayRefund refund) {
        RefundOrderVO vo = new RefundOrderVO();
        vo.setId(refund.getId());
        vo.setRefundNo(refund.getRefundNo());
        vo.setOrderNo(refund.getBizOrderNo());
        vo.setChannel(refund.getChannelCode());
        vo.setAmount(refund.getRefundAmount());
        vo.setStatus(refund.getStatus());
        vo.setStatusDesc(RefundStatus.descriptionOf(refund.getStatus()));
        vo.setChannelRefundNo(refund.getChannelRefundNo());
        vo.setFailReason(refund.getFailReason());
        vo.setRefundTime(refund.getRefundTime());
        vo.setCreateTime(refund.getCreatedAt());
        return vo;
    }
}
