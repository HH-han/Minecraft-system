package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.AfterSaleApplyRequest;
import com.minecraft.dto.request.AfterSaleAuditRequest;
import com.minecraft.dto.request.AfterSaleMessageRequest;
import com.minecraft.dto.request.AfterSaleReturnRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.AfterSaleVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.AfterSale;
import com.minecraft.entity.AfterSaleMessage;
import com.minecraft.entity.Order;
import com.minecraft.enums.AfterSaleStatus;
import com.minecraft.enums.AfterSaleType;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AfterSaleMapper;
import com.minecraft.mapper.AfterSaleMessageMapper;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.service.AfterSaleService;
import com.minecraft.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 售后服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AfterSaleServiceImpl extends ServiceImpl<AfterSaleMapper, AfterSale> implements AfterSaleService {

    private final OrderMapper orderMapper;
    private final AfterSaleMessageMapper messageMapper;
    private final RefundService refundService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AfterSaleVO apply(Long userId, AfterSaleApplyRequest request) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }
        // TODO 售后时间窗口校验（如签收后 N 天）、可售/库存校验、重复申请校验

        AfterSale as = new AfterSale();
        as.setAfterSaleNo(generateAfterSaleNo());
        as.setOrderId(order.getId());
        as.setOrderNo(order.getOrderNo());
        as.setUserId(userId);
        as.setItemType(order.getItemType());
        as.setItemId(order.getItemId());
        as.setItemName(order.getItemName());
        as.setItemImage(order.getImage());
        as.setQuantity(request.getQuantity() != null ? request.getQuantity() : order.getQuantity());
        as.setType(request.getType());
        as.setReasonType(request.getReasonType());
        as.setReason(request.getReason());
        as.setEvidenceUrls(joinUrls(request.getEvidenceUrls()));
        as.setRefundAmount(request.getRefundAmount() != null ? request.getRefundAmount() : order.getAmount());
        as.setStatus(AfterSaleStatus.PENDING.getCode());
        save(as);

        order.setAfterSaleStatus(1);
        orderMapper.updateById(order);
        return toVO(as);
    }

    @Override
    public PageResponse<?> pageByUser(Long userId, PageRequest request) {
        Page<AfterSale> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AfterSale::getUserId, userId).orderByDesc(AfterSale::getCreateTime);
        Page<AfterSale> result = page(page, wrapper);
        List<AfterSaleVO> records = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public AfterSaleVO detail(Long id, Long userId) {
        AfterSale as = getById(id);
        if (as == null) {
            throw new BusinessException("售后单不存在");
        }
        if (userId != null && !as.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看该售后单");
        }
        AfterSaleVO vo = toVO(as);
        vo.setMessages(messageMapper.selectList(new LambdaQueryWrapper<AfterSaleMessage>()
                .eq(AfterSaleMessage::getAfterSaleId, id)
                .orderByAsc(AfterSaleMessage::getCreateTime)));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnGoods(Long id, Long userId, AfterSaleReturnRequest request) {
        AfterSale as = requireOwned(id, userId);
        as.setReturnCarrier(request.getReturnCarrier());
        as.setReturnTracking(request.getReturnTracking());
        as.setReturnTime(LocalDateTime.now());
        as.setStatus(AfterSaleStatus.WAIT_SELLER_RECEIVE.getCode());
        updateById(as);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id, Long userId) {
        AfterSale as = requireOwned(id, userId);
        as.setStatus(AfterSaleStatus.CLOSED.getCode());
        as.setCloseReason("买家撤销");
        updateById(as);
        resetOrderAfterSale(as.getOrderId());
    }

    @Override
    public void message(Long id, Long userId, AfterSaleMessageRequest request) {
        requireOwned(id, userId);
        AfterSaleMessage msg = new AfterSaleMessage();
        msg.setAfterSaleId(id);
        msg.setSenderType(1);
        msg.setSenderId(userId);
        msg.setContent(request.getContent());
        msg.setEvidenceUrls(joinUrls(request.getEvidenceUrls()));
        messageMapper.insert(msg);
    }

    @Override
    public PageResponse<?> pageForAdmin(PageRequest request) {
        Page<AfterSale> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AfterSale::getCreateTime);
        Page<AfterSale> result = page(page, wrapper);
        List<AfterSaleVO> records = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, AfterSaleAuditRequest request, Long adminId) {
        AfterSale as = getById(id);
        if (as == null) {
            throw new BusinessException("售后单不存在");
        }
        if (!AfterSaleStatus.PENDING.getCode().equals(as.getStatus())) {
            throw new BusinessException("当前状态不可审核");
        }
        as.setAuditBy(adminId);
        as.setAuditTime(LocalDateTime.now());
        as.setAuditRemark(request.getAuditRemark());
        if (Boolean.TRUE.equals(request.getApproved())) {
            as.setActualAmount(request.getActualAmount() != null ? request.getActualAmount() : as.getRefundAmount());
            boolean needReturn = AfterSaleType.RETURN_REFUND.getCode().equals(as.getType())
                    || AfterSaleType.EXCHANGE.getCode().equals(as.getType());
            as.setStatus(needReturn
                    ? AfterSaleStatus.WAIT_BUYER_SEND.getCode()
                    : AfterSaleStatus.REFUNDING.getCode());
            updateById(as);
            // 仅退款：审核通过后直接发起退款；退货退款需等待商家收货后触发
            if (!needReturn) {
                refundService.createRefund(id, adminId);
            }
            return;
        } else {
            as.setStatus(AfterSaleStatus.REJECTED.getCode());
            resetOrderAfterSale(as.getOrderId());
        }
        updateById(as);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receive(Long id, Long adminId) {
        AfterSale as = getById(id);
        if (as == null) {
            throw new BusinessException("售后单不存在");
        }
        as.setReceiveTime(LocalDateTime.now());
        as.setStatus(AfterSaleStatus.REFUNDING.getCode());
        updateById(as);
        // 商家确认收货后触发退款
        refundService.createRefund(id, adminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(Long id, String reason, Long adminId) {
        AfterSale as = getById(id);
        if (as == null) {
            throw new BusinessException("售后单不存在");
        }
        as.setStatus(AfterSaleStatus.CLOSED.getCode());
        as.setCloseReason(reason);
        updateById(as);
        resetOrderAfterSale(as.getOrderId());
    }

    private AfterSale requireOwned(Long id, Long userId) {
        AfterSale as = getById(id);
        if (as == null) {
            throw new BusinessException("售后单不存在");
        }
        if (!as.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该售后单");
        }
        return as;
    }

    private void resetOrderAfterSale(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && !Integer.valueOf(2).equals(order.getAfterSaleStatus())) {
            order.setAfterSaleStatus(0);
            orderMapper.updateById(order);
        }
    }

    private AfterSaleVO toVO(AfterSale as) {
        AfterSaleVO vo = new AfterSaleVO();
        vo.setId(as.getId());
        vo.setAfterSaleNo(as.getAfterSaleNo());
        vo.setOrderId(as.getOrderId());
        vo.setOrderNo(as.getOrderNo());
        vo.setItemName(as.getItemName());
        vo.setItemImage(as.getItemImage());
        vo.setQuantity(as.getQuantity());
        vo.setType(as.getType());
        vo.setTypeDesc(AfterSaleType.descriptionOf(as.getType()));
        vo.setReasonType(as.getReasonType());
        vo.setReason(as.getReason());
        vo.setEvidenceUrls(as.getEvidenceUrls());
        vo.setRefundAmount(as.getRefundAmount());
        vo.setActualAmount(as.getActualAmount());
        vo.setStatus(as.getStatus());
        vo.setStatusDesc(AfterSaleStatus.descriptionOf(as.getStatus()));
        vo.setAuditRemark(as.getAuditRemark());
        vo.setReturnCarrier(as.getReturnCarrier());
        vo.setReturnTracking(as.getReturnTracking());
        vo.setRefundNo(as.getRefundNo());
        vo.setRefundTime(as.getRefundTime());
        vo.setCreateTime(as.getCreateTime());
        return vo;
    }

    private String joinUrls(List<String> urls) {
        // TODO 改为 JSON 序列化存储（与 evidence_urls 字段语义一致）
        return (urls == null || urls.isEmpty()) ? null : String.join(",", urls);
    }

    private String generateAfterSaleNo() {
        // TODO 建议改为 "AS" + yyyyMMdd + 4位序列
        return "AS" + System.currentTimeMillis();
    }
}
